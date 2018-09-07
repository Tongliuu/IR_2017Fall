package Indexing;

import Classes.Path;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PreProcessedCorpusReader {

	private BufferedReader br;
	String line;

	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
		br=new BufferedReader(new FileReader(Path.ResultHM1+type));
	}
	

	public Map<String, Object> NextDocument() throws IOException {
		// read a line for docNo and a line for content, put into the map with <docNo, content>

		if((line=br.readLine())!=null){
			Map<String,Object> map=new HashMap<>();
			String docNo=new String(line);
			line=br.readLine();
			char[] content=line.toCharArray();
			map.put(docNo,content);
			return map;
		}
		else{
			br.close();
			return null;
		}

	}

}
