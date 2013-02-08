import javaclient3.PlayerClient;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;

import java.awt.event.*;
import java.awt.geom.Line2D;

import javax.swing.*;
import java.util.*;


public class Bot  implements ActionListener {
	
	private static final int _MapSizeY = 200;
	private static final int _MapSizeX = 200;
	Position2DInterface pos2D;
	RangerInterface sonar;
	GridContents[][] map; 
	double MIN_DISTANCE = 1;
	double MAX_DISTANCE = 1.5;
	int currX = 99;
	int currY = 99;
	
	Queue<Double> previousErrors;
	
	public void actionPerformed(ActionEvent e)
	{
		
		JButton src = (JButton)e.getSource();
		if(src.getText() == "Forward")
		{
			MoveForward();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			pos2D.setSpeed(0.0, 0.0);
		}
		else if(src.getText() == "Reverse")
		{
			Reverse();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			pos2D.setSpeed(0.0, 0.0);
			
		}
		else if(src.getText() == "Rotate")
		{
			Rotate90();
			return;
		}
		else if (src.getText() == "Explore")
			exploreClosestFrontier();
		
		mapCurrentPosition();
		setUpMapCurrPos();
	}
	
	private void setUpMapCurrPos() {
		Thread thread = new Thread(){
			public void run(){
				while(true){
					mapCurrentPosition();
					try {
						sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
		
	}

	void MoveForward()
	{
		pos2D.setSpeed(0.5, 0.0);
		
		
	}
	void Reverse()
	{
		pos2D.setSpeed(-0.5, 0.0);
		
	}
	void Rotate90()
	{
		pos2D.setSpeed(0.0, 0.25);
		try
		{
			Thread.sleep((long) (Math.PI * 1000));
			pos2D.setSpeed(0.0, 0.0);
		}
		catch(Exception e)
		{
		}
	}
	
	
	public Bot()
	{
		PlayerClient robot = new PlayerClient("localhost",6665);
		previousErrors = new LinkedList<Double>();
		
		pos2D = robot.requestInterfacePosition2D(0,PlayerConstants.PLAYER_OPEN_MODE);
		sonar = robot.requestInterfaceRanger(0,PlayerConstants.PLAYER_OPEN_MODE);
        robot.runThreaded(-1, -1);
        map = new GridContents[_MapSizeX][_MapSizeY];
        emptyMap();
	}
	
	

	private void emptyMap() {
		for(int i = 0; i < _MapSizeX; i++)
		{
			for(int j = 0; j < _MapSizeY; j++ )
			{
				map[i][j] = GridContents.Empty;
			}
		}
		
	}

	public void run()
	{
		mapCurrentPosition();
		exploreClosestFrontier();
		
	}
	
	public void mapCurrentPosition() {
		double currX = 100 + (10 * pos2D.getX());
		double currY = /*200-*/(100 + (10 * pos2D.getY()));
		
		
		
		SensorReading[] readings = getSonarValues();
		
		for(SensorReading s : readings){
			double length = s.getLength();
			double roundLength = Utilities.roundLength(length);
			if(length >= 5) continue;//ignore
			
			double currYaw = Utilities.toDegrees(pos2D.getYaw());
			
			
			double theta = Math.toRadians(currYaw - (s.getBearing() + 90));
			double x = Math.sin(theta) * length;
			double y = Math.cos(theta) * length;
			y = (int)(Utilities.Round(y,1) * 10);
			x = (int)(Utilities.Round(x,1) * 10);
			int xVal = (int) (currX + (x)) > 199 ? 199 : (int) (currX + (x)) ; //To ensure against index out of bounds ex
			int yVal = (int) (currY + (y)) > 199 ? 199: (int) (currY + (y));
			
			map[xVal][yVal] = GridContents.Wall;
			triggerMapUpdateEvent(xVal,yVal,GridContents.Wall);
		}
	}
	public void addListener(MapUpdateEvent listener)
	{
		if(_mapUpdateListeners == null)
			_mapUpdateListeners = new Vector<MapUpdateEvent>();
		_mapUpdateListeners.add(listener);
	}
	private void triggerMapUpdateEvent(int i, int j, GridContents cont) {
		// TODO Auto-generated method stub
		if (_mapUpdateListeners != null && !_mapUpdateListeners.isEmpty())
		{
			Enumeration e = _mapUpdateListeners.elements();
			while (e.hasMoreElements())
			{
				MapUpdateEvent event = (MapUpdateEvent)e.nextElement();
				event.mapUpdate(i, j, cont);
			}
		}
	}
	protected Vector<MapUpdateEvent> _mapUpdateListeners;
	
	
	

	private void exploreClosestFrontier() {
		
		
	}
	
	

	public SensorReading[] getSonarValues()
	{
		List<SensorReading> sensors = new ArrayList<SensorReading>();
		while(!sonar.isDataReady());
		double[] sensorReadings =  sonar.getData().getRanges();
		
		 for(int i = 0; i < sensorReadings.length; i++){
			 sensors.add(new SensorReading(i*45, sensorReadings[i]));
		 }

		SensorReading[] result = new SensorReading[sensors.size()];
		sensors.toArray(result);
		return (result);
	}

	

	
	
	
	
}
