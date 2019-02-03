package parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodModel {
	private String myPerspective;
	private String myReturn;
	private String myId;
	private Map<String, String> myParameters;
	private List<String> myThrows;
	private Map<String, String> myClassAttributes;
	private Map<String, String> myVariables;
	private Set<String> myRelations;
	private Map<String, List<String>> sendingMessages;
	
	public MethodModel(Map<String, Object> map) {
		myVariables = new LinkedHashMap<String, String>();
		myRelations = new HashSet<String>();
		
		for (String key : map.keySet()) {
			switch(key) {
			case "perspective":
				myPerspective = (String) map.get(key);
				break;
			case "return":
				myReturn = (String) map.get(key);
				break;
			case "identifier":
				myId = (String) map.get("identifier");
				break;
			case "parameters":
				myParameters = (Map<String, String>) map.get(key);
				for(String varId : myParameters.keySet()) {
					myVariables.put(varId, myParameters.get(varId));
				}
				break;
			case "throws":
				myThrows = (List<String>) map.get(key);
				break;
			}
		}
	}

	public void addVariable(Map<String, Object> map) {
		String varType = (String) map.get("dataType");
		String varName = (String) map.get("identifier");
		myVariables.put(varName, varType);
	}
	
	public Set<String> getRelations() {
		return myRelations;
	}

	public void addClassAttributes(Map<String, String> classAttributes) {
		myClassAttributes = classAttributes;
		relate(myVariables);
		relate(myClassAttributes);
	}
	
	private void relate(  Map<String, String> vars ) {
		String varType; 
		for (String key : vars.keySet()) {
			varType = vars.get(key);
			if (varType.contains("[]")) {
				varType = varType.replace("[]", "");
			}
			
			if (!isPrimitiveType(varType)) {
				myRelations.add(varType);
			}
		}
//		System.out.println(myId + ":" +myRelations);
	}
	
	private boolean isPrimitiveType(String type) {
		switch(type) {
		case "boolean": case "char": case "short": case "int": case "long": case "float": case "double": 
			return true;
		}
		return false;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(myPerspective+" "+myReturn+" "+myId);

		buffer.append("(");
		if ( !myParameters.isEmpty() ) {
			for (String key : myParameters.keySet()) {
				buffer.append(myParameters.get(key) + " " + key +", ");
			}
			buffer.delete(buffer.lastIndexOf(","), buffer.length());
		}
		buffer.append(") ");
		
		if ( !myThrows.isEmpty() ) {
			buffer.append("throws ");
			for (String s : myThrows) {
				buffer.append(s +",");
			}
			buffer.delete(buffer.lastIndexOf(","), buffer.length());
			buffer.append(" ");
		}
		
		if ( !myVariables.isEmpty() ) {
			buffer.append(" --(��������)--> ");
			for (String key : myVariables.keySet()) {
				buffer.append(myVariables.get(key) + " " + key +", ");
			}
			buffer.delete(buffer.lastIndexOf(","), buffer.length());
		}
		
		return buffer.toString();
	}

}

