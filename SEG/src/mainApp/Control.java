
package mainApp;

import garbage.GarbageCollection;
import gui.Gui;
import gui.NBGui;
import gui.Save;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import map.Map;
import robot.Robot;
import robot.RobotState;

/**
 * The class responsible for initialising and connecting all other classes.
 * It processes the given command-line arguments and launches the requested
 * functionality.
 */
public class Control {
	Map map;
	public Gui gui = null;
	public NBGui nbgui = null;

	// ArrayList<Robot> robots = new ArrayList<Robot>();
	// ArrayList<Point> garbage = new ArrayList<Point>();
	BotMode botMode = BotMode.Solo;
	/**
	 * Initialise a new Control instance. It loads a pre-explored map,
	 * if one was saved. It checks the current working directory for a "map.instance" file.
	 */
	public Control(){
		this.map = new Map();
		try {
			//Read map file
			FileInputStream f = new FileInputStream("map.instance");
			//Read using ObjectInputStream
			ObjectInputStream o = new ObjectInputStream(f);
			//Restore map instance from file
			Object obj = o.readObject();
			if (obj instanceof Map){
				Map savedMap = (Map) obj;
				map.setMaxX(savedMap.getMaxX());
				map.setMaxY(savedMap.getMaxY());
				map.setMinX(savedMap.getMinX());
				map.setMinY(savedMap.getMinY());
				map.setNegArray(savedMap.getNegArray());
				map.setPosArray(savedMap.getPosArray());
				map.setGarbageListArray(savedMap.getGarbageListArray());
				this.println("Saved Map successfully loaded");
				o.close();
			}
			else {
				this.println("Saved object is not an instance of Map");
			}
		} catch (FileNotFoundException ex) {
			this.println("Error reading Map instance. File not found.");
		} catch (IOException ex) {
			this.println("Error reading Map instance. IO exception.");
		} catch (ClassNotFoundException ex) {
			this.println("Error reading Map instance. Class not found.");
		}


		setupRobots();
	}
	/**
	 * Initialise a new Control instance. 
	 * @param args the arguments to process and launch requested functionality.
	 */
	public Control(String[] args)
	{
		this();
		processArgs(args);
	}
	/**
	 * Initialise the robots
	 */
	private void setupRobots() {
		if (this.botMode.equals(BotMode.Solo) && map.robotList.size() == 0) {
			map.robotList.add(new Robot(this, 0));
			println("Robot 0 successfully initialised.");
		} else if (this.botMode.equals(BotMode.Multi)) {
			for (int i = map.robotList.size(); i < 3; i++) {
				map.robotList.add(new Robot(this, i));
				println("Robot " + i + " successfully initialised.");
			}
		}
	}
	/**
	 * Process the arguments and launch the requested functionality
	 * @param args
	 */
	private void processArgs(String[] args) {
		//java MainApp -solo -explore -map map1 -multi -collect 1 1 2 0 -map map2
		if (args.length == 0) {
			args = new String[]{"-gui"};
		}
		for(String str : args)
		{
			if(str.equals("-gui")) launchGui();
		}
		
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
				while(!allRobotsAvailable()){sleep(2000);};//Wait for all robots to complete what they're doing
				explore();
			}
			else if(args[i].equals("-map"))
			{
				while(!allRobotsAvailable()){sleep(2000);}; //Wait for all robots to complete what they're doing
				if(i + 1 < args.length)
				{
					String filename = args[i+1];
					saveMap(filename);
				}
			}
			else if(args[i].equals("-collect"))
			{
				while(!allRobotsAvailable()){sleep(2000);};//Wait for all robots to complete what they're doing
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

		}

	}
	/**
	 * Wrap Thread.sleep() and its try-catch block in a convenience method.
	 * @param i
	 */
	private void sleep(int i) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Launch the GUI.
	 */
	private void launchGui() {
		//Enable Nimbus Look and Feel, if available.
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		/* Create and display the GUI */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Gui(Control.this).setVisible(true);
				//new NBGui(Control.this).setVisible(true);
			}
		});
	}
	/**
	 * Start the collection functionality, collecting all garbage objects into the specified rectangle area
	 * @param x1 the x coordinate of the first point
	 * @param y1 the y coordinate of the first point
	 * @param x2 the x coordinate of the second point
	 * @param y2 the y coordinate of the second point
	 */
	public void collect(final double x1, final double y1, final double x2, final double y2) {

		//Rectangle collectionArea = new Rectangle(x1,y1,x2-x1,y2-y1);
		//GarbageCollection.setCollectionArea(collectionArea);
		println("Begin Collecting!");
		List<Robot> availableBots = getAvailableRobots();
		Thread soTheGUIDoesNotFreeze = new Thread(){
			public void run() {
				if(botMode == BotMode.Solo)
				{
					println("Garbage Collection Starting with Robot map.robotList.get(0) droping off at " +
							x1 + "," + y1 + " and " + x2 + "," + y2);
					map.robotList.get(0).goFetchGarbage(x1,y1,x2,y2);
				}
				else
					throw new RuntimeException("Collection Not Implemented For Multiple Robots Yet Exception");
			}
		}; soTheGUIDoesNotFreeze.start();
		//println("Collection Complete");
		//GarbageCollection.collect(map, availableBots, new Rectangle());
		//throw new RuntimeException("Not Implemented Yet Exception");

	}
	/**
	 * 
	 * @return The list of available robots
	 */
	private List<Robot> getAvailableRobots() {

		List<Robot> availableBots = new ArrayList<Robot>();
		if(botMode == BotMode.Solo)
		{
			availableBots.add(map.robotList.get(0));
		}
		else
			availableBots = map.robotList;
		return availableBots;
	}
	/**
	 * Switch to use all robots. Disabled, as multi mode not fully implemented.
	 */
	public void switchToMulti() {
		botMode = BotMode.Multi;
		setupRobots();

	}
	/**
	 * Switch to use only the Robot with index 0.
	 */
	public void switchToSolo() {
		botMode = BotMode.Solo;
		setupRobots();
	}
	/**
	 * 
	 * @return the map instance
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * Save the map to a PNG image
	 * @param filename the filename to be used to save the map (filename+.extension)
	 */
	private void saveMap(String filename) {
		Save.toPNG(map, filename);

	}
	/**
	 * Launch the exploration functionality
	 */
	public void explore() 
	{	
		if(botMode == BotMode.Solo)
		{
			map.robotList.get(0).Status = RobotState.Exploring;
			map.robotList.get(0).explore(); // Only use the first bot
		}
		else{
			for (Robot bot : map.robotList)
			{
				bot.Status = RobotState.Exploring;
				bot.explore();
			}
		}

	}
	/**
	 * Print the given text to console and GUI, if one is used.
	 * @param text
	 */
	public void println(String text){
		System.out.println(text);
		if (gui!=null) gui.printToGuiConsole(text, "#0000C0");
		if (nbgui!=null) nbgui.printToGuiConsole(text, "#0000C0");
	}
	/**
	 * Print the state of the given robot
	 * @param robot the robot
	 * @param state the state of the robot
	 */
	public void RobotStateChanged(Robot robot, RobotState state) {
		println("Robot " + robot.index + " set status to : " + state.toString());
	}
	/**
	 * 
	 * @return true if all robots are idle/available.
	 */
	boolean allRobotsAvailable()
	{
		for(Robot robot : getAvailableRobots())
		{
			if(robot.Status != RobotState.Idle)return false;
		}
		return true;
	}


}
