package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Game {
	private final String title;
	private final Map<String, List<ClassModel>> architecture;
	private Set<String> allClassPaths;
	private Set<ClassModel> allClasses;
	private Set<ClassModel> writtenClasses;
	private Set<ClassModel> clonedClasses;
	private Set<ClassModel> reusedClasses;
	private Map<String, Set<String>> callMap;
	private Map<String, Set<String>> extendMap;
	private Map<String, Set<String>> implementMap;
	private List<String> gPaths;
	private Set<String> cNames;
	
	public Set<ClassModel> getClonedClasses() {
		return this.clonedClasses;
	}
	
	public Set<ClassModel> getReusedClasses() {
		return this.reusedClasses;
	}
	
	public Set<String> getOrgClassNames() {
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
		this.callMap = new HashMap<String, Set<String>>();
		this.extendMap = new HashMap<String, Set<String>>();
		this.implementMap = new HashMap<String, Set<String>>();
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
			
//			System.out.println("####" + mClass.getPath());
//			System.out.println(mClass.getAttribute());
//			System.out.println(mClass.getStaticInstances().toString());
			
			List<String> imports = new ArrayList<String>();
			imports.addAll(mClass.getImports());
			imports.addAll(mClass.getImplicitImports());
			
			this.callMap.putAll( getRelations(imports, mClass.getPath(), mClass.getAllVariables())) ;
			this.extendMap.putAll( getRelations(imports, mClass.getPath(), mClass.getExtends()) );
			this.implementMap.putAll( getRelations(imports, mClass.getPath(), mClass.getImplements()) );
			
			for(String type : mClass.getAllUsedTypes()) {
				String suffix = "." + type;

				for(String path : imports) {
					if(path.endsWith(suffix)) {
						if(!visitedPath.contains(path)) {
							ClassModel cm = this.getClassModel(path);
							if(cm!=null) {
								queue.offer(cm);
								if(path.startsWith("org.")) {
									reusedClasses.add(cm);
								}
							}
						}
						break;	// 명시적 선언이 묵시적 선언보다 우선함
					}
				}
			}
		}
		
		for(String packageName : architecture.keySet()) {
			if(packageName.contains("org.") == false) {
				continue;
			}
			for(ClassModel classModel : architecture.get(packageName)) {
				cNames.add(packageName+"."+classModel.getClassName());
				if(classModel.getMethods().isEmpty()) {
					gPaths.add(this.title+"<-"+packageName+"."+classModel.getClassName());
				} else {
					for(MethodModel methodModel : classModel.getMethods()) {
						gPaths.add(this.title+"<-"+packageName+"."+classModel.getClassName()+"<-"+methodModel.getSignature());
					}
				}
			}
		}
	}

	private Map<String, Set<String>> getRelations(List<String> imports, String from, Set<String> set) {
		Map<String, Set<String>> relations = new HashMap<String, Set<String>>();
		
		for(String type : set) {
			String suffix = "." + type;

			for(String to : imports) {
				if(to.startsWith("org.") && to.endsWith(suffix)) {
					String relation;
					if(from.startsWith("org.")) {
						relation = from + "->" + to;
					} else {
						relation = "#GAME" + "->" + to;
					}
					
					if(relations.containsKey(relation)==false) {
						relations.put(relation, new HashSet<String>());
					}
					relations.get(relation).add(title);
					break; 	// 명시적 선언이 묵시적 선언보다 우선함
				}
			}
		}
		
		return relations;
	}
	
	public ClassModel getClassModel(String path) {
		int pos = path.lastIndexOf('.');
		String pName = path.substring(0, pos);
		String cName = path.substring(pos+1);
		
		if(architecture.containsKey(pName)) {
			for(ClassModel cm : architecture.get(pName)) {
				if(path.equals( cm.getPath() )) {
					return cm;
				}
			}
		}
		
		return null;
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
	
	public String getTitle() {
		return this.title;
	}
	
	public Map<String, Set<String>> getCallMap() {
		return this.callMap;
	}
	
	public Map<String, Set<String>> getExtendMap() {
		return this.extendMap;
	}

	public Map<String, Set<String>> getImplementMap() {
		return this.implementMap;
	}
}

