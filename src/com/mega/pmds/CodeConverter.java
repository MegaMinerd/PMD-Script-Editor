package com.mega.pmds;

import java.io.IOException;

import com.mega.pmds.data.DataDict;
import com.mega.pmds.data.Face;
import com.mega.pmds.data.Location;

public class CodeConverter {
	/**
	 * Convert a single command from text to data 
	 * @param command human-readable command
	 * @return game-readable data
	 */
	public byte[] parseCommand(String command) {
		//TODO
		return null;
	}
	
	/**
	 * Convert a single command from data to text
	 * @param data game-readable data
	 * @return human-readable command
	 */
	public static String interpretCommand(byte[] data) {
		switch((int)(data[0]&0xFF)) {
			case 0x02:
				String command = "WarpTo\tdirect, ";
				command += parseUnsignedShort(data, 2) + ", ";
				command += DataDict.dungeons[parseUnsignedInt(data, 4)];
				return command;
			case 0x04:
				command = "WarpTo\tmap, ";
				command += parseUnsignedShort(data, 2) + ", ";
				command += DataDict.dungeons[parseUnsignedInt(data, 4)];
				return command;
			case 0x0D:
				command = "Call\t";
				command += "0x" + Integer.toHexString(data[1]) + ", ";
				command += "0x" + Integer.toHexString(parseUnsignedShort(data, 2));
				return command;
			case 0x2D:
				command = "Load\t";
				command += "0x" + Integer.toHexString(data[1]) + ", ";
				command += interpretActorID(data[2])  + ", ";
				command += parseUnsignedInt(data, 4);
				return command;
			case 0x2E:
				command = "Kaomado\t";
				if(data[1]!=0x15)
					command += Location.fromID(data[1]).toString() + ", ";
				command += interpretActorID(data[2]) + ", ";
				command += Face.fromID(data[4]).toString();
				return command;
			case 0x30:
				return "ExitMsg";
			case 0x32:
				command = "Message\tplain, ";
				command += interpretActorID(data[2]) + ", ";
                String offset = parsePointer(data);
				command += offset;
				try {
                    command += "\n\t\t" + RomManipulator.readStringAndReturn(Integer.parseInt(offset, 16)).replace("\n", "\n\t\t");
                }
                catch (IOException e) {
                    command += "\tError reading string!";
                    e.printStackTrace();
                }
                return command;
			case 0x33:
				command = "Message\tthought, ";
				command += interpretActorID(data[2]) + ", ";
                offset = parsePointer(data);
				command += offset;
				try {
                    command += "\n\t\t" + RomManipulator.readStringAndReturn(Integer.parseInt(offset, 16)).replace("\n", "\n\t\t");
                }
                catch (IOException e) {
                    command += "\tError reading string!";
                    e.printStackTrace();
                }
				return command;
			case 0x34:
				command = "Message\tspeech, ";
				command += interpretActorID(data[2]) + ", ";
                offset = parsePointer(data);
				command += offset;
				try {
                    command += "\n\t\t" + RomManipulator.readStringAndReturn(Integer.parseInt(offset, 16)).replace("\n", "\n\t\t");
                }
                catch (IOException e) {
                    command += "\tError reading string!";
                    e.printStackTrace();
                }
                return command;
			case 0x35:
				command = "Message\treading, ";
				command += interpretActorID(data[2]) + ", ";
                offset = parsePointer(data);
				command += offset;
				try {
                    command += "\n\t\t" + RomManipulator.readStringAndReturn(Integer.parseInt(offset, 16)).replace("\n", "\n\t\t");
                }
                catch (IOException e) {
                    command += "\tError reading string!";
                    e.printStackTrace();
                }
                return command;
			case 0x3C:
				switch((int)(data[1]&0xFF)) {
					case 0x2C:
						command = "Reward\t";
						command += DataDict.items[parseUnsignedShort(data, 4)] + ", ";
						command += parseUnsignedInt(data, 8);
						return command;
					default:
						return bytesToString(data);
				}
			case 0x3D:
				command = "Rename\t";
				command += interpretActorID((byte)(data[4]-1));
				return command;
			case 0x3E:
				return "Rename\tteam";
			case 0x42:
				return "StopSng";
			case 0x44:
				command = "PlaySng\t";
				command += parseUnsignedShort(data, 4);
				return command;
			case 0x45:
				command = "FadeIn\t";
				command += parseUnsignedShort(data, 2) + ", ";
				command += parseUnsignedShort(data, 4);
				return command;
			case 0x48:
				command = "FadeOut\t";
				command += parseUnsignedShort(data, 2);
				return command;
			case 0x4C:
				command = "PlaySnd\t";
				command += parseUnsignedShort(data, 4);
				return command;
			case 0x4D:
				command = "StopSnd\t";
				command += parseUnsignedShort(data, 4);
				return command;
			case 0x54:
				command = "SetAnim\t0x";
				command += Integer.toHexString(parseUnsignedInt(data, 2));
				return command;
			case 0x62:
				command = "Move\tnoRotate, ";
				command += parseUnsignedShort(data, 2) + ", ";
				command += parseInt(data, 4) + ", ";
				command += parseInt(data, 8) + ", ";
				return command;
			case 0x68:
				command = "ChangeZ\t";
				command += parseShort(data, 2) + ", ";
				command += parseInt(data, 4);
				return command;
			case 0x6A:
				command = "Move\t";
				command += parseUnsignedShort(data, 2) + ", ";
				command += parseInt(data, 4) + ", ";
				command += parseInt(data, 8) + ", ";
				return command;
			case 0x6B:
				command = "MoveTo\tgrid, ";
				command += parseUnsignedShort(data, 2) + ", ";
				command += data[4];
				return command;
			case 0x7A:
				command = "MoveTo\tdirect, ";
				command += parseUnsignedShort(data, 2) + ", ";
				command += data[4];
				return command;
			case 0x8B:
				command = "FaceDir\t";
				command += data[1] + ", ";
				command += DataDict.directions[data[2]];
				return command;
			case 0x91:
				command = "Rotate\t";
				command += (data[1]&0xFF) + ", ";
				if(data[2]==1)
					command += "cw, ";
				else if(data[2]==2)
					command += "ccw, ";
				command += DataDict.directions[data[4]];
				return command;
			case 0xCF:
				return "VaryMsg";
			case 0xD0:
				command = "Case"; 
				if(data[2]==1)
					command += "A\t";
				else if(data[2] == 3)
					command += "B\t";
                offset = parsePointer(data);
				command += offset;
				try {
                    command += "\n\t\t" + RomManipulator.readStringAndReturn(Integer.parseInt(offset, 16)).replace("\n", "\n\t\t");
                }
                catch (IOException e) {
                    command += "\tError reading string!";
                    e.printStackTrace();
                }
                return command;
			case 0xD1:
				command = "Default\t";
                offset = parsePointer(data);
				command += offset;
				try {
                    command += "\n\t\t" + RomManipulator.readStringAndReturn(Integer.parseInt(offset, 16)).replace("\n", "\n\t\t");
                }
                catch (IOException e) {
                    command += "\tError reading string!";
                    e.printStackTrace();
                }
                return command;
			case 0xD5:
				command = "Questn\t";
                offset = parsePointer(data);
				command += offset;
				try {
                    command += "\n\t\t" + RomManipulator.readStringAndReturn(Integer.parseInt(offset, 16)).replace("\n", "\n\t\t");
                }
                catch (IOException e) {
                    command += "\tError reading string!";
                    e.printStackTrace();
                }
				return command;
			case 0xD8:
				return "VryQstn";
			case 0xD9:
				command = "Option\t";
				command += data[2] + ", ";	
                offset = parsePointer(data);
				command += offset;
				try {
                    command += "\n\t\t" + RomManipulator.readStringAndReturn(Integer.parseInt(offset, 16)).replace("\n", "\n\t\t");
                }
                catch (IOException e) {
                    command += "\tError reading string!";
                    e.printStackTrace();
                }
				return command;
			case 0xDB:
				command = "Sleep\t";
				command += parseUnsignedShort(data, 2);
				return command;
			case 0xE2:
				command = "WaitSnd\t";
				command += parseUnsignedShort(data, 2);
				return command;
			case 0xE3:
				command = "WaitFlg\t";
				command += data[2];
				return command;
			case 0xE4:
				command = "SetFlag\t";
				command += data[2];
				return command;
			case 0xE7:
				command = "Loop\t";
				command += data[2];
				return command;
			case 0xE8:
				command = "Execute\t";
				command += DataDict.functions[parseUnsignedShort(data, 2)];
				return command;
			case 0xE9:
				return "MsgEnd";
			case 0xEE:
				return "Return";
			case 0xEF:
				return "End";
			case 0xF0:
				return "Close";
			case 0xF1:
				return "Remove";
			case 0xF4:
				command = "Label\t";
				command += data[2];
				return command;
			default:
				return bytesToString(data);
		}
	}
	
	public static String interpretCode(int offset) throws IOException {
		String output = "";
		byte[] data = new byte[16];
		RomManipulator.seek(offset);
		do {
			output += Integer.toHexString(RomManipulator.getFilePointer()) + "\t";
			RomManipulator.read(data);
			output += interpretCommand(data) + "\n";
			try{
				if(isTerminator(data[0]) && RomManipulator.peek()!=0xF4)
					break;
			}catch(IOException ioe) {
				ioe.printStackTrace();
				break;
			}
		}while(true);
		return output;
	}
	
	private static boolean isTerminator(byte in) {
		int command = ((int)in)&0xFF;
		return (command==0xE9 || (0xEE<=command && command<=0xF1));
	}
	
	public static String bytesToString(byte[] data) {
		String text = "";
		for(byte datum : data) {
			int temp = (int)(datum&0xFF);
			String str = Integer.toString(temp, 16);
			if (str.length()==1)
				str = "0" + str;
			text += str + " ";
		}
		return text.trim();
	}
	
	public static byte[] stringToBytes(String text) {
		String[] parts = text.split(" ");
		byte[] data = new byte[parts.length];
		for(int i=0; i<parts.length; i++) {
			//Temporarily cast as integer to avoid NumberFormatException
			data[i] = (byte)Integer.parseInt(parts[i], 16);
		}
		return data;
	}
	
	public static String interpretActorID(byte id) {
		if(id==0)
			return "player";
		else if(id==1)
			return "partner";
		else
			return "char" + Byte.toString(id);
	}
	
	public static int parseUnsignedShort(byte[] data, int offset) {
		int value = data[offset]&0xFF;
		value += (data[offset+1]&0xFF)<<8;
		return value;
	}
	
	public static short parseShort(byte[] data, int offset) {
		short value = (short)(data[offset]&0xFF);
		value += (data[offset+1]&0xFF)<<8;
		if((value&8000)>0)
			value += 0xFFFF0000;
		return value;
	}
	
	public static int parseUnsignedInt(byte[] data, int offset) {
		int value = data[offset]&0xFF;
		value += (data[offset+1]&0xFF)<<8;
		value += (data[offset+2]&0xFF)<<16;
		value += (data[offset+3]&0xFF)<<24;
		return value;
	}
	
	public static short parseInt(byte[] data, int offset) {
		short value = (short)(data[offset]&0xFF);
		value += (data[offset+1]&0xFF)<<8;
		value += (data[offset+2]&0xFF)<<16;
		value += (data[offset+3]&0xFF)<<24;
		return value;
	}
	
	public static String parsePointer(byte[] data) {
		int value = (int)(data[12]&0xFF);
		value += (int)(data[13]&0xFF)<<8;
		value += (int)(data[14]&0xFF)<<16;
		value += (int)(data[15]&0x1)<<24;
		return Integer.toHexString(value);
	}
}
