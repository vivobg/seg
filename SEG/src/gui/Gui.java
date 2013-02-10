package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import map.CoordVal;
import map.Map;
import map.VerticalArray;

public class Gui extends JFrame implements Observer{
	
	private JPanel jp;
	private Graphics2D pg;
	private Map map;
	private BuffImg img;
	//private int x=20;
	//private int y=20;
	private int blockSize=5;
	
	Gui(Map map){
		super("Map Gui");
		img = new BuffImg(1, 1, BufferedImage.TYPE_INT_RGB);
		this.map = map;
		this.map.addObserver(this);
		updateImage();
		initWidgets();
	}
	
	private void growImage(){
		System.out.println(img.getWidth() + " " + img.getHeight());
		int width = map.getMaxXSize() + map.getMinXSize();
		int height = map.getMaxYSize() + map.getMinYSize();
		int centerX = map.getMinXSize();
		int centerY = map.getMaxYSize();
		//if image is smaller than map
		if (img.getWidth()<width*blockSize || img.getHeight() < (height+1)*blockSize){
			BuffImg biggerImg = new BuffImg((width-1)*blockSize, (height+1)*blockSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D bg2 = biggerImg.createGraphics();
			bg2.drawImage(img, null, (centerX-img.minX)*blockSize, (centerY-img.MaxY)*blockSize);
			biggerImg.MaxY = centerY;
			biggerImg.minX = centerX;
			img = biggerImg;
			bg2.dispose();
		}
		System.out.println(img.getWidth() + " " + img.getHeight());
		
		
	}
	
	private void UpdateImage(CoordVal cv){
		System.out.println("SMART update called");
		if (cv.grown) growImage();
		int centerX = map.getMinXSize();
		int centerY = map.getMaxYSize();
		
		Graphics2D g2 = img.createGraphics();
		g2.setColor(Color.blue);
		float val = cv.value;
		if (Math.abs(val - 0.5f) < 0.01f) g2.setColor(Color.RED);
		else g2.setColor(Color.BLUE);
		g2.fillRect((centerX+cv.x-1)*blockSize, (centerY-cv.y)*blockSize, blockSize, blockSize);
		g2.dispose();
	}
	
	private void updateImage(){
		System.out.println("Dumb update called");
		//if image is smaller than map
		growImage();
		int centerX = map.getMinXSize();
		int centerY = map.getMaxYSize();
		//img.minX = centerX*blockSize;
		//img.MaxY = centerY*blockSize;
		
		Graphics2D g2 = img.createGraphics();
		g2.setColor(Color.blue);		
		
		//positive X
		for (int x=0;x<map.getMaxXSize();x++)
		{	
			VerticalArray vert = map.getVertical(x);
			//System.out.println(vert.toString());
			for (int i=(vert.getNegSize()-1)*-1;i<vert.getPosSize();i++){
				float val = vert.getValue(i);
				if (Math.abs(val - 0.5f) < 0.01f) g2.setColor(Color.RED);
				else g2.setColor(Color.BLUE);
				g2.fillRect((centerX+x-1)*blockSize, (centerY-i)*blockSize, blockSize, blockSize);
			}
				
		}
		System.out.println("Done positive X");
		//Negative X
		for (int x=1;x<map.getMinXSize();x++)
		{	
			VerticalArray vert = map.getVertical(-x);
			//System.out.println(vert.toString());
			for (int i=(vert.getNegSize()-1)*-1;i<vert.getPosSize();i++){
				float val = vert.getValue(i);
				if (Math.abs(val - 0.5f) < 0.01f) g2.setColor(Color.RED);
				else g2.setColor(Color.BLUE);
				g2.fillRect((centerX-x)*blockSize, (centerY-i)*blockSize, blockSize, blockSize);
			}
		}
		g2.dispose();
	}
	
	public Map getMap() {return map;}

	private void initWidgets() {
		
		jp = new JPanel(){
			
			private static final long serialVersionUID = -4960648732508720990L;
			
			public Dimension getPreferredSize(){
				return new Dimension(img.getWidth(),img.getHeight());
			}

			public void paintComponent (Graphics g){
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.drawImage(img,null, 0, 0);
				
				/*
				g2.setColor(Color.blue);
				//g2.setBackground(Color.RED);
				//g2.fillRect(x, y, 20, 20);
				
				//g2.drawImage(img, op, x, y);
				
				int width = map.getMaxXSize() + map.getMinXSize();//-
				int height = map.getMaxYSize() + map.getMinYSize();//-
				int centerX = map.getMinXSize();
				int centerY = map.getMaxYSize();
				
				
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
				}*/
			}
		};
        //pg = (Graphics2D) jp.getGraphics();
        this.setLayout(new BorderLayout());
        JScrollPane scr = new JScrollPane(jp);
		this.add(scr, BorderLayout.CENTER);
		JButton bt = new JButton("Draw");
		this.add(bt,BorderLayout.NORTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 600);
		
		
		
		bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				jp.repaint();
				jp.revalidate();
				
			}
		});
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map map = new Map();
		MapChange mc = new MapChange(map);
		mc.setVisible(true);
		//Gui gui = new Gui(map);
		//gui.setVisible(true);
		//Component c[] = gui.getComponents();
		map.setValue(0, 50, 0.3f);
		map.setValue(0, -50, 0.8f);
		map.setValue(50, 0, 0.8f);
		map.setValue(-70, 0, 0.8f);
		map.setValue(-20, 3, 0.8f);
		for (int i=-14;i<15;i+=1){
				map.setValue(i, (int) (Math.random()*40+10), 0.2f);
				map.setValue(i, (int) -(Math.random()*40+10), 0.2f);
		}
		Gui gui = new Gui(map);
		gui.setVisible(true);
		

	}

	@Override
	public void update(Observable o, Object arg) {
		CoordVal cv = (CoordVal) arg;
		jp.setDoubleBuffered(true);
		
		UpdateImage(cv);
		jp.repaint();
		jp.revalidate();
		
	}

}
