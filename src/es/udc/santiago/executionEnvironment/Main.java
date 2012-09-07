package es.udc.santiago.executionEnvironment;

import java.io.File;
import java.io.IOException;

import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.utils.MazeUtils;
import es.udc.santiago.maze.walker.Path;
import es.udc.santiago.maze.walker.parallel.ParallelWalker;
import es.udc.santiago.maze.walker.parallel.ParallelWalkingManager;
import es.udc.santiago.maze.walker.sequential.SequentialWalker;
import mpi.MPI;

public class Main {
	private static final String LAST_MAZE_FILENAME = "last.maze";

	public static void main(String[] args) {
		if (args.length < 7 && args.length > 8) {
			if (Integer.valueOf(args[0]) == 0) { // Master
				printUsage();
			}
			System.exit(-1);
		}
		Maze maze = null;
		if (args.length == 8) {
			int mazeHeight = Integer.valueOf(args[6]);
			int mazeWidth = Integer.valueOf(args[7]);
			maze = new Maze(mazeHeight, mazeWidth, false);
			if (Integer.valueOf(args[0]) == 0) {
				try {
					MazeUtils.mazeToFile(maze, new File(LAST_MAZE_FILENAME));
				} catch (IOException e) {
					System.out.println("PROBLEM serializing maze");
				}
			}
		} else {
			try {
				maze = MazeUtils.fileToMaze(new File(args[6]));
			} catch (IOException e) {
				System.out.println("Problem reading de maze, abort.");
				return;
			}
		}
		args = MPI.Init(args);
		final int me = MPI.COMM_WORLD.Rank();
		int nproc = MPI.COMM_WORLD.Size();
		long startTime = System.currentTimeMillis();
		// Sequential
		if (nproc == 1) {
			SequentialWalker walker = new SequentialWalker(maze);
			Path result = walker.walk();
			MazeUtils.printResult(maze, result, startTime);
			MPI.Finalize();
			return;
		}
		// Parallel
		if (me == 0) {
			ParallelWalkingManager pwm;
			pwm = new ParallelWalkingManager(nproc, maze);
			pwm.doJob();
		} else {
			ParallelWalker pw = new ParallelWalker(me, maze);
			pw.doJob();
		}
		MPI.Finalize();

	}

	/**
	 * Prints the correct usage.
	 */
	private static void printUsage() {
		System.out.println("Wrong number of arguments. Usage:");
		System.out
				.println("\tfmpjrun -np <number_of_processes> -class es.udc.santiago.executionEnvironment.ParallelMazeSolver <height> <width>");
	}
}
