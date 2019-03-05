
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

import model.ClassModel;
import model.Game;
import model.MethodModel;
import model.OverlapedArchitecture;
import parser.Parser;

public class Main {
	static String commonPath = "C:/Users/user/eclipse-workspace/Java/";
	
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
	
	public static void main(String[] args) {
		Parser parser = new Parser();
		Map<String, Game> games = new HashMap<String, Game>();
		
//		List<String> gameTitles = Arrays.asList("ApoBot");
		List<String> gameTitles = getGameTitles(commonPath);
		for (String gameTitle : gameTitles) {
			String path = commonPath + gameTitle; 
			Game game = new Game( gameTitle, parser.parse(path) );
			games.put(gameTitle, game);
			System.out.println(game.toString());
		}

		OverlapedArchitecture overlapedArchitecture = new OverlapedArchitecture(games);
		System.out.println();
		overlapedArchitecture.printReuseFrequency();	// 얼마나 많은 앱(게임)에서 '.org'패키지의 각 클래스가 재사용되고 있는가?
		System.out.println();
		overlapedArchitecture.printClasses();
		
		System.out.println();
		printAdjacencyMatrix(games); 	// 통계적으로 각 클래스가 얼마나 많은 다른 클래스와 관계를 맺고 있는가?
		System.out.println();
		printModCTTI(games);
	}
	
	private static void printModCTTI(Map<String, Game> games) {
		Set<String> allClassNames = new HashSet<String>();
		List<String> allGraphPaths = new ArrayList<String>();
		
		for(Game game : games.values()) {
			allClassNames.addAll(game.getOrgClassNames());
			allGraphPaths.addAll(game.getGraphPaths());
		}
		
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		for(String graphPath : allGraphPaths) {
			List<String> nodes = Arrays.asList( graphPath.split("<-") );
			if(1 < nodes.size()) {
				String className = nodes.get(1);
				if(map.containsKey(className) == false) {
					map.put(className, new HashSet<String>());
				}
				map.get(className).add(graphPath);
			}
		}
		
		Map<String, Float> tccis = new HashMap<String, Float>();
		Map<String, Integer> frequencies = new HashMap<String, Integer>();
		for(String key : map.keySet()) {
			Set<String> graphPaths = map.get(key);
			Set<String> modGraphPaths = new HashSet<String>();
			for(String path : graphPaths) {
				String[] nodes = path.split("<-");
				switch(nodes.length) {
					case 1: modGraphPaths.add(nodes[0]); break;
					case 2: modGraphPaths.add(nodes[0]+"<-"+nodes[0]+"."+nodes[1]); break;
					case 3: modGraphPaths.add(nodes[0]+"<-"+nodes[0]+"."+nodes[1]+"<-"+nodes[2]); break;
				}
			}

			Set<String> products = new HashSet<String>();
			Set<String> endItems = new HashSet<String>();
			Set<String> edges = new HashSet<String>();
			Set<String> methods = new HashSet<String>();
			for(String path : modGraphPaths) {
				String[] nodes = path.split("<-");
				for(int i=0; i<nodes.length; i++) {
					if(i==0) {
						endItems.add(nodes[i]);
						products.add(nodes[0]);
					} else {
						edges.add(nodes[i-1]+"<-"+nodes[i]);
						if(i==(nodes.length-1)) {
							endItems.add(nodes[i]);
						}
						if(i==2) methods.add(nodes[i]);
					}
				}
			}
			
			int d = endItems.size();
			int sigmaPhi = edges.size();
			float tcci = 1 - (((float)d-1)/(sigmaPhi-1));
			
			tccis.put(key, tcci);
			frequencies.put(key, products.size());
			String format = String.format("%-45s [%2d] : %8f \t[d:%2d, sigmaPhi:%3d, complite-sigmaPhi:%3d]", key, products.size(), tcci, d, sigmaPhi, methods.size()*products.size());
			System.out.println(format);
		}
		
		System.out.println("\n\n");
		List<String> list = new ArrayList<String>(tccis.keySet()); 
		Collections.sort(list);
		for(int i=games.size(); i>0; i--) {
			for(String className : list) {
				float tcci = tccis.get(className);
				int frequency = frequencies.get(className);
				if(frequency == i) {
					System.out.println(String.format("%-45s [%2d] : %f", className, frequency, tcci));
				}
			}
		}
	}

	private static void printAdjacencyMatrix(Map<String, Game> games) {
		Map<String, Map<String, Integer>> adjacencyMatrix = new HashMap<String, Map<String, Integer>>();
		for (Game game : games.values()) {
			Map<String, List<String>> relations = game.getRelations();
			
			for (String from : relations.keySet()) {
				if ( !adjacencyMatrix.containsKey(from) ) {
					adjacencyMatrix.put(from, new HashMap<String, Integer>());
				}
				
				for (String to : relations.get(from)) {
					if ( !adjacencyMatrix.get(from).containsKey(to) ) {
						adjacencyMatrix.get(from).put(to, 0);
					}
					Integer frequency = adjacencyMatrix.get(from).get(to);
					adjacencyMatrix.get(from).replace(to, frequency+1);
				}
			}
		}
		
		List<String> list = new ArrayList<String>(adjacencyMatrix.keySet()); 
		Collections.sort(list);
		for (String from : list) {
			int begin = from.lastIndexOf('.');
			String pName = from.substring(0, begin+1);
			String cName = from.substring(begin+1);
			System.out.print( String.format("%20s  %30s", pName, cName));
			for (String to : list) {
				Integer frequency = adjacencyMatrix.get(from).get(to);
				if (frequency == null) {
					frequency = 0;
				}
				System.out.print( String.format("%3d", frequency) );
			}
			System.out.println();
		}
	}
}
