package es.udc.santiago.maze.walker;

import java.awt.Point;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import es.udc.santiago.maze.utils.MazeUtils;

/**
 * This class represents the path walked by a process.
 * 
 * @author Santiago Munín González
 * 
 */
public class Path implements Serializable {
	private static final long serialVersionUID = 8301650252151209655L;
	private Point start;
	public static final byte NO_DIRECTION = 0;
	public static final byte UP = 0x1;
	public static final byte RIGHT = 0x2;
	public static final byte DOWN = 0x4;
	public static final byte LEFT = 0x8;
	private Queue<Byte> movements;

	public Path(Point start) {
		this.start = start;
		this.movements = new LinkedList<Byte>();
	}

	public Path(Point start, Queue<Byte> movements) {
		this.start = start;
		if (movements != null) {
			this.movements = movements;
		} else {
			this.movements = new LinkedList<Byte>();
		}
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	/**
	 * Gets the current point (walking all saved directions from the starting
	 * point).
	 * 
	 * @return coordinates x and y.
	 */
	public Point getCurrentPoint() {
		Point result = new Point(start);
		for (Iterator<Byte> iterator = movements.iterator(); iterator.hasNext();) {
			byte direction = (byte) iterator.next();
			result = MazeUtils.getNextPoint(result, direction);
		}
		return result;
	}

	public Queue<Byte> getMovements() {
		return new LinkedList<Byte>(this.movements);
	}

	public void setMovements(Queue<Byte> movements) {
		this.movements = movements;
	}

	/**
	 * Adds a movement
	 * 
	 * @param movement
	 *            Integer as direction (see Path's constants)
	 */
	public void addMovement(byte movement) {
		this.movements.add(movement);
	}

	/**
	 * Gets all points of a path.
	 * 
	 * @return A queue of points.
	 */
	public Queue<Point> getPoints() {
		Queue<Point> result = new LinkedList<Point>();
		Point currentPoint = new Point(start.x, start.y);
		result.add(currentPoint);
		for (Iterator<Byte> iterator = movements.iterator(); iterator.hasNext();) {
			byte direction = iterator.next();
			currentPoint = MazeUtils.getNextPoint(currentPoint, direction);
			result.add(currentPoint);
		}
		return result;
	}

	public Path clone() {
		return new Path((Point) start.clone(), new LinkedList<Byte>(
				this.movements));
	}

	public boolean equals(Object path) {
		Path p = (Path) path;
		return p.getCurrentPoint().equals(this.getCurrentPoint());
	}
}