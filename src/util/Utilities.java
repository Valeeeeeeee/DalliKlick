package util;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import main.Edge;
import main.MyArrayList;
import main.MyPolygon;
import main.Vertex;

public class Utilities {
	
	public static final double EPSILON = 0.0000001;
	
	public static Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
	
	public static void log(String format, Object... args) {
		System.out.println(String.format(format, args));
	}
	
	public static void log(Object object) {
		System.out.println(object);
	}
	
	public static void log() {
		System.out.println();
	}
	
	public static MyArrayList<Vertex> revertElementOrder(MyArrayList<Vertex> list) {
		MyArrayList<Vertex> reverted = new MyArrayList<>();
		
		for (int i = 0; i < list.size(); i++) {
			reverted.add(0, list.get(i));
		}
		
		return reverted;
	}
	
	public static MyArrayList<Vertex> orderCounterClockwise(Vertex v1, Vertex v2, Vertex v3) {
		MyArrayList<Vertex> counterClockwise = new MyArrayList<>();
		
		boolean isCC = getDeterminant(v1, v2, v3) > 0;
		if (v1.isInOrderBefore(v2)) {
			// v1 < v2
			if (v1.isInOrderBefore(v3)) {
				// v1 < v3
				counterClockwise.add(v1);
				counterClockwise.add(v2);
				counterClockwise.add(isCC ? 2 : 1, v3);
			} else {
				// v3 < v1 < v2
				counterClockwise.add(v3);
				counterClockwise.add(v1);
				counterClockwise.add(isCC ? 2 : 1, v2);
			}
		} else {
			// v2 < v1
			if (v2.isInOrderBefore(v3)) {
				// v2 < v3
				counterClockwise.add(v2);
				counterClockwise.add(v3);
				counterClockwise.add(isCC ? 2 : 1, v1);
			} else {
				// v3 < v2 < v1
				counterClockwise.add(v3);
				counterClockwise.add(v1);
				counterClockwise.add(isCC ? 2 : 1, v2);
			}
		}
		
		return counterClockwise;
	}
	
	public static int getIndexOfFirstVertexInOrder(List<Vertex> list) {
		if (list == null || list.size() == 0)	return -1;
		int index = 0;
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i).isInOrderBefore(list.get(index))) {
				index = i;
			}
		}
		return index;
	}
	
	public static void addPolygonAscending(List<MyPolygon> list, MyPolygon polygon) {
		int index = -1;
		double area = polygon.getArea();
		if (list.size() == 0)	index = 0;
		else if (area >= list.get(list.size() - 1).getArea())	index = list.size();
		else {
			int lowerBound = 0, upperBound = list.size() - 1, nextCheck = 0;
			while (index == -1) {
				nextCheck = (upperBound + lowerBound) / 2;
				if (nextCheck == lowerBound) {
					if (area < list.get(lowerBound).getArea())		index = lowerBound;
					else if (area < list.get(upperBound).getArea())	index = upperBound;
					else											index = upperBound + 1;
				} else {
					if (area >= list.get(nextCheck).getArea())	lowerBound = nextCheck + 1;
					else										upperBound = nextCheck - 1;
				}
			}
		}
		list.add(index, polygon);
	}
	
	public static void addVertex(List<Vertex> list, Vertex vertex) {
		int index = 0;
		for (int i = 0; i < list.size() && index == 0; i++) {
			if (list.get(i).x > vertex.x || (list.get(i).x == vertex.x && list.get(i).y > vertex.y))	index = i;
		}
		list.add(index, vertex);
	}
	
	public static int getIndexOfVertex(List<Vertex> list, Vertex vertex) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(vertex))	return i;
		}
		return -1;
	}
	
	public static boolean intersect(Edge a, Edge b) {
		Vertex p1 = a.getStart(), p2 = a.getEnd(), p3 = b.getStart(), p4 = b.getEnd();
		
		int deltaxa = p2.x - p1.x;
		int deltaxb = p4.x - p3.x;
		int deltaya = p2.y - p1.y;
		int deltayb = p4.y - p3.y;
		
		if (deltaya * deltaxb == deltayb * deltaxa) {
			return a.contains(p3, true) || a.contains(p4, true) || b.contains(p1, true) || b.contains(p2, true);
		}
		
		double t = ((p1.y - p3.y) * deltaxb - (p1.x - p3.x) * deltayb) * 1.0 / (deltaxa * deltayb - deltaya * deltaxb);
		double u = ((p3.y - p1.y) * deltaxa - (p3.x - p1.x) * deltaya) * 1.0 / (deltaxb * deltaya - deltayb * deltaxa);
		
		return 0.0 < t && t < 1.0 && 0.0 < u && u < 1.0;
	}
	
	public static double distance(Point a, Point b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
	
	public static int getDeterminant(Vertex v1, Vertex v2, Vertex v3) {
		return v1.x * (v2.y - v3.y) + v2.x * (v3.y - v1.y) + v3.x * (v1.y - v2.y);
	}
	
	public static double getArea(Vertex v1, Vertex v2, Vertex v3) {
		return Math.abs(getDeterminant(v1, v2, v3)) * 1.0 / 2;
	}
	
	public static void showAll(List<?> list) {
		for (int i = 0; i < list.size(); i++) {
			if (!(list.get(i) instanceof JComponent))	continue;
			((JComponent) list.get(i)).setVisible(true);
		}
	}
	
	public static void hideAll(List<?> list) {
		if (list == null)	return;
		for (int i = 0; i < list.size(); i++) {
			if ((list.get(i) instanceof List<?>))	hideAll((List<?>) list.get(i));
			if (!(list.get(i) instanceof JComponent))	continue;
			((JComponent) list.get(i)).setVisible(false);
		}
	}
	
	public static BufferedImage loadImage(String fileName) {
		BufferedImage bImage = null;
		try {
			bImage = ImageIO.read(new File(fileName));
		} catch (Exception e) {
			log(String.format("The image at '%s' could not be loaded.", fileName));
			e.printStackTrace();
		}
		
		return bImage;
	}
}
