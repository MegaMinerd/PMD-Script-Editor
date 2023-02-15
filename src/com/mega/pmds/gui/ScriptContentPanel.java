package com.mega.pmds.gui;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

public abstract class ScriptContentPanel extends JPanel implements ActionListener{
    private boolean _shouldScroll;
    
    public ScriptContentPanel() {
        _shouldScroll = false;
    }

    public ScriptContentPanel(boolean shouldScroll) {
        _shouldScroll = shouldScroll;
    }

    public boolean getShouldScroll() {
        return _shouldScroll;
    }
}
