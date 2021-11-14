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
	
	public MapPanel(ScriptTreeNode nodeIn, Integer idIn) {
		//TODO: Remove premade asset compatibility
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
				int offset = Integer.parseInt(nodeName.substring(nodeName.lastIndexOf("(")+3, nodeName.lastIndexOf(")")), 16);
				map = ImageExtractor.extract(offset);
			}
			ImageCache.cache(name, map);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(map!=null) {
        	g.drawImage(map, 0, 0, this);
        }
    }
}
