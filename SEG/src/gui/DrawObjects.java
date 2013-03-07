package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import javaclient3.structures.PlayerPose2d;
import map.Map;
import map.VerticalArray;
import robot.Robot;
import sense.GarbageItem;

public class DrawObjects {
	public static final int BLOCK_SIZE = 5;
	public static final Color COLOR_WALL = Color.BLACK;
	public static final Color COLOR_EMPTY = Color.WHITE;
	public static final Color COLOR_UNEXPLORED = Color.GRAY;
	// Too close to a wall
	public static final Color COLOR_UNWALKABLE = Color.RED.darker();
	public static final Color COLOR_PATH = Color.ORANGE;
	public static final Color COLOR_PATH_START = Color.GREEN;
	public static final Color COLOR_PATH_FINISH = Color.MAGENTA;
	public static final Color[] ROBOT_COLORS = { Color.RED, Color.YELLOW,
			Color.BLUE };
	public static final Color GARGABE_COLOR = Color.MAGENTA;
	public static final float GARBAGE_SIZE = 0.2f;

	/**
	 * Clear the given graphical context
	 * 
	 * @param g2
	 *            The graphics object to draw with
	 * @param size
	 *            The size of the graphic, as Graphics2D does not provide size
	 */
	public static void clear(Graphics2D g2, Point size) {
		g2.setColor(COLOR_UNEXPLORED);
		g2.fillRect(0, 0, size.x, size.y);
	}

	/**
	 * Naively draws the map with the given graphics context
	 * 
	 * @param g2
	 *            The graphics object to draw with
	 * @param size
	 *            The size of the graphic, as Graphics2D does not provide it
	 */
	public static void drawMap(Map map, Graphics2D g2, Point size) {
		int minX = map.getMinXSize();
		int minY = map.getMinYSize();
		int maxX = map.getMaxXSize();
		int maxY = map.getMaxYSize();

		Point center = new Point(size.x / 2 / BLOCK_SIZE, size.y / 2 / BLOCK_SIZE);

		// Iterate from lowest to highest X
		for (int x = -minX; x <= maxX; x++) {
			// Iterate from lowest to highest Y
			for (int y = -minY; y <= maxY; y++) {
				if (map.isEmpty(x, y))
					g2.setColor(COLOR_EMPTY);
				else if (map.isUnexplored(x, y))
					g2.setColor(COLOR_UNEXPLORED);
				else if (map.isOccupied(x, y))
					g2.setColor(COLOR_WALL);
				else
					g2.setColor(COLOR_UNWALKABLE);
				g2.fillRect((center.x + x - 1) * BLOCK_SIZE, (center.y - y)
						* BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

			}
		}

	}

	/**
	 * Smartly draws the map with the given graphics context. Faster than
	 * drawMap() as it avoids the map throwing exceptions on cells that are not
	 * stored.
	 * 
	 * @param g2
	 *            The graphics object to draw with
	 * @param size
	 *            The size of the graphic, as Graphics2D does not provide it
	 */
	public static void drawMapDeep(Map map, Graphics2D g2, Point size) {

		Point center = new Point(size.x / 2 / BLOCK_SIZE, size.y / 2 / BLOCK_SIZE);

		// positive X
		for (int x = 0; x < map.getMaxXSize(); x++) {
			VerticalArray vert = map.getVertical(x);
			for (int y = (vert.getNegSize() - 1) * -1; y < vert.getPosSize(); y++) {
				if (map.isEmpty(x, y))
					g2.setColor(COLOR_EMPTY);
				else if (map.isUnexplored(x, y))
					g2.setColor(COLOR_UNEXPLORED);
				else if (map.isOccupied(x, y))
					g2.setColor(COLOR_WALL);
				else
					g2.setColor(COLOR_UNWALKABLE);
				g2.fillRect((center.x + x) * BLOCK_SIZE, (center.y - y)
						* BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			}

		}
		// Negative X
		for (int x = 1; x < map.getMinXSize(); x++) {
			VerticalArray vert = map.getVertical(-x);
			for (int y = (vert.getNegSize() - 1) * -1; y < vert.getPosSize(); y++) {
				if (map.isEmpty(-x, y))
					g2.setColor(COLOR_EMPTY);
				else if (map.isUnexplored(-x, y))
					g2.setColor(COLOR_UNEXPLORED);
				else if (map.isOccupied(-x, y))
					g2.setColor(COLOR_WALL);
				else
					g2.setColor(COLOR_UNWALKABLE);
				g2.fillRect((center.x - x) * BLOCK_SIZE, (center.y - y)
						* BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			}
		}

	}

	/*
	 * public void drawPath(List<Point> path) { int minX = map.getMinXSize();
	 * int centerX = minX > 0 ? minX : 1;
	 * 
	 * int centerY = map.getMaxYSize(); if (path == null) return; Graphics2D g2
	 * = (Graphics2D) this.getGraphics(); g2.setColor(COLOR_PATH); for (Point p
	 * : path) { int x = p.x; int y = p.y; // System.out.println(x + " " + y);
	 * g2.fillRect((centerX + x) * blockSize, (centerY - y) blockSize,
	 * blockSize, blockSize); } g2.setColor(COLOR_PATH_START); Point start =
	 * path.get(0); g2.fillRect((centerX + start.x) * blockSize, (centerY -
	 * start.y) blockSize, blockSize, blockSize);
	 * g2.setColor(COLOR_PATH_FINISH); Point finish = path.get(path.size() - 1);
	 * g2.fillRect((centerX + finish.x) * blockSize, (centerY - finish.y)
	 * blockSize, blockSize, blockSize); g2.dispose(); this.revalidate();
	 * this.repaint();
	 * 
	 * }
	 */

	/**
	 * Draw robots with the given graphics context
	 * 
	 * @param g2
	 *            The graphics object to draw with
	 */
	public static void drawRobots(Map map, Graphics2D g2, Point size) {
		Point center = new Point(size.x / 2 / BLOCK_SIZE, size.y / 2 / BLOCK_SIZE);
		// int centerX = map.getMinXSize();
		// int centerY = map.getMaxYSize();
		int robotSize = (int) (BLOCK_SIZE * (Robot.ROBOT_SIZE / Map.SCALE));
		for (int i = 0; i < map.robotList.size(); i++) {
			Robot bot = map.robotList.get(i);
			g2.setColor(ROBOT_COLORS[i]);
			PlayerPose2d pose = bot.getPose();
			Point rC = Map.convertPlayerToInternal(pose.getPx(), pose.getPy());
			int angle = (int) Math.toDegrees(pose.getPa());
			int startAngle = angle + (360 - 300) / 2;
			g2.fillArc((center.x + rC.x) * BLOCK_SIZE - robotSize / 2,
					(center.y - rC.y) * BLOCK_SIZE - robotSize / 2, robotSize,
					robotSize, startAngle, 300);
			g2.setColor(Color.BLACK);
			g2.drawArc((center.x + rC.x) * BLOCK_SIZE - robotSize / 2,
					(center.y - rC.y) * BLOCK_SIZE - robotSize / 2, robotSize,
					robotSize, startAngle, 300);
		}
	}

	public static void drawGarbage(Map map, Graphics2D g2, Point size) {
		Point center = new Point(size.x / 2 / BLOCK_SIZE, size.y / 2 / BLOCK_SIZE);
		int garbageSize = (int) (BLOCK_SIZE * (GARBAGE_SIZE / Map.SCALE));
		g2.setColor(GARGABE_COLOR);
		for (int i = 0; i < map.garbageListArray.size(); i++) {
			GarbageItem garbage = map.garbageListArray.get(i);
			g2.fillOval((center.x + garbage.getPoint().x) * BLOCK_SIZE
					- garbageSize / 2, (center.y - garbage.getPoint().y)
					* BLOCK_SIZE - garbageSize / 2, garbageSize, garbageSize);
		}
	}
}
