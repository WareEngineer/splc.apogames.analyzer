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

	public void addNodeInfo(String string) {
		GraphNode node = new GraphNode();
		node.addText("#GAME", 20, Font.PLAIN);
		nodeInfo.put(string, node);
	}
	
	public void addNodeInfo(String stereoType, String name, double value) {
		String strValue = String.format("%.2f", value);
		
		GraphNode node = new GraphNode();
		
		node.addText("¡ì"+stereoType+"¡í", 12, Font.ITALIC);
		node.addText(name, 12, Font.PLAIN);
		node.addLine();
		node.addText(strValue, 12, Font.BOLD);
		
		node.setValue(value);
		
		nodeInfo.put(name, node);
	}
	
	public void addEdgeInfo(String from, String to, double weight) {
		GraphNode fromNode = nodeInfo.get(from);
		GraphNode toNode = nodeInfo.get(to);
		GraphEdge edge = new GraphEdge(fromNode, toNode, "full", "opened", weight);
		edgeInfo.add(edge);
	}
}
