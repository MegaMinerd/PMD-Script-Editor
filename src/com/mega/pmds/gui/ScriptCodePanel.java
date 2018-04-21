package com.mega.pmds.gui;

import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ScriptCodePanel extends ScriptContentPanel {
	JTextArea code;
	JScrollPane scrollPane;
	
	public ScriptCodePanel() {
		super();
		code = new JTextArea();
		this.add(code);
	}
	
	public ScriptCodePanel(String text) {
		this();
		code.setText(text);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

}
