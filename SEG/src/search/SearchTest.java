package search;

import java.awt.Point;
import java.util.List;

import map.Map;

public class SearchTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map map = new Map();
		for(int i = 0; i< 30; i++)
		{
			for(int j = 0; j < 30; j++)
			{
				if (j==0 || j==29 || i==0 || i==29) map.setValue(i, j, Map.WALL,9);
				else map.setValue(i, j, Map.EMPTY,9);
			}
		}
		
		for(int j = 10; j < 22; j++)
		{
			map.setValue(10, j, Map.WALL,9);
			
		}
		for(int i = 10; i < 20; i++)
		{
			map.setValue(i, 10, Map.WALL,9);	
		}
		for(int i = 10; i < 29; i++)
		{
			map.setValue(19, i, Map.WALL,9);
		}
		
		
		
		map.setValue(15, 15, Map.UNEXPLORED,9);
		map.setValue(15, 14, Map.UNEXPLORED,9);
		map.setValue(14, 14, Map.UNEXPLORED,9);
		map.setValue(14, 15, Map.UNEXPLORED,9);
		map.setValue(16, 16, Map.UNEXPLORED,9);
		map.setValue(17, 17, Map.UNEXPLORED,9);
		map.setValue(18, 18, Map.UNEXPLORED,9);
		
		printMap(map);
		
		List<Point> points = AStarSearch.aSearch(map, new Point(16,20), new Point(28,28));
		System.out.println("A* Search");
		printMap(map, points);
		 points = AStarSearch.dSearch(map, new Point(15,2));
		System.out.println("Djikstra's Search");
		printMap(map, points);
	}
  
	private static void printMap(Map map, List<Point> points) {
		char[][] m = new char[30][30];
		for(int i = 0; i < 30; i++)
		{
			for(int j = 0; j< 30; j++)
			{
				if(map.isOccupied(i, j)) m[i][j] = '█';
				else if(map.isUnexplored(i, j)) m[i][j]='▧';
				else{
					m[i][j] = ' ' ;
				}
			}
		}
		for(Point p : points)
		{
			try{
			m[p.x][p.y] ='*';
			}
			catch (ArrayIndexOutOfBoundsException e){
				
			}
		}
		
		for(int i = 0; i < 30; i++)
		{
			System.out.print("[");
			for(int j = 0; j < 30;j++)
			{
				
				System.out.print(m[i][j]);
				
			}
			System.out.println("]");
		}
		
	}

	private static void printMap(Map map) {
		for(int i = 0; i < 30; i++)
		{
			System.out.print("[");
			for(int j = 0; j < 30;j++)
			{
				char r;
				if(map.isOccupied(i, j)) r = '█';
				else if(map.isUnexplored(i, j)) r='▧';
				else{
					r = ' ' ;
				}
				System.out.print(r);
				
			}
			System.out.println("]");
		}
		
	}

}
