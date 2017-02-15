package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.LauncherUI;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ImageHelper;

public class DirectoryRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 5884142568559930219L;
	
	private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT - 2);
	private ImageIcon img = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\edit.png", "Edit"), SteamWindow.SIZE_ICON, SteamWindow.SIZE_ICON));

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		
		label.setFont(font);
		label.setForeground(ColorScheme.FONT_GAME.color);

		label.setIcon(img);
		
		int n = LauncherUI.config.json.get("directories").getAsJsonArray().size();
		if (n == 1) {
			label.setText(n + " Steam Library registered.");
		}
		else {
			label.setText(n + " Steam Libraries registered.");
		}
		label.setHorizontalTextPosition(SwingConstants.LEFT);
		
		return label;
	}

}
