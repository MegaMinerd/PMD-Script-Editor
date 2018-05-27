package com.mega.pmds.util;

import java.awt.image.BufferedImage;

public class Block {
	private final int[][] data;
	
	public Block(int[][] dataIn) {
		this.data = dataIn;
	}
	
	public BufferedImage render(Palette pal, boolean hor, boolean ver) {
		BufferedImage block = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				block.setRGB(j, i, pal.getRgb(data[ver?7-i:i][hor?7-j:j]));
			}
		}
		return block;
	}

}
