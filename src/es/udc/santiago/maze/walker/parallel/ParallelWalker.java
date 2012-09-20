package es.udc.santiago.maze.walker.parallel;

import java.awt.Point;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import mpi.MPI;
import es.udc.santiago.executionEnvironment.ParallelUtils;
import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.utils.MazeUtils;
import es.udc.santiago.maze.walker.Path;

/**
 * @author Santiago Munín González
 * 
 */
public class ParallelWalker {
	private int me;
	private Maze maze;
	private Queue<Entry<Path, Byte>> pendingDirections = new LinkedList<Entry<Path, Byte>>();;
	private List<Point> walkedPoints = new LinkedList<Point>();
	private int newDirsSent = 0;

	public ParallelWalker(int me, Maze maze) {
		this.me = me;
		this.maze = maze;
	}

	/**
	 * Performs the main job.
	 */
	public void doJob() {
		Byte direction;
		Path currentPath;
		WalkResult wr;
		while (true) {
			Byte receivedDirections = ParallelUtils.receiveDirection(me,0);
			if (receivedDirections == Path.NO_DIRECTION) {
				ParallelUtils.log(me, "Kill signal received.");
				return;
			}
			pendingDirections.add(new AbstractMap.SimpleEntry<Path, Byte>(
					ParallelUtils.receivePath(me, 0), receivedDirections));
			while (pendingDirections.size() > 0) {
				Entry<Path, Byte> entry = pendingDirections.peek();
				List<Byte> directions = MazeUtils.directionsByteToList(entry
						.getValue());
				while (directions.size() > 0) {
					direction = directions.get(0);
					directions.remove(0);
					currentPath = entry.getKey().clone();
					wr = walk(currentPath, direction);
					currentPath = wr.path;
					if (wr.found) {
						ParallelUtils.sendResult(me, wr.path);
						return;
					} else {
						if (wr.newDirections.size() > 0) {
							Point currentPoint = wr.path.getCurrentPoint();
							if (!walkedPoints.contains(currentPoint)) {
								walkedPoints.add(currentPoint);
								/*if (newDirsSent > 10000) {*/
								//	newDirsSent += wr.newDirections.size();
								ParallelUtils.log(me, "Sending dirs");
									ParallelUtils
											.sendNewDirections(
													me,
													wr.path,
													MazeUtils
															.directionsListToByte(wr.newDirections));
							/*	} else {
									pendingDirections
											.add(new AbstractMap.SimpleEntry<Path, Byte>(
													wr.path,
													MazeUtils
															.directionsListToByte(wr.newDirections)));
								}*/
							}
						}
					}
				}
				pendingDirections.poll();
			}
			ParallelUtils.sendWall(me);
		}
	}
	/**
	 * Walk through the maze.
	 * @param path Walked path until current point.
	 * @param direction Default direction
	 * @return A WalkResult
	 */
	private WalkResult walk(Path path, byte direction) {
		List<Byte> directions = new LinkedList<Byte>();
		Point currentPoint = path.getCurrentPoint();
		while (true) {
			currentPoint = MazeUtils.getNextPoint(currentPoint, direction);
			path.addMovement(direction);
			if (currentPoint.equals(maze.getEnd())) {
				return new WalkResult(path, null, true);
			}
			directions = MazeUtils.directionsByteToList(maze
					.findPossibleDirections(currentPoint, direction));
			if (directions.size() == 0) {
				return new WalkResult(path, directions, false);
			}
			if (directions.size() == 1) {
				direction = directions.get(0);
			}
			if (directions.size() > 1) {
				return new WalkResult(path, directions, false);
			}
		}
	}
}
/**
 * Represents the result of a walk
 * @author Santiago Munín González
 *
 */
class WalkResult {
	protected Path path;
	protected List<Byte> newDirections;
	protected boolean found;

	public WalkResult(Path path, List<Byte> newDirections, boolean found) {
		this.path = path;
		this.newDirections = newDirections;
		this.found = found;
	}
}