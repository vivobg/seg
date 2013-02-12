package mainApp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame implements MapUpdateEvent  {
	BackgroundPanel drawPanel;
	Graphics g;
	Bot bot ;
	final int west = 10;
	final int east  = 4;
	final int north  = 1;
	final int south = 7;
	BufferedImage gridMap;
	public MainFrame()
	{
		bot = new Bot();
		bot.addListener(this);
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		drawPanel = new BackgroundPanel();

		gridMap = new BufferedImage(200,200,BufferedImage.TYPE_3BYTE_BGR);
		  for(int i = 0; i < 200; i++)
		  {
			  for (int j = 0; j < 200; j++)
			  {
				  gridMap.setRGB(i, j, Color.white.getRGB());
			  }
		  }
		  drawPanel.setImage(gridMap);
		  drawPanel.repaint();
		
		
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(drawPanel, BorderLayout.CENTER);
		
		this.setContentPane(mainPanel);
		g = drawPanel.getGraphics();
		JButton forwardBtn = new JButton("Forward");
		forwardBtn.addActionListener(bot);
		
		JButton reverseBtn = new JButton("Reverse");
		reverseBtn.addActionListener(bot);
		
		JButton rotateBtn = new JButton("Rotate");
		rotateBtn.addActionListener(bot);
		
		JButton exploreBtn = new JButton("Explore");
		exploreBtn.addActionListener(bot);
		
		JButton sonarBtn = new JButton("Sonar Vals");
		sonarBtn.addActionListener(
				new ActionListener(){
					
					public void actionPerformed(ActionEvent e)
					{
						String message= "";
						SensorReading[] sonar = bot.getSonarValues();
						for(int i = 0; i<sonar.length; i++)
						{
							message += "\n["+sonar[i].getBearing()+"] : " + sonar[i].getLength();
						}
						JOptionPane.showMessageDialog(null, message);
					}
			});
		JButton drawBtn = new JButton("Draw");
		int xOff = 0;
		int yOff = drawPanel.size().height;
		rotateBtn.addActionListener(
				new ActionListener(){
			
						public void actionPerformed(ActionEvent e)
						{
							//Draw(bot.getSonarValues());
						}
				});
		
		
		topPanel.add(forwardBtn);
		topPanel.add(reverseBtn);
		topPanel.add(rotateBtn);
		topPanel.add(sonarBtn);
		topPanel.add(drawBtn);
		
		this.startDraw();
		bot.run();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponents(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(gridMap, null, 0,0);
	}

	
	void startDraw()
	{
		Thread drawThread = new Thread(){
			public void run(){
				while(true){
					draw();
					try {
						sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		drawThread.start();
	}
	
	public void draw() {
		bot.mapCurrentPosition();
	}

	
	public void mapUpdate(int x, int y, GridContents cont) {
		
		Color c = Color.white;
		switch(cont)
		{
		case Wall:
			c = Color.black;
			break;
		default: 
			c = Color.white;
		}
		gridMap.setRGB(x, y,c.getRGB());
		drawPanel.setImage(gridMap);
		drawPanel.repaint();
		
	}
}
