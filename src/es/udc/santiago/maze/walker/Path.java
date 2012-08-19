package es.udc.santiago.maze.walker;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import es.udc.santiago.maze.utils.MazeUtils;

/**
 * This class represents the path done by a process.
 * 
 * @author Santiago Munín González
 * 
 */
public class Path {
	private Point start;
	public static final int NO_DIRECTION = -1;
	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	private Queue<Integer> movements;

	public Path(Point start) {
		this.start = start;
		this.movements = new LinkedList<Integer>();
	}

	public Path(Point start, Queue<Integer> movements) {
		this.start = start;
		if (movements != null) {
			this.movements = movements;
		} else {
			this.movements = new LinkedList<Integer>();
		}
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	/**
	 * Gets the current point from the directions walked.
	 * 
	 * @return coordinates x and y.
	 */
	public Point getCurrentPoint() {
		Point result = new Point(start);
		for (Iterator<Integer> iterator = movements.iterator(); iterator
				.hasNext();) {
			int direction = (int) iterator.next();
			result = MazeUtils.getNextPoint(result, direction);
		}
		return result;
	}

	public Queue<Integer> getMovements() {
		return new LinkedList<Integer>(this.movements);
	}

	public void setMovements(Queue<Integer> movements) {
		this.movements = movements;
	}

	/**
	 * Adds a movement
	 * 
	 * @param movement
	 *            Integer as direction (see Path's constants)
	 */
	public void addMovement(int movement) {
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
		for (Iterator<Integer> iterator = movements.iterator(); iterator
				.hasNext();) {
			int direction = iterator.next();
			currentPoint = MazeUtils.getNextPoint(currentPoint, direction);
			result.add(currentPoint);
		}
		return result;
	}

	/**
	 * Returns a new path with the same attributes.
	 */
	public Path clone() {
		return new Path((Point) start.clone(), new LinkedList<Integer>(this.movements));
	}

	public boolean equals(Object path) {
		Path p = (Path) path;
		return p.getCurrentPoint().equals(this.getCurrentPoint());
	}
}