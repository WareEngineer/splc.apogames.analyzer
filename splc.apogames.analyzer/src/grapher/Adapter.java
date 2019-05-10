package grapher;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import grapher.GraphEdge;
import grapher.GraphNode;
import model.OverlapedArchitecture;
import model.OverlapedClass;

public class Adapter {
	private OverlapedArchitecture overlapedArchitecture;
	private Map<String, GraphNode> nodes = new HashMap<String, GraphNode>();
	private List<GraphEdge> edges = new ArrayList<GraphEdge>();
	
	public Adapter(OverlapedArchitecture overlapedArchitecture) {
		this.overlapedArchitecture = overlapedArchitecture;
		setNodes();
		setEdges();
	}
	
	private void setNodes() {
		Map<String, OverlapedClass> classes = overlapedArchitecture.getClassInfo();
		Map<String, Double> tccis = overlapedArchitecture.getTcciInfo();
		
		GraphNode node = new GraphNode();
		node.addText("#GAME", 20, Font.PLAIN);
		nodes.put("#GAME", node);
		for(String cName : classes.keySet()) {
			node = new GraphNode();
			
			OverlapedClass c = classes.get(cName);
			String stereoType = c.getType();
			node.addText(stereoType, 12, Font.ITALIC);
			node.addText(cName, 12, Font.PLAIN);
			String tcci = String.format("%.2f", tccis.get(cName));
			node.addLine();
			node.addText(tcci, 12, Font.BOLD);
			
			node.setValue(tccis.get(cName));
			
			nodes.put(cName, node);
		}
	}
	
	private void setEdges() {
		Map<String, OverlapedClass> classes = overlapedArchitecture.getClassInfo();
		Map<String, Set<String>> callRelations = overlapedArchitecture.getCallRelations();
		
		for(String relation : callRelations.keySet()) {
			String[] tokens = relation.split("->");
			GraphNode from = nodes.get(tokens[0]);
			GraphNode to = nodes.get(tokens[1]);
			
			double weight;
			if(tokens[0].equals("#GAME")) {
				weight = 1.0;
				continue;
			} else {
				weight = 1.0*callRelations.get(relation).size()/classes.get(tokens[0]).getTitles().size();
			}
			GraphEdge edge = new GraphEdge(from, to, "full", "opened", weight);
			edges.add(edge);
		}
	}
	
	public Map<String, GraphNode> getNodes() {
		return nodes;
	}
	
	public List<GraphEdge> getEdges() {
		return edges;
	}
}
