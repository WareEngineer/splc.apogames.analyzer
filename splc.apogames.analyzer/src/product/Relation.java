package product;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Relation {
	private Map<String, Set<String>> outMap = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> inMap = new HashMap<String, Set<String>>();
	
	public Set<String> getCallee(String from) {
		if(outMap.containsKey(from)) {
			return outMap.get(from);
		}
		return new HashSet<String>(); 
	}
	
	public Set<String> getCaller(String to) {
		if(inMap.containsKey(to)) {
			return inMap.get(to);
		}
		return new HashSet<String>(); 
	}
	
	public boolean contains(String from, String to) {
		if(outMap.containsKey(from)) {
			if(outMap.get(from).contains(to)) {
				return true;
			}
		}
		return false;
	}
	
	public void add(String from, String to) {
		if(outMap.containsKey(from) == false) {
			outMap.put(from, new HashSet<String>());
		}
		outMap.get(from).add(to);
		if(inMap.containsKey(to) == false) {
			inMap.put(to, new HashSet<String>());
		}
		inMap.get(to).add(from);
	}
	
	public void addAll(Relation other) {
		for(String from : other.outMap.keySet()) {
			for(String to : other.outMap.get(from)) {
				this.add(from, to);
			}
		}
	}
	
	public int size() {
		int count = 0;
		for(String key : outMap.keySet()) {
			count += outMap.get(key).size();
		}
		return count;
	}
	
	public Set<String> toStringSet() {
		Set<String> relationSet = new HashSet<String>();
		for(String from : outMap.keySet()) {
			for(String to : outMap.get(from)) {
				String relation = from+"->"+to;
				relationSet.add(relation);
			}
		}
		return relationSet;
	}
}
