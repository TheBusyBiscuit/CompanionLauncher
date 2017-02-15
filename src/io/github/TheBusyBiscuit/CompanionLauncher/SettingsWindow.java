package io.github.TheBusyBiscuit.CompanionLauncher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import io.github.TheBusyBiscuit.CompanionLauncher.components.ColorCellEditor;
import io.github.TheBusyBiscuit.CompanionLauncher.components.ControlTable;
import io.github.TheBusyBiscuit.CompanionLauncher.components.DirectoryCellEditor;
import io.github.TheBusyBiscuit.CompanionLauncher.regions.CurrencyTranslator;
import io.github.TheBusyBiscuit.CompanionLauncher.regions.Region;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.ColorRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.ConfigRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.DirectoryRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.GamesScrollbar;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.HeaderRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.NametagRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.OmitNametagRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.OmitThumbnailRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.sorting.AlphabeticalSorter;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ThemeColor;

public class SettingsWindow extends JFrame {
	
	private static final long serialVersionUID = -3945190415220443665L;
	
	public SettingsWindow(SteamWindow window) {
		setIconImage(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\icon.png").getImage());
		
		setSize(window.getSize());
		setLocation(window.getLocation());
		
		JTable controls = new ControlTable(this, "Games");
		
		final JTable games = new JTable(new Object[LauncherUI.games.sorted.size()][2], new String[] {"Icon", "Name"}) {
			
			private static final long serialVersionUID = 745343688555014427L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public String getToolTipText(MouseEvent e) {
                Point p = e.getPoint();
                int row = rowAtPoint(p);
                
                int id = Math.abs((Integer) getValueAt(row, 0));
                
                if (LauncherUI.config.json.get("visibility").getAsJsonObject().get(String.valueOf(id)).getAsBoolean()) {
                	return "<html>Hide?<size=" + row + "></html>";
    			}
                else {
                	return "<html>Show?<size=" + row + "></html>";
                }
            }
			
			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
			@Override
			public Color getGridColor() {
				return ColorScheme.GRID.color;
			}
			
		};
		
		games.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                int row = games.rowAtPoint(p);
                
                int id = Math.abs((Integer) games.getValueAt(row, 0));
                
                if (LauncherUI.config.json.get("visibility").getAsJsonObject().get(String.valueOf(id)).getAsBoolean()) {
                	LauncherUI.config.json.get("visibility").getAsJsonObject().addProperty(String.valueOf(id), false);
    				games.setValueAt(-id, row, 0);
    				repaint();
    				
                	try {
						LauncherUI.config.save();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
    			}
                else {
                	LauncherUI.config.json.get("visibility").getAsJsonObject().addProperty(String.valueOf(id), true);
    				games.setValueAt(id, row, 0);
    				repaint();
    				
                	try {
						LauncherUI.config.save();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
                }
			}
			
		});
		
		List<Integer> list = new ArrayList<Integer>(LauncherUI.games.sorted);
		Collections.sort(list, new AlphabeticalSorter() {
			
			@Override
			public SortingDirection getDirection() {
				return SortingDirection.DOWN;
			}
		});
		
		int i = 0;
		for (int id: list) {
			GameData data = LauncherUI.games.games.get(id);
			
			if (LauncherUI.config.json.get("visibility").getAsJsonObject().get(String.valueOf(id)).getAsBoolean()) {
				games.setValueAt(data.id, i, 0);
			}
			else {
				games.setValueAt(-data.id, i, 0);
			}
			games.setValueAt(data.name, i, 1);
			
			LauncherUI.progress.addProgress();
			
			i++;
		}
		
		games.setCellSelectionEnabled(false);
		games.setFocusable(false);
		games.setRowHeight(SteamWindow.HEIGHT_THUMBNAIL + 9);
		games.setShowVerticalLines(false);
		
		games.getTableHeader().setResizingAllowed(false);
		games.getTableHeader().setReorderingAllowed(false);
		
		TableCellRenderer renderer = games.getTableHeader().getDefaultRenderer();
		games.getTableHeader().setDefaultRenderer(new HeaderRenderer(renderer, false));
		
		Color none = new Color(0, 0, 0, 0);
		Color omit = new Color(120, 120, 120, 120);
		
		OmitNametagRenderer nametag = new OmitNametagRenderer(8, ColorScheme.FONT_GAME, ColorScheme.FONT_GAME_DISABLED);
		OmitThumbnailRenderer thumbnail = new OmitThumbnailRenderer(none, omit);
		
		games.getColumnModel().getColumn(0).setCellRenderer(thumbnail);
		games.getColumnModel().getColumn(1).setCellRenderer(nametag);

		games.getColumnModel().getColumn(0).setMinWidth(SteamWindow.WIDTH_THUMBNAIL + 8);
		games.getColumnModel().getColumn(0).setMaxWidth(SteamWindow.WIDTH_THUMBNAIL + 8);
		
		final JScrollPane pane1 = new JScrollPane(games);
		pane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane1.getViewport().setBackground(ColorScheme.PANE_BACKGROUND.color);
		ThemeColor.viewports.add(pane1.getViewport());
		pane1.setBorder(BorderFactory.createEmptyBorder());

		final JComboBox<String> regions = new JComboBox<String>();
		regions.setFont(new Font("Dialog", Font.PLAIN, SteamWindow.SIZE_FONT));
		final List<String> cc = new ArrayList<String>();
		
		for (Region region: CurrencyTranslator.regions) {
			regions.addItem(region.name + " (" + region.currency + ")");
			cc.add(region.code);
		}
		
		final SettingsWindow frame = this;
		
		DefaultTableModel model = new DefaultTableModel(new Object[14][2], new String[] {"Key", "Value"});
		
		final JTable options = new JTable(model) {

			private static final long serialVersionUID = -6788786467224156357L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 1) {
					return true;
				}
				return false;
			}
			
			private ConfigRenderer value = new ConfigRenderer(true);
			private ColorRenderer color2 = new ColorRenderer();
			private DirectoryRenderer directories2 = new DirectoryRenderer();
			
			private DefaultCellEditor choice = new DefaultCellEditor(regions);
			private ColorCellEditor color = new ColorCellEditor(frame);
			private DirectoryCellEditor directories = new DirectoryCellEditor(frame, directories2);
			
			@Override
			public TableCellEditor getCellEditor(int row, int column) {
				if (column == 1) {
					if (row == 0) {
						return choice;
					}
					else if (row == 1) {
						return directories;
					}
					else if (row > 1) {
						return color;
					}
				}
				return super.getCellEditor(row, column);
			}
			
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				if (column == 1) {
					if (row == 1) {
						return directories2;
					}
					else if (row > 1) {
						return color2;
					}
					else {
						return value;
					}
				}
				else {
					return super.getCellRenderer(row, column);
				}
			}
			
			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
			@Override
			public Color getGridColor() {
				return ColorScheme.GRID.color;
			}
		};
		
		Region r = Region.byCode(LauncherUI.config.json.get("region").getAsString());

		options.setValueAt("Region", 0, 0);
		options.setValueAt(r.name + " (" + r.currency + ")", 0, 1);
		
		options.setValueAt("Steam Directories", 1, 0);
		options.setValueAt(LauncherUI.config.json.get("directories").getAsJsonArray(), 1, 1);

		options.setValueAt("Font Color (Default)", 2, 0);
		options.setValueAt(ColorScheme.FONT_GAME, 2, 1);
		
		options.setValueAt("Font Color (Developers)", 3, 0);
		options.setValueAt(ColorScheme.FONT_DEVELOPERS, 3, 1);
		
		options.setValueAt("Font Color (Disabled)", 4, 0);
		options.setValueAt(ColorScheme.FONT_GAME_DISABLED, 4, 1);
		
		options.setValueAt("Font Color (Info)", 5, 0);
		options.setValueAt(ColorScheme.FONT_INFO, 5, 1);
		
		options.setValueAt("Font Color (Discount)", 6, 0);
		options.setValueAt(ColorScheme.FONT_DISCOUNT, 6, 1);
		
		options.setValueAt("Background Color (Table)", 7, 0);
		options.setValueAt(ColorScheme.TABLE_BACKGROUND, 7, 1);
		
		options.setValueAt("Background Color (Window)", 8, 0);
		options.setValueAt(ColorScheme.WINDOW_BACKGROUND, 8, 1);
		
		options.setValueAt("Background Color (Panel)", 9, 0);
		options.setValueAt(ColorScheme.PANE_BACKGROUND, 9, 1);
		
		options.setValueAt("Grid Color (Table)", 10, 0);
		options.setValueAt(ColorScheme.GRID, 10, 1);
		
		options.setValueAt("Header Color (Table)", 11, 0);
		options.setValueAt(ColorScheme.TABLE_HEADER, 11, 1);
		
		options.setValueAt("Scrollbar Color (Track)", 12, 0);
		options.setValueAt(ColorScheme.SCROLLBAR_TRACK, 12, 1);
		
		options.setValueAt("Scrollbar Color (Thumb)", 13, 0);
		options.setValueAt(ColorScheme.SCROLLBAR_THUMB, 13, 1);
		
		regions.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				options.setValueAt(e.getItem(), 0, 1);
				LauncherUI.config.json.addProperty("region", cc.get(regions.getSelectedIndex()));
				try {
					LauncherUI.config.save();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		options.setCellSelectionEnabled(false);
		options.setFocusable(false);
		options.setRowHeight((SteamWindow.HEIGHT_THUMBNAIL + 9) / 2);
		
		options.getTableHeader().setResizingAllowed(false);
		options.getTableHeader().setReorderingAllowed(false);
		
		options.getTableHeader().setDefaultRenderer(new HeaderRenderer(renderer, false));
		
		options.getColumnModel().getColumn(0).setCellRenderer(new NametagRenderer(6, ColorScheme.FONT_GAME));
		
		final JScrollPane pane2 = new JScrollPane(options);
		
		pane2.getViewport().setBackground(ColorScheme.PANE_BACKGROUND.color);
		pane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		ThemeColor.viewports.add(pane2.getViewport());
		pane2.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel panel = new JPanel(new BorderLayout(0, 0));
		JPanel panelE = new JPanel(new BorderLayout(0, 0));
		JPanel panelW = new JPanel(new BorderLayout(0, 0));
		
		JScrollBar scrollbar = new JScrollBar(JScrollBar.VERTICAL) {

			private static final long serialVersionUID = 1630052558564562975L;

			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
		};
		
		scrollbar.setPreferredSize(new Dimension(20, -1));
		scrollbar.setUI(new GamesScrollbar(scrollbar, games));
		scrollbar.setUnitIncrement(32);
		pane1.setVerticalScrollBar(scrollbar);
		
		JScrollBar scrollbar2 = new JScrollBar(JScrollBar.VERTICAL) {
			
			private static final long serialVersionUID = -8775913427060014302L;

			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
		};
		
		scrollbar2.setPreferredSize(new Dimension(20, -1));
		scrollbar2.setUI(new GamesScrollbar(scrollbar2, options));
		scrollbar2.setUnitIncrement(32);
		pane2.setVerticalScrollBar(scrollbar2);

		panelW.add(pane1, BorderLayout.CENTER);
		panelW.add(scrollbar, BorderLayout.WEST);
		
		panelE.add(pane2, BorderLayout.CENTER);
		panelE.add(scrollbar2, BorderLayout.EAST);
		
		panel.add(panelE, BorderLayout.CENTER);
		
		if (!LauncherUI.fresh_install) {
			panel.add(panelW, BorderLayout.WEST);
		}
		
		panel.add(controls, BorderLayout.NORTH);
		add(panel);
		
		setUndecorated(true);
	}
	
	@Override
	public void setVisible(boolean b) {
		if (!b) {
			try {
				LauncherUI.config.save();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			if (LauncherUI.fresh_install) {
				super.setVisible(b);
				LauncherUI.fresh_install = false;
				LauncherUI.restart();
			}
			else {
				super.setVisible(b);
			}
		}
		else {
			super.setVisible(b);
		}
	}
}
