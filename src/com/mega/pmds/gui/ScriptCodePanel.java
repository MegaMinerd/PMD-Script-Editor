package com.mega.pmds.gui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.GridLayout;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import com.mega.pmds.RomManipulator;
import com.mega.pmds.data.Command;
import com.mega.pmds.data.Script;

public class ScriptCodePanel extends ScriptContentPanel {
	JTextArea addresses;
	ScriptCodeHexComponent hexComponent;
	ScriptCodeTextComponent codeTextComponent;
	StringEditorPanel stringEditorPanel;
	Script currentScript;

	private int selectedLine = -1;

	// Could use an object containing script data instead of text.
	public ScriptCodePanel(Script text) {
		super(false);
		currentScript = text;
		JPanel topPanel = initTopPanel(text);
		JScrollPane topScrollPane = new JScrollPane(topPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel bottomPane = initBottomPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, topScrollPane, bottomPane);
		splitPane.setResizeWeight(0.9);
		this.add(splitPane);
		this.setLayout(new GridLayout(0,1));
	}

	public JPanel initTopPanel(Script text) {
		JPanel topPanel = new JPanel();
		Font monospaced = new Font("monospaced", Font.PLAIN, 12);

		// Header Labels
		JLabel addressLabel = new JLabel("Offset");
		JLabel codeLabel = new JLabel("Commands");

		// Script Addresses
		addresses = new JTextArea();
		addresses.setEditable(false);
		addresses.setFont(monospaced);
		addresses.setForeground(Color.BLUE);
		addresses.setBackground(this.getBackground());
		addresses.setText(text.addressesToString());
		topPanel.add(addresses);

		hexComponent = new ScriptCodeHexComponent(text);
		hexComponent.setFont(monospaced);
		hexComponent.addPropertyChangeListener("selectedLine", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setSelectedLine(hexComponent.getSelectedLine());					
			}
		});
		topPanel.add(hexComponent);


		
		codeTextComponent = new ScriptCodeTextComponent(text);
		codeTextComponent.setFont(monospaced);
		codeTextComponent.addPropertyChangeListener("selectedLine", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setSelectedLine(codeTextComponent.getSelectedLine());					
			}
		});
		topPanel.add(codeTextComponent);

		JButton saveButton = new JButton(new AbstractAction("Save") {

			@Override
			public void actionPerformed(ActionEvent e) {
				text.setFromText(hexComponent.getText());
				text.saveCommands();				
			}
		});
		topPanel.add(saveButton);

		//Layout
		GroupLayout layout = new GroupLayout(topPanel);
		topPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(addressLabel)
					.addComponent(addresses,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(codeLabel)
					.addComponent(hexComponent,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(codeTextComponent)
					.addComponent(saveButton))
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(addressLabel)
					.addComponent(codeLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(addresses)
					.addComponent(hexComponent)
					.addComponent(codeTextComponent))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(saveButton))
		);

		return topPanel;
	}
	
	public JPanel initBottomPanel() {
		stringEditorPanel = new StringEditorPanel();
		return stringEditorPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	private void setSelectedLine(int line) {
		hexComponent.setSelectedLine(line);
		codeTextComponent.setSelectedLine(line);
		Command selectedCommand = currentScript.getCommandAtIndex(line);
		if(selectedCommand != null) {
			stringEditorPanel.setSelectedString(selectedCommand);
		}
		this.selectedLine = line;
	}

}
