package com.mega.pmds.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.mega.pmds.InvalidPointerException;
import com.mega.pmds.RomManipulator;

public class ImageExtractor{
	private static Palette[] palettes;
	private static Block[] blocks;
	private static BufferedImage[] chunks;
	private static int palPointer, blockDefPointer, chunkDefPointer, imgDefPointer, animDefPointer;
	private static int chunkWidth, chunkHeight, blockCount, chunkCount, rows, cols, rowDefLen;
	
	public static BufferedImage extract(int pointers) {
		try {

			RomManipulator.seek(pointers+4);
			palPointer = RomManipulator.parsePointer();
			RomManipulator.skip(4);
			blockDefPointer = RomManipulator.parsePointer();
			RomManipulator.skip(4);
			imgDefPointer = RomManipulator.parsePointer();
			
			//Parse palettes
			RomManipulator.seek(palPointer);
			int palCount = RomManipulator.readShort();
			RomManipulator.skip(2);
			palettes = new Palette[palCount];
			for(int i=0; i<palCount; i++) {
				palettes[i] = new Palette();
			}
			
			//Parse block/chunk metadata
			RomManipulator.seek(blockDefPointer);
			chunkWidth = RomManipulator.readShort()&0xFFFF;
			chunkHeight = RomManipulator.readShort()&0xFFFF;
			blockCount = (RomManipulator.readShort()&0xFFFF)-1;
			//The use of these bytes is unknown
			RomManipulator.skip(8);
			chunkCount = (RomManipulator.readShort()&0xFFFF)-1;
			
			//Build blocks
			blocks = new Block[blockCount];
			for(int i=0; i<(blockCount); i++) {
				int[][] blockData = new int[8][8];
				for(int j=0; j<8; j++) {
					for(int k=0; k<8; k+=2) {
						int pair = RomManipulator.readUnsignedByte();
						blockData[j][k] = pair&0xF;
						blockData[j][k+1] = (pair&0xF0)>>4;
					}
				}
				blocks[i] = new Block(blockData);
			}
			
			//Build chunks
			chunks = new BufferedImage[chunkCount];
			for(int i=0; i<chunkCount; i++) {
				BufferedImage chunk = new BufferedImage(chunkWidth*8, chunkHeight*8, BufferedImage.TYPE_INT_RGB);
				Graphics g = chunk.getGraphics();
				for(int j=0; j<chunkHeight; j++) {
					for(int k=0; k<chunkWidth; k++){
						int meta = RomManipulator.readShort()&0xFFFF;
						int pal = (meta&0xF000)>>12;
						boolean hor = ((meta&0x400)>>10)==1;
						boolean ver = ((meta&0x800)>>11)==1;
						int id = meta&0x3FF;
						g.drawImage(blocks[id-1].render(palettes[pal], hor, ver), k*8, j*8, null);
					}
				}
				chunks[i] = chunk;
			}
			
			//Parse chunk metadata
			RomManipulator.seek(imgDefPointer+4);
			cols = RomManipulator.readUnsignedByte();
			rows = RomManipulator.readUnsignedByte();
			RomManipulator.skip(6);
			
			//Build image
			BufferedImage image = new BufferedImage(cols*chunkWidth*8, rows*chunkHeight*8, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			//Build first row
			Integer[] xors = new Integer[(int)(Math.ceil(cols/2.0)*2)];
			Integer[] ids = buildRow(xors);
			for(int i=0; i<cols; i++) {
				g.drawImage(chunks[ids[i]-1], i*chunkWidth*8, 0, null);
			}
			//Build subsequent rows
			for(int i=1; i<rows; i++) {
				xors = buildRow(xors);
				for(int j=0; j<cols; j++){
					ids[j] = ids[j]^xors[j%xors.length];
					g.drawImage(chunks[ids[j]-1], j*chunkWidth*8, i*chunkHeight*8, null);
				}
			}
			
			return image;
		} catch (IOException e) {
			return null;
		} catch (InvalidPointerException e) {
			return null;
		}
	}
	
	//RomManipulator should already be pointing to the row def
	public static Integer[] buildRow(Integer[] lastXors) throws IOException {
		ArrayList<Integer> data = new ArrayList<Integer>();
		while(data.size()<cols) {
			int control = RomManipulator.readUnsignedByte();
			int len = (control&0xF)+1;
			control = (control&0xF0)>>4;
			if(control==0xC) {
				byte[] temp = new byte[3];
				for(int i=0; i<len; i++) {
					RomManipulator.read(temp);
					data.addAll(unpack(temp));
				}
			}else if(control==0xD) {
				len+=16;
				byte[] temp = new byte[3];
				for(int i=0; i<len; i++) {
					RomManipulator.read(temp);
					data.addAll(unpack(temp));
				}
			}else if(control==0x8){
				byte[] temp = new byte[3];
				RomManipulator.read(temp);
				ArrayList<Integer> unpacked = unpack(temp);
				for(int i=0; i<len; i++) {
					data.addAll(unpacked);
				}
			}else if(control==0x0) {
				len*=2;
				for(int i=0; i<len; i++) {
					data.add(0);
				}
			}
		}
		return data.toArray(new Integer[cols]);
	}
	
	private static ArrayList<Integer> unpack(byte[] in) {
		ArrayList<Integer> out = new ArrayList<Integer>(2);
		out.add((in[0]&0xFF) + ((in[1]&0xF)<<8));
		out.add(((in[1]&0xF0)>>4) + ((in[2]&0xFF)<<4));
		return out;
	}
}
