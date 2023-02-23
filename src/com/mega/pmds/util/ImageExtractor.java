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
			System.out.println("Palette: " + Integer.toHexString(palPointer).toUpperCase());
			System.out.println("Tiles: " + Integer.toHexString(blockDefPointer).toUpperCase());
			System.out.println("Mapping: " + Integer.toHexString(imgDefPointer).toUpperCase());

			//Parse palettes
			RomManipulator.seek(palPointer);
			int palCount = type%9==8 ? 16 : RomManipulator.readShort();
			if(type%9!=8)
				RomManipulator.skip(2);
			palettes = new Palette[palCount];
			for(int i=0; i<palCount; i++) {
				palettes[i] = new Palette(type%9==8);
			}
			
			int animCount;
			if(type%9==1 || type%9==4 || type%9==5) {
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
			if(type%9==8)
				blockCount++;
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
			
			if(type%9==1 || type%9==4 || type%9==5) {
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
			System.out.println("Offset: " + Integer.toHexString(RomManipulator.getFilePointer()));
			//System.out.println(palettes.length);
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
							//System.out.println(pal);
							if(type>8 && id-1<0)
								continue;
							g.drawImage(blocks[type%9==8 ? id : id-1].render(palettes[pal], hor, ver), k*8, j*8, null);
						}catch(ArrayIndexOutOfBoundsException e) {
							System.out.println(String.format("Error in chunk %x block %x, %x: %s", i, j, k, e));
							System.out.println(String.format("Block: %x/%x", type%9==8 ? id : id-1, blocks.length));
							System.out.println(String.format("Palette: %x/%x", pal, palettes.length));
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
			BufferedImage image = new BufferedImage(cols*chunkWidth*8, rows*chunkHeight*8*(type>8?2:1), BufferedImage.TYPE_INT_RGB);
			System.out.println(String.format("%d: %d type %d layers", type, type>8?2:1, type%9));
			Graphics g = image.getGraphics();
			//Just output all of the chunks in order for testing purposes
			if(type%9==7 || type%9==8) {
                for(int i=0; i<rows; i++) {
                    for(int j=0; j<cols; j++) {
                        try {
                            g.drawImage(ImageExtractor.chunks[i*cols+j], j*chunkWidth*8, i*chunkHeight*8, null);
                        }
                        catch (ArrayIndexOutOfBoundsException e){
                        	
                        }
                    }
                }
				RomManipulator.setLength(0x2000000);
                return image;
            }
			for(int layer=0; layer<(type>8?2:1); layer++) {
				System.out.println(layer);
				//Build first row
				Integer[] blank = new Integer[(int)(Math.ceil(cols/2.0)*2)];
				for(int i=0; i<blank.length; i++) {
					blank[i] = 0;
				}
				Integer[] ids = buildRow(blank);
				for(int i=0; i<cols; i++) {
					try {
						g.drawImage(chunks[ids[i]-1], i*chunkWidth*8, chunkHeight*8*rows*layer, null);
					}catch(ArrayIndexOutOfBoundsException e) {
				
					}
				}
				//Build subsequent rows
				for(int i=1; i<rows; i++) {
					ids = ((type%9==7) ? buildRow(blank) : buildRow(ids));
					for(int j=0; j<cols; j++){
						try {
							g.drawImage(chunks[ids[j]-1], j*chunkWidth*8, chunkHeight*8*(rows*layer+i), null);
						}catch(ArrayIndexOutOfBoundsException e) {
						
						}
					}
				}
			}
			
			System.out.println("End: " + Integer.toHexString(RomManipulator.getFilePointer()));
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
		switch(type%9) {
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
				return type;
			case 2:
				RomManipulator.seek(pointers+4);
				palPointer = RomManipulator.parsePointer();
				//Skip 3 overlay pointers plus the blockDef debug name
				RomManipulator.skip(28);
				blockDefPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				imgDefPointer = RomManipulator.parsePointer();
				return type;
			case 3:
				RomManipulator.seek(pointers+4);
				palPointer = RomManipulator.parsePointer();
				//Skip 4 overlay pointers plus the blockDef debug name
				RomManipulator.skip(36);
				blockDefPointer = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				imgDefPointer = RomManipulator.parsePointer();
				return type;
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
				return type;
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
				return type;
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
				return (animDefPointer<0 ? 0 : 1) + (type>8?9:0);
			case 8:
				RomManipulator.seek(pointers+12);
				blockDefPointer = RomManipulator.length();
				int off1 = RomManipulator.parsePointer();
				RomManipulator.skip(12);
				int off2 = RomManipulator.parsePointer();
				RomManipulator.skip(4);
				palPointer = RomManipulator.parsePointer();
				at4px(off2, true);
				chunkDefPointer = RomManipulator.length();
				at4px(off1, false);
				imgDefPointer = ConfigHandler.getAssem(offset);
				return type;
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
	
	//Decompress data at an offset and return to original location
	private static void at4px(int offset, boolean isTiles) throws IOException {
		//System.out.println("AT4");
		//System.out.println("Offset:" + Integer.toHexString(offset));
		int origOff = RomManipulator.getFilePointer();
		RomManipulator.seek(offset);
		ArrayList<Byte> data = new ArrayList<Byte>();
		//if rom.read(5) != b'AT4PX':
		//	raise ValueError('Wrong magic bytes for compressed data')
		RomManipulator.skip(7);
		//end, = unpack('<H', rom.read(2))
		
		// The control codes used for 0-flags vary
		//controls = list(rom.read(9))
		ArrayList<Byte> controls = new ArrayList<Byte>();
		for(int i=0; i<9; i++) {
			controls.add(RomManipulator.readByte());
		}
		
		//length, = unpack('<H', rom.read(2))
		int len = RomManipulator.readShort();
		if(isTiles) {
			data.add((byte)3);
			data.add((byte)0);
			data.add((byte)3);
			data.add((byte)0);
			//System.out.println("Length:" + Integer.toHexString(len));
			data.add((byte)((len/0x20)&0xFF));
			data.add((byte)(((len/0x20)&0xFF00)>>8));
			for(int i=0; i<10; i++)
				data.add((byte)0);
		}

		
		while((isTiles ? data.size()-0x10 : data.size()) < len) {
			byte flags = RomManipulator.readByte();
			//if(!isTiles) System.out.println("Flags:" + Integer.toHexString(flags));
			for(int i=0; i<8; i++) {
				if((flags&(0x80>>i))!=0) {
				// Flag 1: append one byte as-is
					data.add((byte)(RomManipulator.readUnsignedByte()&0xFF));
				}else {
				// Flag 0: do one of two fancy things based on the next byte's
				// high and low nybbles
					byte control = RomManipulator.readByte();
					byte high = (byte)((control >> 4)&0x0F);
					byte low = (byte)(control & 0x0F);

					if(controls.contains(high)){
						// Append a pattern of four nybbles. The high bits determine
						// the pattern, and the low bits determine the base nybble.
						control = (byte)controls.indexOf(high);
						byte[] nybbles = {low, low, low, low};

						if(control==0) {}
						else if(control<=4) {
							// Lower a particular pixel
							if(control==1)
								for(int j=0; j<4; j++)
									nybbles[j]++;
							nybbles[control-1]--;
						}
						else {
							// 5 <= control <= 8; raise a particular pixel
							if(control==5)
								for(int j=0; j<4; j++)
									nybbles[j]--;
							nybbles[control-5]++;
						}
						// Pack the pixels into bytes and append them
						data.add((byte)((nybbles[0]<<4) | nybbles[1]));
						data.add((byte)((nybbles[2]<<4) | nybbles[3]));
					}
					else {
						// Append a sequence of bytes previously used in the data.
						// This can overlap with the beginning of the appended bytes!
						// The high bits determine the length of the sequence, and
						// the low bits help determine the where the sequence starts.
						int off = -0x1000;
						off += ((low << 8) | (RomManipulator.readByte()&0xFF));
						for(int j=0; j<(high+3); j++)
							try {
								data.add(data.get(data.size()+off));
							}catch(IndexOutOfBoundsException e) {
								data.add((byte)0);
							}
					}
				}
				if((isTiles ? data.size()-0x10 : data.size()) >= len)
					break;
			}
		}
		RomManipulator.seek(RomManipulator.length());
		for(byte b : data)
			RomManipulator.writeByte(b);
		if(!isTiles)
			RomManipulator.writeShort((short)(data.size()/18), 0x200000E);
		RomManipulator.seek(origOff);
	}
}


