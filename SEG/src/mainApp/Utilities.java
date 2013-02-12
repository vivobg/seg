package mainApp;

public class Utilities {
	 public static double Round(double number, int decimals)
	    {
	    double mod = Math.pow(10.0, decimals);
	    return Math.round(number * mod ) / mod;
	    }
	 public static int roundLength(double number){
		 return (int) Round(number,0);
	 }
	 public static double toDegrees(double yaw) 
		{
			if(yaw >= 0) return Math.toDegrees(yaw);
			else
			{
				return toDegrees(2 *Math.PI  + yaw);
			}

		}
	 public static void pause(long millis) {
			try {
				Thread.sleep(millis);
			} 
			catch (InterruptedException e) 
			{

			}
		}
}
