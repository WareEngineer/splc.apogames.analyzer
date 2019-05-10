package builder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import builder.Composer;
import builder.Parser;
import builder.Token;
import builder.Tokenizer;
import model.ClassModel;
import model.Game;

public class Builder {
	private Map<String, Game> games;
	
	public Builder(String commonPath) {
		games = new HashMap<String, Game>();
		Tokenizer tokenizer = new Tokenizer();
		Composer composer = new Composer();
		Parser parser = new Parser();

		List<String> gameTitles = FileManager.getDirectoryNames(commonPath);
		for (String gameTitle : gameTitles) {
			String path = commonPath + gameTitle;
			List<String> javaSourceFiles = FileManager.getAllJavaSourceFile(path);
			Map<String, List<ClassModel>> architecture = new HashMap<String, List<ClassModel>>();
			
			for (String sourceFile : javaSourceFiles) {
				List<Token> tokens = tokenizer.getTokens(sourceFile);
				List<Map<String, Object>> chunks = composer.getChunks(tokens);
				List<ClassModel> classList = parser.getComponent(chunks);
				for(ClassModel cm : classList) {
					String pName = cm.getPackageName();
					if(architecture.containsKey(pName)==false) {
						architecture.put(pName, classList);
					} else {
						architecture.get(pName).addAll(classList);
					}
				}
			}
			
			for(String key : architecture.keySet()) {
				Set<String> implicitImports = new HashSet<String>();
				for(ClassModel cm : architecture.get(key)) {
					implicitImports.add(cm.getPath());
				}
				for(ClassModel cm : architecture.get(key)) {
					cm.setImplicitImports(implicitImports);
				}
			}
			
			Game game = new Game( gameTitle,  architecture );
			games.put(gameTitle, game);
		}
	}
	
	public void printSummary() {
		for(Game game : games.values()) {
			System.out.println(game.toString());
		}
	}
	
	public void printArchitecture() {
		for(Game game : games.values()) {
			System.out.println(game.getTitle());
			game.print();
		}
	}

	public Map<String, Game> getArchitectures() {
		return games;
	}
}
