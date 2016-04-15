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
  private byte tileNumber;
  private static double tileWidth;
  private static double tileHeight;
  
  private static Tile[] checkerTiles = new Tile[32];
  private int currentCheckerNumber = -1;
    
  public Tile(double x, double y) {
    super(x, y, tileWidth, tileHeight);  
    tileNumberCounter++;
    tileNumber = (byte) (tileNumberCounter + 1);
    updateSize();
  }
  public void updateSize() {
	  tileWidth = Game.getGame().getWidth() / 14.3;
	  tileHeight = Game.getGame().getHeight() / 7.90;
	  
	  this.width = tileWidth;
	  this.height = tileHeight; 
  }
  public byte getTileNumber() {
    return this.tileNumber;
  }
  
  public static Tile[] getCheckerTiles() {
    return checkerTiles;
  }
  
  public void setTileNumber(byte i) {
    this.tileNumber = i;
  }
  
  public static double getTileHeight() {
	  return tileHeight;
  }
  
  public static void setTileSize(int width, int height) {
	  tileWidth = width / 14.3;
	  tileHeight = height / 8.25;
  }
  
  public static double getTileWidth() {
	  return tileWidth;
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
