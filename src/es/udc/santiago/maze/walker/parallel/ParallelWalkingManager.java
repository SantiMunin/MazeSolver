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
 * Manages all the parallel process, distributing directions between processes.
 * 
 * @author Santiago Munín González
 * 
 */
public class ParallelWalkingManager {
	/**
	 * Path found event code.
	 */
	public static final byte PATH_FOUND = 0;
	/**
	 * New directions event code.
	 */
	public static final byte NEW_DIRECTIONS = 1;
	/**
	 * Wall found event code.
	 */
	public static final byte WALL_FOUND = 2;
	public static final int TAG_DIRECTIONS = 0;
	/**
	 * Stores the processes which are waiting for work.
	 */
	private Queue<Integer> freeSons = new LinkedList<Integer>();
	private Queue<Entry<Path, Byte>> pendingDirections = new LinkedList<Entry<Path, Byte>>();
	private List<Point> walkedPoints = new LinkedList<Point>();
	private int nproc;
	private Maze maze;
	private long startTime;
	private boolean found = false;

	/**
	 * Creates a manager which will auto-generate the maze to be solved.
	 * 
	 * @param nproc
	 * @param mazeHeight
	 * @param mazeWidth
	 */
	public ParallelWalkingManager(int nproc, int mazeHeight, int mazeWidth) {
		this.nproc = nproc;
		this.maze = new Maze(mazeHeight, mazeWidth, false);
	}

	/**
	 * Creates a manager with the given maze.
	 * 
	 * @param nproc
	 *            Number of processes.
	 * @param maze
	 *            Maze.
	 */
	public ParallelWalkingManager(int nproc, Maze maze) {
		this.nproc = nproc;
		this.maze = maze;
	}

	/**
	 * Performs the initial steps.
	 */
	public void prepareJob() {
		// All sons are free at the beginning.
		for (int i = 1; i < nproc; i++) {
			this.freeSons.add(i);
		}
		// ParallelUtils.sendMaze(nproc, maze);
		List<Byte> directionsAsByte = new LinkedList<Byte>();
		for (Byte byte1 : MazeUtils.directionsByteToList(this.maze
				.findPossibleDirections(maze.getStart()))) {
			List<Byte> tempDirectionsList = new LinkedList<Byte>();
			tempDirectionsList.add(byte1);
			Byte directionsByte = MazeUtils
					.directionsListToByte(tempDirectionsList);
			directionsAsByte.add(directionsByte);
		}
		ParallelUtils.distributeDirections(nproc, directionsAsByte,
				this.maze.getStart());
		for (int i = 0; i < directionsAsByte.size(); i++) {
			this.freeSons.poll();
		}
	}

	/**
	 * Process the received operation.
	 * 
	 * @param operationData
	 *            Received bytes.
	 */
	private void processOperation(Byte[] operationData) {
		switch (operationData[0]) {
		case PATH_FOUND:
			pathFound(operationData[1]);
			break;
		case WALL_FOUND:
			wallFound(operationData[1]);
			break;
		case NEW_DIRECTIONS:
			newDirections(operationData[1], operationData[2]);
			break;
		}
	}

	/**
	 * Path found event.
	 * 
	 * @param son
	 *            Son process ID.
	 */
	private void pathFound(Byte son) {
		this.found = true;
		Path path = ParallelUtils.receivePath(0, son);
		MazeUtils.printResult(maze, path, startTime);
		ParallelUtils.tellSonsToStop(nproc);
	}

	/**
	 * Wall found event
	 * 
	 * @param son
	 *            Son process ID.
	 */
	private void wallFound(Byte son) {
		this.freeSons.add((int) son);
	}

	/**
	 * New directions event
	 * 
	 * @param son
	 *            Son process ID.
	 * @param directions
	 *            Byte of directions.
	 */
	private void newDirections(Byte son, Byte directions) {
		Path p = ParallelUtils.receivePath(0, son);
		Entry<Path, Byte> entry = new AbstractMap.SimpleEntry<Path, Byte>(p,
				directions);
		Point current = p.getCurrentPoint();
		if (!walkedPoints.contains(current)) {
			this.pendingDirections.add(entry);
			this.walkedPoints.add(current);
		}
	}

	/**
	 * Sends pending directions to free sons.
	 */
	private void distributePendingDirections() {
		while (pendingDirections.size() > 0 && freeSons.size() > 0) {
			Entry<Path, Byte> entry = pendingDirections.poll();
			ParallelUtils.sendDirection(freeSons.poll(), entry.getValue(),
					entry.getKey());
		}
	}

	/**
	 * Performs the main job.
	 */
	public void doJob() {
		startTime = System.currentTimeMillis();
		prepareJob();
		Byte[] data = new Byte[3];
		while (!found) {
			distributePendingDirections();
			MPI.COMM_WORLD.Recv(data, 0, 3, MPI.BYTE, MPI.ANY_SOURCE,
					ParallelUtils.TAG_COMMUNICATION);
			processOperation(data);
		}
	}
}