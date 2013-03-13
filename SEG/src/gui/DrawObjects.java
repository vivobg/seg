package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javaclient3.structures.PlayerPose2d;
import map.Map;
import map.VerticalArray;
import robot.Robot;
import sense.GarbageItem;

public class DrawObjects {
	public static final int BLOCK_SIZE = 3;
	public static final Color COLOR_WALL = Color.BLACK;
	public static final Color COLOR_EMPTY = Color.WHITE;
	public static final Color COLOR_UNEXPLORED = Color.GRAY;
	// Too close to a wall
	public static final Color COLOR_UNWALKABLE = Color.CYAN;
	public static final Color COLOR_PATH = Color.ORANGE;
	public static final Color COLOR_PATH_OPTIMIZED = Color.ORANGE.darker();
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
				boolean draw = false;
				if (map.isEmpty(x, y)){
					g2.setColor(COLOR_EMPTY);
					draw = true;
				}
				else if (map.isUnexplored(x, y)){
					g2.setColor(COLOR_UNEXPLORED);
					draw = true;
				}
				else if (map.isOccupied(x, y)){
					g2.setColor(COLOR_WALL);
					draw = true;
				}
				else if (map.isBuffer(x, y)){
					g2.setColor(COLOR_UNWALKABLE);
					draw = true;
				}
				if (draw)
				g2.fillRect((center.x + x) * BLOCK_SIZE, (center.y - y)
						* BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			}

		}
		// Negative X
		for (int x = 1; x < map.getMinXSize(); x++) {
			VerticalArray vert = map.getVertical(-x);
			for (int y = (vert.getNegSize() - 1) * -1; y < vert.getPosSize(); y++) {
				boolean draw = false;
				if (map.isEmpty(-x, y)){
					g2.setColor(COLOR_EMPTY);
					draw = true;
				}
				else if (map.isUnexplored(-x, y)){
					g2.setColor(COLOR_UNEXPLORED);
					draw = true;
				}
				else if (map.isOccupied(-x, y)){
					g2.setColor(COLOR_WALL);
					draw = true;
				}
				else if(map.isBuffer(-x, y)){
					g2.setColor(COLOR_UNWALKABLE);
					draw = true;
				}
				if (draw)
				g2.fillRect((center.x - x) * BLOCK_SIZE, (center.y - y)
						* BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			}
		}

	}

	public static void drawPaths(Map map, Graphics2D g2, Point size) {

		Point center = new Point(size.x / 2 / BLOCK_SIZE, size.y / 2
				/ BLOCK_SIZE);
		for (Robot bot : map.robotList) {

			// Unoptimized path
			List<Point> opPath = bot.currentPath;
			if (opPath == null || !bot.isFollowing)
				continue;
			g2.setColor(COLOR_PATH);
			for (Point p : opPath) {
				int x = p.x;
				int y = p.y; // System.out.println(x + " " + y);
				g2.fillRect((center.x + x) * BLOCK_SIZE, (center.y - y)
						* BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			}
			g2.setColor(COLOR_PATH_START);
			Point start = opPath.get(0);
			g2.fillRect((center.x + start.x) * BLOCK_SIZE, (center.y - start.y)
					* BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			g2.setColor(COLOR_PATH_FINISH);
			Point finish = opPath.get(opPath.size() - 1);
			g2.fillRect((center.x + finish.x) * BLOCK_SIZE,
					(center.y - finish.y) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

			// Optimized path
			opPath = bot.currentOptimizedPath;
			if (opPath == null || !bot.isFollowing)
				continue;
			g2.setColor(COLOR_PATH_OPTIMIZED);
			for (int i = 0; i < opPath.size() - 1; i++) {
				Point s = opPath.get(i);
				Point t = opPath.get(i + 1);
				List<Point> missing = line(s.x, s.y, t.x, t.y);
				for (Point p : missing) {
					g2.fillRect((center.x + p.x) * BLOCK_SIZE, (center.y - p.y)
							* BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
				}
			}
		}
	}

	public static List<Point> line(int x0, int y0, int x1, int y1) {

		int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
		int dy = Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
		int err = (dx > dy ? dx : -dy) / 2, e2;

		List<Point> points = new ArrayList<Point>();
		for (;;) {
			points.add(new Point(x0, y0));
			if (x0 == x1 && y0 == y1)
				break;
			e2 = err;
			if (e2 > -dx) {
				err -= dy;
				x0 += sx;
			}
			if (e2 < dy) {
				err += dx;
				y0 += sy;
			}
		}
		return points;
	}


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
