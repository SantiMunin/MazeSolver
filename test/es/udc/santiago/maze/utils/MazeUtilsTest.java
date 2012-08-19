package es.udc.santiago.maze.utils;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import es.udc.santiago.maze.walker.Path;

/**
 * Checks MazeUtils
 * 
 * @author Santiago Munín González
 * 
 */
public class MazeUtilsTest {

	@Test
	public void testGetNextPoint() {
		Point point = new Point(5, 5);
		assertEquals(MazeUtils.getNextPoint(point, Path.UP), new Point(5, 4));
		assertEquals(MazeUtils.getNextPoint(point, Path.DOWN), new Point(5, 6));
		assertEquals(MazeUtils.getNextPoint(point, Path.LEFT), new Point(4, 5));
		assertEquals(MazeUtils.getNextPoint(point, Path.RIGHT), new Point(6, 5));
	}

}
