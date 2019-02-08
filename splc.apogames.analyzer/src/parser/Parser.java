package parser;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Parser {
	
	public Parser() { }
	
	public Map<String, List<ClassModel>> parse(String root) {
		List<String> javaSourceFiles = this.getAllJavaSourceFile(root);
		Map<String, List<ClassModel>> structure = new HashMap<String, List<ClassModel>>();
		structure.put( "", new ArrayList<ClassModel>() );
		Composer composer = new Composer();
		
		for (String path : javaSourceFiles) {
			List<Token> tokens = new Tokenizer(path).getTokens();
			Stack<ClassModel> classes = new Stack<ClassModel>();
			Stack<String> blocks = new Stack<String>();
			Set<String> importList = new HashSet<String>();
			String packageName = "";
			ClassModel mClass = null;
			MethodModel mMethod = null;
			boolean isMethod = false;

//			System.out.println("************************" + path);
			
			for(Token token : tokens) {
				Map<String, Object> map = composer.compose(token);
				if (map.containsKey("TYPE")) {
					String type = (String)map.get("TYPE");
					switch (type) {
					case "package":
						packageName = (String) map.get("path");
						if ( !structure.containsKey(packageName) ) {
							structure.put(packageName, new ArrayList<ClassModel>() );
						}
						break;
					case "import":
						importList.add((String) map.get("path"));
						break;
					case "class": case "interface": case "enum":
						blocks.push("object");
						map.put("package", packageName);
						map.put("imports", importList);
						mClass = new ClassModel(map);
						classes.push(mClass);
						break;
					case "method":
						isMethod = true;
						blocks.push(type);
						mMethod = new MethodModel(map);
						classes.peek().addMethod(mMethod);
						break;
					case "variable":
						if (isMethod) {
							mMethod.addVariable(map);
						} else {
							classes.peek().addAttribute(map);
						}
						break;
					}
				}
				
				if ("{".equals(token.getId())){
					blocks.push(token.getId());
				} else if ("}".equals(token.getId())) {
					while( !"{".equals(blocks.peek()) ) {
						blocks.pop();
					}
					
					blocks.pop();
					if ("object".equals(blocks.peek())) {
						List<ClassModel> classList = structure.get(packageName);
						classes.peek().optimize();
						classList.add(classes.pop());
						blocks.pop();
					} else if("method".equals(blocks.peek())) {
						isMethod = false;
						blocks.pop();
					}
				}
			}
		}
		return structure;
	}

	private List<String> getAllJavaSourceFile(String root) {
		List<String> javaSourceFiles = new ArrayList<String>();
		
		try {
			javaSourceFiles = Files.walk(Paths.get(root))
							   	   .filter(Files::isRegularFile)
							   	   .map(file -> file.toAbsolutePath().toString())
							   	   .filter(name -> name.contains(".java"))
							   	   .collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return javaSourceFiles;
	}
}
