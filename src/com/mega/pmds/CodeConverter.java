package com.mega.pmds;

import java.io.IOException;

import com.mega.pmds.data.Direction;
import com.mega.pmds.data.Face;
import com.mega.pmds.data.Function;
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
			case 0x2E:
				String command = "Kaomado\t";
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
				command += parsePointer(data);
				return command;
			case 0x33:
				command = "Message\tthought, ";
				command += interpretActorID(data[2]) + ", ";
				command += parsePointer(data);
				return command;
			case 0x34:
				command = "Message\tspeech, ";
				command += interpretActorID(data[2]) + ", ";
				command += parsePointer(data);
				return command;
			case 0x3D:
				command = "Rename\t";
				command += interpretActorID((byte)(data[4]-1));
				return command;
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
			case 0x52:
				command = "Hide\t0x";
				command += Integer.toHexString(parseUnsignedInt(data, 4));
				return command;
			case 0x53:
				command = "Show\t0x";
				command += Integer.toHexString(parseUnsignedInt(data, 4));
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
			case 0x91:
				command = "Rotate\t";
				command += (data[1]&0xFF) + ", ";
				if(data[2]==1)
					command += "cw, ";
				else if(data[2]==2)
					command += "ccw, ";
				command += Direction.fromID(data[4]).toString().toLowerCase();
				return command;
			case 0xCF:
				return "VaryMsg";
			case 0xD0:
				command = "Case"; 
				if(data[2]==1)
					command += "A";
				else if(data[2] == 3)
					command += "B";
				command += "\t" + parsePointer(data);
				return command;
			case 0xD1:
				command = "Default\t";
				command += parsePointer(data);
				return command;
			case 0xD5:
				command = "Questn\t";
				command += parsePointer(data);
				return command;
			case 0xD8:
				return "VryQstn";
			case 0xD9:
				command = "Option\t";
				command += data[2] + ", ";	
				command += parsePointer(data);
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
				command += Function.fromID(parseUnsignedShort(data, 2));
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
		}while(!output.endsWith("Close\n") && !output.endsWith("End\n"));
		return output;
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
	
	public static int parseUnsignedInt(byte[] data, int offset) {
		int value = data[offset]&0xFF;
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
