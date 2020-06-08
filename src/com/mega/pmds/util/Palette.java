package com.mega.pmds.util;

import java.io.IOException;

import com.mega.pmds.RomManipulator;

public class Palette {
	private final int[] rgb;
	
	public Palette(boolean isType8) throws IOException {
		rgb = new int[16];
		rgb[0] = isType8 ? loadColor() : 0;
		for(int i=1; i<16; i++) {
			rgb[i]=loadColor();
		}
	}
	
	private int loadColor() throws IOException {
		int temp = 0;
		temp += (RomManipulator.readUnsignedByte())<<16;
		temp += (RomManipulator.readUnsignedByte())<<8;
		temp += (RomManipulator.readUnsignedByte());
		RomManipulator.skip(1);
		return temp;
	}
	
	public int getRgb(int id) {
		return rgb[id];
	}
}
