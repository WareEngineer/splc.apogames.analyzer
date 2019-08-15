
import java.util.Map;

import builder.Builder;
import grapher.Adapter;
import grapher.GraphInfo;
import grapher.Grapher;
import model.Game;
import model.OverlapedArchitecture;
import overlapper.Overlapper;

public class Main {
	static String commonPath = "C:/Users/user/eclipse-workspace/Java/";
//	static String commonPath = "C:/Users/user/eclipse-workspace/SPLtest/";
	
	public static void main(String[] args) {
		Builder builder = new Builder(commonPath);
		builder.printSummary();
//		builder.printArchitecture();
		Map<String, Game> games = builder.getArchitectures();
		
		Overlapper overlapper = new Overlapper(games);
		overlapper.printReuseFrequency();
//		overlapper.printArchitecture();
		overlapper.printMatrix();
		overlapper.printTcci();
		OverlapedArchitecture olArch = overlapper.getOverlapedArchitecture();
		
		Adapter adapter = new Adapter(olArch);
//		GraphInfo graphInfo = adapter.getGraphInfoOfClassLevel();
		GraphInfo graphInfo = adapter.getGraphInfoOfPackageLevel();
		
		Grapher grapher = new Grapher();
		grapher.setGraph(graphInfo);
		grapher.draw();
	}
}
