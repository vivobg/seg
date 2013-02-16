package sense;

import java.awt.Point;
import java.util.ArrayList;

import map.Map;

public class Bresenham {
	/**
	 * inse
	 * @param map
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @param WALL
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
				map.setValue(p.x, p.y, Map.EMPTY);
			}
			for (int i = (int) (points.size() - Map.UNWALKABLE_CELLS); i < points
					.size() - 1; i++) {
				if (i < 0)
					continue;
				Point p = points.get(i);
				map.setValue(p.x, p.y, Map.UNWALKABLE);
			}
			map.setValue(x0, y0, Map.OCCUPIED);
		} else {
			for (Point p : points) {
				map.setValue(p.x, p.y, Map.EMPTY);
			}
		}
	}

}
