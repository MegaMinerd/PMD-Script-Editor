package com.mega.pmds.gui;

import java.awt.event.ActionEvent;
import java.security.acl.Group;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mega.pmds.data.Script;

public class ScriptCodePanel extends ScriptContentPanel {
	JTextArea addresses;
	ScriptCodeHexComponent code;
	JScrollPane scrollPane;
	
	// Could use an object containing script data instead of text.
	public ScriptCodePanel(Script text) {
		super();
		Font monospaced = new Font("monospaced", Font.PLAIN, 12);

		// Header Labels
		JLabel addressLabel = new JLabel("Offset");
		JLabel codeLabel = new JLabel("Commands");

		// Script Addresses
		addresses = new JTextArea();
		addresses.setEditable(false);
		addresses.setFont(monospaced);
		addresses.setText(text.addressesToString());
		this.add(addresses);

		// Script Commands
		code = new ScriptCodeHexComponent(text);
		code.setFont(monospaced);
		this.add(code);

		//Layout
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(addressLabel)
					.addComponent(addresses,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(codeLabel)
					.addComponent(code,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE))
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(addressLabel)
					.addComponent(codeLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(addresses)
					.addComponent(code))
		);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

}
