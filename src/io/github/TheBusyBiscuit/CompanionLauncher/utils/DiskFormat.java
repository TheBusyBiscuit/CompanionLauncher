package io.github.TheBusyBiscuit.CompanionLauncher.utils;

import java.text.DecimalFormat;

public class DiskFormat {
	
	public static String formatBytes(long bytes) {
		double tera = bytes / 1099511627776.0;
		
		if (tera > 1) {
			return fix(tera) + " TB";
		}
		else {
			double giga = bytes / 1073741824.0;
			
			if (giga > 1) {
				return fix(giga) + " GB";
			}
			else {
				double mega = bytes / 1048576.0;
				if (mega > 1) {
					return fix(mega) + " MB";
				}
				else {
					double kilo = bytes / 1024.0;
					if (mega > 1) {
						return fix(kilo) + " KB";
					}
					else {
						return fix(bytes) + " B";
					}
				}
			}
		}
	}
	
	public static double fix(double amount) {
		StringBuilder format = new StringBuilder("##");
		for (int i = 0; i < 2; i++) {
			if (i == 0) format.append(".");
			format.append("#");
		}
		return Double.valueOf(new DecimalFormat(format.toString()).format(amount).replace(",", "."));
	}

}
