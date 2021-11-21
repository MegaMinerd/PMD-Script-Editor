package com.mega.pmds.gui;

import java.awt.event.ActionEvent;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mega.pmds.data.Script;

public class ScriptCodePanel extends ScriptContentPanel {
	JTextArea offsets;
	ScriptCodeHexComponent code;
	JScrollPane scrollPane;
	
	// Could use an object containing script data instead of text.
	public ScriptCodePanel(Script text) {
		super();
		Font monospaced = new Font("monospaced", Font.PLAIN, 12);
		offsets = new JTextArea();
		offsets.setFont(monospaced);
		code = new ScriptCodeHexComponent(text);
		code.setFont(monospaced);
		this.add(offsets);
		this.add(code);
		offsets.setText(text.addressesToString());
		System.out.println(code.getText());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

}
