package product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import architecture.OverlapedArchitecture;
import model.ClassModel;
import model.MethodModel;

public class ProductPool {
	private Map<String, Product> products = new HashMap<String, Product>();	// <productName, productInstance>
	private Map<String, Set<String>> cloneCounter = new HashMap<String, Set<String>>(); // <clonedClassName, productNameList>
	private Map<String, Set<String>> useCounter = new HashMap<String, Set<String>>(); // <usedClassName, productNameList>
	private Map<String, Set<String>> constantCounter = new HashMap<String, Set<String>>(); // <constantClassName, productNameList>
	private RelationPool callRelations = new RelationPool(); // <"caller->callee", productNameList>
	private RelationPool extendRelations = new RelationPool(); // <"caller->callee", productNameList>
	private RelationPool implementRelations = new RelationPool(); // <"caller->callee", productNameList>
	
	public Product getProduct(String name) { return products.get(name); }
	
	public void add(String name, Product product) {
		products.put(name, product);
		
		Set<String> allClassNames = product.getClassNames();
		put(cloneCounter, allClassNames, name);
		Set<String> usedClassNames = product.getUsedClassNames();
		put(useCounter, usedClassNames, name);
		Set<String> constantClassNames = product.getConstantClassNames();
		put(constantCounter, constantClassNames, name);
		
		callRelations.add(name, product.getUsedCallRelations());
		extendRelations.add(name, product.getUsedExtendRelations());
		implementRelations.add(name, product.getUsedImplementRelations());
	}
	
	private void put(Map<String, Set<String>> map, Set<String> keySet, String value) {
		for(String key : keySet) {
			if(map.containsKey(key)==false) {
				map.put(key, new HashSet<String>());
			}
			map.get(key).add(value);
		}
	}

	public Set<String> getReusedClassNames() {
		Set<String> reusedClassNames = new HashSet<String>();
		Set<String> usedClassNames = useCounter.keySet();
		for(String className : usedClassNames) {
			if( cloneCounter.containsKey(className) ) {
				boolean isClone = 2 <= cloneCounter.get(className).size();
				boolean isReuse = 2 <= useCounter.get(className).size();
				boolean isNotConstant = !constantCounter.containsKey(className);
				if(isClone && isReuse && isNotConstant) {
					reusedClassNames.add(className);
				}
			}
		}
		return reusedClassNames;
	}
	
	public void printMatrix() {
		System.out.println("\n\nCall Relation");
		drawMatrix(callRelations);
		System.out.println("\n\nExtend Relation");
		drawMatrix(extendRelations);
		System.out.println("\n\nImplement Relation");
		drawMatrix(implementRelations);
	}
	
	private void drawMatrix(RelationPool pool) {
		Map<String, Integer> counter = new HashMap<String, Integer>();
		List<String> list = new ArrayList<String>(getReusedClassNames());
		Collections.sort(list);
		for(String from : list) {
			System.out.print( String.format("%-45s | ", from) );
			for(String to : list) {
				System.out.print( String.format("%3d", pool.getIncidence(from, to)) );
			}
			System.out.println();
		}
	}
	
	public void printSummary() {
		List<String> names = new ArrayList<String>(cloneCounter.keySet());
		Collections.sort(names);
		int total = cloneCounter.keySet().size();
		int use = useCounter.keySet().size();
		int reuse = 0;
		int clone = 0;
		
		for(String name : names) {
			int incidence = cloneCounter.get(name).size();
			if(1 < incidence) {
				clone++;
				if(useCounter.containsKey(name)) {
					reuse++;
					System.out.print("*");
				} else {
					System.out.print(" ");
				}
				System.out.println(String.format("%-45s %3d", name, incidence));
			}
		}
		System.out.println("##################################################");
		System.out.println(String.format("=> Products:%3d", products.keySet().size()));
		System.out.println(String.format("=> Total:%3d, Use:%3d, Clone:%3d, Reuse:%3d", total, use, clone, reuse));
		System.out.println("##################################################");
	}
	
	public void printBoundary() {
		RelationPool pool = new RelationPool();
		pool.addAll(callRelations);
		pool.addAll(extendRelations);
		pool.addAll(implementRelations);
		System.out.println("\n\nBoundary Classes");
		Set<String> start = new HashSet<String>();
		Set<String> end = new HashSet<String>();
		Set<String> classNames = useCounter.keySet();
		Set<String> reuseClassNames = getReusedClassNames();
		for(String from : classNames) {
			for(String to : classNames) {
				if(pool.contains(from, to)) {
					if(reuseClassNames.contains(from)==false && reuseClassNames.contains(to)==true) {
						start.add(to);
					}
					if(reuseClassNames.contains(from)==true && reuseClassNames.contains(to)==false) {
						end.add(from);
					}
				}
			}
		}
		System.out.println("\t in : ");
		for(String name : start) {
			System.out.println("\t\t" + name);
		}
		System.out.println("\t out : ");
		for(String name : end) {
			System.out.println("\t\t" + name);
		}
	}
	
	public Map<String, Double> getHTTCI(Set<String> targetClassNames) {
		Map<String, Double> tccis = new HashMap<String, Double>();
		Map<String, String> map = new HashMap<String, String>();
		for(String className : targetClassNames) {
			Set<String> distinctComponents = new HashSet<String>();
			Set<String> distinctMethods = new HashSet<String>();
			int edge = 0;
			int method = 0;
			int dummy = 0;
			boolean isAllEqual = true;
			ClassModel basis = null;
			for(String productName : products.keySet()) {
				Product product = products.get(productName);
				ClassModel classModel = product.getClassModel(className);
				if(classModel != null) {
					if(isAllEqual) {
						if(basis==null) basis = classModel;
						if(basis.equals(classModel) == false) isAllEqual = false; 
					}
					distinctComponents.add(classModel.getFullName());
					edge++;
					for(MethodModel methodModel : classModel.getMethods()) {
						distinctComponents.add(methodModel.getSignature());
						distinctMethods.add(methodModel.getSignature());
						edge++;
						method++;
					}
				} else {
					dummy++;
				}
			}
			int d = distinctComponents.size();
			double tcci = 1.0 - ((d-1.0)/(edge-1.0));
			double tcciPrime;
			if(isAllEqual) {
				int sub = distinctMethods.size();
				tcciPrime = 1.0 - ((d-sub+dummy-1.0)/(edge-method+dummy-1.0));;
			} else {
				tcciPrime = 1.0 - ((d+dummy-1.0)/(edge+dummy-1.0));;
			}
			
			double use = useCounter.get(className).size();
			double clone = cloneCounter.get(className).size();
			double total = products.size();
			double p_rate = use / total;
			double httci = (2*tcci*p_rate) / (tcci+p_rate);
			double httciPrime = (2*httci*p_rate) / (httci+p_rate);
			tccis.put(className, httciPrime);
			
//			String s = String.format("%-45s (%2.0f): %.5f", className, use, httciPrime);

			String s = String.format("%-45s (%2.0f/%2.0f/%2.0f): %.5f  %.5f  %.5f  %.5f", className, use, clone, total, tcci, tcciPrime, httci, httciPrime);
			map.put(className, s);
		}
		
		for(int i=0; i<=products.size(); i++) {
			for(String className : targetClassNames) {
				if(useCounter.get(className).size() == i) {
					System.out.println(map.get(className));
				}
			}
		}
		
		return tccis;
	}
	
	public OverlapedArchitecture getOverlapedArchitecture(Set<String> targets) {
		OverlapedArchitecture olArchitecture = new OverlapedArchitecture();
		for(String productName : products.keySet()) {
			Product product = products.get(productName);
			Set<String> classNames = product.getClassNames();
			for(String className : classNames) {
				ClassModel classModel = product.getClassModel(className);
				if(targets.contains(className)) {
					olArchitecture.put(productName, classModel);
				}
			}
			Relation callRelation = product.getUsedCallRelations();
			Relation extendRelation = product.getUsedExtendRelations();
			Relation implementRelation = product.getUsedImplementRelations();
			olArchitecture.addRelation(productName, callRelation, extendRelation, implementRelation);
		}
		return olArchitecture;
	}
}
