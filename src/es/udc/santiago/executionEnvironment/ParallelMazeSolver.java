package es.udc.santiago.executionEnvironment;

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

	private static long startTime;

	public static void main(String[] args) throws InterruptedException {
		if (args.length != 8) {
			if (Integer.valueOf(args[0]) == 0) { // Father
				printUsage();
			}
			System.exit(-1);
		}
		args = MPI.Init(args);
		final int me = MPI.COMM_WORLD.Rank();
		int nproc = MPI.COMM_WORLD.Size();
		Maze maze = null;
		if (me == 0) {
			int mazeHeight = Integer.valueOf(args[0]);
			int mazeWidth = Integer.valueOf(args[1]);
			ParallelUtilities.log(me, "Generating maze.");
			maze = new Maze(mazeHeight, mazeWidth, false);
			ParallelUtilities.log(me, "Maze generated.");
		}
		startTime = System.currentTimeMillis();
		if (nproc == 1) {
			// Sequential
			SequentialWalker walker = new SequentialWalker(maze);
			Path result = walker.walk();
			printResult(maze, result, startTime);
		} else {
			doJob(nproc, me, maze);
			ParallelUtilities.log(me, "EXITING");
		}
		MPI.Finalize();
	}

	/**
	 * Performs the job.
	 * 
	 * @param nproc
	 *            Number of processes.
	 * @param me
	 *            Process ID.
	 * @param maze
	 *            Maze.
	 * @throws InterruptedException
	 *             if there is any problem with any thread.
	 */
	private static void doJob(int nproc, final int me, Maze maze)
			throws InterruptedException {
		byte direction = Path.NO_DIRECTION;
		if (me == 0) {
			// Master
			List<Byte> directions = maze
					.findPossibleDirections(maze.getStart());
			// Send required data
			ParallelUtilities.sendMaze(nproc, maze);
			ParallelUtilities.sendDirections(nproc, directions);
			// Wait for result
			Path result = ParallelUtilities.receiveResult();
			printResult(maze, result, startTime);
			// Tell other's sons to stop
			ParallelUtilities.tellSonsToStop(nproc);
		} else {
			// Slave
			// Receive maze
			maze = ParallelUtilities.receiveMaze(me);
			// Receive direction
			direction = ParallelUtilities.receiveDirection(me);
			final Path[] resultPath = new Path[1];
			final Maze threadMaze = maze;
			final byte threadDirection = direction;
			// Work
			Thread workThread = new Thread(new Runnable() {
				public void run() {
					try {
						SequentialWalker walker = new SequentialWalker(
								threadMaze);
						resultPath[0] = walker.walk(threadDirection);
						ParallelUtilities.log(me, "Path found!");
						// Path found, end thread.
					} catch (Exception e) {
						ParallelUtilities.log(me, "There was an exception: "
								+ e.getLocalizedMessage());
						e.printStackTrace();
					}
				}
			});
			workThread.start();
			Request finish = ParallelUtilities.receiveFinishRequest();
			// Checks if another process found the solution and, in that
			// case, it aborts the operation.
			while (resultPath[0] == null) {
				Thread.sleep(5000);
				if (finish.Test() != null) {
					ParallelUtilities.log(me, "Kill signal received");
					// Another process has found the correct path
					workThread.interrupt();
					break;
				}
			}
			if (resultPath[0] != null) {
				ParallelUtilities.sendResult(resultPath[0]);
			}
			finish.Free();
		}
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
		System.out.println("Maze " + maze.getHeight() + "x" + maze.getWidth()
				+ " solved!");
		System.out.println("Start point: " + maze.getStart());
		System.out.println("End point: " + result.getCurrentPoint());
		System.out.println("End point (map): " + maze.getEnd());
		System.out.println("Time: " + totalTime + " (ms).");
		MazeGraphics mg = new MazeGraphics(maze);
		Frame f = mg.getMapFrame("Maze");
		mg.addPath(new AbstractMap.SimpleEntry<Color, Path>(Color.BLACK, result));
		f.setVisible(true);
	}

	/**
	 * Prints correct usage.
	 */
	private static void printUsage() {
		System.out.println("Wrong number of arguments. Usage:");
		System.out
				.println("\tfmpjrun -np <number_of_processes> -class es.udc.santiago.executionEnvironment.ParallelMazeSolver <height> <width>");
	}
}