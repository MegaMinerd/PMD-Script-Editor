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
    public void replaceSelection(String text) {
        if(!isInsertMode) {
            //Always select a character to implement overwrite mode
            if(!isInsertMode) {
              //Skip over whitespace after you add a character and the carat goes to the next line.
              int pos = this.getSelectionStart();
              try {
                  if(this.getText(pos, 1).isBlank() && pos < getDocument().getLength()){
                      this.setCaretPosition(pos + 1);
                  }
              } catch (BadLocationException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }
                //Select 1 character to overwrite
                pos = this.getSelectionStart();
                if(pos < getDocument().getLength()) {
                    this.setCaretPosition(this.getSelectionStart());
                    this.moveCaretPosition(this.getSelectionStart()+1);
                }

                //Do not add any characters to the end.
                if(this.getSelectionStart() < this.getDocument().getLength()){
                    super.replaceSelection(text);
                }
            }
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent evt) {
        undoManager.addEdit(evt.getEdit());
    }
}
