package sense;

import java.awt.Point;
import map.Map;
import javaclient3.structures.PlayerPose2d;

public class Sense {
	public static void sense(Map map, double[] sonarValues, PlayerPose2d pose) {
		Point start = Map.convertPlayerToInternal(pose.getPx(), pose.getPy());
		if (sonarValues != null) {
			for (int i = 0; i < sonarValues.length; i++) {
				senseSensor(map, start, sonarValues[i],
						pose.getPa() + i * Math.toRadians(360 / 16));
			}
		}
	}

	private static void senseSensor(Map map, Point s, double distance,
			double angle) {
		double distance2 = distance / Map.SCALE; // Convert distance to internal
													// map
		// units;
		Point t = new Point((int) Math.floor(distance2 * Math.cos(angle)),
				(int) Math.floor(distance2 * Math.sin(angle)));
		t.x += s.x;
		t.y += s.y;
		boolean WALL = distance < 5 ? true : false;
		Bresenham.line(map, s.x, s.y, t.x, t.y, WALL);

	}

}
