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
					Thread.sleep(30);
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
					double yaw = targetYaw(start.x, start.y, p.x, p.y);
					PlayerPose2d pose = new PlayerPose2d(p.x*Map.SCALE, p.y * Map.SCALE, yaw);
					System.out.println("Should move");
					robot.move(pose);
					try {
						
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("X: " + pose.getPx() + " Y: " + pose.getPy());
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
	public static double targetYaw(double sX, double sY, double tX, double tY){
        //System.out.println("Targeting targeting");
        double dy = Math.abs(tY - sY);
        double dx = Math.abs(tX - sX);
        double theta = 0;
        double opposite, adjacent;
        opposite = dy;
        adjacent = dx;
        if (tX > sX) {

                if (tY > sY) {
                        // quad TR
                        theta = Math.atan(opposite / adjacent);
                } else {
                        // quad BR
                        theta = -Math.atan(opposite / adjacent);
                }
        } else if (tX == sX) { // vertical
                if (tY > sY)
                        theta = Math.PI / 2;// up
                else if (tY < sY)
                        theta = -Math.PI / 2;// down
        } else if (tY == sY) {// horizontal
                if (tX > sX)
                        theta = 0; // right
                else if (tX < sX)
                        theta = Math.PI;// left
        } else {
                if (tY > sY) {
                        // quad TL
                        theta = Math.PI
                                        - Math.atan(opposite / adjacent);
                } else {
                        // quad BL
                        theta = -Math.PI
                                        + Math.atan(opposite / adjacent);
                }
        }

       
        return theta;
}

}
