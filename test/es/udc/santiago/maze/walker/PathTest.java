package es.udc.santiago.maze.walker;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

/**
 * Checks Path class.
 * 
 * @author Santiago Munín González
 * 
 */
public class PathTest {
	/**
	 * getCurrentPoint()
	 */
	@Test
	public void testCurrentPoint() {
		Point point = new Point(50, 10);
		Path path = new Path(point);
		// Didn't walk
		assertEquals(point, path.getCurrentPoint());
		// Walking
		path.addMovement(Path.UP);
		path.addMovement(Path.UP);
		path.addMovement(Path.UP);
		path.addMovement(Path.LEFT);
		path.addMovement(Path.LEFT);
		assertEquals(new Point(48, 7), path.getCurrentPoint());
		path.addMovement(Path.DOWN);
		path.addMovement(Path.DOWN);
		path.addMovement(Path.DOWN);
		path.addMovement(Path.DOWN);
		path.addMovement(Path.DOWN);
		path.addMovement(Path.DOWN);
		path.addMovement(Path.RIGHT);
		path.addMovement(Path.RIGHT);
		path.addMovement(Path.RIGHT);
		path.addMovement(Path.RIGHT);
		path.addMovement(Path.RIGHT);
		assertEquals(new Point(53, 13), path.getCurrentPoint());
	}

}
