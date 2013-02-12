package mainApp;

public class Control {
	BotMode botMode = BotMode.Solo;
	public Control(String[] args)
	{
		processArgs(args);
	}
	
	private void processArgs(String[] args) {
		//java MainApp -solo -explore -map map1 -multi -collect 1 1 2 0 -map map2
		for(int i = 0; i < args.length; i++)
		{
			if(args[i] == "-solo")
			{
				switchToSolo();
			}
			else if(args[i] == "-multi")
			{
				switchToMulti();
			}
			else if (args[i] == "-explore")
			{
				explore(botMode);
			}
			else if(args[i] == "-map")
			{
				if(i + 1 < args.length)
				{
					String filename = args[i+1];
					saveMap(filename);
				}
			}
			else if(args[i] == "-collect")
			{
				if(i + 4 < args.length)
				{
					double x1 = Double.parseDouble(args[i+1]);
					double y1 = Double.parseDouble(args[i+2]);
					double x2 = Double.parseDouble(args[i+3]);
					double y2 = Double.parseDouble(args[i+4]);
					collect(x1,y1,x2,y2);
				}
			}
			
		}
		
	}

	private void collect(double x1, double y1, double x2, double y2) {
		// TODO Auto-generated method stub
		
	}

	private void switchToMulti() {
		botMode = BotMode.Multi;
		
	}

	private void saveMap(String filename) {
		// TODO Auto-generated method stub
		
	}

	private void explore(BotMode botMode) {
		// TODO Auto-generated method stub
		
	}

	private void switchToSolo() {
		botMode = BotMode.Solo;
	}
}
