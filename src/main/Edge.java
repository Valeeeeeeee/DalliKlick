package main;

import java.util.ArrayList;

import static util.Utilities.*;

public class Edge {
	private Vertex start;
	private Vertex end;
	
	private boolean isBorderEdge;
	private ArrayList<MyPolygon> polygons;
	
	public Edge(Vertex start, Vertex end, int lastX, int lastY) {
		if (start.x < end.x) {
			this.start = start;
			this.end = end;
		} else if (start.x > end.x) {
			this.start = end;
			this.end = start;
		} else if (start.y < end.y) {
			this.start = start;
			this.end = end;
		} else {
			this.start = end;
			this.end = start;
		}
		isBorderEdge = isBorderEdge(lastX, lastY);
		polygons = new ArrayList<>();
	}
	
	public Vertex getStart() {
		return start;
	}
	
	public Vertex getEnd() {
		return end;
	}
	
	public Vertex getOther(Vertex vertex) {
		if (vertex.equals(start))	return end;
		if (vertex.equals(end))		return start;
		return null;
	}
	
	public Vertex getOther(Vertex v1, Vertex v2, Vertex v3) {
		if (v1.equals(start)) {
			if (v2.equals(end))			return v3;
			else if (v3.equals(end))	return v2;
		} else if (v2.equals(start)) {
			if (v3.equals(end))			return v1;
			else if (v1.equals(end))	return v3;
		} else if (v3.equals(start)) {
			if (v1.equals(end))			return v2;
			else if (v2.equals(end))	return v1;
		}
		return null;
	}
	
	public void resetPolygons() {
		polygons.clear();
	}
	
	public boolean checkNewPolygon(MyPolygon polygon) {
		if (polygons.size() == 0)	return true;
		if (polygons.size() == 2)	return false;
		Vertex other1 = getOther(new Vertex(polygon.xpoints[0], polygon.ypoints[0]),
									new Vertex(polygon.xpoints[1], polygon.ypoints[1]),
									new Vertex(polygon.xpoints[2], polygon.ypoints[2]));
		Vertex other2 = getOther(new Vertex(polygons.get(0).xpoints[0], polygons.get(0).ypoints[0]),
									new Vertex(polygons.get(0).xpoints[1], polygons.get(0).ypoints[1]),
									new Vertex(polygons.get(0).xpoints[2], polygons.get(0).ypoints[2]));
		return !other1.equals(other2);
	}
	
	public void addPolygon(MyPolygon polygon) {
		if (polygons.size() > 0) {
			if (polygons.get(0).equals(polygon))	return;
			polygons.get(0).setNeighbour(polygon);
			polygon.setNeighbour(polygons.get(0));
		}
		polygons.add(polygon);
	}
	
	public boolean missesPolygon() {
		return polygons.size() < (isBorderEdge ? 1 : 2);
	}
	
	public int numberOfMissingPolygons() {
		return (isBorderEdge ? 1 : 2) - polygons.size();
	}
	
	public int numberOfPolygons() {
		return polygons.size();
	}
	
	public boolean contains(Vertex vertex, boolean onlyInTheMiddle) {
		if (vertex.equals(start) || vertex.equals(end))	return !onlyInTheMiddle;
		return Math.abs(vertex.y - getYValue(vertex.x)) < EPSILON || Math.abs(vertex.x - getXValue(vertex.y)) < EPSILON;
	}
	
	public double getXValue(double y) {
		double startX = start.x, startY = start.y, endX = end.x, endY = end.y;
		if ((y > startY && y > endY) || (y < startY && y < endY))	return -1.0;
		if (endY - startY == 0)	return startX;
		return startX + (y - startY) * (endX - startX) / (endY - startY);
	}
	
	public double getYValue(int x) {
		double startX = start.x, startY = start.y, endX = end.x, endY = end.y;
		if ((x > startX && x > endX) || (x < startX && x < endX))	return -1.0;
		if (endX - startX == 0)	return startY;
		return startY + (x - startX) * (endY - startY) / (endX - startX);
	}
	
	public double length() {
		return distance(start, end);
	}
	
	public boolean isBorderEdge(int lastX, int lastY) {
		if (start.x == end.x && (start.x == 0 || start.x == lastX))	return true;
		if (start.y == end.y && (start.y == 0 || start.y == lastY))	return true;		
		return false;
	}
	
	public String toString() {
		return String.format("[(%3d|%3d) - (%3d|%3d)]", start.x, start.y, end.x, end.y);
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Edge)) return false;
		Edge other = (Edge) o;
		return start.equals(other.start) && end.equals(other.end);
	}
}
