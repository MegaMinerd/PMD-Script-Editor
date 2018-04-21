package com.mega.pmds.data;

public enum Direction {
	SOUTH,
	SOUTHEAST,
	EAST,
	NORTHEAST,
	NORTH,
	NORTHWEST,
	WEST,
	SOUTHWEST;
	
	public static Direction fromID(byte id) {
		for(Direction dir : Direction.values()) {
			if ((dir.ordinal()) == (id)) {
				return dir;
			}
		}
		return null;
	}
}
