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
		
		Map<GraphNode, Integer> m1 = new HashMap<GraphNode, Integer>();
		for(GraphNode node : nodes) {
			m1.put(node, 0);
		}
		for(GraphEdge edge : edges) {
			GraphNode from = edge.getFrom();
			GraphNode to = edge.getTo();
			m1.replace(from, m1.get(from)+1);
			m1.replace(to, m1.get(to)+1);
		}
		
		Map<Integer, List<GraphNode>> m2 = new HashMap<Integer, List<GraphNode>>();
		for(GraphNode key : m1.keySet()) {
			Integer val = m1.get(key);
			if(m2.containsKey(val) == false) {
				m2.put(val, new ArrayList<GraphNode>());
			}
			m2.get(val).add(key);
		}
		
		List<Integer> keys = new ArrayList<Integer>(m2.keySet());
		Collections.sort(keys);
		for(Integer key : keys) {
			for(GraphNode node : m2.get(key)) {
				node.draw(g);
			}
		}
		
		for(GraphEdge edge : edges) {
			edge.draw(g);
		}
	}
	
	public void addNode(String item, double tcci, String type) {
		GraphNode node = new GraphNode(item, tcci, type);
		nodes.add(node);
		map.put(item, node);
	}
	
	public void addNode(String item, double tcci) {
		this.addNode(item, tcci, null);
	}
	
	public void addEdge(String item, double weight) {
		String[] tokens = item.split("->");
		GraphNode from = map.get(tokens[0]);
		GraphNode to = map.get(tokens[1]);
		GraphEdge edge = new GraphEdge(from, to, weight);
		edges.add(edge);
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


