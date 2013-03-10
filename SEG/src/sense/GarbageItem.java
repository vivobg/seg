package sense;

import java.awt.Point;
import java.io.Serializable;

/**
 * @author Albert
 * 
 * GarbageItem contains a point and a isCollected boolean
 */
public class GarbageItem implements Serializable{

	public Point point;   
	public boolean isCollected;    

	public GarbageItem (Point point, boolean isCollected){
		this.point = point;
		this.isCollected = isCollected;
	}
	
	public Point getPoint() {
		return this.point;
	}

	public boolean getIsCollected () {
		return this.isCollected;
	}

	public void setIsCollected (boolean isCollected) {
		this.isCollected = isCollected;
	}
}

