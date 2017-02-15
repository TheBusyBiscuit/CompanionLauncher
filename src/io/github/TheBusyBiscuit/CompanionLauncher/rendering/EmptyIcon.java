package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class EmptyIcon implements Icon {
	
	int width;
	int height;

	// Somehow Java treats width as height and height as width???
	public EmptyIcon(int height, int width) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getIconHeight() {
		return width;
	}

	@Override
	public int getIconWidth() {
		return height;
	}
	
	Color c = new Color(255, 255, 255, 0);

	@Override
	public void paintIcon(Component comp, Graphics g, int x, int y) {
		g.setColor(c);
		g.fillRect(x, y, getIconWidth(), getIconHeight());
	}

}
