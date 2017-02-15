package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ThemeColor;

public class OmitNametagRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 717559497717718242L;
	
	int offset;
	ThemeColor shown, hidden;
	
	public OmitNametagRenderer(int offset, ThemeColor shown, ThemeColor hidden) {
		this.offset = offset;
		this.shown = shown;
		this.hidden = hidden;
	}
	
	private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);
	private EmptyIcon icon = new EmptyIcon(offset, 10);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		label.setFont(font);
		
		if ((Integer) table.getValueAt(row, 0) > 0) {
			label.setForeground(shown.color);
		}
		else {
			label.setForeground(hidden.color);
		}
		
		label.setIconTextGap(0);
		label.setIcon(icon);
		label.setText((String) value);
		
		return label;
	}

}
