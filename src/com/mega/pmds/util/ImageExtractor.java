package com.mega.pmds.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mega.pmds.RomManipulator;

public class ImageExtractor{
	private static Palette[] palette;
	private static int palPointer, blockDefPointer, chunkDefPointer, imgDefPointer;
	private static int chunkWidth, chunkHeight, chunkCount, rows, cols, rowDefLen;
	
	public static BufferedImage extract(int[] pointers) {
		if(pointers == null)
			return null;
		
		palPointer = pointers[0];
		blockDefPointer = pointers[1];
		chunkDefPointer = pointers[2];
		imgDefPointer = pointers[3];
		
		try {			
			//Parse palettes
			RomManipulator.seek(palPointer);
			int palCount = RomManipulator.readInt();
			palette = new Palette[palCount];
			for(int i=0; i<palCount; i++) {
				palette[i] = new Palette();
			}
			
			//Parse block metadata
			RomManipulator.seek(blockDefPointer - 0x10);
			chunkWidth = RomManipulator.readShort()&0xFFFF;
			chunkHeight = RomManipulator.readShort()&0xFFFF;
			//The use of these bytes is unknown
			RomManipulator.skip(10);
			chunkCount = RomManipulator.readShort()&0xFFFF;
			
			//Parse chunk metadata
			RomManipulator.seek(imgDefPointer-8);
			cols = RomManipulator.readByte()&0xFF;
			rows = RomManipulator.readByte()&0xFF;
			rowDefLen = ((int)Math.ceil(cols/2.0)) * 3 + 1;
			
			//Build image
			BufferedImage image = new BufferedImage(cols*chunkWidth*8, rows*chunkHeight*8, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			//Build first row
			byte[] def = new byte[rowDefLen-1];
			RomManipulator.seek(imgDefPointer+1);
			RomManipulator.read(def);
			int[] ids = unpack(def);
			for(int i=0; i<cols; i++) {
				RomManipulator.seek(chunkDefPointer + (ids[i]-1)*2*chunkWidth*chunkHeight);
				g.drawImage(buildChunk(), i*chunkWidth*8, 0, null);
			}
			//Build subsequent rows
			for(int i=1; i<rows; i++) {
				RomManipulator.seek(imgDefPointer + i*rowDefLen + 1);
				RomManipulator.read(def);
				int xors[] = unpack(def);
				for(int j=0; j<cols; j++){
					ids[j] = ids[j]^xors[j];
					RomManipulator.seek(chunkDefPointer + (ids[j]-1)*2*chunkWidth*chunkHeight);
					g.drawImage(buildChunk(), j*chunkWidth*8, i*chunkHeight*8, null);
				}
			}
			return image;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private static int[] unpack(byte[] def) {
		int[] out = new int[cols];
		for(int i=0; i<cols; i++) {
			if(i%2==0)
				out[i] = (def[(i/2)*3]&0xFF) + ((def[(i/2)*3+1]&0xF)<<8);
			else
				out[i] = ((def[(i/2)*3+1]&0xF0)>>4) + ((def[(i/2)*3+2]&0xFF)<<4);
		}
		return out;
	}
	
	//RomManipulator should already be pointing to the chunk def
	private static BufferedImage buildChunk() throws IOException {
		BufferedImage chunk = new BufferedImage(chunkWidth*8, chunkHeight*8, BufferedImage.TYPE_INT_RGB);
		Graphics g = chunk.getGraphics();
		for(int i=0; i<chunkHeight; i++) {
			for(int j=0; j<chunkWidth; j++){
				g.drawImage(buildBlock(), j*8, i*8, null);
			}
		}
		return chunk;
	}
	
	private static BufferedImage buildBlock() throws IOException {
		BufferedImage block = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
		int lastPointer = RomManipulator.getFilePointer();
		int data = RomManipulator.readShort()&0xFFFF;
		int pal = (data&0xF000)>>12;
		int hor = ((data&0x400)>>9)-1;
		int ver = ((data&0x800)>>10)-1;
		int id = data&0x3FF;
		//Order data
		RomManipulator.seek(blockDefPointer+(id-1)*32);
		
		for(int j=(ver>0 ? 7 : 0); ver>0 ? j>=0 : j<8; j-=ver){
			for(int i=(hor>0 ? 7 : 0); hor>0 ? i>=0 : i<8; i-=(hor*2)) {
				byte temp = RomManipulator.readByte();
				int a = temp&0xF;
				int b = (temp&0xF0)>>4;
				try {
					block.setRGB(i, j, palette[pal].getRgb(a));
				}catch(ArrayIndexOutOfBoundsException e) {
					block.setRGB(i, j, palette[0].getRgb(a));
				}
				try {
					block.setRGB(i-hor, j, palette[pal].getRgb(b));
				}catch(ArrayIndexOutOfBoundsException e) {
					block.setRGB(i-hor, j, palette[0].getRgb(b));
				}
			}
		}
		RomManipulator.seek(lastPointer+2);
		return block;
	}
}
