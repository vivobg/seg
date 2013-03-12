package sense;

import java.awt.Point;
import java.util.ArrayList;

import map.Map;

public class Bresenham {
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
			boolean WALL) {

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
		if (WALL) {
			for (int i = 0; i < points.size() - Map.UNWALKABLE_CELLS; i++) {
				Point p = points.get(i);
				if (!map.isOccupied(p.x, p.y) && !map.isUnwalkable(p.x, p.y))
				map.setValue(p.x, p.y, Map.EMPTY);
			}
			for (int i = (int) (points.size() - Map.UNWALKABLE_CELLS); i < points
					.size() - 1; i++) {
				if (i < 0)
					continue;
				Point p = points.get(i);
				if (!map.isOccupied(p.x, p.y))
				map.setValue(p.x, p.y, Map.UNWALKABLE);
			}
			circle(map,x0, y0, Map.UNWALKABLE_CELLS);
			map.setValue(x0, y0, Map.OCCUPIED);
		} else {
			for (Point p : points) {
				if (!map.isOccupied(p.x, p.y) && !map.isUnwalkable(p.x, p.y))
				map.setValue(p.x, p.y, Map.EMPTY);
			}
		}
	}

	private static void circle(Map map, int x0, int y0, int radius) {
		for(int y=-radius; y<=radius; y++)
		    for(int x=-radius; x<=radius; x++)
		        if(x*x+y*y <= radius*radius && !map.isOccupied(x0+x, y0+y))
		            map.setValue(x0+x, y0+y, Map.UNWALKABLE);
	}

}
