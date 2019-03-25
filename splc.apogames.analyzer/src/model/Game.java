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
	private Set<ClassModel> allClasses;
	private Set<ClassModel> writtenClasses;
	private Set<ClassModel> clonedClasses;
	private Set<ClassModel> reusedClasses;
	private Set<String> callRelations;
	private Set<String> extendRelations;
	private Set<String> implementsRelations;
	
	public Set<ClassModel> getClonedClasses() {
		return this.clonedClasses;
	}
	
	public Set<ClassModel> getReusedClasses() {
		return this.reusedClasses;
	}
	
	public void print() {
		for(List<ClassModel> list : architecture.values()) {
			for(ClassModel cm : list) {
				System.out.println(cm.toString());
			}
		}
	}
	
	public Game(String title, Map<String, List<ClassModel>> architecture) {
		this.title = title;
		this.architecture = architecture;
		this.allClasses = new HashSet<ClassModel>();
		this.writtenClasses = new HashSet<ClassModel>();
		this.clonedClasses = new HashSet<ClassModel>();
		this.reusedClasses = new HashSet<ClassModel>();
		this.callRelations = new HashSet<String>();
		this.extendRelations = new HashSet<String>();
		this.implementsRelations = new HashSet<String>();
		
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
			
			callRelations.addAll( getRelations(imports, mClass.getPath(), mClass.getAllVariables()) );
			extendRelations.addAll( getRelations(imports, mClass.getPath(), mClass.getExtends()) );
			implementsRelations.addAll( getRelations(imports, mClass.getPath(), mClass.getImplements()) );
			
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
	}

	private Set<String> getRelations(List<String> imports, String from, Set<String> set) {
		Set<String> relations = new HashSet<String>();
		
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
					
					relations.add(relation);
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
	
	public Set<String> getCallRelations() {
		return this.callRelations;
	}
	
	public Set<String> getExtendRelations() {
		return this.extendRelations;
	}

	public Set<String> getImplementRelations() {
		return this.implementsRelations;
	}
}

