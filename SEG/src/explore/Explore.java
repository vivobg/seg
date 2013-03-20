package explore;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import map.Map;
import robot.Robot;
import search.Search;

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
			path = Search.dSearch(map, start);
			robot.currentPath = path;
			if (path != null) {
				if(path.size() > 3)path = Search.optimizePath(path);
				robot.currentOptimizedPath = path;
				robot.isFollowing = true;
				for (int i = 1; i<path.size();i++){
					
				
					Point p = path.get(i);
					Point end = path.get(path.size()-1);

					// if (!map.isUnexplored(end.x, end.y)
					// || !Search.isAvailableCell(end, map)) {
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

	

	
}
