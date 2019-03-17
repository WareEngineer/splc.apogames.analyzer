package grapher;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class GraphEdge {
	private GraphNode from;
	private GraphNode to;
	private String type;
	private int weight;
	private boolean isFirstDraw;
	
	public GraphEdge(GraphNode from, GraphNode to, double weight) {
		this.from = from;
		this.to = to;
		this.type = "call";
		this.weight = (int) (weight*100);
	}
	
	public void draw(Graphics g) {
		isFirstDraw = true;
		double gradient = ((float)to.getY()-from.getY())/(to.getX()-from.getX());
		double gFrom = ((float)from.getHeight()/from.getWidth());
		double gTo = ((float)to.getHeight()/to.getWidth());
		
		double absGradient = Math.abs(gradient);
		double absGfrom = Math.abs(gFrom);
		double absGto = Math.abs(gTo);
		
		int dxFrom = (int) ((from.getWidth() - from.getHeight()/absGradient)/2);
		int dyFrom = (int) ((from.getHeight() - from.getWidth()*absGradient)/2);
		int dxTo = (int) ((to.getWidth() - to.getHeight()/absGradient)/2);
		int dyTo = (int) ((to.getHeight() - to.getWidth()*absGradient)/2);
		
		int x1, x2, y1, y2;
//		if(from.getX() < to.getX()) {
//			// from -> to
//			x1 = from.getX() + from.getWidth();
//			x2 = to.getX();
//		} else {
//			// from <- to
//			x1 = from.getX();
//			x2 = to.getX() + to.getWidth();
//		}
//		
//		if(from.getY() < to.getY()) {
//			// from v to
//			y1 = from.getY() + from.getHeight();
//			y2 = to.getY();
//		} else {
//			// from ^ to
//			y1 = from.getY();
//			y2 = to.getY() + to.getHeight();
//		}
		if(from.getX() < to.getX()) {
			if(from.getY() < to.getY()) {
				if(absGfrom < absGradient) {
					x1 = from.getX() + from.getWidth() - dxFrom;
					y1 = from.getY() + to.getHeight();
				} else {
					x1 = from.getX() + from.getWidth();
					y1 = from.getY() + to.getHeight() - dyFrom;
				}
				
				if(absGto < absGradient) {
					x2 = to.getX() + dxTo;
					y2 = to.getY();
				} else {
					x2 = to.getX();
					y2 = to.getY() + dyTo;
				}
			} else {
				if(absGfrom < absGradient) {
					x1 = from.getX() + from.getWidth() - dxFrom;
					y1 = from.getY();
				} else {
					x1 = from.getX() + from.getWidth();
					y1 = from.getY() + dyFrom;
				}
				
				if(absGto < absGradient) {
					x2 = to.getX() + dxTo;
					y2 = to.getY() + to.getHeight();
				} else {
					x2 = to.getX();
					y2 = to.getY() + to.getHeight() - dyTo;
				}
			}
		} else {
			if(from.getY() < to.getY()) {
				if(absGfrom < absGradient) {
					x1 = from.getX() + dxFrom;
					y1 = from.getY() + from.getHeight();
				} else {
					x1 = from.getX();
					y1 = from.getY() + from.getHeight() - dyFrom;
				}
				
				if(absGto < absGradient) {
					x2 = to.getX() + to.getWidth() - dxTo;
					y2 = to.getY();
				} else {
					x2 = to.getX() + to.getWidth();
					y2 = to.getY() + dyTo;
				}
			} else {
				if(absGfrom < absGradient) {
					x1 = from.getX() + dxFrom;
					y1 = from.getY();
				} else {
					x1 = from.getX();
					y1 = from.getY() + dyFrom;
				}
				
				if(absGto < absGradient) {
					x2 = to.getX() + to.getWidth() - dxTo;
					y2 = to.getY() + to.getHeight();
				} else {
					x2 = to.getX() + to.getWidth();
					y2 = to.getY() + to.getHeight() - dyTo;
				}
			}
		}

		int thickness = this.getThickness();
	    int half;
	    int n;
	    if(thickness%2 == 1) {
	    	half = thickness/2 + 1;
	    	n = 0;
	    } else {
	    	half = thickness/2;
	    	n = 1;
	    }
	    
	    for(int i=1; i<=thickness; i++) {
	    	if(i <= half) {
	    		for(int j=1; j<=thickness; j++) {
	    			if(half-i < j && j < half+i+n) {
	    				drawArrow(g, x1+(i-half), y1+(j-half), x2+(i-half), y2+(j-half));
	    			}
	    		}
	    	} else {
	    		for(int j=1; j<=thickness; j++) {
	    			if(i-(half+n) < j && j <= thickness-(i-half-n)) {
	    				drawArrow(g, x1+(i-half), y1+(j-half), x2+(i-half), y2+(j-half));
	    			}
	    		}
	    	}
	    }
	}

	private void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
	    int txtX = (x1+x2)/2;
	    int txtY = (y1+y2)/2;
	    
		if(from == to) {
			int dx = from.getWidth()/6;
			int dy = from.getHeight()/2;
			int[] xpoints = new int[] {x1, 	 x1-dx, x1-dx, x1+dx, x1+dx};
			int[] ypoints = new int[] {y1+dy, y1+dy, y1-dy, y1-dy, y1};
			g.drawPolyline(xpoints, ypoints, 5);
			drawArrowHead(g, xpoints[3], ypoints[3], xpoints[4], ypoints[4]);
			txtX = x1-dx/2;
			txtY = y1-dy/2;
		} else {
		    g.drawLine(x1, y1, x2, y2);
			drawArrowHead(g, x1, y1, x2, y2);
		    txtX = (x1+x2)/2;
		    txtY = (y1+y2)/2;
		}
	    
		if(isFirstDraw) {
		    g.setColor(this.getColor());
		    g.drawString(String.format("%d%s", weight,"%"), txtX, txtY);
		    g.setColor(Color.BLACK);
		    isFirstDraw = false;
		}
	}

	private void drawArrowHead(Graphics g, int x1, int y1, int x2, int y2) {
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
	    
	    switch(type) {
	    case "call" : 
	    	xpoints = new int[] {(int) xm, x2, (int) xn};
	    	ypoints = new int[] {(int) ym, y2, (int) yn};
	    	g.drawPolyline(xpoints, ypoints, 3);
	    	break;
	    case "extend" :
	    	xpoints = new int[] {x2, (int) xm, (int) xn, x2};
		    ypoints = new int[] {y2, (int) ym, (int) yn, y2};
		    g.drawPolyline(xpoints, ypoints, 4);
		    break;
	    case "implement" :
	    	xpoints = new int[] {x2, (int) xm, (int) xn};
		    ypoints = new int[] {y2, (int) ym, (int) yn};
		    g.fillPolygon(xpoints, ypoints, 3);
		    break;
	    }
	}
	
	private int getThickness() {
		int step5 = weight/20;
		if(step5 < 2) {
			return 1;
		}
		return step5/2;
	}

	private Color getColor() {
	    if(0<=weight || weight <= 100) {
	    	if(weight < 50) {
	    		return Color.GRAY;
	    	} else if (weight < 70) {
	    		return Color.BLUE;
	    	} else if (weight < 80) {
	    		return Color.ORANGE;
	    	} else {
	    		return Color.RED;
	    	}
	    }
	    return Color.BLACK;
	}

	public GraphNode getFrom() {
		return from;
	}
	
	public GraphNode getTo
	
	() {
		return to;
	}
}
