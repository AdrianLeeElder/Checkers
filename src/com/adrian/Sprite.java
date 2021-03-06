package com.adrian;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Sprite {
	private BufferedImage sprite;
	private static ArrayList<BufferedImage> sprites = new ArrayList<BufferedImage>();
	private final int SPRITE_COUNT = 4;
	public final static int BOARD = 0, CHECKER = 1, CHECKER_CROWNED = 2, SHADOW_EFFECT = 3;
	
	public Sprite() {
		try {

			for(int i = 0; i < SPRITE_COUNT; i++) {
				 sprite = ImageIO.read(new File("images/" + i + ".png"));
				 sprites.add(sprite);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static BufferedImage getSprite(int id) {
		return sprites.get(id);
	}
}
