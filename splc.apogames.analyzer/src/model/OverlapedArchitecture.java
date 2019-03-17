package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import grapher.Grapher;

public class OverlapedArchitecture {
	private Set<String> gameTitles;
	private Map<String, Set<String>> clones;
	private Map<String, Set<String>> reuses;
	private Map<String, OverlapedClass> overlapedClasses;
	private Map<String, Set<String>> overlapedCallRelationMap;
	private Map<String, Set<String>> overlapedExtendRelationMap;
	private Map<String, Set<String>> overlapedImplementRelationMap;
	private Map<String, Double> tccis;
//	private Map<String, Double> innerSimilarities;
	
	private void buildMap(Map<String, Set<String>> map, Set<String> keys, String value) {
		for(String key : keys) {
			if(!map.containsKey(key)) {
				map.put(key, new HashSet<String>());
			}
			map.get(key).add(value);
		}
	}
	
	public Map<String, OverlapedClass> getClasses() {
		return overlapedClasses;
	}
	public Map<String, Set<String>> getCallRelations() {
		return overlapedCallRelationMap;
	}
	public Map<String, Double> getTccis() {
		return tccis;
	}
	
	public OverlapedArchitecture(Map<String, Game> games) {
		gameTitles = new HashSet<String>();
		clones = new HashMap<String, Set<String>>();
		reuses = new HashMap<String, Set<String>>();
		overlapedClasses = new HashMap<String, OverlapedClass>();
		overlapedCallRelationMap = new HashMap<String, Set<String>>();
		overlapedExtendRelationMap = new HashMap<String, Set<String>>();
		overlapedImplementRelationMap = new HashMap<String, Set<String>>();
		tccis = new HashMap<String, Double>();
		
		for(String title : games.keySet()) {
			gameTitles.add(title);
			Game game = games.get(title);
			
			this.buildMap(overlapedCallRelationMap, game.getCallRelations(), title);
			this.buildMap(overlapedExtendRelationMap, game.getExtendRelations(), title);
			this.buildMap(overlapedImplementRelationMap, game.getImplementRelations(), title);
			
			for(ClassModel cm : game.getClonedClasses()) {
				String cName = cm.getPath();
				if(clones.containsKey(cName)==false) {
					clones.put(cName, new HashSet<String>());
				}
				clones.get(cName).add(title);
			}
			
			for(ClassModel cm : game.getReusedClasses()) {
				String cName = cm.getPath();
				if(reuses.containsKey(cName) == false) {
					reuses.put(cName, new HashSet<String>());
					overlapedClasses.put(cName, new OverlapedClass(cName));
				}
				reuses.get(cName).add(title);
				overlapedClasses.get(cName).overlab(title, cm);
			}
		}
		
		
		for(String id : reuses.keySet()) {
			Set<String> distinctComponents = new HashSet<String>();
			int sigma=0;
			
			for(String title : reuses.get(id)) {
				Game game = games.get(title);
				
				ClassModel cm = game.getClassModel(id);
				if(cm!=null) {
					sigma++;
//					distinctComponents.add(id);
					distinctComponents.add(game.getTitle() + id);
					List<MethodModel> mms = cm.getMethods();
					for(MethodModel mm : cm.getMethods()) {
						sigma++;
						distinctComponents.add(mm.getSignature());
					}

					int d = distinctComponents.size();
					if(sigma == 1) {
						d = 2;
						sigma = 2;	
					}
					double tcci = 1.0 - ( (d-1.0) / (sigma-1.0) );
					tccis.put(id, tcci);
				}
			}
		}
	}
	
	public void printTcci() {
		List<String> keys = new ArrayList<String>(tccis.keySet());
		Collections.sort(keys);
		Set<Double> set = new HashSet<Double>(tccis.values());
		List<Double> values = new ArrayList<Double>(set);
		Collections.sort(values);
		
		for(Double value : values) {
			for(String key : keys) {
				if(tccis.get(key).equals(value)) {
					String s = String.format("%-45s [%2d] : %3.2f", key, reuses.get(key).size(), tccis.get(key));
					System.out.println(s);
				}
			}
		}
	}
	
	public void printMatrix() {
		System.out.println("Call Relation :: " + overlapedCallRelationMap.size());
		this.printAdjacencyMatrix(overlapedCallRelationMap);
		System.out.println("Extend Relation :: " + overlapedExtendRelationMap.size());
		this.printAdjacencyMatrix(overlapedExtendRelationMap);
		System.out.println("Implement Relation :: " + overlapedImplementRelationMap.size());
		this.printAdjacencyMatrix(overlapedImplementRelationMap);
	}
	
	private void printAdjacencyMatrix(Map<String, Set<String>> relations) {
		List<String> nodes = new ArrayList<String>( overlapedClasses.keySet() );
		nodes.add("#GAME");
		Collections.sort(nodes);
		
		for(String n1 : nodes) {
			System.out.print( String.format("%-45s | ", n1) );
			for(String n2 : nodes) {
				String r = n1 +"->"+ n2;
				if(relations.containsKey(r)) {
					System.out.print( String.format("%2d ", relations.get(r).size()) );
				} else {
					System.out.print( String.format("%2d ", 0) );
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printReuseFrequency() {
		List<String> list = new ArrayList<String>(overlapedClasses.keySet()); 
		Collections.sort(list);
		
		for(int i=0; i<=gameTitles.size(); i++) {
			for(String item : list) {
				if(reuses.get(item).size() == i) {
					int pos = item.lastIndexOf('.');
					String pName = item.substring(0, pos);
					String cName = item.substring(pos+1);
					String s = String.format("%-20s %-30s %2d", pName, cName, i);
					System.out.println(s);
				}
			}
		}
		System.out.println("------------------------------------------------------");
		String s = String.format("OVERLAP || Poduct:%d, Clone:%d, Reuse:%d", gameTitles.size(), clones.size(), reuses.size());
		System.out.println(s);
		System.out.println("------------------------------------------------------");
	}

	public void printClasses() {
		for(String cName : overlapedClasses.keySet()) {
			String fn = reuses.get(cName).size() + "/" + gameTitles.size();
			String s = String.format("%5s :: %s", fn, cName);
			System.out.println(s);
			overlapedClasses.get(cName).print();
		}
	}

//	public void printSimilarity() {
//		List<String> list = new ArrayList<String>(overlapedClasses.keySet());
//		for(String key : list) {
//			double similarity = overlapedClasses.get(key).getSimilarity();
//			String s = String.format("%-45s [%2d] : %3.2f", key, clones.get(key).size(), similarity);
//			System.out.println(s);
//		}
//	}
}


