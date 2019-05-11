package grapher;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Grapher extends JFrame implements MouseWheelListener, MouseListener{
	private JFrame frame;
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
	private JTextField txtNodeRight;
	private JTextField txtNodeLeft;
	private JTextField txtEdgeRight;
	private JTextField txtEdgeLeft;
	
	public Grapher() {
		this.frame = this;
		this.setSize(500, 500);
		getContentPane().setLayout(null);
		this.drawFilter();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		
		this.x = this.getX();
		this.y = this.getY();
	}
	
	public void setGraph(GraphInfo graphInfo) {
		this.nodes = graphInfo.getNodeInfo();
		this.edges = graphInfo.getEdgeInfo();
		this.repaint();
	}

	public void drawFilter() {
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 162, 118);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblFliter = new JLabel("Fliter");
		lblFliter.setBackground(Color.WHITE);
		lblFliter.setHorizontalAlignment(SwingConstants.CENTER);
		lblFliter.setBounds(35, 10, 84, 15);
		panel.add(lblFliter);
		
		JLabel lblNode = new JLabel("\u2264 Node \u2264 ");
		lblNode.setHorizontalAlignment(SwingConstants.CENTER);
		lblNode.setBounds(43, 35, 76, 15);
		panel.add(lblNode);

		txtNodeLeft = new JTextField();
		txtNodeLeft.setBounds(12, 32, 35, 21);
		txtNodeLeft.setColumns(10);
		txtNodeLeft.setText("0.0");
		panel.add(txtNodeLeft);
		
		txtNodeRight = new JTextField();
		txtNodeRight.setBounds(115, 32, 35, 21);
		txtNodeRight.setColumns(10);
		txtNodeRight.setText("1.0");
		panel.add(txtNodeRight);
		
		JLabel lblEdge = new JLabel("\u2264 Edge \u2264 ");
		lblEdge.setHorizontalAlignment(SwingConstants.CENTER);
		lblEdge.setBounds(43, 63, 76, 15);
		panel.add(lblEdge);
		
		txtEdgeLeft = new JTextField();
		txtEdgeLeft.setBounds(12, 60, 35, 21);
		txtEdgeLeft.setColumns(10);
		txtEdgeLeft.setText("0.0");
		panel.add(txtEdgeLeft);

		txtEdgeRight = new JTextField();
		txtEdgeRight.setBounds(115, 60, 35, 21);
		txtEdgeRight.setColumns(10);
		txtEdgeRight.setText("1.0");
		panel.add(txtEdgeRight);
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				minNodeVal = Double.valueOf(txtNodeLeft.getText());
				maxNodeVal = Double.valueOf(txtNodeRight.getText());
				minEdgeWgt = Double.valueOf(txtEdgeLeft.getText());
				maxEdgeWgt = Double.valueOf(txtEdgeRight.getText());
				frame.repaint();
			}
		});
		btnApply.setFont(new Font("±¼¸²", Font.PLAIN, 12));
		btnApply.setBounds(43, 88, 76, 23);
		panel.add(btnApply);
		
		JButton background = new JButton("");
		background.setEnabled(false);
		background.setBounds(0, 0, 162, 118);
		panel.add(background);
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


