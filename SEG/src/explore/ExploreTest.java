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
				Point end = path.get(path.size()-1);
				float found = Math.random() < 0.8 ? Map.EMPTY : Map.UNWALKABLE;
				map.setValue(end.x, end.y, found);
				start = end;
				
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
