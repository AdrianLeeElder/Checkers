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
  private int tileNumber = 0;
  private static double tileSize = (Main.canvasWidth / 8);
  private static Tile[] checkerTiles = new Tile[64];
  private int currentCheckerNumber = -1;
  
  public Tile(double x, double y, double w, double h) {
    super(x, y, w, h);
  }
  
  public int getTileNumber() {
    return this.tileNumber;
  }
  
  public static Tile[] getCheckerTiles() {
    return checkerTiles;
  }
  
  public void setTileNumber(int i) {
    this.tileNumber = i;
  }
  
  public static double getTileSize() {
    return tileSize;
  }
  
  public int getCurrentCheckerNumber() {
	  return currentCheckerNumber;
  }
  
  public void setCurrentCheckerNumber(int number) {
	  currentCheckerNumber = number;
  }
  
  public boolean hasChecker() {
	  return currentCheckerNumber != -1;
  }
}
