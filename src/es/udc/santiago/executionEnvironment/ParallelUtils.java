package es.udc.santiago.executionEnvironment;

import java.awt.Point;
import java.util.List;

import mpi.MPI;
import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.walker.Path;
import es.udc.santiago.maze.walker.parallel.ParallelWalkingManager;

/**
 * Parallelization methods.
 * 
 * @author Santiago Munín González
 * 
 */
public class ParallelUtils {
	private static final byte TAG_MAZE = 1;
	private static final byte TAG_DIRECTION = 2;
	private static final byte TAG_RESULT = 3;

	/**
	 * Prints information.
	 * 
	 * @param me
	 *            Process ID.
	 * @param message
	 *            Message.
	 */
	public static void log(int me, String message) {
		if (me == 0) {
			System.out.println("#" + "MASTER" + ": " + message);
		} else {
			System.out.println("#" + me + ": " + message);
		}
	}

	/**
	 * Sends maze to all sons.
	 * 
	 * @param nproc
	 *            Number of process
	 * @param maze
	 *            Maze (an array of one).
	 */
	public static void sendMaze(int nproc, Maze maze) {
		Maze[] mazeArr = new Maze[1];
		mazeArr[0] = maze;
		log(0, "Sending maze to sons.");
		for (int i = 1; i < nproc; i++) {
			MPI.COMM_WORLD.Send(mazeArr, 0, 1, MPI.OBJECT, i, TAG_MAZE);
		}
		log(0, "Maze sent to all sons.");
		mazeArr = null;
	}

	/**
	 * Sends directions to sons. <b>NOTE:</b> It'll use the first processes, no
	 * matter whether they are busy or not.
	 * 
	 * @param nproc
	 *            Number of process.
	 * @param directions
	 *            List of directions byte (not single directions).
	 */
	public static void distributeDirections(int nproc, List<Byte> directions,
			Point start) {
		byte[] temp = new byte[1];
		Path p = new Path(start);
		for (int i = 0; i < directions.size() && i < nproc - 1; i++) {
			temp[0] = directions.get(i);
			MPI.COMM_WORLD.Send(temp, 0, 1, MPI.BYTE, i + 1, TAG_DIRECTION);
			sendPath(i + 1, p);
		}
	}

	/**
	 * Sends directions to sons (async).
	 * 
	 * @param processes
	 *            List of processes.
	 * @param directions
	 *            List of directions.
	 */
	public static void sendDirectionsAsync(List<Integer> processes,
			List<Byte> directions) {
		for (int i = 0; i < directions.size() && i < processes.size(); i++) {
			sendDirectionAsync(processes.get(i), directions.get(i));
		}
	}

	/**
	 * Sends a direction to a process (async).
	 * 
	 * @param process
	 *            Process ID.
	 * @param direction
	 *            Direction.
	 */
	public static void sendDirectionAsync(int process, byte direction) {
		byte[] temp = new byte[1];
		temp[0] = direction;
		MPI.COMM_WORLD.Irsend(temp, 0, 1, MPI.BYTE, process, TAG_DIRECTION);
	}

	/**
	 * Sends a direction to a process and the path followed to reach its start
	 * point.
	 * 
	 * @param process
	 *            Process ID.
	 * @param direction
	 *            Direction.
	 * @param path
	 *            Path.
	 */
	public static void sendDirection(int process, byte direction, Path path) {
		byte[] temp = new byte[1];
		temp[0] = direction;
		MPI.COMM_WORLD.Send(temp, 0, 1, MPI.BYTE, process, TAG_DIRECTION);
		sendPath(process, path);
	}

	/**
	 * Sends a NO_DIRECTION byte to all sons (works as a kill signal).
	 * 
	 * @param nproc
	 *            Number of process
	 */
	public static void tellSonsToStop(int nproc) {
		log(0, "Asking all sons to stop working.");
		byte[] temp = new byte[1];
		for (int i = 1; i < nproc; i++) {
			temp[0] = Path.NO_DIRECTION;
			MPI.COMM_WORLD.Send(temp, 0, 1, MPI.BYTE, i, TAG_DIRECTION);
		}
		//TODO fix
	}

	/**
	 * Sends a path.
	 * 
	 * @param process
	 *            Destination.
	 * @param path
	 *            Path.
	 */
	public static void sendPath(int process, Path path) {
		Path[] pathArr = new Path[1];
		pathArr[0] = path;
		MPI.COMM_WORLD.Send(pathArr, 0, 1, MPI.OBJECT, process, TAG_RESULT);
		pathArr = null;
	}

	/**
	 * Sends a path (asynchronous message).
	 * 
	 * @param process
	 *            Destination.
	 * @param path
	 *            Path.
	 */
	public static void sendPathAsync(int process, Path path) {
		Path[] pathArr = new Path[1];
		pathArr[0] = path;
		MPI.COMM_WORLD.Irsend(pathArr, 0, 1, MPI.OBJECT, process, TAG_RESULT);
		pathArr = null;
	}

	/**
	 * Receives the correct path.
	 * 
	 * @param me
	 *            Process ID.
	 * @param source
	 *            Source ID, MPI.ANY_SOURCE if it doesn't matters.
	 * 
	 * @return Correct path.
	 */
	public static Path receivePath(int me, int source) {
		Path[] result = new Path[1];
		MPI.COMM_WORLD.Recv(result, 0, 1, MPI.OBJECT, source, TAG_RESULT);
		return result[0];
	}

	/**
	 * Receives the maze.
	 * 
	 * @param me
	 *            Process ID.
	 * @return Maze
	 */
	public static Maze receiveMaze(int me) {
		Maze[] m = new Maze[1];
		MPI.COMM_WORLD.Recv(m, 0, 1, MPI.OBJECT, 0, TAG_MAZE);
		return m[0];
	}

	/**
	 * Receives a direction.
	 * 
	 * @param me
	 *            Process ID.
	 * @return Direction
	 */
	public static byte receiveDirection(int me) {
		byte[] direction = new byte[1];
		MPI.COMM_WORLD.Recv(direction, 0, 1, MPI.BYTE, 0, TAG_DIRECTION);
		return direction[0];
	}

	/**
	 * Sends a wall found event to the master process.
	 * 
	 * @param me
	 *            Source process ID.
	 */
	public static void sendWall(int me) {
		Byte[] byteArr = new Byte[3];
		byteArr[0] = ParallelWalkingManager.WALL_FOUND;
		byteArr[1] = (byte) me;
		byteArr[2] = null;
		MPI.COMM_WORLD.Send(byteArr, 0, 3, MPI.BYTE, 0, MPI.ANY_TAG);
	}

	/**
	 * Sends new directions to the master process.
	 * 
	 * @param me
	 *            Source process ID.
	 * @param path
	 *            Walked path.
	 * @param directions
	 *            New directions.
	 * 
	 */
	public static void sendNewDirections(int me, Path path, byte directions) {
		Byte[] byteArr = new Byte[3];
		byteArr[0] = ParallelWalkingManager.NEW_DIRECTIONS;
		byteArr[1] = (byte) me;
		byteArr[2] = directions;
		MPI.COMM_WORLD.Send(byteArr, 0, 3, MPI.BYTE, 0, MPI.ANY_TAG);
		sendPath(0, path);
	}

	/**
	 * Sends the correct path to master.
	 * 
	 * @param me
	 *            Source process ID.
	 * @param path
	 *            Correct path.
	 */
	public static void sendResult(int me, Path path) {
		Byte[] byteArr = new Byte[3];
		byteArr[0] = ParallelWalkingManager.PATH_FOUND;
		byteArr[1] = (byte) me;
		byteArr[2] = null;
		MPI.COMM_WORLD.Send(byteArr, 0, 3, MPI.BYTE, 0, MPI.ANY_TAG);
		sendPath(0, path);
		log(me, "RESULT SENT!");
	}
}