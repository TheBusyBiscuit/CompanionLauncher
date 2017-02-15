package io.github.TheBusyBiscuit.CompanionLauncher.components;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JTable;

public class WindowMover extends MouseAdapter implements MouseMotionListener {
	
	public static Point mouse;
	
	public static Set<JFrame> frames = new HashSet<JFrame>();
	public boolean secondcolumn;
	
	public WindowMover(JFrame frame, boolean secondcolumn) {
		frames.add(frame);
		this.secondcolumn = secondcolumn;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		mouse = null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        int column = ((JTable) e.getSource()).columnAtPoint(p);
        
    	if (column == 0 || column == 2|| (column == 1 && secondcolumn)) {
        	mouse = e.getPoint();
    	}
    }
    
    @Override
	public void mouseDragged(MouseEvent e) {
        Point p = e.getLocationOnScreen();
        
        if (mouse != null) {
       	 	for (JFrame frame: frames) {
       	 		frame.setLocation(p.x - mouse.x, p.y - mouse.y);
       	 	}
        }
	}
}
