package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OverlapedClass {
	private String className;
	private Set<String> types;
	private Set<String> gameTitles;
	private Set<String> methodSignatures;
	private Map<String, Set<String>> method2titles;
	private Map<String, Integer> method2accLoc;
	private Map<String, Integer> method2maxLoc;
	private int maxDistictClassLOC;
	
	public String getType() {
		String s = "¡ì" + types.toString().substring(1, types.toString().length()-1) + "¡í";;
		return s;
	}
	
	public Set<String> getTypes() {
		return types;
	}
	
	public Set<String> getTitles() {
		return gameTitles;
	}
	
	public OverlapedClass(String className) {
		types = new HashSet<String>();
		gameTitles = new HashSet<String>();
		methodSignatures = new HashSet<String>();
		method2titles = new HashMap<String, Set<String>>();
		method2accLoc = new HashMap<String, Integer>();
		method2maxLoc = new HashMap<String, Integer>();
		this.className = className;
		this.maxDistictClassLOC = 0;
	}

	public void overlab(String title, ClassModel classModel) {
		types.add(classModel.getType());
		gameTitles.add(title);
		for(MethodModel mm : classModel.getMethods()) {
			String mSignature = mm.getSignature();
			methodSignatures.add(mSignature);
			if(method2titles.containsKey(mSignature) == false) {
				method2titles.put(mSignature, new HashSet<String>());
				method2accLoc.put(mSignature, 0);
				method2maxLoc.put(mSignature, 0);
			}
			method2titles.get(mSignature).add(title);
			method2accLoc.replace(mSignature, method2accLoc.get(mSignature) + mm.getLOC());
			if(mm.getLOC() > method2maxLoc.get(mSignature)) {
				method2maxLoc.replace(mSignature, mm.getLOC());
			}
		}
		
		int distictClassLOC = classModel.getDistinctLOC();
		if(maxDistictClassLOC < distictClassLOC) {
			maxDistictClassLOC = distictClassLOC;
		}
	}
	
	public double getSimilarity() {
		int totalLoc = 0;
		totalLoc += maxDistictClassLOC;
		for(int loc : method2maxLoc.values()) {
			totalLoc += loc;
		}
		
		double similarity = 0;
		for(String method : methodSignatures) {
			System.out.println(method2titles.get(method).size());
			System.out.println(method2maxLoc.get(method));
			similarity = (method2maxLoc.get(method)/totalLoc) * (method2accLoc.get(method)/(method2titles.get(method).size()*method2maxLoc.get(method)));
		}
		
		return similarity;
	}
	
	public void print() {
//		System.out.println(className);
		for(String signature : methodSignatures) {
			String fn = method2titles.get(signature).size() + "/" + gameTitles.size();
			String s = String.format("\t%5s :: %s", fn, signature);
			System.out.println(s);
		}
	}
}
