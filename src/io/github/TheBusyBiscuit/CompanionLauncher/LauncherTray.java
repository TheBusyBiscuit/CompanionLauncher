package io.github.TheBusyBiscuit.CompanionLauncher;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class LauncherTray extends TrayIcon {

	public LauncherTray() {
		super(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\icon.png").getImage());
        setImageAutoSize(true);
        setToolTip(AppInfo.name);
        
        PopupMenu popup = new PopupMenu();
        
        MenuItem toggle = new MenuItem("Show / Hide");
        toggle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (LauncherUI.games.isVisible()) {
					LauncherUI.games.setVisible(false);
				}
				else if (LauncherUI.settings.isVisible()) {
					LauncherUI.settings.setVisible(false);
				}
				else {
					LauncherUI.games.setVisible(true);
				}
			}
		});
        
        MenuItem lib = new MenuItem("Library");
        lib.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LauncherUI.games.setVisible(true);
				LauncherUI.settings.setVisible(false);
			}
		});
        
        MenuItem options = new MenuItem("Settings");
        options.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LauncherUI.games.setVisible(false);
				LauncherUI.settings.setVisible(true);
			}
		});
        
        MenuItem about = new MenuItem("About");
        about.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(LauncherUI.games, "Version: " + AppInfo.version + "\nAuthor(s): " + AppInfo.authors + "\n\nhttps://github.com/TheBusyBiscuit/" + AppInfo.name, "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
        
        MenuItem restart = new MenuItem("Restart");
        restart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LauncherUI.restart();
			}
		});
        
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

        popup.add(toggle);
        popup.add(lib);
        popup.add(options);
        popup.add(about);
        popup.add(restart);
        popup.add(exit);
        
        setPopupMenu(popup);
        
        addMouseListener(new MouseAdapter() {
        	
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					if (LauncherUI.games.isVisible()) {
						LauncherUI.games.setVisible(false);
					}
					else if (LauncherUI.settings.isVisible()) {
						LauncherUI.settings.setVisible(false);
					}
					else {
						LauncherUI.games.setVisible(true);
					}
				}
			}
			
		});
	}

}
