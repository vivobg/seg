
package mainApp;

import garbage.GarbageCollection;
import gui.Gui;
import gui.NBGui;
import gui.Save;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import map.Map;
import robot.Robot;


public class Control {
	Map map;
	public Gui gui = null;
        public NBGui nbgui = null;
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
			map.robotList.add(new Robot(this, i));
		}
	}

	private void processArgs(String[] args) {
		//java MainApp -solo -explore -map map1 -multi -collect 1 1 2 0 -map map2
                if (args.length == 0) {
                    args = new String[]{"-gui"};
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
	/**
	 * Print given text to console and GUI, if one is used.
	 * @param text
	 */
	public void println(String text){
		System.out.println(text);
		if (gui!=null) gui.printToGuiConsole(text, "#0000C0");
                if (nbgui!=null) nbgui.printToGuiConsole(text, "#0000C0");
	}

	
}
