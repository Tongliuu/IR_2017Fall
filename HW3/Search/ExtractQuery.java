package Search;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import Classes.Path;
import Classes.Query;
import Classes.Stemmer;

public class ExtractQuery {

	private int qIndex = 0;

	private ArrayList<String> topic = new ArrayList<>();
	private ArrayList<String> title = new ArrayList<>();

	public ExtractQuery() throws IOException {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		HashSet<String> stopword = new HashSet<>();
		FileInputStream file = new FileInputStream(Path.StopwordDir);
		BufferedReader br= new BufferedReader(new InputStreamReader(file));
		String l = br.readLine();
		while (l != null){
			stopword.add(l);
			l = br.readLine();
		}
		br.close();

		BufferedReader rd = new BufferedReader(new FileReader(Path.TopicDir));
		String line = rd.readLine();
		while(line!= null){
			if(line.indexOf("<num>") != -1){
				String queryid = line.substring(14);
				topic.add(queryid);
			}
			if( line.indexOf( "<title>" )!= -1){
				String content = line.substring(7);
				String[] token = content.replaceAll("[\\pP‘’“”]", "").split(" ");
				String res ="";
				for(String word:token){
					word = word.toLowerCase();
					if(!stopword.contains(word)){
						String str="";
						char[] c = word.toCharArray();
						Stemmer stemming = new Stemmer();
						stemming.add(c, c.length);
						stemming.stem();
						str = stemming.toString();
						res = res+" "+str;
					}
				}
				title.add(res);
			}
			line = rd.readLine();
		}
		rd.close();
	}
	
	public boolean hasNext(){
		if(qIndex<topic.size()){
			qIndex++;
			return true;
		}
		else
			return false;
	}
	
	public Query next(){
		Query query = new Query();

		query.SetQueryContent(title.get(qIndex-1));
		query.SetTopicId(topic.get(qIndex-1));
		return query;
	}
}
