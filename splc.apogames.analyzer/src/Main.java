
import java.util.List;
import java.util.Map;
import java.util.Set;

import architecture.OverlapedArchitecture;
import builder.FileManager;
import grapher.Adapter;
import grapher.GraphInfo;
import grapher.Grapher;
import product.Product;
import product.ProductPool;

public class Main {
	static String commonPath = "C:/Users/user/Desktop/연구 과제/계층형 가변성 모델링 및 검증 방법 연구/ApoGames/Java/";
//	static String commonPath = "C:/Users/user/Desktop/연구 과제/계층형 가변성 모델링 및 검증 방법 연구/ApoGames/SPLtest/";
	
	public static void main(String[] args) {
		ProductPool productPool = new ProductPool();

		List<String> productNames = FileManager.getDirectoryNames(commonPath);
		for(String pName : productNames) {
			String path = commonPath + pName;
			Product product = new Product(path);
			product.printSummary();
//			product.printDetail();
			productPool.add(pName, product);
		}
		
//		productPool.printSummary();
//		productPool.printMatrix();
//		productPool.printBoundary();
		
		Set<String> targets = productPool.getReusedClassNames();
		Map<String, Double> tccis = productPool.getHTTCI(targets);
		OverlapedArchitecture olArchitecture = productPool.getOverlapedArchitecture(targets);
//		olArchitecture.printSummary();
		
		Adapter adapter = new Adapter();
		GraphInfo graphInfo = adapter.getGraphInfo(olArchitecture, tccis);
		
		Grapher grapher = new Grapher();
		grapher.setGraph(graphInfo);
		grapher.draw();
	}
}
