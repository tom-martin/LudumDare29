package com.heychinaski.ld29;

public class Level {

	int numCarts;
	int quota;
	long seed;
	float difficulty;
	int time;
	String bgTile;
	String fgTile;
	
	public Level(int numCarts, int quota, long seed, float difficulty, int time, String bgTile, String fgTile) {
		super();
		this.numCarts = numCarts;
		this.quota = quota;
		this.seed = seed;
		this.difficulty = difficulty;
		this.time = time;
		this.bgTile = bgTile;
		this.fgTile = fgTile;
	}

	
	public static Level[] levels = {
		new Level(2, 1, 1, 0.5f, 70, "bgTile.png", "fgTile.png"),
		new Level(3, 1, 2, 0.6f, 60, "bgTile2.png", "fgTile2.png"),
		new Level(3, 2, 3, 0.7f, 60, "bgTile3.png", "fgTile3.png"),
		new Level(4, 2, 4, 0.7f, 60, "fgTile.png", "bgTile3.png"),
		new Level(5, 3, 5, 0.7f, 70, "bgTile2.png", "fgTile3.png"),
		new Level(6, 3, 6, 0.8f, 90, "bgTile.png", "fgTile3.png"),
		new Level(6, 3, 7, 0.9f, 90, "bgTile3.png", "fgTile.png"),
		new Level(6, 4, 8, 1.0f, 90, "bgTile.png", "fgTile2.png"),
		new Level(6, 6, 9, 1.0f, 90, "bgTile.png", "fgTile.png")
	};
}

