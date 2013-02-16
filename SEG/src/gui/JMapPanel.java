package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import search.AStarSearch;

import map.CoordVal;
import map.Map;
import map.VerticalArray;

public class JMapPanel extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1361469152515719114L;
	public static final int blockSize = 5;
	public static final int minWidth = 400;
	public static final int minHeight = 400;
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

	JMapPanel(Map map) {
		img = new BuffImg(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		g2.setColor(COLOR_UNEXPLORED);
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());
		g2.dispose();
		this.map = map;
		this.map.addObserver(this);
		updateImage();
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
		g2.dispose();
	}

	/**
	 * Update and repaint the component on each map update
	 */
	@Override
	public void update(Observable o, Object arg) {
		CoordVal cv = (CoordVal) arg;
		UpdateImage(cv);
		this.repaint();
		this.revalidate();
	}

	private void growImage() {
		int width = map.getMaxXSize() + map.getMinXSize();
		int height = map.getMaxYSize() + map.getMinYSize();
		int centerX = map.getMinXSize();
		int centerY = map.getMaxYSize();
		System.out.println(width);
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

	private void UpdateImage(CoordVal cv) {
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

	private void updateImage() {
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

	public void drawPath(List<Point> path) {
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
			System.out.println(x + " " + y);
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

		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {
				if (j == 0 || j == 29 || i == 0 || i == 29)
					map.setValue(i, j, Map.OCCUPIED);
				else
					map.setValue(i, j, Map.EMPTY);
			}
		}

		for (int j = 15; j < 22; j++) {
			map.setValue(10, j, Map.OCCUPIED);

		}
		for (int i = 10; i < 20; i++) {
			map.setValue(i, 15, Map.OCCUPIED);
		}
		for (int i = 15; i < 29; i++) {
			map.setValue(19, i, Map.OCCUPIED);
		}

		map.setValue(19, 2, Map.UNEXPLORED);
		map.setValue(19, 3, Map.UNEXPLORED);
		map.setValue(19, 4, Map.UNEXPLORED);
		map.setValue(19, 5, Map.UNEXPLORED);
		map.setValue(19, 6, Map.UNEXPLORED);

		System.out.println(map.getValue(0, 50));
		System.out.println(map.getValue(0, -50));
		// map.setValue(100, 100, Map.OCCUPIED);
		// map.setValue(-40, -5, Map.OCCUPIED);
		sense.Bresenham.line(map, -42, -3, -45, 15, false);
		sense.Bresenham.line(map, -21, -3, -40, 1, true);
		sense.Bresenham.line(map, -10, -5, -30, 5, true);
		sense.Bresenham.line(map, -4, 0, -8, 4, true);
		sense.Bresenham.line(map, -20, 15, -21, 14, true);
		List<Point> points = AStarSearch.Search(map, new Point(16, 20),
				new Point(28, 28), true);
		jMapPanel.drawPath(points);
		points = AStarSearch.Search(map, new Point(3, 9), null, false);
		jMapPanel.drawPath(points);

	}

}
