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
		//Point start = Map.convertPlayerToInternal(pose.getPx(), pose.getPy());
		if (sonarValues != null) {
			for (int i = 0; i < sonarValues.length; i++) {
				senseSonarSensor(robot, map, pose.getPx(), pose.getPy(), sonarValues[i],
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

	private static void senseSonarSensor(Robot robot, Map map, double sX, double sY, double distance,
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
		//boolean WALL = distance < 5 ? true : false;
		
		
		
		sensingAnotherRobot( map, robot);
	    boolean WALL = obstacle(distance,t.x ,t.y);
		Bresenham.line(map, s.x, s.y, t.x, t.y, WALL);

	}

	private static void updateFiducialExplored(Map map, Robot robot){
		throw new UnsupportedOperationException("Not Implemented Yet!");
	}
	
	public static void sensingAnotherRobot(Map map, Robot robot){
		
		
			
			for (int i = 0; i < map.robotList.size(); i++){
				
				
				
				
				//Point robot2 =  map.getRobotList().get(1).getRobotPosition();
				
				//System.out.println(map.getRobotList().get(i).getRobotPosition());
				Point robotTAL = map.getRobotList().get(i).getRobotPosition();
				adjacentPoints = AStarSearch.getAdjacentPoints(map, robotTAL, true);
				//System.out.println(adjacentPoints);
				
				
			}
			
		//}
		
		
		
		
		
	}
	public static boolean obstacle(double distance, int tX, int tY) {
	
		
		
		if (distance < 5){
			
			for (int i = 0; i< adjacentPoints.size(); i++){
				// it is a robot
				
				System.out.println(adjacentPoints.get(i).x);
				System.out.println("Valor de tX" + tX);
				
				if (tX==adjacentPoints.get(i).x && tY==adjacentPoints.get(i).y){
					System.out.println("EH ROBOTTT");
					return false;
				}				
			} 
			//it is a wall
			return true;
			}
		//if distance is 5, there is no wall
		return false;
	}
	


}
