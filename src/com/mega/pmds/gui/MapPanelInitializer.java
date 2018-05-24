package com.mega.pmds.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public class MapPanelInitializer<T extends MapPanel> implements Callable<ScriptContentPanel>{
	private final ScriptTreeNode node;
	private Class<T> type;
	
	public MapPanelInitializer(ScriptTreeNode nodeIn, Class<T> typeIn) {
		this.node = nodeIn;
		this.type = typeIn;
	}
	
	@Override
	public ScriptContentPanel call() {
		try {
			return (ScriptContentPanel)type.getDeclaredConstructor((new ScriptTreeNode(null)).getClass()).newInstance(node);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
}

