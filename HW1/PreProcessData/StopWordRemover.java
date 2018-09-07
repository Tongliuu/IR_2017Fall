package PreProcessData;
import Classes.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StopWordRemover {
	//you can add essential private methods or variables.
	private BufferedReader br;
	private String data=null;
	public Set<String> stopword=new HashSet<>();

	public StopWordRemover( ) throws IOException{
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir
		this.br=new BufferedReader(new FileReader(Path.StopwordDir));
		while((data=br.readLine())!=null)
			stopword.add(data);
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		// return true if the input word is a stopword, or false if not
		if(stopword.contains(new String(word)))
			return true;

		return false;
	}
}
