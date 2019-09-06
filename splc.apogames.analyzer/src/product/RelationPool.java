package product;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RelationPool {
	private Relation relation = new Relation();
	private Map<String, Set<String>> productCounter = new HashMap<String, Set<String>>();
	
	public void add(String productName, Relation relation) {
		this.relation.addAll(relation);
		Set<String> set = relation.toStringSet();
		for(String e : set) {
			if(productCounter.containsKey(e) == false) {
				productCounter.put(e, new HashSet<String>());
			}
			productCounter.get(e).add(productName);
		}
	}
	
	public void addAll(RelationPool other) {
		this.relation.addAll(other.relation);
		for(String key : other.productCounter.keySet()) {
			if(this.productCounter.containsKey(key)) {
				this.productCounter.get(key).addAll(other.productCounter.get(key));
			} else {
				this.productCounter.put(key, other.productCounter.get(key));
			}
		}
	}
	
	public boolean contains(String from, String to) {
		return relation.contains(from, to);
	}
	
	public int size() {
		return relation.size();
	}

	public Set<String> getCallee(String from) {
		return relation.getCallee(from);
	}
	
	public Set<String> getCaller(String to) {
		return relation.getCaller(to);
	}

	public int getIncidence(String from, String to) {
		String relation = from+"->"+to;
		int incidence = 0;
		if(productCounter.containsKey(relation)) {
			incidence = productCounter.get(relation).size();
		}
		return incidence;
	}
}
