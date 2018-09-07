package Indexing;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	String type;
	Map<Integer,String> block;//used to store the information for one block, <docIndex,docNo>
	Map<String,HashMap<Integer,Integer>> index;//used to store index, <docNo,<docIndex,docFrequency>>
	int dCount=1;//this is the index for documents, starting from 1, and add 1 each time.
	int blockCount=1;//this is to count how many blocks created
	BufferedWriter bw,finalDoc;
	BufferedReader br;

	
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		this.type=type;
		block=new HashMap<>();
		index=new HashMap<>();
		bw=new BufferedWriter(new FileWriter(type+".docNo"));
		finalDoc=new BufferedWriter(new FileWriter(type+".index"));
	}
	
	public void IndexADocument(String docno, char[] content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		block.put(dCount,docno);
		String[] words=String.valueOf(content).split(" ");
		HashMap<Integer,Integer> posting;

		//create or increase the document frequency
		for(String word:words){
			if(index.containsKey(word)){
				posting=index.get(word);
				if(posting.containsKey(dCount))
					posting.put(dCount,posting.get(dCount)+1);
				else
					posting.put(dCount,1);
			}
			else{
				posting=new HashMap<>();
				posting.put(dCount,1);
				index.put(word,posting);
			}
		}
		dCount+=1;

		//divide into block, each with size of 10000 documents.
		if(dCount%10000==0){
			blockWrite();
		}
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.

		//Write the rest
		blockWrite();
		bw.close();

		//fuse
		for(int i=1;i<blockCount;i++){
			br=new BufferedReader(new FileReader(type+".temp"+i));
			String line;
			while((line=br.readLine())!=null)
				finalDoc.write(line+"\n");
			br.close();
			File file=new File(type+".temp"+i);
			file.delete();
		}
		finalDoc.close();
	}

	private void blockWrite() throws IOException{
		//to write the information in the memory into files, then empty the memory
		for(int key:block.keySet())
			bw.write(key+","+block.get(key)+"\n");

		BufferedWriter bw2=new BufferedWriter(new FileWriter(type+".temp"+blockCount));
		for(String i:index.keySet()){
			bw2.write(i+"\t");
			HashMap<Integer,Integer> posting=index.get(i);
			for(int position:posting.keySet())
				bw2.write(position+","+posting.get(position)+"\t");
			bw2.write("\n");
		}
		blockCount+=1;
		block.clear();
		index.clear();
		bw2.close();
	}

}
