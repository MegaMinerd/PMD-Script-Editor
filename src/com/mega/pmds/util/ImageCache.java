package com.mega.pmds.util;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ImageCache {
	private static HashMap<String, BufferedImage> cache = null;
	
	private static void verifyInit() {
		if(cache==null) {
			cache = new HashMap<String, BufferedImage>();
		}
	}
	
	public static boolean isCached(String name) {
		verifyInit();
		return cache.containsKey(name);
	}
	
	public static BufferedImage getImage(String name) {
		verifyInit();
		return cache.get(name);
	}
	
	public static void cache(String name, BufferedImage image) {
		verifyInit();
		cache.put(name, image);
	}
}
