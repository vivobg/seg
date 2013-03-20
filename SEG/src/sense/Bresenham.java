package sense;

import java.awt.Point;
import java.util.ArrayList;

import javaclient3.structures.PlayerPose2d;

import map.Map;
/**
 * Class providing static methods that have algorithms
 * based on Bresenham, or generate a circle
 *
 */
public class Bresenham {
	private static final double MAP_SCALE = 0.1;

	/**
	 * Draws a line onto the map, with the given internal map coordinates
	 * 
	 * @param map
	 *            The Map instance to work with
	 * @param x0
	 *            The X coordinate of the start point
	 * @param y0
	 *            The Y coordinate of the start point
	 * @param x1
	 *            The X coordinate of the end point
	 * @param y1
	 *            The Y coordinate of the end point
	 * @param WALL
	 *            A flag to decide whether the line ends with a wall, or is
	 *            completely empty
	 */
	public static void line(Map map, int x0, int y0, int x1, int y1,
			boolean WALL, PlayerPose2d pose, int robotIndex) {
		double rX = pose.getPx();
		double rY = pose.getPy();
		Point robotPoint = Map.convertPlayerToInternal(rX, rY);
		ArrayList<Point> points = bresenhamLine(x0, y0, x1, y1);

		if (WALL) {
			double distance = Double.MAX_VALUE;
			for (int i = 0; i < points.size() - Map.BUFFER_CELLS; i++) {
				Point p = points.get(i);
				//if (!map.isBuffer(p.x, p.y))
					// Only update, if cell was observed from a closer range.
				distance = Map.calculateSonarDistance(p, robotPoint);// Player
				// Only update, if cell was observed from a closer range.
				if (!map.isBuffer(p.x, p.y)
						&& distance+MAP_SCALE < map.getSonarDistance(p.x, p.y)) {
					map.setValue(p.x, p.y, Map.EMPTY, distance);
				}
				// If buffer, do not change type, but update distance
				else if (map.isBuffer(p.x, p.y)
						&& distance+MAP_SCALE < map.getSonarDistance(p.x, p.y)) {
					map.setValue(p.x, p.y, Map.BUFFER, distance);
				}

			}
			
			Point p = new Point(x1, y1);
			distance = Map.calculateSonarDistance(p, robotPoint);
			if (distance+MAP_SCALE < Map.FAR_WALL && !Sense.sensingAnotherRobot(map, map.robotList.get(robotIndex), new Point(x1,y1)))
				circle(map,x1, y1, Map.BUFFER_CELLS);
			
			if (distance+MAP_SCALE  < map.getSonarDistance(x1, y1)/* || map.getSonarDistance(x1, y1) < 0.3*/){
				map.setValue(x1, y1, Map.WALL, distance);
			}
		} else {
			for (int i = 0; i < points.size() - Map.BUFFER_CELLS; i++) {
				Point p = points.get(i);
				
				double distance = Map.calculateSonarDistance(p, robotPoint);
				if (!map.isBuffer(p.x, p.y) && distance+MAP_SCALE  < map.getSonarDistance(p.x, p.y))
					map.setValue(p.x, p.y, Map.EMPTY,  distance);
				//If buffer, do not change type, but update distance
				else if (map.isBuffer(p.x, p.y) && distance+MAP_SCALE  < map.getSonarDistance(p.x, p.y)){
					map.setValue(p.x, p.y, Map.BUFFER,  distance);
				}
			}
		}
	}
	/**
	 * The Bresenham Line algorithm
	 * @param x0 x coordinate of start point
	 * @param y0 y coordinate of start point
	 * @param x1 x coordinate of end point
	 * @param y1 y coordinate of end point
	 * @return the list of points between the start and end points
	 */
	public static ArrayList<Point> bresenhamLine(int x0, int y0, int x1, int y1){
		int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
		int dy = Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
		int err = (dx > dy ? dx : -dy) / 2, e2;

		ArrayList<Point> points = new ArrayList<Point>();
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
	 * Draw a circle onto the map, with the given center and radius
	 * @param map the map to draw to
	 * @param x0 the x coordinate of the center of the circle
	 * @param y0 the y coordinate of the center of the circle
	 * @param radius the radius of the circle
	 */
	public static void circle(Map map, int x0, int y0, int radius) {
		for(int y=-radius; y<=radius; y++)
		    for(int x=-radius; x<=radius; x++)
				if (x * x + y * y <= radius * radius
						&& !map.isOccupied(x0 + x, y0 + y) && !map.isFarWall(x0 + x, y0 + y)) {
					// current distance or 9.5 - Buffer is calculated, not sensed.
					double distance = Math.min(
							map.getSonarDistance(x0 + x, y0 + y), 9.5);
					map.setValue(x0 + x, y0 + y, Map.BUFFER, distance);
				}
	}

}
