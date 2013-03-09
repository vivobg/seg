
package mainApp;

import garbage.GarbageCollection;
import gui.NBGui;
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
	public Control(){
		this.map = new Map();
		setupRobots();
	}
	public Control(String[] args)
	{
		this();
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
					try{
					double x1 = Double.parseDouble(args[i+1]);
					double y1 = Double.parseDouble(args[i+2]);
					double x2 = Double.parseDouble(args[i+3]);
					double y2 = Double.parseDouble(args[i+4]);
					collect(x1,y1,x2,y2);
					}
					catch (NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
			else if(args[i].equals("-gui")){
				new NBGui(this).setVisible(true);
			}
			
		}
		
	}

	public void collect(double x1, double y1, double x2, double y2) {
		
		//Rectangle collectionArea = new Rectangle(x1,y1,x2-x1,y2-y1);
		//GarbageCollection.setCollectionArea(collectionArea);
		
		List<Robot> availableBots = new ArrayList<Robot>();
		if(botMode == BotMode.Solo)
		{
			availableBots.add(map.robotList.get(0));
		}
		else
			availableBots = map.robotList;
		
		//GarbageCollection.collect(map, availableBots, new Rectangle());
		throw new RuntimeException("Not Implemented Yet Exception");
		
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
