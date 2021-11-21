package com.mega.pmds.gui;

import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import com.mega.pmds.data.Script;

public class ScriptCodeHexComponent extends JTextArea{

    private boolean isInsertMode = false;

    public ScriptCodeHexComponent(Script script) {
        super();
        AbstractDocument abstractDoc = (AbstractDocument) this.getDocument();
        abstractDoc.setDocumentFilter(new HexFilter(script));
        this.setText(script.commandsToString());
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
}
