package es.udc.santiago.maze.walker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.walker.sequential.SequentialWalker;

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
		SequentialWalker k;
		Path result;
		long time = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			m = new Maze(1000, 1000, true);
			k = new SequentialWalker(m);
			result = k.walk();
			assertEquals(result.getCurrentPoint(), m.getEnd());
			assertEquals(result.getStart(), m.getStart());
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - time;
		System.out.println("Milliseconds: " + totalTime);
		System.out.println("Milliseconds per maze: " + totalTime/10);
	}
}
