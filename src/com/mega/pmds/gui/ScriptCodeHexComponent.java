package com.mega.pmds.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;

import com.mega.pmds.data.Script;

public class ScriptCodeHexComponent extends JTextArea implements UndoableEditListener {

    private boolean isInsertMode = false;
    private UndoManager undoManager;
    private AbstractAction undoAction, redoAction;

    public ScriptCodeHexComponent(Script script) {
        super();
        AbstractDocument abstractDoc = (AbstractDocument) this.getDocument();
        abstractDoc.setDocumentFilter(new HexFilter(script));

        undoManager = new UndoManager();
        undoManager.setLimit(100);
        abstractDoc.addUndoableEditListener(this);

        undoAction = new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(undoManager.canUndo()) {
                    //Hack to undo in overwrite mode since we delete then rewrite technically.
                    undoManager.undo();
                    undoManager.undo();
                }              
            }
        };

        redoAction = new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(undoManager.canRedo()) {
                    undoManager.redo();
                    undoManager.redo();
                }  
            }
        };

        // Map undo key to undo command.
        this.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        this.getActionMap().put("Undo",undoAction);

        this.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        this.getActionMap().put("Redo",redoAction);


        this.setText(script.commandsToString());
        // Do not count the initial load as an edit.
        undoManager.discardAllEdits();
    }

    @Override
    public void replaceSelection(String newText) {
        //Always select a character to implement overwrite mode
        if(!isInsertMode) {
            try{
                //Strip whitespace from input
                newText = newText.replaceAll("[ \n]+", "");

                //Start with caret at beginning.
                this.setCaretPosition(this.getSelectionStart());
                int offset = this.getSelectionStart();
                int newtextIndex = 0;
                int blankIndex = 0;
                StringBuilder combinedText = new StringBuilder();
                // Loop through replacable text and add spaces if necessary
                while(true) {
                    int textboxIndex = offset + blankIndex + newtextIndex;
                    // If we reach the end, move the caret as far as we can
                    if(textboxIndex >= this.getDocument().getLength() || newtextIndex >= newText.length()) {
                        this.moveCaretPosition(textboxIndex);
                        break;
                    }
                    //If not, add a character to the strings.
                    String curChar = this.getText(textboxIndex, 1);
                    if(curChar.isBlank()) {
                        combinedText.append(curChar);
                        blankIndex++;
                    } else {
                        combinedText.append(newText.charAt(newtextIndex));
                        newtextIndex++;
                    }
                }
                super.replaceSelection(combinedText.toString());
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent evt) {
        undoManager.addEdit(evt.getEdit());
    }
}
