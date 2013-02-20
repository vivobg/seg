/**
 * 
 */
package robot;

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
	public static final int MOVE_SLEEP = 30;
	public static final boolean WAIT_MOVE = false;

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

	public void move(char direction, double distance) {
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
		
		while (WAIT_MOVE && Math.abs(pos.getPx() - x) > 0.1 && Math.abs(pos.getPy() - y) > 0.1){
			System.out.println("Waiting for robot to move");
			try {
				Thread.sleep(MOVE_SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void move (PlayerPose2d pose){
		pos2D.setPosition(pose, pose, (byte) 1);
		while (WAIT_MOVE && Math.abs(pose.getPx() - x) >0.1 && Math.abs(pose.getPy() - y) > 0.1){
			System.out.println("Waiting for robot to move to " + pose.getPx() + " " + pose.getPy());
			try {
				Thread.sleep(MOVE_SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void explore(){
		System.out.println("Explore request received");
		ExploreTest.exploreRobot(map, this, Map.convertCoordinates(x, y));
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
