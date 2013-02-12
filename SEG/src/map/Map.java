/**
 * 
 */
package map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Vilian Atmadzhov
 *
 */
public class Map extends Observable{
	
	public static final float OCCThreshold = 0.8f;
	public static final float EMPTYThreshold = 0.2f;
	public static final float UNEXPLORED = 0.5f;
	public static final float OCCUPIED = 1;
	public static final float EMPTY = 0;
	
	
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
	
	
	private boolean updateMinMaxY(int x,int y){
		if (y>maxY) {
			maxY = y;
			return true;
		}
		else if(Math.abs(y)>minY) {
			minY=Math.abs(y);
			return true;
		}
		else if (x>maxX){
			maxX = x;
			return true;
		}
		else if (Math.abs(x)>minX){
			minX=Math.abs(x);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param x the horizontal index
	 * @param y the vertical index
	 * @return the value at the specified indices
	 * @throws IndexOutOfBoundsException if indices out of range
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
		return getValue(x,y) > Map.OCCThreshold;
	}
	public boolean isEmpty(int x,int y) {
		return getValue(x,y) < Map.EMPTYThreshold;
	}
	public boolean isUnexplored(int x,int y) {
		return (getValue(x,y) <= Map.OCCThreshold && getValue(x,y) >= Map.EMPTYThreshold);
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
