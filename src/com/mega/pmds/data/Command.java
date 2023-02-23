package com.mega.pmds.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.mega.pmds.CodeConverter;
import com.mega.pmds.RomManipulator;

public class Command {
	public static int COMMAND_LENGTH = 0x10;

    private long address;
    private byte[] commandData;
    private int commandId;
    private CommandType commandType;
    private int pointer;

    public Command(byte[] commandBytes, long address) throws Exception {
        this.commandData = commandBytes.clone();
        if(this.commandData.length != COMMAND_LENGTH) {
            throw new Exception("Invalid Command Size");
        }
        this.commandId = (int)this.commandData[0]&0xFF;
        this.commandType = CommandType.fromID((int)this.commandId&0xFF);
        this.address = address;
        if(this.hasStringPointer()) {
            String offset = CodeConverter.parsePointer(this.commandData);
            this.pointer = Integer.parseInt(offset, 16);
        }
    }

    public String interpretCommand() {
        StringBuilder sb = new StringBuilder();

        if(this.commandType != null) {
            sb.append(this.commandType.toString());
        } else {
            sb.append(String.format("%02x", this.commandId));
        }
        //TODO: Later we will want to consolidate all of the properties for each command instead of doing this
        switch(commandId) {
            case 0xD9:
            case 0xD5:
            case 0xD1:
            case 0xD0:
            case 0x35:
            case 0x34:
            case 0x33:
            case 0x32:
            case 0x3C:
                String offset = CodeConverter.parsePointer(this.commandData);
                try {
                    sb.append("\t" + RomManipulator.readStringAndReturn(Integer.parseInt(offset, 16)).replace("\n", "\\n"));
                } catch (IOException e) {
                    sb.append("\tError reading string!");
                    e.printStackTrace();
                }
                break;
            case 0xF4:
                sb.append("\t" + String.format("%02x",(int)this.commandData[2]&0xFF));
                break;
            case 0xE8:
                sb.append("\t");
                sb.append(DataDict.functions[CodeConverter.parseShort(this.commandData, 2)]);
        }
        return sb.toString();
    }

    public boolean hasStringPointer() {
        List<Integer> commandsWithPointers = Arrays.asList(0xD9, 0xD5, 0xD1, 0xD0, 0x35, 0x34, 0x33, 0x32, 0x3C);
        return commandsWithPointers.contains(this.commandId);
    }

    public int getPointer() {
        return this.pointer;
    }

    public byte[] getBytes() {
        return this.commandData;
    }

    public void setBytes(byte[] data) {
        this.commandData = data;
    }

    public void setBytes(byte[] data, int offset) {
        for(int i = offset; i < commandData.length; i++) {
            commandData[i] = data[i-offset];
        }
    }

    public void save() {
        try {
            RomManipulator.seek((int)address);
            for(byte myByte : commandData) {
                RomManipulator.writeByte(myByte);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public long getAddress() {
        return this.address;
    }

    @Override
    public String toString() {
        return CodeConverter.bytesToString(this.commandData);
    }
}
