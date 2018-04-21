package com.mega.pmds.gui;

import javax.swing.tree.DefaultMutableTreeNode;

public class ScriptTreeNode extends DefaultMutableTreeNode{
	private String name;
	
	public ScriptTreeNode(String text) {
		super(text);
		name=text;
	}
	
	public ScriptTreeNode(String text, boolean b) {
		super(text, b);
		name=text;
	}
	
	public String getName() {
		return this.isRoot() ? "root" : name;
	}
	
	public String getPathName() {
		return this.isRoot() ? "root" : (((ScriptTreeNode)parent).getPath() + "/" + name);
	}
}
