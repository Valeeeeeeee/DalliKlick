package main;

import java.awt.Polygon;

import static util.Utilities.*;

public class MyPolygon extends Polygon {
	private static final long serialVersionUID = 7464255102895959681L;
	
	private static int counter;
	private int id;
	
	private double area;
	
	private boolean isTransparent;
	
	private boolean active = true;
	
	private MyArrayList<Vertex> vertices;
	
	private MyArrayList<MyPolygon> neighbours;
	
	public MyPolygon() {
		super();
		id = counter++;
		vertices = new MyArrayList<>();
		neighbours = new MyArrayList<>();
	}
	
	public boolean isTransparent() {
		return isTransparent;
	}
	
	public void setTransparent() {
		isTransparent = true;
	}
	
	public double getArea() {
		return area;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public MyArrayList<MyPolygon> getNeighbours() {
		MyArrayList<MyPolygon> copyOfNeighbours = new MyArrayList<>();
		for (int i = 0; i < neighbours.size(); i++) {
			copyOfNeighbours.add(neighbours.get(i));
		}
		return copyOfNeighbours;
	}
	
	public void setNeighbour(MyPolygon neighbour) {
		if (!isNeighbour(neighbour))	neighbours.add(neighbour);
	}
	
	public boolean isNeighbour(MyPolygon polygon) {
		return neighbours.contains(polygon);
	}
	
	public void replaceEitherNeighbour(MyPolygon oneOldNeighbour, MyPolygon otherOldNeighbour, MyPolygon newNeighbour) {
		if (neighbours.contains(oneOldNeighbour)) {
			neighbours.remove(oneOldNeighbour);
			neighbours.remove(otherOldNeighbour);
			setNeighbour(newNeighbour);
		} else if (neighbours.contains(otherOldNeighbour)) {
			neighbours.remove(otherOldNeighbour);
			setNeighbour(newNeighbour);
		}
	}
	
	public void replaceNeighbour(MyPolygon oldNeighbour, MyPolygon newNeighbour) {
		if (neighbours.contains(oldNeighbour)) {
			neighbours.remove(oldNeighbour);
			setNeighbour(newNeighbour);
		}
	}
	
	private void setPointsFromVertices() {
		for (int i = 0; i < vertices.size(); i++) {
			addPoint(vertices.get(i).x, vertices.get(i).y);
		}
	}
	
	@Override
	public void addPoint(int x, int y) {
		super.addPoint(x, y);
	}
	
	public boolean isConvex() {
		for (int i = 0; i < vertices.size(); i++) {
			Vertex a = vertices.get(i), b = vertices.get(i + 1), c = vertices.get(i + 2);
			if (getDeterminant(a, b, c) < 0)	return false;
		}
		return true;
	}
	
	private double calculateArea() {
		double area = 0.0;
		
		for (int i = 0; i < vertices.size(); i++) {
			area += vertices.get(i).x * vertices.get(i + 1).y;
			area -= vertices.get(i).y * vertices.get(i + 1).x;
		}
		
		return area / 2;
	}
		
	public static MyPolygon newTriangle(Vertex v1, Vertex v2, Vertex v3) {
		MyPolygon polygon = new MyPolygon();
		
		polygon.vertices = orderCounterClockwise(v1, v2, v3);
		polygon.setPointsFromVertices();
		polygon.area = polygon.calculateArea();
		
		return polygon;
	}
	
	public static MyPolygon combine(MyPolygon a, MyPolygon b) {
		MyArrayList<Vertex> vsA = a.vertices, vsB = b.vertices;
		int countA = vsA.size(), countB = vsB.size();
		for (int indexA = 0; indexA < countA; indexA++) {
			int indexB = getIndexOfVertex(vsB, vsA.get(indexA));
			if (indexB != -1) {
				int successiveCommonVertices = 1;
				boolean ascendingInA = true;
				Vertex start = vsA.get(indexA), end = null;
				if (getIndexOfVertex(vsB, vsA.get(indexA + 1)) == (indexB + countB - 1) % countB) {
					ascendingInA = true;
					if (indexA == 0) {
						int move = 0;
						for (int i = 1; i < countA && i < countB; i++) {
							if (getIndexOfVertex(vsB, vsA.get(indexA - i)) != (indexB + i) % countB)	break;
							start = vsA.get(indexA - i);
							move++;
						}
						indexA = (indexA + countA - move) % countA;
						indexB = (indexB + countB + move) % countB;
					}
					for (int i = 1; i < countA && i < countB; i++) {
						if (getIndexOfVertex(vsB, vsA.get(indexA + i)) != (indexB + countB - i) % countB)	break;
						successiveCommonVertices++;
						end = vsA.get(indexA + i);
					}
				} else if (getIndexOfVertex(vsB, vsA.get(indexA - 1)) == (indexB + 1) % countB) {
					ascendingInA = false;
					for (int i = 1; i < countA && i < countB; i++) {
						if (getIndexOfVertex(vsB, vsA.get(indexA - i)) != (indexB + i) % countB)	break;
						successiveCommonVertices++;
						end = vsA.get(indexA - i);
					}
				}
				
				if (successiveCommonVertices > 1) {
					MyArrayList<Vertex> vertices = new MyArrayList<>();
					if (ascendingInA) {
						vertices.add(start);
						for (int i = 1; i < countB; i++) {
							if (vsB.get(indexB + i).equals(end))	break;
							vertices.add(vsB.get(indexB + i));
						}
						vertices.add(end);
						for (int i = successiveCommonVertices; i < countA; i++) {
							if (vsA.get(indexA + i).equals(start))	break;
							vertices.add(vsA.get(indexA + i));
						}
					} else {
						vertices.add(start);
						for (int i = 1; i < countB; i++) {
							if (vsB.get(indexB - i).equals(end))	break;
							vertices.add(vsB.get(indexB - i));
						}
						vertices.add(end);
						for (int i = successiveCommonVertices; i < countA; i++) {
							if (vsA.get(indexA - i).equals(start))	break;
							vertices.add(vsA.get(indexA - i));
						}
					}
					
					MyPolygon polygon = new MyPolygon();
					
					if (!ascendingInA)	vertices = revertElementOrder(vertices);
					int offset = getIndexOfFirstVertexInOrder(vertices);
					for (int i = 0; i < vertices.size(); i++) {
						polygon.vertices.add(vertices.get(offset + i));
					}
					
					polygon.setPointsFromVertices();
					
					polygon.area = polygon.calculateArea();
					
					for (MyPolygon neighbour : a.neighbours) {
						if (neighbour == b)	continue;
						polygon.setNeighbour(neighbour);
					}
					for (MyPolygon neighbour : b.neighbours) {
						if (neighbour == a)	continue;
						polygon.setNeighbour(neighbour);
					}
					
					return polygon;
				}
				
				break;
			}
		}
		
		log("Impossible to combine:");
		log("  a: " + a.toString());
		log("  b: " + b.toString());
		
		return null;
	}
	
	public String toString() {
		String polygon = "[#" + id + ";", sep = "";
		
		for (int i = 0; i < vertices.size(); i++) {
			polygon += sep + vertices.get(i);
			sep = "-";
		}
		
		polygon += "]";
		
		return polygon;
	}
}
