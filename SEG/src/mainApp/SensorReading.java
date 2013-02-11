package mainApp;

public class SensorReading {
	private double bearing;
	private double length;
	
	public double getBearing() {
		return bearing;
	}

	public double getLength() {
		return length;
	}
	
	public SensorReading(double bearing , double length)
	{
		this.bearing = bearing;
		this.length = length;
	}
}

