package com.mega.pmds.gui;

import java.util.concurrent.Callable;

import com.mega.pmds.data.Script;

public class CodePanelInitializer implements Callable<ScriptContentPanel>{
	private final int offset;
	
	public CodePanelInitializer(int offsetIn) {
		this.offset = offsetIn;
	}
	
	@Override
	public ScriptContentPanel call() {
		return new ScriptCodePanel(new Script(offset));
	}

}
