package main;

import java.awt.Point;

public class Vertex extends Point {

	private static final long serialVersionUID = -3729326166904750041L;
	
	public Vertex() {
		super();
	}
	
	public Vertex(int x, int y) {
		super(x, y);
	}
	
	public boolean isInOrderBefore(Vertex other) {
		return this.x < other.x || (this.x == other.x && this.y < other.y);
	}
	
	public boolean equals(Object o) {
		if (o instanceof Vertex) {
			Vertex v = (Vertex) o;
			return (x == v.x) && (y == v.y);
		}
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		return "(" + x + "|" + y + ")";
	}
}
