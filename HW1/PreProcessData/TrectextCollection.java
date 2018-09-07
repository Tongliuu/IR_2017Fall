package PreProcessData;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import Classes.Path;

/**
 * This is for INFSCI 2140 in 2018
 *
 */
public class TrectextCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private BufferedReader br;
	private String data=null;
	public Map<String,Object> map;
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file in Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!

		this.br=new BufferedReader(new FileReader(Path.DataTextDir));
		this.map=new HashMap<>();
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use it any more
		String doc_n=null,doc_c=null;
		while((data = br.readLine())!=null) {
			if(data.equals("<DOC>")){
				//This is followed by <DOCNO> in the next line. Extract it.
				data=br.readLine();
				doc_n=data.substring(8,data.length()-9);

				//Extract the content
				while(!(data=br.readLine()).equals("<TEXT>"));
				StringBuilder sb=new StringBuilder();
				while(!(data=br.readLine()).equals("</TEXT>"))
					sb.append(data).append(" ");
				doc_c=sb.toString();

				//Store it in a hashmap whose size is always 1.
				char[] text=doc_c.toCharArray();
				map.clear();
				map.put(doc_n,text);
				return map;
			}
		}

		br.close();

		return null;
	}
	
}
