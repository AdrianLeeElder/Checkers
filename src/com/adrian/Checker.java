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

public class Checker extends Ellipse2D.Double {
	private static Checker[] checkers = new Checker[24];

	private static double oldX, oldY = 0;

	private int checkerNumber = 0;

	private boolean isSelected = false;

	private int currentTileNumber = -1;
	private boolean playerOnePiece = false;
	private boolean playerTwoPiece = false;
	private boolean crownedPiece = false;


	public Checker(double x, double y, boolean playerOnePiece, int checkerNumber, int currentTileNumber) {
		super(x, y, Game.getCheckerWidth(), Game.getCheckerHeight());
		
		this.checkerNumber = checkerNumber;
		this.playerOnePiece = playerOnePiece;
		this.playerTwoPiece = !playerOnePiece;
		this.currentTileNumber = currentTileNumber;
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

	public void setCurrentTileNumber(int i) {
		currentTileNumber = i;	
	}

	@Override
	public String toString() {
		return "Checker [checkerNumber=" + checkerNumber + ", currentTileNumber=" + currentTileNumber
				+ ", playerOnePiece=" + playerOnePiece + ", playerTwoPiece=" + playerTwoPiece + ", crownedPiece="
				+ crownedPiece + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
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
		return (y + (Game.getCheckerHeight() / 2));
	}
	
	public double getCenterX() {
		return (x + (Game.getCheckerWidth() / 2));
	}

	public void setX(double x2) {
		this.x = x2;
	}

	public void setY(double y2) {
		this.y = y2;
	}

	public static BufferedImage getCheckerSprite(Checker c) {
		if(c.isPlayerOnePiece()) {
			return Sprite.getSprite(Sprite.CHECKER).getSubimage(0, 0, 305,305);
		} else {
			return Sprite.getSprite(Sprite.CHECKER).getSubimage(310, 0, 305,305);
		}
	}
}

