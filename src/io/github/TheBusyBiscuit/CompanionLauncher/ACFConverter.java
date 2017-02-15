package io.github.TheBusyBiscuit.CompanionLauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ACFConverter {
	
	public JsonObject games = new JsonObject();
	
	public GameData convert(File file) throws IOException {
		System.out.println("Reading: " + file.getName());
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		
		String data = "";
		
		String line;
	    while ((line = reader.readLine()) != null) {
	    	data = data + line + "\n";
	    }
	    
	    reader.close();
	    
	    data = data.replace("\"AppState\"\n", "");
	    data = data.replace("\"\n\t\"", "\",\n\t\"");
	    data = data.replace("\"\n\t\t\"", "\",\n\t\t\"");
	    data = data.replace("\"\n\t\t\t\"", "\",\n\t\t\t\"");
	    data = data.replace("\"\t\t\"", "\":\"");
	    data = data.replace("\"\n\t{", "\":\n\t{");
	    data = data.replace("\"\n\t\t{", "\":\n\t\t{");
	    data = data.replace("}\n\t\t\"", "},\n\t\t\"");
	    data = data.replace("}\n\t\"", "},\n\t\"");
	    
	    JsonElement element = null;
	    
		GameData gd = new GameData();
		
		try {
			element = new JsonParser().parse(data);
			gd.manifest = element.getAsJsonObject();
		} catch(IllegalStateException x) {
			System.out.println(" ERROR - " + file.getName() + " could not be converted to the JSON file format.");
			return null;
		}
	    
	    int id = -1;
	    
	    try {
	    	id = element.getAsJsonObject().get("appid").getAsInt();
	    }
	    catch(Exception x) {
	    	id = element.getAsJsonObject().get("appID").getAsInt();;
	    }
	    
	    if (id == -1){
			System.out.println(" ERROR - " + file.getName() + " could not be read as it's missing an 'appid' tag.");
	    	return null;
	    }
	    
	    gd.id = id;
	    gd.name = element.getAsJsonObject().get("name").getAsString();
	    
	    File json = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\manifest\\" + id + ".json");
	    
	    games.addProperty(String.valueOf(id), gd.name);

		System.out.print("  Downloading header.jpg");
	    pullThumbnail(id);

		System.out.print("  Downloading details...");
	    gd.callback = pullAppDetails(id);
	    
	    PrintWriter writer = new PrintWriter(json, "UTF-8");
	    writer.println(data);
	    writer.close();

		System.out.println("  Exporting " + id + ".json");
		
		return gd;
	}
	
	public File pullThumbnail(int id) throws IOException {
		File file = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\thumbnail\\" + id + ".jpg");
		
		if (file.exists()) {
			System.out.println(" 'skip'");
			return file;
		}
		
		long timestamp = System.currentTimeMillis();
		URL website = new URL("http://cdn.akamai.steamstatic.com/steam/apps/" + id + "/header.jpg");
		
		HttpURLConnection connection = (HttpURLConnection) website.openConnection();
        connection.setConnectTimeout(6000);
        connection.addRequestProperty("User-Agent", AppInfo.name + " (by TheBusyBiscuit)");
        connection.setDoOutput(true);
		
        if (connection.getResponseCode() == 200) {
        	ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
     		FileOutputStream fos = new FileOutputStream(file);
     	    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
     	    fos.close();
     	    connection.disconnect();
     	    
     	    System.out.println(" " + (System.currentTimeMillis() - timestamp) + "ms");
        }
        else {
        	System.out.println(" ERROR " + connection.getResponseCode());
        }
	    
	    return file;
	}
	
	public JsonObject pullAppDetails(int id) throws IOException {
		File file = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\callback\\" + id + ".json");
		
		String link = "http://store.steampowered.com/api/appdetails/?appids=" + id + "&cc=" + LauncherUI.config.json.get("region").getAsString() + "&filters=price_overview,developers,publishers,categories,platforms";
		
		try {
			long timestamp = System.currentTimeMillis();
			URL website = new URL(link);

			HttpURLConnection connection = (HttpURLConnection) website.openConnection();
	        connection.setConnectTimeout(2000);
	        connection.addRequestProperty("User-Agent", AppInfo.name + " (by TheBusyBiscuit)");
	        connection.setDoOutput(true);
			
	        if (connection.getResponseCode() == 200) {
			    ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
				FileOutputStream fos = new FileOutputStream(file);
			    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			    fos.close();
			    connection.disconnect();
			    
			    System.out.println(" " + (System.currentTimeMillis() - timestamp) + "ms");
	        }
	        else {
	        	System.out.println(" ERROR " + connection.getResponseCode());
	        }
		}
		catch(Exception x) {
			System.out.println();
			System.out.println(x.getClass().getName() + " : " + link);
			System.out.println();
		}
		
		if (!file.exists()) {
			JsonObject obj = new JsonObject();
			obj.addProperty("success", false);
			return obj;
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String json = "";

		String line;
	    while ((line = reader.readLine()) != null) {
	    	json += line;
	    }
	    
	    reader.close();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
	    PrintWriter writer = new PrintWriter(file);
	    writer.println(gson.toJson(new JsonParser().parse(json)));
	    writer.close();
	    
	    return new JsonParser().parse(json).getAsJsonObject();
	}

	public JsonObject createIndex() throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
	    PrintWriter writer = new PrintWriter(new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\library.json"));
	    writer.println(gson.toJson(games));
	    writer.close();
	    
	    return games;
	}

}
