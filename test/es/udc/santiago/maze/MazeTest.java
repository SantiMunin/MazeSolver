package es.udc.santiago.maze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Test;

/**
 * Checks the correct functionality of Maze class.
 * 
 * @author Santiago Munín González
 * 
 */
public class MazeTest {

	@Test
	/**
	 *  This test just checks if all borders of the maze have the right walls and there aren't a cell with all borders up.
	 */
	public void testMaze() {
		int height = 1000;
		int width = 1800;
		Maze m = new Maze(height, width, true);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Cell cell = m.getCell(i, j);
				assertFalse(cell.hasAllWallsUp());
				if (i == 0) {
					// Top
					assertTrue(cell.hasTopWall());
				}
				if (i == height - 1) {
					// Bottom
					assertTrue(cell.hasBottomWall());
				}
				if (j == 0) {
					// Left
					assertTrue(cell.hasLeftWall());
				}
				if (j == width - 1) {
					// Right
					assertTrue(cell.hasRightWall());
				}
			}
		}
	}

	/**
	 * canWalk()
	 */
	@Test
	public void testCanWalk() {
		Cell[][] cells = new Cell[5][10];
		Cell c = new Cell(0, 0, true, false, true, true);
		cells[0][0] = c;
		c = new Cell(1, 0, true, true, true, false);
		cells[0][1] = c;
		Maze m = new Maze(cells, null, null);
		assertTrue(m.canWalk(new Point(0, 0), new Point(1, 0)));
		cells[0][1] = new Cell(1, 0, true, true, true, true);
		m = new Maze(cells, null, null);
		assertFalse(m.canWalk(new Point(0, 0), new Point(1, 0)));
		assertFalse(m.canWalk(new Point(0, 0), new Point(-1, 0)));
		assertFalse(m.canWalk(new Point(0, 0), new Point(200, 0)));
		cells[0][2] = new Cell(2, 0, true, false, false, false);
		cells[0][1] = new Cell(1, 0, true, false, true, true);
		assertTrue(m.canWalk(new Point(1, 0), new Point(2, 0)));
	}

	/**
	 * findPossibleDirections()
	 */
	@Test
	public void testFindPossibleDirections() {
		Cell[][] cells = new Cell[10][10];
		cells[5][5] = new Cell(5, 5, false, false, false, false);
		cells[6][5] = new Cell(6, 5, false, true, true, true);
		cells[5][6] = new Cell(5, 6, true, true, true, false);
		cells[4][5] = new Cell(4, 5, true, true, false, true);
		cells[5][4] = new Cell(5, 4, true, false, true, true);
		Maze m = new Maze(cells, null, null);
		assertEquals(4, m.findPossibleDirections(new Point(5, 5)).size());
		cells[5][5] = new Cell(5, 5, true, false, false, false);
		cells[6][5] = new Cell(6, 5, false, true, true, true);
		cells[5][6] = new Cell(5, 6, true, true, true, false);
		cells[4][5] = new Cell(4, 5, true, true, false, true);
		cells[5][4] = new Cell(5, 4, true, false, true, true);
		m = new Maze(cells, null, null);
		assertTrue(1 == m.findPossibleDirections(new Point(5, 5)).get(0));
		assertTrue(2 == m.findPossibleDirections(new Point(5, 5)).get(1));
		assertTrue(3 == m.findPossibleDirections(new Point(5, 5)).get(2));
	}
}
