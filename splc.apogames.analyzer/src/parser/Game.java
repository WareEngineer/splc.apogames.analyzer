package parser;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Game {
	private final String title;
	private final Map<String, List<ClassModel>> architecture;
	private Set<ClassModel> allClasses;
	private Set<ClassModel> realizedClasses;
	private Set<ClassModel> reusedClasses;
	private Set<ClassModel> invokedClassesReal2Reuse;
	
	public Set<ClassModel> getAllClassNames() {
		return allClasses;
	}
	
	public Game(String title, Map<String, List<ClassModel>> architecture) {
		this.title = title;
		this.architecture = architecture;
		this.allClasses = new HashSet<ClassModel>();
		this.realizedClasses = new HashSet<ClassModel>();
		this.reusedClasses = new HashSet<ClassModel>();
		
		for(String packageName : architecture.keySet()) {
			List<ClassModel> classes = architecture.get(packageName);
			for (ClassModel mClass : classes) {
				allClasses.add(mClass);
				if (packageName.startsWith("org.")) {
					reusedClasses.add(mClass);
				} else {
					realizedClasses.add(mClass);
				}
			}
		}
		
		Set<String> usedClasses = new HashSet<String>();
		for(ClassModel mClass : realizedClasses) {
			List<String> importedOrgClasses = mClass.getImports().stream().filter(name -> name.startsWith("org.")).collect(Collectors.toList());
			Set<String> usedType = mClass.getRelations();
			
			for (String imp : importedOrgClasses) {
				String type = imp.substring( imp.lastIndexOf(".") + 1 );
				System.out.println(type);
				if ( usedType.contains(type) ) {
					usedClasses.add(imp);
				}
			}
//			usedClasses.addAll(importedOrgClasses);
		}
		
		String s = String.format("***%-20s  [REALIZATION: %2d, REUSED:%2d, ALL:%2d]", this.title, this.realizedClasses.size(), this.reusedClasses.size(), this.allClasses.size());
		System.out.println(s);
		System.out.println("   => " + usedClasses.toString());
	}
}