package es.udc.santiago.maze;

import java.awt.Frame;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import es.udc.santiago.maze.graphics.MazeGraphics;

public class Maze {
	private static int MINIMUM_DISTANCE_DIVISOR = 3;
	private Cell[][] data;
	private int width;
	private int height;
	private Point start;
	private Point end;

	public Maze(int height, int width) {
		this.width = width;
		this.height = height;
		this.generate();
	}

	/**
	 * Generates a perfect maze (one without any loops or closed circuits, and
	 * without any inaccessible areas.)
	 */
	private void generate() {
		// Initializes all cells
		data = new Cell[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data[i][j] = new Cell(i, j);
			}
		}

		// Picks start and end
		Random randomGenerator = new Random();
		this.start = new Point(randomGenerator.nextInt(width),
				randomGenerator.nextInt(height));
		this.end = new Point(randomGenerator.nextInt(width),
				randomGenerator.nextInt(height));
		while (start.distance(end) < width / MINIMUM_DISTANCE_DIVISOR) {
			this.end = new Point(randomGenerator.nextInt(width),
					randomGenerator.nextInt(height));
		}
		// Picks a random cell
		int x = randomGenerator.nextInt(height);
		int y = randomGenerator.nextInt(width);
		Cell currentCell = data[x][y];

		// Starts generation
		int visitedCells = 1;
		int totalCells = height * width;
		Queue<Cell> cellStack = new LinkedList<Cell>();
		while (visitedCells < totalCells) {
			cellStack.add(currentCell);
			Cell[] neighbors = getNeighbors(currentCell, true);
			List<Cell> neighborsNotNull = new ArrayList<Cell>();
			for (int i = 0; i < neighbors.length; i++) {
				if (neighbors[i] != null) {
					neighborsNotNull.add(neighbors[i]);
				}
			}
			int numberOfNeighbors = neighborsNotNull.size();
			if (numberOfNeighbors > 0) {
				// Choose one at random
				Cell random = neighborsNotNull.get(randomGenerator
						.nextInt(numberOfNeighbors));
				// Knocks down the wall between it and CurrentCell
				openPath(currentCell, random);
				// Writes changes
				data[random.getX()][random.getY()] = random;
				data[currentCell.getX()][currentCell.getY()] = currentCell;
				// Raises the number of visitedCells and continues the path with
				// the new cell chosen.
				visitedCells++;
				currentCell = random;
			} else {
				currentCell = cellStack.poll();
			}
		}
	}

	/**
	 * Gets all neighbors of the given cell.
	 * 
	 * @param cell
	 *            Origin cell.
	 * @param allWallsUp
	 *            Only gets neighbors with all walls up.
	 * @return An array of cells in this order: TOP, RIGHT, BOTTOM, LEFT.
	 */
	private Cell[] getNeighbors(Cell cell, boolean allWallsUp) {
		Cell[] result = new Cell[4];
		for (int i = 0; i < 4; i++) {
			result[i] = null;
		}
		int x = cell.getX();
		int y = cell.getY();
		if (allWallsUp) {
			// Top neighbor
			if (x > 0) {
				result[0] = data[x - 1][y].hasAllWallsUp() ? data[x - 1][y]
						: null;
			}
			// Right neighbor
			if (y < width - 1) {
				result[1] = data[x][y + 1].hasAllWallsUp() ? data[x][y + 1]
						: null;
			}
			// Bottom neighbor
			if (x < height - 1) {
				result[2] = data[x + 1][y].hasAllWallsUp() ? data[x + 1][y]
						: null;
			}
			// Left neighbor
			if (y > 0) {
				result[3] = data[x][y - 1].hasAllWallsUp() ? data[x][y - 1]
						: null;
			}
		} else {
			// Top neighbor
			if (x > 0) {
				result[0] = data[x - 1][y];
			}
			// Right neighbor
			if (y < width - 1) {
				result[1] = data[x][y + 1];
			}
			// Bottom neighbor
			if (x < height - 1) {
				result[2] = data[x + 1][y];
			}
			// Left neighbor
			if (y > 0) {
				result[3] = data[x][y - 1];
			}
		}
		return result;
	}

	/**
	 * Knocks out the walls between two cells.
	 * 
	 * @param from
	 *            First cell.
	 * @param to
	 *            Neighbor of the first cell.
	 */
	private void openPath(Cell from, Cell to) {
		if (from.getX() == to.getX()) {
			// Same row
			if (from.getY() + 1 == to.getY()) {
				// From -> To
				from.setRightWall(false);
				to.setLeftWall(false);
			} else {
				if (from.getY() - 1 == to.getY()) {
					// To <- From
					from.setLeftWall(false);
					to.setRightWall(false);
				}
			}
		} else {
			// Same column
			if (from.getX() + 1 == to.getX()) {
				// From above to
				from.setBottomWall(false);
				to.setTopWall(false);
			} else {
				if (from.getX() - 1 == to.getX()) {
					// To above from
					from.setTopWall(false);
					to.setBottomWall(false);
				}
			}
		}
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Cell getCell(int row, int column) {
		return data[row][column];
	}

	public static void main(String[] args) {
		Maze m = new Maze(75, 150);
		MazeGraphics mg = new MazeGraphics(m);
		Frame f = mg.getMapFrame("Generated Map");
		f.setVisible(true);
	}

}
