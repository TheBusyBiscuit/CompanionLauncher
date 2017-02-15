package io.github.TheBusyBiscuit.CompanionLauncher.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.border.Border;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ImageHelper;

public class IconTextField extends JTextField {
	
	private static final long serialVersionUID = 7514621446925199564L;
	
	private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);
	
	private ImageIcon icon = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\search.png", "Search"), SteamWindow.SIZE_ICON, SteamWindow.SIZE_ICON));
	
	public IconTextField() {
		super();
		
		setBorder(BorderFactory.createEmptyBorder(0, SteamWindow.SIZE_ICON, 0, 0));
	}
	
	@Override
	public Color getBackground() {
		return ColorScheme.PANE_BACKGROUND.color;
	}
	
	@Override
	public Font getFont() {
		return font;
	}
	
	@Override
	public Color getForeground() {
		return ColorScheme.FONT_GAME.color;
	}
	
	@Override
	public Border getBorder() {
		return BorderFactory.createEmptyBorder();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int iconHeight = icon.getIconHeight();
        int x = 5;
        int y = (this.getHeight() - iconHeight) / 2;
        icon.paintIcon(this, g, x, y);
	}

}
