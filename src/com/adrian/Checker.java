/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adrian;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Checker {
	private static Checker[] checkers = new Checker[24];
	private static double checkerWidth = Tile.getTileWidth() - 20;
	private static double checkerHeight = Tile.getTileHeight() - 20;

	private static double oldX, oldY = 0;

	private int checkerNumber = 0;

	private boolean isSelected = false;

	private int currentTileNumber = -1;
	private boolean playerOnePiece = false;
	private boolean playerTwoPiece = false;
	private boolean crownedPiece = false;

	private static BufferedImage checkerSprite = null;

	double x;
	private double y;

	public Checker(double x, double y, boolean playerOne) {
		setX(x);
		setY(y);
		
		playerOnePiece = playerOne ? playerOne : false;
		playerTwoPiece = playerOnePiece ? false : true;
	}

	public int getCheckerNumber() {
		return checkerNumber;
	}

	public void setCheckerNumber(int i) {
		this.checkerNumber = i;
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

	public int getCurrentTileNumber() {
		return currentTileNumber;
	}

	public void setCurrentTile(int i) {
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
	
	public double getCenterY() {
		return (y + (checkerHeight / 2));
	}
	
	public double getCenterX() {
		return (x + (checkerWidth / 2));
	}

	public void setX(double x2) {
		this.x = x2;
	}

	public void setY(double y2) {
		this.y = y2;
	}

	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

	public static double getCheckerHeight() {
		return checkerHeight;
	}

	public static double getCheckerWidth() {
		return checkerWidth;
	}
}

