package com.mega.pmds;

public class InvalidPointerException extends Exception {
	private final int offset;
	
	public InvalidPointerException(int offsetIn) {
		super("Invalid pointer at " + offsetIn);
		this.offset = offsetIn;	
	}
	
	public int getOffset() {
		return offset;
	}
}
