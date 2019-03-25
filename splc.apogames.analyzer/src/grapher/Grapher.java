package grapher;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grapher extends JFrame implements MouseWheelListener, MouseListener{
	private Map<String, GraphNode> map = new HashMap<String, GraphNode>();
	private List<GraphNode> nodes = new ArrayList<GraphNode>();
	private List<GraphEdge> edges = new ArrayList<GraphEdge>();
	private List<GraphNode> pressed = new ArrayList<GraphNode>();
	private double minNodeVal = 0.0;
	private double maxNodeVal = 1.0;
	private double minEdgeWgt = 0.0;
	private double maxEdgeWgt = 1.0;
	private Point startPoint;
	private int x;
	private int y;
	
	public Grapher() {
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		
		this.x = this.getX();
		this.y = this.getY();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.translate(x, y);
		
		List<GraphNode> invisibleNodes = new ArrayList<GraphNode>();
		
		for(GraphNode node : nodes) {
			double nodeValue = node.getValue();
			if( minNodeVal <= nodeValue && nodeValue <= maxNodeVal ) {
				node.draw(g);
			} else {
				invisibleNodes.add(node);
			}
		}
		
		for(GraphEdge edge : edges) {
			double edgeWeight = edge.getWeight();
			if( minEdgeWgt <= edgeWeight && edgeWeight <= maxEdgeWgt ) {
				boolean visible = true;
				for(GraphNode node : invisibleNodes) {
					if(edge.contains(node)) {
						visible=false;
						break;
					}
				}
				if(visible) {
					edge.draw(g);
				}
			}
		}
	}
	
	public void setNodes(Map<String, GraphNode> nodes) {
		this.map = nodes;
		for(GraphNode node : nodes.values()) {
			this.nodes.add(node);
		}
		this.repaint();
	}
	
	public void setEdges(List<GraphEdge> edges) {
		this.edges = edges;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//Zoom in
	    if (e.getWheelRotation() < 0) {
	    	for(GraphNode node : nodes) {
	    		node.zoomIn();
	    	}
	        repaint();
	    }
	    //Zoom out
	    if (e.getWheelRotation() > 0) {
	    	for(GraphNode node : nodes) {
	    		node.zoomOut();
	    	}
	        repaint();
	    }
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = e.getPoint();
		for(GraphNode node : nodes) {
			if(node.isMouseOn(e.getX(), e.getY())) {
				pressed.add(node);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int dx = (int) (e.getX() - startPoint.getX());
		int dy = (int) (e.getY() - startPoint.getY());
		if(pressed.isEmpty()) {
			if(dx==0 && dy==0) return;
			for(GraphNode node : nodes) {
				node.move(dx, dy);
			}
			repaint();
		} else {
			for(GraphNode node : pressed) {
				node.move(dx, dy);
			}
			pressed.clear();
			repaint();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void draw() {
		this.repaint();
	}
}


