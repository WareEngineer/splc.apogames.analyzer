package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import model.ClassModel;
import model.MethodModel;

public class Parser {
	
	public Parser() { }
	
	public Map<String, List<ClassModel>> parse(String root) {
		List<String> javaSourceFiles = this.getAllJavaSourceFile(root);
		Map<String, List<ClassModel>> structure = new HashMap<String, List<ClassModel>>();
		structure.put( "", new ArrayList<ClassModel>() );
		Composer composer = new Composer();
		
		for (String path : javaSourceFiles) {
			Tokenizer tokenizer = new Tokenizer(path);
			Stack<ClassModel> classes = new Stack<ClassModel>();
			Stack<String> blocks = new Stack<String>();
			Set<String> importList = new HashSet<String>();
			String packageName = "";
			ClassModel mClass = null;
			MethodModel mMethod = null;
			boolean isMethod = false;

			for( Token token : tokenizer.getTokens() ) {
				Map<String, Object> component = composer.compose(token);
				
				if (component.containsKey("TYPE")) {
					String type = (String)component.get("TYPE");
					switch (type) {
					case "package":
						packageName = (String) component.get("path");
						if ( !structure.containsKey(packageName) ) {
							structure.put(packageName, new ArrayList<ClassModel>() );
						}
						break;
					case "import":
						importList.add((String) component.get("path"));
						break;
					case "class": case "interface": case "enum":
						blocks.push("object");
						component.put("package", packageName);
						component.put("imports", importList);
						mClass = new ClassModel(component);
						classes.push(mClass);
						break;
					case "method":
						if( "{".equals(token.getId()) ) {
							isMethod = true;
						}
						blocks.push(type);
						mMethod = new MethodModel(component);
						classes.peek().addMethod(mMethod);
						break;
					case "variable":
						if (isMethod) {
							mMethod.addVariable(component);
						} else {
							classes.peek().addAttribute(component);
						}
						break;
					case "undefined":
						if(classes.empty()) break;
						ClassModel cm = classes.peek();
						String id = (String) component.get("id");
						if(cm.containsAttribute(id)==false) {
							if(isMethod && mMethod.containsVariable(id)==false) {
								cm.addStaticInstance(id);
							}
						}
						break;
					}
				}

				int lineNum = token.getLineNumber();
				if(classes.isEmpty()==false) {
					classes.peek().addLineNumbers(lineNum);
				}
				if(isMethod) {
					mMethod.addLineNumbers(lineNum);
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
						classList.add(classes.pop());
						blocks.pop();
					} else if("method".equals(blocks.peek())) {
						isMethod = false;
						blocks.pop();
					}
				}
			}
		}
		
		for(List<ClassModel> list : structure.values()) {
			for(ClassModel cm1 : list) {
				for(ClassModel cm2 : list) {
					cm1.addImplicitImport(cm2.getPath());
				}
				
				List<String> imports = new ArrayList<String>(cm1.getImports());
				for(String s : imports) {
					if(s.endsWith(".*")) {
						String pName = s.replace(".*", "");
						if(structure.containsKey(pName)) {
							cm1.removeImport(s);
							for(ClassModel cm2 : structure.get(pName)) {
								cm1.addImport(cm2.getPath());
							}
						}
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
