package architecture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.ClassModel;
import model.MethodModel;
import product.Relation;
import product.RelationPool;

public class OverlapedArchitecture {
	private Set<String> productNames = new HashSet<String>();
	private Map<String, Set<String>> classes = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> classCounter = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> methodCounter = new HashMap<String, Set<String>>();
	private RelationPool callRelations = new RelationPool();
	private RelationPool extendRelations = new RelationPool();
	private RelationPool implementRelations = new RelationPool();
	
	public Set<String> getClassNames() { return classes.keySet(); }
	public int getNumOfProducts() { return productNames.size(); }
	public int getClassIndidence(String className) { return classCounter.get(className).size(); }
	public RelationPool getUniRelations() {
		RelationPool pool = new RelationPool();
		pool.addAll(callRelations);
		pool.addAll(extendRelations);
		pool.addAll(implementRelations);
		return pool;
	}
	
	public Set<String> getEntries() {
		Set<String> inBoundaries = new HashSet<String>();
		RelationPool pool = getUniRelations();
		Set<String> inner = classes.keySet();
		for(String className : inner) {
			for(String target : pool.getCaller(className)) {
				if(inner.contains(target) == false) {
					inBoundaries.add(className);
					break;
				}
			}
		}
		return inBoundaries;
	}
	
	public Set<String> getExits() {
		Set<String> outBoundaries = new HashSet<String>();
		RelationPool pool = getUniRelations();
		Set<String> inner = classes.keySet();
		for(String className : inner) {
			for(String target : pool.getCallee(className)) {
				if(inner.contains(target) == false) {
					outBoundaries.add(className);
					break;
				}
			}
		}
		return outBoundaries;
	}
	
	public void put(String productName, ClassModel classModel) {
		productNames.add(productName);
		String className = classModel.getFullName();
		if(classes.containsKey(className) == false) {
			classes.put(className, new HashSet<String>());
			classCounter.put(className, new HashSet<String>());
		}
		classCounter.get(className).add(productName);
		for(MethodModel methodModel : classModel.getMethods()) {
			String methodSignature = methodModel.getSignature();
			String key = className + "." + methodSignature;
			classes.get(className).add(methodSignature);
			if(methodCounter.containsKey(key) == false) {
				methodCounter.put(key, new HashSet<String>());
			}
			methodCounter.get(key).add(productName);
		}
	}
	
	public void addRelation(String productName,  Relation call, Relation extend, Relation implement) {
		callRelations.add(productName, call);
		extendRelations.add(productName, extend);
		implementRelations.add(productName, implement);
	}
	
	public void printSummary() {
		int p = productNames.size();
		for(String className : classes.keySet()) {
			int c = classCounter.get(className).size();
			System.out.println(String.format("[%2d/%2d]%s", c, p, className));
			for(String methodSignature : classes.get(className)) {
				String key = className + "." + methodSignature;
				int m = methodCounter.get(key).size();
				System.out.println(String.format("\t[%2d/%-2d]%s", m, c, methodSignature));
			}
		}
		System.out.println(classes.keySet().size());
	}
}
