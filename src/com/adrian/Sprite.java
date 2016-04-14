package com.adrian;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Sprite {
	private BufferedImage sprite;
	private static ArrayList<BufferedImage> sprites = new ArrayList<BufferedImage>();
	private final int SPRITE_COUNT = 2;
	public final static int BOARD = 0, CHECKER = 1;
	
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
