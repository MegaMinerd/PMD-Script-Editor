package com.mega.pmds.data;

import java.io.IOException;
import java.util.ArrayList;

import com.mega.pmds.RomManipulator;

public class Script {
    private ArrayList<Command> commands;

    public Script(int offset) {
        try {
            commands = interpretCode(offset);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public Script(ArrayList<Command> commands) {
        this.commands = commands;
    }

    public static ArrayList<Command> interpretCode(int offset) throws IOException {
		ArrayList<Command> output = new ArrayList<Command>();

		byte[] data = new byte[16];
		RomManipulator.seek(offset);
		do {
			long curCommandOffset = RomManipulator.getFilePointer();
			RomManipulator.read(data);
            try{
                Command c = new Command(data, curCommandOffset);
			    output.add(c);
				if(isTerminator(data[0]) && RomManipulator.peek()!=0xF4)
					break;
			}catch(IOException ioe) {
				ioe.printStackTrace();
				break;
			} catch(Exception e) {
                e.printStackTrace();
                break;
            }
		}while(true);
		return output;
	}

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void setCommands(ArrayList<Command> commands) {
        this.commands = commands;
    }

    public void saveCommands() {
        // TODO: Implement saving
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Command command : this.commands) {
            sb.append(Long.toHexString(command.getAddress()) + "\t" + command.toString() + "\n");
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    public String addressesToString() {
        StringBuilder sb = new StringBuilder();
        for (Command command : this.commands) {
            sb.append(String.format("0x%x",command.getAddress()) + "\n");
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    public String commandsToString() {
        StringBuilder sb = new StringBuilder();
        for (Command command : this.commands) {
            sb.append(command.toString() + "\n");
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    private static boolean isTerminator(byte in) {
		int command = ((int)in)&0xFF;
		return (command==0xE7 || command==0xE9 || (0xEE<=command && command<=0xF1));
	}
}
