package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ThemeColor;

public class NametagRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 8651926630948646194L;
	
	int offset;
	ThemeColor color;
	
	public NametagRenderer(int offset, ThemeColor color) {
		this.offset = offset;
		this.color = color;
	}

	private EmptyIcon icon = new EmptyIcon(offset, 10);
	private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		label.setFont(font);
		label.setForeground(color.color);
		label.setIconTextGap(0);
		label.setIcon(icon);
		
		label.setText((String) value);
		
		return label;
	}

}
