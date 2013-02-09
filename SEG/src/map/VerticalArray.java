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
public class VerticalArray {

	private ArrayList<Float> posArray;
	private ArrayList<Float> negArray;

	public VerticalArray(){
		setValue(0, Map.UNEXPLORED);
	}
	
	public VerticalArray(int y, float value){
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
			int negY = y * -1 - 1;
			return negArray.get(negY);
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
	public void setValue(int y, float value) {
		if (y < 0) {
			// Convert to a "negative" index to work with negArray
			int negY = y * -1 - 1;
			// If array is already big enough
			if (negY < negArray.size())
				// Update the existing value
				negArray.set(negY, value);
			else {
				// Grow the array with default values, up to the needed index
				negArray.addAll(Collections.nCopies(negY - negArray.size(),
						Map.UNEXPLORED));
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
				posArray.addAll(Collections.nCopies(y - posArray.size(),
						Map.UNEXPLORED));
				// Add the value of the needed index at the end
				posArray.add(value);
			}
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
	 * 
	 * @param y
	 *            The index to check if occupied
	 * @return True if above (>) occupied threshold, false otherwise
	 */
	public boolean isOccupied(int y) {
		return getValue(y) > Map.OCCThreshold;
	}

	/**
	 * 
	 * @param y
	 *            The index to check if empty
	 * @return True if below (<) empty threshold, false otherwise
	 */
	public boolean isEmpty(int y) {
		return getValue(y) < Map.EMPTYThreshold;
	}

	/**
	 * 
	 * @param y
	 *            The index to check if unexplored
	 * @return True if between occupied and empty thresholds (Occ >= Un >=
	 *         Empty)
	 */
	public boolean isUnexplored(int y) {
		return (getValue(y) <= Map.OCCThreshold && getValue(y) >= Map.EMPTYThreshold);
	}

}
