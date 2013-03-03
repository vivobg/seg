package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javaclient3.structures.PlayerPose2d;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import explore.ExploreTest;

import robot.Robot;
import search.AStarSearch;
import search.SearchTest;
import sense.GarbageManager;
import map.CoordVal;
import map.Map;
import map.VerticalArray;

public class JMapPanel extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1361469152515719114L;
	public static final int blockSize = 5;
	public static final int minWidth = 800;
	public static final int minHeight = 800;
	public static final Color COLOR_WALL = Color.BLACK;
	public static final Color COLOR_EMPTY = Color.WHITE;
	public static final Color COLOR_UNEXPLORED = Color.GRAY;
	// Too close to a wall
	public static final Color COLOR_UNWALKABLE = Color.RED;
	public static final Color COLOR_PATH = Color.ORANGE;
	public static final Color COLOR_PATH_START = Color.GREEN;
	public static final Color COLOR_PATH_FINISH = Color.MAGENTA;
	private Map map;
	private BuffImg img;
	private BufferedImage robotImage;
	
	private List<Robot> robots;

	JMapPanel(Map map) {
		img = new BuffImg(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		g2.setColor(COLOR_UNEXPLORED);
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());
		g2.dispose();
		this.map = map;
		this.map.addObserver(this);
		robots = new ArrayList<Robot>();
		robotImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				updateImage();
			}
		});
		
	}

	/**
	 * The preferred size of the component is either minWidth,minHeigth or the
	 * size of the map, whichever is greater
	 */
	@Override
	public Dimension getPreferredSize() {
		// return new
		// Dimension(img.getWidth()>minWidth?img.getWidth():minWidth,img.getHeight()>minHeight?img.getHeight():minHeight);
		return new Dimension(img.getWidth(), img.getHeight());
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * Draw the generated Buffered Image
	 * 
	 **/
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(img, null, 0, 0);
		
		int centerX = map.getMinXSize();
		int centerY = map.getMaxYSize();
		int robotSize = (int) (blockSize*(Robot.ROBOT_SIZE / Map.SCALE));
		g2.setColor(Color.MAGENTA);
		for (Robot bot : robots){
			PlayerPose2d pose =  bot.getPose();
			Point rC = Map.convertPlayerToInternal(pose.getPx(), pose.getPy());
			int angle = (int) Math.toDegrees(pose.getPa());
			int startAngle = angle + (360-300)/2;
			g2.fillArc((centerX + rC.x-1) * blockSize-robotSize/2, (centerY - rC.y) * blockSize-robotSize/2, robotSize, robotSize, startAngle, 300);
		}
		
		g2.dispose();
	}

	/**
	 * Update and repaint the component on each map update
	 */
	@Override
	public void update(Observable o, Object arg) {
		final CoordVal cv = (CoordVal) arg;
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				UpdateImage(cv);
				JMapPanel.this.repaint();
				JMapPanel.this.revalidate();
			}
			
		});
		
	}

	private synchronized void growImage() {
		int width = map.getMaxXSize() + map.getMinXSize();
		int height = map.getMaxYSize() + map.getMinYSize();
		int centerX = map.getMinXSize();
		int centerY = map.getMaxYSize();
		// System.out.println(width);
		// if image is smaller than map
		if (img.getWidth() < width * blockSize
				|| img.getHeight() < (height + 1) * blockSize) {
			BuffImg biggerImg = new BuffImg((width - 1) * blockSize + 1,
					(height + 1) * blockSize + 1, BufferedImage.TYPE_INT_RGB);
			Graphics2D bg2 = biggerImg.createGraphics();
			bg2.setColor(COLOR_UNEXPLORED);
			bg2.fillRect(0, 0, biggerImg.getWidth(), biggerImg.getHeight());
			bg2.drawImage(img, null, (centerX - img.minX) * blockSize,
					(centerY - img.MaxY) * blockSize);
			biggerImg.MaxY = centerY;
			biggerImg.minX = centerX;
			img = biggerImg;
			bg2.dispose();
		}
	}

	private synchronized void UpdateImage(CoordVal cv) {
		if (cv.grown)
			growImage();
		int centerX = map.getMinXSize();
		int centerY = map.getMaxYSize();

		Graphics2D g2 = img.createGraphics();
		g2.setColor(COLOR_EMPTY);
		if (map.isEmpty(cv.x, cv.y))
			g2.setColor(COLOR_EMPTY);
		else if (map.isUnexplored(cv.x, cv.y))
			g2.setColor(COLOR_UNEXPLORED);
		else if (map.isOccupied(cv.x, cv.y))
			g2.setColor(COLOR_WALL);
		else
			g2.setColor(COLOR_UNWALKABLE);
		g2.fillRect((centerX + cv.x - 1) * blockSize, (centerY - cv.y)
				* blockSize, blockSize, blockSize);
		g2.dispose();
	}

	private synchronized void updateImage() {
		// if image is smaller than map
		growImage();
		int minX = map.getMinXSize();
		// int maxY = map.getMaxYSize();
		int centerX = minX > 0 ? minX : 1;

		int centerY = map.getMaxYSize();

		Graphics2D g2 = img.createGraphics();

		// positive X
		for (int x = 0; x < map.getMaxXSize(); x++) {
			VerticalArray vert = map.getVertical(x);
			for (int y = (vert.getNegSize() - 1) * -1; y < vert.getPosSize(); y++) {
				if (map.isEmpty(x, y))
					g2.setColor(COLOR_EMPTY);
				else if (map.isUnexplored(x, y))
					g2.setColor(COLOR_UNEXPLORED);
				else if (map.isOccupied(x, y))
					g2.setColor(COLOR_WALL);
				else
					g2.setColor(COLOR_UNWALKABLE);
				g2.fillRect((centerX + x - 1) * blockSize, (centerY - y)
						* blockSize, blockSize, blockSize);
			}

		}
		// Negative X
		for (int x = 1; x < map.getMinXSize(); x++) {
			VerticalArray vert = map.getVertical(-x);
			for (int y = (vert.getNegSize() - 1) * -1; y < vert.getPosSize(); y++) {
				if (map.isEmpty(x, y))
					g2.setColor(COLOR_EMPTY);
				else if (map.isUnexplored(x, y))
					g2.setColor(COLOR_UNEXPLORED);
				else if (map.isOccupied(x, y))
					g2.setColor(COLOR_WALL);
				else
					g2.setColor(COLOR_UNWALKABLE);
				g2.fillRect((centerX - x) * blockSize, (centerY - y)
						* blockSize, blockSize, blockSize);
			}
		}
		g2.dispose();
	}

	public synchronized void drawPath(List<Point> path) {
		growImage();
		int minX = map.getMinXSize();
		int centerX = minX > 0 ? minX : 1;

		int centerY = map.getMaxYSize();
		if (path == null)
			return;
		Graphics2D g2 = img.createGraphics();
		g2.setColor(COLOR_PATH);
		for (Point p : path) {
			int x = p.x;
			int y = p.y;
			// System.out.println(x + " " + y);
			g2.fillRect((centerX + x - 1) * blockSize, (centerY - y)
					* blockSize, blockSize, blockSize);
		}
		g2.setColor(COLOR_PATH_START);
		Point start = path.get(0);
		g2.fillRect((centerX + start.x - 1) * blockSize, (centerY - start.y)
				* blockSize, blockSize, blockSize);
		g2.setColor(COLOR_PATH_FINISH);
		Point finish = path.get(path.size() - 1);
		g2.fillRect((centerX + finish.x - 1) * blockSize, (centerY - finish.y)
				* blockSize, blockSize, blockSize);
		g2.dispose();
		this.revalidate();
		this.repaint();

	}
	
	public void addRobot(Robot r){
		robots.add(r);
	}
	/*
	private void drawRobotsThread(){
		Thread robotsThread = new Thread(){
			public void run(){
				if(robots.size() > 0){
					robotImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = robotImage.createGraphics();
					int centerX = map.getMinXSize();
					int centerY = map.getMaxYSize();
					int robotSize = (int) (blockSize*(Robot.ROBOT_SIZE / Map.SCALE));
					for (Robot bot : robots){
						PlayerPose2d pose =  bot.getPose();
						Point rC = Map.convertPlayerToInternal(pose.getPx(), pose.getPy());
						int angle = (int) Math.toDegrees(pose.getPa());
						int startAngle = angle + (360-300)/2;
						g2.fillArc((centerX + rC.x) * blockSize, (centerY + rC.y) * blockSize, robotSize, robotSize, startAngle, 300);
					}
				}
			}
		};
		robotsThread.start();
	}*/

	public Map getMap() {
		return map;
	}

	/**
	 * Example of using the JMapPanel component. The component is placed inside
	 * a scrollpane to allow viewing the whole map, without resizing the parent
	 * component/JFrame
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final Map map = new Map();
		MapChange mc = new MapChange(map);
		mc.setVisible(true);
		JFrame frame = new JFrame("Map GUI");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		final JMapPanel jMapPanel = new JMapPanel(map);
		JPanel p = new JPanel(new GridBagLayout()) {
			private static final long serialVersionUID = 466462159608863092L;

			@Override
			public Dimension getPreferredSize() {
				// Dimension size = new Dimension(minWidth, minHeight);
				Dimension child = jMapPanel.getPreferredSize();
				int width = child.width > minWidth ? child.width : minWidth;
				int height = child.height > minHeight ? child.height
						: minHeight;

				return new Dimension(width, height);
			}

			@Override
			public Dimension getMinimumSize() {
				return getPreferredSize();
			}
		};
		p.setBackground(COLOR_UNEXPLORED);
		p.add(jMapPanel, new GridBagConstraints());
		JScrollPane scr = new JScrollPane(p);
		frame.add(scr, BorderLayout.CENTER);
		// frame.add(jMapPanel);

		JButton btn = new JButton("Random values");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (int i = -14; i < 15; i += 1) {
					map.setValue(i, (int) (Math.random() * 40 + 10),
							Map.OCCUPIED);
					map.setValue(i, (int) -(Math.random() * 40 + 10), Map.EMPTY);
				}

			}
		});

		frame.add(btn, BorderLayout.SOUTH);

		frame.pack();
		frame.setVisible(true);

		/*
		 * map.setValue(0, 50, Map.OCCUPIED); map.setValue(0, -50,
		 * Map.OCCUPIED); map.setValue(50, 0, Map.OCCUPIED); map.setValue(-50,
		 * 0, Map.OCCUPIED);
		 * 
		 * map.setValue(-20, 3, Map.EMPTY); map.setValue(25, 25,
		 * Map.UNEXPLORED); map.setValue(-43, 35, Map.EMPTY);
		 */

		/*
		 * Generate a wall-bound rectangle to test path finding on
		 *//*
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {
				if (j == 0 || j == 29 || i == 0 || i == 29)
					map.setValue(i-200, j, Map.OCCUPIED);
				else
					map.setValue(i-200, j, Map.EMPTY);
			}
		}

		for (int j = 15; j < 22; j++) {
			map.setValue(10-200, j, Map.OCCUPIED);

		}
		for (int i = 10; i < 20; i++) {
			map.setValue(i-200, 15, Map.OCCUPIED);
		}
		for (int i = 15; i < 29; i++) {
			map.setValue(19-200, i, Map.OCCUPIED);
		}

		map.setValue(19-200, 2, Map.UNEXPLORED);
		map.setValue(19-200, 3, Map.UNEXPLORED);
		map.setValue(19-200, 4, Map.UNEXPLORED);
		map.setValue(19-200, 5, Map.UNEXPLORED);
		map.setValue(19-200, 6, Map.UNEXPLORED);

		// System.out.println(map.getValue(0, 50));
		// System.out.println(map.getValue(0, -50));
		// map.setValue(100, 100, Map.OCCUPIED);
		// map.setValue(-40, -5, Map.OCCUPIED);

		/*
		 * Bresenham test
		 *//*
		sense.Bresenham.line(map, -42-200, -3, -45-200, 15, false);
		sense.Bresenham.line(map, -21-200, -3, -40-200, 1, true);
		sense.Bresenham.line(map, -10-200, -5, -30-200, 5, true);
		sense.Bresenham.line(map, -4-200, 0, -8-200, 4, true);
		sense.Bresenham.line(map, -20-200, 15, -21-200, 14, true);

		/*
		 * Path drawing test
		 *//*
		List<Point> points = AStarSearch.aSearch(map, new Point(16-200, 20),
				new Point(28-200, 28));
		jMapPanel.drawPath(points);
		points = AStarSearch.dSearch(map, new Point(3-200, 9));
		jMapPanel.drawPath(points);

		/*
		 * Exploration test
		 *//*
		for (int i = 0; i < 30; i++) {
			for (int j = 40; j < 70; j++) {
				if (j == 40 || j == 69 || i == 0 || i == 29)
					map.setValue(i-200, j, Map.OCCUPIED);

			}
		}

		ExploreTest.explore(map, new Point(5-200, 45));
		*/
		Robot robot = new Robot(map,0);
		Robot robot2 = new Robot(map,1);
		Robot robot3 = new Robot(map,2);
		jMapPanel.addRobot(robot);
		jMapPanel.addRobot(robot2);
		jMapPanel.addRobot(robot3);
		new GarbageManager(robot);
		new RobotControl(robot).setVisible(true);

	}

}
