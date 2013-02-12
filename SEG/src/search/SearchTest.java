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
				map.setValue(i, j, Map.EMPTYThreshold-0.1f);
			}
		}
		
		for(int j = 10; j < 22; j++)
		{
			map.setValue(10, j, Map.OCCThreshold+0.1f);
			
		}
		for(int i = 10; i < 20; i++)
		{
			map.setValue(i, 10, Map.OCCThreshold+0.1f);	
		}
		for(int i = 10; i < 28; i++)
		{
			map.setValue(19, i, Map.OCCThreshold+0.1f);
		}
		printMap(map);
		
		List<Point> points = AStarSearch.Search(map, new Point(0,0), new Point(28,28), true);
		System.out.println();
		printMap(map, points);
	}
  
	private static void printMap(Map map, List<Point> points) {
		char[][] m = new char[30][30];
		for(int i = 0; i < 30; i++)
		{
			for(int j = 0; j< 30; j++)
			{
				if(map.isOccupied(i, j)) m[i][j] = '|';
				
				else{
					m[i][j] = ' ' ;
				}
			}
		}
		for(Point p : points)
		{
			m[p.x][p.y] ='*';
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
				String r = map.isOccupied(i, j)? "|" : " ";
				System.out.print(r);
				
			}
			System.out.println("]");
		}
		
	}

}
