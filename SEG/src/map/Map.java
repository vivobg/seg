/**
 * 
 */
package map;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Vilian Atmadzhov
 *
 */
public class Map {
	
	static final float OCCThreshold = 0.8f;
	static final float EMPTYThreshold = 0.2f;
	static final float UNEXPLORED = 0.5f;
	
	private ArrayList<VerticalArray> posArray;
	private ArrayList<VerticalArray> negArray;
	private int minY=0; //handy for the GUI
	private int maxY=0; //handy for the GUI	
	
	public Map(){
		posArray=new ArrayList<VerticalArray>();
		negArray=new ArrayList<VerticalArray>();
	}
	
	
	private void updateMinMaxY(int y){
		if (y>maxY) maxY = y;
		else if(y<minY) minY=y;
	}
	
	/**
	 * 
	 * @param x the horizontal index
	 * @param y the vertical index
	 * @return the value at the specified indices
	 * @throws IndexOutOfBoundsException if indices out of range
	 */
	public float getValue(int x, int y){
		if (x < 0) {
			int negX = x * -1 - 1;
			return negArray.get(negX).getValue(y);
		} else
			return posArray.get(x).getValue(y);
	}
	/**
	 * Only use to read and NOT set (can mess up MinMax sizes)
	 * @param x the horizontal index
	 * @return the vertical array at the given index
	 */
	public VerticalArray getVertical(int x){
		if (x < 0) {
			int negX = x * -1 - 1;
			return negArray.get(negX);
		} else
			return posArray.get(x);
	}
	
	public void setValue(int x, int y, float value){
		if (x < 0) {
			// Convert to a "negative" index to work with negArray
			int negX = x * -1 - 1;
			// If array is already big enough
			if (negX < negArray.size())
				// Update the existing value
				negArray.get(negX).setValue(y, value);
			else {
				// Grow the array with default values, up to the needed index
				
				for (int i=0;i<negX-negArray.size();i++){
					negArray.add(new VerticalArray());
				}
				// Add the value of the needed index at the end
				negArray.add(new VerticalArray(y, value));
			}
		} else {
			// If array is already big enough
			if (x < posArray.size())
				// Update the existing value
				posArray.get(x).setValue(y, value);
			else {
				// Grow the array with default values, up to the needed index
				for (int i=0;i<y-posArray.size();i++){
					posArray.add(new VerticalArray());
				}
				// Add the value of the needed index at the end
				posArray.add(new VerticalArray(y, value));
			}
		}
		updateMinMaxY(y);
		
	}
	
	public int getMaxXSize(){return posArray.size();}
	public int getMinXSize(){return negArray.size();}
	public int getMaxYSize(){return maxY;}
	public int getMinYSize(){return minY;}
	
	public boolean isOccupied(int x,int y) {
		if (x < 0) {
			int negX = x * -1 - 1;
			return negArray.get(negX).isOccupied(y);
		} else
			return posArray.get(x).isOccupied(y);
	}
	public boolean isEmpty(int x,int y) {
		if (x < 0) {
			int negX = x * -1 - 1;
			return negArray.get(negX).isEmpty(y);
		} else
			return posArray.get(x).isEmpty(y);
	}
	public boolean isUnexplored(int x,int y) {
		if (x < 0) {
			int negX = x * -1 - 1;
			return negArray.get(negX).isUnexplored(y);
		} else
			return posArray.get(x).isUnexplored(y);
	}
	
	public static void main(String[] a){
		Map map = new Map();
		System.out.println(map.getMaxXSize());
	}

}
