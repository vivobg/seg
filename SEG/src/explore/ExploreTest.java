package explore;

import java.awt.Point;
import java.util.List;

import javaclient3.structures.PlayerPose2d;

import robot.Robot;
import search.AStarSearch;

import map.Map;

public class ExploreTest {

	public static void explore(Map map, Point start) {
		List<Point> path = null;
		do {
			path = AStarSearch.Search(map, start, null, false);
			if (path != null) {
				int size = path.size();
				Point end = path.get(size - 1);
				byte found = Math.random() < 0.4 ? Map.EMPTY : Map.OCCUPIED;
				map.setValue(end.x, end.y, found);
				
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
	
	public static void exploreRobot(Map map,Robot robot, Point start) {
		System.out.println("Exploration started");
		List<Point> path = null;
		do {
			path = AStarSearch.Search(map, start, null, false);
			if (path != null) {
				for (int i = 1; i<path.size();i++){
					Point p = path.get(i);
					robot.explore_move(p.x,p.y, path.get(path.size()-1));
					try {
						
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				start = Map.convertPlayerToInternal(robot.x, robot.y);			

				try {
					
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (path != null);
		System.out.println("Exploration finished");
	}

}
