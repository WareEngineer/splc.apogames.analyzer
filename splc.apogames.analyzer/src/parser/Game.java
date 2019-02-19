package parser;

import java.util.ArrayList;
import java.util.HashMap;
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
	private Set<String> allClassPaths;
	private Set<ClassModel> allClasses;
	private Set<ClassModel> writtenClasses;
	private Set<ClassModel> clonedClasses;
	private Set<ClassModel> reusedClasses;
	private Map<String, List<String>> relations;
	private List<String> gPaths;
	private Set<String> cNames;
	
	public Set<ClassModel> getReusedClasses() {
		return this.reusedClasses;
	}
	
	public Map<String, List<String>> getRelations() {
		return this.relations;
	}
	
	public Set<String> getClassNames() {
		return this.cNames;
	}
	
	public List<String> getGraphPaths() {
		return this.gPaths;
	}
	
	public Game(String title, Map<String, List<ClassModel>> architecture) {
		this.title = title;
		this.architecture = architecture;
		this.allClassPaths = new HashSet<String>();
		this.allClasses = new HashSet<ClassModel>();
		this.writtenClasses = new HashSet<ClassModel>();
		this.clonedClasses = new HashSet<ClassModel>();
		this.reusedClasses = new HashSet<ClassModel>();
		this.relations = new HashMap<String, List<String>>();
		this.gPaths = new ArrayList<String>();
		this.cNames = new HashSet<String>();
		
		for(String packageName : architecture.keySet()) {
			List<ClassModel> classes = architecture.get(packageName);
			for (ClassModel mClass : classes) {
				allClasses.add(mClass);
				allClassPaths.add(mClass.getPath());
				if (packageName.startsWith("org.")) {
					clonedClasses.add(mClass);
				} else {
					writtenClasses.add(mClass);
				}
			}
		}
		
		Queue<ClassModel> queue = new LinkedList<ClassModel>();
		Set<String> visitedPath = new HashSet<String>();
		queue.addAll(writtenClasses);
		
		while( !queue.isEmpty() ) {
			ClassModel mClass = queue.poll();
			visitedPath.add(mClass.getPath());
			
			Set<String> paths = this.getRelationFromClassToPackage(mClass, "org.");
			for(String path : paths) {
				if( mClass.getPath().startsWith("org.") ) {
					if( !relations.containsKey(mClass.getPath()) ) {
						relations.put(mClass.getPath(), new ArrayList<String>());
					}
					relations.get(mClass.getPath()).add(path);
				} else {	// 구현 클래스에서 org 클래스를 호출하는 경우
					if( !relations.containsKey("#GAME") ) {
						relations.put("#GAME", new ArrayList<String>());
					}
					relations.get("#GAME").add(path);
				}
				
				if( !visitedPath.contains(path) ) {
					int pos = path.lastIndexOf('.');
					String pName = path.substring(0, pos);
					String cName = path.substring(pos+1);
					for(ClassModel c : architecture.get(pName)) {
						if(c.getClassName().equals(cName)) {
							queue.offer(c);
							reusedClasses.add(c);
						}
					}
				}
			}
		}
		
		for(String packageName : architecture.keySet()) {
			for(ClassModel classModel : architecture.get(packageName)) {
				cNames.add(classModel.getClassName());
				if(classModel.getMethods().isEmpty()) {
					gPaths.add(this.title+"<-"+packageName+"<-"+classModel.getClassName());
				} else {
					for(MethodModel methodModel : classModel.getMethods()) {
						gPaths.add(this.title+"<-"+packageName+"<-"+classModel.getClassName()+"<-"+methodModel.getSignature());
					}
				}
			}
		}
	}
	
	private Set<String> getRelationFromClassToPackage(ClassModel from, String to) {
		Set<String> importStatements = from.getImports();
		Set<String> usedTypes = from.getRelations();
		
		Set<String> implicitImportTypes = new HashSet<String>(usedTypes);
		for( String statement : importStatements ) {
			int beginIndex = statement.lastIndexOf('.') + 1;
			String className = statement.substring(beginIndex);
			if( implicitImportTypes.contains(className) ) {
				implicitImportTypes.remove(className);
			}
		}
		
		for(String type : implicitImportTypes) {
			String statement = from.getPackageName() + "." + type;
			if(allClassPaths.contains(statement)) {
				importStatements.add(statement);
			}
		}
		
		return importStatements.stream().filter(name -> name.startsWith(to)).collect(Collectors.toSet());
	}

	public String toString() {
		String s = String.format("***%-15s  [WRITTEN:%3d, CLONED:%3d, ALL:%3d]  => REUSED:%2d,  UNUSED:%2d", 
								 this.title, 
								 this.writtenClasses.size(), 
								 this.clonedClasses.size(), 
								 this.allClasses.size(), 
								 this.reusedClasses.size(),
								 this.clonedClasses.size() - this.reusedClasses.size()
								);
		
		return s;
	}
}





















