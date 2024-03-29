package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassModel {
	private static String rootPath;

	private Set<Integer> lineNumbers;
	private String myType;
	private String myAccessibility;
	private String myPackage;
	private Set<String> myImports;
	private Set<String> myImplicitImports;
	private String myId;
	private Set<String> myExtends;
	private Set<String> myImplements;
	private Set<String> myStaticInstances;
	private Map<String, String> myAttributes;
	private List<MethodModel> myMethods;
//	private Map<String, List<String>> sendingMessages;
	
	public ClassModel(Map<String, Object> map) {
		myImplicitImports = new HashSet<String>();
		myStaticInstances = new HashSet<String>();
		myAttributes = new HashMap<String, String>();
		myMethods = new ArrayList<MethodModel>();
		lineNumbers = new HashSet<Integer>();
		
		for (String key : map.keySet()) {
			switch(key) {
			case "package": 
				myPackage = (String) map.get("package");
				break;
			case "imports":
				myImports = (Set<String>) map.get("imports");	
				break;
			case "accessibility":
				myAccessibility = (String) map.get("accessibility");
				break;
			case "TYPE":
				myType = (String) map.get("TYPE");
				break;
			case "identifier":
				myId = (String) map.get("identifier");
				break;
			case "extends":
				myExtends = new HashSet<String>( (List<String>) map.get("extends") );
				break;
			case "implements":
				myImplements =  new HashSet<String>( (List<String>) map.get("implements") );
				break;
			}
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[" + myType + "]");
		buffer.append(myAccessibility+" "+myId);
		buffer.append("\n  |=>Package    :" + myPackage);
		buffer.append("\n  |=>LineOfCode :" + lineNumbers.size());
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
		
		if (myAttributes != null) {
			buffer.append( String.format("\n  |=>%-10s : ", "Attribute") );
			for (String key : myAttributes.keySet()) {
				buffer.append( "\n  |  " + myAttributes.get(key) + " " + key );
			}
		}
		buffer.append("\n");
		
		return buffer.toString();
	}

	public boolean containsAttribute(String id) {
		if(myAttributes.containsKey(id)) {
			return true;
		}
		return false;
	}
	
	public void addLineNumbers(int lineNum) {
		lineNumbers.add(lineNum);
	}

	public void addImport(String s) {
		myImports.add(s);
	}
	
	public void addImplicitImport(String s) {
		myImplicitImports.add(s);
	}
	
	public void addMethod(MethodModel method) {
		myMethods.add(method);
	}

	public void addStaticInstance(String s) {
		myStaticInstances.add(s);
	}
	
	public void addAttribute(Map<String, Object> map) {
		myAttributes.put( (String) map.get("identifier"), (String) map.get("dataType") );
	}
	
	public void removeImport(String s) {
		myImports.remove(s);
	}
	
	public String getPackageName() {
		return myPackage;
	}

	public List<String> getImports() {
		List<String> imports = new ArrayList<String>();
		imports.addAll(myImports);
		imports.addAll(myImplicitImports);
		return imports;
	}
	
	public Set<String> getImplicitImports() {
		return myImplicitImports;
	}
	
	public Set<String> getExtends() {
		return myExtends;
	}

	public Set<String> getImplements() {
		return myImplements;
	}
	
	public String getClassName() {
		return myId;
	}
	
	public List<MethodModel> getMethods() {
		return myMethods;
	}

	public Set<String> getStaticInstances() {
		return myStaticInstances;
	}

	public Set<String> getAttribute() {
		return myAttributes.keySet();
	}
	
	public String getFullName() {
		if ( "".equals(myPackage) ) {
			return myId;
		}
		else {
			return myPackage+"."+myId;
		}
	}

	public Set<String> getAllUsedTypes() {
		Set<String> usedTypes = new HashSet<String>();
		usedTypes.addAll(myExtends);
		usedTypes.addAll(myImplements);
		usedTypes.addAll(this.getAllVariables());
		return usedTypes;
	}
	
	public Set<String> getAllVariables() {
		Set<String> allVariable = new HashSet<String>();
		allVariable.addAll(myStaticInstances);
		
		for(String attr : myAttributes.values()) {
			if(attr.contains("[]")) {
				allVariable.add(attr.replace("[]", ""));
			}
			else {
				allVariable.add(attr);
			}
		}
		
		for (MethodModel method : myMethods) {
			allVariable.addAll(method.getUsedTypes());
		}
		
		return allVariable;
	}

	public int getLOC() {
		return lineNumbers.size();
	}

	public int getDistinctLOC() {
		int accMethodLoc = 0;
		for(MethodModel mm : myMethods) {
			accMethodLoc += mm.getLOC();
		}
		
		return lineNumbers.size() - accMethodLoc;
	}

	public String getType() {
		return myType;
	}

	public void setImplicitImports(Set<String> implicitImports) {
		myImplicitImports = implicitImports;
	}

	public boolean equals(ClassModel cm) {
		Set<String> mSet = new HashSet<String>();
		
		if(this.myMethods.size() == cm.myMethods.size()) {
			for(MethodModel mm : this.myMethods) {
				mSet.add(mm.getSignature());
			}
			for(MethodModel mm : cm.myMethods) {
				if(mSet.contains(mm.getSignature()) == false) {
					return false;
				}
			}
			return true;
		}
		
		return false;
	}
}
