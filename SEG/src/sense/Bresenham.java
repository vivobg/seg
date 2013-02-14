package sense;

import map.Map;

public class Bresenham {
	
	public static void line(Map map, int x0, int y0, int x1, int y1) {
		 
		  int dx = Math.abs(x1-x0), sx = x0<x1 ? 1 : -1;
		  int dy = Math.abs(y1-y0), sy = y0<y1 ? 1 : -1; 
		  int err = (dx>dy ? dx : -dy)/2, e2;
		 
		  for(;;){
		    map.setValue(x0,y0, Map.EMPTY);
		    if (x0==x1 && y0==y1) break;
		    e2 = err;
		    if (e2 >-dx) { err -= dy; x0 += sx; }
		    if (e2 < dy) { err += dx; y0 += sy; }
		  }
		  
		}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
