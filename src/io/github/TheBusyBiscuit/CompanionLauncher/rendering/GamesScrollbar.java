package io.github.TheBusyBiscuit.CompanionLauncher.rendering;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicScrollBarUI;

import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;

public class GamesScrollbar extends BasicScrollBarUI {

	public Point indent_track = new Point(8, 6);
	public Point indent_thumb = new Point(6, 6);
	
	public JScrollBar scrollbar;
	public JTable table;
	
	public int table_height;
	
	public GamesScrollbar(JScrollBar scrollbar, JTable table) {
		this.scrollbar = scrollbar;
		this.table = table;
		
		table_height = table.getTableHeader().getHeight();
	}

	@Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle rect) {
    	g.setColor(ColorScheme.SCROLLBAR_TRACK.color);
    	g.fillRect(rect.x + (int) indent_track.getX(), rect.y + (int) indent_track.getY(), rect.width - (int) indent_track.getX() * 2, rect.height - (int) indent_track.getY() * 2);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle rect) {
    	g.setColor(ColorScheme.SCROLLBAR_THUMB.color);
    	g.fillRect(rect.x + (int) indent_thumb.getX(), rect.y + (int) indent_thumb.getY(), rect.width - (int) indent_thumb.getX() * 2, rect.height - (int) indent_thumb.getY() * 2);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createEmptyButton();
    }

    @Override    
    protected JButton createIncreaseButton(int orientation) {
        return createEmptyButton();
    }

    private JButton createEmptyButton() {
        JButton jbutton = new JButton();
        jbutton.setPreferredSize(new Dimension(0, 0));
        jbutton.setMinimumSize(new Dimension(0, 0));
        jbutton.setMaximumSize(new Dimension(0, 0));
        return jbutton;
    }
    
}
