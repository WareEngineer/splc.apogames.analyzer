package grapher;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.OverlapedArchitecture;
import model.OverlapedClass;

public class Adapter {
	private OverlapedArchitecture overlapedArchitecture;
	
	public Adapter(OverlapedArchitecture overlapedArchitecture) {
		this.overlapedArchitecture = overlapedArchitecture;
	}
	
	public GraphInfo getGraphInfoOfClassLevel() {
		GraphInfo graphInfo = new GraphInfo();
		
		Map<String, Set<String>> callRelations = overlapedArchitecture.getCallRelations();
		Map<String, OverlapedClass> classes = overlapedArchitecture.getClassInfo();
		Map<String, Double> tccis = overlapedArchitecture.getTcciInfo();
		
		graphInfo.addNodeInfo("#GAME");
		for(String cName : classes.keySet()) {
			OverlapedClass c = classes.get(cName);
			String stereoType = c.getType();
			double tcci = tccis.get(cName);
			graphInfo.addNodeInfo(stereoType, cName, tcci);
		}
		
		for(String relation : callRelations.keySet()) {
			String[] tokens = relation.split("->");
			String from = tokens[0];
			String to = tokens[1];
			
			double reuseRatio;
			if(tokens[0].equals("#GAME")) {
				reuseRatio = 1.0*callRelations.get(relation).size()/overlapedArchitecture.getTitleInfo().size();
//				continue;
			} else {
				reuseRatio = 1.0*callRelations.get(relation).size()/classes.get(tokens[0]).getTitles().size();
//				continue;
			}
			
			graphInfo.addEdgeInfo(from, to, reuseRatio);
		}
		
		return graphInfo;
	}

	public GraphInfo getGraphInfoOfPackageLevel() {
		GraphInfo graphInfo = new GraphInfo();
		Map<String, Integer> classCount = new HashMap<String, Integer>();
		Map<String, Double> sumTcci = new HashMap<String, Double>();
		Map<String, Integer> relationCount = new HashMap<String, Integer>();
		Map<String, Double> sumResueRatio = new HashMap<String, Double>();
		
		Map<String, Set<String>> callRelations = overlapedArchitecture.getCallRelations();
		Map<String, OverlapedClass> classes = overlapedArchitecture.getClassInfo();
		Map<String, Double> tccis = overlapedArchitecture.getTcciInfo();
		
		graphInfo.addNodeInfo("#GAME");
		for(String cName : classes.keySet()) {
			String pName = cName.substring(0, cName.lastIndexOf("."));
			if(classCount.containsKey(pName)==false) {
				classCount.put(pName, 0);
			}
			classCount.replace(pName, classCount.get(pName)+1);

			OverlapedClass c = classes.get(cName);
			double tcci = tccis.get(cName);
			if(sumTcci.containsKey(pName)==false) {
				sumTcci.put(pName, 0.0);
			}
			sumTcci.replace(pName, sumTcci.get(pName)+tcci);
		}
		for(String pName : sumTcci.keySet()) {
			double meanTcci = sumTcci.get(pName)/classCount.get(pName);
			graphInfo.addNodeInfo("package", pName, meanTcci);
		}
		
		for(String relation : callRelations.keySet()) {
			String[] tokens = relation.split("->");
			String from = tokens[0];
			String to = tokens[1];
			
			double reuseRatio;
			if(tokens[0].equals("#GAME")) {
				reuseRatio = 1.0*callRelations.get(relation).size()/overlapedArchitecture.getTitleInfo().size();
//				continue;
			} else {
				reuseRatio = 1.0*callRelations.get(relation).size()/classes.get(tokens[0]).getTitles().size();
//				continue;
			}
			
			String fromPackage = from;
			if("#GAME".equals(fromPackage)==false) {
				fromPackage = from.substring(0, from.lastIndexOf("."));
			}
			String toPackage = to;
			if("#GAME".equals(toPackage)==false) {
				toPackage = to.substring(0, to.lastIndexOf("."));
			}
			String pRelation = fromPackage+"->"+toPackage;
			if(relationCount.containsKey(pRelation)==false) {
				relationCount.put(pRelation, 0);
			}
			relationCount.replace(pRelation, relationCount.get(pRelation)+1);

			if(sumResueRatio.containsKey(pRelation)==false) {
				sumResueRatio.put(pRelation, 0.0);
			}
			sumResueRatio.replace(pRelation, sumResueRatio.get(pRelation)+reuseRatio);
		}
		for(String relation : sumResueRatio.keySet()) {
			String[] tokens = relation.split("->");
			String from = tokens[0];
			String to = tokens[1];
			
			double meanReuseRatio = sumResueRatio.get(relation)/relationCount.get(relation);
			graphInfo.addEdgeInfo(from, to, meanReuseRatio);
		}
		
		return graphInfo;
	}
}
