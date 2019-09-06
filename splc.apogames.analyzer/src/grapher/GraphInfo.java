package grapher;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphInfo {
	private Map<String, GraphNode> nodeInfo = new HashMap<String, GraphNode>();
	private List<GraphEdge> edgeInfo = new ArrayList<GraphEdge>();
	
	public List<GraphNode> getNodeInfo() {
		return new ArrayList<GraphNode>(nodeInfo.values());
	}
	
	public List<GraphEdge> getEdgeInfo() {
		return edgeInfo;
	}

	public void addNode(String string) {
		GraphNode node = new GraphNode();
		node.addText("#GAME", 20, Font.PLAIN);
		nodeInfo.put(string, node);
	}
	
	public void addNormalNode(String stereoType, String name, double value) {
		GraphNode node = new GraphNode();
		addNode(node, stereoType, name, value);
	}

	public void addBoundaryNode(int type, String stereoType, String name, double value) {
		GraphNode node = new GraphBoundaryNode(type);
		addNode(node, stereoType, name, value);
	}
	
	private void addNode(GraphNode node, String stereoType, String name, double value) {
		node.addText(name, 12, Font.PLAIN);
		node.addLine();
		String strValue = String.format("%.2f", value);
		node.addText(strValue, 12, Font.BOLD);
		node.setValue(value);
		nodeInfo.put(name, node);
	}
	
	public void addEdge(String from, String to, double weight) {
		GraphNode fromNode = nodeInfo.get(from);
		GraphNode toNode = nodeInfo.get(to);
		GraphEdge edge = new GraphEdge(fromNode, toNode, "full", "opened", weight);
		edgeInfo.add(edge);
	}
}
