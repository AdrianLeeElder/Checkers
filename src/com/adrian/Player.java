package com.adrian;

import java.util.ArrayList;

public class Player {
	public static final int PLAYER_TWO = 1;
	public static final int PLAYER_ONE = 0;
	
	private boolean isPlayerTurn = false;
	private static ArrayList<Player> players = new ArrayList<Player>();
	static int playerNumber = -1;
	
	public Player(int playerNumber) {
		this.playerNumber = playerNumber;
		players.add(this);
	}

	public boolean isPlayerTurn() {
		return isPlayerTurn;
	}
	
	public static ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void setTurn(boolean turn) {
		isPlayerTurn = turn;
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}
}
