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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

	private static final long serialVersionUID = -6510887307056841002L;

	private static Game game = null;

	private boolean debugFeatures = true;

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

	//font Metrics for our menu screen
	private Font font;
	private FontMetrics metrics;
	private Font font2;
	private FontMetrics metrics2;;

	private Rectangle com, twoPlayer;
	private boolean underlineCom, underlineTwoPlayer;

	private boolean isPlayerOneTurn = true;
	private boolean isPlayerTwoTurn;

	private int mouseSelectedX;
	private int mouseSelectedY;

	/*
	 * Directional movement
	 */

	private static int moveDirection = -1;


	private static final byte NORTH_WEST = 0;
	private static final byte NORTH_EAST = 1;
	private static final byte SOUTH_WEST = 2;
	private static final byte SOUTH_EAST = 3;


	public Game() {
		setFocusable(true);
		requestFocusInWindow();

		addMouseListener(this); 
		addMouseMotionListener(this);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(debugFeatures) {
						menuShowing = false;
						repaint();
					}
				}
			}

		});

		setLayout(new FlowLayout());

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if(gameObjectsCreated) {
					setupTilePlacementVariables();

					for(int i = 0; i < 32; i++) {
						updateTileSize(i);
						updateTileLocation(i);
						updateCheckerLocation(i < 12 ? i : i - 8);
						updateCheckerSize(i < 12 ? i : i - 8);
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

		if(menuShowing == false) {
			if(!gameObjectsCreated) {
				offset = getWidth() / 14.3;
				padding = getWidth() / 14.3;
				createGameObjects(g2);
			}
			drawObjects(g2);
		}

		addDebugFeatures(g2);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseSelectedX = (int) (e.getX() - (getCheckerWidth() / 2));
		mouseSelectedY = (int) (e.getY() - (getCheckerHeight() / 2));
		repaint();
	}

	public void mousePressed(MouseEvent e) { 
		if(menuShowing) {
			return;
		}

		Checker currentChecker = getCheckerForMouseLoc(e.getPoint());

		if(currentChecker != null) {
			currentChecker.setSelected(true);
			mouseSelectedX = (int) (e.getX() - (getCheckerWidth() / 2));
			mouseSelectedY = (int) (e.getY() - (getCheckerHeight() / 2));
			repaint();
		}
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
		} else {
			Checker c = Checker.getSelectedChecker();
			Tile potentialTile = getTileForPoint(e.getPoint());

			if(c != null && potentialTile != null) {
				boolean isMove = (getDirection(c, potentialTile, 1) > -1 ? true : false);
				boolean isJump = (getDirection(c, potentialTile, 2) > -1 ? true : false);

				if(isMove) {
					moveDirection = getDirection(c, potentialTile, 1);

					if(canMoveToTile(c, potentialTile)) {
						moveChecker(c, potentialTile);
						changeTurns();
					}
				} else if (isJump) {
					moveDirection = getDirection(c, potentialTile, 2);
					
					if(canJumpToTile(c, potentialTile)) {
						removeChecker(Checker.getCheckers()[getMiddleTile(c, moveDirection).getCurrentCheckerNumber()]);
						moveChecker(c, potentialTile);
						
						//prevent a checker from stopping a jump if it has more than one
						for(int i = 0; i < 5; i++) {
							if(hasJump(c, i, getTheoreticalPotentialTile(c, i))) {
								println("Checker #"+ c.getCheckerNumber() +" needs to finish its jumps");
								return;
							}
						}

						changeTurns();
					} 
				} else {
					println("Not a jump or a move");
				}
			}

			if(c != null) {
				c.setSelected(false);
			}
			repaint();
		}
	}


	public void mouseMoved(MouseEvent e) {
		if(menuShowing) {
			if(isWithinBounds(getMousePosition(), twoPlayer)) {
				underlineTwoPlayer = true;
			} else if(isWithinBounds(getMousePosition(), com)) {
				underlineTwoPlayer = false;
				underlineCom = true;
			} else {
				underlineCom = false;
				underlineTwoPlayer = false;
			}
		}

		setMouseX(e.getX());
		setMouseY(e.getY());
		repaint();
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
			radius = getCheckerWidth() / 2;
			radiusSquared = radius * radius;

			if(distanceSquared <= radiusSquared) {
				return c;
			}
		}
		return null;
	}


	public void showMenu(Graphics2D g2) {
		int checkersX = this.getWidth() / 2 - (metrics2.stringWidth("Checkers 2D") / 2);

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
		if(rect == null) return false;
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
		for(Checker c : Checker.getCheckers()) {
			if(c == null) continue;

			g2.drawImage(Checker.getCheckerSprite(c), (int) (c.isSelected() ? mouseSelectedX : c.x),(int) (c.isSelected() ? mouseSelectedY : c.y), (int) getCheckerWidth(), (int) getCheckerHeight(), this);
		}
	}

	public void drawTiles(Graphics2D g2) {
		g2.setColor(Color.white);
		for(int i = 0; i < 32; i++) {
			g2.draw(Tile.getCheckerTiles()[i]);
		}	
	}

	/**
	 * 
	 * @Graphics2D need a Graphics object casted to a Graphics2D object
	 * @canvasWidthX the width of the canvas
	 * @canvasHeightY the height of the canvas
	 * 
	 */
	public void createGameObjects(Graphics2D g2) {
		setupTilePlacementVariables();

		for(int i = 0; i < 32; i++) {
			createTiles(i);

			if(i < 12 || i > 19) createCheckers(i, g2);
		}

		gameObjectsCreated = true;
	}

	/*
	 * Tile Creation
	 */

	double offset;
	double padding;
	int offsetCount = 0;
	double accumlativeColOffset = 0;
	double accumlativeRowOffset = 0;
	public void createTiles(int i) {
		//start a new row, alternating the offset
		if(offsetCount == 4) {
			nextRow();
		}

		Tile tile = new Tile((offsetCount == 0 ? offset : accumlativeRowOffset), accumlativeColOffset, i);
		accumlativeRowOffset += (offsetCount == 0 ? offset : 0) + getTileWidth() + padding; //only add offset on first tile in row
		Tile.getCheckerTiles()[i] = tile;
		
		if(i < 12) {
			tile.setCurrentCheckerNumber(i);
		} else if(i >= 12 && i <= 19) {
			tile.setCurrentCheckerNumber(-1);
		} else {
			tile.setCurrentCheckerNumber(i- 8);
		}
		offsetCount++;
	}


	/*
	 * During tile placement, go to the next row
	 */
	public void nextRow() {
		offset = (offset == 0? getTileWidth() : 0);
		offsetCount = 0;
		accumlativeColOffset += getTileHeight() - 1;
		accumlativeRowOffset = 0;
	}

	/*
	 * reset our accumulating variables
	 */
	public void setupTilePlacementVariables() {
		offset = getTileWidth();
		padding = getTileWidth();
		offsetCount = 0;
		accumlativeColOffset = 0;
		accumlativeRowOffset = 0;
	}

	/*
	 * Update size and location of objects after resizing event
	 */

	public void updateTileLocation(int i) {		
		if(offsetCount == 4) {
			nextRow();
		}
		Tile.getCheckerTiles()[i].x = (offsetCount == 0 ? offset : accumlativeRowOffset);
		Tile.getCheckerTiles()[i].y = accumlativeColOffset;

		accumlativeRowOffset += (offsetCount == 0 ? offset : 0) + (padding * 2); //only add offset on first tile in row

		offsetCount++;
	}

	public void updateTileSize(int i) {
		Tile.getCheckerTiles()[i].height = getTileHeight();
		Tile.getCheckerTiles()[i].width = getTileWidth();
	}

	public void updateCheckerLocation(int i) {
		if(Checker.getCheckers()[i] == null) return;

		Checker.getCheckers()[i].setX(Tile.getCheckerTiles()[Checker.getCheckers()[i].getCurrentTileNumber()].x + 10);
		Checker.getCheckers()[i].setY(Tile.getCheckerTiles()[Checker.getCheckers()[i].getCurrentTileNumber()].y + 10);
	}

	public void updateCheckerSize(int i) {
		if (Checker.getCheckers()[i] == null) return;

		Checker.getCheckers()[i].height = getCheckerHeight();
		Checker.getCheckers()[i].width = getCheckerWidth();
	}

	/*
	 * Create our checker objects so that they can be drawn to the Canvas
	 */
	public void createCheckers(int i, Graphics2D g2) {
		Checker checker = new Checker(Tile.getCheckerTiles()[i].getX() + (getTileWidth() / 8), Tile.getCheckerTiles()[i].getY() + (getTileHeight() / 8), (i > 12), (i < 12 ? i : i - 8), i);

		Checker.getCheckers()[i < 12 ? i : i - 8] = checker;
	}

	/*
	 * Debug Information
	 */

	public long getTimeTaken(long startTime, long endTime) {
		return startTime - endTime;
	}

	public  void addDebugFeatures(Graphics2D g2) {
		if(debugFeatures) {
			addMouseLocationInfo(g2);

			if(gameObjectsCreated) {
				g2.setColor(Color.white);
				addDebugToCheckers(g2);
				addDebugInfoToTiles(g2);
			}
		}
	}

	public void addMouseLocationInfo(Graphics2D g2) {
		if(menuShowing) {
			g2.setColor(Color.BLACK);
		}

		g2.setFont(new Font("Arial", Font.PLAIN, 12));
		g2.drawString("Mouse X = " + getMouseX(), getWidth() - 100, 30);
		g2.drawString("Mouse Y = " + getMouseY(), getWidth() - 100,55);
	}

	public  void addDebugToCheckers(Graphics2D g2) {
		for (Checker c : Checker.getCheckers()) {
			if(c == null) continue;
			g2.setColor(Color.black);
			g2.fillRect((int) c.getCenterX() - 7, (int) c.getCenterY() - 15, 45, 42);

			g2.setColor(Color.white);
			g2.drawString("#"+ c.getCheckerNumber(), (int) c.getCenterX(), (int) c.getCenterY() - 5);
			g2.drawString(""+ c.isSelected(), (int) c.getCenterX() - 5, (int) c.getCenterY() + 5 );
			g2.drawString("x="+ (int) c.x, (int) c.getCenterX(), (int) c.getCenterY() + 15);
			g2.drawString("y="+ (int) c.y, (int) c.getCenterX(), (int) c.getCenterY() + 25);
		}
	}

	private void addDebugInfoToTiles(Graphics2D g2) {
		for(int i = 0; i < 32; i++) {
			g2.drawString("" + Tile.getCheckerTiles()[i].getTileNumber(), (int) Tile.getCheckerTiles()[i].getX() + 5, (int) Tile.getCheckerTiles()[i].getY() + 5);
			g2.drawString("C#" + Tile.getCheckerTiles()[i].getCurrentCheckerNumber(), (int) Tile.getCheckerTiles()[i].getX() + 5, (int) Tile.getCheckerTiles()[i].getY() + 15);
		}
	}

	public  void println(String s) {
		if(debugFeatures) {
			System.out.println(s);
		}
	}

	private boolean isPlayerTurn(Checker c) {
		Player[] players = Player.getPlayers();

		if(isPlayerOneTurn && c.isPlayerOnePiece()) {
			return true;
		} else if(isPlayerTwoTurn && c.isPlayerTwoPiece()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Move detection
	 */

	private boolean clearForLanding(Tile potentialTile) {
		if (potentialTile.hasChecker()) {
			println("Can't move to a space where a checker is");
			return false;
		} else {
			return true;
		}
	}

	private boolean hasJump(Checker c, int dir, Tile potentialTile) {
		Tile middleTile = getMiddleTile(c, dir);

		if(potentialTile != null && middleTile != null) {
			if((middleTile.hasChecker() && isOtherPlayerChecker(c, Checker.getCheckers()[middleTile.getCurrentCheckerNumber()]) && !potentialTile.hasChecker())) {
				return true;
			}
		}

		return false;
	}

	private boolean isOtherPlayerChecker(Checker c, Checker c2) {
		if(c.isPlayerOnePiece() && c2.isPlayerTwoPiece()) {
			return true;
		} else if(c.isPlayerTwoPiece() && c2.isPlayerOnePiece()) {
			return true;
		}

		return false;
	}

	private Tile getMiddleTile(Checker c, int dir) {
		switch(dir) {
		case NORTH_EAST:
			return getTileForPoint(new Point((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getX() + getTileWidth() + 10), 
					(int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getY() - getTileHeight() + 10)));
		case NORTH_WEST: 
			return getTileForPoint(new Point((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getX() - getTileWidth() + 10), 
					(int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getY() - getTileHeight() + 10)));
		case SOUTH_WEST: 
			return getTileForPoint(new Point((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getX() - getTileWidth() + 10), 
					(int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getY() + getTileHeight() + 10)));
		case SOUTH_EAST: 
			return getTileForPoint(new Point((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getX() + getTileWidth() + 10), 
					(int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getY() + getTileHeight() + 10)));
		}

		return null;
	}

	/*
	 * Detect if checker c can move to the given tile
	 * @param c The checker to move
	 * @param tileNumber Which tile number to move to
	 */
	private boolean canMoveToTile(Checker c, Tile potentialTile) {  
		if(!isPlayerTurn(c) || !clearForLanding(potentialTile) || !validMoveDirection(c)) return false;

		for(Checker c2: Checker.getCheckers()) {
			if(c2 == null) continue;

			if((isPlayerTurn(c2))) {
				for(int i = 0; i < 5; i ++) {
					if(isPlayerOneTurn && i > 1)  continue;
					if(isPlayerTwoTurn && i < 2) continue;
					if(hasJump(c2, i, getTheoreticalPotentialTile(c2, i))) {
						println("Checker " +c2.getCheckerNumber() + " has a jump in direction " + i);
						return false;
					}
				}
			}
		}	
		return true;
	}

	private boolean validMoveDirection(Checker c) {
		if(c.isPlayerOnePiece()) {
			if(moveDirection == 0 || moveDirection == 1) {
				return true;
			}
		} else {
			if(moveDirection == 2 || moveDirection == 3) {
				return true;
			}
		}
		
		return false;
	}
	private boolean canJumpToTile(Checker c, Tile potentialTile) {
		//		if(currentJumpingPiece != null && (currentJumpingPiece != c && hasJumps(currentJumpingPiece))) {
		//			println("The last jumping piece still has another jump.");
		//			return false;
		//		}	

		if(isPlayerTurn(c) && hasJump(c, moveDirection, potentialTile) && clearForLanding(potentialTile)) {
			println("Attempting to take jump");
			//			currentJumpingPiece = c;//now the program knows the last jumping piece
			return true;
		} else {
			return false;
		}
	}

	private void changeTurns() {
		isPlayerOneTurn = (isPlayerOneTurn ? false : true);
		isPlayerTwoTurn = !isPlayerOneTurn;
	}

	private void moveChecker(Checker c, Tile potentialTile) {
		c.setX(potentialTile.getX() + (getTileWidth() / 8));
		c.setY(potentialTile.getY() + (getTileHeight() / 8));

		//clear out the checker number for the tile the checker is moving from
		Tile.getCheckerTiles()[c.getCurrentTileNumber()].setCurrentCheckerNumber((byte) -1);

		//set the checker number of the tile that we are moving to to the current checker number
		potentialTile.setCurrentCheckerNumber(c.getCheckerNumber());

		c.setCurrentTileNumber(potentialTile.getTileNumber());
		println("Attempting to move to tile " + potentialTile.toString());
	}

	private void removeChecker(Checker c) {
		Tile.getCheckerTiles()[c.getCurrentTileNumber()].setCurrentCheckerNumber(-1);
		Checker.getCheckers()[c.getCheckerNumber()] = null;
	}

	/*
	 * Determine a direction of the potential tile from the current tile
	 * return -1 if the piece isn't xTilesOver or invalid 
	 */
	private int getDirection(Checker c, Tile potentialTile, int xTilesOver) {
		//		Tile [tileNumber=22, x=309.42334739803096, y=421.4150943396226, width=77.35583684950774, height=85.28301886792453]
		//		Tile [tileNumber=18, x=386.7791842475387, y=337.1320754716981, width=77.35583684950774, height=85.28301886792453]

		if((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getY() - potentialTile.getY()) == (int) ((getTileHeight() - 1) * xTilesOver)) {
			//north
			if((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getX() - potentialTile.getX()) == (int) ((getTileWidth()) * xTilesOver)) {
				return NORTH_WEST;
			}
			if((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getX() - potentialTile.getX()) == (int) ((getTileWidth()) * -xTilesOver)) {
				return NORTH_EAST;
			}
		} else if(((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getY() - potentialTile.getY()) == (int)  -((getTileHeight() - 1) * xTilesOver))) {
			//south
			if((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getX() - potentialTile.getX()) == (int) ((getTileWidth()) * xTilesOver)) {
				return SOUTH_WEST;
			}
			if((int) (Tile.getCheckerTiles()[c.getCurrentTileNumber()].getX() - potentialTile.getX()) == (int) -((getTileWidth()) * xTilesOver)) {
				return SOUTH_EAST;
			}
		} 

		return -1;
	}
	/*
	 * When calculating if the player's current checkers have jumps, this gets a "theoretical" potential tile as a point of reference for the hasJump method
	 */
	private Tile getTheoreticalPotentialTile(Checker c, int dir) {
		switch(dir) {
		case NORTH_WEST:
			//			println(""+ getTileForPoint(new Point((int) (c.getX() - (getTileWidth() * 2) + 5), (int) (c.getY() - (getTileHeight() * 2) + 5))).toString());
			return getTileForPoint(new Point((int) (c.getX() - (getTileWidth() * 2) + 10), (int) (c.getY() - (getTileHeight() * 2) + 10)));
		case NORTH_EAST:
			return getTileForPoint(new Point((int) (c.getX() + (getTileWidth() * 2) + 10), (int) (c.getY() - (getTileHeight() * 2) + 10)));
		case SOUTH_WEST:
			return getTileForPoint(new Point((int) (c.getX() - (getTileWidth() * 2) + 10), (int) (c.getY() + (getTileHeight() * 2) + 10)));
		case SOUTH_EAST:
			return getTileForPoint(new Point((int) (c.getX() + (getTileWidth() * 2) + 10), (int) (c.getY() - (getTileHeight() * 2) + 10)));
		}

		return null;
	}
	private Tile getTileForPoint(Point p) {
		for(Tile tile : Tile.getCheckerTiles()) {
			if(tile.contains(p)) {
				return tile;
			}
		}
		return null;
	}

	public static Game getGame() {
		return game;
	}

	/*
	 * Game piece sizing
	 */

	public static double getCheckerHeight() {
		return getTileHeight() - 20;
	}

	public static double getCheckerWidth() {
		return getTileWidth() - 20;
	}

	public static double getTileHeight() {
		return Game.getGame().getHeight() / 7.95;
	}

	public static double getTileWidth() {
		return Game.getGame().getWidth() / 14.22;
	}
}