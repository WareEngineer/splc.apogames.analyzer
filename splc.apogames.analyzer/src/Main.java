
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
		Set<ClassModel> commonClassNames = null;

//		List<String> gameTitles = Arrays.asList("ApoBot");
		List<String> gameTitles = getGameTitles(commonPath);
		for (String gameTitle : gameTitles) {
			String path = commonPath + gameTitle; 
			Game game = new Game( gameTitle, parser.parse(path) );
			games.put(gameTitle, game);
			
			if (commonClassNames == null) {		// 초기 집합 설정
				commonClassNames = game.getAllClassNames();
			} else {
				commonClassNames.retainAll(game.getAllClassNames());
			}
		}
		
		System.out.println("=> Common Classes : " + commonClassNames.size());
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


