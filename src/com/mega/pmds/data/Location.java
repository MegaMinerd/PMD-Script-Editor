package com.mega.pmds.data;

public enum Location {
	UNUSED(""),
	RIGHTWARD_CENTER("rc"),
	BOTTOM_LEFT("bl"),
	BOTTOM_RIGHT("br"),
	INSET_BOTTOM_LEFT("ibl"),
	INSET_BOTTOM_RIGHT("ibr"),
	LEFTWARD_CENTER("lc"),
	LEFTWARD_BOTTOM_LEFT("lbl"),
	RIGHTWARD_BOTTOM_RIGHT("rbr"),
	INSET_LEFTWARD_BOTTOM_LEFT("ilbl"),
	INSET_RIGHTWARD_BOTTOM_RIGHT("irbr"),
	RIGHTWARD_TOP_MIDDLE("rtm"),
	TOP_LEFT("tl"),
	TOP_RIGHT("tr"),
	INSET_TOP_LEFT("itl"),
	INSET_TOP_RIGHT("itr"),
	LEFTWARD_TOP_MIDDLE("ltm"),
	LEFTWARD_TOP_LEFT("ltl"),
	RIGHTWARD_TOP_RIGHT("rtr"),
	INSET_LEFTWARD_TOP_LEFT("iltl"),
	INSET_RIGHTWARD_TOP_RIGHT("irtr"),
	SAME("");
	
	private final String CODE;
	private Location(String in) {
		this.CODE = in;
	}
	
	public static Location fromID(byte id) {
		for(Location loc : Location.values()) {
			if ((loc.ordinal()) == (id)) {
				return loc;
			}
		}
		return null;
	}
	
	public static Location fromCode(String code) {
		for(Location loc : Location.values()) {
			if (loc.toString().equalsIgnoreCase(code)) {
				return loc;
			}
		}
		return null;
	}
	
	public byte toID() {
		return (byte)(ordinal());
	}
	
	@Override
	public String toString() {
		return this.CODE;
	}
}
