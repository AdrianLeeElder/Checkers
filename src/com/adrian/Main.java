/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adrian;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Main extends JPanel implements MouseListener, MouseMotionListener {
	static int widthX = 650;
	static int widthY = 650;
	static double canvasWidth = (double) widthX - 100;//width of checker canvas
	static double canvasHeight = (double) widthY - 100;//height of checker canvas
	static int startingCheckerX = 50; //corner loc x of checker canvas
	static int startingCheckerY = 50; //corner loc y of checker canvas
	static boolean debugFeatures = true;
	//spaces for checkers
	private int mouseX = 0;
	private int mouseY = 0;

	//check rendering of game shapes
	static boolean checkersCreated = false;
	static boolean tilesCreated = false;

	//store last checker number to add to a fresh object
	static int lastCheckerNumber = 0;
	
	static boolean menuShowing = false;

	//store the tile number we are trying to move to so
	//we don't have to keep running the getTileNumberForPoint method 
	static int potentialTileNumber = -1;
	public static void init() {

		JFrame frame = new JFrame("Testing");
		JPanel contentPane = new Main();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(widthX, widthY));
		frame.getContentPane().add("Center", contentPane);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(((dim.width/2)-frame.getSize().width) / 2, ((dim.height/2) -frame.getSize().height) /2);

		frame.pack();
		frame.setVisible(true);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g; 

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		showMenu(g2);

		g2.drawString("Mouse X = " + this.getMouseX(), 10, 15);
		g2.drawString("Mouse Y = " + this.getMouseY(), 10, 30);
		
		if(!menuShowing) {
			createGame(g2);
			addDebugFeatures(g2);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
	@Override
	public void mousePressed(MouseEvent e) { 
		Checker currentChecker = getCheckerForMouseLoc(e.getPoint());

		if(currentChecker != null) {
			currentChecker.setSelected(true);
			Checker.setOldX(currentChecker.getX());
			Checker.setOldY(currentChecker.getY());
			currentChecker.x = e.getX() - (Checker.getCheckerSize() / 2);
			currentChecker.y = e.getY() - (Checker.getCheckerSize() / 2);
			repaint();
		}
		println("Mouse Pressed");
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {

	}
	@Override
	public void mouseReleased(MouseEvent e) {
		Checker c = Checker.getSelectedChecker();
		potentialTileNumber = getTileNumberForPoint(e.getPoint());
		
		if(c != null && potentialTileNumber != -1) {
			if(canMoveToTile(c)) {
				c.x = Tile.getCheckerTiles()[potentialTileNumber].getX() + (Tile.getTileSize() / 8);
				c.y = Tile.getCheckerTiles()[potentialTileNumber].getY() + (Tile.getTileSize() / 8);
				
				//clear out the checker number for the tile the checker is moving from
				Tile.getCheckerTiles()[c.getCurrentTileNumber()].setCurrentCheckerNumber(-1);
				
				//set the checker number of the tile that we are moving to to the current checker number
				Tile.getCheckerTiles()[potentialTileNumber].setCurrentCheckerNumber(c.getCheckerNumber());
				
				c.setCurrentTile(potentialTileNumber);
				println("Attempting to move to tile " + potentialTileNumber);
				repaint();
				
				
			} else {
				//move the checker piece back to its original location
				c.x = Checker.getOldX();
				c.y = Checker.getOldY();
				c.setCheckerNumber(c.getCheckerNumber());
				potentialTileNumber = -1;
			}
			c.setSelected(false);
		}
		repaint();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		Checker c = Checker.getSelectedChecker();
		if(c != null) {
			setCheckerLocToMouse(c, e.getPoint());
			repaint();
		} 
	}
	@Override
	public void mouseMoved(MouseEvent e) {
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
		for(Checker c : Checker.getCheckersPlayer()) {
			dx = c.getCenterX() - p.getX();
			dy = c.getCenterY() - p.getY();
			dx *= dx;//square the distance
			dy *= dy;//square the distance
			distanceSquared = dx + dy;
			radius = Checker.getCheckerSize() / 2;
			radiusSquared = radius * radius;

			if(distanceSquared <= radiusSquared) {
				return c;
			}
		}
		return null;
	}

	public void setCheckerLocToMouse(Checker c, Point mouseCoordinates) {
		//println("Attempted to change checker #" + c.getCheckerNumber() + " to loc x: " + mouseCoordinates.getX() +", loc y: " + mouseCoordinates.getY());
		//Checker.getCheckersPlayer().set(c.getCheckerNumber(), new Checker(mouseCoordinates.getX(), mouseCoordinates.getY(), Checker.getCheckerSize(), Checker.getCheckerSize()));
		c.x = mouseCoordinates.getX() - (Checker.getCheckerSize() / 2);
		c.y = mouseCoordinates.getY() - (Checker.getCheckerSize() / 2);
	}

	/**
	 * Detect if a space is playable, and paint that tile either red or black
	 * @param i Tile number 
	 * @return
	 */
	public Color getPaintColor(int i) {
		if(isPlayable(i)) {
			return Color.black;
		} else {
			return Color.red;
		}
	}

	public boolean isEven(int i) {
		if(i % 2 == 0) 
			return true;
		else
			return false;
	}
	/* 
	 * Detect if the tile is a playable black Tile (red tiles return false, black return true)
	 * 
	 */
	public boolean isPlayable(int i) {
		if((i < 8)) {
			if(isEven(i)) {
				return false;
			}
			return true;
		} else if (i < 16) {
			if(isEven(i)) {
				return true;
			}
			return false;
		} else if (i < 24) {
			if(isEven(i)) {
				return false;
			}
			return true;
		} else if (i < 32) {
			if(isEven(i)) {
				return true;
			}
			return false;
		} else if (i < 40) {
			if(isEven(i)) {
				return false;
			}
			return true;
		} else if (i < 48) {
			if(isEven(i)) {
				return true;
			}
			return false;
		} else if (i < 56) {
			if(isEven(i)) {
				return false;
			}
			return true;
		} else if (i < 64) {
			if(isEven(i)) {
				return true;
			}
			return false;
		} 
		
		println("Space is not playable");
		return false; 
	}
	
	public void showMenu(Graphics2D g2) {
		Font font = new Font("Serif", Font.PLAIN, this.getWidth() / 10);
		FontMetrics metrics = getGraphics().getFontMetrics(font);
		Font font2 = new Font("Monospaced", Font.BOLD, this.getWidth() / 8);
		FontMetrics metrics2 = getGraphics().getFontMetrics(font2);
		
		int checkersX = this.getWidth() / 2 - (metrics2.stringWidth("Checkers 2D") / 2);
		int checkersY = this.getHeight() / 2;
		
		Rectangle2D.Double wrapper = new Rectangle2D.Double((double) checkersX - 15, 
								(double) ((checkersY - 150) - metrics2.getHeight()), 
											((checkersX + metrics2.stringWidth("Checkers 2D") - 15)), 
											((checkersY + 85) - ((checkersY - 150) - metrics2.getHeight())) + metrics2.getHeight());
		g2.draw(wrapper);
		g2.setFont(font2);
		g2.setColor(Color.RED);
		g2.drawString("Checkers 2D", checkersX, this.getHeight() / 2 - 150);
		g2.setFont(font);
		g2.setColor(Color.black);
		g2.drawString("1p vs 2p", this.getWidth() / 2 - (metrics.stringWidth("1p vs 2p") / 2), this.getHeight() / 2);
		g2.drawString("1p vs Com", this.getWidth() / 2 - (metrics.stringWidth("1p vs Com") / 2), this.getHeight() / 2 + 85);
		g2.setFont(new Font("Arial", Font.PLAIN, 12));
		
		
		menuShowing = true;
	}

	
	/**
	 * 
	 * @Graphics2D need a Graphics object casted to a Graphics2D object
	 * @canvasWidthX the width of the canvas
	 * @canvasHeightY the height of the canvas
	 * 
	 */
	public void createGame(Graphics2D g2) {
		createInitialTile(g2);

		if(!tilesCreated) {
			for(int i=1; i < 64; i++) {
				createTiles(g2, i);
				if(i < 24 || i > 39) {
					if(isPlayable(i)) {
						if(!checkersCreated) {
							createCheckers(i, g2);
						} 
					}
				}
			}
		} else {
			redrawTiles(g2);
			if(checkersCreated) {
				redrawCheckers(g2);
			}
		}
	}

	//redraw the checkers without 
	public void redrawCheckers(Graphics2D g2) {
		long startTime = System.currentTimeMillis();
		long endTime;
		for(Checker c : Checker.getCheckersComputer()) {
			g2.setPaint(Checker.getFillColorComputer());
			g2.fill(c);
			g2.draw(c);
		}

		for(Checker c : Checker.getCheckersPlayer()) {
			g2.setPaint(Checker.getFillColorPlayer());
			g2.fill(c);
			g2.draw(c);
		}
		endTime = System.currentTimeMillis();
		//println("Checkers were successfully redrawn. Took " + getTimeTaken(startTime, endTime) + " ms");
	}

	public void redrawTiles(Graphics2D g2) {
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		for(int i = 1; i < 64; i++) {
			g2.setPaint(getPaintColor(i));
			g2.fill(Tile.getCheckerTiles()[i]);
			g2.draw(Tile.getCheckerTiles()[i]);
			addDebugInfoToTiles(g2, i);
			if(i == 63) {
				endTime = System.currentTimeMillis();
				//println("Tiles successfully redrawn. Took " + getTimeTaken(startTime, endTime) + " ms");
			}
		}
	}

	public void createInitialTile(Graphics2D g2) {
		Tile.getCheckerTiles()[0] = new Tile(startingCheckerX, startingCheckerY, Tile.getTileSize(), Tile.getTileSize());
		g2.draw(Tile.getCheckerTiles()[0]);
		g2.setPaint(Color.red);
		g2.fill(Tile.getCheckerTiles()[0]);
	}
	public void createTiles(Graphics2D g2, int i) {    
		if(i < 8) {
			Tile.getCheckerTiles()[i] = new Tile(Tile.getCheckerTiles()[i-1].getX() + Tile.getTileSize(), startingCheckerY, Tile.getTileSize(), Tile.getTileSize());
		} else if(i < 16) {
			Tile.getCheckerTiles()[i] = new Tile(Tile.getCheckerTiles()[i-8].getX(), startingCheckerY + Tile.getTileSize(), Tile.getTileSize(), Tile.getTileSize());
		}  else if(i < 24) {
			Tile.getCheckerTiles()[i] = new Tile(Tile.getCheckerTiles()[i-8].getX(), Tile.getCheckerTiles()[i-8].getY() + Tile.getTileSize(), Tile.getTileSize(), Tile.getTileSize());
		}  else if(i < 32) {
			Tile.getCheckerTiles()[i] = new Tile(Tile.getCheckerTiles()[i-8].getX(), Tile.getCheckerTiles()[i-8].getY() + Tile.getTileSize(), Tile.getTileSize(), Tile.getTileSize());
		}  else if(i < 40) {
			Tile.getCheckerTiles()[i] = new Tile(Tile.getCheckerTiles()[i-8].getX(), Tile.getCheckerTiles()[i-8].getY() + Tile.getTileSize(), Tile.getTileSize(), Tile.getTileSize());
		}  else if(i < 48) {
			Tile.getCheckerTiles()[i] = new Tile(Tile.getCheckerTiles()[i-8].getX(), Tile.getCheckerTiles()[i-8].getY() + Tile.getTileSize(), Tile.getTileSize(), Tile.getTileSize());
		}  else if(i < 56) {
			Tile.getCheckerTiles()[i] = new Tile(Tile.getCheckerTiles()[i-8].getX(), Tile.getCheckerTiles()[i-8].getY() + Tile.getTileSize(), Tile.getTileSize(), Tile.getTileSize());
		}  else if(i < 64) {
			Tile.getCheckerTiles()[i] = new Tile(Tile.getCheckerTiles()[i-8].getX(), Tile.getCheckerTiles()[i-8].getY() + Tile.getTileSize(), Tile.getTileSize(), Tile.getTileSize());
		}     
		g2.setPaint(getPaintColor(i));
		g2.fill(Tile.getCheckerTiles()[i]);
		g2.draw(Tile.getCheckerTiles()[i]);
		Tile.getCheckerTiles()[i].setTileNumber(i);
		addDebugInfoToTiles(g2, i);
		if(i == 63) {
			println("Tile creation finished.");
			tilesCreated = true;
		}
	}

	private void addDebugInfoToTiles(Graphics2D g2, int i) {
		g2.setPaint(Color.white);
		//if(isPlayable(i)) {
			g2.drawString("" + Tile.getCheckerTiles()[i].getTileNumber(), (int) Tile.getCheckerTiles()[i].getCenterX(), (int) Tile.getCheckerTiles()[i].getCenterY());
		//}
	}

	//count number to keep track of which checker piece to render
	int checkerNumberComp = 0;
	int checkerNumberPlayer = 0;
	public void createCheckers(int i, Graphics2D g2) {
		if(i < 24) {
			g2.setPaint(Checker.getFillColorComputer());
			Checker.getCheckersComputer().add(checkerNumberComp, new Checker((Tile.getCheckerTiles()[i].getX() + (Checker.getCheckerSize() / 8)), (Tile.getCheckerTiles()[i].getY() + (Checker.getCheckerSize() / 8)), Checker.getCheckerSize(), Checker.getCheckerSize()));
			g2.fill(Checker.getCheckersComputer().get(checkerNumberComp));   
			g2.draw(Checker.getCheckersComputer().get(checkerNumberComp));  
			Checker.getCheckersComputer().get(checkerNumberComp).setCheckerNumber(checkerNumberComp);
			Checker.getCheckersComputer().get(checkerNumberComp).setCurrentTile(i);
			Tile.getCheckerTiles()[i].setCurrentCheckerNumber(checkerNumberComp);
			checkerNumberComp++; 
			println("Computer Checker number " + checkerNumberComp + " successfully");
		} else {
			g2.setPaint(Checker.getFillColorPlayer());
			Checker.getCheckersPlayer().add(checkerNumberPlayer, new Checker((Tile.getCheckerTiles()[i].getX() + (Checker.getCheckerSize() / 8)), (Tile.getCheckerTiles()[i].getY() + (Checker.getCheckerSize() / 8)), Checker.getCheckerSize(), Checker.getCheckerSize()));
			g2.fill(Checker.getCheckersPlayer().get(checkerNumberPlayer));   
			g2.draw(Checker.getCheckersPlayer().get(checkerNumberPlayer));   
			Checker.getCheckersPlayer().get(checkerNumberPlayer).setCheckerNumber(checkerNumberPlayer);
			Checker.getCheckersPlayer().get(checkerNumberPlayer).setCurrentTile(i);
			Tile.getCheckerTiles()[i].setCurrentCheckerNumber(checkerNumberPlayer);
			checkerNumberPlayer++;
			println("Player Checker number " + checkerNumberPlayer + " successfully");
		}

		if(debugFeatures) {
			println("Rendered " + Checker.getCheckersComputer().size() + " checker(s) for the computer");
			println("Rendered " + Checker.getCheckersPlayer().size() + " checker(s) for the human");
		}
		if(i == 62) {
			println("Checkers creation finished.");
			checkersCreated = true; //prevent duplicate checker pieces from being generated
		}
	}

	public long getTimeTaken(long startTime, long endTime) {
		return startTime - endTime;
	}

	public static void addDebugFeatures(Graphics2D g2) {
		if(debugFeatures) {
			addLabelsToCheckers(g2);
		}
	}
	public static void println(String s) {
		if(debugFeatures) {
			System.out.println(s);
		}
	}
	public static void addLabelsToCheckers(Graphics2D g2) {
		g2.setPaint(Color.white);
		for (Checker c : Checker.getCheckersComputer()) {
			g2.drawString("#"+ c.getCheckerNumber(), (int) c.getCenterX(), (int) c.getCenterY() - 5);
			g2.drawString(""+ c.isSelected(), (int) c.getCenterX() - 5, (int) c.getCenterY() + 5 );
		}

		g2.setPaint(Color.red);
		for (Checker c : Checker.getCheckersPlayer()) {
			g2.drawString("#"+ c.getCheckerNumber(), (int) c.getCenterX(), (int) c.getCenterY() - 5);
			g2.drawString("" + c.isSelected(), (int) c.getCenterX() - 5, (int) c.getCenterY() + 5);
		}
	}

	public Main() {
		addMouseListener(this); 
		addMouseMotionListener(this);
	}

	public static void main(String[] args) { 
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				init();

			}
		});
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
		if(isPlayable(potentialTileNumber) && !needsToTakeOtherJump(c) && (((isDiagonalXTilesFromChecker(c, 1) && clearForLanding()) 
						|| hasJumps(c)))) {
			println("Successful move");
			return true;
		}
		
			println("Move failed");
		return false;
	}
	
	private boolean clearForLanding() {
		if (Tile.getCheckerTiles()[potentialTileNumber].hasChecker()) {
			println("Can't move to a space where a checker is");
			return false;
		} else {
			return true;
		}
	}

	private int getOtherJumps(Checker c) {
		int otherJumps = 0;
		for(Checker c2: Checker.getCheckersComputer()) {
			if(c2.getCheckerNumber() == c.getCheckerNumber()) continue;
			if(hasJumps(c)) {
				otherJumps++;
			}
		}
		return otherJumps;
	}

	private boolean hasJumps(Checker c) {
		if(isDiagonalXTilesFromChecker(c, 2) && checkerInMiddleTile(c) && clearForLanding()) {
			println("Attempting to take jump");
			return true;
		} else {
			return false;
		}
	}

	private boolean checkerInMiddleTile(Checker c) {
		if(getMiddleTile(c) == -1) 
			return false;
		else
		    return Tile.getCheckerTiles()[getMiddleTile(c)].hasChecker();
	}

	private int getMiddleTile(Checker c) {
		switch(getDirectionOfJump(c)) {
		case 0: return c.getCurrentTileNumber() - 7;
		case 1: return c.getCurrentTileNumber() + 9;
		case 2: return c.getCurrentTileNumber() + 7;
		case 3: return c.getCurrentTileNumber() - 8;
		
		default: return -1;
		}
	}

	private int getDirectionOfJump(Checker c) {
		byte NE = 0, SE = 1, SW = 2, NW = 3;

		int difference = potentialTileNumber - c.getCurrentTileNumber();
		println("The direction of the jump is "+ difference);
		switch(difference) {
		case -14: return SW;
		case -18: return SE;
		case 18: return NW;
		case 14: return NE;
		
		default: return -1;
		}
	}


	private boolean needsToTakeOtherJump(Checker c) {
		if(!hasJumps(c) && getOtherJumps(c) > 0) {
			println("You have to take your jumps");
			return true;
		}
		return false;
	}
	/*
	 * detect if a tile is diagonal @tileCount times
	 */
	private boolean isDiagonalXTilesFromChecker(Checker c, int x) {
		x = 1;
		if((Math.abs(Tile.getCheckerTiles()[c.getCurrentTileNumber()].x - Tile.getCheckerTiles()[potentialTileNumber].x) == (Tile.getTileSize() * x))
		&& (Math.abs(Tile.getCheckerTiles()[c.getCurrentTileNumber()].y - (Tile.getCheckerTiles()[potentialTileNumber].y)) == (Tile.getTileSize() * x))) {
					return true;
		} else {
			println("Tile is not diagonal. Current checker #"+ c.getCheckerNumber() + ", Potential tile #"+potentialTileNumber+".");
			println("Current tile #" +c.getCurrentTileNumber());
			
			println("Expected" + Tile.getTileSize() * x +", actual x: " + Math.abs(Tile.getCheckerTiles()[c.getCurrentTileNumber()].x - Tile.getCheckerTiles()[potentialTileNumber].x));
			println("Expected" + Tile.getTileSize() * x +", actual y: " + Math.abs(Tile.getCheckerTiles()[c.getCurrentTileNumber()].y - Tile.getCheckerTiles()[potentialTileNumber].y));
			return false;
		}
	}

	private int getTileNumberForPoint(Point p) {
		for(int i = 0; i < Tile.getCheckerTiles().length; i++) {
			if((p.x - Tile.getCheckerTiles()[i].getX()) <= Tile.getTileSize() 
					&& p.y - Tile.getCheckerTiles()[i].getY() <= Tile.getTileSize()) {
				return Tile.getCheckerTiles()[i].getTileNumber();
			}
		}
		return -1;
	}
}

