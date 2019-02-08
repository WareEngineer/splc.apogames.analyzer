package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassModel {
	private static String rootPath;

	private String myType;
	private String myPerspective;
	private String myPackage;
	private Set<String> myImports;
	private String myId;
	private List<String> myExtends;
	private List<String> myImplements;
	private List<MethodModel> myMethods;
	private Map<String, String> myAttribute;
	private Set<String> myRelations;
//	private Map<String, List<String>> sendingMessages;
	
	public ClassModel(Map<String, Object> map) {
		myMethods = new ArrayList<MethodModel>();
		myAttribute = new HashMap<String, String>();
		myRelations = new HashSet<String>();
		
		for (String key : map.keySet()) {
			switch(key) {
			case "package": 
				myPackage = (String) map.get("package");
				break;
			case "imports":
				myImports = (Set<String>) map.get("imports");	
				break;
			case "perspective":
				myPerspective = key;
				break;
			case "TYPE":
				myType = (String) map.get("TYPE");
				break;
			case "identifier":
				myId = (String) map.get("identifier");
				break;
			case "extends":
				myExtends = (List<String>) map.get("extends");
				break;
			case "implements":
				myImplements = (List<String>) map.get("implements");
				break;
			}
		}
	}
	
	public Set<String> getImports() {
		return myImports;
	}
	
	public String getClassName() {
		return myId;
	}
	
	public Set<String> getRelations() {
		return myRelations;
	}
	
	public void addMethod(MethodModel method) {
		myMethods.add(method);
	}
	
	public void addAttribute(Map<String, Object> map) {
		myAttribute.put( (String) map.get("identifier"), (String) map.get("dataType") );
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[" + myType + "]");
		buffer.append(myPerspective+" "+myId);
		buffer.append("\n  |=>Package    :" + myPackage);
		if (myImports != null) {
			buffer.append( String.format("\n  |=>%-10s : %s", "Imports", myImports.toString()) );
		}
		if (myExtends != null) {
			buffer.append( String.format("\n  |=>%-10s : %s", "Extends", myExtends.toString()) );
		}
		if (myImplements != null) {
			buffer.append( String.format("\n  |=>%-10s : %s", "Implements", myImplements
					.toString()) );
		}
		if (myMethods != null) {
			buffer.append( String.format("\n  |=>%-10s : ", "Methods") );
			for (MethodModel method : myMethods) {
				buffer.append( "\n  |  " + method.toString() );
			}
		}
		
		if (myAttribute != null) {
			buffer.append( String.format("\n  |=>%-10s : ", "Attribute") );
			for (String key : myAttribute.keySet()) {
				buffer.append( "\n  |  " + myAttribute.get(key) + " " + key );
			}
		}
		buffer.append("\n");
		
		return buffer.toString();
	}

	public void optimize() {
//		myRelations.addAll(myExtends);
//		myRelations.addAll(myImplements);
		
		for (MethodModel method : myMethods) {
			method.addClassAttributes(myAttribute);
			myRelations.addAll(method.getRelations());
		}
		
//		System.out.println(this.toString());
//		System.out.print("    " + myId);
//		System.out.println("=>" + myRelations);
	}
	
	public boolean equals(ClassModel other) {
		String s1 = this.myPackage + this.myId;
		String s2 = other.myPackage + other.myId;
		
		if (s1.equals(s2)) {
			return true;
		} 
		
		return false;
	}

}
