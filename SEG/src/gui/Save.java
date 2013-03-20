package gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import map.Map;
/**
 * A class providing static methods to save a Map instance
 * to a PNG image file
 *
 */
public class Save {
	/**
	 * Save the given image to a PNG file.
	 * @param image The BufferedImage to save
	 * @param filename The filename to save to, including file extension 
	 * @return True if successful, False if error occurred.
	 */
	private static boolean toPNG(BufferedImage image, String filename) {
		File f = new File(filename);
		try {
			if (!ImageIO.write(image, "PNG", f))
				return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Save the given map to a PNG file
	 * 
	 * @param map
	 *            The map instance to use
	 * @param filename
	 *            the filename to use for the image, including file extension
	 * @return
	 */
	public static boolean toPNG(Map map, String filename) {
		int minX = map.getMinXSize();
		int minY = map.getMinYSize();
		int maxX = map.getMaxXSize();
		int maxY = map.getMaxYSize();

		BufferedImage mapImage = new BufferedImage((minX + maxX)
				* DrawObjects.BLOCK_SIZE, (minY + maxY)
				* DrawObjects.BLOCK_SIZE, BufferedImage.TYPE_INT_RGB);
		Point size = new Point(mapImage.getWidth(), mapImage.getHeight());
		Graphics2D g2 = mapImage.createGraphics();
		DrawObjects.clear(g2, size);
		// drawMap(g2, size);
		DrawObjects.drawMapDeep(map, g2, size);
		DrawObjects.drawRobots(map, g2, size);
		DrawObjects.drawGarbage(map, g2, size);
		g2.dispose();
		return toPNG(mapImage, filename);
	}
}
