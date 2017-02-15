package io.github.TheBusyBiscuit.CompanionLauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Config {
	
	public JsonObject json;
	
	public Config() {
		try {
			this.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void load() throws IOException {
	    File file = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\config.json");
	    if (file.exists()) {
	    	BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String data = "";

			String line;
		    while ((line = reader.readLine()) != null) {
		    	data += line;
		    }
		    
		    reader.close();
			
			json = new JsonParser().parse(data).getAsJsonObject();
	    }
	    else {
	    	LauncherUI.fresh_install = true;
	    	json = new JsonObject();
	    	
	    	json.addProperty("region", "us");
	    	json.addProperty("sorting", 0);
	    	json.add("visibility", new JsonObject());
	    	json.add("colors", new JsonObject());
	    	json.add("directories", new JsonArray());
	    	
	    	save();
	    }
	}
	
	public void save() throws FileNotFoundException {
	    File file = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\config.json");
	    
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
	    PrintWriter writer = new PrintWriter(file);
	    writer.println(gson.toJson(json));
	    writer.close();
	}

}
