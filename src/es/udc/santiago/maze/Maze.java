package es.udc.santiago.maze;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import es.udc.santiago.maze.walker.Path;

/**
 * The main class of the application. It represents all cells of the maze and
 * provides useful methods.
 * 
 * @author Santiago Munín González
 * 
 */
public class Maze implements Serializable {

	private static final long serialVersionUID = -3629143399901626658L;
	private static float MINIMUM_DISTANCE_DIVISOR = 3f;
	private Cell[][] data;
	private int width;
	private int height;
	private Point start;
	private Point end;

	/**
	 * Generates a random maze
	 * 
	 * @param height
	 *            Height
	 * @param width
	 *            Width
	 * @param randomStartEnd
	 *            points are generated randomly if <i>true</i>.
	 */
	public Maze(int height, int width, boolean randomStartEnd) {
		this.width = width;
		this.height = height;
		this.generate();
	}

	/**
	 * Creates a maze from a table of cells.
	 * 
	 * @param cells
	 *            Maze data.
	 */
	public Maze(Cell[][] cells, Point start, Point end) {
		this.height = cells.length;
		this.width = cells[0].length;
		this.data = cells;
		this.start = start;
		this.end = end;
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
				// data[height..0][0..width]
				data[i][j] = new Cell(i, j);
			}
		}

		// Picks start and end
		Random randomGenerator = new Random();
		this.start = new Point(randomGenerator.nextInt(width),
				randomGenerator.nextInt(height));
		this.end = new Point(randomGenerator.nextInt(width),
				randomGenerator.nextInt(height));
		double distanceTopLeft_RightDown = Math.sqrt(Math.pow(width, 2)
				+ Math.pow(height, 2));
		while (start.distance(end) < (distanceTopLeft_RightDown / MINIMUM_DISTANCE_DIVISOR)) {
			this.start = new Point(randomGenerator.nextInt(width),
					randomGenerator.nextInt(height));
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

	/**
	 * Determines if two cells are connected.
	 * 
	 * @param from
	 *            From cell
	 * @param to
	 *            To cell (must be neighbor of <i>from</i> cell).
	 * @return true if it's possible.
	 */
	public boolean canWalk(Point from, Point to) {
		int fromx = from.x;
		int fromy = from.y;
		int tox = to.x;
		int toy = to.y;
		if ((fromx < 0 || fromx >= width) || (fromy < 0 || fromy >= height)
				|| (toy < 0 || toy >= height) || (tox < 0 || tox >= width)) {
			return false;
		}
		Cell fromCell = data[fromy][fromx];
		Cell toCell = data[toy][tox];
		if (fromy == toy) {
			// Same row
			if (fromx + 1 == tox) {
				// From -> To
				return (!fromCell.hasRightWall() && !toCell.hasLeftWall());
			} else {
				if (fromx - 1 == tox) {
					// To <- From
					return (!fromCell.hasLeftWall() && !toCell.hasRightWall());
				}
			}
		} else {
			// Same column
			if (fromy + 1 == toy) {
				// From above to
				return (!fromCell.hasBottomWall() && !toCell.hasTopWall());
			} else {
				if (fromy - 1 == toy) {
					// To above from
					return (!fromCell.hasTopWall() && !toCell.hasBottomWall());
				}
			}
		}
		return false;
	}

	/**
	 * Finds out possible directions from a cell.
	 * 
	 * @param point
	 *            Coordinates of the cell.
	 * @param incomingDirection
	 *            Incoming direction (it will be excluded)
	 * @return A byte which contains information of all directions.
	 */
	public byte findPossibleDirections(Point point, byte incomingDirection) {
		int result = 15;
		if (!(this.canWalk(point, new Point(point.x, point.y - 1))
				&& incomingDirection != Path.DOWN)) {
			result = result & ~Path.UP;
		}
		if (!(this.canWalk(point, new Point(point.x + 1, point.y))
				&& incomingDirection != Path.LEFT)) {
			result = result & ~Path.RIGHT;
		}
		if (!(this.canWalk(point, new Point(point.x, point.y + 1))
				&& incomingDirection != Path.UP)) {
			result = result & ~Path.DOWN;
		}
		if (!(this.canWalk(point, new Point(point.x - 1, point.y))
				&& incomingDirection != Path.RIGHT)) {
			result = result & ~Path.LEFT;
		}
		return (byte) result;
	}

	/**
	 * Finds out possible directions from a cell.
	 * 
	 * @param point
	 *            Coordinates of the cell.
	 * @return A byte which contains information of all directions.
	 */
	public byte findPossibleDirections(Point point) {
 		return this.findPossibleDirections(point, Path.NO_DIRECTION);
	}
}
