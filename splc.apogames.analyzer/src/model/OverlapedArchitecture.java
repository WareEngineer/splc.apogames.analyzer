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
	private Map<String, Set<String>> class2titles;
	
	public OverlapedArchitecture(Map<String, Game> games) {
		gameTitles = new HashSet<String>();
		overlapedClasses = new HashMap<String, OverlapedClass>();
		class2titles = new HashMap<String, Set<String>>();
		
		for(String title : games.keySet()) {
			gameTitles.add(title);
			Game game = games.get(title);

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


