package explore;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import map.Map;
import robot.Robot;
import search.AStarSearch;

public class Explore {
	/**
	 * Explore the map with the given robot
	 * @param map the map instance to write the exploration data to
	 * @param robot the robot to explore with
	 * @param start the starting point of the exploration
	 */
	public static void explore(Map map,Robot robot, Point start) {
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
	

	
}
