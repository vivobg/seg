package explore;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import map.Map;
import robot.Robot;
import search.AStarSearch;
import sense.Sense;

public class ExploreTest {
	/**
	 * Robotless random exploration. To be REMOVED!
	 * @param map
	 * @param start
	 */
	public static void explore(Map map, Point start) {
		List<Point> path = null;
		do {
			path = AStarSearch.dSearch(map, start);
			if (path != null) {
				int size = path.size();
				Point end = path.get(size - 1);
				float found = Math.random() < 0.4 ? Map.EMPTY : Map.WALL;
				map.setValue(end.x, end.y, found,5);
				
				if (found < 0.4) //Target cell is EMPTY, start from it.
					start = path.get(size - 1);
				else //Target cell is unwalkable, start from previous cell(walkable)
					start = size > 2 ? path.get(size - 2) : path.get(0);

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (path != null);
		System.out.println("Exploration finished");
	}
	/**
	 * Explore the map with the given robot
	 * @param map the map instance to write the exploration data to
	 * @param robot the robot to explore with
	 * @param start the starting point of the exploration
	 */
	public static void exploreRobot(Map map,Robot robot, Point start) {
		//System.out.println("Exploration started");
		List<Point> path = null;
		do {
			//scan(robot);
			path = AStarSearch.dSearch(map, start);
			robot.currentPath = path;
			if (path != null) {
				if(path.size() > 3)path = optimizePath2(path);
				robot.currentOptimizedPath = path;
				robot.isFollowing = true;
				for (int i = 1; i<path.size();i++){
					
				
					Point p = path.get(i);
					Point end = path.get(path.size()-1);

					// if (!map.isUnexplored(end.x, end.y)
					// || !AStarSearch.isAvailableCell(end, map)) {
					// break;
					// }
					robot.move(p, end);// Decide if worth moving, before moving
					// try {
					//
					// Thread.sleep(30);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
				}
				start = Map.convertPlayerToInternal(robot.x, robot.y);			

				// try {
				//
				// Thread.sleep(30);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
		} while (path != null);
		robot.isFollowing = false;
		//System.out.println("Exploration finished");

	}
	/**
	 * BROKEN. TO BE REMOVED
	 * @param path
	 * @return
	 */
	private static List<Point> optimizePath(List<Point> path) {
		List<Point> result = new ArrayList<Point>();
		int lastGradient = 100;
		for(int i = 1; i< path.size(); i++)
		{
			Point current = path.get(i);
			Point last = path.get(i-1);
			
			int gradient;
			if(current.x - last.x > 0) gradient = (int) ((current.y - last.y) / (current.x - last.x));
			else gradient = 0;
			if(gradient != lastGradient || i == path.size() -1) result.add(last);
			
			lastGradient = gradient;
		}
		return result;
	}
	
	/**
	 * Optimise the given path by removing middle nodes from straight lines.
	 * The returned path is geometrically identical, but contains far fewer nodes.
	 * @param path the path to optimise
	 * @return the optimised path
	 */
	public static List<Point> optimizePath2(List<Point> path) {
		List<Point> result = new ArrayList<Point>();

		Point initialP = path.get(0);
		Point finalP = path.get(path.size()-1);
		result.add(initialP);
		Point coordA;
		Point coordB;
		

		for (int i = 2; i < path.size()-1; i++){

			 coordA = path.get(i-1);
			 coordB = path.get(i);


			if (coordA.x ==coordB.x || coordA.y == coordB.y){
				//skip node
			}else{
				result.add(coordA);
				for (int c = 0; i < path.size(); i++){
					
				coordA = path.get(i-1);
			    coordB = path.get(i);

					
				if (coordA.x != coordB.x & coordA.y != coordB.y){
					//skip nodes
				}else {
					result.add(coordA);
					break;
				}
				
				
				
				}
			}
	}
	result.add(finalP);
	
	
	//System.out.println("Original Path  " + path.size());
	//System.out.println("Optimized Path  " + result.size());

	return result;
}

		
		
		
	/**
	 * UNUSED, TO BE REMOVED
	 * @param robot
	 */
	public static void scan(Robot robot) {
		for(int i = 0; i < 4; i++)robot.turn(robot.yaw + Math.PI/i);
		
	}

}
