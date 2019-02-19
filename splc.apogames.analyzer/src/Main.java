
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

import parser.ClassModel;
import parser.Game;
import parser.MethodModel;
import parser.Parser;

public class Main {
	static String commonPath = "C:/Users/user/eclipse-workspace/Java/";
	
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

		System.out.println();
		printAdjacencyMatrix(games); 	// ��������� �� Ŭ������ �󸶳� ���� �ٸ� Ŭ������ ���踦 �ΰ� �ִ°�?
		System.out.println();
		printReuseFrequency(games);		// �󸶳� ���� ��(����)���� Ư�� Ŭ������ ����ǰ� �ִ°�?
		System.out.println();
		printMethodSimilarity(games);   // Ŭ���� ���� �޼ҵ��� ���絵�� ��Ѱ�? public boolean readLevel(boolean bURL, String fileName)
		System.out.println();
		printCTTI(games);
	}
	
	private static void printCTTI(Map<String, Game> games) {
		Map<String, Float> tccis = new HashMap<String, Float>();
		Map<String, Integer> frequencies = new HashMap<String, Integer>();
		Set<String> allClassNames = new HashSet<String>();
		List<String> allGraphPaths = new ArrayList<String>();
		List<String> gameTitles = getGameTitles(commonPath);
		
		for(Game game : games.values()) {
			allClassNames.addAll(game.getClassNames());
			allGraphPaths.addAll(game.getGraphPaths());
		}
		
		// TCCI = 1 - ((d-1)/((sigmaPhi)-1))
		for(String className : allClassNames) {
			Set<String> targetGraphPaths = new HashSet<String>();
			Set<String> paths = new HashSet<String>();
			Set<String> nodes = new HashSet<String>();
			Set<String> apps = new HashSet<String>();
			
			for(String graphPath : allGraphPaths) {
				if(graphPath.contains(className)) {
					targetGraphPaths.add(graphPath);
					List<String> list = Arrays.asList( graphPath.split("<-") );
					for(String node : list) {
					
						
					}
					nodes.addAll(list);
					apps.add(list.get(0));
				}
			}
			nodes.removeAll(gameTitles);

			int d = nodes.size();
			int sigmaPhi = 0;
			for(String graphPath : targetGraphPaths) {
				for(String node : nodes) {
					if(graphPath.contains(node)) {
						sigmaPhi++;
					}
				}
			}
			System.out.println(targetGraphPaths.toString());
			float tcci = 1 - (((float)d-1)/(sigmaPhi-1));
			tccis.put(className, tcci);
			frequencies.put(className, apps.size());
			System.out.println(apps.size() + " " + d + " " + sigmaPhi);
		}
		
		for(String className : tccis.keySet()) {
			float tcci = tccis.get(className);
			int frequency = frequencies.get(className);
//			System.out.println(String.format("%30s [%2d] : %f", className, frequency, tcci));
		}
	}

	private static List<String> getGameTitles(String path) {
		List<String> gameTitles = new ArrayList<String>();
		
		try {
			gameTitles =  Files.walk(Paths.get(commonPath), 1)
						   	   .filter(Files::isDirectory)
						   	   .skip(1)		// ��Ʈ ���丮(Java) ����
						   	   .map(file -> file.getFileName().toString())
						   	   .filter(name -> !name.contains("(X)"))
						   	   .collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return gameTitles;
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

	private static void printReuseFrequency(Map<String, Game> games) {
		Map<String, Integer> reuseFrequency = new HashMap<String, Integer>();
		
		for (Game game : games.values()) {
			for(ClassModel cm : game.getReusedClasses()) {
				String s = cm.getPath();
				if(reuseFrequency.containsKey(s)) {
					int newValue = reuseFrequency.get(s) + 1;
					reuseFrequency.replace(s, newValue);
				} else {
					reuseFrequency.put(s, 1);
				}
			}
		}
		
		List<String> list = new ArrayList<String>(reuseFrequency.keySet()); 
		Collections.sort(list);
		
		for(int i=0; i<reuseFrequency.size(); i++) {
			for(String key : list) {
				if(i == reuseFrequency.get(key)) {
					int pos = key.lastIndexOf('.');
					String pName = key.substring(0, pos);
					String cName = key.substring(pos+1);
					String s = String.format("%-20s %-30s %2d", pName, cName, i);
					System.out.println(s);
				}
			}
		}
	}
	
	private static void printMethodSimilarity(Map<String, Game> games) {
		Map<String, Map<String, Integer>> adjacencyMatrix = new HashMap<String, Map<String, Integer>>();
		
		for (Game game : games.values()) {
			for (ClassModel cm : game.getReusedClasses()) {
				String cName = cm.getPath();
				if ( !adjacencyMatrix.containsKey(cName) ) {
					adjacencyMatrix.put(cName, new HashMap<String, Integer>());
				}
				
				for (MethodModel mm : cm.getMethods()) {
//					String mHeader = mm.toString();
					String mHeader = mm.getSignature();
					if ( !adjacencyMatrix.get(cName).containsKey(mHeader) ) {
						adjacencyMatrix.get(cName).put(mHeader, 0);
					}
					Integer frequency = adjacencyMatrix.get(cName).get(mHeader);
					adjacencyMatrix.get(cName).replace(mHeader, frequency+1);
				}
			}
		}
		
		List<String> cList = new ArrayList<String>(adjacencyMatrix.keySet()); 
		Collections.sort(cList);
		for (String cName : cList) {
			System.out.println(cName);
			List<String> mList = new ArrayList<String>(adjacencyMatrix.get(cName).keySet()); 
			Collections.sort(mList);
			for (String mHeader : mList) {
				System.out.println(String.format("  => %d :: %-30s", adjacencyMatrix.get(cName).get(mHeader), mHeader));
			}
		}
	}

	private static void printTCCI() {
		
	}
}