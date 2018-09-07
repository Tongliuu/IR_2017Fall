package PreProcessData;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import Classes.Path;

/**
 * This is for INFSCI 2140 in 2018
 *
 */
public class TrecwebCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private BufferedReader br;
	private String data=null;
	public Map<String,Object> map;


	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrecwebCollection() throws IOException {
		// This constructor should open the file in Path.DataWebDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		this.br=new BufferedReader(new FileReader(Path.DataWebDir));
		this.map=new HashMap<>();
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NT: the returned content of the document should be cleaned, all html tags should be removed.
		// NTT: remember to close the file that you opened, when you do not use it any more
		String doc_n=null,doc_c=null;
		while((data = br.readLine())!=null) {
			if(data.equals("<DOC>")){
				//This is followed by <DOCNO> in the next line. Extract it.
				data=br.readLine();
				doc_n=data.substring(7,data.length()-8);

				while(!(data=br.readLine()).equals("</DOCHDR>"));

				StringBuilder sb=new StringBuilder();
				while(!(data=br.readLine()).equals("</DOC>")) {

					//Remove all complete html tags in one line
					data=data.replaceAll("<.*?>", "");

					//Remove any incomplete content of html tags in one line
					int start=data.indexOf("<"),end=data.indexOf(">");
					if(start!=-1) data=data.substring(0,start);
					if(end!=-1) data=data.substring(end+1,data.length());

					sb.append(data).append(" ");
				}
				doc_c=sb.toString();

				//Store and return
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
