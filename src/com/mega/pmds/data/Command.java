package com.mega.pmds.data;

import java.io.IOException;

import com.mega.pmds.CodeConverter;
import com.mega.pmds.RomManipulator;

public class Command {
	public static int COMMAND_LENGTH = 0x10;

    private long address;
    private byte[] commandData;
    private byte commandId;
    private byte[] pointer;

    public Command(byte[] commandBytes, long address) throws Exception {
        this.commandData = commandBytes.clone();
        if(this.commandData.length != COMMAND_LENGTH) {
            throw new Exception("Invalid Command Size");
        }
        commandId = this.commandData[0];
        this.address = address;
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
