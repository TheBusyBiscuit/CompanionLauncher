package io.github.TheBusyBiscuit.CompanionLauncher.regions;

public class Region {
	
	public String name;
	public String code;
	public String currency;
	public String symbol;
	
	public Region(String name, String code, String currency, String symbol) {
		this.name = name;
		this.code = code;
		this.currency = currency;
		this.symbol = symbol;
	}
	
	public static Region byCode(String code) {
		for (Region r: CurrencyTranslator.regions) {
			if (r.code.equals(code))
				return r;
		}
		return null;
	}

}
