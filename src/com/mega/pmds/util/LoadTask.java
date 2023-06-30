package com.mega.pmds.util;

import java.io.IOException;
import java.util.PriorityQueue;

import javax.swing.tree.TreePath;

import com.mega.pmds.CodeConverter;
import com.mega.pmds.InvalidPointerException;
import com.mega.pmds.RomManipulator;
import com.mega.pmds.data.Actor;
import com.mega.pmds.data.DataDict;
import com.mega.pmds.gui.CodePanelInitializer;
import com.mega.pmds.gui.MapPanel;
import com.mega.pmds.gui.MapPanelInitializer;
import com.mega.pmds.gui.PmdScriptEditorWindow;
import com.mega.pmds.gui.ScriptTreeNode;
import com.mega.pmds.gui.WaypointMapPanel;

/**
 * Used to keep track of what needs to be loaded from a ROM
 */
public class LoadTask implements Comparable<LoadTask>{
	private final Type type;
	private final  ScriptTreeNode parent;
	private final int offset, size;
	
	public enum Type{
		AREA,
		WAYPOINT_LIST,
		SCENE_LIST,
		SCENE_DATA,
		ACTOR,
		SCRIPT,
		CAMERA_DATA,
		FOOTER_POINTER,
		MAIN_THREAD,
		INTERACTION
	}
	
	public LoadTask(Type typeIn, ScriptTreeNode parentIn, int sizeIn, int offsetIn) {
		this.type = typeIn;
		this.parent = parentIn;
		this.size = sizeIn;
		this.offset = offsetIn;
	}
	
	public void load(PriorityQueue<LoadTask> tasks) throws IOException, InvalidPointerException {
		if(this.type==Type.AREA) {
			String name = ConfigHandler.nameFromTypeAndOffset(type, offset);
			if(name.equals(""))
				name = "Unknown Area (0x" + Integer.toHexString(this.offset) + ")";
			else
				name += " (0x" + Integer.toHexString(offset) + ")";
			ScriptTreeNode node = new ScriptTreeNode(name, true);
			parent.add(node);
			RomManipulator.seek(offset);
			int size = RomManipulator.readInt();
			tasks.add(new LoadTask(Type.SCENE_LIST, node, size, RomManipulator.parsePointer()));
			int pointer = RomManipulator.parsePointer();
			tasks.add(new LoadTask(Type.WAYPOINT_LIST, node, (offset-pointer)/8, pointer));
			PmdScriptEditorWindow.addTreeAction(new TreePath(node.getPath()), new MapPanelInitializer<MapPanel>(node, -1, MapPanel.class));
		}else if(this.type==Type.WAYPOINT_LIST) {
			ScriptTreeNode node = new ScriptTreeNode("Waypoints" + " (0x" + Integer.toHexString(offset) + ")", true);
			parent.add(node);
			RomManipulator.seek(offset);
			for(int i=0; i<size; i++) {
				ScriptTreeNode waypoint = new ScriptTreeNode("Waypoint " + i + " (0x" + Integer.toHexString(RomManipulator.getFilePointer()) + ")", true);
				waypoint.add(new ScriptTreeNode("Location: (" + RomManipulator.readByte() + ", " + RomManipulator.readByte() + ")"));
				byte[] data = new byte[6];
				RomManipulator.read(data);
				waypoint.add(new ScriptTreeNode("Unknown data:" + CodeConverter.bytesToString(data)));
				node.add(waypoint);
				PmdScriptEditorWindow.addTreeAction(new TreePath(waypoint.getPath()), new MapPanelInitializer<WaypointMapPanel>(waypoint, i, WaypointMapPanel.class));
			}
			PmdScriptEditorWindow.addTreeAction(new TreePath(node.getPath()), new MapPanelInitializer<WaypointMapPanel>(node, -1, WaypointMapPanel.class));
		}else if(this.type==Type.SCENE_LIST) {
			ScriptTreeNode node = new ScriptTreeNode("Scenes (0x" + Integer.toHexString(offset) + ")", true);
			parent.add(node);
			RomManipulator.seek(offset);
			for(int i=0; i<size; i++) {		
				int nextSize = RomManipulator.readInt();
				int pointer;
				try{	
					pointer = RomManipulator.parsePointer();
				}catch(InvalidPointerException ipe){	
					ScriptTreeNode scene = new ScriptTreeNode("Null Scene", true);
					node.add(scene);
					continue;
				}	
				String name = ConfigHandler.nameFromTypeAndOffset(type, pointer);	
				if(name.equals(""))	
					name = "Unknown Scene (0x" + Integer.toHexString(pointer) + ")";
				else	
					name += " (0x" + Integer.toHexString(pointer) + ")";
				ScriptTreeNode scene = new ScriptTreeNode(name, true);	
				node.add(scene);	
				tasks.add(new LoadTask(Type.SCENE_DATA, scene, nextSize, pointer));	
			}		
		}else if(this.type==Type.SCENE_DATA) {
			RomManipulator.seek(offset);
			for(int i=0; i<size; i++) {
				try {
					String name = ConfigHandler.nameFromTypeAndOffset(type, RomManipulator.getFilePointer());
					if(name.equals(""))
						name = "Call " + i + " (0x" + Integer.toHexString(RomManipulator.getFilePointer()) + ")";
					else
						name += " (0x" + Integer.toHexString(RomManipulator.getFilePointer()) + ")";
					ScriptTreeNode node = new ScriptTreeNode(name, true);
					tasks.add(new LoadTask(Type.ACTOR, node, RomManipulator.readInt(), RomManipulator.parsePointer()));
					parent.add(node);
				}catch(InvalidPointerException ipe) {
				
				}
				try {
					tasks.add(new LoadTask(Type.INTERACTION, parent, RomManipulator.readInt(), RomManipulator.parsePointer()));
				}catch(InvalidPointerException ipe) {
				
				}
				try {
					tasks.add(new LoadTask(Type.CAMERA_DATA, parent, RomManipulator.readInt(), RomManipulator.parsePointer()));
				}catch(InvalidPointerException ipe) {
				
				}
				RomManipulator.skip(8);
				try {
					tasks.add(new LoadTask(Type.FOOTER_POINTER, parent, RomManipulator.readInt(), RomManipulator.parsePointer()));
				}catch(InvalidPointerException ipe) {
				}
			}
		}else if(this.type==Type.ACTOR) {
			RomManipulator.seek(offset);
			for(int i=0; i<size; i++) {
				ScriptTreeNode node;
				int id = RomManipulator.readByte()&0xFF;
				try {
					node = new ScriptTreeNode(Actor.fromID(id).toString(), true);
				}catch(NullPointerException npe) {
					node = new ScriptTreeNode("Actor" + Integer.toHexString(id), true);
				}
				node.add(new ScriptTreeNode("Direction: " + DataDict.directions[RomManipulator.readByte()]));
				RomManipulator.skip(2);
				node.add(new ScriptTreeNode("Location: (" + RomManipulator.readByte() + ", " + RomManipulator.readByte() + ")"));
				byte[] data = new byte[2];
				RomManipulator.read(data);
				node.add(new ScriptTreeNode("Unknown data: " + CodeConverter.bytesToString(data)));
				parent.add(node);
				try{
					tasks.add(new LoadTask(Type.SCRIPT, node, 1, RomManipulator.parsePointer()));
				}catch(InvalidPointerException ipe) {
					
				}
				try{
					int pointer = RomManipulator.parsePointer();
					ScriptTreeNode subNode = new ScriptTreeNode("Script 1");
					tasks.add(new LoadTask(Type.SCRIPT, subNode, 1, pointer));
					node.add(subNode);
				}catch(InvalidPointerException ipe) {
					
				}
				try{
					int pointer = RomManipulator.parsePointer();
					ScriptTreeNode subNode = new ScriptTreeNode("Interaction");
					tasks.add(new LoadTask(Type.SCRIPT, subNode, 1, pointer));
					node.add(subNode);
				}catch(InvalidPointerException ipe) {
					
				}
				try{
					int pointer = RomManipulator.parsePointer();
					ScriptTreeNode subNode = new ScriptTreeNode("Script 3");
					tasks.add(new LoadTask(Type.SCRIPT, subNode, 1, pointer));
					node.add(subNode);
				}catch(InvalidPointerException ipe) {
					
				}
			}
		}else if(this.type==Type.SCRIPT) {
			PmdScriptEditorWindow.addTreeAction(new TreePath(parent.getPath()), new CodePanelInitializer(offset));
		}else if(this.type==Type.CAMERA_DATA) {
			ScriptTreeNode node = new ScriptTreeNode("Camera (0x" + Integer.toHexString(offset) + ")", true);
			RomManipulator.seek(offset);
			byte[] data = new byte[4];
			RomManipulator.read(data);
			node.add(new ScriptTreeNode("Unknown data: " + CodeConverter.bytesToString(data)));
			node.add(new ScriptTreeNode("Location: (" + RomManipulator.readByte() + ", " + RomManipulator.readByte() + ")"));
			data = new byte[2];
			RomManipulator.read(data);
			node.add(new ScriptTreeNode("Unknown data: " + CodeConverter.bytesToString(data)));
			try {
				tasks.add(new LoadTask(Type.SCRIPT, node, 1, RomManipulator.parsePointer()));
			}catch(InvalidPointerException ipe) {
				
			}
			parent.add(node);
		}else if(this.type==Type.FOOTER_POINTER) {
			RomManipulator.seek(offset);
			tasks.add(new LoadTask(Type.MAIN_THREAD, parent, 1, RomManipulator.parsePointer()));
		}else if(this.type==Type.MAIN_THREAD) {
			RomManipulator.seek(offset);
			ScriptTreeNode node = new ScriptTreeNode("Main thread (0x" + Integer.toHexString(offset) + ")", true);
			parent.add(node);
			byte[] data = new byte[8];
			RomManipulator.read(data);
			node.add(new ScriptTreeNode("Unknown data: " + CodeConverter.bytesToString(data)));
			tasks.add(new LoadTask(Type.SCRIPT, node, 1, RomManipulator.parsePointer()));
		}else if(this.type==Type.INTERACTION) {
			RomManipulator.seek(offset);
			for(int i=0; i<size; i++) {
				String name = ConfigHandler.nameFromTypeAndOffset(type, offset);
				if(name.equals(""))
					name = "Object " + i + " (0x" + Integer.toHexString(offset) + ")";
				else
					name += " (0x" + Integer.toHexString(offset) + ")";
				ScriptTreeNode node = new ScriptTreeNode(name, true);
				node.add(new ScriptTreeNode("Size: (" + RomManipulator.readByte() + ", " + RomManipulator.readByte() + ")"));
				byte[] data = new byte[2];
				RomManipulator.read(data);
				node.add(new ScriptTreeNode("Unknown data: " + CodeConverter.bytesToString(data)));
				node.add(new ScriptTreeNode("Location: (" + RomManipulator.readByte() + ", " + RomManipulator.readByte() + ")"));
				RomManipulator.read(data);
				node.add(new ScriptTreeNode("Unknown data: " + CodeConverter.bytesToString(data)));
				for(int j=0; j<3; j++) {
					try {
						ScriptTreeNode scriptNode = new ScriptTreeNode("Script " + j);
						tasks.add(new LoadTask(Type.SCRIPT, scriptNode, 1, RomManipulator.parsePointer()));
						node.add(scriptNode);
					}catch(InvalidPointerException ipe) {
						
					}
				}
				RomManipulator.skip(4);
				parent.add(node);
			}
		}
	}
	
	protected int getOffset() {
		return offset;
	}

	@Override
	public int compareTo(LoadTask other) {
		return this.offset-other.getOffset();
	}
	
	public Type getType() {
		return type;
	}
}
