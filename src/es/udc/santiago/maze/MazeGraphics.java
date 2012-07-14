package es.udc.santiago.maze;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MazeGraphics {
	private static int SIZE = 8;
	private static int MARGIN = 5;
	private static int FRAME_MARGIN = 30;

	private Maze m;

	MazeGraphics(Maze m) {
		this.m = m;
	}

	public Frame getMapFrame(String title) {
		ScrollPane sp = new ScrollPane();
		sp.add(new MazeCanvas(m));
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

	private class MazeCanvas extends Canvas {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8025702657222278281L;

		Maze maze;

		public MazeCanvas(Maze maze) {
			this.maze = maze;
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
		}

		private void drawPoint(int x, int y, Color c, Graphics g) {
			System.out.println("Drawing point (" + x + ", " + y + ")");
			g.setColor(c);
			g.fillRect(x*SIZE+MARGIN+2, y*SIZE+MARGIN+2, SIZE-3, SIZE-3);
			g.setColor(Color.BLACK);
		}
	}

}
