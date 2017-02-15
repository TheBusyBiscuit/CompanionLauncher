package io.github.TheBusyBiscuit.CompanionLauncher.components;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import io.github.TheBusyBiscuit.CompanionLauncher.AppInfo;
import io.github.TheBusyBiscuit.CompanionLauncher.LauncherUI;
import io.github.TheBusyBiscuit.CompanionLauncher.SteamWindow;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.SearchRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.WindowNameRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ImageHelper;

public class ControlTable extends JTable {
	
	private static final long serialVersionUID = 8544420338840113756L;
	
	final ImageIcon minimize1 = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\minimize.png", "Minimize"), SteamWindow.SIZE_ICON - 12, SteamWindow.SIZE_ICON - 12));
	final ImageIcon minimize2 = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\minimize.png", "Minimize"), SteamWindow.SIZE_ICON - 4, SteamWindow.SIZE_ICON - 4));
	
	ImageIcon settings1, settings2;
	
	String image;
	
	public JTextField search_field = new IconTextField();
	
	DefaultCellEditor search_editor = new DefaultCellEditor(search_field);
	
	public ControlTable(final JFrame frame, final String image) {
		super(new Object[1][5], new String[] {"a", "b", "c", "d", "e"});
		
		this.image = image;
		
		search_editor.setClickCountToStart(1);
		
		search_field.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				EventQueue.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						LauncherUI.games.queryTable();
					}
				});
			}
		});
		
		getTableHeader().setEnabled(false);
		setBorder(BorderFactory.createRaisedBevelBorder());
		
		settings1 = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\" +  image.toLowerCase() + ".png"), SteamWindow.SIZE_ICON - 8, SteamWindow.SIZE_ICON - 8));
		settings2 = new ImageIcon(ImageHelper.resize(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\" +  image.toLowerCase() + ".png"), SteamWindow.SIZE_ICON, SteamWindow.SIZE_ICON));
		
		setCellSelectionEnabled(false);
		setFocusable(false);
		setShowVerticalLines(false);
		getColumnModel().getColumn(0).setCellRenderer(new WindowNameRenderer());
		
		if (image.equals("Settings")) {
			getColumnModel().getColumn(1).setCellRenderer(new SearchRenderer());
		}
		
		getColumnModel().getColumn(3).setCellRenderer(getDefaultRenderer(ImageIcon.class));
		getColumnModel().getColumn(4).setCellRenderer(getDefaultRenderer(ImageIcon.class));
		setRowHeight(SteamWindow.SIZE_ICON);
		
		getColumnModel().getColumn(1).setMinWidth(SteamWindow.SIZE_ICON + 150);
		getColumnModel().getColumn(1).setMaxWidth(SteamWindow.SIZE_ICON + 150);
		getColumnModel().getColumn(2).setMinWidth(85 - SteamWindow.SIZE_ICON);
		getColumnModel().getColumn(2).setMaxWidth(85 - SteamWindow.SIZE_ICON);
		getColumnModel().getColumn(3).setMinWidth(SteamWindow.SIZE_ICON);
		getColumnModel().getColumn(3).setMaxWidth(SteamWindow.SIZE_ICON);
		getColumnModel().getColumn(4).setMinWidth(SteamWindow.SIZE_ICON);
		getColumnModel().getColumn(4).setMaxWidth(SteamWindow.SIZE_ICON);
		
		setValueAt("  " + AppInfo.name + " v" + AppInfo.version, 0, 0);
		
		if (!LauncherUI.fresh_install) {
			setValueAt(settings1, 0, 3);
		}
		
		setValueAt(minimize1, 0, 4);
		
		addMouseListener(new MouseAdapter() {

	        @Override
	        public void mousePressed(MouseEvent e) {
	            Point p = e.getPoint();
	            int column = columnAtPoint(p);
	            
	        	if (column == 4) {
	        		frame.setVisible(false);
	        	}
	        	else if (column == 3) {
	        		if (image.equals("Games")) {
	        			LauncherUI.games.setVisible(true);
	        			LauncherUI.settings.setVisible(false);
	        		}
	        		else if (image.equals("Settings")) {
	        			LauncherUI.games.setVisible(false);
	        			LauncherUI.settings.setVisible(true);
	        		}
	        	}
	        }
	        
	        @Override
	        public void mouseExited(MouseEvent e) {
	        	if (!LauncherUI.fresh_install) {
	    			setValueAt(settings1, 0, 3);
	    		}
				setValueAt(minimize1, 0, 4);
	        }
	        
		});
		
		WindowMover mover = new WindowMover(frame, image.equals("Games"));
		
		addMouseListener(mover);
		addMouseMotionListener(mover);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return image.equals("Settings") && column == 1;
	}
	
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == 1) {
			return search_editor;
		}
		return super.getCellEditor(row, column);
	}
	
	@Override
	public String getToolTipText(MouseEvent e) {
        Point p = e.getPoint();
        int column = columnAtPoint(p);
        
        if (column == 3) {
        	if (!LauncherUI.fresh_install) {
    			setValueAt(settings2, 0, 3);
    		}
			setValueAt(minimize1, 0, 4);
			
        	return this.image;
        }
        else if (column == 4) {
        	if (!LauncherUI.fresh_install) {
    			setValueAt(settings1, 0, 3);
    		}
			setValueAt(minimize2, 0, 4);
			
        	return "Hide";
        }
        else {
        	if (!LauncherUI.fresh_install) {
    			setValueAt(settings1, 0, 3);
    		}
			setValueAt(minimize1, 0, 4);
        }

        return null;
    }
	
	@Override
	public Color getBackground() {
		return ColorScheme.WINDOW_BACKGROUND.color;
	}

}
