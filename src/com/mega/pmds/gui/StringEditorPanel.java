package com.mega.pmds.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
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
    JLabel validationLabel;

    StringEditorPanel() {
        super(new BorderLayout());

        validationLabel = new JLabel("");
        this.add(validationLabel, BorderLayout.NORTH);
        this.setPreferredSize(new Dimension(400,20));


        JPanel detailsPanel = new JPanel();
        lengthLabel = new JLabel("String Length (bytes): ");
        this.add(lengthLabel);

        lengthValueLabel = new JLabel();
        this.add(lengthValueLabel);

        detailsPanel.add(lengthLabel);
        detailsPanel.add(lengthValueLabel);
        this.add(detailsPanel, BorderLayout.WEST);
        detailsPanel.setPreferredSize(new Dimension(400,200));

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

        this.add(stringTextArea, BorderLayout.CENTER);
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
        this.add(saveButton, BorderLayout.SOUTH);
        this.setEnabled(false);
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
            saveButton.setEnabled(false);
        } else {
            setValidationError("");
            saveButton.setEnabled(true);
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
        } else {
            this.validationLabel.setText(message);
            validationLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        }

    }
}
