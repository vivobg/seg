package gui;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import map.Map;
import robot.Robot;

public class RobotControl extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -576449275784661972L;
	private Robot robot;
	private Map map;
	
	public RobotControl(Robot robot, Map map) {
		super("Robot control");
		this.robot = robot;
		this.map = map;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initWidgets();
	}

	private void initWidgets() {
		final JTextField jtX = new JTextField(4);
		final JTextField jtY = new JTextField(4);
		final JTextField jtV = new JTextField(4);
		JButton jbtn = new JButton("Move");
		JButton jbtnUp = new JButton("Move Up");
		JButton jbtnDown = new JButton("Move Down");
		JButton jbtnLeft = new JButton("Move Left");
		JButton jbtnRight = new JButton("Move Right");
		JButton jbtnExplore = new JButton("Explore");
		JButton jbtnSave = new JButton("Save Map");
		//jbtnExplore.setEnabled(false);
		this.setLayout(new GridLayout(5, 3));
		this.add(new JLabel("X:(INT x) "));
		this.add(jtX);
		this.add(jbtnUp);
		this.add(new JLabel("Y:(INT y) "));
		this.add(jtY);
		this.add(jbtnDown);
		this.add(new JLabel("Yaw/Distance: "));
		this.add(jtV);
		this.add(jbtnLeft);
		
		this.add(jbtn);
		this.add(jbtnExplore);
		this.add(jbtnRight);

		this.add(jbtnSave);
		this.pack();
		jbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int x = Integer.valueOf(jtX.getText());
				final int y = Integer.valueOf(jtY.getText());
				Thread moveThread = new Thread(){
					public void run(){
						Point target = Map.convertPlayerToInternal(x, y);
						robot.move(target);
					}
				};
				moveThread.start();
				
				
			}
		});
		
		ActionListener moveRobot = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton btn = (JButton) e.getSource();
				String txt = btn.getText();
				final double d = Double.valueOf(jtV.getText());				
				
				if (txt.equals("Move Up")) {
					Thread moveThread = new Thread(){
						public void run(){
							robot.move('u', d);
						}
					};
					moveThread.start();
					}
				else if (txt.equals("Move Down")){
					Thread moveThread = new Thread(){
						public void run(){
							robot.move('d', d);
						}
					};
					moveThread.start();
					}
				else if (txt.equals("Move Left")) {
					Thread moveThread = new Thread(){
						public void run(){
							robot.move('l', d);
						}
					};
					moveThread.start();
				}
				else if (txt.equals("Move Right")) {
					Thread moveThread = new Thread(){
						public void run(){
							robot.move('r', d);
						}
					};
					moveThread.start();
				}
				else if (txt.equals("Explore")){
					System.out.println("Exploration requested");
					robot.explore();
					
				}
			}
		};
		jbtnExplore.addActionListener(moveRobot);
		jbtnUp.addActionListener(moveRobot);
		jbtnDown.addActionListener(moveRobot);
		jbtnLeft.addActionListener(moveRobot);
		jbtnRight.addActionListener(moveRobot);
		
		jbtnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				long time = System.currentTimeMillis();
				Save.toPNG(map, time + ".png");

			}
		});

	}

}
