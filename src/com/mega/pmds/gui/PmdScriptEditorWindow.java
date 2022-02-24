package com.mega.pmds.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
	JMenuItem openFile, reloadConfig;
	JMenu helpMenu;
	JMenuItem textTableHelp;
	JFileChooser fc;
	JSplitPane splitPane;
	JTree scriptTree;
	DefaultTreeModel treeModel;
	RomManipulator rom;
	HashMap<TreePath, Callable<ScriptContentPanel>> treeActions;
	private static PmdScriptEditorWindow instance;
	private Preferences prefs;

	private final String LAST_USED_FOLDER = "LAST_USED_FOLDER";
	
	public PmdScriptEditorWindow(String header) {
		super(header);
		
		JScrollPane leftScrollPane;
		ScriptContentPanel rightPane;

		rom = null;
		
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
			openFile = new JMenuItem("Open", KeyEvent.VK_O);
			openFile.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
			openFile.addActionListener(this);
			fileMenu.add(openFile);
			
			reloadConfig = new JMenuItem("Reload Configs", KeyEvent.VK_R);
			reloadConfig.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
			reloadConfig.addActionListener(this);
			fileMenu.add(reloadConfig);

		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);

			textTableHelp = new JMenuItem("ROM Hacking Help", KeyEvent.VK_T);
			textTableHelp.setAccelerator(KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
			textTableHelp.addActionListener(this);
			helpMenu.add(textTableHelp);
		
		this.setJMenuBar(menuBar);

		treeModel = new DefaultTreeModel(new ScriptTreeNode("No ROM", false));
		scriptTree = new JTree(treeModel);
		scriptTree.setBorder(BorderFactory.createEmptyBorder());
		scriptTree.addTreeSelectionListener(this);
		leftScrollPane = new JScrollPane(scriptTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		leftScrollPane.setPreferredSize(new Dimension(300,800));
		leftScrollPane.setMinimumSize(new Dimension(250,0));
		rightPane = new ScriptOverviewPanel();
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftScrollPane, rightPane);
		
		this.getContentPane().add(splitPane);
		this.pack();

		// Get same directory as last open.
		prefs = Preferences.userRoot().node(getClass().getName());

		fc = new JFileChooser(prefs.get(LAST_USED_FOLDER, new File(".").getAbsolutePath()));
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
				//System.out.println("Error loading " + task.getType() + ": " + ipe.getMessage());
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
					prefs.put(LAST_USED_FOLDER, fc.getSelectedFile().getParent());
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
		} else if (event.getSource().equals(textTableHelp)) {
			JOptionPane.showMessageDialog(this, "Nothing here yet...");
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {
		try {
			JComponent rightPane;
			ScriptContentPanel rightContentPane = treeActions.get(scriptTree.getSelectionPath()).call();
			if(rightContentPane.getShouldScroll()) {
				rightPane = new JScrollPane(rightContentPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			} else {
				rightPane = rightContentPane;
			}
			//If the user changed the divider location, keep it there whn you update.
			int dividerLocation = splitPane.getDividerLocation();
			splitPane.setRightComponent(rightPane);
			splitPane.setDividerLocation(dividerLocation);
		} catch (NullPointerException npe) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
