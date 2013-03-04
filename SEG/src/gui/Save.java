package gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Save {
	/**
	 * Save the given image to a PNG file.
	 * @param image The BufferedImage to save
	 * @param filename The filename to save to, including file extension 
	 * @return True if successful, False if error occurred.
	 */
	public static boolean toPNG(BufferedImage image, String filename) {
		File f = new File(filename);
		try {
			if (!ImageIO.write(image, "PNG", f))
				return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
