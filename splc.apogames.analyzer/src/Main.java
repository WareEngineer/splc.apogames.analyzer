
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parser.ClassModel;
import parser.Game;
import parser.Parser;

public class Main {
	static String commonPath = "C:/Users/user/eclipse-workspace/Java/";
	static String[] gameTitles = {"ApoBot"	, 		"ApoCommando", 	"ApoIcarus", //"ApoMarc", 	//	"ApoNotSoSimple", 
								  "ApoPongBeat", "ApoRelax", 	/*"ApoSimple",*/ "ApoSimpleSudoku",	"ApoSlitherLink",
							 	  /*"ApoStarz"*/};
	
	public static void main(String[] args) {
		Parser parser = new Parser();
		Map<String, Game> games = new HashMap<String, Game>();
		Set<ClassModel> commonClassNames = null;
		
		for (String gameTitle : gameTitles) {
			String path = commonPath + gameTitle; 
			Game game = new Game( gameTitle, parser.parse(path) );
			games.put(gameTitle, game);
			
			if (commonClassNames == null) {
				commonClassNames = game.getAllClassNames();
			} else {
				commonClassNames.retainAll(game.getAllClassNames());
			}
		}
		
		System.out.println("=> Common Classes : " + commonClassNames.size());
	}
}


