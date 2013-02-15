/**
 * 
 */
package robot;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;

/**
 * @author Albert
 * 
 * Generic robot class provides the interface with player/stage
 *
 */
public class Robot {

	PlayerClient robot=null;
	Position2DInterface pos2D=null;
	RangerInterface sonar=null;

	public static final int COLLECTION_SLEEP = 1;
	
	private double x;
	private double y;
	private double yaw; 
	private double[] sonarValues;

	public Robot(){

		// Set up service proxies
		try {
			robot = new PlayerClient("localhost", 6665);
			pos2D = robot.requestInterfacePosition2D(0,PlayerConstants.PLAYER_OPEN_MODE);
			sonar = robot.requestInterfaceRanger(0,PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println("Simplebob: Error connecting to Player!\n>>>" + e.toString());
			System.exit(1);
		}
		robot.runThreaded(-1,-1);
		collectionThread();

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

					try {sleep(COLLECTION_SLEEP);} catch (InterruptedException e) {}

				}
			}
		};
		collection.start();
	}

}
