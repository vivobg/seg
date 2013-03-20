/**
 * 
 */
package map;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import robot.Robot;
import sense.GarbageItem;

/**
 * Provides a dynamic 2D array of floats. It is based on ArrayLists, and is
 * suitable for storing occupancy grid maps of arbitrary sizes, because it grows
 * as needed. The integer part indicates the TYPE of the cell (empty, unexplored, etc.),
 * while the decimal part contains the encoded DISTANCE at which the cell was observed.
 * 
 * @author Vilian Atmadzhov
 * 
 */
public class Map implements Serializable{
	
	private static final long serialVersionUID = 7594975695476674486L;
	/**
	 * 
	 * @return The list of discovered garbage objects
	 */
	public List<GarbageItem> getGarbageListArray() {
		return garbageListArray;
	}
	/**
	 * 
	 * @return The underlying positive X array
	 */
	public ArrayList<VerticalArray> getPosArray() {
		return posArray;
	}
	/**
	 * 
	 * @return The underlying negative X array
	 */
	public ArrayList<VerticalArray> getNegArray() {
		return negArray;
	}
	public transient List<Robot> robotList;
	public List<GarbageItem> garbageListArray;// GUI code based on this
	public transient HashMap<Point, Boolean> garbageList;// Abdi's code based on this
	/**
	 * 
	 * @return The minimum Y value of the map with the sign stripped, as the minimum Y
	 * can only be 0 or less and the indices of the negative array are positive.
	 */
	public int getMinY() {
		return minY;
	}
	/**
	 * 
	 * @return The maximum Y value of the map, 0 or greater.
	 */
	public int getMaxY() {
		return maxY;
	}
	/**
	 * 
	 * @return The minimum X value of the map with the sign stripped, as the minimum X
	 * can only be 0 or less and the indices of the negative array are positive.
	 */
	public int getMinX() {
		return minX;
	}

	public void setGarbageListArray(List<GarbageItem> garbageListArray) {
		this.garbageListArray = garbageListArray;
	}

	public void setPosArray(ArrayList<VerticalArray> posArray) {
		this.posArray = posArray;
	}

	public void setNegArray(ArrayList<VerticalArray> negArray) {
		this.negArray = negArray;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}
	//Fiducially explored cells have the sign flipped.
	public static final float UNEXPLORED = 1.95f;//Allow to be overwritten
	public static final float EMPTY 	 = 2;
	public static final float BUFFER     = 3;
	public static final float WALL 	 	 = 4;
	public static final float FAR_WALL = Map.WALL + 0.35f;//3.5meters
	
	public static final float TOO_CLOSE = 0.7f;// Player Units from wall
	public static final float SCALE = 0.1f;// Player units into 1 internal map
											// unit
	// how many cells away from wall are empty, but too close to the wall
	public static final int BUFFER_CELLS = Math.round(TOO_CLOSE / SCALE);

	private ArrayList<VerticalArray> posArray;
	private ArrayList<VerticalArray> negArray;
	private int minY = 0; // handy for the GUI
	private int maxY = 0; // handy for the GUI
	private int minX = 0; // handy for the GUI
	private int maxX = 0; // handy for the GUI
	/**
	 * Initialise a new Map instance, with empty garbage and robot lists.
	 */
	public Map() {
		posArray = new ArrayList<VerticalArray>();
		negArray = new ArrayList<VerticalArray>();
		garbageList = new HashMap<Point, Boolean>();
		garbageListArray = new ArrayList<GarbageItem>();
		robotList = new ArrayList<Robot>();
		setValue(0, 0, Map.UNEXPLORED,0);
		setValue(1, 1, Map.UNEXPLORED,0);
		setValue(-1, -1, Map.UNEXPLORED,0);
	}
	/**
	 * Add a robot to the map's list of robots.
	 * @param r
	 */
	public void addRobot(Robot r){
		robotList.add(r);
	}
	/**
	 * 
	 * @return The map's robot list.
	 */
	public List<Robot> getRobotList(){
		return robotList;
	}
	/**
	 * Convert from Player coordinates to internal map coordinates
	 * @param x the X Player coordinate
	 * @param y the Y Player coordinate
	 * @return a Point with the converted internal coordinates
	 */
	public static Point convertPlayerToInternal(double x, double y) {
		int xi = (int) Math.round(x / Map.SCALE);
		int yi = (int) Math.round(y / Map.SCALE);
		return new Point(xi, yi);
	}
	/**
	 * Update the MinMax Y values
	 * @param x The x coordinate of the new cell.
	 * @param y The y coordinate of the new cell.
	 * @return true if the map's dimensions increased, false otherwise.
	 */
	private boolean updateMinMaxY(int x, int y) {
		boolean grown = false;
		if (y > maxY) {
			maxY = y;
			grown = true;
		} else if (y < 0 && Math.abs(y) > minY) {
			minY = Math.abs(y);
			grown = true;
		}
		if (x > maxX) {
			maxX = x;
			grown = true;
		} else if (x < 0 && Math.abs(x) > minX) {
			minX = Math.abs(x);
			grown = true;
		}
		return grown;
	}

	/**
	 * Always returns a value. If the location was never set it returns
	 * Map.UNEXPLORED
	 * 
	 * @param x
	 *            the horizontal index
	 * @param y
	 *            the vertical index
	 * @return the value at the specified location
	 */
	public float getValue(int x, int y) {
		try {
			if (x < 0) {
				x = Math.abs(x);
				return negArray.get(x).getValue(y);
			} else
				return posArray.get(x).getValue(y);
		} catch (IndexOutOfBoundsException e) {
			return Map.UNEXPLORED;
		}
	}
	/**
	 * Extract the encoded distance for the specified cell
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return the decoded distance from which the cell was observed.
	 */
	public double getSonarDistance(int x, int y){
		double value = Math.abs(getValue(x, y));
		//Remove integer part
		value -=  Math.floor(value);
		//Multiply by 10 to extract sonar difference
		value *= 10;
		return value;
		
	}
	/**
	 * Calculate the distance in Player units between two Points in internal coordinates.
	 * @param a the first Point
	 * @param b the second Point
	 * @return the Player distance between the two points
	 */
	public static double calculateSonarDistance(Point a, Point b){
		return a.distance(b)*Map.SCALE;
	}
	/**
	 * Encode the given Player distance
	 * @param distance the distance to encode
	 * @return the encoded distance, as a decimal with a 0 integer part
	 */
	public static float encodeSonarDifference(double distance){
		return (float) (distance/10);//nothing to do with map scale
	}

	/**
	 * A wrapper method to abstract the internal representation from the caller.
	 * The caller can just provide Player/Stage coordinates. Always returns a
	 * value. If the location was never set it returns Map.UNEXPLORED
	 * 
	 * @param x
	 *            the horizontal index
	 * @param y
	 *            the vertical index
	 * @return the value at the specified location
	 *//*
	public byte getValue(float x, float y) {
		Point c = convertPlayerToInternal(x, y);
		return getValue(c.x, c.y);
	}*/

	/**
	 * It is strongly recommended to use setValue() and getValue() instead Only
	 * use to read and NOT set (can mess up MinMax sizes)
	 * 
	 * @param x
	 *            the horizontal index
	 * @return the vertical array at the given index
	 */
	public VerticalArray getVertical(int x) {
		if (x < 0) {
			x = Math.abs(x);
			return negArray.get(x);
		} else
			return posArray.get(x);
	}
	/**
	 * Update/create the specified cell with the given value
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @param value the value to be stored in the specified cell
	 */
	private void updateMap(int x, int y, float value){
		ArrayList<VerticalArray> array;
		if (x < 0)
			array = negArray;
		else
			array = posArray;

		int xc = Math.abs(x);

		if (xc < array.size()) {// If array is already big enough
			array.get(xc).setValue(y, value);// Update the existing value
		} else {// Grow the array with default values, up to the needed index
			int size = array.size();
			for (int i = 1; i < xc - size + 1; i++) {
				array.add(new VerticalArray());
			}
			array.add(new VerticalArray(y, value));
		}

		updateMinMaxY(x, y);
	}
	/**
	 * Set a cell as explored by the fiducial sensor or not
	 * @param x Horizontal coordinate
	 * @param y Vertical Coordinate
	 * @param explored Explored or Not
	 */
	public synchronized void setFiducialExplored(int x, int y) {
		float val = getValue(x, y);
		if (val > 0) val *= -1;	
		updateMap(x, y, val);
	}

	/**
	 * Updates the existing value, or grows the underlying array and writes the
	 * given value(positive). Use setFiducialExplored() to set fiducial flag.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param value
	 *            the value to set in the given location, assumes positive value
	 */
	public synchronized void setValue(int x, int y, float value, double distance) {
		//Only update, if cell was observed from a closer range.
		value += Map.encodeSonarDifference(distance);
		//Preserve fiducial status
		if (isFiducialExplored(x, y)) value *=-1;
		updateMap(x, y, value);
		//}
	}

	/**
	 * A wrapper method to abstract internal representation from caller. The
	 * caller can give Player/Stage coordinates directly. Updates the existing
	 * value, or grows the underlying array and writes the given value
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param value
	 *            the value to set in the given location
	 *//*
	public void setValue(float x, float y, byte value) {
		Point c = convertPlayerToInternal(x, y);
		setValue(c.x, c.y, value);
	}*/
	
	
	/**
	 * 
	 * @return the size of the internal positive x array
	 */
	public int getMaxXSize() {
		return posArray.size();
	}
	/**
	 * 
	 * @return the size of the internal negative x array
	 */
	public int getMinXSize() {
		return negArray.size();
	}
	/**
	 * 
	 * @return the size of the maximum positive y array
	 */
	public int getMaxYSize() {
		return maxY;
	}
	/**
	 * 
	 * @return the size of the minimum positive y array
	 */
	public int getMinYSize() {
		return minY;
	}
	/**
	 * Check if the specified cell is a FAR WALL
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return true if the cell is FAR WALL, false otherwise
	 */
	public boolean isFarWall(int x, int y) {
		//return getValue(x, y) == Math.abs(Map.OCCUPIED);
		float value  = Math.abs(getValue(x, y));
		return value > Map.FAR_WALL;
	}
	/**
	 * Check if the specified cell is a WALL
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return true if the cell is WALL, false otherwise
	 */
	public boolean isOccupied(int x, int y) {
		//return getValue(x, y) == Math.abs(Map.OCCUPIED);
		float value  = Math.abs(getValue(x, y));
		return value >= Map.WALL && value <= Map.FAR_WALL;
	}
	/**
	 * Check if the specified cell is EMPTY
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return true if the cell is EMPTY, false otherwise
	 */
	public boolean isEmpty(int x, int y) {
		float value  = Math.abs(getValue(x, y));
		return value >= Map.EMPTY && value < Map.BUFFER;
	}
	/**
	 * Check if the specified cell is UNEXPLORED
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return true if the cell is UNEXPLORED, false otherwise
	 */
	public boolean isUnexplored(int x, int y) {
		float value  = Math.abs(getValue(x, y));
		return value >= Map.UNEXPLORED && value < Map.EMPTY;
	}
	/**
	 * Check if the specified cell is BUFFER
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return true if the cell is BUFFER, false otherwise
	 */
	public boolean isBuffer(int x, int y) {
		float value  = Math.abs(getValue(x, y));
		return value >= Map.BUFFER && value < Map.WALL;
	}
	/**
	 * Check if the specified cell is fiducially explored.
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return true if the cell is fiducially explored, false otherwise
	 */
	public boolean isFiducialExplored(int x, int y){
		return getValue(x,y) < 0;
	}
	/*
	public boolean isOccupied(float x, float y) {
		return getValue(x, y) == Math.abs(Map.OCCUPIED);
	}
	
	
	public boolean isEmpty(float x, float y) {
		return getValue(x, y) == Math.abs(Map.EMPTY);
	}

	public boolean isUnexplored(float x, float y) {
		return getValue(x, y) == Math.abs(Map.UNEXPLORED);
	}

	public boolean isUnwalkable(float x, float y) {
		return getValue(x, y) == Math.abs(Map.BUFFER);
	}
	*/
	public static void main(String[] a) {
		Map map = new Map();

		map.setValue(0, 3, Map.EMPTY, 4);
		map.setValue(2, 4, Map.WALL, 3);
		map.setValue(4, -2, Map.BUFFER, 2);
		map.setValue(3, -5, Map.UNEXPLORED, 2);

		map.setValue(-2, -2, Map.EMPTY, 2);
		// map.setValue(1, 1, 1.1f);
		// map.setValue(1, 2, 0.2f);
		// map.setValue(3, 4, 3.4f);
		// map.setValue(0, -1, 1.2f);
		// map.setValue(0, -2, 2.2f);
		// map.setValue(0, -3, 3.2f);
		// map.setValue(-2, -6, 4.2f);

		// positive X
		for (int x = 0; x < map.getMaxXSize(); x++) {
			VerticalArray vert = map.getVertical(x);

			System.out.println(vert.toString());

		}
		System.out.println("Done positive X");
		// Negative X
		for (int x = 1; x < map.getMinXSize(); x++) {
			VerticalArray vert = map.getVertical(-x);
			System.out.println(vert.toString());

		}
		System.out.println("Done negative X");
	}
	/**
	 * 
	 * @return The maximum X value of the map, 0 or greater.
	 */
	public int getMaxX() {
		return maxX;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

}
