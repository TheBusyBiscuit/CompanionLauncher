package io.github.TheBusyBiscuit.CompanionLauncher.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.LauncherUI;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;

public class DirectoryEditor extends JDialog {

	private static final long serialVersionUID = 575023415766287085L;
	
	public JList<String> list;

	public JButton add, remove;
	
	public DirectoryEditor() {
		super(LauncherUI.settings, AppInfo.name + " (Directory Editor)", true);
		
		final JDialog dialog = this;
		
		setSize(380, 360);
		setLocationRelativeTo(LauncherUI.settings);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		JPanel panel = new JPanel() {

			private static final long serialVersionUID = 7081249116513501229L;

			@Override
			public Color getBackground() {
				return ColorScheme.PANE_BACKGROUND.color;
			}
			
		};
		
		list = new JList<String>() {
			
			private static final long serialVersionUID = -9030727402588473007L;
			private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);

			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
			@Override
			public Color getForeground() {
				return ColorScheme.FONT_GAME.color;
			}
			
			@Override
			public Font getFont() {
				return font;
			}
			
		};
		
		DefaultListModel<String> model = new DefaultListModel<String>();
		
		for (JsonElement directory: LauncherUI.config.json.get("directories").getAsJsonArray()) {
			model.addElement(directory.getAsString());
		}
		
		list.setModel(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFixedCellHeight(20);
		list.setBounds(10, 10, getWidth() - 34, 255);
		
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				remove.setEnabled(true);
			}
		});
		
		add = new JButton("Add library") {

			private static final long serialVersionUID = 1498401400035998931L;
			private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);

			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
			@Override
			public Color getForeground() {
				return ColorScheme.FONT_GAME.color;
			}
			
			@Override
			public Font getFont() {
				return font;
			}
			
			@Override
			public boolean isFocusPainted() {
				return false;
			}
			
			@Override
			public boolean isBorderPainted() {
				return false;
			}
			
		};
		add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				fc.showDialog(dialog, "Add");
				File file = fc.getSelectedFile();
				
				if (file != null) {
					((DefaultListModel<String>) list.getModel()).addElement(file.getPath());
				}
			}
		});
		add.setBounds(10, 270, 155, 25);
		
		remove = new JButton("Remove library") {

			private static final long serialVersionUID = 6391017652072885068L;
			private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);

			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
			@Override
			public Color getForeground() {
				return ColorScheme.FONT_GAME.color;
			}
			
			@Override
			public Font getFont() {
				return font;
			}
			
			@Override
			public boolean isFocusPainted() {
				return false;
			}
			
			@Override
			public boolean isBorderPainted() {
				return false;
			}
			
		};
		remove.setEnabled(false);
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int index = list.getSelectedIndex();
					((DefaultListModel<String>) list.getModel()).remove(index);
					remove.setEnabled(false);
				}
				catch(NullPointerException x) {
				}
			}
		});
		remove.setBounds(200, 270, 155, 25);
		
		JButton save = new JButton("Save & Restart") {

			private static final long serialVersionUID = 8039717460565510613L;
			private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);

			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
			@Override
			public Color getForeground() {
				return ColorScheme.FONT_GAME.color;
			}
			
			@Override
			public Font getFont() {
				return font;
			}
			
			@Override
			public boolean isFocusPainted() {
				return false;
			}
			
			@Override
			public boolean isBorderPainted() {
				return false;
			}
			
		};
		
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				
				LauncherUI.restart();
			}
		});
		save.setBounds(40, 300, 285, 25);
		
		add(list);
		add(add);
		add(remove);
		
		add(save);
		
		add(panel);
		
		setVisible(true);
	}
	
	@Override
	public Color getBackground() {
		return ColorScheme.PANE_BACKGROUND.color;
	}
	
	@Override
	public void dispose() {
		JsonArray array = new JsonArray();
		
		for (Object directory: ((DefaultListModel<String>) list.getModel()).toArray()) {
			array.add((String) directory);
		}
		
		LauncherUI.config.json.add("directories", array);
		
		try {
			LauncherUI.config.save();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		super.dispose();
	}

}
