package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ImageHelper;

public class SearchRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 5884142568559930219L;

	private ImageIcon img = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\search.png", "Search"), SteamWindow.SIZE_ICON, SteamWindow.SIZE_ICON));
	private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		
		label.setFont(font);
		label.setForeground(ColorScheme.FONT_GAME.color);
		
		label.setIcon(img);
		
		label.setText((String) value);
		
		JScrollPane panel = new JScrollPane(label);
		panel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		panel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.getViewport().setBackground(ColorScheme.PANE_BACKGROUND.color);
		panel.setBorder(null);
		panel.setViewportBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, ColorScheme.TABLE_BACKGROUND.color, ColorScheme.WINDOW_BACKGROUND.color));
		
		return panel;
	}

}
