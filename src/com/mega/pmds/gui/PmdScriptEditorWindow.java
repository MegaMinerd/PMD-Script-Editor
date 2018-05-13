package com.mega.pmds.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.xml.sax.SAXException;

import com.mega.pmds.InvalidPointerException;
import com.mega.pmds.RomManipulator;
import com.mega.pmds.util.ConfigHandler;
import com.mega.pmds.util.LoadTask;

public class PmdScriptEditorWindow extends JFrame implements ActionListener, TreeSelectionListener{
	JMenuBar menuBar;
	JMenu fileMenu;
	JMenuItem openFile, saveFile, reloadConfig;
	JFileChooser fc;
	JSplitPane splitPane;
	JTree scriptTree;
	DefaultTreeModel treeModel;
	RomManipulator rom;
	JScrollPane leftScrollPane, rightScrollPane;
	HashMap<TreePath, Callable<ScriptContentPanel>> treeActions;
	private static PmdScriptEditorWindow instance;
	
	public PmdScriptEditorWindow(String header) {
		super(header);
		
		rom = null;
		
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		openFile = new JMenuItem("Open", KeyEvent.VK_O);
		openFile.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		openFile.addActionListener(this);
		fileMenu.add(openFile);
		
		saveFile = new JMenuItem("Save", KeyEvent.VK_S);
		saveFile.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		saveFile.addActionListener(this);
		fileMenu.add(saveFile);
		
		reloadConfig = new JMenuItem("Reload Configs", KeyEvent.VK_R);
		reloadConfig.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		reloadConfig.addActionListener(this);
		fileMenu.add(reloadConfig);
		
		this.setJMenuBar(menuBar);

		treeModel = new DefaultTreeModel(new ScriptTreeNode("No ROM", false));
		scriptTree = new JTree(treeModel);
		scriptTree.setBorder(BorderFactory.createEmptyBorder());
		scriptTree.addTreeSelectionListener(this);
		leftScrollPane = new JScrollPane(scriptTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rightScrollPane = new JScrollPane(new ScriptOverviewPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftScrollPane, rightScrollPane);
		
		this.getContentPane().add(splitPane);
		
		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("ROM Files (.gba)", "gba"));
		
		treeActions = new HashMap<TreePath, Callable<ScriptContentPanel>>();
		instance = this;
	}
	
	public static void addTreeAction(TreePath path, Callable<ScriptContentPanel> action) {
		instance.treeActions.put(path, action);
	}
	
	public void updateAll() throws IOException {
		if(rom==null) {
			splitPane.setDividerLocation(0.33);
		}else {
			try {
				ConfigHandler.reload();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updateTree();
			updateContent();
		}
	}
	
	public void updateTree() throws IOException {
		ScriptTreeNode root = new ScriptTreeNode(RomManipulator.getFilename(), true);
		treeModel.setRoot(root);
		PriorityQueue<LoadTask> tasks = new PriorityQueue<LoadTask>();
		RomManipulator.seek(0x11E258);
		//Used to avoid duplication of areas
		//The team base is duplicated several times, likely once for each possible appearance
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		while(true) {
			try {
				int pointer = RomManipulator.parsePointer();
				if(!offsets.contains(pointer)) {
					tasks.add(new LoadTask(LoadTask.Type.AREA, root, 1, pointer));
					offsets.add(pointer);
				}
			}catch (InvalidPointerException ipe) {
				break;
			}
		}
		while(!tasks.isEmpty()) {
			LoadTask task = tasks.remove();
			try {
				task.load(tasks);
			}catch(InvalidPointerException ipe) {
				//System.out.println(ipe.getMessage());
			}
		}
	}
	
	public void updateContent() {
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(openFile)) {
			int returnVal = fc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					rom = new RomManipulator(fc.getSelectedFile());
					updateAll();
				}catch (FileNotFoundException fnfe) {
				}catch (IOException ioe) {
				}
			}
		}else if(event.getSource().equals(reloadConfig)) {
			try {
				updateAll();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {
		try {
			try {
				rightScrollPane = new JScrollPane((treeActions.get(scriptTree.getSelectionPath()).call()), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				rightScrollPane.setPreferredSize(null);
				splitPane.setRightComponent(rightScrollPane);
			} catch (NullPointerException npe) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch(NullPointerException npe) {
			
		}
	}

}
