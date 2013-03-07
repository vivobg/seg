/**
 * 
 */
package robot;

import java.awt.Point;
import java.util.List;

import javaclient3.FiducialInterface;
import javaclient3.GripperInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose2d;
import javaclient3.structures.fiducial.PlayerFiducialItem;
import map.Map;
import sense.Sense;
import explore.ExploreTest;

/**
 * 
 * 
 *Generic robot class provides the interface with player/stage
 * 
 */
public class Robot{

	PlayerClient robot = null;
	Position2DInterface pos2D = null;
	RangerInterface sonar = null;
	GripperInterface gripper = null;
	FiducialInterface fiducial = null;

	public static final int COLLECTION_SLEEP = 3;
	public static final int SENSE_SLEEP = 5;
	public static final int MOVE_SLEEP = 5;
	public static final int TURN_SLEEP = 5;
	public static final double TURN_RATE = 0.5;
	public static final double TURN_RATE_SLOW = 0.1;
	public static final double TURN_RATE_LIMIT = 0.3;// Below this, turn slowly
	public static final double SPEED_RATE = 0.5;
	public static final double TARGET_THRESHOLD = 0.085;
	public static final double HEADING_THRESHOLD = 0.05;
	public static final double ROBOT_SIZE = 0.50;
	public boolean isFollowing = false;
	public List<Point> currentOptimizedPath;
	public List<Point> currentPath;

	public Object moveLock = new Object();

	public double x;
	public double y;
	public double yaw;
	private Map map;
	private double[] sonarValues;
	public PlayerFiducialItem[] fiducialsInView;

	/**
	 * 
	 * @return the current pose of the robot
	 */
	public PlayerPose2d getPose(){
		return new PlayerPose2d(x,y,yaw);
	}

	public Robot(Map map) {

		this(map, 0);
	}

	public Robot(Map map, int index) {
		this.map = map;

		// Set up service proxies
		try {
			robot = new PlayerClient("localhost", 6665);
			gripper = robot.requestInterfaceGripper(index,
					PlayerConstants.PLAYER_OPEN_MODE);
			pos2D = robot.requestInterfacePosition2D(index,
					PlayerConstants.PLAYER_OPEN_MODE);
			sonar = robot.requestInterfaceRanger(index,
					PlayerConstants.PLAYER_OPEN_MODE);
			fiducial = robot.requestInterfaceFiducial(0,PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("Simplebob: Error connecting to Player!\n>>>"
					+ e.toString());
			System.exit(1);
		}
		robot.runThreaded(-1, -1);
		collectionThread();
		senseThread();
	}

	public double[] getSonar(){
		return sonarValues;
	}

	public double[] getFiducial() {
		throw new UnsupportedOperationException("Not Implemented Yet!");
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

					if(fiducial.isDataReady()){
						fiducialsInView = fiducial.getData().getFiducials();
	                }
					
					if(gripper.isDataReady()){
						if(gripper.getData().getBeams() > 0){
							gripper.close();
						}
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
					Sense.sonarScan(map, Robot.this);
					try {
						sleep(SENSE_SLEEP);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		sense.start();
	}

	/**
	 * Calculate the heading to the target
	 * 
	 * @param targetX
	 *            Target's X coordinate, Player units
	 * @param targetY
	 *            Target's Y coordinate, Player units
	 * @return the heading in radians
	 */
	private double targetYaw(double targetX, double targetY) {
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
				theta = Math.PI - Math.atan(opposite / adjacent);
			} else {
				// quad BL
				theta = -Math.PI + Math.atan(opposite / adjacent);
			}
		}

		return theta;
	}

	/**
	 * Turn to the specified heading.
	 * 
	 * @param targetYaw
	 *            The heading to turn to.
	 * @param stop
	 *            whether to stop the robot after turning (faster if false when
	 *            moving)
	 */
	public void turn(double targetYaw) {
		while (Math.abs(targetYaw - yaw) > HEADING_THRESHOLD
				&& Math.abs(targetYaw - yaw) < 2 * Math.PI - HEADING_THRESHOLD) {

			double a = targetYaw - yaw;
			if (a > Math.PI)
				a -= 2 * Math.PI;
			if (a < -Math.PI)
				a += 2 * Math.PI;

			if (a > 0) {
				if (a > TURN_RATE_LIMIT)
					pos2D.setSpeed(0, TURN_RATE);// left
				else
					pos2D.setSpeed(0, TURN_RATE_SLOW);// left
			} else {
				if (a < -TURN_RATE_LIMIT)
					pos2D.setSpeed(0, -TURN_RATE);// right
				else
					pos2D.setSpeed(0, -TURN_RATE_SLOW);// right
			}

			try {
				Thread.sleep(TURN_SLEEP);
			} catch (InterruptedException e) {
			}
		}
		pos2D.setSpeed(0, 0);// Remove for speed?
	}

	/**
	 * Move to the specified location
	 * 
	 * @param target
	 *            Target in internal coordinates
	 */
	public void move(Point target) {
		synchronized (moveLock){
			double px = target.x * Map.SCALE;// convert to Player coords
			double py = target.y * Map.SCALE;
			move(new PlayerPose2d(px, py, 0));
		}
	}

	/**
	 * Move to the specified location. Main move method. Other move methods call
	 * this method.
	 * 
	 * @param pose
	 *            Target in Player coordinates
	 */
	public void move(PlayerPose2d pose) {
		synchronized (moveLock){
			double px = pose.getPx();
			double py = pose.getPy();
			Point target =  new Point((int)px,(int)py);
			
			while (true) {
				if ((Math.abs(px - x) < TARGET_THRESHOLD
						&& Math.abs(py - y) < TARGET_THRESHOLD) || !search.AStarSearch.isAvailableCell(target, map) || 
						isTooCloseToWall() ) {
					pos2D.setSpeed(0, 0);
					break;// target reached
				}
				/*
				 * Decide which way to turn, to never turn more than 1/2 circle.
				 */
				double targetYaw = targetYaw(px, py);
				turn(targetYaw);
				double difference  = Math.sqrt(Math.pow(Math.abs(px -x) + Math.abs(py-y),2));

				pos2D.setSpeed(Math.min(1, difference / 2 * Math.PI), 0);

				try {
					Thread.sleep(MOVE_SLEEP);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private boolean isTooCloseToWall() {
		double threshold = 0.6;
			if(sonarValues[0] < threshold || sonarValues[1] < threshold || sonarValues[15] < threshold)
			{
				return true;
			}
		
		return false;
	}

	/**
	 * Used by RobotControl to move up,down,left,right
	 * 
	 * @param direction
	 * @param distance
	 */
	public void move(char direction, double distance) {
		synchronized (moveLock){
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

			move(pos);
		}

	}

	public void explore() {
		System.out.println("Explore request received");
		final Robot robot = this;
		Thread thr = new Thread(){
			public void run(){
				ExploreTest.exploreRobot(map, robot, Map.convertPlayerToInternal(x, y));
			}
		};
		thr.start();


	}


	/**
	 * Picks up an object
	 * 
	 * @return true if something was successfully picked up
	 */
	public boolean pickUpObject() {
		int stored = gripper.getData().getStored();
		gripper.close();
		gripper.store();
		return gripper.getData().getStored() > stored;

	}

	/**
	 * Drop the current object by opening gripper
	 */
	public void dropObject() {
		gripper.open();
	}

	/**
	 * Follows a path consisting of a given list of points
	 * 
	 * @param points
	 *            Points in the path
	 */
	public void followPath(List<Point> points) {
		for (Point p : points) {
			move(p);
		}

	}

}
