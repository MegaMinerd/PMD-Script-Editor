package com.mega.pmds.gui;

import java.awt.event.ActionEvent;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ScriptCodePanel extends ScriptContentPanel {
	JTextArea code;
	JScrollPane scrollPane;
	
	public ScriptCodePanel() {
		super();
		code = new JTextArea();
		code.setFont(new Font("monospaced", Font.PLAIN, 12));
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
