package io.github.TheBusyBiscuit.CompanionLauncher.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import io.github.TheBusyBiscuit.CompanionLauncher.utils.ThemeColor;

public class ColorCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 5930299914582387896L;

	private JButton button = new JButton();
	ThemeColor color;
	JFrame frame;
	
	public ColorCellEditor(JFrame frame) {
		this.frame = frame;
		
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent actionEvent) {
				setColor(JColorChooser.showDialog(button, "Color Chooser", color.color));
		    }
			
		});
	}
	
	@Override
	public Object getCellEditorValue() {
		return color;
	}

	private void setColor(Color c) {
		if (c != null) {
			c = new Color(c.getRed(), c.getGreen(), c.getBlue());
			color.setColor(c);
			button.setBackground(c);
			frame.repaint();
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		color = (ThemeColor) value;
		setColor(color.color);
	    return button;
	}
}