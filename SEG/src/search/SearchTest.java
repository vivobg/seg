package search;

import java.awt.Point;
import java.util.List;

import map.Map;

public class SearchTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		float[][] map = new float[30][30];
		for(int i = 0; i< 30; i++)
		{
			for(int j = 0; j < 30; j++)
			{
				map[i][j] = 0;
			}
		}
		
		for(int j = 10; j < 22; j++)
		{
			map[10][j] = 1;
			
		}
		for(int i = 10; i < 20; i++)
		{
			map[i][10] = 1;	
		}
		for(int i = 10; i < 28; i++)
		{
			map[19][i] = 1;	
		}
		printMap(map);
		
		List<Point> points = AStarSearch.Search(map, new Point(0,0), new Point(28,28));
		System.out.println();
		printMap(map, points);
	}

	private static void printMap(float[][] map, List<Point> points) {
		char[][] m = new char[30][30];
		for(int i = 0; i < 30; i++)
		{
			for(int j = 0; j< 30; j++)
			{
				if(map[i][j] > 0) m[i][j] = '|';
				
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

	private static void printMap(float[][] map) {
		for(int i = 0; i < 30; i++)
		{
			System.out.print("[");
			for(int j = 0; j < 30;j++)
			{
				String r = map[i][j] > 0? "|" : " ";
				System.out.print(r);
				
			}
			System.out.println("]");
		}
		
	}

}
