package search;
import java.util.HashMap;
import java.util.List;
import java.awt.Point;
import java.util.ArrayList;

import map.Map;

public class AStarSearch {
	/**
	 * A* search from source to target.
	 * Useful during garbage collection.
	 * @param map
	 * @param source
	 * @param target
	 * @return
	 */
	public static List<Point> aSearch(Map map , Point source, Point target){
		return search(map, source, target, true );
	}
	/**
	 * Djikstra's search from source to nearest unexplored cell.
	 * Useful during exploration
	 * @param map
	 * @param source
	 * @return
	 */
	public static List<Point> dSearch(Map map , Point source){
		return search(map, source, null, false );
	}
	/**
	 * A* or Djikstra's search, depending on parameters
	 * @param map
	 * @param source
	 * @param target
	 * @param ASEARCH
	 * @return
	 */
	private static List<Point> search(Map map , Point source, Point target, boolean ASEARCH )
	{
		List<Point> closedSet = new ArrayList<Point>(); //Nodes already evaluated
		List<Point> openSet = new ArrayList<Point>();//Nodes yet to be evaluated and their score
		HashMap<Point, Point> cameFrom = new HashMap<Point,Point>(); //Used in path reconstruction
		HashMap<Point,Integer> gScore = new HashMap<Point,Integer>();
		HashMap<Point,Integer> fScore = new HashMap<Point,Integer>();
		
		openSet.add(source);//Start from source
		
		gScore.put(source, 0); //score starts at 0
		fScore.put(source, Hcost(source,target,ASEARCH));//est total cost (no point to do 0 + cost)
		
		
		
		while(!openSet.isEmpty())
		{
			Point current = getLowest(openSet,fScore);
			if((ASEARCH && current.equals(target))|| (!ASEARCH && map.isUnexplored(current.x, current.y)))
				//if we are are at the target, stop search and return path
			{
				return reconstructPath(cameFrom, current);
			}
			
			openSet.remove(current);
			closedSet.add(current);
			
			for(Point neighbour : getAdjacentPoints(map,current,ASEARCH))
			{
				
				if(closedSet.contains(neighbour))
					continue;
				
				int tScore = gScore.get(current) + Gcost(current, neighbour);
				
				if(!openSet.contains(neighbour) || (gScore.containsKey(neighbour) && tScore < gScore.get(neighbour)) )
				{
					cameFrom.put(neighbour, current);
					gScore.put(neighbour, tScore);
					fScore.put(neighbour, gScore.get(neighbour) + Hcost(neighbour, target, ASEARCH));
					
					if(!openSet.contains(neighbour))
					{
						openSet.add(neighbour);
					}
				}
			}
			
		}
		return null;
		
	}
	
	
	

	private static int Hcost(Point source, Point target, boolean ASEARCH) {
		//Incase we want to change the cost function
		if (ASEARCH) return 5*euclidDist(source,target);
		else return 0;
		//return 0;
	}
	
	/**
	 * Calculates the cost for adjacent points.
	 * CAUTION!!! Only use it for adjacent points.
	 * @param source
	 * @param target
	 * @return 7 if diagonal, 5 otherwise
	 */
			
	private static int Gcost(Point source, Point target){
		if (isDiagonal(source, target)) return 7;
		else return 5;
		
	}
	
	private static boolean isDiagonal(Point s, Point t){
		if (s.x == t.x || s.y == t.y)return false;
		else return true;
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

	private static List<Point> getAdjacentPoints(Map map, Point p , boolean ASEARCH)
	{
		List<Point> possiblePoints = new ArrayList<Point>();
		
		for(int i = p.x -1;i<p.x+2;i++)
		{
			for(int j = p.y-1;j < p.y+2; j++)
			{
				Point adjPoint = new Point(i,j);
				if(!p.equals(adjPoint)  )
				{
					if(ASEARCH && map.isEmpty(i, j)){
						possiblePoints.add(adjPoint);
					}
					else if( !ASEARCH && (map.isEmpty(i, j) || map.isUnexplored(i, j)) )possiblePoints.add(adjPoint); 
				}
			}
		}
		
		return possiblePoints;
		
	}
}
