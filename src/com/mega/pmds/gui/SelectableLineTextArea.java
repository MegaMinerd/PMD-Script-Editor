package com.mega.pmds.gui;

import javax.swing.JTextArea;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.beans.*;

public class SelectableLineTextArea extends JTextArea {
    private int selectedLine = -1;
    private DefaultHighlighter highlighter;

    public SelectableLineTextArea() {
        super();
        highlighter = new DefaultHighlighter();
        highlighter.setDrawsLayeredHighlights(true);
        this.setHighlighter(highlighter);
        this.addMouseListener(new MouseInputAdapter() {
            public void mouseClicked(MouseEvent me) {
                Point p = getMousePosition();
                int newSelectedLine = (int)p.getY()/getRowHeight();
                if(p != null) {
                    setSelectedLine(newSelectedLine);
                }
            }
        });
    }

    public int getSelectedLine() {
        return selectedLine;
    }
    
    public void setSelectedLine(int line) {
        int oldSelectedLine = selectedLine;
        selectedLine = line;
        this.refreshHighlights();
        this.firePropertyChange("selectedLine",
            oldSelectedLine, line);
    }

    private void refreshHighlights() {
        if(0 <= selectedLine && selectedLine < getLineCount()) {
            try {
                highlighter.removeAllHighlights();
                highlighter.addHighlight(
                    getLineStartOffset(selectedLine),
                    getLineEndOffset(selectedLine), 
                    new DefaultHighlighter.DefaultHighlightPainter(new Color(230,230,230)));
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        } else {
            highlighter.removeAllHighlights();
        }
    }
}
