package sense;

import java.awt.Point;
import java.io.Serializable;

/**
 * @author Albert
 * Class describing each discovered garbage object.
 * GarbageItem contains the coordinates of the garbage, and a boolean
 * flag of whether the garbage has been collected or not
 */
public class GarbageItem implements Serializable{

	public Point point;   
	public boolean isCollected;    
	/**
	 * A new garbage item instance
	 * @param point the coordinates of the new garbage item
	 * @param isCollected the collected status of the new garbage item
	 */
	public GarbageItem (Point point, boolean isCollected){
		this.point = point;
		this.isCollected = isCollected;
	}
	/**
	 * 
	 * @return the coordinates of the garbage item
	 */
	public Point getPoint() {
		return this.point;
	}
	/**
	 * 
	 * @param point The new coordinates of the garbage item
	 */
	public void setPoint(Point point) {
		this.point = point;
	}
	/**
	 * Query the collection status of the garbage item
	 * @return  true if collected, false otherwise
	 */
	public boolean getIsCollected () {
		return this.isCollected;
	}
	/**
	 * Update the collection status of the garbage item
	 * @param isCollected true if collected, false otherwise
	 */
	public void setIsCollected (boolean isCollected) {
		this.isCollected = isCollected;
	}
}

