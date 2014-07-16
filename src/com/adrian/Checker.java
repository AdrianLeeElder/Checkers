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
    
  private static double checkerSize = Tile.getTileSize() - 20;
  
  private static ArrayList<Checker> checkersComputer = new ArrayList<Checker>();
  private static ArrayList<Checker> checkersPlayer = new ArrayList<Checker>();
  
  private static double oldX, oldY = 0;

  private int checkerNumber = 0;
  
  private boolean isSelected = false;
  
  private int currentTileNumber = -1;
  
  public Checker(double x, double y, double width, double height) {
    super(x, y, width, height);
  }
  
  public static ArrayList<Checker> getCheckersComputer() {
    return checkersComputer;
  }
  
  public static ArrayList<Checker> getCheckersPlayer() {
    return checkersPlayer;
  }
    
    public static Color getFillColorPlayer() {
      return Color.white;
    }
    
    public static Color getFillColorComputer() {
      return Color.red;
    }
    
    public int getCheckerNumber() {
        return checkerNumber;
    }
    
    public void setCheckerNumber(int checkerNumber) {
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
        for(Checker c: getCheckersPlayer()) {
            if(c.isSelected()) return c;
        }
        return null;
    }
    
    public int getCurrentTileNumber() {
    	return currentTileNumber;
    }

	public void setCurrentTile(int i) {
		currentTileNumber = i;	
	}

}

