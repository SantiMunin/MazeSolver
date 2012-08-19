package es.udc.santiago.maze.utils;

import java.awt.Point;

import es.udc.santiago.maze.Cell;
import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.walker.Path;

/**
 * Class with some static methods which will help the development process.
 * 
 * @author Santiago Munín González
 * 
 */
public class MazeUtils {
	/**
	 * Gets the next point to walk.
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
	 * Prints the java code which will generate a given maze.
	 * @param maze
	 */
	public static void dumpMaze(Maze maze) {
		System.out.println("Cell[][] cells = new Cell["+maze.getWidth()+"]["+maze.getHeight()+"];");
		for (int i = 0; i < maze.getHeight(); i++) {
			for (int j = 0; j < maze.getWidth(); j++) {
				Cell c = maze.getCell(i, j);
				System.out.print("cells["+i+"]["+j+"]=new Cell("+i+","+j+","+c.hasTopWall()+","+c.hasRightWall()+","+c.hasBottomWall()+","+c.hasLeftWall()+");\n");
			}
		}
		System.out.println("Start: "+maze.getStart());
		System.out.println("End: "+maze.getEnd());
	}
}
