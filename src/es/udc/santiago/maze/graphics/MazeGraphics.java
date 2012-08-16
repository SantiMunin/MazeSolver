package es.udc.santiago.maze.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

import es.udc.santiago.maze.Cell;
import es.udc.santiago.maze.Maze;
import es.udc.santiago.maze.walker.Path;

public class MazeGraphics {
	private static int SIZE = 8;
	private static int MARGIN = 5;
	private static int FRAME_MARGIN = 30;
	private Maze m;
	private MazeCanvas mc;

	public MazeGraphics(Maze m) {
		this.m = m;
	}
	/**
	 * Generates a frame which shows the maze.
	 * @param title Window title.
	 * @return Frame.
	 */
	public Frame getMapFrame(String title) {
		ScrollPane sp = new ScrollPane();
		mc = new MazeCanvas(m);
		sp.add(mc);
		Frame f = new Frame(title);
		f.setSize(MazeGraphics.SIZE * m.getWidth() + MazeGraphics.MARGIN * 2
				+ FRAME_MARGIN, MazeGraphics.SIZE * m.getHeight()
				+ MazeGraphics.MARGIN * 2 + FRAME_MARGIN);
		f.add(sp);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		return f;
	}

	/**
	 * Adds a path to be painted.
	 * 
	 * @param path
	 */
	public void addPath(Entry<Color, Path> path) {
		mc.addPath(path);
		mc.repaint();
	}

	private class MazeCanvas extends Canvas {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8025702657222278281L;

		Maze maze;
		Queue<Entry<Color, Path>> pathsToDraw;

		public MazeCanvas(Maze maze) {
			this.maze = maze;
			pathsToDraw = new LinkedList<Entry<Color, Path>>();
		}
		public void paint(Graphics g) {

			Point start = maze.getStart();
			Point end = maze.getEnd();
			for (int x = 0; x < maze.getWidth(); x++) {
				for (int y = 0; y < maze.getHeight(); y++) {
					if (start.equals(new Point(x, y))) {
						drawPoint(x, y, Color.BLUE, g);
					}
					if (end.equals(new Point(x, y))) {
						drawPoint(x, y, Color.RED, g);
					}
					Cell cell = maze.getCell(y, x);
					if (cell.hasTopWall()) {
						g.drawLine(MARGIN + x * SIZE, MARGIN + y * SIZE, MARGIN
								+ x * SIZE + SIZE, MARGIN + y * SIZE);
					}

					if (cell.hasBottomWall()) {
						g.drawLine(MARGIN + x * SIZE, MARGIN + y * SIZE + SIZE,
								MARGIN + x * SIZE + SIZE, MARGIN + y * SIZE
										+ SIZE);
					}

					if (cell.hasLeftWall()) {
						g.drawLine(MARGIN + x * SIZE, MARGIN + y * SIZE, MARGIN
								+ x * SIZE, MARGIN + y * SIZE + SIZE);
					}

					if (cell.hasRightWall()) {
						g.drawLine(MARGIN + x * SIZE + SIZE, MARGIN + y * SIZE,
								MARGIN + x * SIZE + SIZE, MARGIN + y * SIZE
										+ SIZE);
					}
				}
			}
			for (Iterator<Entry<Color, Path>> it = pathsToDraw.iterator(); it
					.hasNext();) {
				Entry<Color, Path> entry = it.next();
				drawPath(entry.getKey(), entry.getValue(), g);
			}
		}

		/**
		 * Adds a path to be painted when the repaint() method is called.
		 * 
		 * @param path
		 */
		public void addPath(Entry<Color, Path> path) {
			this.pathsToDraw.add(path);
		}
	}

	/**
	 * Draws a square point in the map.
	 * 
	 * @param x
	 *            X coord.
	 * @param y
	 *            Y coord.
	 * @param c
	 *            Color.
	 * @param g
	 *            Graphics instance.
	 */
	private void drawPoint(int x, int y, Color c, Graphics g) {
		g.setColor(c);
		g.fillRect(x * SIZE + MARGIN + 2, y * SIZE + MARGIN + 2, SIZE - 3,
				SIZE - 3);
		g.setColor(Color.BLACK);
	}

	/**
	 * Draws a path.
	 * 
	 * @param color
	 *            path's color.
	 * @param path
	 *            Path data.
	 * @param g
	 *            Graphics instance.
	 */
	private void drawPath(Color color, Path path, Graphics g) {
		for (Iterator<Point> iterator = path.getPoints().iterator(); iterator
				.hasNext();) {
			Point p = iterator.next();
			if (!p.equals(m.getStart()) && !p.equals(m.getEnd())) {
				drawPoint(p.x, p.y, color, g);
			}
		}
	}
}
