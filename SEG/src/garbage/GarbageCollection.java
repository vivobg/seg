package garbage;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import map.Map;

import javaclient3.structures.PlayerPose2d;

import robot.Robot;

public class GarbageCollection {//too much static
	
	static Queue<Point> toBeCollected = new ArrayDeque<Point>();
	static Rectangle collectionArea;
	/**
	 * 
	 * @param map
	 * @param robots
	 * @param collArea The rectangle that specifies the collection area
	 */
	public static void collect(Map map , List<Robot> robots, Rectangle collArea)
	{
		for(int i = 0; i< robots.size(); i++)
		{
			Robot bot = robots.get(i); 
			if(!toBeCollected.isEmpty())
			{
				beginCollection(bot,map);
			}
		}
		
		while(map.garbageList.containsValue(false)) //while there exists one that hasnt been collected
		{
			//wait for list to be empty
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void setCollectionArea(Rectangle collectionArea)
	{	
		GarbageCollection.collectionArea = collectionArea;
	}
	
	private static void beginCollection(final Robot bot,final Map map) {
		// TODO Change method for when garbage is next to a wall
		Thread collect = new Thread() {
			public void run() {
				Point gObject = getClosestGarbageObject(new Point((int)bot.x,(int)bot.y),map);
				while(gObject != null)
				{
					PlayerPose2d pose = new PlayerPose2d();
					pose.setPa(Math.PI/4);
					pose.setPx(gObject.getX());
					pose.setPy(gObject.getY() - 0.25);//the gripper has a range of ~0.5
					bot.move(pose);//assuming its blocking?
					if(bot.pickUpObject())//object picked up successfully
					{
						//sets target to center
						//Point targetPoint = new Point(collectionArea.getCenterX(),collectionArea.getCenterY());
					}
				}
			}

			
		};
		collect.start(); 
		
	}
	
	private static Point getClosestGarbageObject(Point botPos,Map map) {
		double lowest = 1000;
		Point pos = null;
		for(Point gLoc : map.garbageList.keySet())
		{
			if(map.garbageList.get(gLoc) == false) //Uncollected
			{
				double dist = gLoc.distance(botPos);
				if(dist < lowest){
					pos = gLoc;
					lowest = dist;
				}
			}
		}
		//remove this one from the list of garbage objects or set to true?
		if(pos!= null) map.garbageList.put(pos, true);
		return pos;
	}
}
