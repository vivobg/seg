package explore;

import java.awt.Point;
import java.util.List;

import search.AStarSearch;

import map.Map;

public class ExploreTest {

	public static void explore(Map map, Point start) {
		List<Point> path = null;
		do {
			System.out.println("Explore");
			path = AStarSearch.Search(map, start, null, false);
			if (path != null) {
				int size = path.size();
				Point end = path.get(size - 1);
				float found = Math.random() < 0.4 ? Map.EMPTY : Map.OCCUPIED;
				map.setValue(end.x, end.y, found);
				
				if (found < 0.4) //Target cell is EMPTY, start from it.
					start = path.get(size - 1);
				else //Target cell is unwalkable, start from previous cell(walkable)
					start = size > 2 ? path.get(size - 2) : path.get(0);

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
