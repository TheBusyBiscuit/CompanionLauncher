package io.github.TheBusyBiscuit.CompanionLauncher.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class ImageHelper {
	
	private static float step = 400F;
	
	public static Image resize(ImageIcon img, int width, int height) {
		int w = img.getIconWidth();
		int h = img.getIconHeight();
		
		Image image = img.getImage();
		
		while (w != width || h != height) {
			if (w > width) {
	            w = (int) (w / step);
	            if (w < width) 
	                w = width;
	        }

	        if (h > height) {
	            h = (int) (h / step);
	            if (h < height) 
	                h = height;
	        }

	        BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g = buffer.createGraphics();
	        g.drawImage(image, 0, 0, w, h, null);
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	        g.dispose();

	        image = buffer;
		}
		
		return image;
	}

}
