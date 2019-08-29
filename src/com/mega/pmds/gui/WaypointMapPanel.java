package com.mega.pmds.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class WaypointMapPanel extends MapPanel {
	private int id;

	public WaypointMapPanel(ScriptTreeNode nodeIn, Integer idIn) {
		super(idIn<0 ? (ScriptTreeNode)nodeIn.getParent() : (ScriptTreeNode)nodeIn.getParent().getParent(), idIn);
		this.node = nodeIn;
		this.id = idIn;
	}
	
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	if(map!=null) {
    		if(id<0) {
    			for(int i=0; i<node.getChildCount(); i++) {
    				ScriptTreeNode child = (ScriptTreeNode)node.getChildAt(i);
    				String waypoint = ((ScriptTreeNode)child.getChildAt(0)).getName();
    				String loc = waypoint.substring(waypoint.indexOf("(")+1, waypoint.indexOf(")"));
    				int x = Integer.parseInt(loc.split(",")[0])*8;
    				int y = Integer.parseInt(loc.split(",")[1].trim())*8;
    				g.setColor(Color.WHITE);
    				g.fillOval(x-4, y-4, 8, 8);
    				g.setFont(new Font("default", Font.BOLD, 16));
    				g.drawString(Integer.toHexString(node.getIndex(child)), x+6, y+4);
    				g.setColor(Color.BLACK);
    				g.drawLine(x-2, y, x+2, y);
    				g.drawLine(x, y-2, x, y+2);
    				g.setFont(new Font("default", Font.PLAIN, 16));
    				g.drawString(Integer.toHexString(node.getIndex(child)), x+6, y+4);	
    			}
    		}else {
    			ScriptTreeNode child = (ScriptTreeNode)node.getChildAt(0);
    			String waypoint = child.getName();
    			String loc = waypoint.substring(waypoint.indexOf("(")+1, waypoint.indexOf(")"));
    			int x = Integer.parseInt(loc.split(",")[0])*8;
    			int y = Integer.parseInt(loc.split(",")[1].trim())*8;
    			g.setColor(Color.WHITE);
    			g.fillOval(x-4, y-4, 8, 8);
    			g.setFont(new Font("default", Font.BOLD, 16));
    			g.drawString(Integer.toHexString(id), x+6, y+4);
    			g.setColor(Color.BLACK);
    			g.drawLine(x-2, y, x+2, y);
    			g.drawLine(x, y-2, x, y+2);
    			g.setFont(new Font("default", Font.PLAIN, 16));
    			g.drawString(Integer.toHexString(id), x+6, y+4);	
    		}
    	}
    }
}
