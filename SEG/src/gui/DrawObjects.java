package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import javaclient3.structures.PlayerPose2d;
import map.GarbageItem;
import map.Map;
import map.VerticalArray;
import robot.Robot;
/**
 * Class providing static methods to draw the map, robots, garbages and paths
 * to either a Java Swing component, or a Buffered image, depending on the provided
 * Graphics2D instance.
 *
 */
public class DrawObjects {
	public static final int BLOCK_SIZE = 1;
	private static final Color COLOR_WALL = Color.BLACK;
	private static final Color COLOR_EMPTY = Color.WHITE;
	private static final Color COLOR_UNEXPLORED = Color.GRAY;
	// Too close to a wall
	private static final Color COLOR_UNWALKABLE = Color.CYAN;
	private static final Color COLOR_FAR_WALL = Color.BLACK;
	private static final Color COLOR_PATH = Color.ORANGE;
	private static final Color COLOR_PATH_START = Color.GREEN;
	private static final Color COLOR_PATH_FINISH = Color.MAGENTA;
	private static final Color[] ROBOT_COLORS = { Color.RED, Color.YELLOW,
			Color.BLUE };
	private static final Color GARGABE_COLOR = Color.RED;
	private static final Color GARGABE_COLOR_COLLECTED = Color.GREEN;
	private static final float GARBAGE_SIZE = 0.2f;
	private static final Color COLOR_PATH_FINISH_EXPLORED = Color.BLUE;
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
	 * Naively draws the map with the given graphics context. Slower than drawMapDeep
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
				else if (map.isFarWall(x, y)){
					g2.setColor(COLOR_FAR_WALL);
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
				else if (map.isFarWall(-x, y)){
					g2.setColor(COLOR_FAR_WALL);
					draw = true;
				}
				if (draw)
				g2.fillRect((center.x - x) * BLOCK_SIZE, (center.y - y)
						* BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			}
		}

	}
	/**
	 * Draw the path that each robot is currently following.
	 * @param map the map instance
	 * @param g2 the graphics context to draw with
	 * @param size the size of the "canvas" on which to draw, as Graphics2D does not provide size information
	 */
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
			
			Point finish = opPath.get(opPath.size() - 1);
			if (map.isUnexplored(finish.x, finish.y))
				g2.setColor(COLOR_PATH_FINISH);
			else g2.setColor(COLOR_PATH_FINISH_EXPLORED);
			
			g2.fillRect((center.x + finish.x) * BLOCK_SIZE,
					(center.y - finish.y) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
			
		}
	}

	/**
	 * Draw robots with the given graphics context
	 * 
	 * @param g2
	 *            The graphics object to draw with
	 */
	public static void drawRobots(Map map, Graphics2D g2, Point size) {
		Point center = new Point(size.x / 2 / BLOCK_SIZE, size.y / 2 / BLOCK_SIZE);
		int robotSize = (int) (BLOCK_SIZE * (Robot.ROBOT_SIZE / Map.SCALE) * 1.3);
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
	/**
	 * Draw the discovered garbage objects, from the given map
	 * @param map the map instance
	 * @param g2 the graphics context to draw with
	 * @param size the size of the canvas, as Graphics2D does not provide size information
	 */
	public static void drawGarbage(Map map, Graphics2D g2, Point size) {
		Point center = new Point(size.x / 2 / BLOCK_SIZE, size.y / 2 / BLOCK_SIZE);
		int garbageSize = (int) (BLOCK_SIZE * (GARBAGE_SIZE / Map.SCALE));
		
		for (int i = 0; i < map.garbageListArray.size(); i++) {
			GarbageItem garbage = map.garbageListArray.get(i);
			if(garbage.isCollected) g2.setColor(GARGABE_COLOR_COLLECTED);
			else g2.setColor(GARGABE_COLOR);
			g2.fillOval((center.x + garbage.getPoint().x) * BLOCK_SIZE
					- garbageSize / 2, (center.y - garbage.getPoint().y)
					* BLOCK_SIZE - garbageSize / 2, garbageSize, garbageSize);
		}
	}
	
}
