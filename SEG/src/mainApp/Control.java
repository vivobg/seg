
package mainApp;

import garbage.GarbageCollection;
import gui.Save;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import map.Map;
import robot.Robot;


public class Control {
	Map map;
	// ArrayList<Robot> robots = new ArrayList<Robot>();
	// ArrayList<Point> garbage = new ArrayList<Point>();
	BotMode botMode = BotMode.Solo;
	public Control(String[] args)
	{
		map = new Map();
		
		setupRobots();
		processArgs(args);
	}
	
	private void setupRobots() {
		
		for(int i = 0; i< 3; i++)//TODO: Change to autodetect number of robots available
		{
			map.robotList.add(new Robot(map, i));
		}
	}

	private void processArgs(String[] args) {
		//java MainApp -solo -explore -map map1 -multi -collect 1 1 2 0 -map map2
		for(int i = 0; i < args.length; i++)
		{
			if(args[i].equals("-solo"))
			{
				switchToSolo();
			}
			else if(args[i].equals("-multi"))
			{
				switchToMulti();
			}
			else if (args[i].equals("-explore"))
			{
				explore();
			}
			else if(args[i].equals("-map"))
			{
				if(i + 1 < args.length)
				{
					String filename = args[i+1];
					saveMap(filename);
				}
			}
			else if(args[i].equals("-collect"))
			{
				if(i + 4 < args.length)
				{
					int x1 = Integer.parseInt(args[i+1]);
					int y1 = Integer.parseInt(args[i+2]);
					int x2 = Integer.parseInt(args[i+3]);
					int y2 = Integer.parseInt(args[i+4]);
					collect(x1,y1,x2,y2);
				}
			}
			
		}
		
	}

	public void collect(int x1, int y1, int x2, int y2) {
		
		Rectangle collectionArea = new Rectangle(x1,y1,x2-x1,y2-y1);
		GarbageCollection.setCollectionArea(collectionArea);
		
		List<Robot> availableBots = new ArrayList<Robot>();
		if(botMode == BotMode.Solo)
		{
			availableBots.add(map.robotList.get(0));
		}
 else
			availableBots = map.robotList;
		
		GarbageCollection.collect(map, availableBots, new Rectangle());
		
	}

	public void switchToMulti() {
		botMode = BotMode.Multi;
		
	}

	public void switchToSolo() {
		botMode = BotMode.Solo;
	}
	
	public Map getMap() {
		return map;
	}

	/**
	 * 
	 * @param filename the filename to be used to save the map (filename+.extension)
	 */
	private void saveMap(String filename) {
		Save.toPNG(map, filename);
		
	}

	public void explore() 
	{	
		if(botMode == BotMode.Solo)
		{
			map.robotList.get(0).explore(); // Only use the first bot
		}
		else{
			for (Robot bot : map.robotList)
			{
				bot.explore();
			}
		}
		
	}

	
}
