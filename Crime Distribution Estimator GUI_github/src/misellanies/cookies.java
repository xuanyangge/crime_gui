package misellanies;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import org.python.antlr.PythonParser.return_stmt_return;
import org.python.objectweb.asm.commons.StaticInitMerger;


public class cookies {
	private final String defaultFileName="cookies.ini";
	public final static String predictNumber="predictNumber";
	public final static String nClusters="nClusters";
	private static cookies instance=null;
	HashMap<String, String> myMap=new HashMap<String,String>();
	//Read saved cookies
	private cookies() throws FileNotFoundException{
		Scanner scanner=new Scanner(new File(defaultFileName));
		while(scanner.hasNext()){
			String s=scanner.next();
			myMap.put(s.split(":")[0], s.split(":")[1]);
		}
		scanner.close();
	}
	
	public static cookies getInstance() throws FileNotFoundException{
		if(instance==null){
			instance=new cookies();
		}
		return instance;
	}
	
	
	//Get a value from cookies key-value pair by the key. 
	public String getString(String key){
		return myMap.get(key);
	}
	
	/**
	 * Put a value into cookies key-value pair
	 * @param key
	 * @param object
	 */
	public void put(String key,Object object){
		myMap.put(key, object.toString());
	}
	
	
	/**
	 * Save the cookies
	 * @throws FileNotFoundException
	 */
	public void saveCookies() throws FileNotFoundException{
		PrintStream out=new PrintStream(new File(defaultFileName));
		for(String s:myMap.keySet()){
			out.println(s+":"+myMap.get(s));
		}
		out.close();
	}
}
