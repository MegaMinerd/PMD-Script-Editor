package com.mega.pmds;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

import com.mega.pmds.gui.PmdScriptEditorWindow;

public class Main {
	private static PmdScriptEditorWindow frame;
	
	public static void main(String[] args) throws IOException {
		//RandomAccessFile file = new RandomAccessFile(new File("C:\\Users\\Owner\\Desktop\\Games\\emu\\PMD-Red-Modified.gba"), "r");
		//System.out.print(CodeConverter.interpretCode(file, 0x1A239C));
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					init();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private static void init() throws IOException {
		frame = new PmdScriptEditorWindow("PMD Script Editing Tool");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1200, 750));
		frame.pack();
		frame.setVisible(true);
		frame.updateAll();
	}
}