package com.mega.pmds.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public class MapPanelInitializer<T extends MapPanel> implements Callable<ScriptContentPanel>{
	private final ScriptTreeNode node;
	private Class<T> type;
	private int id;
	
	public MapPanelInitializer(ScriptTreeNode nodeIn, int idIn, Class<T> typeIn) {
		this.node = nodeIn;
		this.type = typeIn;
		this.id = idIn;
	}
	
	@Override
	public ScriptContentPanel call() {
		try {
			return (ScriptContentPanel)type.getDeclaredConstructor((new ScriptTreeNode(null)).getClass(), (Integer.valueOf(0)).getClass()).newInstance(node, Integer.valueOf(id));
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

