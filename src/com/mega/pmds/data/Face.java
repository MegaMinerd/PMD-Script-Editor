package com.mega.pmds.data;

public enum Face{
	NEUTRAL,
	SMILING,
	PAINED,
	ANGRY,
	CONFUSED,
	SAD,
	CRYING,
	SHOUTING,
	TEARY_EYED,
	HAPPY,
	DELIGHTED,
	INSPIRED,
	SHOCKED,
	EMPTY,
	NONE,
	SAME;
	
	public static Face fromID(byte id) {
		for(Face face : Face.values()) {
			if ((face.ordinal()&0xF) == (id&0xF)) {
				return face;
			}
		}
		return null;
	}
	
	public static Face fromName(String name) {
		for(Face face : Face.values()) {
			if (face.toString().equalsIgnoreCase(name)) {
				return face;
			}
		}
		return null;
	}
	
	public byte toID() {
		return (byte)(ordinal()&0xF);
	}
	
	@Override
	public String toString() {
		String out = super.toString().toLowerCase();
		out = out.replace('_', '-');
		return out;
	}
}
