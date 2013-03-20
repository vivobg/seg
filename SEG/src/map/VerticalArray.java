package map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A one-dimension dynamic array, which can grow in either direction. It stores a float
 * in each cell.
 * 
 */
public class VerticalArray implements Serializable{

	private static final long serialVersionUID = 8012591684140451394L;	
	private ArrayList<Float> posArray;
	private ArrayList<Float> negArray;
	/**
	 * initialise a new VerticalArray with an UNEXPLORED 0 index
	 */
	public VerticalArray(){
		
		posArray=new ArrayList<Float>();
		negArray=new ArrayList<Float>();
		setValue(0, Map.UNEXPLORED);
	}
	/**
	 * initialise a new VerticalArray with an UNEXPLORED 0 index, and
	 * the specified index with the given value
	 * @param y the y coordinate
	 * @param value the value to give to the cell
	 */
	public VerticalArray(int y, float value){
		posArray=new ArrayList<Float>();
		negArray=new ArrayList<Float>();
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
	public float getValue(int y) {
		if (y < 0) {
			y = Math.abs(y);
			return negArray.get(y);
		} else
			return posArray.get(y);
	}

	/**
	 * Updates the given index. If the array is smaller,
	 * it is grown and filled with default values and then the given index is
	 * inserted.
	 * 
	 * @param y
	 *            The index of the array to update
	 * @param value
	 *            The value to write into the cell
	 */
	public void setValue(int y, float value) {
		
		ArrayList<Float> array;
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
	 * Returns a string representation of the Vertical array.
	 */
	public String toString(){
		
		StringBuffer sb = new StringBuffer("[");
		/*for (int i=getNegSize()-3;i>=0;i++){
			sb.append(negArray.get(i) + ", ");
			//sb.insert(offset, d)
		}
		sb.append("]");*/
		Iterator<Float> it= negArray.iterator();
		while (it.hasNext()){
			sb.insert(1, it.next()+", ");
			
		}
		sb.append("]");
		//sb.append(negArray.toString());
		sb.append(posArray.toString());
		return sb.toString();
		
	}

}
