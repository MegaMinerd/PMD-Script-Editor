package com.mega.pmds.data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * This class is for whatever strings you pull out of the ROM with the same encoding as the dialog.
 */
public class PMDString {
    private String _escapedString;
    private int _initialLength;
    private int _currentLength;
    private long _offset;

    /**
     * This constructor gets the initial length of the string in bytes and sets it.
     * @param stringBytes
     */
    public PMDString(byte[] stringBytes) {
        setFromByteArray(stringBytes);
        _initialLength = stringBytes.length;
        _currentLength = _initialLength;
    }

    public PMDString(byte[] stringBytes, long offset) {
        this(stringBytes);
        _offset = offset;
    }

    /**
     * Sets from a string and checks if length matches up.
     * @param newStr
     * @return If the length is too large returns false.
     */
    public boolean setFromString(String newStr) {
        _escapedString = newStr;
        _currentLength = getBytes().length;
        if(_currentLength > _initialLength) {
            return false;
        } else {
            return true;
        }
    }

    public void setFromByteArray(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bytes.length; i++) {
            char curChar = (char) (bytes[i]&0xFF);
            switch(curChar) {
                case 0x00:
                    break;
                case 0xA:
                case 0xD:
                case 0x1D:
                    sb.append(curChar);
                    break;
                //Two byte characters.
                case 0x81:
                case 0x82:
                case 0x87:
                    sb.append("\\x" + String.format("%02X", (int)curChar));
                    i++;
                    if(i < bytes.length) {
                        curChar = (char) (bytes[i]&0xFF);
                        sb.append("\\x" + String.format("%02X", (int)curChar));
                    }
                    break;
                default:
                    sb.append(curChar);
                    break;
            }
            if(curChar == 0x0) {
                break;
            }
        }
        _currentLength = bytes.length;
        _escapedString = sb.toString();
    }

    public byte[] getBytes() {
        ArrayList<Byte> output = new ArrayList<Byte>();
        for(int i = 0; i < _escapedString.length(); i++) {
            char curChar = _escapedString.charAt(i);
            if(curChar == '\\' && i+3 < _escapedString.length() && _escapedString.charAt(i+1) == 'x' 
            && Character.digit(_escapedString.charAt(i+2), 16) != -1
            && Character.digit(_escapedString.charAt(i+3), 16) != -1) {
                int charNum = Integer.parseInt(_escapedString.substring(i+2,i+4),16);
                byte myByte = (byte) charNum;
                output.add(myByte);
                i+=3;
            } else {
                output.add((byte) curChar);
            }
        }
        byte[] primitiveOutput = new byte[output.size()];
        for(int i = 0; i < primitiveOutput.length; i++) {
            primitiveOutput[i] = output.get(i);
        }
        return primitiveOutput;
    }

    public int getLength() {
        return _initialLength;
    }

    public void resetLength() {
        _initialLength = _currentLength;
    }

    public String toString() {
        return _escapedString;
    }

    public long getOffset() {
        return _offset;
    }
}
