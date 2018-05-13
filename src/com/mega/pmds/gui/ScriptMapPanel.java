package com.mega.pmds.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mega.pmds.util.ConfigHandler;
import com.mega.pmds.util.ImageExtractor;

public class ScriptMapPanel extends ScriptContentPanel{
	ScriptTreeNode node;
	private BufferedImage map;
	
	public ScriptMapPanel(ScriptTreeNode nodeIn) {
		this.node=nodeIn;
		String name = ((ScriptTreeNode)node.getParent()).getName().split("\\(")[0].trim().toLowerCase().replaceAll(" ","_").replaceAll("\\.", "");
		File file = new File("assets/" + name + ".png");
		if(file.exists()) {
			try {
				map = ImageIO.read(file);
			}catch (IOException e) {
				map = null;
			}
		}else {
			name = ((ScriptTreeNode)node.getParent()).getName();
			map = ImageExtractor.extract(ConfigHandler.getMapDefPointers(Integer.parseInt(name.substring(name.indexOf("(")+3, name.indexOf(")")), 16)));
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(map!=null) {
        	g.drawImage(map, 0, 0, this);
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
        }
    }
}
