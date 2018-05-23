package com.mega.pmds.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.mega.pmds.RomManipulator;

public class ImageExtractor{
	private static Palette[] palette;
	private static int palPointer, blockDefPointer, chunkDefPointer, imgDefPointer, animDefPointer;
	private static int chunkWidth, chunkHeight, chunkCount, rows, cols;
	
	public static BufferedImage extract(int[] pointers) {
		if(pointers == null)
			return null;
		
		palPointer = pointers[0];
		blockDefPointer = pointers[1];
		chunkDefPointer = pointers[2];
		imgDefPointer = pointers[3];
		animDefPointer = pointers[4];
		
		try {			
			//Parse palettes
			RomManipulator.seek(palPointer);
			int palCount = RomManipulator.readShort();
			RomManipulator.skip(2);
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
			cols = RomManipulator.readUnsignedByte();
			rows = RomManipulator.readUnsignedByte();
			
			//Build image
			BufferedImage image = new BufferedImage(cols*chunkWidth*8, rows*chunkHeight*8, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			//Build first row
			RomManipulator.seek(imgDefPointer);
			Integer[] xors = new Integer[(int)(Math.ceil(cols/2.0)*2)];
			Integer[] ids = buildRow(xors);
			imgDefPointer = RomManipulator.getFilePointer();
			for(int i=0; i<cols; i++) {
				RomManipulator.seek(chunkDefPointer + (ids[i]-1)*2*chunkWidth*chunkHeight);
				g.drawImage(buildChunk(), i*chunkWidth*8, 0, null);
			}
			//Build subsequent rows
			for(int i=1; i<rows; i++) {
				RomManipulator.seek(imgDefPointer);
				xors = buildRow(xors);
				imgDefPointer = RomManipulator.getFilePointer();
				for(int j=0; j<cols; j++){
						ids[j] = ids[j]^xors[j%xors.length];					
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
				int offset = data.size();
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
		if(id==0) {
			for(int i=0; i<8; i++) {
				for(int j=0; j<8; j++) {
					block.setRGB(i, j, 0x000000);
				}
			}
			return block;
		}else if(id>=0x175 && animDefPointer!=0)
			RomManipulator.seek(animDefPointer+(id-0x175)*32);
		else
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
