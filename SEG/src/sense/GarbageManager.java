package sense;

import robot.Robot;
import java.awt.Point;
import java.util.ArrayList;
import map.Map;
import javaclient3.structures.fiducial.PlayerFiducialItem;

public class GarbageManager {

	public static ArrayList<GarbageItem> garbageToCollectList;
	public static final int FIDUCIAL_SLEEP = 1;
	
	private Robot robot;
	
	public GarbageManager(Robot robot){
		//it will only be instantiated once because it's static
		garbageToCollectList = new ArrayList<GarbageItem>();
		this.robot = robot;
		
		fiducialsInViewThread();
		//printGarbageToCollectList();
	}

	public void addItem(GarbageItem garbageItem){

		//check if garbage has already been added
		boolean garbageItemAlreadyExists = false;
		for(int i = 0; i < garbageToCollectList.size(); i++){
			if(garbageItem.getPoint().equals(garbageToCollectList.get(i).getPoint())){
				garbageItemAlreadyExists = true;
			}
		}

		if(!garbageItemAlreadyExists){
			garbageToCollectList.add(garbageItem);
		}

	}

	private void fiducialsInViewThread() {
		Thread collection = new Thread() {
			public void run() {
				while(true){
					if (robot.fiducialsInView != null) {
						for(int i = 0; i < robot.fiducialsInView.length; i++ ){
							double x = robot.x - robot.fiducialsInView[i].getPose().getPy();
							double y = robot.fiducialsInView[i].getPose().getPx() + robot.y;
							
							System.out.println("getPx() " + robot.fiducialsInView[i].getPose().getPx());
							System.out.println("getPy() " + robot.fiducialsInView[i].getPose().getPy());
							//System.out.println("X = " + robot.x + " Y = " + robot.y);
							//System.out.println("Combined X " + x + "Combined Y " + y);
							//addItem(new GarbageItem(Map.convertCoordinates(x, y),false));
						}
					}
	
					try {
						sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		collection.start();
	}
	
	private void printGarbageToCollectList() {
		Thread collection = new Thread() {
			public void run() {
				while(true){
					for(int i = 0; i < garbageToCollectList.size(); i++ )
					System.out.println(garbageToCollectList.get(i).getPoint().toString());
					System.out.println("*************" + "Array Size is " + garbageToCollectList.size());
					System.out.println("X = " + robot.x + " Y = " + robot.y);
					try {
						sleep(1000);
					} catch (InterruptedException e) {
					}
				}		
			}
		};
		collection.start();
	}
}

