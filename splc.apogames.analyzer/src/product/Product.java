package product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import builder.Composer;
import builder.FileManager;
import builder.Parser;
import builder.Token;
import builder.Tokenizer;
import model.ClassModel;
import model.MethodModel;

public class Product {
	private String path;
	private Map<String, ClassModel> classes = new HashMap<String, ClassModel>(); // <fullName, classModel>
	private Set<String> mainClassNames = new HashSet<String>(); // list of classes with main function
	private Set<String> usedClassNames = new HashSet<String>();
	private Set<String> constantClassNames = new HashSet<String>();
	private Relation callRelations = new Relation(); // "callerFullName->calleeFullName"
	private Relation extendRelations = new Relation(); // "callerFullName->calleeFullName"
	private Relation implementsRelations = new Relation(); // "callerFullName->calleeFullName"
	
	public String getPath() { return this.path; }
	public Set<String> getClassNames() { return this.classes.keySet(); }
	public Set<String> getMainClassNames() { return this.mainClassNames; }
	public Set<String> getUsedClassNames() { return this.usedClassNames; }
	public Set<String> getConstantClassNames() { return this.constantClassNames; }
	public Relation getUsedCallRelations() { return this.getUsedRelationFrom(callRelations); }
	public Relation getUsedExtendRelations() { return this.getUsedRelationFrom(extendRelations); }
	public Relation getUsedImplementRelations() { return this.getUsedRelationFrom(implementsRelations); }
	public ClassModel getClassModel(String name) { return this.classes.get(name); }
	
	private Relation getUsedRelationFrom(Relation relation) {
		Relation usedRelation = new Relation();
		for(String from : usedClassNames) {
			for(String to : relation.getCallee(from)) {
				usedRelation.add(from, to);
			}
		}
		return usedRelation;
	}
	
	public void printSummary() {
		int cs = classes.size();
		int ucs = usedClassNames.size();
		int crs = callRelations.size();
		int ers = extendRelations.size();
		int irs = implementsRelations.size();
		String[] pathTokens = path.split("/");
		String name = pathTokens[pathTokens.length-1];
		String msg = String.format("%-20s [TOTAL:%3d, USED:%3d] & [CALL:%3d, EXTEND:%3d, IMPLEMENT:%3d]", name, cs, ucs, crs, ers, irs);
		System.out.println(msg);
	}
	
	public void printDetail() {
		System.out.println("\t# main class list");
		for(String className : mainClassNames) {
			System.out.println("\t\t-> " + className);
		}
		System.out.println("\t# non-used class list");
		List<String> nonUsedClassNames = new ArrayList<String>();
		for(String className : classes.keySet()) {
			if(usedClassNames.contains(className) == false) {
				nonUsedClassNames.add(className);
			}
		}
		Collections.sort(nonUsedClassNames);
		for(String className : nonUsedClassNames) {
			System.out.println("\t\t-> " + className);
		}
	}
	
	public Product(String path) {
		List<String> javaSourceFiles = FileManager.getAllJavaSourceFile(path);
		Map<String, Set<String>> implicitImportMap = new HashMap<String, Set<String>>();
		Tokenizer tokenizer = new Tokenizer();
		Composer composer = new Composer();
		Parser parser = new Parser();
		
		this.path = path;
		for (String sourceFile : javaSourceFiles) {
			List<Token> tokens = tokenizer.getTokens(sourceFile);
			List<Map<String, Object>> chunks = composer.getChunks(tokens);
			List<ClassModel> classList = parser.getComponent(chunks);	// processing the nested class
			
			for(ClassModel classModel : classList) {
				String packageName = classModel.getPackageName();
				String fullName = classModel.getFullName();
				classes.put(fullName, classModel);
				if(implicitImportMap.containsKey(packageName) == false) {
					implicitImportMap.put(packageName, new HashSet<String>());
				}
				Set<String> implicitImports = implicitImportMap.get(packageName);
				implicitImports.add(fullName);
				classModel.setImplicitImports(implicitImports);
				
				if(classModel.getMethods().isEmpty()) {
					constantClassNames.add(classModel.getFullName());
				}
				
				for(MethodModel methodModel : classModel.getMethods()) {
					String methodName = methodModel.getId();
					if( "main".equals(methodName) ) {
						mainClassNames.add(classModel.getFullName());
					}
				}
			}
		}
		
		for(ClassModel classModel : classes.values()) {
			callRelations.addAll( getRelations(classModel, classModel.getAllVariables()) );
			extendRelations.addAll( getRelations(classModel, classModel.getExtends()) );
			implementsRelations.addAll( getRelations(classModel, classModel.getImplements()) );
		}
		
		Queue<String> queue = new LinkedList<String>();
		queue.addAll(mainClassNames);
		while(queue.isEmpty() == false) {
			String fromClassName = queue.poll();
			if( classes.containsKey(fromClassName) ) {
				usedClassNames.add(fromClassName);
				Set<String> toClassNameSet = new HashSet<String>();
				toClassNameSet.addAll( callRelations.getCallee(fromClassName) );
				toClassNameSet.addAll( extendRelations.getCallee(fromClassName) );
				toClassNameSet.addAll( implementsRelations.getCallee(fromClassName) );
				for(String toClassName : toClassNameSet) {
					boolean isNotVisited = !usedClassNames.contains(toClassName);
					if(isNotVisited) {
						queue.offer(toClassName);
					}
				}
			}
		}
	}
	
	private Relation getRelations(ClassModel classModel, Set<String> list) {
		Relation relations = new Relation();
		for(String e : list) {
			List<String> imports = classModel.getImports();
			for(String i : imports) {
				boolean single = i.contains(".")==false && i.equals(e);
				boolean multiple = i.contains(".")==true && i.endsWith("."+e);
				if(single || multiple) {
					String from = classModel.getFullName();
					String to = i;
					relations.add(from, to);
					break;
				}
			}
		}
		return relations;
	}
}
