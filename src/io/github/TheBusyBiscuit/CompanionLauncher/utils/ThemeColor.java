package io.github.TheBusyBiscuit.CompanionLauncher.utils;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JViewport;

import com.google.gson.JsonObject;

import io.github.TheBusyBiscuit.CompanionLauncher.LauncherUI;

public class ThemeColor {
	
	// Because Java is not object-oriented
	public static Set<JViewport> viewports = new HashSet<JViewport>();
	
	public String path;
	public Color defaultColor;
	public Color color;
	
	public ThemeColor(String path, Color color) {
		this.defaultColor = color;
		this.color = color;
		
		this.path = path;
		
		JsonObject obj = LauncherUI.config.json.get("colors").getAsJsonObject();
		
		if (!obj.has(path)) {
			obj.addProperty(path, colorToString(color));
		}
		
		ColorScheme.colors.add(this);
	}
	
	public static String colorToString(Color color) {
		return "(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
	}
	
	public static Color stringToColor(String color) {
		color = color.substring(1, color.length() - 1);
		int red = Integer.parseInt(color.split(",")[0]);
		int green = Integer.parseInt(color.split(",")[1]);
		int blue = Integer.parseInt(color.split(",")[2]);
		return new Color(red, green, blue);
	}
	
	public String toHex() {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	public void setColor(Color c) {
		if (this.path.equals("background_pane")) {
			for (JViewport port: viewports) {
				port.setBackground(c);
			}
		}
		this.color = c;
		
		JsonObject obj = LauncherUI.config.json.get("colors").getAsJsonObject();

		obj.addProperty(path, colorToString(color));
		
		try {
			LauncherUI.config.save();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
