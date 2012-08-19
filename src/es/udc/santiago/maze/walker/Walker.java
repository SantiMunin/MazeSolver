package es.udc.santiago.maze.walker;

import java.awt.Point;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.utils.MazeUtils;

/**
 * Represents a walker in the maze.
 * 
 * @author Santiago Munín González
 * 
 */
public class Walker {
	private List<Entry<Path, List<Integer>>> pendingDirections;
	private Set<Point> walkedDirections;
	private Maze maze;

	public Walker(Maze maze) {
		pendingDirections = new LinkedList<Entry<Path, List<Integer>>>();
		walkedDirections = new HashSet<Point>();
		this.maze = maze;
	}

	public WalkResult walk() {
		List<Path> wrongPaths = new LinkedList<Path>();
		List<Integer> directions;
		int direction = Path.NO_DIRECTION;

		// Initializes path and point to maze's start
		Path currentPath = new Path((Point) maze.getStart().clone());
		Point currentPoint = (Point) maze.getStart().clone();

		while (!maze.getEnd().equals(currentPoint)) {
			// Finds out all possible directions from a point
			directions = maze.findPossibleDirections(currentPoint, direction);
			// If there aren't possible directions, just add the wrong path and
			// pick a pendant point
			if (directions.size() == 0) {
				wrongPaths.add(currentPath.clone());
				// If there is pending directions, pick one point and a
				// direction
				if (pendingDirections.size() > 0) {
					boolean newWayFound = false;
					while (!newWayFound && pendingDirections.size() > 0) {
						Entry<Path, List<Integer>> pendingDirectionsEntry = pendingDirections
								.get(0);
						directions = pendingDirectionsEntry.getValue();
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
							List<Integer> tempDirections = pendingDirectionsEntry
									.getValue();
							tempDirections.remove(0);
							pendingDirectionsEntry.setValue(tempDirections);
							pendingDirections.set(0, pendingDirectionsEntry);
							newWayFound = true;
						}
					}
				}
			} else {
				direction = directions.get(0);
				if (directions.size() > 1
						&& !walkedDirections.contains(currentPoint)) {
					List<Integer> remainingDirections = new LinkedList<Integer>();
					for (int i = 1; i < directions.size(); i++) {
						remainingDirections.add(directions.get(i));
					}
					pendingDirections
							.add(new AbstractMap.SimpleEntry<Path, List<Integer>>(
									currentPath.clone(), remainingDirections));
					// FIXME this shouldn't be neccessary
					walkedDirections.add((Point) currentPoint.clone());
				}
			}
			currentPoint = MazeUtils.getNextPoint(currentPoint, direction);
			currentPath.addMovement(direction);
		}
		return new WalkResult(currentPath, wrongPaths);
	}

	/**
	 * Represents the result of the walk.
	 * 
	 * @author Santiago Munín González
	 * 
	 */
	public class WalkResult {
		private Path correctPath;
		private List<Path> wrongPaths;

		public Path getCorrectPath() {
			return correctPath;
		}

		public void setCorrectPath(Path correctPath) {
			this.correctPath = correctPath;
		}

		public List<Path> getWrongPaths() {
			return wrongPaths;
		}

		public void setWrongPaths(List<Path> wrongPaths) {
			this.wrongPaths = wrongPaths;
		}

		public WalkResult(Path correctPath, List<Path> wrongPaths) {
			this.correctPath = correctPath;
			this.wrongPaths = wrongPaths;
		}
	}
}
