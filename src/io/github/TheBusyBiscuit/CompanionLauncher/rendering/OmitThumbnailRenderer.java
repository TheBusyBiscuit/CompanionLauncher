package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ImageHelper;

public class OmitThumbnailRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = -2989302091434897998L;
	
	public Color shown, hidden;
	
	public OmitThumbnailRenderer(Color shown, Color hidden) {
		this.shown = shown;
		this.hidden = hidden;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		
		if (value == null)
			return label;
		
		int id = Math.abs((Integer) value);
		
		File file = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\thumbnail\\" + id + ".jpg");
		
		if (file.exists()) {
			ImageIcon img = new ImageIcon(file.getPath());
			BufferedImage buffered = new BufferedImage(SteamWindow.WIDTH_THUMBNAIL, SteamWindow.HEIGHT_THUMBNAIL, BufferedImage.TYPE_INT_ARGB);
			Graphics g = buffered.createGraphics();
			g.drawImage(ImageHelper.resize(img, SteamWindow.WIDTH_THUMBNAIL, SteamWindow.HEIGHT_THUMBNAIL), 0, 0, SteamWindow.WIDTH_THUMBNAIL, SteamWindow.HEIGHT_THUMBNAIL, null);
			
			if ((Integer) value > 0) {
				g.setColor(shown);
			}
			else {
				g.setColor(hidden);
			}
			
			g.fillRect(0, 0, SteamWindow.WIDTH_THUMBNAIL, SteamWindow.HEIGHT_THUMBNAIL);
			
			label.setIcon(new ImageIcon(buffered));
		}
		
		return label;
	}

}
