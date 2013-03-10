package sense;

import robot.Robot;
import search.AStarSearch;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import map.Map;
import javaclient3.structures.fiducial.PlayerFiducialItem;

/**
 * @author Albert
 * 
 * GarbageItemManager is constructed with a Robot. It then adds that robots fiducialy discovered
 * GarbageItems to map.garbageListArray 
 */
public class GarbageManager {

	public static final int FIDUCIAL_SLEEP = 100;

	public static final int GO_FETCH_SLEEP = 1000;

	private static final int GRIPPER_THRESHOLD = (int) (1 / Map.SCALE);

	private Robot robot;
	private Map map;

	public GarbageManager(Robot robot, Map map){
		this.robot = robot;
		this.map = map;

		fiducialsInViewThread();
		printGarbageToCollectList();
		goFetchGarbage();
	}

	public void addItem(GarbageItem garbageItem){

		//check if garbage has already been added
		boolean garbageItemAlreadyExists = false;
		//calculates how many tiles a garbage item takes up. They appear to take up 0.2 player units.
		int threshold = (int) (0.2 / Map.SCALE);
		for(int i = 0; i < map.garbageListArray.size(); i++){
			if(Math.abs(garbageItem.getPoint().getX() - map.garbageListArray.get(i).getPoint().getX()) <= threshold &&
					Math.abs(garbageItem.getPoint().getY() - map.garbageListArray.get(i).getPoint().getY()) <= threshold){
				garbageItemAlreadyExists = true;
				break;
			}
		}

		if(!garbageItemAlreadyExists){
			map.garbageListArray.add(garbageItem);
		}

	}

	private void fiducialsInViewThread() {
		Thread collection = new Thread() {
			public void run() {
				while(true){
					if (robot.fiducialsInView != null) {
						for(int i = 0; i < robot.fiducialsInView.length; i++ ){
							try {
								double Py = robot.fiducialsInView[i].getPose().getPy();
								// +0.2 accounts for the fiducial sensor being slightly forward on the robot.
								double Px = robot.fiducialsInView[i].getPose().getPx() + 0.2;
								double distance = Math.sqrt(Py*Py + Px*Px);
								double diffX = Math.cos(robot.yaw + Math.atan(Py / Px)) * distance;
								double diffY = Math.sin(robot.yaw + Math.atan(Py / Px)) * distance;

								addItem(new GarbageItem(Map.convertPlayerToInternal(robot.x + diffX, robot.y + diffY),false));
							} catch (ArrayIndexOutOfBoundsException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					try {
						sleep(FIDUCIAL_SLEEP);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		collection.start();
	}

	private void goFetchGarbage() {
		Thread collection = new Thread() {
			public void run() {
				while(true){
					
					if(map.garbageListArray.size() == 2){
						for(int i = 0; i < map.garbageListArray.size(); i++){
							
							Point garbagePoint = map.garbageListArray.get(i).getPoint();
							
							List<Point> list = AStarSearch.aSearch(map, 
									robot.getPoint(),
									garbagePoint);
							System.out.println(list.size());
							for (int j = 0; j < list.size(); j++) {
								robot.move(list.get(j));
								if (robot.gripperData.getBeams() > 0 &&
										Math.abs(robot.getPoint().x - garbagePoint.x) < GRIPPER_THRESHOLD &&
										Math.abs(robot.getPoint().y - garbagePoint.y) < GRIPPER_THRESHOLD) {
									robot.gripper.close();
									break;
								}
							}
							
							list = AStarSearch.aSearch(map, 
									robot.getPoint(),
									Map.convertPlayerToInternal(-9, -4));
							System.out.println(list.size());
							for (int k = 0; k < list.size(); k++)
								robot.move(list.get(k));
							
							robot.gripper.open();
						}
					}
					try {
						sleep(GO_FETCH_SLEEP);
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
					//for(int i = 0; i < map.garbageListArray.size(); i++ )
					//	System.out.println(map.garbageListArray.get(i).getPoint().toString());

					System.out.println("*** " + "map.garbageListArray Size is " + map.garbageListArray.size() + " ***");
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

