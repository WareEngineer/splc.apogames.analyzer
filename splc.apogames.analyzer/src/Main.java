
import java.util.Map;

import builder.Builder;
import grapher.Adapter;
import grapher.Grapher;
import model.Game;
import model.OverlapedArchitecture;
import overlapper.Overlapper;

public class Main {
	static String commonPath = "C:/Users/user/eclipse-workspace/Java/";

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
		
		Grapher grapher = new Grapher();
		grapher.setNodes(adapter.getNodes());
		grapher.setEdges(adapter.getEdges());
		grapher.draw();
	}
}
