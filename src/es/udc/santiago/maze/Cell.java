package es.udc.santiago.maze;

/**
 * Part of a Maze.
 * 
 * @author Santiago Munín González
 * 
 */
public class Cell {

	private int x;
	private int y;
	private boolean topWall;
	private boolean rightWall;
	private boolean bottomWall;
	private boolean leftWall;

	/**
	 * Instances a new cell in coordinates (x, y) with all walls up.
	 * 
	 * @param x
	 *            X coord.
	 * @param y
	 *            Y coord.
	 */
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
		this.topWall = true;
		this.rightWall = true;
		this.bottomWall = true;
		this.leftWall = true;
	}

	/**
	 * Instances a new cell in coordinates (x, y) with the specified walls up.
	 * 
	 * @param x
	 *            X coord.
	 * @param y
	 *            Y coord.
	 * @param topWall
	 *            Top wall.
	 * @param rightWall
	 *            Right wall.
	 * @param bottomWall
	 *            Bottom wall.
	 * @param leftWall
	 *            Left wall.
	 */
	public Cell(int x, int y, boolean topWall, boolean rightWall,
			boolean bottomWall, boolean leftWall) {
		this.x = x;
		this.y = y;
		this.topWall = topWall;
		this.rightWall = rightWall;
		this.bottomWall = bottomWall;
		this.leftWall = leftWall;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean hasTopWall() {
		return topWall;
	}

	public void setTopWall(boolean upWall) {
		this.topWall = upWall;
	}

	public boolean hasRightWall() {
		return rightWall;
	}

	public void setRightWall(boolean rightWall) {
		this.rightWall = rightWall;
	}

	public boolean hasBottomWall() {
		return bottomWall;
	}

	public void setBottomWall(boolean bottomWall) {
		this.bottomWall = bottomWall;
	}

	public boolean hasLeftWall() {
		return leftWall;
	}

	public void setLeftWall(boolean leftWall) {
		this.leftWall = leftWall;
	}

	/**
	 * Determines if the cell is surrounded by all walls.
	 * 
	 * @return boolean.
	 */
	public boolean hasAllWallsUp() {
		return this.topWall && this.rightWall && this.bottomWall
				&& this.leftWall;
	}

	/**
	 * Determines if the cell has any wall.
	 * 
	 * @return boolean.
	 */
	public boolean hasAnyWallUp() {
		return this.topWall || this.rightWall || this.bottomWall
				|| this.leftWall;
	}
}