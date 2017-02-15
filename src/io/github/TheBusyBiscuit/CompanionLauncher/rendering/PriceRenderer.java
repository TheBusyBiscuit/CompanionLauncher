package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ImageHelper;

public class PriceRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 5884142568559930219L;

	private ImageIcon img = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\price.png", "Price"), SteamWindow.SIZE_ICON, SteamWindow.SIZE_ICON));
	private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		
		String price = (String) value;

		label.setFont(font);
		
		if (price != null) {
			if (price.equals("Free")) {
				label.setFont(label.getFont().deriveFont(Font.ITALIC));
			}
			
			if (price.contains("%")) {
				label.setForeground(ColorScheme.FONT_DISCOUNT.color);
			}
			else {
				label.setForeground(ColorScheme.FONT_GAME.color);
			}
			label.setIcon(img);
		}
		else {
			label.setForeground(ColorScheme.FONT_GAME.color);
		}
		
		label.setText(price);
		
		return label;
	}

}
