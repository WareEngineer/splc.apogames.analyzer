package parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Game {
	private final String title;
	private final Map<String, List<ClassModel>> architecture;
	private Set<ClassModel> allClasses;
	private Set<ClassModel> writtenClasses;
	private Set<ClassModel> clonedClasses;
	private Set<String> reusedClasses;
	
	public Set<String> getReusedClasses() {
		return reusedClasses;
	}
	
	public Game(String title, Map<String, List<ClassModel>> architecture) {
		this.title = title;
		this.architecture = architecture;
		this.allClasses = new HashSet<ClassModel>();
		this.writtenClasses = new HashSet<ClassModel>();
		this.clonedClasses = new HashSet<ClassModel>();
		
		for(String packageName : architecture.keySet()) {
			List<ClassModel> classes = architecture.get(packageName);
			for (ClassModel mClass : classes) {
				allClasses.add(mClass);
				if (packageName.startsWith("org.")) {
					clonedClasses.add(mClass);
				} else {
					writtenClasses.add(mClass);
				}
			}
		}
		
		setReusedClasses();
	}
	
	private void setReusedClasses() {
		Queue<ClassModel> queue = new LinkedList<ClassModel>();
		queue.addAll(writtenClasses);
		reusedClasses = new HashSet<String>();
		
		while( !queue.isEmpty() ) {
			ClassModel mClass = queue.poll();
			Set<String> importStatements = mClass.getImports(); //.stream().filter(name -> name.startsWith("org.")).collect(Collectors.toSet());
			Set<String> usedTypes = mClass.getRelations();
			
			Set<String> implicitImportTypes = new HashSet<String>(usedTypes);
			for(String statement : importStatements) {
				int beginIndex = statement.lastIndexOf('.') + 1;
				String className = statement.substring(beginIndex);
				if ( usedTypes.contains(className) ) {
					implicitImportTypes.remove(className);
				}
			}
			
			for(ClassModel c : architecture.get(mClass.getPackageName())) {
				String pName = c.getPackageName();
				String cName = c.getClassName();
				if(implicitImportTypes.contains(cName)) {
					importStatements.add( pName+"."+cName );
				}
			}
			
			Set<String> extractedImportStatements = importStatements.stream()
																	.filter(name -> name.startsWith("org."))
																	.collect(Collectors.toSet());

//			System.out.println(mClass.getPackageName()+" : "+mClass.getClassName());
			for(String statement : extractedImportStatements) {
//				System.out.println("\t" + statement);
				if( !reusedClasses.contains(statement) ) {
					int pos = statement.lastIndexOf('.');
					String pName = statement.substring(0, pos);
					String cName = statement.substring(pos+1);
					for(ClassModel c : architecture.get(pName)) {
						if(c.getClassName().equals(cName)) {
							queue.offer(c);
						}
					}
					reusedClasses.add(statement);
				}
			}
			
//			System.out.println(mClass.getPackageName() + ":" + mClass.getClassName() + "->");
		}
	}
	
	public String toString() {
		String s = String.format("***%-20s  [WRITTEN:%3d, CLONED:%3d, ALL:%3d]  => REUSED:%d ", 
								 this.title, 
								 this.writtenClasses.size(), 
								 this.clonedClasses.size(), 
								 this.allClasses.size(), 
								 reusedClasses.size()
								);
		
		return s;
	}
}




//
//reusedClasses = new HashSet<String>();
//
//for (ClassModel mClass : writtenClasses) {
//	Set<String> importStatements = mClass.getImports().stream().filter(name -> name.startsWith("org.")).collect(Collectors.toSet());
//	
//	// import문에서 * 사용한 경우 처리
//	for (String s : importStatements) {
//		if ( s.endsWith(".*") ) {
//			int pos = s.lastIndexOf(".");
//			String pName = s.substring(0, pos);
//			
//			List<ClassModel> classList = architecture.get(pName);
//			for (ClassModel c : classList) {
//				String statement = pName + c.getClassName();
//				importStatements.add(statement);
//			}
//		}
//		
//	}
//	
//	reusedClasses.addAll(importStatements);
//}





















