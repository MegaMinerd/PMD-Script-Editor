package com.mega.pmds.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.mega.pmds.InvalidMapDefException;
import com.mega.pmds.InvalidPointerException;
import com.mega.pmds.RomManipulator;

public class ImageExtractor{
	private static Palette[] palettes;
	private static Block[] blocks;
	private static BufferedImage[] chunks;
	private static int palPointer, blockDefPointer, chunkDefPointer, imgDefPointer, animDefPointer;
	private static int chunkWidth, chunkHeight, blockCount, chunkCount, rows, cols;
	
	public static BufferedImage extract(int offset) {
		int pointers = ConfigHandler.getMapDefPointers(offset);
		int type = ConfigHandler.getMapDefType(offset);
		
		try {
			type = loadPointers(pointers, type, offset);
			//Parse palettes
			RomManipulator.seek(palPointer);
			int palCount = RomManipulator.readShort();
			RomManipulator.skip(2);
			palettes = new Palette[palCount];
			for(int i=0; i<palCount; i++) {
				palettes[i] = new Palette();
			}
			
			int animCount;
			if(type==1 || type==4 || type==5) {
				//Parse excerpt metadata
				RomManipulator.seek(animDefPointer);
				int animWidth = RomManipulator.readShort()&0xFFFF;
				int animHeight = RomManipulator.readShort()&0xFFFF;
				animCount = animWidth*animHeight;
			}else {
				animCount = 0;
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
			blocks = new Block[blockCount+animCount];
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
			
			if(type==1 || type==4 || type==5) {
				chunkDefPointer = RomManipulator.getFilePointer();
				
				RomManipulator.seek(animDefPointer+ 2);
				RomManipulator.skip((RomManipulator.readShort()&0xFFFF)*4);
				for(int i=0; i<animCount; i++) {
					int[][] blockData = new int[8][8];
					for(int j=0; j<8; j++) {
						for(int k=0; k<8; k+=2) {
							int pair = RomManipulator.readUnsignedByte();
							blockData[j][k] = pair&0xF;
							blockData[j][k+1] = (pair&0xF0)>>4;
						}
					}
					blocks[blockCount+i] = new Block(blockData);
				}
				
				RomManipulator.seek(chunkDefPointer);
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
						int id = (meta&0x3FF);
						try {
							g.drawImage(blocks[id-1].render(palettes[pal], hor, ver), k*8, j*8, null);
						}catch(ArrayIndexOutOfBoundsException e) {
							
						}
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
			//Just output all of the chunks in order for testing purposes
			if(type==7) {
                for(int i=0; i<rows; i++) {
                    for(int j=0; j<cols; j++) {
                        try {
                            g.drawImage(ImageExtractor.chunks[i*ImageExtractor.cols+j], j*ImageExtractor.chunkWidth*8, i*ImageExtractor.chunkHeight*8, null);
                        }
                        catch (ArrayIndexOutOfBoundsException e){
                        	
                        }
                    }
                }
                return image;
            }
			//Build first row
			Integer[] blank = new Integer[(int)(Math.ceil(cols/2.0)*2)];
			for(int i=0; i<blank.length; i++) {
				blank[i] = 0;
			}
			Integer[] ids = buildRow(blank);
			for(int i=0; i<cols; i++) {
				try {
					g.drawImage(chunks[ids[i]-1], i*chunkWidth*8, 0, null);
				}catch(ArrayIndexOutOfBoundsException e) {
				
				}
			}
			//Build subsequent rows
			for(int i=1; i<rows; i++) {
				ids = ((type == 7) ? buildRow(blank) : buildRow(ids));
				for(int j=0; j<cols; j++){
					try {
						g.drawImage(chunks[ids[j]-1], j*chunkWidth*8, i*chunkHeight*8, null);
					}catch(ArrayIndexOutOfBoundsException e) {
						
					}
				}
			}
			
			return image;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidPointerException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (InvalidMapDefException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	private static int loadPointers(int pointers, int type, int offset) throws IOException, InvalidPointerException, InvalidMapDefException {
		switch(type) {
			case 0:
			case 7:
				RomManipulator.seek(pointers+4);
				palPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				blockDefPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				imgDefPointer = RomManipulator.parsePointer();
				return type;
			case 1:
				RomManipulator.seek(pointers+4);
				palPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				animDefPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				blockDefPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				imgDefPointer = RomManipulator.parsePointer();
				return 1;
			case 2:
				RomManipulator.seek(pointers+4);
				palPointer = RomManipulator.parsePointer();
				//Skip 3 overlay pointers plus the blockDef debug name
				RomManipulator.skip(28);
				blockDefPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				imgDefPointer = RomManipulator.parsePointer();
				return 2;
			case 3:
				RomManipulator.seek(pointers+4);
				palPointer = RomManipulator.parsePointer();
				//Skip 4 overlay pointers plus the blockDef debug name
				RomManipulator.skip(36);
				blockDefPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				imgDefPointer = RomManipulator.parsePointer();
				return 3;
			case 4:
				RomManipulator.seek(pointers+4);
				palPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				animDefPointer = RomManipulator.parsePointer();
				//Skip 3 overlay pointers plus the blockDef debug name
				RomManipulator.skip(28);
				blockDefPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				imgDefPointer = RomManipulator.parsePointer();
				return 4;
			case 5:
				RomManipulator.seek(pointers+4);
				palPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				animDefPointer = RomManipulator.parsePointer();
				//Skip 4 overlay pointers plus the blockDef debug name
				RomManipulator.skip(36);
				blockDefPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				imgDefPointer = RomManipulator.parsePointer();
				return 5;
			case 6:
				palPointer = -1;
				animDefPointer = -1;
				blockDefPointer = -1;
				imgDefPointer = -1;
				char[] parts = ConfigHandler.getParts(offset).toCharArray();
			
				for(char part : parts) {
					switch(part){
						case 'p':
							RomManipulator.seek(pointers+4);
							palPointer = RomManipulator.parsePointer();
							break;
						case 'a':
							RomManipulator.seek(pointers+4);
							animDefPointer = RomManipulator.parsePointer();
							break;
						case 'b':
							RomManipulator.seek(pointers+4);
							blockDefPointer = RomManipulator.parsePointer();
							break;
						case 'i':
							RomManipulator.seek(pointers+4);
							imgDefPointer = RomManipulator.parsePointer();
							break;
						default:
							break;
					}
					pointers += 8;
				}
				if(palPointer<0) {
					throw new InvalidMapDefException("No palette");
				}
				if(blockDefPointer<0) {
					throw new InvalidMapDefException("No tile data");
				}
				if(imgDefPointer<0) {
					throw new InvalidMapDefException("No arrangement data");
				}
				return animDefPointer<0 ? 0 : 1;
			default:
				throw new InvalidMapDefException("Unsupported map def type");
		}
	}
	
	//RomManipulator should already be pointing to the row def
	public static Integer[] buildRow(Integer[] lastIds) throws IOException {
		ArrayList<Integer> data = new ArrayList<Integer>();
		while(data.size()<cols) {
			int control = RomManipulator.readUnsignedByte();
			int len;
			if(control < 0x80) {
				len=(control+1)*2;
				for(int i=0; i<len; i++) {
					data.add(0);
				}
			}else if(control < 0xC0) {
				len = control - 0x80 + 1;
				byte[] temp = new byte[3];
				RomManipulator.read(temp);
				ArrayList<Integer> unpacked = unpack(temp);
				for(int i=0; i<len; i++) {
					data.addAll(unpacked);
				}
			}else {
				len = control - 0xC0 + 1;
				byte[] temp = new byte[3];
				for(int i=0; i<len; i++) {
					RomManipulator.read(temp);
					data.addAll(unpack(temp));
				}
			}
		}
		Integer[] out = data.toArray(new Integer[cols]);
		for(int i=0; i<out.length; i++)
			out[i] = lastIds[i]^out[i];
		return out;
	}
	
	private static ArrayList<Integer> unpack(byte[] in) {
		ArrayList<Integer> out = new ArrayList<Integer>(2);
		out.add((in[0]&0xFF) + ((in[1]&0xF)<<8));
		out.add(((in[1]&0xF0)>>4) + ((in[2]&0xFF)<<4));
		return out;
	}
}
