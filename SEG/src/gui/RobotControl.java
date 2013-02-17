package gui;

import java.awt.GridLayout;
import robot.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javaclient3.structures.PlayerPose2d;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import map.Map;

import explore.ExploreTest;

public class RobotControl extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -576449275784661972L;
	private Robot robot;
	
	public RobotControl(Robot robot){
		super("Robot control");
		this.robot = robot;
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
		jbtnExplore.setEnabled(false);
		this.setLayout(new GridLayout(4, 3));
		this.add(new JLabel("X: "));
		this.add(jtX);
		this.add(jbtnUp);
		this.add(new JLabel("Y: "));
		this.add(jtY);
		this.add(jbtnDown);
		this.add(new JLabel("Yaw/Distance: "));
		this.add(jtV);
		this.add(jbtnLeft);
		
		this.add(jbtn);
		this.add(jbtnExplore);
		this.add(jbtnRight);
		this.pack();
		jbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double x = Double.valueOf(jtX.getText());
				double y = Double.valueOf(jtY.getText());
				double yaw = Double.valueOf(jtV.getText());
				PlayerPose2d pose = new PlayerPose2d(x, y, yaw);
				robot.move(pose);
				
			}
		});
		
		ActionListener moveRobot = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton btn = (JButton) e.getSource();
				String txt = btn.getText();
				double d = Double.valueOf(jtV.getText());
				if (txt.equals("Move Up")) robot.move('u', d);
				else if (txt.equals("Move Down")) robot.move('d', d);
				else if (txt.equals("Move Left")) robot.move('l', d);
				else if (txt.equals("Move Right")) robot.move('r', d);
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
		
	}

}
