package com.mega.pmds.gui;

import javax.swing.JTextArea;

import com.mega.pmds.data.Command;
import com.mega.pmds.data.Script;

public class ScriptCodeTextComponent extends JTextArea {
    public ScriptCodeTextComponent(Script script) {
        this.setEditable(false);
        this.setLineWrap(false);
        StringBuilder sb = new StringBuilder();
        for(Command c : script.getCommands().values()) {
            sb.append(c.interpretCommand() + "\n");
        }
        this.setText(sb.toString());
    }
}
