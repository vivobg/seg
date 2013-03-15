package sense;

import java.awt.List;
import java.awt.Point;

import javaclient3.structures.PlayerPose2d;
import map.Map;
import robot.Robot;
import search.AStarSearch;

public class Sense {
	/**
	 * Scan the current surroundings with the sonar sensors
	 * @param map
	 * @param robot
	 */
	
	public static java.util.List<Point> adjacentPoints;
	
	public static void sonarScan(Map map, Robot robot) {
		PlayerPose2d pose;
		double[] sonarValues = null;
		synchronized(robot.sensorLock){
				pose = new PlayerPose2d(robot.x, robot.y, robot.yaw);
				if(robot.getSonar() != null) sonarValues = robot.getSonar().clone();
		}
//		while(!robot.pos2D.isDataReady() ){};
//		pose = robot.pos2D.getData().getPos();
//		robot.x = pose.getPx();
//		robot.y = pose.getPy();
//		robot.yaw = pose.getPa();
//		
//		while(!robot.sonar.isDataReady() ){};
//		sonarValues = robot.sonar.getData().getRanges();
		
		
		//Point start = Map.convertPlayerToInternal(pose.getPx(), pose.getPy());
		if (sonarValues != null) {
			for (int i = 0; i < sonarValues.length-3; i++) {
				senseSonarSensor(robot, map, pose.getPx(), pose.getPy(), sonarValues[i],
						pose.getPa() + Math.toRadians(-30 + i * 5), pose);

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
	private static void senseSonarSensor(Robot robot, Map map, double sX, double sY, double distance,
			double angle,PlayerPose2d pose) {

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
		Bresenham.line(map, s.x, s.y, t.x, t.y, WALL, pose, robot.index);
	}

	private static void updateFiducialExplored(Map map, Robot robot){
		throw new UnsupportedOperationException("Not Implemented Yet!");
	}
	/**
	 * 
	 * @param map
	 * @param robot
	 * @param radius
	 * @return
	 */
	public static boolean sensingAnotherRobot(Map map, Robot robot, Point target) {
		int radius = (int) ((Robot.ROBOT_SIZE+0.7) / Map.SCALE);

		for (int i = 0; i < map.robotList.size(); i++) {
			Robot bot = map.robotList.get(i);
			if (!bot.equals(robot)) {
				
				Point center = bot.getRobotPosition();
				if (    Math.pow((target.x - center.x),2)  + Math.pow((target.y - center.y),2)    < radius*radius  ){
					return true;
				} 

			}

		}
		
		return false;
	}

}






//Point robot2 =  map.getRobotList().get(1).getRobotPosition();				
//System.out.println(map.getRobotList().get(i).getRobotPosition());
//Point robotTAL = map.getRobotList().get(i).getRobotPosition();
//adjacentPoints = AStarSearch.getAdjacentPoints(map, robotTAL, true);
//System.out.println(adjacentPoints);
