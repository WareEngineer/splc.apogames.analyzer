package grapher;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GraphEdge {
	private GraphNode from;
	private GraphNode to;
	private String headType;
	private String bodyType;
	private double weight;
	private boolean isFirstDraw;
	
	public GraphEdge(GraphNode from, GraphNode to, String body, String head, double weight) {
		this.from = from;
		this.to = to;
		this.bodyType = body;
		this.headType = head;
		this.weight = weight;
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
		if(from.getX() < to.getX()) {
			if(from.getY() < to.getY()) {
				if(absGfrom < absGradient) {
					x1 = from.getX() + from.getWidth() - dxFrom;
					y1 = from.getY() + from.getHeight();
				} else {
					x1 = from.getX() + from.getWidth();
					y1 = from.getY() + from.getHeight() - dyFrom;
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
		Graphics2D g2 = (Graphics2D) g;
	    int txtX = (x1+x2)/2;
	    int txtY = (y1+y2)/2;
		
	    switch(bodyType) {
		case "dot" : 
			float dash[] = {5f,7f};
			g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash,0));
			break;
		case "full" : default :
			break;
		}
	    
		if(from == to) {
			int dx = from.getWidth()/6;
			int dy = from.getHeight()/3;
//			int[] xpoints = new int[] {x1, 	 x1-dx, x1-dx, x1+dx, x1+dx};
//			int[] ypoints = new int[] {y1+dy, y1+dy, y1-dy, y1-dy, y1};
			int w = from.getWidth();
			int h = from.getHeight();
			int[] xpoints = new int[] {x1+w-dx, x1+w-dx, 	x1+w+dx, x1+w+dx, x1+w};
			int[] ypoints = new int[] {y1, 		y1-dy-dy, 		y1-dy-dy, y1+dy, y1+dy};
			g.drawPolyline(xpoints, ypoints, 5);
			drawArrowHead(g, xpoints[3], ypoints[3], xpoints[4], ypoints[4]);
			txtX = x1+w;
			txtY = y1;
		} else {
			g.drawLine(x1, y1, x2, y2);
			drawArrowHead(g, x1, y1, x2, y2);
		    txtX = (x1+x2)/2;
		    txtY = (y1+y2)/2;
		}
	    
		if(isFirstDraw) {
		    g.setColor(this.getColor());
		    int percent = (int) (weight*100);
		    g.drawString(String.format("%d%s", percent,"%"), txtX, txtY);
		    g.setColor(Color.BLACK);
		    isFirstDraw = false;
		}
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
	    
	    switch(headType) {
	    case "closedEmpty" :
	    	xpoints = new int[] {x2, (int) xm, (int) xn, x2};
		    ypoints = new int[] {y2, (int) ym, (int) yn, y2};
		    g.drawPolyline(xpoints, ypoints, 4);
		    break;
	    case "closedFill" :
	    	xpoints = new int[] {x2, (int) xm, (int) xn};
		    ypoints = new int[] {y2, (int) ym, (int) yn};
		    g.fillPolygon(xpoints, ypoints, 3);
		    break;
	    case "opened" : default : 
	    	xpoints = new int[] {(int) xm, x2, (int) xn};
	    	ypoints = new int[] {(int) ym, y2, (int) yn};
	    	g.drawPolyline(xpoints, ypoints, 3);
	    	break;
	    }
	}
	
	private int getThickness() {
		return (int) Math.ceil(weight*3);
	}

	private Color getColor() {
	    if(0<=weight || weight <= 1.0) {
	    	if(weight < 0.5) {
	    		return Color.GRAY;
	    	} else if (weight < 0.7) {
	    		return Color.BLUE;
	    	} else if (weight < 0.8) {
	    		return Color.ORANGE;
	    	} else {
	    		return Color.RED;
	    	}
	    }
	    return Color.BLACK;
	}
	
	public double getWeight() {
		return weight;
	}

	public GraphNode getFrom() {
		return from;
	}
	
	public GraphNode getTo() {
		return to;
	}

	public boolean contains(GraphNode node) {
		if(from==node || to==node) {
			return true;
		}
		return false;
	}
}
