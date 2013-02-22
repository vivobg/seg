package garbage;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import map.Map;

import mainApp.Utilities;

import javaclient3.structures.PlayerPose2d;

import robot.Robot;

public class GarbageCollection {//too much static
	static Map map;
	static Queue<Point> toBeCollected = new ArrayDeque<Point>();
	static HashMap<Point,Boolean> garbageObjects = new HashMap<Point,Boolean>();//redundant, Map holds the list
	static Rectangle collectionArea;
	static List<Point> garbageLoc;//redundant, Map holds the list
	/**
	 * 
	 * @param map
	 * @param robots
	 * @param collArea The rectangle that specifies the collection area
	 */
	public static void collect(Map map , List<Robot> robots, Rectangle collArea)
	{
		GarbageCollection.map = map;
		for(Point p : garbageLoc)
		{
			garbageObjects.put(p, false);
			toBeCollected.add(p);
		}
		
		for(int i = 0; i< robots.size(); i++)
		{
			Robot bot = robots.get(i); 
			if(!toBeCollected.isEmpty())
			{
				beginCollection(bot,toBeCollected.remove());
			}
		}
		
		while(garbageObjects.containsValue(false)) //while there exists one that hasnt been collected
		{
			//wait for list to be empty
			Utilities.pause(100);
		}
		
	}
	/**
	 * 
	 * @param locations A list of Points stating the location of the garbage objects
	 */
	public static void setGarbageObjects(List<Point> locations)
	{
		GarbageCollection.garbageLoc = locations;
	}
	public static void setCollectionArea(Rectangle collectionArea)
	{	
		GarbageCollection.collectionArea = collectionArea;
	}
	
	private static void beginCollection(final Robot bot, final Point gObject) {
		// TODO Change method for when garbage is next to a wall
		Thread collect = new Thread() {
			public void run() {
				
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
		};
		collect.start(); 
		
	}
}
