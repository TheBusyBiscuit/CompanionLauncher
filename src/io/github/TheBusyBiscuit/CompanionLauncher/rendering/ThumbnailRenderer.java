package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.LauncherUI;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ImageHelper;

public class ThumbnailRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 8538147910056323722L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		
		if (value == null)
			return label;
		
		int id = (Integer) value;
		
		File file = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\thumbnail\\" + id + ".jpg");
		
		if (file.exists()) {
			ImageIcon img = new ImageIcon(file.getPath());
			BufferedImage buffered = new BufferedImage(SteamWindow.WIDTH_THUMBNAIL, SteamWindow.HEIGHT_THUMBNAIL, BufferedImage.TYPE_INT_ARGB);
			Graphics g = buffered.createGraphics();
			g.drawImage(ImageHelper.resize(img, SteamWindow.WIDTH_THUMBNAIL, SteamWindow.HEIGHT_THUMBNAIL), 0, 0, SteamWindow.WIDTH_THUMBNAIL, SteamWindow.HEIGHT_THUMBNAIL, null);
			
			if (row == LauncherUI.games.selected)
				g.drawImage(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\overlay.png", "Overlay").getImage(), 0, 0, SteamWindow.WIDTH_THUMBNAIL, SteamWindow.HEIGHT_THUMBNAIL, null);
			
			label.setIcon(new ImageIcon(buffered));
		}
		
		return label;
	}

}
