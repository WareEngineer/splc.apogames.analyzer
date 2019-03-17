
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

		Map<String, OverlapedClass> classes = overlapedArchitecture.getClasses();
		Map<String, Set<String>> callRelations = overlapedArchitecture.getCallRelations();
		Map<String, Double> tccis = overlapedArchitecture.getTccis();
		
		Grapher grapher = new Grapher();
		grapher.addNode("#GAME", 0);
		for(String cName : classes.keySet()) {
			Set<String> set = classes.get(cName).getTypes();
			String[] types = set.toArray(new String[set.size()]);
			String s = "";
			if(types.length>0) {
				s = types[0];
				for(int i=1; i<types.length; i++) {
					s += ","+types[i];
				}
			}
			grapher.addNode(cName, tccis.get(cName), s);
		}
		for(String relation : callRelations.keySet()) {
			String from = relation.split("->")[0];
			if(from.equals("#GAME")) {
//				grapher.addEdge(relation, 1.0);
			} else {
				double participationRate = 1.0*callRelations.get(relation).size()/classes.get(from).getTitles().size();
				grapher.addEdge(relation, participationRate);
			}
		}
		grapher.draw();
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
