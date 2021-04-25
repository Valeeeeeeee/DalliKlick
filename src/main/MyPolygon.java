package main;

import java.awt.Polygon;

public class MyPolygon extends Polygon {
	private static final long serialVersionUID = 7464255102895959681L;
	
	private boolean isTransparent;
	
	public boolean isTransparent() {
		return isTransparent;
	}
	
	public void setTransparent() {
		isTransparent = true;
	}
	
	public double calculateArea() {
		return 0.0;
	}
}
