package mainApp;
import javaclient3.PlayerClient;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose2d;
import javaclient3.structures.position2d.PlayerPosition2dCmdPos;

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.Line2D;

import javax.swing.*;

import search.AStarSearch;

import java.util.*;


public class Bot  implements ActionListener {
	
	private static final int _MapSizeY = 200;
	private static final int _MapSizeX = 200;
	Position2DInterface pos2D;
	RangerInterface sonar;
	GridContents[][] map; 
	final int SensorMaxLength = 5;
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
			exploreClosestFrontier(getNearestUnexploredPoints());
		
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
				map[i][j] = GridContents.Unexplored;
			}
		}
		
	}

	public void run()
	{
		explore();
		
		
	}
	public void explore()
	{
		mapCurrentPosition();
		List<Point> frontiers = new ArrayList<Point>();
		frontiers  = getNearestUnexploredPoints();  
		exploreClosestFrontier(frontiers);
	}
	
	private List<Point> getNearestUnexploredPoints()//Max Length is the maximum dimension of the map
	{
		Point currentPoint = this.getCurrentPoint();
		
		List<Point> unexploredPoints = new ArrayList<Point>();
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				Point p = new Point(currentPoint.x + (i*this.SensorMaxLength),currentPoint.y + (j*this.SensorMaxLength));
				if(i == 0 && j == 0 ) continue; //when i = 0 and j = 0. thats the same as the point we're currently standing on 
				else if(p.x < 0 || p.y < 0 ) continue;//not negative array value
				else if (p.x >= map.length || p.y >= map.length) continue;//not bigger than the maximum map size
				else if(map[p.x][p.y] != GridContents.Unexplored) continue;
				else unexploredPoints.add(p);
			}
		}
		
		return unexploredPoints;
	}
	public Point getCurrentPoint() //getCurrentPoint of robot translated with map
	{
		int currX = (int) (100 + (10 * pos2D.getX()));
		int currY = (int) /*200-*/(100 + (10 * pos2D.getY()));
		return new Point(currX,currY);
	}
	public void mapCurrentPosition() {
		
		
		
		
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
			
			
			Point currentPoint = this.getCurrentPoint();
			Point wall = new Point(xVal,yVal);
			
			List<Point> pointsInbetween = AStarSearch.Search(map, currentPoint,wall);
			for(Point p : pointsInbetween)
			{
				triggerMapUpdateEvent(p.x,p.y,GridContents.Empty);
			}
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
	
	
	

	private void exploreClosestFrontier(List<Point> frontiers) {
		
		while(frontiers.size() > 0)
		{
			//get First element
			Point frontier = frontiers.get(0);
			frontiers.remove(0);//remove it from the array
			
			moveTo(frontier);
			mapCurrentPosition();
			exploreClosestFrontier(getNearestUnexploredPoints());
		}
		
	}
	
	

	private void moveTo(Point target) {
		
		Point currentPoint = this.getCurrentPoint();
		List<Point> path = AStarSearch.Search(map, currentPoint , target);
		for(Point p : path)
		{
			if(map[p.x][p.y] != GridContents.Unexplored) continue;
			int adjustPx = -9 + (p.x /10);
			int adjustPy = -6 + (p.y/10);
			if(Math.abs(p.x /10) > 9.5 ) continue;
			if(Math.abs(p.y /10) > 6 ) continue;
			PlayerPose2d pos = new PlayerPose2d();
			
			
			pos.setPx(adjustPx);
			
			pos.setPy(adjustPy);
			pos.setPa(0.0);
			
			PlayerPose2d vel = new PlayerPose2d();
//			vel.setPx(1);
//			vel.setPy(1);
			vel.setPa(Math.PI);
			
			
			int state = 0;
			pos2D.setPosition(pos, vel, state );
		}
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
