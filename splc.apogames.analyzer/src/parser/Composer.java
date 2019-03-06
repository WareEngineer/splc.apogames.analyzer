package parser;

import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import model.ClassModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Composer {
	private boolean isPackage;
	private Map<String, Object> packageMap;

	private boolean isImport;
	private Map<String, Object> importMap;
	
	private int classStep;
	private int classGeneticCount;
	private String className;
	private Map<String, Object> classMap;
	private Map<Integer, List<String>> CLASS;
	
	private int methodStep;
	private int methodGeneticCount;
	private boolean isMethod;
	private String parameterType;
	private String parameterId;
	private Map<String, Object> methodMap;
	private Map<Integer, List<String>> METHOD;
	
	private int variableStep;
	private int variableGeneticCount;
	private boolean isAssignment;
	private Map<String, Object> variableMap;
	private Map<Integer, List<String>> VARIABLE;
	
	private boolean isAnnotation;
	private Queue<Token> queue;
	private Map<String, Object> undefinedMap;
	
	public Composer() {
		isAnnotation = false;
		
		queue = new LinkedList<Token>();
		queue.offer(null);
		queue.offer(null);
		queue.offer(null);
		
		classStep = -1;
		className = "";
		classGeneticCount = 0;
		CLASS = new HashMap<Integer, List<String>>();
		CLASS.put(0, Arrays.asList("public","protected","private","abstract","final","strictfp"));
		CLASS.put(1, Arrays.asList("class","interface","enum"));
		CLASS.put(2, Arrays.asList("<"));
		CLASS.put(3, Arrays.asList(","));
		CLASS.put(4, Arrays.asList(">"));
		CLASS.put(5, Arrays.asList("extends"));
		CLASS.put(6, Arrays.asList("."));
		CLASS.put(7, Arrays.asList("<"));
//		CLASS.put(11, Arrays.asList(","));
		CLASS.put(8, Arrays.asList(">"));
		CLASS.put(9, Arrays.asList(","));
		CLASS.put(10, Arrays.asList("implements"));
		CLASS.put(11, Arrays.asList("."));
		CLASS.put(12, Arrays.asList("<"));
//		CLASS.put(11, Arrays.asList(","));
		CLASS.put(13, Arrays.asList(">"));
		CLASS.put(14, Arrays.asList(","));
		CLASS.put(15, Arrays.asList("{"));

		methodStep = -1;
		isMethod = false;
		methodGeneticCount = 0;
		parameterType = "";
		parameterId = "";
		METHOD = new HashMap<Integer, List<String>>();
		METHOD.put(0, Arrays.asList("public","protected","private","abstract","final","strictfp","default","static","synchronized"));
		METHOD.put(1, Arrays.asList("<"));
		METHOD.put(2, Arrays.asList(","));
		METHOD.put(3, Arrays.asList(">"));
		METHOD.put(4, Arrays.asList("void","boolean","char","short","int","long","float","double"));
		METHOD.put(5, Arrays.asList("<"));
		METHOD.put(6, Arrays.asList(","));
		METHOD.put(7, Arrays.asList(">"));
		METHOD.put(8, Arrays.asList("["));
		METHOD.put(9, Arrays.asList("]"));
		METHOD.put(10, Arrays.asList("("));
		METHOD.put(11, Arrays.asList("boolean","char","short","int","long","float","double"));
		METHOD.put(12, Arrays.asList("<"));
		METHOD.put(13, Arrays.asList(">"));
		METHOD.put(14, Arrays.asList("["));
		METHOD.put(15, Arrays.asList("]"));
		METHOD.put(16, Arrays.asList(","));
		METHOD.put(17, Arrays.asList(")"));
		METHOD.put(18, Arrays.asList("throws"));
		METHOD.put(19, Arrays.asList(","));
		METHOD.put(20, Arrays.asList(";","{"));
		
		variableStep = -1;
		variableGeneticCount = 0;
		isAssignment = false;
		VARIABLE = new HashMap<Integer, List<String>>();
		VARIABLE.put(0, Arrays.asList("public","protected","private","final","static","volatile", "transient"));
		VARIABLE.put(1, Arrays.asList("boolean","char","short","int","long","float","double"));
		VARIABLE.put(2, Arrays.asList("<"));
		VARIABLE.put(3, Arrays.asList(">"));
		VARIABLE.put(4, Arrays.asList("["));
		VARIABLE.put(5, Arrays.asList("]"));
		VARIABLE.put(6, Arrays.asList("="));
		VARIABLE.put(7, Arrays.asList(","));
		VARIABLE.put(8, Arrays.asList(";"));
	}
	
	public Map<String, Object> compose(Token token) {
		Map<String, Object> tmp = new HashMap();
		int count = 0;
		
		// 어노테이션 무시
		if ( "@".equals( token.getId() )) {
			isAnnotation = true;
		} else if ( isAnnotation ) {
			isAnnotation = false;
			return tmp;
		}
		
		if (composeStaticClass(token)) {
			tmp = undefinedMap;
			count++;
		}
		
		if (composePackage(token)) {
			tmp = packageMap;
			count++;
		}
		
		if (composeImport(token)) {
			tmp = importMap;
			count++;
		}
		
		if (composeClass(token)) {
			tmp = classMap;
			count++;
		}
		if (composeMethod(token)) {
			tmp = methodMap;
			count++;
		}
		
		if ( isMethod ) variableStep = -1;
		
		if (composeVariable(token)) {
			tmp = variableMap;
			count++;
		}
		
		if (count > 1) {
			try {
				throw new Exception();
			} catch (Exception e) {
				System.out.println("분류 에러");
				e.printStackTrace();
			}
		}
		
		return tmp;
	}
	
	private boolean composeStaticClass(Token token) {
		queue.offer(token);
		if(queue.poll() == null) return false;
		
		Token[] tokens = queue.toArray(new Token[queue.size()]);
		if(tokens.length != 3) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String staticClassPattern = TOKEN_TYPE.IDENTIFIER + ".";
		if(".".equals(tokens[0].getId())==false && tokens[0].getType()!=TOKEN_TYPE.IDENTIFIER) {
			String pattern = tokens[1].getType() + tokens[2].getId();

			if(staticClassPattern.equals(pattern)) {
				undefinedMap = new HashMap<String, Object>();
				undefinedMap.put("TYPE", "undefined");
				undefinedMap.put("id", tokens[1].getId());
				return true;
			}
		}
		
		String constructPattern = "new" + TOKEN_TYPE.IDENTIFIER;
		if(tokens[0].getType()==TOKEN_TYPE.KEYWORD && "new".equals(tokens[0].getId())) {
			String pattern = tokens[0].getId() + tokens[1].getType();
			
			if(constructPattern.equals(pattern)) {
				undefinedMap = new HashMap<String, Object>();
				undefinedMap.put("TYPE", "undefined");
				undefinedMap.put("new", "");
				undefinedMap.put("id", tokens[1].getId());
				return true;
			}
		}
		
		// 캐스팅 처리 필요
		
		return false;
	}

	private boolean composePackage(Token token) {
		if ("package".equals(token.getId())) {
			isPackage = true;
			packageMap = new HashMap<String, Object>();
			packageMap.put("TYPE", "package");
			packageMap.put("path", "");
		} else if (isPackage) {
			if (";".equals(token.getId())) {
				isPackage = false;
				return true;
			}
			packageMap.replace("path", packageMap.get("path") + token.getId());
		}
		
		return false;
	}
	
	private boolean composeImport(Token token) {
		if ("import".equals(token.getId())) {
			isImport = true;
			importMap = new HashMap<String, Object>();
			importMap.put("TYPE", "import");
			importMap.put("path", "");
		} else if (isImport) {
			if (";".equals(token.getId())) {
				isImport = false;
				return true;
			}
			importMap.replace("path", importMap.get("path") + token.getId());
		}
		
		return false;
	}
	
	private boolean composeClass(Token token) {
		if( classStep==-1  ||  (classStep>0 && CLASS.get(0).contains(token.getId())) ) {
			classGeneticCount = 0;
			classMap = new HashMap<String, Object>();
			classMap.put("TYPE", null);
			classMap.put("perspective", "default");
			classMap.put("identifier", null);
			classMap.put("extends", new Stack<String>());
			classMap.put("implements", new Stack<String>());
			classStep = 0;
		}
		
		if (token.isIdentifier()) {
			Stack<String> mExtends;
			Stack<String> mImplements;
			switch(classStep) {
				case 1: 	// 오브젝트(class, interface, enum) 식별자
					className = token.getId();
					classMap.replace("identifier", token.getId()); 
					break;
				case 5: 	// 확장(extends) 클래스
					mExtends = (Stack<String>) classMap.get("extends");
					mExtends.push(token.getId());
					break;
				case 6:		// "." 연속
					mExtends = (Stack<String>) classMap.get("extends");
					if (mExtends.isEmpty()) return false;
					mExtends.push(mExtends.pop() + "." + token.getId());
					break;
				case 10:	// 구현(implements) 클래스
					mImplements = (Stack<String>) classMap.get("implements");
					mImplements.push(token.getId());
					break;
				case 11:		// "." 연속
					mImplements = (Stack<String>) classMap.get("implements");
					if (mImplements.isEmpty()) return false;
					mImplements.push(mImplements.pop() + "." + token.getId());
					break;
			}
		} else {
			while(classStep < CLASS.size() && !CLASS.get(classStep).contains(token.getId())) {
				classStep++;
			}
			switch (classStep) {
				// "public","protected","private","abstract","final","strictfp"
				case 0: 
					String s = token.getId();
					if ( "public".equals(s) || "protected".equals(s) || "private".equals(s) ) {
						classMap.replace("perspective", s);
					} else {
						classMap.put(s, s); 
					} 
					break;
				// "class","interface","enum"
				case 1: 
					classMap.replace("TYPE", token.getId()); 
					break;
				// "<"
				case 2: 
					classGeneticCount++;
					break;
				// ","
				case 3: 
					classStep = 2;
					break;
				// ">"
				case 4:
					classGeneticCount--;
					if (classGeneticCount > 0) {
						classStep = 2;
					}
					break;
				// "extends", "."
				case 5: case 6: break;
				// "<"
				case 7: 
					classGeneticCount++;
					break;
				// ">"
				case 8:
					classGeneticCount--;
					if (classGeneticCount > 0) {
						classStep = 7;
					}
					break;
				// ","
				case 9:
					if (classGeneticCount > 0) {
						classStep = 7;
					} else {
						classStep = 5;
					}
					break;
				// "implements", "."
				case 10: case 11: break;
				// "<"
				case 12: 
					classGeneticCount++;
					break;
				// ">"
				case 13:
					classGeneticCount--;
					if (classGeneticCount > 0) {
						classStep = 12;
					}
					break;
				// ","
				case 14:
					if (classGeneticCount > 0) {
						classStep = 12;
					} else {
						classStep = 10;
					}
					break;
				// "{"
				case 15:
					if( classMap.get("identifier")!=null ) {
						return true;
					}
					classStep = -1;
					break;
			}
		}
		
		if (classStep == CLASS.size()) {
			classStep = -1;
		}
		
		return false;
	}

	private boolean composeMethod(Token token) {
		if (methodStep==-1  ||  (methodStep>0 && METHOD.get(0).contains(token.getId()))) {
			isMethod = false;
			parameterId = "";
			parameterType = "";
			methodGeneticCount = 0;
			methodMap = new HashMap<String, Object>();
			methodMap.put("TYPE", "method");
			methodMap.put("perspective", "default");
			methodMap.put("return", null);
			methodMap.put("identifier", null);
			methodMap.put("parameters", new LinkedHashMap<String, String>());
			methodMap.put("throws", new ArrayList<String>()); 
			methodStep = 0;
		}
		
		if (token.isIdentifier()) {
			switch(methodStep) {
				case 0: case 3:			// primitive 타입이 아닌, 메소드 리턴값
					methodMap.replace("return", token.getId());
					methodStep = 4;
					break;
				case 4: case 7: case 9: // 메소드 식별자
					methodMap.replace("identifier", token.getId());
					break;
				case 10:  				// primitive 타입이 아닌, 파라미터 타입
					parameterType = token.getId();
					methodStep = 11;
					break;
				case 11: case 13: case 15: 	// 파라미터 식별자
					parameterId = token.getId();
					break;
				case 18: 	// 메소드에서 발생 가능한 예외(Exception)
					List<String> exceptions = (List<String>) methodMap.get("throws");
					exceptions.add(token.getId());
					break;
			}
		} else {
			Map<String,String> parameters;
			while(methodStep < METHOD.size() && !METHOD.get(methodStep).contains(token.getId())) {
				methodStep++;
			}
			switch (methodStep) {
				// "public","protected","private","abstract","final","strictfp","default","static","synchronized"
				case 0: 
					String s = token.getId();
					if ( "public".equals(s) || "protected".equals(s) || "private".equals(s) ) {
						methodMap.replace("perspective", s);
					} else {
						methodMap.put(s, s);
					} 
					break;
				// "<" 메소드 전체에 사용하는 제네릭
				case 1:
					methodGeneticCount++;
					break;
				// ","
				case 2:
					methodStep = 1;
					break;
				// ">"
				case 3:
					methodGeneticCount--;
					if (methodGeneticCount > 0) {
						methodStep = 1;
					}
					break;
				// "void","boolean","char","short","int","long","float","double"
				case 4: 
					methodMap.put("return", token.getId()); 
					break;
				// "<" 메소드 반환 타입에 등장하는 제네릭
				case 5:
					methodGeneticCount++;
					break;
				// ","
				case 6:
					methodStep = 5;
					break;
				// ">"
				case 7:
					methodGeneticCount--;
					if (methodGeneticCount > 0) {
						methodStep = 5;
					}
					break;
				// "["
				case 8:
					methodMap.replace("return", methodMap.get("return")+"[");
					break;
				// "]"
				case 9:
					methodMap.replace("return", methodMap.get("return")+"]");
					methodStep = 7;
					break;
				// "("
				case 10:
					methodMap.put(token.getId(), token.getId()); 
					if ( methodMap.get("return")!=null && methodMap.get("identifier")!=null ) {
						isMethod = true;
					} else 
					if ( methodMap.get("identifier")==null && className.equals(methodMap.get("return")) ) {
						methodMap.replace("identifier", methodMap.get("return"));
						methodMap.replace("return", null);
						isMethod = true;
					}
					break;
				// "boolean","char","short","int","long","float","double"
				case 11: 
					parameterType = token.getId();
					break;
				// "<" 파라미터 데이터 타입에 등장하는 제네릭
				case 12:
					methodGeneticCount++;
					break;
				// ">"
				case 13:
					methodGeneticCount--;
					if (methodGeneticCount > 0) {
						methodStep = 12;
					}
					break;
				// "["
				case 14:
					parameterType = parameterType + "[";
					break;
				// "]"
				case 15:
					parameterType = parameterType + "]";
					methodStep = 13;
					break;
				// "," 파라미터 내부 쉼표
				case 16:
					parameters = (Map<String, String>) methodMap.get("parameters");
					parameters.put(parameterId, parameterType);
					if (methodGeneticCount > 0) {
						methodStep = 12;
					} else {
						methodStep=10;
					}
					break;
				// ")"
				case 17: 
					if ( !"".equals(parameterId) && !"".equals(parameterType)) {
						parameters = (Map<String, String>) methodMap.get("parameters");
						parameters.put(parameterId, parameterType);
					}
					methodMap.put(token.getId(), token.getId()); 
					isMethod = false;
					break;
				// "throws" 키워드 뒤에 등장하는 쉼표
				case 19: 
					methodStep=18; 
					break;
				// ";", "{"
				case 20 : 
					if( methodMap.get("identifier")!=null && methodMap.containsKey("(") && methodMap.containsKey(")")) {
						methodMap.remove("(");
						methodMap.remove(")");
						return true;
					}
					methodStep = -1;
					break;
			}
		}

		if (methodStep == METHOD.size()) {
			methodStep = -1;
		}
		
		return false;
	}
	
	private boolean composeVariable(Token token) {
		if(variableStep==-1  ||  variableStep>0 && VARIABLE.get(0).contains(token.getId())) {
			isAssignment = false;
			variableGeneticCount = 0;
			variableMap = new HashMap<String, Object>();
			variableMap.put("TYPE", "variable");
			variableMap.put("perspective", "default");
			variableMap.put("dataType", null);
			variableMap.put("identifier", null);
			variableStep = 0;
		}
		
		if(isAssignment) {
			if( ",".equals(token.getId())  ||  ";".equals(token.getId()) ) {
				isAssignment = false;
			} else {
				return false;
			}
		}
		
		if (token.isIdentifier()) {
			switch(variableStep) {
				case 0 :
					variableMap.replace("dataType", token.getId());
					variableStep = 1;
					break;
				case 1: case 3: case 5:
					variableMap.replace("identifier", token.getId());
					break;
			}
		} else {
			while(variableStep < VARIABLE.size() && !VARIABLE.get(variableStep).contains(token.getId())) {
				variableStep++;
			}
			switch (variableStep) {
				// "public","protected","private","final","static","volatile", "transient"
				case 0: 
					String s = token.getId();
					if ( "public".equals(s) || "protected".equals(s) || "private".equals(s) ) {
						variableMap.replace("perspective", s);
					} else {
						variableMap.put(s, s);
					} 
					break;	
				// "boolean","char","short","int","long","float","double"
				case 1: 
					variableMap.replace("dataType", token.getId());
					break;
				// "<"
				case 2:
					variableGeneticCount++;
					break;
				// ">"
				case 3:
					variableGeneticCount--;
					if (variableGeneticCount > 0) {
						variableStep = 2;
					}
					break;
				// "["
				case 4:
					variableMap.replace("dataType", variableMap.get("dataType")+"[");
					break;
				// "]"
				case 5:
					variableMap.replace("dataType", variableMap.get("dataType")+"]");
					variableStep = 3;
					break;
				// "="
				case 6: 
					isAssignment = true;
					break;
				// ","
				case 7:
					if (variableGeneticCount > 0) {
						variableStep = 2;
					} else if (variableMap.get("identifier")!=null) {
						variableStep = 3;
						if( variableMap.get("dataType")!=null ) {
							return true;
						}
					}
					break;
				// ";"
				case 8: 
					variableStep = -1;
					if( variableMap.get("dataType")!=null && variableMap.get("identifier")!=null ) {
						return true;
					}
					break;
			}
		}

		if (variableStep == VARIABLE.size()) {
			variableStep = -1;
		}
		
		return false;
	}
}
