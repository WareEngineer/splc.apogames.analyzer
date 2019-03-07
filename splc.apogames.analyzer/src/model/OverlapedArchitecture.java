package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OverlapedArchitecture {
	private Set<String> gameTitles;
	private Map<String, Set<String>> clones;
	private Map<String, OverlapedClass> overlapedClasses;
	private Map<String, Set<String>> overlapedCallMap;
	private Map<String, Set<String>> overlapedExtendMap;
	private Map<String, Set<String>> overlapedImplementMap;
	private Map<String, Set<String>> class2titles;
	private Map<String, Double> tccis;
	
	public OverlapedArchitecture(Map<String, Game> games) {
		gameTitles = new HashSet<String>();
		clones = new HashMap<String, Set<String>>();
		overlapedClasses = new HashMap<String, OverlapedClass>();
		overlapedCallMap = new HashMap<String, Set<String>>();
		overlapedExtendMap = new HashMap<String, Set<String>>();
		overlapedImplementMap = new HashMap<String, Set<String>>();
		class2titles = new HashMap<String, Set<String>>();
		tccis = new HashMap<String, Double>();
		
		for(String title : games.keySet()) {
			gameTitles.add(title);
			Game game = games.get(title);
			
			overlapedCallMap.putAll(game.getCallMap());
			overlapedExtendMap.putAll(game.getExtendMap());
			overlapedImplementMap.putAll(game.getImplementMap());

			for(ClassModel cm : game.getClonedClasses()) {
				String path = cm.getPath();
				if(clones.containsKey(path)==false) {
					clones.put(path, new HashSet<String>());
				}
				clones.get(path).add(title);
			}
			
			Set<ClassModel> classes = game.getReusedClasses();
			for(ClassModel c : classes) {
				String cName = c.getPath();
				if(overlapedClasses.containsKey(cName) == false) {
					overlapedClasses.put(cName, new OverlapedClass(cName));
				}
				overlapedClasses.get(cName).overlab(title, c);
				
				if(class2titles.containsKey(cName) == false) {
					class2titles.put(cName, new HashSet<String>());
				}
				class2titles.get(cName).add(title);
			}
		}
		
		
		for(String c : clones.keySet()) {
			Set<String> distinctComponents = new HashSet<String>();
			int sigma=0;
			
			for(Game game : games.values()) {
				ClassModel cm = game.getClassModel(c);
				if(cm!=null) {
					sigma++;
//					distinctComponents.add(c);
					distinctComponents.add(game.getTitle() + c);
					List<MethodModel> mms = cm.getMethods();
					for(MethodModel mm : cm.getMethods()) {
						sigma++;
						distinctComponents.add(mm.getSignature());
					}
				}
			}
			
			int d = distinctComponents.size();
			if(sigma == 1) {
				d = 2;
				sigma = 2;	
			}
			double tcci = 1.0 - ( (d-1.0) / (sigma-1.0) );
			tccis.put(c, tcci);
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
					String s = String.format("%-45s [%2d] : %3.2f", key, clones.get(key).size(), tccis.get(key));
					System.out.println(s);
				}
			}
		}
	}
	
	public void printMatrix() {
		System.out.println("Call Relation :: " + overlapedCallMap.size());
		this.printAdjacencyMatrix(overlapedCallMap);
		System.out.println("Extend Relation :: " + overlapedExtendMap.size());
		this.printAdjacencyMatrix(overlapedExtendMap);
		System.out.println("Implement Relation :: " + overlapedImplementMap.size());
		this.printAdjacencyMatrix(overlapedImplementMap);
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
				if(class2titles.get(item).size() == i) {
					int pos = item.lastIndexOf('.');
					String pName = item.substring(0, pos);
					String cName = item.substring(pos+1);
					String s = String.format("%-20s %-30s %2d", pName, cName, i);
					System.out.println(s);
				}
			}
		}
		System.out.println("------------------------------------------------------");
		String s = String.format("OVERLAP || Poduct:%d, Clone:%d, Reuse:%d", gameTitles.size(), clones.size(), overlapedClasses.size());
		System.out.println(s);
		System.out.println("------------------------------------------------------");
	}

	public void printClasses() {
		for(String cName : overlapedClasses.keySet()) {
			String fn = class2titles.get(cName).size() + "/" + gameTitles.size();
			String s = String.format("%5s :: %s", fn, cName);
			System.out.println(s);
			overlapedClasses.get(cName).print();
		}
	}
}


