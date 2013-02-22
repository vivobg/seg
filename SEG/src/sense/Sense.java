package sense;

import java.awt.Point;

import robot.Robot;
import map.Map;
import javaclient3.structures.PlayerPose2d;

public class Sense {
	/**
	 * Scan the current surroundings with the sonar sensors
	 * @param map
	 * @param robot
	 */
	public static void sonarScan(Map map, Robot robot) {
		PlayerPose2d pose = robot.getPose();
		double[] sonarValues = robot.getSonar();
		Point start = Map.convertPlayerToInternal(pose.getPx(), pose.getPy());
		if (sonarValues != null) {
			for (int i = 0; i < sonarValues.length; i++) {
				senseSonarSensor(map, start, sonarValues[i],
						pose.getPa() + i * Math.toRadians(360 / 16));
			}
		}
	}
	/**
	 * Scan the current surroundings with the fiducial sensor
	 * @param map
	 * @param robot
	 */
	public static void fiducialScan(Map map, Robot robot){
		updateFiducialExplored(map, robot);
		throw new UnsupportedOperationException("Not Implemented Yet!");
	}
	/**
	 * Scan the current surroundings with both types of sensors
	 * @param map
	 * @param robot
	 */
	public static void scan360(Map map, Robot robot){
		throw new UnsupportedOperationException("Not Implemented Yet!");
	}

	private static void senseSonarSensor(Map map, Point s, double distance,
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
	
	private static void updateFiducialExplored(Map map, Robot robot){
		throw new UnsupportedOperationException("Not Implemented Yet!");
	}

}
