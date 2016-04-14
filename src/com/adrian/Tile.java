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
  private byte tileNumber = 0;
  
  private static double tileWidth = Main.canvasWidth / 10.48;

  private static double tileHeight = Main.canvasHeight / 6.25;
  
  private static Tile[] checkerTiles = new Tile[32];
  private int currentCheckerNumber = -1;
    
  public Tile(double x, double y, double w, double h) {
    super(x, y, w, h);  
   
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
}
