package builder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {
	public static List<String> getDirectoryNames(String path) {
		List<String> directories = new ArrayList<String>();
		
		try {
			directories =  Files.walk(Paths.get(path), 1)
						   	    .filter(Files::isDirectory)
						   	    .skip(1)		// except the root directory.
						   	    .map(file -> file.getFileName().toString())
						   	    .filter(name -> !name.contains("(X)"))
						   	    .collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return directories;
	}
	
	public static List<String> getAllJavaSourceFile(String path) {
		List<String> javaSourceFiles = new ArrayList<String>();
		
		try {
			javaSourceFiles = Files.walk(Paths.get(path))
							   	   .filter(Files::isRegularFile)
							   	   .map(file -> file.toAbsolutePath().toString())
							   	   .filter(name -> name.endsWith(".java"))
							   	   .collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return javaSourceFiles;
	}
}