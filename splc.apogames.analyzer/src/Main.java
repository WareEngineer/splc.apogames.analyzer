
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import grapher.GraphEdge;
import grapher.GraphNode;
import grapher.Grapher;
import model.ClassModel;
import model.Game;
import model.MethodModel;
import model.OverlapedArchitecture;
import model.OverlapedClass;
import parser.Parser;

public class Main {
	static String commonPath = "C:/Users/user/eclipse-workspace/Java/";

	public static void main(String[] args) {
		Parser parser = new Parser();
		Map<String, Game> games = new HashMap<String, Game>();

//* 
//		List<String> gameTitles = Arrays.asList("ApoBot");
		List<String> gameTitles = getGameTitles(commonPath);
		for (String gameTitle : gameTitles) {
			String path = commonPath + gameTitle; 
			Game game = new Game( gameTitle, parser.parse(path) );
			games.put(gameTitle, game);
		}

		for(Game game : games.values()) {
			System.out.println(game.toString());
//			game.print();
		}
		
		OverlapedArchitecture overlapedArchitecture = new OverlapedArchitecture(games);
		overlapedArchitecture.printReuseFrequency();
		overlapedArchitecture.printClasses();
		overlapedArchitecture.printMatrix();
		overlapedArchitecture.printTcci();
//		overlapedArchitecture.printSimilarity();
//*/	
		Grapher grapher = new Grapher();
		Map<String, GraphNode> nodes = getNodes(overlapedArchitecture);
		grapher.setNodes(nodes);
		System.out.println(nodes.size());
		List<GraphEdge> edges = getEdges(overlapedArchitecture, nodes);
		grapher.setEdges(edges);
		grapher.draw();
	}
	
	private static Map<String, GraphNode> getNodes(OverlapedArchitecture overlapedArchitecture) {
		Map<String, OverlapedClass> classes = overlapedArchitecture.getClasses();
		Map<String, Double> tccis = overlapedArchitecture.getTccis();
		
		Map<String, GraphNode> nodes = new HashMap<String, GraphNode>();
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
		
		return nodes;
	}
	
	private static List<GraphEdge> getEdges(OverlapedArchitecture overlapedArchitecture, Map<String, GraphNode> nodes) {
		Map<String, OverlapedClass> classes = overlapedArchitecture.getClasses();
		Map<String, Set<String>> callRelations = overlapedArchitecture.getCallRelations();
		
		List<GraphEdge> edges = new ArrayList<GraphEdge>();
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
		
		return edges;
	}
	
	private static List<String> getGameTitles(String path) {
		List<String> gameTitles = new ArrayList<String>();
		
		try {
			gameTitles =  Files.walk(Paths.get(commonPath), 1)
						   	   .filter(Files::isDirectory)
						   	   .skip(1)		// 루트 디렉토리(Java) 제외
						   	   .map(file -> file.getFileName().toString())
						   	   .filter(name -> !name.contains("(X)"))
						   	   .collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return gameTitles;
	}
}
