package es.udc.santiago.executionEnvironment;

import java.util.List;

import mpi.MPI;
import mpi.Request;
import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.walker.Path;

public class ParallelUtilities {
	private static final byte TAG_MAZE = 1;
	private static final byte TAG_DIRECTION = 2;
	private static final byte TAG_RESULT = 3;
	private static final byte TAG_STOP = 4;

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
	 * Send initial directions to sons.
	 * 
	 * @param nproc
	 *            Number of process.
	 * @param directions
	 *            List of directions
	 */
	public static void sendDirections(int nproc, List<Byte> directions) {
		log(0, "Sending directions. Number: " + directions.size());
		byte[] temp = new byte[1];
		for (int i = 0; i < directions.size() && i < nproc - 1; i++) {
			temp[0] = directions.get(i);
			MPI.COMM_WORLD.Send(temp, 0, 1, MPI.BYTE, i + 1, TAG_DIRECTION);
		}
		log(0, "Directions sent.");
	}
	/**
	 * Get the request of an async-receiving action (direction).
	 * @return Receive request.
	 */
	public static Request receiveFinishRequest() {
		return MPI.COMM_WORLD.Irecv(new byte[1], 0, 1,
				MPI.BYTE, 0, TAG_STOP);
	}

	/**
	 * Sends a byte to all sons (works as a kill signal).
	 * 
	 * @param nproc
	 *            Number of process
	 */
	public static void tellSonsToStop(int nproc) {
		log(0, "Asking all sons to stop working.");
		byte[] temp = new byte[1];
		for (int i = 1; i < nproc; i++) {
			temp[0] = 0;
			MPI.COMM_WORLD.Isend(temp, 0, 1, MPI.BYTE, i, TAG_STOP);
		}
	}
	/**
	 * Sends result to master process.
	 */
	public static void sendResult(Path resultPath) {
		Path[] pathArr = new Path[1];
		pathArr[0] = resultPath;
		MPI.COMM_WORLD.Send(pathArr, 0, 1, MPI.OBJECT, 0,
				TAG_RESULT);
		pathArr = null;
	}
	/**
	 * Receives the correct path.
	 * 
	 * @return Correct path.
	 */
	public static Path receiveResult() {
		Path[] result = new Path[1];
		MPI.COMM_WORLD.Recv(result, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE,
				TAG_RESULT);
		return result[0];
	}

	/**
	 * Receives maze.
	 * 
	 * @param me
	 *            Process ID.
	 * @return Maze
	 */
	public static Maze receiveMaze(int me) {
		Maze[] m = new Maze[1];
		MPI.COMM_WORLD.Recv(m, 0, 1, MPI.OBJECT, 0, TAG_MAZE);
		if (me > m[0].findPossibleDirections(m[0].getStart()).size()) {
			log(me, "This process is no needed, exiting.");
			MPI.Finalize();
			System.exit(0);
		}
		return m[0];
	}

	/**
	 * Receives direction.
	 * 
	 * @param me
	 *            Process ID.
	 * @return Direction
	 */
	public static byte receiveDirection(int me) {
		byte[] direction = new byte[1];
		MPI.COMM_WORLD.Recv(direction, 0, 1, MPI.BYTE, 0, TAG_DIRECTION);
		log(me, "Received direction: " + direction[0]);
		return direction[0];
	}
}
