package es.udc.santiago.maze;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Checks the correct functionality of Cell class.
 * 
 * @author Santiago Munín González
 * 
 */
public class CellTest {

	@Test
	/**
	 * Checks Cell.hasAllWallsUp() method.
	 */
	public void testHasAllWallsUp() {
		Cell c1 = new Cell(0, 0, false, false, false, false);
		Cell c2 = new Cell(0, 0, true, false, false, false);
		Cell c3 = new Cell(0, 0, false, true, false, false);
		Cell c4 = new Cell(0, 0, false, false, true, false);
		Cell c5 = new Cell(0, 0, false, false, false, true);
		Cell c6 = new Cell(0, 0, true, true, true, true);
		assertFalse(c1.hasAllWallsUp());
		assertFalse(c2.hasAllWallsUp());
		assertFalse(c3.hasAllWallsUp());
		assertFalse(c4.hasAllWallsUp());
		assertFalse(c5.hasAllWallsUp());
		assertTrue(c6.hasAllWallsUp());
	}

	@Test
	/**
	 * Checks Cell.hasAnyWallUp() method.
	 */
	public void testHasAnyWallUp() {
		Cell c1 = new Cell(0, 0, false, false, false, false);
		Cell c2 = new Cell(0, 0, true, false, false, false);
		Cell c3 = new Cell(0, 0, false, true, false, false);
		Cell c4 = new Cell(0, 0, false, false, true, false);
		Cell c5 = new Cell(0, 0, false, false, false, true);
		Cell c6 = new Cell(0, 0, false, false, false, true);
		assertFalse(c1.hasAnyWallUp());
		assertTrue(c2.hasAnyWallUp());
		assertTrue(c3.hasAnyWallUp());
		assertTrue(c4.hasAnyWallUp());
		assertTrue(c5.hasAnyWallUp());
		assertTrue(c6.hasAnyWallUp());
	}

}
