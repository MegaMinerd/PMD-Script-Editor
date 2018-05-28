package com.mega.pmds.gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mega.pmds.util.ConfigHandler;
import com.mega.pmds.util.ImageCache;
import com.mega.pmds.util.ImageExtractor;

public class MapPanel extends ScriptContentPanel{
	ScriptTreeNode node;
	protected BufferedImage map;
	
	public MapPanel(ScriptTreeNode nodeIn) {
		this.node=nodeIn;
		String name = ((ScriptTreeNode)node).getName().split("\\(")[0].trim().toLowerCase().replaceAll(" ","_").replaceAll("\\.", "");
		if(ImageCache.isCached(name)) {
			map = ImageCache.getImage(name);
		}else {
			File file = new File("assets/" + name + ".png");
			if(file.exists()) {
				try {
					map = ImageIO.read(file);
				}catch (IOException e) {
					map = null;
				}
			}else {
				String nodeName = ((ScriptTreeNode)node).getName();
				map = ImageExtractor.extract(ConfigHandler.getMapDefPointers(Integer.parseInt(nodeName.substring(nodeName.lastIndexOf("(")+3, nodeName.lastIndexOf(")")), 16)));
			}
			ImageCache.cache(name, map);
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
        }
    }
}
