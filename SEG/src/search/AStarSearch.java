package search;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import map.Map;
import sense.Bresenham;
/**
 * Class providing static methods, to calculate a path between 2 points
 *
 */
public class AStarSearch {
	/**
	 * A* search from source to target.
	 * Useful during garbage collection.
	 * @param map the map to base the path on
	 * @param source the starting point
	 * @param target the final point
	 * @return the list of path nodes to follow from source to target, null if no possible path
	 */
	public static List<Point> aSearch(Map map , Point source, Point target){
		return search(map, source, target, true );
	}
	/**
	 * Djikstra's search from source to nearest unexplored cell.
	 * Useful during exploration
	 * @param map map the map to base the path on
	 * @param source source the starting point
	 * @return the list of path nodes to follow from the source to the nearest unexplored point
	 */
	public static List<Point> dSearch(Map map , Point source){
		return search(map, source, null, false );
	}
	/**
	 * A* or Djikstra's search, depending on parameters
	 * @param map map the map to base the path on
	 * @param source source the starting point
	 * @param target the final point
	 * @param ASEARCH a flag to indicate whether to perform an A* or Dijkstra search
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
			if((ASEARCH && current.equals(target))|| (!ASEARCH && (map.isUnexplored(current.x, current.y) )))
				//if we are are at the target, stop search and return path
			{
				//return optimisePath(map, reconstructPath(cameFrom, current));
				return reconstructPath(cameFrom, current);
			}

			openSet.remove(current);
			closedSet.add(current);

			for(Point neighbour : getAdjacentPoints(map,current,ASEARCH))
			{

				if(closedSet.contains(neighbour))
					continue;

				int tScore = gScore.get(current)
						+ Gcost(current, neighbour, map);

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



	/**
	 * Calculate the heuristic cost for A*
	 * @param source the current point
	 * @param target the final point
	 * @param ASEARCH true for A*, false for Dijkstra
	 * @return the heuristic cost from given point to final target
	 */
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

	private static int Gcost(Point source, Point target, Map map) {
		int cost = 0;
		if (map.isBuffer(target.x, target.y))
			cost = 20;
		
		if (isDiagonal(source, target))
			return cost + 7;
		else
			return cost + 5;

	}
	/**
	 * Check to see if two points are diagonal to each other
	 * @param s the first point
	 * @param t the second point
	 * @return true if diagonal, false otherwise
	 */
	private static boolean isDiagonal(Point s, Point t){
		if (s.x == t.x || s.y == t.y)return false;
		else return true;
	}


	/**
	 * Reconstruct the path from source to target, starting from the target
	 * @param cameFrom the node from which the current node was reached
	 * @param target the target node
	 * @return list of nodes from source to target
	 */
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


	/**
	 * Returns the lowest cost node from the open set
	 * @param openSet the open set
	 * @param fScores the list of total cost for every point
	 * @return the lowest cost node
	 */
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
	/**
	 * The straight line distance between two points
	 * @param source the first point
	 * @param target the second point
	 * @return the distance between the two points
	 */
	private static int euclidDist(Point source, Point target) {
		int dx = target.x - source.x;
		int dy = target.y - source.y;

		int dist = (int) Math.sqrt(Math.pow(dx + dy, 2));
		return dist;
	}
	/**
	 * Get a list of all adjacent points
	 * @param map the map to use
	 * @param p the current point for which to return adjacent points
	 * @param ASEARCH true if A*, false if Dijkstra
	 * @return the list of adjacent points
	 */
	public static List<Point> getAdjacentPoints(Map map, Point p , boolean ASEARCH)
	{
		List<Point> possiblePoints = new ArrayList<Point>();
		
		for(int i = p.x -1;i<p.x+2;i++)
		{
			for(int j = p.y-1;j < p.y+2; j++)
			{
				Point adjPoint = new Point(i,j);
				if(!p.equals(adjPoint)  )
				{
					if(ASEARCH && (map.isEmpty(i, j) || map.isBuffer(i, j) )){
						// if(isAvailableCell(adjPoint,map))possiblePoints.add(adjPoint);
						possiblePoints.add(adjPoint);
					}
 else if (!ASEARCH && (!map.isOccupied(i, j)))
					{
	 					
						if (isAvailableCell(adjPoint, map))
							possiblePoints.add(adjPoint);
					}
				}
			}
		}

		return possiblePoints;

	}
	/**
	 * Check if the given cell is available (not too close to a wall)
	 * @param adjPoint the point to check
	 * @param map the map to use
	 * @return true if the point is available, false otherwise
	 */
	public static boolean isAvailableCell(Point adjPoint, Map map) {
		//int scale = 2; //for Testing
		int scale = (int) Math.ceil( (0.3 / Map.SCALE ));
		for(int i = adjPoint.x - scale; i <= adjPoint.x + scale; i++)
		{
			for(int j = adjPoint.y - scale; j<=adjPoint.y + scale; j++)
			{
				if(map.isOccupied(i, j) || map.isFarWall(i, j)) 
				{
					//System.out.println(map.getValue(i, j));
					return false;
				}
			}
		}
		return true;
	}
	/*
	public static List<Point> optimisePath(Map map, List<Point> path) {
		List<Point> opti = new ArrayList<Point>();
		if (path!=null && path.size() > 2){
		Point node = path.get(0);
		Point lastNode;
		int index = 0;
		//System.out.println("Path Length = " + path.size());
		opti.add(new Point(node));
		while (index+1 < path.size()) {
			for (int i = index+1; i < path.size(); i++) {
				//Get the next node
				lastNode = node;
				node = path.get(i);
				index = i;
				Point optiEnd =opti.get(opti.size()-1);
				//no line of sight: add, then break
				// make sure lineOfSight works on !!!copies!!! of the Points
				if (!lineofSight(map, optiEnd, node)) {
					opti.add(lastNode);
					//System.out.println("NO Line of sight " + i);
					break;
				}
				//else System.out.println("Line of sight between " + opti.get(opti.size() - 1) + " and " + path.get(i));

				// if ( ! lastVisible.equals(opti.get(opti.size()-1))){
				
				// }
			}
			opti.add(new Point(node));
		}
		return opti;
		}
		
		return path;

		
	}*/

	/**
	 * Draws a line onto the map, with the given internal map coordinates
	 * 
	 * @param map
	 *            The Map instance to work with
	 * @param x0
	 *            The X coordinate of the start point
	 * @param y0
	 *            The Y coordinate of the start point
	 * @param x1
	 *            The X coordinate of the end point
	 * @param y1
	 *            The Y coordinate of the end point
	 * @param WALL
	 *            A flag to decide whether the line ends with a wall, or is
	 *            completely empty
	 */
	public static boolean lineofSight(Map map, Point source, Point target) {
		Point s = new Point(source);
		Point t = new Point(target);

		List<Point> points = Bresenham.bresenhamLine(s.x, s.y, t.x, t.y);
		

		for (int i = 0; i < points.size()-1; i++) {
			Point node = points.get(i);
			if (!(map.isEmpty(node.x, node.y) || map.isBuffer(node.x, node.y)))
				return false;
		}
		return true;
	}

}
