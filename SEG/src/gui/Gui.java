package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import map.CoordVal;
import map.Map;
import map.VerticalArray;

public class Gui extends JFrame implements Observer{
	
	private JPanel jp;
	private Graphics2D pg;
	private Map map;
	//private int x=20;
	//private int y=20;
	private int blockSize=5;
	
	Gui(Map map){
		super("Map Gui");
		this.map = map;
		this.map.addObserver(this);
		initWidgets();
	}
	
	public Map getMap() {return map;}

	private void initWidgets() {
		jp = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -4960648732508720990L;

			public void paint (Graphics g){
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(Color.blue);
				//g2.setBackground(Color.RED);
				//g2.fillRect(x, y, 20, 20);
				
				
				int width = map.getMaxXSize() - map.getMinXSize();
				int height = map.getMaxYSize() - map.getMinYSize();
				int centerX = map.getMinXSize();
				int centerY = map.getMinYSize();
				
				
				//positive X
				for (int x=0;x<map.getMaxXSize();x++)
				{	
					VerticalArray vert = map.getVertical(x);
					System.out.println(vert.toString());
					for (int i=(vert.getNegSize()-1)*-1;i<vert.getPosSize();i++){
						float val = vert.getValue(i);
						if (Math.abs(val - 0.5f) < 0.01f) g2.setColor(Color.RED);
						else g2.setColor(Color.BLUE);
						g2.fillRect((centerX+x)*blockSize, (centerY-i)*blockSize, blockSize, blockSize);
					}
						
				}
				System.out.println("Done positive X");
				//Negative X
				for (int x=1;x<map.getMinXSize();x++)
				{	
					VerticalArray vert = map.getVertical(-x);
					System.out.println(vert.toString());
					for (int i=(vert.getNegSize()-1)*-1;i<vert.getPosSize();i++){
						float val = vert.getValue(i);
						if (Math.abs(val - 0.5f) < 0.01f) g2.setColor(Color.RED);
						else g2.setColor(Color.BLUE);
						g2.fillRect((centerX-x)*blockSize, (centerY-i)*blockSize, blockSize, blockSize);
					}
				}
			}
		};
        pg = (Graphics2D) jp.getGraphics();
        this.setLayout(new BorderLayout());
		this.add(jp, BorderLayout.CENTER);
		JButton bt = new JButton("Draw");
		this.add(bt,BorderLayout.NORTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 600);
		
		
		
		bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				jp.repaint();
			}
		});
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map map = new Map();
		Gui gui = new Gui(map);
		gui.setVisible(true);
		Component c[] = gui.getComponents();
		map.setValue(0, 50, 0.3f);
		map.setValue(0, -50, 0.8f);
		map.setValue(50, 0, 0.8f);
		map.setValue(-70, 0, 0.8f);
		map.setValue(-20, 3, 0.8f);
		for (int i=-14;i<15;i+=1){
				map.setValue(i, (int) (Math.random()*40+10), 0.2f);
				map.setValue(i, (int) -(Math.random()*40+10), 0.2f);
		}
		

	}

	@Override
	public void update(Observable o, Object arg) {
		CoordVal cv = (CoordVal) arg;
		
		jp.repaint();
		
	}

}
