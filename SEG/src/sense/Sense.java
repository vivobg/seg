package sense;

import java.awt.Point;

import javaclient3.structures.PlayerPose2d;
import map.Map;
import robot.Robot;

public class Sense {
	/**
	 * Scan the current surroundings with the sonar sensors
	 * @param map
	 * @param robot
	 */
	public static void sonarScan(Map map, Robot robot) {
		PlayerPose2d pose;
		double[] sonarValues = null;
		synchronized(robot.sensorLock){
			pose = new PlayerPose2d(robot.x, robot.y, robot.yaw);
			if(robot.getSonar() != null) sonarValues = robot.getSonar().clone();
		}
		//Point start = Map.convertPlayerToInternal(pose.getPx(), pose.getPy());
		if (sonarValues != null) {
			for (int i = 0; i < sonarValues.length; i++) {
				senseSonarSensor(map, pose.getPx(), pose.getPy(), sonarValues[i],
						pose.getPa() +  Math.toRadians( i *360  / 16));
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

	private static void senseSonarSensor(Map map, double sX, double sY, double distance,
			double angle) {
		/*double distance2 = distance / Map.SCALE; // Convert distance to internal
													// map
		// units;
	    Point t = new Point((int) Math.floor(distance2 * Math.cos(angle)),
				(int) Math.floor(distance2 * Math.sin(angle))); */
		// distance = Math.min(distance, 3);
		double tX = distance * Math.cos(angle);
		double tY = distance * Math.sin(angle);
		tX += sX;
		tY += sY;
		Point s = Map.convertPlayerToInternal(sX,sY);
		Point t = Map.convertPlayerToInternal(tX,tY);
		boolean WALL = distance < 5 ? true : false;
		Bresenham.line(map, s.x, s.y, t.x, t.y, WALL);

	}

	private static void updateFiducialExplored(Map map, Robot robot){
		throw new UnsupportedOperationException("Not Implemented Yet!");
	}

}
