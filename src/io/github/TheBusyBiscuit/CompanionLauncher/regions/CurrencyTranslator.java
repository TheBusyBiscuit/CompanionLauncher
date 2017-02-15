package io.github.TheBusyBiscuit.CompanionLauncher.regions;

import java.util.ArrayList;
import java.util.List;

import io.github.TheBusyBiscuit.CompanionLauncher.utils.DiskFormat;

public class CurrencyTranslator {
	
	public static List<Region> regions = new ArrayList<Region>();
	
	static {
		regions.add(new Region("Europe", "de", "EUR", "\u20AC"));
		regions.add(new Region("USA", "us", "USD", "$"));
		regions.add(new Region("Great Britain", "gb", "GBP", "\u20A4"));
		regions.add(new Region("Canada", "ca", "CAD", "$"));
		regions.add(new Region("New Zealand", "nz", "NZD", "$"));
		regions.add(new Region("China", "cn", "CNY", "\u00A5"));
		regions.add(new Region("Japan", "jp", "JPY", "\u00A5"));
		regions.add(new Region("India", "in", "INR", " INR"));
		regions.add(new Region("Russia", "ru", "RUB", " RUB"));
		regions.add(new Region("Turkey", "tr", "TRY", " TRY"));
		regions.add(new Region("Switzerland", "ch", "CHF", " CHF"));
		regions.add(new Region("Thailand", "th", "THB", "\u0E3F"));
		regions.add(new Region("Mexico", "mx", "MXN", "\u20B1"));
	}
	
	public static String translate(String currency) {
		
		for (Region region: regions) {
			if (currency.equals(region.currency)) return region.symbol;
		}
		
		return "";
	}
	
	public static String formatPrice(int price, int discount, String currency) {
		String d = String.valueOf(DiskFormat.fix(price / 100.0));
		
		if (d.contains(".") && d.split("\\.")[1].length() == 1) {
			d += "0";
		}
		
		String str = d + " " + CurrencyTranslator.translate(currency);
		
		if (discount > 0)
			str += " (- " + discount + "%)";
		
		return str;
	}

}
