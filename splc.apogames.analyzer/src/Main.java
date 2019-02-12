
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

		printReuseFrequency(games);		// 얼마나 많은 앱(게임)에서 특정 클래스가 재사용되고 있는가?
		printAdjacencyMatrix(games); 	// 통계적으로 각 클래스가 얼마나 많은 다른 클래스와 관계를 맺고 있는가?
		printMethodSimilarity(games);   // 클래스 내부 메소드의 유사도는 어떠한가? public boolean readLevel(boolean bURL, String fileName)
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
					String mHeader = mm.toString();
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
		
		printMapSortByValueAndKey(reuseFrequency);	
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
	
	private static void printMapSortByValue(Map<String, Integer> map) {
		for(int i=0; i<map.size(); i++) {
			for(String key : map.keySet()) {
				if(i == map.get(key)) {
					int pos = key.lastIndexOf('.');
					String pName = key.substring(0, pos);
					String cName = key.substring(pos+1);
					int count = map.get(key);
					String s = String.format("%-20s %-30s %2d", pName, cName, count);
					System.out.println(s);
				}
			}
		}
	}
	
	private static void printMapSortByValueAndKey(Map<String, Integer> map) {
		List<String> keys = new ArrayList();
		List<Integer> values = new ArrayList();
		
		for(String key : map.keySet()) {
			keys.add(key);
			values.add(map.get(key));
		}
		
		for(int i=0; i<keys.size()-1; i++) {
			for(int k=i; k<keys.size(); k++) {
				if( keys.get(i).compareTo(keys.get(k)) > 0 ) {
					String tmpKey = keys.get(i);
					keys.set(i, keys.get(k));
					keys.set(k, tmpKey);
					
					Integer tmpValue = values.get(i);
					values.set(i, values.get(k));
					values.set(k, tmpValue);
				}
			}
		}
		
		for(int i=0; i<map.size(); i++) {
			for(int k=0; k<map.size(); k++) {
				if(i == values.get(k)) {
					String key = keys.get(k);
					int pos = key.lastIndexOf('.');
					String pName = key.substring(0, pos);
					String cName = key.substring(pos+1);
					int count = values.get(k);
					String s = String.format("%-20s %-30s %2d", pName, cName, count);
					System.out.println(s);
				}
			}
		}
	}
	
	private static void printMapSortByKey(Map<String, Integer> map) {
		List<String> keys = new ArrayList();
		List<Integer> values = new ArrayList();
		
		for(String key : map.keySet()) {
			keys.add(key);
			values.add(map.get(key));
		}
		
		for(int i=0; i<keys.size()-1; i++) {
			for(int k=i; k<keys.size(); k++) {
				if( keys.get(i).compareTo(keys.get(k)) > 0 ) {
					String tmpKey = keys.get(i);
					keys.set(i, keys.get(k));
					keys.set(k, tmpKey);
					
					Integer tmpValue = values.get(i);
					values.set(i, values.get(k));
					values.set(k, tmpValue);
				}
			}
		}
		
		for(int i=0; i<keys.size(); i++) {
			String key = keys.get(i);
			int pos = key.lastIndexOf('.');
			String pName = key.substring(0, pos);
			String cName = key.substring(pos+1);
			int count = values.get(i);
			String s = String.format("%-20s %-30s %2d", pName, cName, count);
			System.out.println(s);
		}
	}
}





















//private static void printSummary(Map<String, Game> game
//	Set<String> commonClassNames = null;
//	
//	for(String title : games.keySet()) {
//		Game game = games.get(title);
//		System.out.println(game.toString());
//		if (commonClassNames == null) {		// 초기 집합 설정
//			commonClassNames = game.getReusedClasses();
//		} else {
//			commonClassNames.retainAll(game.getReusedClasses());
//		}
//	}
//	
//	System.out.println("=> Common Classes : " + commonClassNames.toString());
//	System.out.println();
//}
