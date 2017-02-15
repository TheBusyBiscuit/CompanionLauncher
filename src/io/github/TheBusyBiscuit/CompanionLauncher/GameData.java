package io.github.TheBusyBiscuit.CompanionLauncher;

import com.google.gson.JsonObject;

public class GameData {
	
	public int id;
	public String name;
	public String categories = "";
	public String developers = "";
	public int features = 0;
	public int price = 0;
	public long size = 0;
	
	public JsonObject manifest;
	public JsonObject callback;

}
