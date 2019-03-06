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
	private Map<String, OverlapedClass> overlapedClasses;
	private Map<String, Set<String>> overlapedCallMap;
	private Map<String, Set<String>> overlapedExtendMap;
	private Map<String, Set<String>> overlapedImplementMap;
	private Map<String, Set<String>> class2titles;
	
	public OverlapedArchitecture(Map<String, Game> games) {
		gameTitles = new HashSet<String>();
		overlapedClasses = new HashMap<String, OverlapedClass>();
		overlapedCallMap = new HashMap<String, Set<String>>();
		overlapedExtendMap = new HashMap<String, Set<String>>();
		overlapedImplementMap = new HashMap<String, Set<String>>();
		class2titles = new HashMap<String, Set<String>>();
		
		for(String title : games.keySet()) {
			gameTitles.add(title);
			Game game = games.get(title);
			
			overlapedCallMap.putAll(game.getCallMap());
			overlapedExtendMap.putAll(game.getExtendMap());
			overlapedImplementMap.putAll(game.getImplementMap());

//			Set<ClassModel> classes = game.getClonedClasses();	// 80개
			Set<ClassModel> classes = game.getReusedClasses();	// 39개 	[수정전 : 42개] - 검토필
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


