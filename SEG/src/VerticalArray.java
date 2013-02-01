import java.util.ArrayList;


public class VerticalArray {
	private ArrayList<Float> posArray;
	private ArrayList<Float> negArray;
	
	/**
	 * Return's the probability of a specified cell being occupied
	 * @param y the vertical index
	 * @return probability value
	 */
	public float getCell(int y)
	{
		if(y >= 0 ) {
			return posArray.get(y);
		}
		else{
			return negArray.get(-1 *(y) -1);
		}
	}
	
	public void updateCell(int y,float value)
	{
		if(y >= 0 ) {
			posArray.set(y, value);
		}
		else{
			negArray.set(-1 *(y) -1,value);
		}
	}
	
	public boolean isOccupied(int y){return (getCell(y) >= Map.OccThreshold);}
	public boolean isUnexplored(int y ){return (getCell(y) == 0.5);}
	public boolean isEmpty(int y ){return (getCell(y) <= Map.NonOccThreshold);}
}
