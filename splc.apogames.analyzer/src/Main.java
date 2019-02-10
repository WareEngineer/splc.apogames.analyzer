
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import parser.ClassModel;
import parser.Game;
import parser.Parser;

public class Main {
	static String commonPath = "C:/Users/user/eclipse-workspace/Java/";
	
	public static void main(String[] args) {
		Parser parser = new Parser();
		Map<String, Game> games = new HashMap<String, Game>();
		Map<String, List<String>> reuse = new HashMap<String, List<String>>();
		
		Map<String, Integer> reuseCount = new HashMap<String, Integer>();

//		List<String> gameTitles = Arrays.asList("ApoBot");
		List<String> gameTitles = getGameTitles(commonPath);
		for (String gameTitle : gameTitles) {
			String path = commonPath + gameTitle; 
			Game game = new Game( gameTitle, parser.parse(path) );
			games.put(gameTitle, game);
			
			for(String s : game.getReusedClasses()) {
				if(reuseCount.containsKey(s)) {
					int newValue = reuseCount.get(s) + 1;
					reuseCount.replace(s, newValue);
				} else {
					reuseCount.put(s, 1);
				}
			}
		}
		
		printSummary(games);
		printMapSortByValueAndKey(reuseCount);
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

	private static void printSummary(Map<String, Game> games) {
		Set<String> commonClassNames = null;
		
		for(String title : games.keySet()) {
			Game game = games.get(title);
			System.out.println(game.toString());
			if (commonClassNames == null) {		// 초기 집합 설정
				commonClassNames = game.getReusedClasses();
			} else {
				commonClassNames.retainAll(game.getReusedClasses());
			}
		}
		
		System.out.println("=> Common Classes : " + commonClassNames.toString());
		System.out.println();
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
