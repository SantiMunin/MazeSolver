package es.udc.santiago.maze.walker.parallel;

import java.awt.Color;
import java.awt.Frame;
import java.util.AbstractMap;
import java.util.List;

import mpi.MPI;
import mpi.Request;
import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.graphics.MazeGraphics;
import es.udc.santiago.maze.walker.Path;
import es.udc.santiago.maze.walker.sequential.SequentialWalker;

/**
 * Class which manages all the parallel process.
 * 
 * @author Santiago Munín González
 * 
 */
public class ParallelMazeSolver {

	private static int MAZE_WIDTH = 2000;
	private static int MAZE_HEIGHT = 2000;

	public static void main(String[] args) throws InterruptedException {
		args = MPI.Init(args);
		final int me = MPI.COMM_WORLD.Rank();
		int nproc = MPI.COMM_WORLD.Size();
		final Maze[] m = new Maze[1];
		if (me == 0) {
			log(me, "Generating maze.");
			m[0] = new Maze(MAZE_HEIGHT, MAZE_WIDTH);
			log(me, "Maze generated.");
		}
		long startTime = System.currentTimeMillis();
		if (nproc == 1) {
			// Sequential
			SequentialWalker walker = new SequentialWalker(m[0]);
			Path result = walker.walk();
			printResult(m[0], result, startTime);
		} else {
			final byte[] direction = new byte[1];
			if (me == 0) {
				// Master
				List<Byte> directions = m[0].findPossibleDirections(m[0]
						.getStart());
				// Send required data
				sendMaze(nproc, m);
				sendDirections(nproc, directions);
				// Wait for result
				Path[] result = new Path[1];
				MPI.COMM_WORLD.Recv(result, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE,
						10);
				printResult(m[0], result[0], startTime);
				// Tell other's sons to stop
				tellSonsToStop(nproc);
			} else {
				// Slave
				// Receive maze
				MPI.COMM_WORLD.Recv(m, 0, 1, MPI.OBJECT, 0, 1);
				if (me > m[0].findPossibleDirections(m[0].getStart()).size()) {
					log(me, "This process is no needed, exiting.");
					MPI.Finalize();
					System.exit(0);
				}
				// Receive direction
				MPI.COMM_WORLD.Recv(direction, 0, 1, MPI.BYTE, 0, 2);
				log(me, "Received direction: " + direction[0]);
				final Path[] resultPath = new Path[1];
				resultPath[0] = null;
				// Work
				Thread workThread = new Thread(new Runnable() {
					public void run() {
						try {
							SequentialWalker walker = new SequentialWalker(m[0]);
							resultPath[0] = walker.walk(direction[0]);
							log(me, "Path found!");
							// Path found, end thread.
						} catch (Exception e) {
							log(me,
									"There was an exception: "
											+ e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				});
				workThread.start();
				Request finish = MPI.COMM_WORLD.Irecv(direction, 0, 1,
						MPI.BYTE, 0, 99);
				// Checks if another process found the solution and, in that
				// case, it aborts the operation.
				while (resultPath[0] == null) {
					Thread.sleep(5000);
					if (finish.Test() != null) {
						log(me, "Kill signal received");
						// Another process has found the correct path
						workThread.interrupt();
						break;
					}
				}
				if (resultPath[0] != null) {
					MPI.COMM_WORLD.Send(resultPath, 0, 1, MPI.OBJECT, 0, 10);
				}
				finish.Free();
			}
		}
		log(me, "EXITING");
		MPI.Finalize();
	}

	/**
	 * Prints work's result.
	 * 
	 * @param maze
	 *            Maze.
	 * @param result
	 *            Path from start to the end.
	 * @param startTime
	 *            First time measurement.
	 */
	private static void printResult(Maze maze, Path result, long startTime) {
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Start point: " + maze.getStart());
		System.out.println("End point: " + result.getCurrentPoint());
		System.out.println("End point (map): " + maze.getEnd());
		System.out.println("Milliseconds: " + totalTime);
		MazeGraphics mg = new MazeGraphics(maze);
		Frame f = mg.getMapFrame("Maze");
		mg.addPath(new AbstractMap.SimpleEntry<Color, Path>(Color.BLACK, result));
		f.setVisible(true);
	}

	/**
	 * Prints information.
	 * 
	 * @param me
	 *            Process ID.
	 * @param message
	 *            Message.
	 */
	private static void log(int me, String message) {
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
	private static void sendMaze(int nproc, Maze[] maze) {
		log(0, "Sending maze to sons");
		for (int i = 1; i < nproc; i++) {
			MPI.COMM_WORLD.Send(maze, 0, 1, MPI.OBJECT, i, 1);
		}
		log(0, "Maze sent to all sons.");
	}

	/**
	 * Send initial directions to sons.
	 * 
	 * @param nproc
	 *            Number of process.
	 * @param directions
	 *            List of directions
	 */
	private static void sendDirections(int nproc, List<Byte> directions) {
		log(0, "Sending directions. Number: " + directions.size());
		byte[] temp = new byte[1];
		for (int i = 0; i < directions.size() && i < nproc - 1; i++) {
			temp[0] = directions.get(i);
			MPI.COMM_WORLD.Send(temp, 0, 1, MPI.BYTE, i + 1, 2);
		}
		log(0, "Directions sent.");
	}

	/**
	 * Sends a byte to all sons (works as a kill signal).
	 * 
	 * @param nproc
	 *            Number of process
	 */
	private static void tellSonsToStop(int nproc) {
		log(0, "Asking all sons to stop working.");
		byte[] temp = new byte[1];
		for (int i = 1; i < nproc; i++) {
			temp[0] = 0;
			MPI.COMM_WORLD.Isend(temp, 0, 1, MPI.BYTE, i, 99);
		}
	}
}