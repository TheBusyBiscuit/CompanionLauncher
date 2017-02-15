package io.github.TheBusyBiscuit.CompanionLauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class AssetsLoader {
	
	public void load() throws IOException {
		loadImage("disk");
		loadImage("edit");
		loadImage("games");
		loadImage("icon");
		loadImage("minimize");
		loadImage("overlay");
		loadImage("price");
		loadImage("search");
		loadImage("settings");
		loadImage("sort_down");
		loadImage("sort_up");
		
		loadCategory(1);
		loadCategory(2);
		loadCategory(9);
		loadCategory(18);
		loadCategory(22);
		loadCategory(23);
		loadCategory(28);
		loadCategory(29);
		loadCategory(30);
		loadCategory(35);
	}
	
	public void loadCategory(int id) throws IOException {
		File file = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\categories\\" + id + ".png");
		InputStream stream = AssetsLoader.class.getResourceAsStream("assets/categories/" + id + ".png");
		
		ReadableByteChannel rbc = Channels.newChannel(stream);
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		
		stream.close();
	}
	
	public void loadImage(String name) throws IOException {
		File file = new File(System.getenv("APPDATA") + "\\." + AppInfo.name + "\\common\\" + name + ".png");
		InputStream stream = AssetsLoader.class.getResourceAsStream("assets/" + name + ".png");
		
		ReadableByteChannel rbc = Channels.newChannel(stream);
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		
		stream.close();
	}

}
