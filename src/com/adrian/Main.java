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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Main extends JFrame {

	private int minimumWidth = 720;
	private int minimumHeight = 600;
	private static int widthX = 1100;
	private static int widthY = 700;
	
	public static final double canvasWidth = (double) widthX - 100;//width of checker canvas
	public static final double canvasHeight = (double) widthY - 100;//height of checker canvas
	static double version = 1.0;

	
	private static Game game = null;
	public static Game getGame() {
		return game;
	}
	//store the tile number we are trying to move to so
	//we don't have to keep running the getTileNumberForPoint method 

	public void init() {
		game = new Game();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(widthX, widthY));
		
		this.getContentPane().add("Center", game);
		this.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
		
		this.pack();
		this.setLocationRelativeTo(null);

		this.setVisible(true);
	}


	public Main(String string) {
		loadImages();
		init();
	}
	
	public void loadImages() {
		new Sprite();
	}

	public static void main(String[] args) { 
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main("Checkers V"+ version);
			}
		});
	}
}
