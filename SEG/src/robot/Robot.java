/**
 * 
 */
package robot;

import java.awt.Point;
import java.awt.Rectangle;
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
import javaclient3.structures.fiducial.PlayerFiducialGeom;
import javaclient3.structures.fiducial.PlayerFiducialItem;
import javaclient3.structures.gripper.PlayerGripperData;
import map.GarbageItem;
import map.Map;
import search.Search;
import sense.Sense;

/**
 *Generic robot class providing the interface with player/stage
 *and providing the basic functionality of the robot
 */
public class Robot{
	PlayerClient robot = null;
	public Position2DInterface pos2D = null;
	public RangerInterface sonar = null;
	GripperInterface gripper = null;
	FiducialInterface fiducial = null;
	public static final int COLLECTION_SLEEP = 20;
	public static final int SENSE_SLEEP = 20;
	public static final int MOVE_SLEEP = 5;
	public static final int MOVE_BACK_SLEEP = 15;
	public static final int TURN_SLEEP = 5;
	public static final double TURN_RATE = 0.5;
	public static final double TURN_RATE_SLOW = 0.1;
	public static final double TURN_RATE_LIMIT = 0.3;// Below this, turn slowly
	public static final double SPEED_RATE = 0.5;
	public static final double TARGET_THRESHOLD = 0.085;
	public static final double HEADING_THRESHOLD = 0.03;
	public static final double ROBOT_SIZE = 0.50;
	private static final double TURN_360 = 0.01;
	private static final int GRIPPER_THRESHOLD = (int) (0.8 / Map.SCALE);
	public static final int FIDUCIAL_SLEEP = 100;
	public boolean isFollowing = false;
	public boolean isCollecting = false;
	public List<Point> currentOptimizedPath;
	public List<Point> currentPath;
	public Object moveLock = new Object();
	public Object sensorLock = new Object();
	public double x;
	public double y;
	public double yaw;
	private Map map;
	private Control control;
	public final int index;
	private double[] sonarValues;
	public PlayerFiducialItem[] fiducialsInView;
	public RobotState Status;
	
	private double totalJiggle = 0;
	private int jiggleCount = 0;
	private double jiggleAverage = 3;

	public PlayerGripperData gripperData;
	private boolean goFetchGarbageHasBeenCalled = false;
	protected PlayerFiducialGeom fiducialGeom;
	private boolean carryingGarbage = false;
	private Rectangle dropOffRectangle;
	private Point x1y1;
	private Point x2y2;
	private int currentGarbageIndex;
	private int GARBAGE_AREA_BOUNDARY = 3;


	/**
	 * 
	 * @return the current pose of the robot
	 */
	public PlayerPose2d getPose(){
		return new PlayerPose2d(x,y,yaw);
	}
/*
	public Robot(Control control) {

		this(control, 0);
	}*/
	/**
	 * Initialise a new Robot instance
	 * @param control the control instance to use
	 * @param index the index of the robot
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
			fiducial = robot.requestInterfaceFiducial(index,PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("Simplebob: Error connecting to Player!\n>>>"
					+ e.toString());
			System.exit(1);
		}
		robot.runThreaded(-1, -1);
		collectionThread();

		//makes the robot do a 360 scan (HACK)
		Thread do360 = new Thread(){
			public void run(){
				do360SonarScan();
			}
		};
		// do360.start();
		senseThread();
		lookOutForGarbageThread();
		setStatus(RobotState.Idle);
	}
	/**
	 * 
	 * @return the sonar values
	 */
	public double[] getSonar(){
		return sonarValues;
	}/*
	/**
	 * 
	 * @return the fiducial data
	 *
	public double[] getFiducial() {
		throw new UnsupportedOperationException("Not Implemented Yet!");
	}*/
	
	/**
	 * 
	 * @return the current position of the robot, in internal coordinates
	 */
	public Point getRobotPosition(){
		return Map.convertPlayerToInternal(x, y);
	}

	/**
	 * Retrieves x,y,yaw and sensor readings every N milliseconds
	 */
	private void collectionThread() {
		Thread collection = new Thread() {
			public void run() {
				while (true) {
					synchronized(sensorLock){
						if (pos2D.isDataReady() && sonar.isDataReady() && fiducial.isDataReady() && pos2D.getTimestamp() == fiducial.getTimestamp()
								&& pos2D.getTimestamp() == sonar.getTimestamp()) {

							x = pos2D.getX();
							y = pos2D.getY();
							yaw = pos2D.getYaw();
							sonarValues = sonar.getData().getRanges().clone();
							fiducialsInView = fiducial.getData().getFiducials();
							//fiducialGeom = fiducial.getGeom();
						}

						if(gripper.isDataReady()){
							gripperData = gripper.getData();
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
			
			if (!isValidMoveCondition(target, end)) break;
			
			if (isRobotStuckExploration()) break;
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
	 * @param targetYaw the heading to turn to
	 * @param rate the rate to turn at
	 */
	public void turn(double targetYaw, double rate){
		turn(targetYaw, rate, null, null);
	}
	/**
	 * Turn the robot
	 * @param targetYaw the heading to turn to
	 */
	public void turn(double targetYaw){
		turn(targetYaw, TURN_RATE);
	}
	/**
	 * TO BE REMOVED,BROKEN, NEVER USED
	 */
	public void do360SonarScan(){
		//for (int i=0;i<4;i++)
		//turn(Math.toRadians(120) + yaw, TURN_360);
		//double initialYaw = yaw;
		/*pos2D.setSpeed(0, TURN_360);
		try {
			Thread.sleep((long) (Math.toRadians(30)/TURN_360 * 1000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pos2D.setSpeed(0, 0); */
		//turn(initialYaw);
		double yaw = this.yaw;
		turn(yaw + Math.toRadians(15));
		turn(yaw - Math.toRadians(15));
		turn(yaw);
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
	 * @param target the point to move to
	 * @param end the final point the robot wants to move to at the end of the path
	 */
	public void move(Point target, Point end){
		synchronized (moveLock){
			double px = target.x * Map.SCALE;// convert to Player coords
			double py = target.y * Map.SCALE;
			move(new PlayerPose2d(px, py, 0),end);
		}
	}
	/**
	 * Move the robot
	 * @param pose the position to move to
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
		synchronized (moveLock){
			double px = pose.getPx();
			double py = pose.getPy();
			//Point target =  new Point((int)px,(int)py);
			Point target =  Map.convertPlayerToInternal(px, py);
			
			boolean do360 = true;
			double distance360 = this.jiggleAverage;
			while (true) {
				//pos2D.setSpeed(0, 0);
//				if (!map.isUnexplored(target.x, target.y) ||
//						!Search.isAvailableCell(target, map) ||
//						(end!=null
//						&& (!map.isUnexplored(end.x, end.y)
//						|| !Search.isAvailableCell(end, map)))) {
//					pos2D.setSpeed(0, 0);
//					break;
//				}
				
				
				
				if (!isValidMoveCondition(target, end)) break;
				if (carryingGarbage)
				{
					Point garbagePoint = Map.convertPlayerToInternal(x+(Math.cos(yaw)*0.4), y + (Math.sin(yaw)*0.4));
					map.garbageListArray.get(currentGarbageIndex).setPoint(garbagePoint);
					
					if(x1y1.x + GARBAGE_AREA_BOUNDARY  <= garbagePoint.x && garbagePoint.x <= x2y2.x - GARBAGE_AREA_BOUNDARY 
							&& x2y2.y + GARBAGE_AREA_BOUNDARY <= garbagePoint.y && garbagePoint.y <= x1y1.y - GARBAGE_AREA_BOUNDARY)
					{
						pos2D.setSpeed(0, 0);
						map.garbageListArray.get(currentGarbageIndex).setIsCollected(true);
						break;
					}
				}
				
				if (isRobotStuckExploration()) break;
				isRobotStuckGarbageCollection();
				
				if ((Math.abs(px - x) < TARGET_THRESHOLD && Math.abs(py - y) < TARGET_THRESHOLD)) {
					pos2D.setSpeed(0, 0);
					// System.out.println("BREAKING");
					break;// target reached
				}
				
				
				
				if (this.isFollowing && !isCollecting && do360 && Search.lineofSight(map, this.getRobotPosition(), end) &&
						(this.getRobotPosition().distance(end) < (jiggleAverage / Map.SCALE))){
					//this.control.println("do360 at " + jiggleAverage);
					//this.do360SonarScan();
					
//					if (isValidMoveCondition(target, end)) {
//						distance360 = Math.max(distance360 - 0.2, 0);
//						jiggleCount++;
//						totalJiggle += distance360;
//						jiggleAverage = totalJiggle / jiggleCount;
//						this.control.println("Jiggle distance reduced to: "
//								+ jiggleAverage);
//						
//						if (distance360 < Map.SCALE)
//							do360 = false;
//					} else {
//						this.control.println("Jiggle worked!!!");
//						distance360 = Math.min(distance360 + 0.1, 4.5);
//						jiggleCount++;
//						totalJiggle += distance360;
//						jiggleAverage = totalJiggle / jiggleCount;
//						this.control.println("Jiggle distance increased to: "
//								+ jiggleAverage);
//					}
					
				}
				//System.out.println("Targeting");
				/*
				 * Decide which way to turn, to never turn more than 1/2 circle.
				 */
				double targetYaw = targetYaw(px, py);
				// System.out.println("Turning");
				turn(targetYaw);
				double difference  = Math.sqrt(Math.pow(Math.abs(px -x) + Math.abs(py-y),2));
				// System.out.println("MOVING");
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
	 * @param target the current move target
	 * @param end the final point on the followed path
	 * @return true if the move conditions are valid, false otherwise
	 */
	public boolean isValidMoveCondition(Point target, Point end){
		if (target!=null && ((!target.equals(end) &&  false /*map.isUnexplored(target.x, target.y)*/) || !Search.isAvailableCell(target, map)) ||
				(  end!=null && (!(map.isUnexplored(end.x, end.y) || map.isFarWall(end.x, end.y)) || !Search.isAvailableCell(end, map))    )    ) {
			pos2D.setSpeed(0, 0);
			//System.out.println("Breaking");
			return false;
		}
		return true;
	}

	/**
	 * Check if the robot's motors have stalled
	 * @return true, if the robot is stuck, false otherwise
	 */
	public boolean isRobotStuckExploration(){
        //0 walking
        //1 is stuck        
        
        if (pos2D.getData().getStall()==1 && !goFetchGarbageHasBeenCalled){
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
	 * @return true, if the robot is stuck, false otherwise
	 */
	public void isRobotStuckGarbageCollection(){
        //0 walking
        //1 is stuck        
        
        if (pos2D.getData().getStall()==1 && goFetchGarbageHasBeenCalled){
            pos2D.setSpeed(-0.5, 0);
            try {
                Thread.sleep(250);
             } catch (InterruptedException e) {
            }
            System.out.println("Robot stuck during garbage collection, move back");
            pos2D.setSpeed(0, 0);
         
            
         } 
        
      
    }
	
	

	/**
	 * NEVER USED, TO BE REMOVED
	 * @return
	 */
	private boolean isTooCloseToWall() {
		double threshold = 0.6;
		if(sonarValues[0] < threshold || sonarValues[1] < threshold || sonarValues[15] < threshold)
		{
			return true;
		}

		return false;
	}

	/**
	 * TO BE REMOVED with RobotControl, not needed for final code
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
	/**
	 * Start exploration
	 */
	public void explore() {
		
		this.control.println("Robot " + Robot.this.index + " started exploration.");
		final Robot robot = this;
		Thread thr = new Thread(){
			public void run(){
				setStatus(RobotState.Exploring);
				Point start = Map.convertPlayerToInternal(x, y);
				List<Point> path = null;
				do {
					path = Search.dSearch(map, start);
					robot.currentPath = path;
					if (path != null) {
						if(path.size() > 3)path = Search.optimizePath(path);
						robot.currentOptimizedPath = path;
						robot.isFollowing = true;
						for (int i = 1; i<path.size();i++){
							
							Point p = path.get(i);
							Point end = path.get(path.size()-1);

							robot.move(p, end);
						}
						start = Map.convertPlayerToInternal(robot.x, robot.y);
					}
				} while (path != null);
				robot.isFollowing = false;
				
				Robot.this.control.println("Robot " + Robot.this.index + " finished exploration.");
				setStatus(RobotState.Idle);
			}
		};
		thr.start();


	}
	/**
	 * Set the status of the robot
	 * @param state the state to set the status to
	 */
	private void setStatus(RobotState state) {
		this.Status = state;
		control.RobotStateChanged(this, state);

	}


	/**
	 * Pick up an object
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
	
	
	
	
	/**
	 * Look out for garbage items, and add them to the list of discovered garbage
	 * items to be collected later
	 */
	private void lookOutForGarbageThread() {
		Thread lookOutForGarbageThread = new Thread() {
			public void run() {
				while(!goFetchGarbageHasBeenCalled){
					synchronized(sensorLock){
						if (fiducialsInView != null) {
							for(int i = 0; i < fiducialsInView.length; i++ ){
								//we got a null pointer exception
								int id = 0;
								id = fiducialsInView[i].getId();
								//unlike robots and 0,0 garbage items have an id of 5,6,7
								if(id == 5 || id == 6 || id == 7 ){
									double Py = fiducialsInView[i].getPose().getPy();
									// +0.2 accounts for the fiducial sensor being slightly forward on the robot.
									double Px = fiducialsInView[i].getPose().getPx() + 0.2;
									double distance = Math.sqrt(Py*Py + Px*Px);
									double diffX = Math.cos(yaw + Math.atan(Py / Px)) * distance;
									double diffY = Math.sin(yaw + Math.atan(Py / Px)) * distance;
									//System.out.println((x + diffX) + "," +  (y + diffY));

									addItem(new GarbageItem(Map.convertPlayerToInternal(x + diffX, y + diffY),false));
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
	 * Add a new garbage item to the list of discovered garbage items, if it does not exist already
	 * @param garbageItem the garbage item to add
	 */
	public void addItem(GarbageItem garbageItem){

		//check if garbage has already been added
		boolean garbageItemAlreadyExists = false;
		//calculates how many tiles a garbage item takes up. They appear to take up 0.2 player units.
		for(int i = 0; i < map.garbageListArray.size(); i++){
			if(garbageItem.getPoint().getX() == map.garbageListArray.get(i).getPoint().getX() &&
				  garbageItem.getPoint().getY() == map.garbageListArray.get(i).getPoint().getY()){
				garbageItemAlreadyExists = true;
				break;
			}
		}

		if(!garbageItemAlreadyExists){
			map.garbageListArray.add(garbageItem);
			printGarbageToCollectList();
			//System.out.println("Geom " + fiducialGeom.getPose().getPx());
		}
	}
	
	//for dev purposes only
	/**
	 * POSSIBLY REMOVE ?
	 * Print the list of currently discovered garbage objects
	 */
	private void printGarbageToCollectList() {
		control.println("Just added a garbage item: " + "map.garbageListArray size is " + map.garbageListArray.size() + " and holds:");
		for(int i = 0; i < map.garbageListArray.size(); i++ )
			control.println(map.garbageListArray.get(i).getPoint().toString());
	}
	/**
	 * Start garbage collection in the specified area
	 * @param x1 the x coordinate of the first point
	 * @param y1 the y coordinate of the first point
	 * @param x2 the x coordinate of the second point
	 * @param y2 the y coordinate of the second point
	 */
	public void goFetchGarbage(double x1, double y1, double x2, double y2) {
		goFetchGarbageHasBeenCalled = true;
		int distanceFromGripperToRobotCenter = (int) Math.round(0.4/Map.SCALE);
		Point dropOffPoint = Map.convertPlayerToInternal((x1+x2)/2, (y1+y2)/2);
		//dropOffRectangle = new Rectangle((int)(x1/Map.SCALE), (int)(y1/Map.SCALE), (int)((x2 - x1) /Map.SCALE), (int)((y1 - y2)/Map.SCALE));
		x1y1 = Map.convertPlayerToInternal(x1, y1);
		x2y2 = Map.convertPlayerToInternal(x2, y2);
		for(int i = 0; i < map.garbageListArray.size(); i++){
			
			currentGarbageIndex = i;
			Point garbagePoint = map.garbageListArray.get(i).getPoint();


			List<Point> outboundList = Search.aSearch(map, 
					getRobotPosition(),
					garbagePoint);
			if(outboundList == null){System.out.println("outboundList null");continue;}
			if(outboundList.size() > distanceFromGripperToRobotCenter){
				outboundList = outboundList.subList(0,outboundList.size()-distanceFromGripperToRobotCenter); 
			}
			currentPath = outboundList;
			outboundList = Search.optimizePath(outboundList);
			isFollowing = true;
			isCollecting = true;
			for (int j = 0; outboundList != null && j < outboundList.size(); j++)
				move(outboundList.get(j));
			isFollowing = false;
			isCollecting = false;

			double targetYaw = targetYaw(garbagePoint.getX()*Map.SCALE, garbagePoint.getY()*Map.SCALE);
			turn(targetYaw);
			
			//this caused problems and only rarely saw the garbage
			//if(gripperData.getBeams() == 0){continue;}
			
			gripper.close();
			carryingGarbage  = true;
			List<Point> returnList = Search.aSearch(map, 
					getRobotPosition(),
					dropOffPoint);
			if(returnList == null){System.out.println("returnList null");gripper.open(); continue;}
			currentPath = returnList;
			returnList = Search.optimizePath(returnList);
			isFollowing = true;
			isCollecting = true;
			for (int k = 0; returnList != null && k < returnList.size(); k++){
				move(returnList.get(k));
			}
			isFollowing = false;
			isCollecting = false;

			carryingGarbage = false;
			gripper.open();
		}
		control.println("Garbage collection finished");
	}
}
