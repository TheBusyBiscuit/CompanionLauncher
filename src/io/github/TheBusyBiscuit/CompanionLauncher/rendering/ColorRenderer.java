package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.utils.ThemeColor;

public class ColorRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 2741861876999315808L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JPanel panel = new JPanel();
		
		panel.setBorder(BorderFactory.createEmptyBorder());
		if (value != null) {
			panel.setBackground(((ThemeColor) value).color);
		}
		
		return panel;
	}

}
