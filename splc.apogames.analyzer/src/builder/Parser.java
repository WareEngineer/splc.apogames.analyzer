package builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import model.ClassModel;
import model.MethodModel;

public class Parser {
	
	public List<ClassModel> getComponent(List<Map<String, Object>> chunks) {
		List<ClassModel> classList = new ArrayList<ClassModel>();
		
		Set<String> importList = new HashSet<String>();
		String packageName = "";	// default package
		ClassModel mClass = null;
		MethodModel mMethod = null;
		
		Stack<ClassModel> cStack = new Stack<ClassModel>();
		Stack<String> blocks = new Stack<String>();
		boolean isMethod = false;
		
		for(Map<String, Object> chunk : chunks) {
			String type = (String)chunk.get("TYPE");
			
			switch (type) {
			case "package": 
				packageName = (String) chunk.get("path");
				break;
			case "import":
				importList.add((String) chunk.get("path"));
				break;
			case "class": case "interface": case "enum":
				blocks.push("object");
				chunk.put("package", packageName);
				chunk.put("imports", importList);
				mClass = new ClassModel(chunk);
				cStack.push(mClass);
				break;
			case "method":
				if("interface".equals(cStack.peek().getType())==false && chunk.containsKey("abstract")==false) {
					isMethod = true;
				}
				blocks.push(type);
				mMethod = new MethodModel(chunk);
				cStack.peek().addMethod(mMethod);
				break;
			case "variable":
				if (isMethod) {
					mMethod.addVariable(chunk);
				} else {
					cStack.peek().addAttribute(chunk);
				}
				break;
			case "undefined":
				if(cStack.empty()) break;
				ClassModel cm = cStack.peek();
				String id = (String) chunk.get("id");
				if(cm.containsAttribute(id)==false) {
					if(isMethod && mMethod.containsVariable(id)==false) {
						cm.addStaticInstance(id);
					}
				}
				break;
			case "{":
				blocks.push("{");
				break;
			case "}":
				while( !"{".equals(blocks.peek()) ) {
					blocks.pop();
				}
				blocks.pop();
				if ("object".equals(blocks.peek())) {
					classList.add(cStack.pop());
					blocks.pop();
				} else if("method".equals(blocks.peek())) {
					mMethod = null;
					isMethod = false;
					blocks.pop();
				}
				break;
			}
			
//			int lineNum = token.getLineNumber();
//			if(classes.isEmpty()==false) {
//				classes.peek().addLineNumbers(lineNum);
//			}
//			if(isMethod) {
//				mMethod.addLineNumbers(lineNum);
//			}
		}
		
		return classList;
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
