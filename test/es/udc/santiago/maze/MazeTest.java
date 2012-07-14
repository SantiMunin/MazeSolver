package es.udc.santiago.maze;

import static org.junit.Assert.*;

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
		Maze m = new Maze(height, width);
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
}
