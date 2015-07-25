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
  private static double tileSize = (Main.canvasWidth / 8);
  private static Tile[] checkerTiles = new Tile[64];
  private byte currentCheckerNumber = -1;
  
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
  
  public static double getTileSize() {
    return tileSize;
  }
  
  public byte getCurrentCheckerNumber() {
	  return currentCheckerNumber;
  }
  
  public void setCurrentCheckerNumber(byte number) {
	  currentCheckerNumber = number;
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
