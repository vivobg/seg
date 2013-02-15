/**
 * 
 */
package map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Provides a dynamic 2D grid of float.
 * It is based on ArrayLists, and is suitable for storing
 * occupancy grid maps of arbitrary sizes, because it grows as needed.
 * @author Vilian Atmadzhov
 *
 */
public class Map extends Observable{
	
	public static final float OCCThreshold = 0.8f;
	public static final float EMPTYThreshold = 0.2f;
	public static final float UNEXPLORED = 0.5f;
	public static final float UNWALKABLE = 0.3f;
	public static final float OCCUPIED = 1;
	public static final float EMPTY = 0;
	public static final float TOO_CLOSE = 0.3f;//Player Units from wall
	public static final float SCALE = 0.1f;//Player units into 1 internal map unit
	//how many cells away from wall are empty, but too close to the wall
	public static final int UNWALKABLE_CELLS = Math.round(TOO_CLOSE / SCALE);
	
	private ArrayList<VerticalArray> posArray;
	private ArrayList<VerticalArray> negArray;
	private int minY=0; //handy for the GUI
	private int maxY=0; //handy for the GUI	
	private int minX=0; //handy for the GUI
	private int maxX=0; //handy for the GUI	
	
	public Map(){
		posArray=new ArrayList<VerticalArray>();
		negArray=new ArrayList<VerticalArray>();
		setValue(0, 0, Map.UNEXPLORED);
		setValue(1, 1, Map.UNEXPLORED);
		setValue(-1, -1, Map.UNEXPLORED);
	}
	
	public static Point convertCoordinates(float x, float y){
		int xi = (int) (x / Map.SCALE) ;
		int yi = (int) (y / Map.SCALE) ;
		return new Point(xi, yi);
	}
	
	
	private boolean updateMinMaxY(int x,int y){
		boolean grown = false;
		if (y>maxY) {
			maxY = y;
			grown =  true;
		}
		else if(y< 0 && Math.abs(y)>minY) {
			minY=Math.abs(y);
			grown =  true;
		}
		if (x>maxX){
			maxX = x;
			grown =  true;
		}
		else if (x<0 && Math.abs(x)>minX){
			minX=Math.abs(x);
			grown =  true;
		}
		return grown;
	}
	
	/**
	 * Always returns a value. If the location was never set
	 * it returns Map.UNEXPLORED
	 * @param x the horizontal index
	 * @param y the vertical index
	 * @return the value at the specified location
	 */
	public float getValue(int x, int y){
		try{
		if (x < 0) {
			x= Math.abs(x);
			return negArray.get(x).getValue(y);
		} else
			return posArray.get(x).getValue(y);
		}
		catch (IndexOutOfBoundsException e){
			return Map.UNEXPLORED;
		}
	}
	/**
	 * It is strongly recommended to use setValue() and getValue() instead
	 * Only use to read and NOT set (can mess up MinMax sizes)
	 * @param x the horizontal index
	 * @return the vertical array at the given index
	 */
	public VerticalArray getVertical(int x){
		if (x < 0) {
			x = Math.abs(x);
			return negArray.get(x);
		} else
			return posArray.get(x);
	}
	/**
	 * Updates the existing value, or grows the underlying array
	 * and writes the given value
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param value the value to set in the given location
	 */
	public void setValue(int x, int y, float value){
		
		ArrayList<VerticalArray> array;
		if (x<0) array = negArray;
		else array = posArray;
		
		int xc = Math.abs(x);
		
		if (xc < array.size()){// If array is already big enough
			array.get(xc).setValue(y, value);// Update the existing value
		}
		else {// Grow the array with default values, up to the needed index
			int size = array.size();
			for (int i=1;i<xc-size+1;i++){//*********************i=0
				array.add(new VerticalArray());
			}
			array.add(new VerticalArray(y, value));
		}
		
		boolean grown = updateMinMaxY(x,y);
		setChanged();
		notifyObservers(new CoordVal(x, y, value, grown));
		
	}
	
	public int getMaxXSize(){return posArray.size();}
	public int getMinXSize(){return negArray.size();}
	public int getMaxYSize(){return maxY;}
	public int getMinYSize(){return minY;}
	
	public boolean isOccupied(int x,int y) {
		return getValue(x, y) == Map.OCCUPIED;
	}
	public boolean isEmpty(int x,int y) {
		return getValue(x, y) == Map.EMPTY;
	}
	public boolean isUnexplored(int x,int y) {
		return getValue(x, y) == Map.UNEXPLORED;
	}
	public boolean isUnwalkable(int x,int y) {
		return getValue(x, y) == Map.UNWALKABLE;
	}
	
	public static void main(String[] a){
		Map map = new Map();
		
		map.setValue(0, 3, 0.3f);
		map.setValue(2, 4, 2.4f);
		map.setValue(4, -2, -4.2f);
		map.setValue(3, -5, -3.5f);
		
		map.setValue(-2, -2, -2.2f);
		//map.setValue(1, 1, 1.1f);
		//map.setValue(1, 2, 0.2f);
		//map.setValue(3, 4, 3.4f);
		//map.setValue(0, -1, 1.2f);
		//map.setValue(0, -2, 2.2f);
		//map.setValue(0, -3, 3.2f);
		//map.setValue(-2, -6, 4.2f);
		
		
		
		
		//positive X
		for (int x=0;x<map.getMaxXSize();x++)
		{	
			VerticalArray vert = map.getVertical(x);
			
			System.out.println(vert.toString());
				
		}
		System.out.println("Done positive X");
		//Negative X
		for (int x=1;x<map.getMinXSize();x++)
		{	
			VerticalArray vert = map.getVertical(-x);
			System.out.println(vert.toString());
				
		}
		System.out.println("Done negative X");
	}

}
