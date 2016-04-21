/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adrian;

import java.awt.*;
import java.awt.geom.*;

/**
 * Auto Generated Java Class.
 */
public class Tile extends Rectangle2D.Double {
  private static byte tileNumberCounter = 0;
  private int tileNumber;
 
  private static Tile[] checkerTiles = new Tile[32];
  private int currentCheckerNumber = -1;
    
  public Tile(double x, double y, int i) {
    super(x, y, Game.getTileWidth(), Game.getTileHeight());  
    
    tileNumber = i;
  }

  public int getTileNumber() {
    return this.tileNumber;
  }
  
  public static Tile[] getCheckerTiles() {
    return checkerTiles;
  }
  
  public void setTileNumber(byte i) {
    this.tileNumber = i;
  }
  
  public double getHeight() {
	  return Game.getGame().getHeight() / 7.90;
  }
  
  public double getWidth() {
	  return Game.getGame().getWidth() / 14.3;
  }
  
  public int getCurrentCheckerNumber() {
	  return currentCheckerNumber;
  }
  
  public void setCurrentCheckerNumber(int i) {
	  currentCheckerNumber = i;
  }
  
  public boolean hasChecker() {
	  return currentCheckerNumber != -1;
  }
  
  public boolean hasPlayerTwoChecker() {
	  return (hasChecker() && Checker.getCheckers()[currentCheckerNumber].isPlayerTwoPiece());
  }
  
  public boolean hasPlayerOneChecker() {
	  return (hasChecker() && Checker.getCheckers()[currentCheckerNumber].isPlayerOnePiece());
  }
  @Override
public String toString() {
	return "Tile [tileNumber=" + tileNumber + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
}
  
}
