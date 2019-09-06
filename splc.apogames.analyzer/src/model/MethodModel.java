package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodModel {
	private Set<Integer> lineNumbers;
	private String myAccessibility;
	private String myReturn;
	private String myId;
	private Map<String, String> myParameters;
	private List<String> myThrows;
	private Map<String, String> myVariables;
//	private Map<String, List<String>> sendingMessages;
	
	public MethodModel(Map<String, Object> map) {
		myVariables = new LinkedHashMap<String, String>();
		lineNumbers = new HashSet<Integer>();
		
		for (String key : map.keySet()) {
			switch(key) {
			case "accessibility":
				myAccessibility = (String) map.get(key);
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
	
	public String getId() {
		return myId;
	}
	
	public void addLineNumbers(int lineNum) {
		lineNumbers.add(lineNum);
	}

	public void addVariable(Map<String, Object> map) {
		String varType = (String) map.get("dataType");
		String varName = (String) map.get("identifier");
		myVariables.put(varName, varType);
	}
	
	public Set<String> getUsedTypes() {
		Set<String> usedTypes = new HashSet<String>();
		
		for(String var : myVariables.values()) {
			if(var.contains("[]")) {
				usedTypes.add(var.replace("[]", ""));
			}
			else {
				usedTypes.add(var);
			}
		}
		
		return usedTypes;
	}
	
	public String getSignature() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(myAccessibility+" "+myReturn+" "+myId);

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
		
		return buffer.toString();
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
//		buffer.append( String.format("[LOC:%2d] ", lineNumbers.size()) );
		buffer.append(myAccessibility+" "+myReturn+" "+myId);
		
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
			buffer.append(" --(local variable)--> ");
			for (String key : myVariables.keySet()) {
				buffer.append(myVariables.get(key) + " " + key +", ");
			}
			buffer.delete(buffer.lastIndexOf(","), buffer.length());
		}

//		buffer.append( String.format("\n\t[LOC:%s] ", lineNumbers.toString()) );
		
		return buffer.toString();
	}

	public boolean containsVariable(String id) {
		if(myVariables.containsKey(id)) {
			return true;
		}
		return false;
	}

	public Set<String> getVariable() {
		return myVariables.keySet();
	}

	public Integer getLOC() {
		return lineNumbers.size();
	}

}

