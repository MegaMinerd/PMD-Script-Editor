package com.mega.pmds.gui;

import java.io.IOException;
import java.util.concurrent.Callable;

public class MapPanelInitializer implements Callable<ScriptContentPanel>{
	private final ScriptTreeNode node;
	
	public MapPanelInitializer(ScriptTreeNode nodeIn) {
		this.node = nodeIn;
	}
	
	@Override
	public ScriptContentPanel call() {
		return new ScriptMapPanel(node);
	}
}

