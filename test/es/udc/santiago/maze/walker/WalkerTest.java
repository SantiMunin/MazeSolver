package es.udc.santiago.maze.walker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.walker.Walker.WalkResult;

/**
 * Walker class.
 * 
 * @author Santiago Munín González
 * 
 */
public class WalkerTest {
	/**
	 * walk()
	 */
	@Test
	public void testWalk() {
		Maze m;
		Walker k;
		WalkResult wr;
		long time = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			m = new Maze(100, 500);
			k = new Walker(m);
			wr = k.walk();
			assertEquals(wr.getCorrectPath().getCurrentPoint(), m.getEnd());
			assertEquals(wr.getCorrectPath().getStart(), m.getStart());
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - time;
		System.out.println("Milliseconds: " + totalTime);
		System.out.println("Milliseconds per maze: " + totalTime/10);
	}
}
