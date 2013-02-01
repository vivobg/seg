import java.util.ArrayList;

public class Map {
	private ArrayList<VerticalArray> posArray;
	private ArrayList<VerticalArray> negArray;
	
	static final float OccThreshold = 0.75f;
	static final float NonOccThreshold = 0.25f;
	
	
	
	public float getCell(int x, int y ){return 0.0f;}
	public void updateCell(int x, int y, float value){}
	public boolean isOccupied(int x, int y){return false;}
	public boolean isUnexplored(int x, int y ){return false;}
	public boolean isEmpty(int x, int y ){return false;}
}
