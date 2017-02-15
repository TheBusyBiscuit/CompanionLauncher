package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;

public class WindowNameRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -2141391715598827941L;

	private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT + 1).deriveFont(Font.BOLD);
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		label.setFont(font);
		label.setForeground(ColorScheme.FONT_WINDOW.color);
		label.setText((String) value);
		
		return label;
	}

}
