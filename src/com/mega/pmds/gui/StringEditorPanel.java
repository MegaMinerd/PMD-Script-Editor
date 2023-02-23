package com.mega.pmds.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mega.pmds.RomManipulator;
import com.mega.pmds.data.Command;
import com.mega.pmds.data.PMDString;

public class StringEditorPanel extends JPanel{
    JTextArea stringTextArea;
    JButton saveButton;
    PMDString text;
    JLabel lengthLabel;
    JLabel lengthValueLabel;
    JLabel lengthRemainingLabel;
    JLabel lengthRemainingValueLabel;
    JLabel validationLabel;

    StringEditorPanel() {
        validationLabel = new JLabel("");
        validationLabel.setMinimumSize(new Dimension(400,20));
        this.add(validationLabel);

        JPanel detailsPanel = new JPanel();
        FlowLayout detailsLayout = new FlowLayout();
        detailsLayout.setAlignment(FlowLayout.LEADING);
        detailsPanel.setLayout(detailsLayout);

        lengthLabel = new JLabel("Max String Length (bytes): ");
        lengthValueLabel = new JLabel();
        
        detailsPanel.add(lengthLabel);
        detailsPanel.add(lengthValueLabel);
        
        detailsPanel.setPreferredSize(new Dimension(400,200));
        this.add(detailsPanel);

        // Textbox
        stringTextArea = new JTextArea();
        stringTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                handleTextChange();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleTextChange();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                handleTextChange();
            }
        });

        stringTextArea.setPreferredSize(new Dimension(400,200));
        stringTextArea.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.DARK_GRAY), "String"));
        this.add(stringTextArea);

        // Save button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener (){
            @Override
            public void actionPerformed(ActionEvent e) {
                byte[] bytes = text.getBytes();
                try {
                    RomManipulator.writeBytes(bytes, text.getOffset());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        this.add(saveButton);
        this.setEnabled(false);

        GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(detailsPanel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(validationLabel)
                    .addComponent(stringTextArea)
					.addComponent(saveButton, GroupLayout.Alignment.TRAILING))
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(validationLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(detailsPanel)
                    .addComponent(stringTextArea))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(saveButton))
		);

    }

    public void setSelectedString(Command selectedCommand) {
        // Check if command has pointer
        if(!selectedCommand.hasStringPointer()) {
            this.setEnabled(false);
            return;
        }
        this.setEnabled(true);

        // Grab string from pointer.
        int pointer = selectedCommand.getPointer();
        byte[] textBytes = RomManipulator.readBytesUntilNull(pointer);
        // Create PMDString
        text = new PMDString(textBytes, pointer);
        // Set textbox to PMDString
        stringTextArea.setText(text.toString());

        lengthValueLabel.setText(Integer.toHexString(text.getLength()));
    }

    private void handleTextChange() {
        if(!text.setFromString(stringTextArea.getText())) {
            setValidationError("Your string has too many characters.");
        } else {
            setValidationError("");
        }
    }


    public void setEnabled(boolean enabled) {
        stringTextArea.setEnabled(enabled);
        saveButton.setEnabled(enabled);
    } 



    private void setValidationError(String message) {
        if(message == null || message.isEmpty()) {
            this.validationLabel.setText("");
            validationLabel.setIcon(null);
            saveButton.setEnabled(true);
        } else {
            this.validationLabel.setText(message);
            ImageIcon errorIconOrig = (ImageIcon) UIManager.getIcon("OptionPane.errorIcon");
            ImageIcon errorIconScaled = new ImageIcon(errorIconOrig.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
            validationLabel.setIcon(errorIconScaled);
            saveButton.setEnabled(false);
        }

    }
}
