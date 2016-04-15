package com.adrian;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Game extends JPanel implements MouseMotionListener, MouseListener {
	private static Game game = null;
	private int startingCheckerX = 50; //corner loc x of checker canvas
	private int startingCheckerY = 50; //corner loc y of checker canvas

	private final int COM_MODE = 0;
	private final int TWO_PLAYER_MODE = 1;

	private boolean debugFeatures = false;
	//spaces for checkers
	private int mouseX = 0;
	private int mouseY = 0;

	//check rendering of game shapes
	boolean gameObjectsCreated = false;

	//store last checker number to add to a fresh object
	int lastCheckerNumber = 0;

	//menu selection
	boolean menuShowing = true;
	boolean comMode = false;
	boolean twoPlayerMode = true;

	byte potentialTileNumber = -1;

	//font Metrics for our menu screen
	private Font font;
	private FontMetrics metrics;
	private Font font2;
	private FontMetrics metrics2;;

	private Rectangle com, twoPlayer;

	private boolean underlineCom, underlineTwoPlayer;

	//images

	public Game() {
		addMouseListener(this); 
		addMouseMotionListener(this);
		this.setLayout(new FlowLayout());
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				for(int i = 0; i < 32; i++) {
					if(gameObjectsCreated) {
						if(i == 0) setupForTileCreation();
						createTiles(i);
					}
				}
			}
		});
		game = this;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		font = new Font("Serif", Font.PLAIN, this.getWidth() / 10);
		metrics = getGraphics().getFontMetrics(font);
		font2 = new Font("Monospaced", Font.BOLD, this.getWidth() / 8);
		metrics2 = getGraphics().getFontMetrics(font2);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(menuShowing) {
			showMenu(g2);
			if(underlineCom) {
				g2.drawLine(com.x, com.y + com.height, com.x + com.width, com.y + com.height);
			}

			if(underlineTwoPlayer) {
				g2.drawLine(twoPlayer.x, twoPlayer.y + twoPlayer.height, twoPlayer.x + twoPlayer.width, twoPlayer.y + twoPlayer.height);
			}
		}

		if(debugFeatures) {
			g2.setFont(new Font("Arial", Font.PLAIN, 12));
			g2.drawString("Mouse X = " + getMouseX(), 20, 30);
			g2.drawString("Mouse Y = " + getMouseY(), 20, 45);
		}

		if(menuShowing == false) {
			if(!gameObjectsCreated) {
				offset = getWidth() / 14.3;
				padding = getWidth() / 14.3;
				createGameObjects(g2);
			}
			drawObjects(g2);
			addDebugFeatures(g2);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) { 
		if(menuShowing) {
			return;
		}
		Checker currentChecker = getCheckerForMouseLoc(e.getPoint());

		if(currentChecker != null) {
			currentChecker.setSelected(true);
			Checker.setOldX(currentChecker.getX());
			Checker.setOldY(currentChecker.getY());
			currentChecker.setX((int) (e.getX() - (Checker.getCheckerWidth() / 2)));
			currentChecker.setY((int) (e.getY() - (Checker.getCheckerHeight() / 2)));
			repaint();
		}
		println("Mouse Pressed");
	}

	public void mouseEntered(MouseEvent e) {

	}
	public void mouseReleased(MouseEvent e) {
		if(debugFeatures) {
			println("-------------------------------");
		}
		if(menuShowing == true) {
			if(isWithinBounds(e.getPoint(), com)) {
				comMode = true;
				menuShowing = false;
				println("Set menushowing to false");
			} else if(isWithinBounds(e.getPoint(), twoPlayer)) {
				twoPlayerMode = true;
				menuShowing = false;
				println("Set menushowing to false for two player mode");
			}
			repaint();
			return;
		}
		Checker c = Checker.getSelectedChecker();
		potentialTileNumber = getTileNumberForPoint(e.getPoint());

		if(c != null && potentialTileNumber != -1) {
			if(canMoveToTile(c)) {
				moveChecker(potentialTileNumber, c);
				changeTurns();
			} 
			else if(canJumpToTile(c)) {
				removeJumpedChecker(c);
				moveChecker(potentialTileNumber, c);
				changeTurns();
			} else {
				revertPiece(c);
			}
		}
		if(c != null) {
			c.setSelected(false);
		}
		repaint();
	}

	public void mouseDragged(MouseEvent e) {
		Checker c = Checker.getSelectedChecker();
		if(c != null) {
			setCheckerLocToMouse(c, e.getPoint());
			repaint();
		} 
	}

	public void mouseMoved(MouseEvent e) {
		if(isWithinBounds(getMousePosition(), twoPlayer) && menuShowing) {
			underlineTwoPlayer = true;
		} else if(isWithinBounds(getMousePosition(), com)) {
			underlineTwoPlayer = false;
			underlineCom = true;
		} else {
			underlineCom = false;
			underlineTwoPlayer = false;
		}
		setMouseX(e.getX());
		setMouseY(e.getY());
		repaint();
	}

	/*
	 * Set checker piece back to old tile
	 */
	private void revertPiece(Checker c) {
		c.setX((int) Checker.getOldX());
		c.setY((int) Checker.getOldY());
		c.setCheckerNumber(c.getCheckerNumber());
		potentialTileNumber = -1;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	public Checker getCheckerForMouseLoc(Point p) {
		double dx, dy, radius, radiusSquared, distanceSquared;
		for(Checker c : Checker.getCheckers()) {
			if(c == null) continue;
			dx = c.getCenterX() - p.getX();
			dy = c.getCenterY() - p.getY();
			dx *= dx;//square the distance
			dy *= dy;//square the distance
			distanceSquared = dx + dy;
			radius = dx / 2;
			radiusSquared = radius * radius;

			if(distanceSquared <= radiusSquared) {
				return c;
			}
		}
		return null;
	}

	public void setCheckerLocToMouse(Checker c, Point mouseCoordinates) {
		c.setX((int) (mouseCoordinates.getX() - (Checker.getCheckerWidth() / 2)));
		c.setY((int) (mouseCoordinates.getY() - (Checker.getCheckerHeight() / 2)));
	}

	public void showMenu(Graphics2D g2) {
		int checkersX = this.getWidth() / 2 - (metrics2.stringWidth("Checkers 2D") / 2);
		int checkersY = this.getHeight() / 2;

		int padding = 15;
		Rectangle2D.Double wrapper = new Rectangle2D.Double(padding, padding, this.getWidth() - (padding * 2), this.getHeight() - (padding * 2));
		g2.draw(wrapper);
		g2.setFont(font2);
		g2.setColor(Color.RED);
		g2.drawString("Checkers 2D", checkersX, this.getHeight() / 2 - 150);
		g2.setFont(font);
		g2.setColor(Color.black);

		twoPlayer = getStringBounds("1p VS 2P", g2, menuButtonLoc("1p VS 2p", 0));
		com = getStringBounds("1p VS COM", g2, menuButtonLoc("1p VS COM", (int) (85 + twoPlayer.getHeight())));

		g2.drawString("1p VS 2p", menuButtonLoc("1p VS 2p", 0).x, menuButtonLoc("1p VS 2p", 0).y);
		g2.drawString("1p VS COM", menuButtonLoc("1p VS COM", 85).x, (int) (menuButtonLoc("1p vs COM", 85).y + twoPlayer.getHeight()));

		//println("Menu Button size COM" + menuButtonSize("1p VS COM"));
		//println("1P VS COM , x=" + menuButtonLoc("1p VS COM", 85).x + ", y = " + menuButtonLoc("1p VS COM", 85).y);

	}

	public Rectangle getStringBounds(String str, Graphics2D g2d, Point p) {
		GlyphVector gv = g2d.getFont().createGlyphVector(metrics.getFontRenderContext(), str);
		return gv.getPixelBounds(null, p.x, p.y);
	}

	public boolean isWithinBounds(Point p, Rectangle rect) {
		if(p.x >= rect.x && p.y >= rect.y && p.x <= rect.x + rect.getWidth() && p.y <= rect.y + rect.getHeight()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * @param buttonText Contents of Menu Button Text
	 * @param increment How farm from the center the button lies
	 */
	private Point menuButtonLoc(String buttonText, int increment) {

		return new Point((this.getWidth() / 2) - (metrics.stringWidth(buttonText) / 2), (this.getHeight() / 2) + increment);
	}
	/*
	 * @param buttonText Contents of Menu Button Text
	 * returns size of square emcompassing Menu Button
	 */
	private Dimension menuButtonSize(String buttonText) {
		return new Dimension(metrics.stringWidth(buttonText), metrics.getHeight());
	}


	/**
	 * 
	 * @Graphics2D need a Graphics object casted to a Graphics2D object
	 * @canvasWidthX the width of the canvas
	 * @canvasHeightY the height of the canvas
	 * 
	 */
	public void createGameObjects(Graphics2D g2) {
		setupForTileCreation();
		for(int i = 0; i < 32; i++) {
			createTiles(i);
			if(i < 12 || i > 19) {
				createCheckers(i, g2);
			}
		}

	}
	
	/*
	 * reset our accumulating variables
	 */
	public void setupForTileCreation() {
		offset = getWidth() / 14.3;
		padding = getWidth() / 14.3;
		offsetCount = 0;
		accumlativeColOffset = 0;
		accumlativeRowOffset = 0;
		gameObjectsCreated = true;
	}
	
	public void drawObjects(Graphics2D g2) {
		drawBoard(g2);
		if(debugFeatures) {
			drawTiles(g2);
		}
		drawCheckers(g2);
	}
	
	public void drawBoard(Graphics2D g2) {
		g2.drawImage(Sprite.getSprite(Sprite.BOARD), 0, 0, this.getWidth(), this.getHeight(), this);
	}

	//redraw the checkers without 
	public void drawCheckers(Graphics2D g2) {
		int checkerWidth = getWidth() / 14;
		int checkerHeight = getHeight() / 12;
		for(int i = 0; i < 24; i++) {
			g2.drawImage(Checker.getCheckers()[i].isPlayerOnePiece() ? Sprite.getSprite(Sprite.CHECKER).getSubimage(0, 0, 61, 61) :
																	   Sprite.getSprite(Sprite.CHECKER).getSubimage(62, 0, 61, 61)
				,(int) Tile.getCheckerTiles()[i < 12 ? i : i + 8].x, (int) Tile.getCheckerTiles()[i < 12 ? i : i + 8].y + 10, checkerWidth , checkerHeight, this);
		}
	}

	public void drawTiles(Graphics2D g2) {
		g2.setColor(Color.white);
		for(int i = 0; i < 32; i++) {
			g2.draw(Tile.getCheckerTiles()[i]);
			System.out.println(Tile.getCheckerTiles()[i].toString());
		}	
	}

	double offset;
	double padding;
	int offsetCount = 0;
	double accumlativeColOffset = 0;
	double accumlativeRowOffset = 0;
	public void createTiles(int i) {
		//start a new row, alternating the offset
		if(offsetCount == 4) {
			offset = (offset == 0? Tile.getTileWidth() : 0);
			offsetCount = 0;
			accumlativeColOffset += Tile.getTileHeight() - 1;
			accumlativeRowOffset = 0;
		}
		
		Tile tile = new Tile((offsetCount == 0 ? offset : accumlativeRowOffset), accumlativeColOffset);
		accumlativeRowOffset += (offsetCount == 0 ? offset : 0) + Tile.getTileWidth() + padding + .65; //only add offset on first tile in row
		Tile.getCheckerTiles()[i] = tile;
		offsetCount++;
	}

	public void createCheckers(int i, Graphics2D g2) {
		Checker checker = new Checker(Tile.getCheckerTiles()[i].getX(), Tile.getCheckerTiles()[i].getY(), (i < 12 ? true : false));

		checker.setCurrentTile(i);
		Checker.getCheckers()[i < 12 ? i : i - 8] = checker;

	}

	private void addDebugInfoToTiles(Graphics2D g2, int i) {
		if(debugFeatures) {
			g2.setPaint(Color.white);
			//if(tileIsPlayable(i)) {
			g2.drawString("" + Tile.getCheckerTiles()[i].getTileNumber(), (int) Tile.getCheckerTiles()[i].getCenterX(), (int) Tile.getCheckerTiles()[i].getCenterY());
		}
		//}
	}

	public long getTimeTaken(long startTime, long endTime) {
		return startTime - endTime;
	}

	public  void addDebugFeatures(Graphics2D g2) {
		if(debugFeatures) {
			addLabelsToCheckers(g2);
		}
	}
	public  void println(String s) {
		if(debugFeatures) {
			System.out.println(s);
		}
	}
	public  void addLabelsToCheckers(Graphics2D g2) {
		g2.setPaint(Color.white);
		for (Checker c : Checker.getCheckers()) {
			if(c == null) continue;
			if(c.isPlayerOnePiece()) {
				g2.setPaint(Color.black);
				g2.drawString("#"+ c.getCheckerNumber(), (int) c.getCenterX(), (int) c.getCenterY() - 5);
				g2.drawString(""+ c.isSelected(), (int) c.getCenterX() - 5, (int) c.getCenterY() + 5 );
			} else {
				g2.drawString("#"+ c.getCheckerNumber(), (int) c.getCenterX(), (int) c.getCenterY() - 5);
				g2.drawString("" + c.isSelected(), (int) c.getCenterX() - 5, (int) c.getCenterY() + 5);
			}
		}
	}

	/*
	 * create our two players, assign them a number
	 */
	private void createPlayers() {
		for(int i = 0; i <= 1; i++) {
			new Player(i);
		}

		setTurn(Player.PLAYER_ONE, true);//its player one's turn
	}

	/*
	 * set which players turn it is
	 * @int player 
	 */

	private void setTurn(int player, boolean turn) {
		Player.getPlayers().get(player).setTurn(turn);
	}

	private boolean turnMatchesChecker(Checker c) {
		ArrayList<Player> players = Player.getPlayers();

		if(players.get(Player.PLAYER_ONE).isPlayerTurn() && Checker.getCheckers()[c.getCheckerNumber()].isPlayerOnePiece()) {
			return true;
		} else if(players.get(Player.PLAYER_TWO).isPlayerTurn() && Checker.getCheckers()[c.getCheckerNumber()].isPlayerTwoPiece()) {
			return true;
		} else {
			println("It's not your turn, move a white piece for player one and a red for player two.");
			return false;
		}
	}



	/*
	 * Move detection
	 */

	/*
	 * Detect if checker c can move to the given tile
	 * @param c The checker to move
	 * @param tileNumber Which tile number to move to
	 */
	private boolean canMoveToTile(Checker c) {    	
		if(currentJumpingPiece != null && (hasJumps(currentJumpingPiece) && currentJumpingPiece != c)) {
			if(currentJumpingPiece.isPlayerOnePiece()) {
				changeTurnPlayerOne();
			} else {
				changeTurnPlayerTwo();
			}
			println("The last jumping piece still has another jump.");
			return false;
		}

		if(hasJumps(c)) {
			println("Take your jumps");
			return false;
		}

		for(Checker c2: Checker.getCheckers()) {
			if(c2 == null || !hasJumps(c2)) continue;
			if((turnMatchesChecker(c2))) {
				println("You must take your jumps");
				return false;
			}
		}

		if(turnMatchesChecker(c) && clearForLanding(potentialTileNumber) && isDiagonalXTilesFromChecker(c, 1)) {
			if(!validMoveDirection(c)) {
				return false;
			}
			return true;
		}
		println("Move failed");
		return false;
	}



	private boolean validMoveDirection(Checker c) {
		if(!c.isCrowned() && ((getDirectionOfMove(c) == SE || getDirectionOfMove(c) == SW)
				|| getDirectionOfJump(c) == SE || getDirectionOfJump(c) == SW)) {
			println("Only crowned pieces can move backwards.");
			return false;
		} else {
			return true;
		}
	}
	private boolean clearForLanding(int tileNumber) {
		if (Tile.getCheckerTiles()[tileNumber].hasChecker()) {
			println("Can't move to a space where a checker is");
			return false;
		} else {
			return true;
		}
	}

	//private int getOtherJumps(Checker c) {
	//	int otherJumps = 0;
	//	for(Checker c2: Checker.getCheckers()) {
	//		if(c2.getCheckerNumber() == c.getCheckerNumber()) continue;
	//		if(hasJumps(c)) {
	//			otherJumps++;
	//		}
	//	}
	//	return otherJumps;
	//}

	private Checker currentJumpingPiece = null;
	private int[] nonJumpableTiles = {1, 3, 5, 7, 8, 24, 23, 40, 56, 39, 55, 58, 60, 62};

	private boolean hasJumps(Checker c) {
		int currentTileNumber = c.getCurrentTileNumber();
		if(c == null) return false;
		if(getMiddleTile(c) == null) return false;

		for(int i = 0; i < nonJumpableTiles.length; i++ ) {
			if(getMiddleTile(c).getTileNumber() == nonJumpableTiles[i]) {
				return false;
			}
		}
		if(!c.isCrowned()) {

			if(c.isPlayerOnePiece()) {
				if((currentTileNumber - 14) > 0) {
					if((clearForLanding(currentTileNumber - 14)) && Tile.getCheckerTiles()[currentTileNumber - 7].hasPlayerTwoChecker()) {
						return true;
					}
				}
				if((currentTileNumber - 18) > 0) {
					if((clearForLanding(currentTileNumber - 18)) && Tile.getCheckerTiles()[currentTileNumber - 9].hasPlayerTwoChecker()) {
						return true;
					}
				}
			} else if(c.isPlayerTwoPiece()) {
				if((currentTileNumber + 14) < 63) {
					if((clearForLanding(currentTileNumber + 14)) && Tile.getCheckerTiles()[currentTileNumber + 7].hasPlayerOneChecker()) {
						return true;
					}
				}
				if((currentTileNumber + 18) < 63) {
					if((clearForLanding(currentTileNumber + 18)) && Tile.getCheckerTiles()[currentTileNumber + 9].hasPlayerOneChecker()) {
						return true;
					}
				}
			}
		} else if(c.isCrowned() && 
				(((clearForLanding(currentTileNumber - 18) && Tile.getCheckerTiles()[currentTileNumber - 9].hasChecker()) ||
						(clearForLanding(currentTileNumber - 14) && Tile.getCheckerTiles()[currentTileNumber - 7].hasChecker()))) ||
				((clearForLanding(currentTileNumber + 18) && Tile.getCheckerTiles()[currentTileNumber + 9].hasChecker()) ||
						(clearForLanding(currentTileNumber + 14) && Tile.getCheckerTiles()[currentTileNumber + 7].hasChecker()))) {
			return true;
		} 

		return false;
	}

	private boolean canJumpToTile(Checker c) {

		if(currentJumpingPiece != null && (currentJumpingPiece != c && hasJumps(currentJumpingPiece))) {
			println("The last jumping piece still has another jump.");
			return false;
		}	

		if(!validMoveDirection(c)) {
			return false;
		}

		if(turnMatchesChecker(c) && isDiagonalXTilesFromChecker(c, 2) && clearForLanding(potentialTileNumber)) {
			println("Attempting to take jump");
			currentJumpingPiece = c;//now the program knows the last jumping piece
			return true;
		} else {
			return false;
		}
	}

	private void changeTurnPlayerOne() {
		setTurn(Player.PLAYER_ONE, true);
		setTurn(Player.PLAYER_TWO, false);
	}

	private void changeTurnPlayerTwo() {
		setTurn(Player.PLAYER_ONE, false);
		setTurn(Player.PLAYER_TWO, true);
	}

	private void changeTurns() {
		if(isPlayerOneTurn()) {
			setTurn(Player.PLAYER_ONE, false);
			setTurn(Player.PLAYER_TWO, true);
		} else {
			setTurn(Player.PLAYER_ONE, true);
			setTurn(Player.PLAYER_TWO, false);
		}
	}

	private void moveChecker(byte potentialTileNumber, Checker c) {
		c.setX(Tile.getCheckerTiles()[potentialTileNumber].getX() + (Tile.getTileWidth() / 8));
		c.setY(Tile.getCheckerTiles()[potentialTileNumber].getY() + (Tile.getTileHeight() / 8));

		//clear out the checker number for the tile the checker is moving from
		Tile.getCheckerTiles()[c.getCurrentTileNumber()].setCurrentCheckerNumber((byte) -1);

		//set the checker number of the tile that we are moving to to the current checker number
		Tile.getCheckerTiles()[potentialTileNumber].setCurrentCheckerNumber(c.getCheckerNumber());

		c.setCurrentTile(potentialTileNumber);
		println("Attempting to move to tile " + potentialTileNumber);
	}

	private void removeJumpedChecker(Checker c) {
		Tile middleTile = getMiddleTile(c);
		int checkerNumber = middleTile.getCurrentCheckerNumber();
		println("Attempting to remove checker #" + checkerNumber +" from tile #" + middleTile.getTileNumber());
		Checker.getCheckers()[checkerNumber] = null;
		middleTile.setCurrentCheckerNumber((byte) -1);
	}
	//jumping checker 9 with 13, 12 behind checker
	private Tile getMiddleTile(Checker c) {
		if(c.isPlayerOnePiece()) {
			switch(getDirectionOfJump(c)) {
			case 0: return Tile.getCheckerTiles()[c.getCurrentTileNumber() - 7];
			case 1: return Tile.getCheckerTiles()[c.getCurrentTileNumber() + 9];
			case 2: return Tile.getCheckerTiles()[c.getCurrentTileNumber() + 7];
			case 3: return Tile.getCheckerTiles()[c.getCurrentTileNumber() - 9];

			default: return null;
			}
		} else {
			switch(getDirectionOfJump(c)) {
			case 0: return Tile.getCheckerTiles()[c.getCurrentTileNumber() + 7];
			case 1: return Tile.getCheckerTiles()[c.getCurrentTileNumber() - 9];
			case 2: return Tile.getCheckerTiles()[c.getCurrentTileNumber() - 7];
			case 3: return Tile.getCheckerTiles()[c.getCurrentTileNumber() + 9];

			default: return null;
			}
		}
	}
	private String getDirectionToString(Checker c, int difference) {
		if(c.isPlayerOnePiece()) {
			switch(difference) {
			case 14: return "South West";
			case 18: return "South East";
			case -18: return "North West";
			case -14: return "North East";

			default: return "Invalid Direction";
			}
		} else {
			switch(difference) {
			case -14: return "South West";
			case -18: return "South East";
			case 18: return "North West";
			case 14: return "North East";

			default: return "Invalid Direction";
			}
		}
	}
	private int getDirectionOfJump(Checker c) {
		byte NE = 0, SE = 1, SW = 2, NW = 3;

		int difference = potentialTileNumber - c.getCurrentTileNumber();
		println("The direction of the jump is "+ getDirectionToString(c, difference));

		if(c.isPlayerOnePiece()) {
			switch(difference) {
			case 14: return SW;
			case 18: return SE;
			case -18: return NW;
			case -14: return NE;

			default: return -1;
			}
		} else {
			switch(difference) {
			case -14: return SW;
			case -18: return SE;
			case 18: return NW;
			case 14: return NE;

			default: return -1;
			}
		}
	}

	final byte NE = 0, SE = 1, SW = 2, NW = 3;

	private int getDirectionOfMove(Checker c) {
		int difference = potentialTileNumber - c.getCurrentTileNumber();
		println("The direction of the move "+ difference +"towards tile #" +potentialTileNumber);

		if(c.isPlayerOnePiece()) {
			switch(difference) {
			case 7: return SW;
			case 9: return SE;
			case -9: return NW;
			case -7: return NE;

			default: return -1;
			}
		} else {
			switch(difference) {
			case -7: return SW;
			case -9: return SE;
			case 9: return NW;
			case 7: return NE;

			default: return -1;
			}
		}
	}



	//private boolean needsToTakeOtherJump(Checker c) {
	//	if(!canTakeJump(c) && getOtherJumps(c) > 0) {
	//		println("You have to take your jumps");
	//		return true;
	//	}
	//	return false;
	//}
	/*
	 * detect if a tile is diagonal @tileCount times
	 */
	private boolean isDiagonalXTilesFromChecker(Checker c, int x) {
		//		if((Math.abs(Tile.getCheckerTiles()[c.getCurrentTileNumber()].x - Tile.getCheckerTiles()[potentialTileNumber].x) == (Tile.getTileSize() * x))
		//				&& (Math.abs(Tile.getCheckerTiles()[c.getCurrentTileNumber()].y - (Tile.getCheckerTiles()[potentialTileNumber].y)) == (Tile.getTileSize() * x))) {
		//			return true;

		if((Math.abs(c.getCurrentTileNumber() - potentialTileNumber) == 7 * x) || Math.abs(c.getCurrentTileNumber() - potentialTileNumber) == 9 * x) {
			println("The potential tile ("+ potentialTileNumber +" is diagonal of the current tile ("+ c.getCurrentTileNumber() +".");
			return true;
		} else {
			println("Not a diagonal jump within the alloted distance. "+ Math.abs(c.getCurrentTileNumber() - potentialTileNumber) +"");
			//			println("Tile is not diagonal. Current checker #"+ c.getCheckerNumber() + ", Potential tile #"+potentialTileNumber+".");
			//			println("Current tile #" +c.getCurrentTileNumber());
			//
			//			println("Expected" + Tile.getTileSize() * x +", actual x: " + Math.abs(Tile.getCheckerTiles()[c.getCurrentTileNumber()].x - Tile.getCheckerTiles()[potentialTileNumber].x));
			//			println("Expected" + Tile.getTileSize() * x +", actual y: " + Math.abs(Tile.getCheckerTiles()[c.getCurrentTileNumber()].y - Tile.getCheckerTiles()[potentialTileNumber].y));
			return false;
		}
	}

	private byte getTileNumberForPoint(Point p) {
		for(byte i = 0; i < Tile.getCheckerTiles().length; i++) {
			if((p.x - Tile.getCheckerTiles()[i].getX()) <= Tile.getTileWidth() 
					&& p.y - Tile.getCheckerTiles()[i].getY() <= Tile.getTileHeight()) {
				return Tile.getCheckerTiles()[i].getTileNumber();
			}
		}
		return -1;
	}
	
	public static Game getGame() {
		return game;
	}

	private boolean isPlayerOneTurn() {
		return Player.getPlayers().get(Player.PLAYER_ONE).isPlayerTurn();
	}

	private boolean isPlayerTwoTurn() {
		return Player.getPlayers().get(Player.PLAYER_ONE).isPlayerTurn();
	}
}