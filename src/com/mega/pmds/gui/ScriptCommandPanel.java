package com.mega.pmds.gui;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.mega.pmds.RomManipulator;
import com.mega.pmds.data.Command;

public class ScriptCommandPanel extends JPanel {
    private Command command;
    private boolean isEnabled;
    private JTextArea jta;

    public ScriptCommandPanel() {
        jta = new JTextArea();
        jta.setEnabled(false);
        this.add(jta);
    }
    
    public void setCommand(Command c) {
        this.command = c;
        updateFields();
    }

    public void updateFields() {
        if(this.command.hasStringPointer()){
            try {
                String result = RomManipulator.readString(this.command.getPointer());
                jta.setText(result);
            } catch (Exception e) {
                //TODO: handle exception
            }
        } else {
            jta.setEnabled(false);
            jta.setText("");
        }
    }
    
}
