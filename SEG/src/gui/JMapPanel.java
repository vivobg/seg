package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import mainApp.Control;
import map.Map;
import robot.Robot;
import sense.GarbageItem;

public class JMapPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1361469152515719114L;
	public static final long REFRESH_RATE = 50;
	public static final int minWidth = 800;
	public static final int minHeight = 800;

	private Map map;
	public static final int panelSize = (int) (50 / Map.SCALE * DrawObjects.BLOCK_SIZE);

	JMapPanel(Map map) {
		this.map = map;
		JMapPanel.this.setSize(panelSize, panelSize);
		updateThread();
	}

	/**
	 * The preferred size of the component is either minWidth,minHeigth or the
	 * size of the map, whichever is greater
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(panelSize, panelSize);
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Point size = new Point(panelSize, panelSize);
		DrawObjects.clear(g2, size);
		// drawMap(g2, size);
		DrawObjects.drawMapDeep(map, g2, size);
		DrawObjects.drawRobots(map, g2, size);
		DrawObjects.drawGarbage(map, g2, size);
		DrawObjects.drawPaths(map, g2, size);
		//DrawObjects.drawRobotArea(map, g2, size);
		
		g2.dispose();

	}

	/**
	 * Update and repaint the component every N milliseconds
	 */
	public void updateThread() {
		Thread update = new Thread(){
			@Override
			public void run(){
				while (true){

				SwingUtilities.invokeLater(new Runnable(){

					@Override
					public void run() {

						JMapPanel.this.revalidate();
						JMapPanel.this.repaint();
					}
					
				});
					try {
						Thread.sleep(REFRESH_RATE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		update.start();
	}

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
		final Control control = new Control();
		final Map map = control.getMap();
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {

				JFrame frame = new JFrame("Map GUI");

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new BorderLayout());
				final JMapPanel jMapPanel = new JMapPanel(map);
				final JScrollPane scrMap = new JScrollPane(jMapPanel);
				frame.add(scrMap, BorderLayout.CENTER);
				// frame.add(jMapPanel);

				JButton btn = new JButton("Center View");
				btn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						JViewport vp = scrMap.getViewport();
						Dimension vs = vp.getExtentSize();
						vp.setViewPosition(new Point(JMapPanel.panelSize / 2
								- vs.width / 2, JMapPanel.panelSize / 2
								- vs.height / 2));
					}
				});

				frame.add(btn, BorderLayout.SOUTH);

				frame.pack();
				frame.setVisible(true);
			}
		});


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
		final Robot robot = new Robot(control, 0);
		//Robot robot2 = new Robot(map,1);
		//Robot robot3 = new Robot(map,2);
		map.addRobot(robot);
		//map.addRobot(robot2);
		//map.addRobot(robot3);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new RobotControl(robot, map).setVisible(true);
			}
		});

	}

}
