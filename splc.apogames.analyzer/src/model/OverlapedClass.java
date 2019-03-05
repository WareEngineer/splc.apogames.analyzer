package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OverlapedClass {
	private String className;
	private Set<String> gameTitles;
	private Set<String> methodSignatures;
	private Map<String, Set<String>> method2titles;
	
	public OverlapedClass(String className) {
		gameTitles = new HashSet<String>();
		methodSignatures = new HashSet<String>();
		method2titles = new HashMap<String, Set<String>>();
		this.className = className;
	}

	public void overlab(String title, ClassModel classModel) {
		gameTitles.add(title);
		for(MethodModel mm : classModel.getMethods()) {
			String mSignature = mm.getSignature();
			if(method2titles.containsKey(mSignature) == false) {
				method2titles.put(mSignature, new HashSet<String>());
			}
			method2titles.get(mSignature).add(title);
			methodSignatures.add(mSignature);
		}
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
