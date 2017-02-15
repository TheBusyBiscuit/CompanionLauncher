package io.github.TheBusyBiscuit.CompanionLauncher.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import io.github.TheBusyBiscuit.CompanionLauncher.LauncherUI;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.DirectoryRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;

public class DirectoryCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 5930299914582387896L;
	
	private JButton button;
	
	public DirectoryCellEditor(final JFrame frame, final DirectoryRenderer renderer) {
		button = new JButton() {
			
			private static final long serialVersionUID = -1220537702186426861L;
			private Font font = new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT);

			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
			@Override
			public Color getForeground() {
				return ColorScheme.FONT_GAME.color;
			}
			
			@Override
			public Font getFont() {
				return font;
			}
			
		};
		
		button.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		button.setText(((JLabel) renderer.getTableCellRendererComponent(null, null, false, false, 0, 0)).getText());
		button.setIcon(((JLabel) renderer.getTableCellRendererComponent(null, null, false, false, 0, 0)).getIcon());
		button.setBorder(BorderFactory.createEmptyBorder());
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new DirectoryEditor();
				
				button.setText(((JLabel) renderer.getTableCellRendererComponent(null, null, false, false, 0, 0)).getText());
				
				frame.repaint();
			}
		});
	}

	@Override
	public Object getCellEditorValue() {
		return LauncherUI.config.json.get("directories").getAsJsonArray();
	}

	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, int row, int column) {
	    return button;
	}
}