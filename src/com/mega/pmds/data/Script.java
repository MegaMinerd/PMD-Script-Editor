package com.mega.pmds.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.mega.pmds.CodeConverter;
import com.mega.pmds.RomManipulator;

public class Script {

    private static final int MAX_COMMANDS_SEARCHED = 600;

    private int script_start_offset;

    private TreeMap<Integer,Command> commands;

    public Script(int offset) {
        this.script_start_offset = offset;
        try {
            TreeMap<Integer,Command> output = new TreeMap<Integer,Command>();
            commands = interpretCode(offset, output);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public Script(TreeMap<Integer,Command> commands) {
        this.commands = commands;
    }

    // Base
    public TreeMap<Integer,Command> interpretCode(int offset, TreeMap<Integer, Command> output) throws IOException {
		byte[] data = new byte[Command.COMMAND_LENGTH];
		RomManipulator.seek(offset);
		do {
			int curCommandOffset = RomManipulator.getFilePointer();
			RomManipulator.read(data);
            try{
                Command c = new Command(data, curCommandOffset);
                // If we've already seen this command...
                if(output.containsKey(curCommandOffset)) {
                    break;
                }

			    output.put(Integer.valueOf(curCommandOffset), c);
                // Find label that corresponds with jump.
                if(isJump(data[0])) {
                    //Find new label
                    int newLabelOffset = findNewLabel(curCommandOffset);
                    interpretCode(newLabelOffset, output);
                    //Return current offset
                    RomManipulator.seek(curCommandOffset);
                    RomManipulator.read(new byte[Command.COMMAND_LENGTH]);
                }

                // Check if end of function
                // It's important to do this after finding label jumps because a terminator can also be a jump
                // Ex: E7 - Goto
                if(isTerminator(data[0])) {
                    break;
                }
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

    // Sets RomManipulator current address to the label.
    private int findNewLabel(int curCommandOffset) throws IOException {
        int numCommandsSearched = 0;
        byte[] data = new byte[Command.COMMAND_LENGTH];

        RomManipulator.seek(curCommandOffset);
        RomManipulator.read(data);
        int labelToSearch = getJumpLabel(data);
        RomManipulator.seek(this.script_start_offset);
        while(numCommandsSearched < MAX_COMMANDS_SEARCHED) {
            RomManipulator.read(data);
            // Check if label
            if((((int)data[0])&0xFF) == 0xF4) {
                if((((int)data[2])&0xFF) == labelToSearch) {
                    return RomManipulator.getFilePointer();
                }
            }
            numCommandsSearched++;
        }
        System.err.println("UNREACHABLE LABEL from jump at address " + Integer.toHexString(curCommandOffset));
        System.err.println("Searching for " + Integer.toHexString(labelToSearch));
        return 0;
    }

    private static boolean isTerminator(byte in) {
		int command = ((int)in)&0xFF;
		return (command==0xE7 || command==0xE9 || command==0xEB || (0xEE<=command && command<=0xF1));
	}

    private static boolean isJump(byte in) {
		int command = ((int)in)&0xFF;
		return ((0xCC<=command && command<=0xCE) || (0xB3<=command && command<=0xBF) || command==0x3A || command==0xE6 || command==0xE7);
	}

    private static int getJumpLabel(byte[] data) {
        int label = 0;
        switch(((int)data[0])&0xFF) {
            case 0xB3:
            case 0xB8:
            case 0xB9:
            case 0xBA:
            case 0xBB:
            case 0xBC:
            case 0xBD:
            case 0xBE:
            case 0xBF:
                label = ((int)data[1])&0xFF;
                break;
            case 0x3A:
            case 0xB4:
            case 0xB5:
            case 0xB6:
            case 0xB7:
            case 0xCC:
            case 0xCD:
            case 0xCE:
            case 0xE6:
            case 0xE7:
                label = CodeConverter.parseUnsignedShort(data, 2);
                break;
            default:
                label = 0;
                System.err.println("Bad command for getJumpLabel " + Integer.toHexString(((int)data[0])&0xFF));
        }

        return label;
    }

    public TreeMap<Integer,Command> getCommands() {
        return commands;
    }

    public void setCommands(TreeMap<Integer,Command> commands) {
        this.commands = commands;
    }

    /**
     * Saves the current contents of the script to the opened ROM file.
     */
    public void saveCommands() {
        for(Command c : commands.values()) {
            c.save();
        }
    }

    /**
     * Sets the contents of the Script from a newline delimited list of commands.
     * @param text The text to set the script from. Should be hex bytes and can have spaces. Commands are delimited by newlines.
     */
    public void setFromText(String text) {
        String[] lines = text.split("\n");
        if(commands.size() != lines.length) {
            System.err.println("The text has too many commands for the opened script.");
            return;
        }
        // Extract data from text
        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            byte[] data = CodeConverter.stringToBytes(line);
            commands.get(i).setBytes(data);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Command command : this.commands.values()) {
            sb.append(Long.toHexString(command.getAddress()) + "\t" + command.toString() + "\n");
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    public String addressesToString() {
        StringBuilder sb = new StringBuilder();
        for (Command command : this.commands.values()) {
            sb.append(String.format("0x%08x",command.getAddress()) + "\n");
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    public String commandsToString() {
        StringBuilder sb = new StringBuilder();
        for (Command command : this.commands.values()) {
            sb.append(command.toString() + "\n");
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }
}
