package io.github.TheBusyBiscuit.CompanionLauncher;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.TheBusyBiscuit.CompanionLauncher.components.ProgressDialog;
import io.github.TheBusyBiscuit.CompanionLauncher.components.WindowMover;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ThemeColor;

public class LauncherUI {
	
	public static boolean fresh_install = false;
	
	public static SteamWindow games;
	public static SettingsWindow settings;
	
	public static ProgressDialog progress;
	
	public static Config config;
	
	public static TrayIcon icon;
	
	public static JFrame frame;

	private static Thread thread;
	
	public static void main(final String[] args) throws IOException {
		frame = new JFrame();
		
		init(args);
	}

	private static void init(String[] args) throws IOException {
		long timestamp = System.currentTimeMillis();
		
		String path = System.getenv("APPDATA") + "\\." + AppInfo.name;
		
		ThemeColor.viewports = new HashSet<JViewport>();
		WindowMover.frames = new HashSet<JFrame>();
		
		if (!new File(path + "\\mainfest").exists()) new File(path + "\\manifest").mkdirs();
		if (!new File(path + "\\thumbnail").exists()) new File(path + "\\thumbnail").mkdirs();
		if (!new File(path + "\\callback").exists()) new File(path + "\\callback").mkdirs();
		if (!new File(path + "\\common\\categories").exists()) new File(path + "\\common\\categories").mkdirs();
		
		System.out.println("Loading Assets...");

		new AssetsLoader().load();
		
		System.out.println("Loading 'config.json'...");
		config = new Config();
		
		progress = new ProgressDialog(frame, !fresh_install);

    	progress.addMaximum(ColorScheme.colors.size());
		for (ThemeColor color: ColorScheme.colors) {
			color.color = ThemeColor.stringToColor(config.json.get("colors").getAsJsonObject().get(color.path).getAsString());
	    	progress.addProgress();
		}
		
		ACFConverter converter = new ACFConverter();
		Set<GameData> data = new HashSet<GameData>();
		
		if (!fresh_install) {
			progress.addMaximum(config.json.get("directories").getAsJsonArray().size());
			for (JsonElement element: config.json.get("directories").getAsJsonArray()) {
				System.out.println("Mining '" + element.getAsString() + "' for games...");
				File dir = new File(element.getAsString());
				if (dir.exists() && dir.isDirectory()) {
					for (File file: dir.listFiles()) {
						if (file.getName().startsWith("appmanifest_") && file.getName().endsWith(".acf")) {
					    	progress.addMaximum(3);
						}
					}
					for (File file: dir.listFiles()) {
						if (file.getName().startsWith("appmanifest_") && file.getName().endsWith(".acf")) {
							GameData gd = converter.convert(file);
							if (gd != null) {
								data.add(gd);
								progress.addProgress();
							}
							else {
								progress.addMaximum(-3);
							}
						}
					}
				}
				progress.addProgress();
			}
		}
		
		JsonObject library = converter.createIndex();
		
		if (!SystemTray.isSupported()) {
			JOptionPane.showMessageDialog(games, "Could not add TrayIcon to SystemTray", "Critical Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }
		
        ToolTipManager manager = ToolTipManager.sharedInstance();
        manager.setInitialDelay(0);
        
        UIManager.put("ToolTip.background", new ColorUIResource(255, 255, 255));
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(new Color(50, 50, 50)));
		
		System.out.println("Launching Window...");
		
		games = new SteamWindow(data);
		games.setupFrame(library);
		
		settings = new SettingsWindow(games);

		games.setVisible(!fresh_install && args.length > 0 && args[0].equals("-library"));
		settings.setVisible(fresh_install);
		
		if (fresh_install) {
			JOptionPane.showMessageDialog(settings, "It looks like you are using this Application\nfor the first time.\nAdjust the settings to your likings.\nThe Application will restart itself when you are done.\n\nNote: Make sure to add your Steam libraries in here,\notherwise this Software may not work properly", "Info", JOptionPane.WARNING_MESSAGE);
		}
		else {
			System.out.println("Launching System Tray...");

	        icon = new LauncherTray();
	         
	        try {
	        	SystemTray.getSystemTray().add(icon);
	        } catch (AWTException e) {
	        	JOptionPane.showMessageDialog(games, "Could not add TrayIcon to SystemTray", "Critical Error", JOptionPane.ERROR_MESSAGE);
	            System.exit(0);
	        }
		}
		
		progress.addProgress();
		System.out.println("Finished (" + (System.currentTimeMillis() - timestamp) + "ms)");
	}

	public static void restart() {
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		// We need a Thread since Java somehow refuses to draw Graphics
		// if we keep using the same Thread for all our code
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				fresh_install = false;
				
				games.dispose();
				settings.dispose();
				
				if (icon != null) {
					SystemTray.getSystemTray().remove(icon);
				}
				
				try {
					System.out.println();
					System.out.println("# Restarting...");
					System.out.println();
					init(new String[] {"-library"});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
	}

}
