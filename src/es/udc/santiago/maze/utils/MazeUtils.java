package es.udc.santiago.maze.utils;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;

import es.udc.santiago.maze.Cell;
import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.graphics.MazeGraphics;
import es.udc.santiago.maze.walker.Path;

/**
 * Utilities which will help the development process.
 * 
 * @author Santiago Munín González
 * 
 */
public class MazeUtils {
	/**
	 * Gets the next point
	 * 
	 * @param from
	 *            Original point.
	 * @param direction
	 *            Walk direction.
	 * @return
	 */
	public static Point getNextPoint(Point from, int direction) {
		int x = from.x;
		int y = from.y;
		switch (direction) {
		case Path.UP:
			y--;
			break;
		case Path.RIGHT:
			x++;
			break;
		case Path.DOWN:
			y++;
			break;
		case Path.LEFT:
			x--;
			break;
		}
		return new Point(x, y);
	}

	/**
	 * Prints the java code which will generate the given maze.
	 * 
	 * @param maze
	 */
	public static void dumpMaze(Maze maze) {
		System.out.println("Cell[][] cells = new Cell[" + maze.getWidth()
				+ "][" + maze.getHeight() + "];");
		for (int i = 0; i < maze.getHeight(); i++) {
			for (int j = 0; j < maze.getWidth(); j++) {
				Cell c = maze.getCell(i, j);
				System.out.print("cells[" + i + "][" + j + "]=new Cell(" + i
						+ "," + j + "," + c.hasTopWall() + ","
						+ c.hasRightWall() + "," + c.hasBottomWall() + ","
						+ c.hasLeftWall() + ");\n");
			}
		}
		System.out.println("Start: " + maze.getStart());
		System.out.println("End: " + maze.getEnd());
	}

	/**
	 * Converts a byte of directions to a List in order to get a more friendly
	 * type.
	 * 
	 * @param directions
	 *            Byte of directions.
	 * @return List of directions.
	 */
	public static List<Byte> directionsByteToList(Byte directions) {
		List<Byte> result = new LinkedList<Byte>();
			if ((directions & Path.UP) == Path.UP) {
				result.add(Path.UP);
			}
			if ((directions & Path.RIGHT) == Path.RIGHT) {
				result.add(Path.RIGHT);
			}
			if ((directions & Path.DOWN) == Path.DOWN) {
				result.add(Path.DOWN);
			}
			if ((directions & Path.LEFT) == Path.LEFT) {
				result.add(Path.LEFT);
			}
		return result;
	}

	/**
	 * Converts a list of directions in a single byte.
	 * 
	 * @param directions
	 *            List.
	 * @return Byte.
	 */
	public static Byte directionsListToByte(List<Byte> directions) {
		byte result = 0;
		for (Byte dir : directions) {
			result = (byte) (result | dir);
		}
		return result;
	}

	/**
	 * Prints work result.
	 * 
	 * @param maze
	 *            Maze.
	 * @param result
	 *            Path from start to the end.
	 * @param startTime
	 *            First time measurement.
	 */
	public static void printResult(Maze maze, Path result, long startTime) {
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
	 * Serializes a maze in a file.
	 * 
	 * @param maze
	 *            Maze.
	 * @param file
	 *            Destination file.
	 * @throws IOException
	 *             If there is any problem with the maze writing.
	 */
	public static void mazeToFile(Maze maze, File file) throws IOException {
		FileOutputStream fout;
		fout = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(maze);
		oos.close();
	}

	/**
	 * Unserializes a maze from a file.
	 * 
	 * @param file
	 *            Source file.
	 * @return Unserialized Maze.
	 * @throws IOException
	 *             If there is any problem with the maze reading.
	 */
	public static Maze fileToMaze(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fin);
		Maze maze;
		try {
			maze = (Maze) ois.readObject();
			ois.close();
			return maze;
		} catch (ClassNotFoundException e) {
			// Won't happen
			return null;
		}
	}
}
