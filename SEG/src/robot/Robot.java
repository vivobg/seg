/**
 * 
 */
package robot;

import java.awt.Point;
import java.util.List;

import control.Control;

import robot.Robot;

import javaclient3.FiducialInterface;
import javaclient3.GripperInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerPose2d;
import javaclient3.structures.fiducial.PlayerFiducialItem;
import map.GarbageItem;
import map.Map;
import search.Search;
import sense.Sense;

/**
 * Generic robot class providing the interface with player/stage and providing
 * the basic functionality of the robot
 */
public class Robot {
	PlayerClient robot = null;
	private Position2DInterface pos2D = null;
	private RangerInterface sonar = null;
	private GripperInterface gripper = null;
	private FiducialInterface fiducial = null;
	private static final int COLLECTION_SLEEP = 20;
	private static final int SENSE_SLEEP = 20;
	private static final int MOVE_SLEEP = 5;
	private static final int MOVE_BACK_SLEEP = 15;
	private static final int TURN_SLEEP = 5;
	private static final double TURN_RATE = 0.5;
	private static final double TURN_RATE_SLOW = 0.1;
	private static final double TURN_RATE_LIMIT = 0.3;// Below this, turn slowly
	private static final double TARGET_THRESHOLD = 0.085;
	private static final double HEADING_THRESHOLD = 0.03;
	public static final double ROBOT_SIZE = 0.50;
	private static final int FIDUCIAL_SLEEP = 100;
	public boolean isFollowing = false;
	public List<Point> currentOptimizedPath;
	public List<Point> currentPath;
	private Object moveLock = new Object();
	public Object sensorLock = new Object();
	public double x;
	public double y;
	public double yaw;
	private Map map;
	private Control control;
	public final int index;
	private double[] sonarValues;
	private PlayerFiducialItem[] fiducialsInView;
	public RobotState Status;

	private boolean goFetchGarbageHasBeenCalled = false;
	private boolean carryingGarbage = false;
	private Point x1y1;
	private Point x2y2;
	private int currentGarbageIndex;
	private int GARBAGE_AREA_BOUNDARY = 3;

	/**
	 * 
	 * @return the current pose of the robot
	 */
	public PlayerPose2d getPose() {
		return new PlayerPose2d(x, y, yaw);
	}

	/**
	 * Initialise a new Robot instance
	 * 
	 * @param control
	 *            the control instance to use
	 * @param index
	 *            the index of the robot
	 */
	public Robot(Control control, int index) {
		this.control = control;
		this.index = index;
		this.map = this.control.getMap();

		// Set up service proxies
		try {
			robot = new PlayerClient("localhost", 6665);
			gripper = robot.requestInterfaceGripper(index,
					PlayerConstants.PLAYER_OPEN_MODE);
			pos2D = robot.requestInterfacePosition2D(index,
					PlayerConstants.PLAYER_OPEN_MODE);
			sonar = robot.requestInterfaceRanger(index,
					PlayerConstants.PLAYER_OPEN_MODE);
			fiducial = robot.requestInterfaceFiducial(index,
					PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("Simplebob: Error connecting to Player!\n>>>"
					+ e.toString());
			System.exit(1);
		}
		robot.runThreaded(-1, -1);
		collectionThread();

		senseThread();
		lookOutForGarbageThread();
		setStatus(RobotState.Idle);
	}

	/**
	 * 
	 * @return the sonar values
	 */
	public double[] getSonar() {
		return sonarValues;
	}

	/**
	 * 
	 * @return the current position of the robot, in internal coordinates
	 */
	public Point getRobotPosition() {
		return Map.convertPlayerToInternal(x, y);
	}

	/**
	 * Retrieves x,y,yaw and sensor readings every N milliseconds
	 */
	private void collectionThread() {
		Thread collection = new Thread() {
			public void run() {
				while (true) {
					synchronized (sensorLock) {
						if (pos2D.isDataReady()
								&& sonar.isDataReady()
								&& fiducial.isDataReady()
								&& pos2D.getTimestamp() == fiducial
										.getTimestamp()
								&& pos2D.getTimestamp() == sonar.getTimestamp()) {

							x = pos2D.getX();
							y = pos2D.getY();
							yaw = pos2D.getYaw();
							sonarValues = sonar.getData().getRanges().clone();
							fiducialsInView = fiducial.getData().getFiducials();
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

	/**
	 * Update the map with the sensor data every N milliseconds
	 */
	private void senseThread() {
		Thread sense = new Thread() {
			public void run() {

				while (!goFetchGarbageHasBeenCalled) {
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
	public void turn(double targetYaw, double rate, Point target, Point end) {
		while (Math.abs(targetYaw - yaw) > HEADING_THRESHOLD
				&& Math.abs(targetYaw - yaw) < 2 * Math.PI - HEADING_THRESHOLD) {

			if (!isValidMoveCondition(target, end))
				break;

			if (isRobotStuckExploration())
				break;
			isRobotStuckGarbageCollection();

			double a = targetYaw - yaw;
			if (a > Math.PI)
				a -= 2 * Math.PI;
			if (a < -Math.PI)
				a += 2 * Math.PI;

			if (a > 0) {
				if (a > TURN_RATE_LIMIT)
					pos2D.setSpeed(0, rate);// left
				else
					pos2D.setSpeed(0, TURN_RATE_SLOW);// left
			} else {
				if (a < -TURN_RATE_LIMIT)
					pos2D.setSpeed(0, -rate);// right
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
	 * Turn the robot
	 * 
	 * @param targetYaw
	 *            the heading to turn to
	 * @param rate
	 *            the rate to turn at
	 */
	public void turn(double targetYaw, double rate) {
		turn(targetYaw, rate, null, null);
	}

	/**
	 * Turn the robot
	 * 
	 * @param targetYaw
	 *            the heading to turn to
	 */
	public void turn(double targetYaw) {
		turn(targetYaw, TURN_RATE);
	}

	/**
	 * Move to the specified location
	 * 
	 * @param target
	 *            Target in internal coordinates
	 */
	public void move(Point target) {
		move(target, null);
	}

	/**
	 * Move the robot
	 * 
	 * @param target
	 *            the point to move to
	 * @param end
	 *            the final point the robot wants to move to at the end of the
	 *            path
	 */
	public void move(Point target, Point end) {
		synchronized (moveLock) {
			double px = target.x * Map.SCALE;// convert to Player coords
			double py = target.y * Map.SCALE;
			move(new PlayerPose2d(px, py, 0), end);
		}
	}

	/**
	 * Move the robot
	 * 
	 * @param pose
	 *            the position to move to
	 */
	public void move(PlayerPose2d pose) {
		move(pose, null);
	}

	/**
	 * Move to the specified location. Main move method. Other move methods call
	 * this method.
	 * 
	 * @param pose
	 *            Target in Player coordinates
	 * @param end
	 *            The end Point of the followed path
	 */
	public void move(PlayerPose2d pose, Point end) {
		synchronized (moveLock) {
			double px = pose.getPx();
			double py = pose.getPy();
			Point target = Map.convertPlayerToInternal(px, py);

			while (true) {
				
				Point a = this.getRobotPosition();
				if (a.distance(target) < 3){
					double targetYaw = targetYaw(px, py);
					turn(targetYaw);
				}

				if (!isValidMoveCondition(target, end))
					break;
				if (carryingGarbage) {
					Point garbagePoint = Map.convertPlayerToInternal(
							x + (Math.cos(yaw) * 0.4), y
									+ (Math.sin(yaw) * 0.4));
					map.garbageListArray.get(currentGarbageIndex).setPoint(
							garbagePoint);

					if (x1y1.x + GARBAGE_AREA_BOUNDARY <= garbagePoint.x
							&& garbagePoint.x <= x2y2.x - GARBAGE_AREA_BOUNDARY
							&& x2y2.y + GARBAGE_AREA_BOUNDARY <= garbagePoint.y
							&& garbagePoint.y <= x1y1.y - GARBAGE_AREA_BOUNDARY) {
						pos2D.setSpeed(0, 0);
						map.garbageListArray.get(currentGarbageIndex)
								.setIsCollected(true);
						break;
					}
				}

				if (isRobotStuckExploration())
					break;
				if (isRobotStuckGarbageCollection()) break;

				if ((Math.abs(px - x) < TARGET_THRESHOLD && Math.abs(py - y) < TARGET_THRESHOLD)) {
					pos2D.setSpeed(0, 0);
					break;// target reached
				}

				// Decide which way to turn, to never turn more than 1/2 circle.
				double targetYaw = targetYaw(px, py);
				turn(targetYaw);
				double difference = Math.sqrt(Math.pow(
						Math.abs(px - x) + Math.abs(py - y), 2));
				pos2D.setSpeed(Math.min(1, difference / 2 * Math.PI), 0);

				try {
					Thread.sleep(MOVE_SLEEP);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	/**
	 * Check if current move conditions are valid
	 * 
	 * @param target
	 *            the current move target
	 * @param end
	 *            the final point on the followed path
	 * @return true if the move conditions are valid, false otherwise
	 */
	public boolean isValidMoveCondition(Point target, Point end) {
		if (target != null
				&& (!Search.isAvailableCell(target, map))
				|| (end != null && (!(map.isUnexplored(end.x, end.y) || map
						.isFarWall(end.x, end.y)) || !Search.isAvailableCell(
						end, map)))) {
			pos2D.setSpeed(0, 0);
			return false;
		}
		return true;
	}

	/**
	 * Check if the robot's motors have stalled
	 * 
	 * @return true, if the robot is stuck, false otherwise
	 */
	public boolean isRobotStuckExploration() {

		if (pos2D.getData().getStall() == 1 && !goFetchGarbageHasBeenCalled) {
			pos2D.setSpeed(-0.5, 0);
			try {
				Thread.sleep(MOVE_BACK_SLEEP);
			} catch (InterruptedException e) {
			}
			System.out.println("Robot stuck, calculate a new path");
			pos2D.setSpeed(0, 0);
			return true;

		}

		return false;
	}

	/**
	 * Check if the robot's motors have stalled
	 * 
	 * @return true, if the robot is stuck, false otherwise
	 */
	public boolean isRobotStuckGarbageCollection() {

		if (pos2D.getData().getStall() == 1 && goFetchGarbageHasBeenCalled) {
			pos2D.setSpeed(-0.5, 0);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
			System.out
					.println("Robot stuck during garbage collection, move back");
			pos2D.setSpeed(0, 0);
			return true;
		}
		return false;
	}

	/**
	 * Start exploration
	 */
	public void explore() {

		this.control.println("Robot " + Robot.this.index
				+ " started exploration.");
		final Robot robot = this;
		Thread thr = new Thread() {
			public void run() {
				setStatus(RobotState.Exploring);
				Point start = Map.convertPlayerToInternal(x, y);
				List<Point> path = null;
				do {
					path = Search.dSearch(map, start);
					robot.currentPath = path;
					if (path != null) {
						if (path.size() > 3)
							path = Search.optimizePath(path);
						robot.currentOptimizedPath = path;
						robot.isFollowing = true;
						for (int i = 1; i < path.size(); i++) {

							Point p = path.get(i);
							Point end = path.get(path.size() - 1);

							robot.move(p, end);
						}
						start = Map.convertPlayerToInternal(robot.x, robot.y);
					}
				} while (path != null);
				robot.isFollowing = false;

				Robot.this.control.println("Robot " + Robot.this.index
						+ " finished exploration.");
				setStatus(RobotState.Idle);
			}
		};
		thr.start();

	}

	/**
	 * Set the status of the robot
	 * 
	 * @param state
	 *            the state to set the status to
	 */
	private void setStatus(RobotState state) {
		this.Status = state;
		control.RobotStateChanged(this, state);

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

	/**
	 * Look out for garbage items, and add them to the list of discovered
	 * garbage items to be collected later
	 */
	private void lookOutForGarbageThread() {
		Thread lookOutForGarbageThread = new Thread() {
			public void run() {
				while (!goFetchGarbageHasBeenCalled) {
					synchronized (sensorLock) {
						if (fiducialsInView != null) {
							for (int i = 0; i < fiducialsInView.length; i++) {
								// we got a null pointer exception
								int id = 0;
								id = fiducialsInView[i].getId();
								// unlike robots and 0,0 garbage items have an
								// id of 5,6,7
								if (id == 5 || id == 6 || id == 7) {
									double Py = fiducialsInView[i].getPose()
											.getPy();
									// +0.2 accounts for the fiducial sensor
									// being slightly forward on the robot.
									double Px = fiducialsInView[i].getPose()
											.getPx() + 0.2;
									double distance = Math.sqrt(Py * Py + Px
											* Px);
									double diffX = Math.cos(yaw
											+ Math.atan(Py / Px))
											* distance;
									double diffY = Math.sin(yaw
											+ Math.atan(Py / Px))
											* distance;
									addItem(new GarbageItem(
											Map.convertPlayerToInternal(x
													+ diffX, y + diffY), false));
								}
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
		lookOutForGarbageThread.start();
	}

	/**
	 * Add a new garbage item to the list of discovered garbage items, if it
	 * does not exist already
	 * 
	 * @param garbageItem
	 *            the garbage item to add
	 */
	public void addItem(GarbageItem garbageItem) {

		// check if garbage has already been added
		boolean garbageItemAlreadyExists = false;
		// calculates how many tiles a garbage item takes up. They appear to
		// take up 0.2 player units.
		for (int i = 0; i < map.garbageListArray.size(); i++) {
			if (garbageItem.getPoint().getX() == map.garbageListArray.get(i)
					.getPoint().getX()
					&& garbageItem.getPoint().getY() == map.garbageListArray
							.get(i).getPoint().getY()) {
				garbageItemAlreadyExists = true;
				break;
			}
		}

		if (!garbageItemAlreadyExists) {
			map.garbageListArray.add(garbageItem);
			control.println("Just seen a new garbage item at " + garbageItem.getPoint().x + "," + garbageItem.getPoint().y);
		}
	}

	/**
	 * Start garbage collection in the specified area
	 * 
	 * @param x1
	 *            the x coordinate of the first point
	 * @param y1
	 *            the y coordinate of the first point
	 * @param x2
	 *            the x coordinate of the second point
	 * @param y2
	 *            the y coordinate of the second point
	 */
	public void goFetchGarbage(double x1, double y1, double x2, double y2) {
		goFetchGarbageHasBeenCalled = true;
		int distanceFromGripperToRobotCenter = (int) Math
				.round(0.4 / Map.SCALE);
		Point dropOffPoint = Map.convertPlayerToInternal((x1 + x2) / 2,
				(y1 + y2) / 2);
		x1y1 = Map.convertPlayerToInternal(x1, y1);
		x2y2 = Map.convertPlayerToInternal(x2, y2);
		for (int i = 0; i < map.garbageListArray.size(); i++) {

			currentGarbageIndex = i;
			Point garbagePoint = map.garbageListArray.get(i).getPoint();

			List<Point> outboundList = Search.aSearch(map, getRobotPosition(),
					garbagePoint);
			if (outboundList == null) {
				System.out.println("outboundList null");
				continue;
			}
			if (outboundList.size() > distanceFromGripperToRobotCenter) {
				outboundList = outboundList.subList(0, outboundList.size()
						- distanceFromGripperToRobotCenter);
			}
			currentPath = outboundList;
			outboundList = Search.optimizePath(outboundList);
			isFollowing = true;
			for (int j = 0; outboundList != null && j < outboundList.size(); j++)
				move(outboundList.get(j));
			isFollowing = false;
			double targetYaw = targetYaw(garbagePoint.getX() * Map.SCALE,
					garbagePoint.getY() * Map.SCALE);
			turn(targetYaw);

			gripper.close();
			carryingGarbage = true;
			List<Point> returnList = Search.aSearch(map, getRobotPosition(),
					dropOffPoint);
			if (returnList == null) {
				System.out.println("returnList null");
				gripper.open();
				continue;
			}
			currentPath = returnList;
			returnList = Search.optimizePath(returnList);
			isFollowing = true;
			for (int k = 0; returnList != null && k < returnList.size(); k++) {
				move(returnList.get(k));
			}
			isFollowing = false;
			carryingGarbage = false;
			gripper.open();
		}
		control.println("Garbage collection finished");
	}
}
