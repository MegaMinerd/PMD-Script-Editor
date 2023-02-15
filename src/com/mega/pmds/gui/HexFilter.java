package com.mega.pmds.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.mega.pmds.data.Script;

import java.awt.Toolkit;

public class HexFilter extends DocumentFilter {

    private Script scriptModel;

    public HexFilter(Script script) {
        this.scriptModel = script;
    }

    public void insertString(FilterBypass fb, int offs,
                             String str, AttributeSet a) throws BadLocationException{
        if (str.matches("[0-9a-fA-F \n]+")) {
            super.insertString(fb, offs, str.toUpperCase(), a);
        }
        else {
            System.out.println("INVALID1");
            Toolkit.getDefaultToolkit().beep();
        }
    }
     
    public void replace(FilterBypass fb, int offs,
                        int length, 
                        String str, AttributeSet a)
        throws BadLocationException {
        if (str.matches("[0-9a-fA-F \n]+")) {
            super.replace(fb, offs, length, str.toUpperCase(), a);
        }
        else {
            System.out.println("INVALID2");
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void remove(FilterBypass fb, int offs, int length) {
        Toolkit.getDefaultToolkit().beep();
    }
}
