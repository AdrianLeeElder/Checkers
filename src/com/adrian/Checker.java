/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adrian;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class Checker extends Ellipse2D.Double {
  private static Checker[] checkers = new Checker[24];
  private static double checkerSize = Tile.getTileSize() - 20;
  
  private static double oldX, oldY = 0;

  private byte checkerNumber = 0;
  
  private boolean isSelected = false;
  
  private byte currentTileNumber = -1;
  private boolean playerOnePiece = false;
  private boolean playerTwoPiece = false;
  private boolean crownedPiece = false;
  
  public Checker(double x, double y, double width, double height, boolean playerOne, boolean playerTwo) {
    super(x, y, width, height);
    
    this.playerOnePiece = playerOne;
    this.playerTwoPiece = playerTwo;
  }
    
    public static Color getFillColorPlayerOne() {
      return Color.white;
    }
    
    public static Color getFillColorPlayerTwo() {
      return Color.red;
    }
    
    public static Color getCrownFillColorOne() {
    	return Color.cyan;
    }
    
    public static Color getCrownFillColorTwo() {
    	return Color.green;
    }
    
    public byte getCheckerNumber() {
        return checkerNumber;
    }
    
    public void setCheckerNumber(byte checkerNumber) {
        this.checkerNumber = checkerNumber;
    }
    
    public static double getCheckerSize() {
        return checkerSize;
    }
    
    public static void setOldX(double x) {
        oldX = x;
    }
    
    public static void setOldY(double y) {
        oldY = y;
    }
    
    public static double getOldX() {
        return oldX;
    }
    
    public static double getOldY() {
        return oldY;
    }
    
    public boolean isSelected() {
        return this.isSelected;
    }
    
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    public static Checker getSelectedChecker() {
        for(Checker c: Checker.getCheckers()) {
        	if(c == null) continue;
            if(c.isSelected()) return c;
        }
        return null;
    }
    
    public byte getCurrentTileNumber() {
    	return currentTileNumber;
    }

	public void setCurrentTile(byte i) {
		currentTileNumber = i;	
	}
	
	public boolean isPlayerOnePiece() {
		return playerOnePiece;
	}
	
	public boolean isPlayerTwoPiece() {
		return playerTwoPiece;
	}
	
	public static Checker[] getCheckers() {
		return checkers;
	}
	
	public boolean isCrowned() {
		return crownedPiece;
	}

}

