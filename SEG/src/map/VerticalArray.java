/**
 * 
 */
package map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Vilian Atmadzhov
 * 
 */
public class VerticalArray implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8012591684140451394L;	
	private ArrayList<Byte> posArray;
	private ArrayList<Byte> negArray;

	public VerticalArray(){
		
		posArray=new ArrayList<Byte>();
		negArray=new ArrayList<Byte>();
		//negArray.add((byte) -123);
		setValue(0, Map.UNEXPLORED);
	}
	
	public VerticalArray(int y, byte value){
		posArray=new ArrayList<Byte>();
		negArray=new ArrayList<Byte>();
		//negArray.add((byte) -123);
		setValue(0, Map.UNEXPLORED);
		setValue(y, value);
	}
	
	
	
	/**
	 * @param y
	 *            The index of the array
	 * @return The content of the cell
	 * @throws IndexOutOfBoundsException
	 *             - if the index is out of range
	 */
	public byte getValue(int y) {
		if (y < 0) {
			y = Math.abs(y);
			return negArray.get(y);
		} else
			return posArray.get(y);
	}

	/**
	 * HEAVY TESTING NEEDED! Updates the given index. If the array is smaller,
	 * it is grown and filled with default values and then the given index is
	 * inserted.
	 * 
	 * @param y
	 *            The index of the array to update
	 * @param value
	 *            The value to write into the cell
	 */
	public void setValue(int y, byte value) {
		
		ArrayList<Byte> array;
		if (y<0) array = negArray;
		else array = posArray;
		
		y = Math.abs(y);
		
		if (y < array.size()){// If array is already big enough
			array.set(y,value);// Update the existing value
		}
		else {// Grow the array with default values, up to the needed index
			int size = array.size();
			for (int i=0;i<y-size+1;i++){
				array.add(Map.UNEXPLORED);
			}
			array.set(y,value);// Add the value of the needed index at the end
		}
		
		
		
		
		/*
		
		if (y < 0) {
			// Convert to a "negative" index to work with negArray
			int negY = Math.abs(y);
			// If array is already big enough
			if (negY < negArray.size())
				// Update the existing value
				negArray.set(negY, value);
			else {
				// Grow the array with default values, up to the needed index
				for (int i=0;i<negY-negArray.size();i++){//*********************i=0
					negArray.add(Map.UNEXPLORED);
				}
				// Add the value of the needed index at the end
				negArray.add(value);
			}
		} else {
			// If array is already big enough
			if (y < posArray.size())
				// Update the existing value
				posArray.set(y, value);
			else {
				// Grow the array with default values, up to the needed index
				for (int i=0;i<y-posArray.size();i++){//*********************************i=0
					posArray.add(Map.UNEXPLORED);
				}
				// Add the value of the needed index at the end
				posArray.add(value);
			}
		}*/
	}

	/**
	 * 
	 * @return The size of the positive array
	 */
	public int getPosSize() {
		return posArray.size();
	}

	/**
	 * 
	 * @return The size of the negative array
	 */
	public int getNegSize() {
		return negArray.size();
	}

	/**
	 * 
	 * @param y
	 *            The index to check if occupied
	 * @return True if above (>) occupied threshold, false otherwise
	 */
	public boolean isOccupied(int y) {
		return getValue(y) == Map.OCCUPIED;
	}

	/**
	 * 
	 * @param y
	 *            The index to check if empty
	 * @return True if below (<) empty threshold, false otherwise
	 */
	public boolean isEmpty(int y) {
		return getValue(y) == Map.EMPTY;
	}

	/**
	 * 
	 * @param y
	 *            The index to check if unexplored
	 * @return True if between occupied and empty thresholds (Occ >= Un >=
	 *         Empty)
	 */
	public boolean isUnexplored(int y) {
		return (getValue(y) == Map.UNEXPLORED);
	}
	
	public String toString(){
		
		StringBuffer sb = new StringBuffer("[");
		/*for (int i=getNegSize()-3;i>=0;i++){
			sb.append(negArray.get(i) + ", ");
			//sb.insert(offset, d)
		}
		sb.append("]");*/
		Iterator<Byte> it= negArray.iterator();
		while (it.hasNext()){
			sb.insert(1, it.next()+", ");
			
		}
		sb.append("]");
		//sb.append(negArray.toString());
		sb.append(posArray.toString());
		return sb.toString();
		
	}

}
