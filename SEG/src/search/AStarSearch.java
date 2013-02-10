package search;
import java.util.AbstractMap.SimpleEntry;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.awt.Point;
import java.util.ArrayList;

import map.Map;

public class AStarSearch {
	public static List<Point> Search(float[][] map , Point source, Point target )
	{
		List<Point> closedSet = new ArrayList<Point>(); //Nodes already evaluated
		List<Point> openSet = new ArrayList<Point>();//Nodes yet to be evaluated and their score
		HashMap<Point, Point> cameFrom = new HashMap<Point,Point>(); //Used in path reconstruction
		HashMap<Point,Integer> gScore = new HashMap<Point,Integer>();
		HashMap<Point,Integer> fScore = new HashMap<Point,Integer>();
		
		openSet.add(source);//Start from source
		
		gScore.put(source, 0); //score starts at 0
		fScore.put(source, gScore.get(source) + cost(source,target));//est total cost
		
		
		
		while(!openSet.isEmpty())
		{
			Point current = getLowest(openSet,fScore);
			if(cost(current,target) < 0.5) 
				//if we are atleast within 0.5 metres we can say we're there and just use the robot controller to adjust the last 0.5
			{
				return reconstructPath(cameFrom, current);
			}
			
			openSet.remove(current);
			closedSet.add(current);
			
			for(Point neighbour : getAdjacentPoints(map,current))
			{
				
				if(closedSet.contains(neighbour))
					continue;
				
				int tScore = gScore.get(current) + cost(current, neighbour);
				
				if(!openSet.contains(neighbour) || (gScore.containsKey(neighbour) && tScore < gScore.get(neighbour)) )
				{
					cameFrom.put(neighbour, current);
					gScore.put(neighbour, tScore);
					fScore.put(neighbour, gScore.get(neighbour) + cost(neighbour, target));
					
					if(!openSet.contains(neighbour))
					{
						openSet.add(neighbour);
					}
				}
			}
			
		}
		return null;
		
	}
	
	

	private static int cost(Point source, Point target) {
		//Incase we want to change the cost function
		return euclidDist(source,target);
	}



	private static List<Point> reconstructPath(HashMap<Point, Point> cameFrom, Point target) {
		if(cameFrom.containsKey(target)){
			List<Point> result = new ArrayList<Point>();
			result.addAll(reconstructPath(cameFrom,cameFrom.get(target)));
			result.add(target);
			return result;
		}
		List<Point> targetList = new ArrayList<Point>();
		targetList.add(target);
		return targetList;
	}



	private static Point getLowest(
			List<Point> openSet,HashMap<Point, Integer> fScores) {
		Point lowest = null;
		if(openSet.size() > 0)
		{
			lowest = openSet.get(0);
			for(Point i : openSet)
			{
				if(fScores.get(i) < fScores.get(lowest))
				{
					lowest = i;
				}
			}
		}
		
		
		return  lowest;
	}

	private static int euclidDist(Point source, Point target) {
		int dx = target.x - source.x;
		int dy = target.y - source.y;
		
		int dist = (int) Math.sqrt(Math.pow(dx + dy, 2));
		return dist;
	}

	private static List<Point> getAdjacentPoints(float[][] map, Point p )
	{
		List<Point> possiblePoints = new ArrayList<Point>();
		
		for(int i = p.x -1;i<p.x+2;i++)
		{
			for(int j = p.y-1;j < p.y+2; j++)
			{
				if(!p.equals(new Point(i,j))  )
				{
					
					if(!(i<0 || j < 0 || i >= map.length || j >= map[0].length) && map[i][j] == 0)
					{
						possiblePoints.add(new Point(i,j));
					}
				}
			}
		}
		
		return possiblePoints;
		
	}
}
