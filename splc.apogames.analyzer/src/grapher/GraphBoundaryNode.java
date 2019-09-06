package grapher;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GraphBoundaryNode extends GraphNode {
	public static int ENTRY = 0;
	public static int EXIT = 1;
	public static int BOTH = 2;
	private int type;
	
	public GraphBoundaryNode(int type) {
		super();
		this.type = type;
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		if(type == BOTH) {
			drawEntryArrow(g);
			drawExitArrow(g);
		} else if (type == ENTRY) {
			drawEntryArrow(g);
		} else if (type == EXIT) {
			drawExitArrow(g);
		}
	}
	
	private void drawEntryArrow(Graphics g) {
		int gap = 20;
		int x1 = super.getX()-gap;
		int y1 = super.getY()-gap;
		int x2 = super.getX();
		int y2 = super.getY();
		g.drawLine(x1, y1, x2, y2);
		drawArrowHead(g, x1, y1, x2, y2);
	}
	
	private void drawExitArrow(Graphics g) {
		int gap = 20;
		int x1 = super.getX()+super.getWidth();
		int y1 = super.getY()+super.getHeight();
		int x2 = super.getX()+super.getWidth()+gap;
		int y2 = super.getY()+super.getHeight()+gap;
		g.drawLine(x1, y1, x2, y2);
		drawArrowHead(g, x1, y1, x2, y2);
	}
	
	private void drawArrowHead(Graphics g, int x1, int y1, int x2, int y2) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke());
		
		int d = 6, h = 4;
		int dx = x2 - x1, dy = y2 - y1;
	    double D = Math.sqrt(dx*dx + dy*dy);
	    double xm = D - d, xn = xm, ym = h, yn = -h, x;
	    double sin = dy / D, cos = dx / D;

	    x = xm*cos - ym*sin + x1;
	    ym = xm*sin + ym*cos + y1;
	    xm = x;

	    x = xn*cos - yn*sin + x1;
	    yn = xn*sin + yn*cos + y1;
	    xn = x;
	    
	    int[] xpoints;
	    int[] ypoints;
	    
		xpoints = new int[] {x2, (int) xm, (int) xn};
	    ypoints = new int[] {y2, (int) ym, (int) yn};
	    g.fillPolygon(xpoints, ypoints, 3);
	}
}
