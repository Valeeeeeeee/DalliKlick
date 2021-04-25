package util;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Utilities {
	
	public static void log(Object object) {
		System.out.println(object);
	}
	
	public static void log() {
		System.out.println();
	}
	
	public static BufferedImage loadImage(String fileName) {
		BufferedImage bImage = null;
		try {
			bImage = ImageIO.read(new File(fileName));
		} catch (Exception e) {
			log(String.format("The image at '%s' could not be loaded.", fileName));
			e.printStackTrace();
		}
		
		return bImage;
	}
}
