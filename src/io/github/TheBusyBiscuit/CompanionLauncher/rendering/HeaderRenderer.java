package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.LauncherUI;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ImageHelper;

public class HeaderRenderer implements TableCellRenderer {
	
	TableCellRenderer renderer;
	boolean sorting;
	
	public HeaderRenderer(TableCellRenderer renderer, boolean sorting) {
		this.renderer = renderer;
		this.sorting = sorting;
	}
	
	private Font font = new Font("Dialog", Font.PLAIN, 16);
	
	private ImageIcon img1 = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\sort_down.png", "Sorting"), 20, 20));
	private ImageIcon img2 = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\sort_up.png", "Sorting"), 20, 20));

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		label.setFont(font);
		label.setBackground(ColorScheme.TABLE_HEADER.color);
		label.setForeground(ColorScheme.FONT_GAME.color);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setText((String) value);
		label.setBorder(BorderFactory.createEtchedBorder());
		label.setHorizontalAlignment(SwingConstants.LEFT);
		
		if (sorting) {
			int c = (column - 1) * 2;
			
			if (LauncherUI.games.sorting == c) {
				label.setIcon(img1);
			}
			else if (LauncherUI.games.sorting == c + 1) {
				label.setIcon(img2);
			}
		}
		
		return label;
	}

}
