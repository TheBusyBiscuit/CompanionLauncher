package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.GameData;
import io.github.TheBusyBiscuit.CompanionLauncher.LauncherUI;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;

public class GameTitleRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -4140210198566312326L;
	
	private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		label.setFont(font);
		label.setForeground(ColorScheme.FONT_GAME.color);
		
		int id = (Integer) value;
		GameData data = LauncherUI.games.games.get(id);
		
		label.setText("<html>" + data.name + "<br><span style='font-size:87%'><font color=" + ColorScheme.FONT_DEVELOPERS.toHex() + ">" + data.developers + "</font></span></html>");
		
		return label;
	}

}
