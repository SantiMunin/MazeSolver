package es.udc.santiago.maze.walker.sequential;

import java.awt.Point;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.utils.MazeUtils;
import es.udc.santiago.maze.walker.Path;

/**
 * Represents a walker in the maze.
 * 
 * @author Santiago Munín González
 * 
 */
public class SequentialWalker {
	private List<Entry<Path, Byte>> pendingDirections;
	private Set<Point> walkedDirections;
	private Maze maze;

	public SequentialWalker(Maze maze) {
		pendingDirections = new LinkedList<Entry<Path, Byte>>();
		walkedDirections = new HashSet<Point>();
		this.maze = maze;
	}

	/**
	 * Walks through the maze.
	 * 
	 * @return Path from start to end.
	 */
	public Path walk() {
		return walk(Path.NO_DIRECTION);
	}

	/**
	 * Walks through the maze.
	 * 
	 * @param startDirection
	 *            Default direction
	 * @return Path from start to end.
	 */
	public Path walk(byte startDirection) {
		// List<Path> wrongPaths = new LinkedList<Path>();
		List<Byte> directions;

		// Initializes path and point to maze's start
		Path currentPath = new Path((Point) maze.getStart());
		Point currentPoint = (Point) maze.getStart().clone();
		byte direction = Path.NO_DIRECTION;
		if (startDirection >= 0 && startDirection <= 3) {
			if (MazeUtils.directionsByteToList(maze.findPossibleDirections(currentPoint)).contains(
					startDirection)) {
				direction = startDirection;
				currentPoint = MazeUtils.getNextPoint(currentPoint, direction);
				currentPath.addMovement(direction);
			}
		}

		while (!maze.getEnd().equals(currentPoint)) {
			// Finds out all possible directions from a point
			directions = MazeUtils.directionsByteToList(maze.findPossibleDirections(currentPoint, direction));
			// If there aren't possible directions, just pick a pendant point
			if (directions.size() == 0) {
				// If there is pending directions, pick one point and a
				// direction
				if (pendingDirections.size() > 0) {
					boolean newWayFound = false;
					while (!newWayFound && pendingDirections.size() > 0) {
						Entry<Path, Byte> pendingDirectionsEntry = pendingDirections
								.get(0);
						directions = MazeUtils.directionsByteToList(pendingDirectionsEntry.getValue());
						// All directions entry will have at least 1 element
						direction = directions.get(0);
						if (directions.size() == 1) {
							currentPath = pendingDirectionsEntry.getKey()
									.clone();
							currentPoint = currentPath.getCurrentPoint();
							newWayFound = true;
							pendingDirections.remove(0);
						} else {
							// directions.size() > 1
							currentPath = pendingDirectionsEntry.getKey()
									.clone();
							currentPoint = currentPath.getCurrentPoint();
							List<Byte> tempDirections = MazeUtils.directionsByteToList(pendingDirectionsEntry
									.getValue());
							tempDirections.remove(0);
							pendingDirectionsEntry.setValue(MazeUtils.directionsListToByte(tempDirections));
							pendingDirections.set(0, pendingDirectionsEntry);
							newWayFound = true;
						}
					}
				}
			} else {
				direction = directions.get(0);
				if (directions.size() > 1
						&& !walkedDirections.contains(currentPoint)) {
					List<Byte> remainingDirections = new LinkedList<Byte>();
					for (int i = 1; i < directions.size(); i++) {
						remainingDirections.add(directions.get(i));
					}
					pendingDirections
							.add(new AbstractMap.SimpleEntry<Path, Byte>(
									currentPath.clone(), MazeUtils.directionsListToByte(remainingDirections)));
					// FIXME this shouldn't be neccessary
					walkedDirections.add((Point) currentPoint.clone());
				}
			}
			currentPoint = MazeUtils.getNextPoint(currentPoint, direction);
			currentPath.addMovement(direction);
		}
		return currentPath;
	}
}
