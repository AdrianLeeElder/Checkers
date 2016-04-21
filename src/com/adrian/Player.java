package com.adrian;

import java.util.ArrayList;

public class Player {
	public static final int PLAYER_TWO = 1;
	public static final int PLAYER_ONE = 0;
	
	private static Player[] players = new Player[2];
	
	public static Player[] getPlayers() {
		return players;
	}

	
}
