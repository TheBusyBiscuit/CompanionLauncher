package io.github.TheBusyBiscuit.CompanionLauncher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.TheBusyBiscuit.CompanionLauncher.components.ControlTable;
import io.github.TheBusyBiscuit.CompanionLauncher.regions.CurrencyTranslator;
import io.github.TheBusyBiscuit.CompanionLauncher.regions.Region;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.DiskRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.GameTitleRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.GamesScrollbar;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.HeaderRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.NametagRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.PriceRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.rendering.ThumbnailRenderer;
import io.github.TheBusyBiscuit.CompanionLauncher.sorting.CategorySorter;
import io.github.TheBusyBiscuit.CompanionLauncher.sorting.DirectionalSorter;
import io.github.TheBusyBiscuit.CompanionLauncher.sorting.Sorters;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ColorScheme;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.DiskFormat;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ImageHelper;
import io.github.TheBusyBiscuit.CompanionLauncher.utils.ThemeColor;

public class SteamWindow extends JFrame {
	
	private static final long serialVersionUID = 2881198255832751117L;
	
	// Constants
	public static int SIZE_FEATURE = 23;
	public static int SIZE_ICON = 35;
	public static int SIZE_FONT = 13;
	
	public static int WIDTH_THUMBNAIL = 420 / 10 * 3;
	public static int HEIGHT_THUMBNAIL = 215 / 10 * 3;
	
	public int selected = -1;
	
	public List<Integer> sorted = new ArrayList<Integer>();
	public Map<Integer, GameData> games = new HashMap<Integer, GameData>();
	public int sorting = 0;

	int sum_price = 0;
	long sum_size = 0;
	
	Map<Integer, DirectionalSorter> sorters = new HashMap<Integer, DirectionalSorter>();
	
	final Map<Integer, Object[]> data = new HashMap<Integer, Object[]>();
	JTable table;
	
	ControlTable controls;
	
	public SteamWindow(Set<GameData> gd) throws IOException {
		for (GameData d: gd) {
			games.put(d.id, d);
		}
	}
	
	public void setupFrame(JsonObject library) throws FileNotFoundException {
		setIconImage(new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\icon.png").getImage());
		
		Sorters.load(sorters);
		
		sorting = LauncherUI.config.json.get("sorting").getAsInt();
		
		final Map<Integer, String> tooltips = new HashMap<Integer, String>();
		
		for (Map.Entry<Integer, GameData> entry : games.entrySet()) {
			sorted.add(entry.getKey());
		}

    	Collections.sort(sorted, sorters.get(sorting));
		
    	int amount = 0;
    	
    	for (int id: sorted) {
    		Config config = LauncherUI.config;
			
			if (!config.json.get("visibility").getAsJsonObject().has(String.valueOf(id))) {
				config.json.get("visibility").getAsJsonObject().addProperty(String.valueOf(id), true);
			}
			
			if (config.json.get("visibility").getAsJsonObject().get(String.valueOf(id)).getAsBoolean()) {
				amount++;
			}
    	}
    	
    	LauncherUI.config.save();
    	
		String[] columns = new String[] {"Icon", "Name", "Features", "Price", "Size on Disk"};
		Object[][] contents = new Object[amount][5];
    	
    	DefaultTableModel model = new DefaultTableModel(contents, columns);
		
		table = new JTable(model) {
			
			private static final long serialVersionUID = 612214201711979358L;
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public String getToolTipText(MouseEvent e) {
                Point p = e.getPoint();
                int row = rowAtPoint(p);
                int column = columnAtPoint(p);
                
                if (selected != row) {
                	repaint();
                	selected = row;
                }
                
                int game = (Integer) getValueAt(row, 0);

                if (column == 2 && tooltips.containsKey(game)) {
                	return tooltips.get(game) + "<size=" + game + ">";
                }

                return null;
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
		
		table.setCellSelectionEnabled(false);
		table.setFocusable(false);
		table.setRowHeight(HEIGHT_THUMBNAIL + 9);
		
		table.getColumnModel().getColumn(0).setCellRenderer(new ThumbnailRenderer());
		table.getColumnModel().getColumn(1).setCellRenderer(new GameTitleRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(table.getDefaultRenderer(ImageIcon.class));
		table.getColumnModel().getColumn(3).setCellRenderer(new PriceRenderer());
		table.getColumnModel().getColumn(4).setCellRenderer(new DiskRenderer());

		table.getColumnModel().getColumn(0).setMinWidth(WIDTH_THUMBNAIL + 8);
		table.getColumnModel().getColumn(0).setMaxWidth(WIDTH_THUMBNAIL + 8);
		table.getColumnModel().getColumn(1).setMinWidth(100);
		table.getColumnModel().getColumn(3).setMinWidth(SIZE_ICON + 150);
		table.getColumnModel().getColumn(3).setMaxWidth(SIZE_ICON + 150);
		table.getColumnModel().getColumn(4).setMinWidth(SIZE_ICON + 85);
		table.getColumnModel().getColumn(4).setMaxWidth(SIZE_ICON + 85);
		
		table.setShowVerticalLines(false);
		
		table.getTableHeader().setResizingAllowed(false);
		table.getTableHeader().setReorderingAllowed(false);
		
		TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
		table.getTableHeader().setDefaultRenderer(new HeaderRenderer(renderer, true));
		
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int col = table.columnAtPoint(e.getPoint());
		        if (col > 0) {
		        	int c = col - 1;
		        	
		        	if (sorting == c * 2)
		        		sorting = (c * 2) + 1;
		        	else
		        		sorting = c * 2;
		        	
		        	Collections.sort(sorted, sorters.get(sorting));
					queryTable();
		        	LauncherUI.config.json.addProperty("sorting", sorting);
		        	try {
						LauncherUI.config.save();
					} catch (FileNotFoundException x) {
						x.printStackTrace();
					}
		        	repaint();
		        }
		    }
		    
		});
		
		table.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                int row = table.rowAtPoint(p);
                int column = table.columnAtPoint(p);
                
                if (column == 0) {
                	int id = (Integer) table.getValueAt(row, 0);
                	
                	try {
						Desktop.getDesktop().browse(new URI("steam://rungameid/" + id));
						setVisible(false);
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
                }
			}
			
		});
		
		CategorySorter cs = new CategorySorter();
		
		int maxCategories = 0;
		for (int id: sorted) {
			System.out.println(" Indexing '" + id + "'");
			JsonObject callback = games.get(id).callback;
			JsonObject manifest = games.get(id).manifest;
			
			JsonObject root = callback.get(String.valueOf(id)).getAsJsonObject();
			
			Object[] cells = new Object[5];
			
			cells[0] = id;
			cells[1] = id;
			cells[4] = DiskFormat.formatBytes(manifest.get("SizeOnDisk").getAsLong());
			
			sum_size += manifest.get("SizeOnDisk").getAsLong();
			games.get(id).size = manifest.get("SizeOnDisk").getAsLong();
			
			if (root.get("success").getAsBoolean()) {
				root = root.get("data").getAsJsonObject();
				
				String price = "Free";
				
				if (root.has("price_overview")) {
					JsonObject obj = root.get("price_overview").getAsJsonObject();
					games.get(id).price = obj.get("final").getAsInt();
					sum_price += games.get(id).price;
					price = CurrencyTranslator.formatPrice(games.get(id).price, obj.get("discount_percent").getAsInt(), obj.get("currency").getAsString());
				}
				
				String developers = "";
				
				for (JsonElement element: root.get("developers").getAsJsonArray()) {
					if (developers.equals("")) {
						developers = element.getAsString();
					}
					else {
						developers += ", " + element.getAsString();
					}
				}
				
				games.get(id).developers = developers;

				cells[3] = price;
				
				Map<Integer, String> categories = new HashMap<Integer, String>();
				
				String tooltip = null;
				
				if (root.has("categories")) {
					JsonArray array = root.get("categories").getAsJsonArray();
					
					for (JsonElement element: array) {
						JsonObject category = element.getAsJsonObject();
						int cId = category.get("id").getAsInt();
						String cName = category.get("description").getAsString();
						
						File file = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\categories\\" + cId + ".png");
						if (file.exists()) {
							categories.put(cId, cName);
						}
					}
				}
				
				games.get(id).features = categories.size();
				
				for (Map.Entry<Integer, String> entry: categories.entrySet()) {
					games.get(id).categories += " " + preapreString(entry.getValue(), true) + " ";
				}
				
				if (!categories.isEmpty()) {
					BufferedImage icons = new BufferedImage(12 + SIZE_FEATURE * categories.size(), SIZE_FEATURE, BufferedImage.TYPE_INT_ARGB);
					Graphics graphics = icons.createGraphics();
					
					List<Integer> cat = new ArrayList<Integer>();
					
					for (int n: categories.keySet()) {
						cat.add(n);
					}
					Collections.sort(cat, cs);
					
					for (int c: cat) {
						if (tooltip == null) {
							tooltip = categories.get(c);
						}
						else {
							tooltip += "<br>" + categories.get(c);
						}
					}
					
					for (int index = 0; index < cat.size(); index++) {
						ImageIcon ii = new ImageIcon(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\categories\\" + cat.get(index) + ".png", categories.get(cat.get(index)));
						graphics.drawImage(ImageHelper.resize(ii, SIZE_FEATURE, SIZE_FEATURE), 6 + SIZE_FEATURE * index, 0, SIZE_FEATURE, SIZE_FEATURE, null);
					}
					
					cells[2] = new ImageIcon(icons);
					
					if (categories.size() > maxCategories) 
						maxCategories = categories.size();
				}
				
				if (tooltip != null) {
					tooltips.put(id, "<html>" + tooltip + "</html>");
				}
			}
			
			data.put(id, cells);
			LauncherUI.progress.addProgress();
		}
		table.getColumnModel().getColumn(2).setMinWidth(12 + SIZE_FEATURE * maxCategories);
		table.getColumnModel().getColumn(2).setMaxWidth(12 + SIZE_FEATURE * maxCategories);
		
		final JScrollPane pane = new JScrollPane(table);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getViewport().setBackground(ColorScheme.PANE_BACKGROUND.color);
		ThemeColor.viewports.add(pane.getViewport());
		pane.setBorder(BorderFactory.createEmptyBorder());
		
		BorderLayout layout = new BorderLayout(0, 0);
		JPanel panel = new JPanel(layout);
		
		JScrollBar scrollbar = new JScrollBar(JScrollBar.VERTICAL) {
			private static final long serialVersionUID = -117624805062527285L;

			@Override
			public Color getBackground() {
				return ColorScheme.TABLE_BACKGROUND.color;
			}
			
		};
		
		scrollbar.setPreferredSize(new Dimension(20, -1));
		scrollbar.setUI(new GamesScrollbar(scrollbar, table));
		scrollbar.setUnitIncrement(32);
		pane.setVerticalScrollBar(scrollbar);

		controls = new ControlTable(this, "Settings");
		
		final JTable info = new JTable(new Object[1][3], new String[] {"a", "b", "c"}) {

			private static final long serialVersionUID = 152936189966264561L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
			@Override
			public Color getBackground() {
				return ColorScheme.WINDOW_BACKGROUND.color;
			}
			
			@Override
			public Color getGridColor() {
				return ColorScheme.GRID.color;
			}
		};
		
		info.getTableHeader().setEnabled(false);
		info.setBorder(BorderFactory.createRaisedBevelBorder());
		
		info.setCellSelectionEnabled(false);
		info.setFocusable(false);
		info.setShowVerticalLines(true);
		info.setRowHeight(SIZE_FEATURE + 2);

		info.getColumnModel().getColumn(0).setCellRenderer(new NametagRenderer(28, ColorScheme.FONT_INFO));
		info.getColumnModel().getColumn(1).setCellRenderer(new PriceRenderer());
		info.getColumnModel().getColumn(2).setCellRenderer(new DiskRenderer());

		info.setValueAt(sorted.size() + " Games installed", 0, 0);
		info.setValueAt(CurrencyTranslator.formatPrice(sum_price, 0, Region.byCode(LauncherUI.config.json.get("region").getAsString()).currency), 0, 1);
		info.setValueAt(DiskFormat.formatBytes(sum_size), 0, 2);
		
		info.getColumnModel().getColumn(1).setMinWidth(SIZE_ICON + 150);
		info.getColumnModel().getColumn(1).setMaxWidth(SIZE_ICON + 150);
		info.getColumnModel().getColumn(2).setMinWidth(SIZE_ICON + 85);
		info.getColumnModel().getColumn(2).setMaxWidth(SIZE_ICON + 85);
		
		panel.add(controls, BorderLayout.NORTH);
		panel.add(scrollbar, BorderLayout.WEST);
		panel.add(pane, BorderLayout.CENTER);
		panel.add(info, BorderLayout.SOUTH);
		add(panel);

		setUndecorated(true);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		setSize((int) (screen.getWidth() / 5.0 * 3.0), (int) (screen.getHeight() / 4.0 * 3.0));
		setLocationRelativeTo(null);
	}

	public void queryTable() {
		String query = controls.search_field.getText();
		int amount = 0;
    	
    	for (int id: sorted) {
			if (LauncherUI.config.json.get("visibility").getAsJsonObject().get(String.valueOf(id)).getAsBoolean()) {
				if (filter(id, query)) {
					amount++;
				}
			}
    	}
    	
    	((DefaultTableModel) table.getModel()).setRowCount(amount);
    	
		int i = 0;
		for (int id: sorted) {
			if (LauncherUI.config.json.get("visibility").getAsJsonObject().get(String.valueOf(id)).getAsBoolean()) {
				if (filter(id, query)) {
					Object[] cells = data.get(id);
					for (int j = 0; j < cells.length; j++) {
						table.setValueAt(cells[j], i, j);
					}
					i++;
				}
			}
		}
	}
	
	private boolean filter(int id, String query) {
		query = preapreString(query, true);
		GameData data = games.get(id);
		
		if (preapreString(data.name, true).contains(query))
			return true;
		
		if (data.categories.contains(query))
			return true;
		
		if (preapreString(data.developers, false).contains(query))
			return true;
		
		return false;
	}

	private String preapreString(String query, boolean spaces) {
		String str = query;
		if (spaces) str = query.replaceAll(" ", "");
		return str.toLowerCase().replaceAll("-", "").replaceAll("_", "").replaceAll(",", "").replaceAll("\\.", "");
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			queryTable();
		}
		super.setVisible(b);
	}
}
