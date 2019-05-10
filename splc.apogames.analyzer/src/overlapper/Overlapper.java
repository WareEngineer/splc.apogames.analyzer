package overlapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.ClassModel;
import model.Game;
import model.MethodModel;
import model.OverlapedArchitecture;

public class Overlapper {
	private OverlapedArchitecture olArch = new OverlapedArchitecture();
	
	public Overlapper(Map<String, Game> games) {
		for(String title : games.keySet()) {
			Game game = games.get(title);

			olArch.addTitle(title);
			olArch.addCallRelationInfo(title, game.getCallRelations());
			olArch.addExtendRelationInfo(title, game.getExtendRelations());
			olArch.addImplementRelationInfo(title, game.getImplementRelations());
			
			for(ClassModel cm : game.getClonedClasses()) {
				olArch.addCloneInfo(title, cm);
			}
			
			for(ClassModel cm : game.getReusedClasses()) {
				olArch.addReuseInfo(title, cm);
			}
		}
		
		this.setTcci(games);
	}

	public OverlapedArchitecture getOverlapedArchitecture() {
		return olArch;
	}

	private void setTcci(Map<String, Game> games) {
		Map<String, Double> tccis = new HashMap<String, Double>();
		Map<String, Set<String>> reuseInfo = olArch.getReuseInfo();
		for(String cName : reuseInfo.keySet()) {
			Set<String> distinctComponents = new HashSet<String>();
			int sigma=0;
			
			for(String title : reuseInfo.get(cName)) {
				Game game = games.get(title);
				ClassModel cm = game.getClassModel(cName);
				if(cm!=null) {
					sigma++;
					distinctComponents.add(game.getTitle() + cName);
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
					tccis.put(cName, tcci);
				}
			}
		}
		olArch.setTcciInfo(tccis);
	}
	
	public void printTcci() {
		Map<String, Double> tccis = olArch.getTcciInfo();
		List<String> keys = new ArrayList<String>(tccis.keySet());
		Collections.sort(keys);
		Set<Double> set = new HashSet<Double>(tccis.values());
		List<Double> values = new ArrayList<Double>(set);
		Collections.sort(values);
		
		for(Double value : values) {
			for(String key : keys) {
				if(tccis.get(key).equals(value)) {
					String s = String.format("%-45s [%2d] : %3.2f", key, olArch.getReuseInfo().get(key).size(), tccis.get(key));
					System.out.println(s);
				}
			}
		}
	}
	
	public void printMatrix() {
		System.out.print("Call Relation :: ");
		this.printAdjacencyMatrix(olArch.getCallRelations());
		System.out.print("Extend Relation :: ");
		this.printAdjacencyMatrix(olArch.getExtendRelations());
		System.out.print("Implement Relation :: ");
		this.printAdjacencyMatrix(olArch.getImplementRelations());
	}
	
	private void printAdjacencyMatrix(Map<String, Set<String>> relations) {
		List<String> nodes = new ArrayList<String>( olArch.getClassInfo().keySet() );
		nodes.add("#GAME");
		Collections.sort(nodes);
		
		System.out.println(relations.size());
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
		List<String> list = new ArrayList<String>(olArch.getClassInfo().keySet()); 
		Collections.sort(list);
		
		for(int i=0; i<=olArch.getTitleInfo().size(); i++) {
			for(String item : list) {
				if(olArch.getReuseInfo().get(item).size() == i) {
					int pos = item.lastIndexOf('.');
					String pName = item.substring(0, pos);
					String cName = item.substring(pos+1);
					String s = String.format("%-20s %-30s %2d", pName, cName, i);
					System.out.println(s);
				}
			}
		}
		System.out.println("------------------------------------------------------");
		String s = String.format("OVERLAP || Poduct:%d, Clone:%d, Reuse:%d", olArch.getTitleInfo().size(), olArch.getCloneInfo().size(), olArch.getReuseInfo().size());
		System.out.println(s);
		System.out.println("------------------------------------------------------");
	}

	public void printArchitecture() {
		for(String cName : olArch.getClassInfo().keySet()) {
			String fn = olArch.getReuseInfo().get(cName).size() + "/" + olArch.getTitleInfo().size();
			String s = String.format("%5s :: %s", fn, cName);
			System.out.println(s);
			olArch.getClassInfo().get(cName).print();
		}
	}
}
