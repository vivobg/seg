import javax.swing.JFrame;



public class MainApp
{
	
	static double forwardSpeed = 0.5;
	//This is the main method
	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setTitle("Main Control");
		frame.setSize(500,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
       
	  }
	}
	
