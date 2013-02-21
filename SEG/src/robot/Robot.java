/**
 * 
 */
package robot;

import java.awt.Point;

import explore.ExploreTest;
import map.Map;
import sense.Sense;
import javaclient3.GripperInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose2d;
import javaclient3.structures.gripper.PlayerGripperGeom;

/**
 * @author Albert
 * 
 *         Generic robot class provides the interface with player/stage
 * 
 */
public class Robot {

	PlayerClient robot = null;
	Position2DInterface pos2D = null;
	RangerInterface sonar = null;
	GripperInterface gripper = null;

	public static final int COLLECTION_SLEEP = 1;
	public static final int SENSE_SLEEP = 2;
	public static final int MOVE_SLEEP = 14;
	public static final int TURN_SLEEP = 4;
	public static final double TURN_RATE = 0.5;
	public static final double TURN_RATE_SLOW = 0.1;
	public static final double TURN_RATE_LIMIT = 0.3;//Below this, turn slowly
	public static final double SPEED_RATE = 0.5;
	public static final double TARGET_THRESHOLD = 0.05;
	public static final double HEADING_THRESHOLD = 0.05;
	

	public double x;
	public double y;
	private double yaw;
	private Map map;
	private double[] sonarValues;

	public Robot(Map map) {
		
		this(map,0);
	}
	
	public Robot(Map map, int index)
	{
		this.map = map;

		// Set up service proxies
		try {
			robot = new PlayerClient("localhost", 6665);
			gripper = robot.requestInterfaceGripper(index, PlayerConstants.PLAYER_OPEN_MODE);
			pos2D = robot.requestInterfacePosition2D(index,
					PlayerConstants.PLAYER_OPEN_MODE);
			sonar = robot.requestInterfaceRanger(index,
					PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("Simplebob: Error connecting to Player!\n>>>"
					+ e.toString());
			System.exit(1);
		}
		robot.runThreaded(-1, -1);
		collectionThread();
		senseThread();
	}

	/**
	 * It retrieves x,y,yaw and sensor readings
	 */
	private void collectionThread() {
		Thread collection = new Thread() {
			public void run() {

				while (true) {

					if (pos2D.isDataReady()) {
						x = pos2D.getX();
						y = pos2D.getY();
						yaw = pos2D.getYaw();
					}

					if (sonar.isDataReady()) {
						sonarValues = sonar.getData().getRanges();
					}

					try {
						sleep(COLLECTION_SLEEP);
					} catch (InterruptedException e) {
					}

				}
			}
		};
		collection.start();
	}

	private void senseThread() {
		Thread sense = new Thread() {
			public void run() {

				while (true) {
					PlayerPose2d pose = new PlayerPose2d(x, y, yaw);
					Sense.sense(map, sonarValues, pose);
					try {
						sleep(SENSE_SLEEP);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		sense.start();
	}

	public void moveP(char direction, double distance) {
		PlayerPose2d pos = new PlayerPose2d();

		switch (direction) {
		case 'u':
			pos.setPx(x);
			pos.setPy(y + distance);
			pos.setPa(Math.PI / 2);
			break;
		case 'd':
			pos.setPx(x);
			pos.setPy(y - distance);
			pos.setPa(-Math.PI / 2);
			break;
		case 'l':
			pos.setPx(x - distance);
			pos.setPy(y);
			pos.setPa(Math.PI);
			break;
		case 'r':
			pos.setPx(x + distance);
			pos.setPy(y);
			pos.setPa(0);
			break;

		}

		pos2D.setPosition(pos, pos, (byte) 1);

	}
	
	public void moveP (PlayerPose2d pose){
		pos2D.setPosition(pose, pose, (byte) 1);
		
	}
	
	private double targetYaw(double targetX, double targetY){
		double dy = Math.abs(targetY - y);
		double dx = Math.abs(targetX - x);
		double theta = 0;
		double opposite, adjacent;
		opposite = dy;
		adjacent = dx;
		if (targetX > x) {

			if (targetY > y) {
				// quad TR
				theta = Math.atan(opposite / adjacent);
			} else {
				// quad BR
				theta = -Math.atan(opposite / adjacent);
			}
		} else if (targetX == x) { // vertical
			if (targetY > y)
				theta = Math.PI / 2;// up
			else if (targetY < y)
				theta = -Math.PI / 2;// down
		} else if (targetY == y) {// horizontal
			if (targetX > x)
				theta = 0; // right
			else if (targetX < x)
				theta = Math.PI;// left
		} else {
			if (targetY > y) {
				// quad TL
				theta = Math.PI
						- Math.atan(opposite / adjacent);
			} else {
				// quad BL
				theta = -Math.PI
						+ Math.atan(opposite / adjacent);
			}
		}

		
		return theta;
	}
	/**
	 * Turn to the specified heading. 
	 * @param targetYaw The heading to turn to.
	 * @param stop whether to stop the robot after turning (faster if false when moving)
	 */
	public void turn(double targetYaw, boolean stop){
		while (Math.abs(targetYaw - yaw) > HEADING_THRESHOLD && Math.abs(targetYaw - yaw) < 2*Math.PI-HEADING_THRESHOLD ) {
			
			double a = targetYaw - yaw;
			if (a > Math.PI)
				a -= 2 * Math.PI;
			if (a < -Math.PI)
				a += 2 * Math.PI;
			
			if (a > 0){
				if (a > TURN_RATE_LIMIT)
				pos2D.setSpeed(0, TURN_RATE);// left
				else pos2D.setSpeed(0, TURN_RATE_SLOW);// left
			}
			else {
				if (a < -TURN_RATE_LIMIT)
				pos2D.setSpeed(0, -TURN_RATE);// right
				else pos2D.setSpeed(0, -TURN_RATE_SLOW);// right
			}
			
			try {
				Thread.sleep(TURN_SLEEP);
			} catch (InterruptedException e) {
			}
		}
		if(stop) pos2D.setSpeed(0, 0);//Remove for speed?
	}
	/**
	 * Move to the specified location
	 * @param tx internal X coordinate
	 * @param ty internal Y coordinate
	 */
	public void move(int tx, int ty){
		double px = tx * Map.SCALE;//convert to Player coords
		double py = ty * Map.SCALE;
		while (true) {
				if (Math.abs(px - x) < TARGET_THRESHOLD
						&& Math.abs(py - y) < TARGET_THRESHOLD) {
					pos2D.setSpeed(0, 0);
					break;//target reached
				}
				/*
				 * Decide which way to turn, to never turn more than 1/2 circle.
				 */
				double targetYaw = targetYaw(px,py);
				turn(targetYaw, false);

				pos2D.setSpeed(SPEED_RATE, 0);
			
			try {
				Thread.sleep(MOVE_SLEEP);
			} catch (InterruptedException e) {
			}
		}
		
	}
	/**
	 * WORK IN PROGRESS
	 * @param tx
	 * @param ty
	 * @param end
	 */
	public void explore_move(int tx, int ty, Point end){
		double px = tx * Map.SCALE;//convert to Player coords
		double py = ty * Map.SCALE;
		while(map.isUnexplored(end.x, end.y)){
				if (Math.abs(px - x) < TARGET_THRESHOLD
						&& Math.abs(py - y) < TARGET_THRESHOLD) {
					pos2D.setSpeed(0, 0);
					break;//target reached
				}
				/*
				 * Decide which way to turn, to never turn more than 1/2 circle.
				 */
				double targetYaw = targetYaw(px,py);
				turn(targetYaw,false);

				pos2D.setSpeed(SPEED_RATE, 0);
			
			try {
				Thread.sleep(MOVE_SLEEP);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void explore(){
		System.out.println("Explore request received");
		ExploreTest.exploreRobot(map, this, Map.convertPlayerToInternal(x, y));
	}
	
	public boolean pickUpObject()
	{
		int stored = gripper.getData().getStored();
		gripper.close();
		return gripper.getData().getStored() > stored;
		
	}
	public void dropObject(){
		gripper.open();
	}

}
