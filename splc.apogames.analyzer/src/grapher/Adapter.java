package grapher;

import java.util.Map;
import java.util.Set;

import architecture.OverlapedArchitecture;
import product.RelationPool;

public class Adapter {
	
	public GraphInfo getGraphInfo(OverlapedArchitecture olArchitecture, Map<String, Double> tccis) {
		GraphInfo graphInfo = new GraphInfo();
		Set<String> targetClassNames = olArchitecture.getClassNames();
		Set<String> entries = olArchitecture.getEntries();
		Set<String> exits = olArchitecture.getExits();
		for(String className : targetClassNames) {
			String stereoType = null;
			double tcci = tccis.get(className);
			
			boolean isEntry = entries.contains(className);
			boolean isExit = exits.contains(className);
			if(isEntry && isExit) {
				graphInfo.addBoundaryNode(GraphBoundaryNode.BOTH, stereoType, className, tcci);
			} else if (isEntry) {
				graphInfo.addBoundaryNode(GraphBoundaryNode.ENTRY, stereoType, className, tcci);
			} else if (isExit) {
				graphInfo.addBoundaryNode(GraphBoundaryNode.EXIT, stereoType, className, tcci);
			} else {
				graphInfo.addNormalNode(stereoType, className, tcci);
			}
		}
		
		for(String className : targetClassNames) {
			RelationPool relationPool = olArchitecture.getUniRelations();
			for(String to : relationPool.getCallee(className)) {
				double p_rate = (double)olArchitecture.getClassIndidence(className)/olArchitecture.getNumOfProducts();
				if(targetClassNames.contains(to)) {
					graphInfo.addEdge(className, to, p_rate);
				}
			}
		}
		
		return graphInfo;
	}
}
