package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import map.Map;
/**
 * Provides a custom JPanel, tailored to displaying a Map instance,
 * with the contents of each cell, the garbages, the robots and their paths. 
 *
 */
public class JMapPanel extends JPanel{

	private static final long serialVersionUID = -1361469152515719114L;
	public static final long REFRESH_RATE = 50;
	public static final int minWidth = 800;
	public static final int minHeight = 800;

	private Map map;
	public static final int panelSize = (int) (100 / Map.SCALE * DrawObjects.BLOCK_SIZE);
	/**
	 * Initialise a new JMapPanel
	 * @param map the map instance to draw
	 */
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
	/**
	 * The custom drawing of the panel.
	 */
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
	/**
	 * 
	 * @return the map instance
	 */
	public Map getMap() {
		return map;
	}

}
