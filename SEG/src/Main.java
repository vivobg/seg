import javax.swing.JFrame;

import javaclient3.PlayerClient;
import javaclient3.Position2DInterface;
import javaclient3.PlayerException;
import javaclient3.structures.PlayerConstants;
import javaclient3.RangerInterface;


public class Main 
{
	
	static double forwardSpeed = 0.5;
	
	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setTitle("Main Control");
		frame.setSize(500,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
       
	  }
	}
	
