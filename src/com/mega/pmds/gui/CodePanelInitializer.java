package com.mega.pmds.gui;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.mega.pmds.CodeConverter;

public class CodePanelInitializer implements Callable<ScriptContentPanel>{
	private final int offset;
	
	public CodePanelInitializer(int offsetIn) {
		this.offset = offsetIn;
	}
	
	@Override
	public ScriptContentPanel call() {
		try {
			return new ScriptCodePanel(CodeConverter.interpretCode(offset));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
