package Indexing;

import Classes.Path;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class MyIndexReader {
	//you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...
	String colPath,docPath,line;
	BufferedReader docR,indexR;
	Map<Integer,String> block;
	int[][] posting;
	
	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
		if(type.equals("trecweb")){
			colPath=Path.IndexWebDir+type+".index";
			docPath=Path.IndexWebDir+type+".docNo";
		}
		else{
			colPath=Path.IndexTextDir+type+".index";
			docPath=Path.IndexTextDir+type+".docNo";
		}
		docR=new BufferedReader(new FileReader(docPath));
		block=new HashMap<>();
		while((line=docR.readLine())!=null){
			String[] docId=line.split(",");
			block.put(Integer.parseInt(docId[0]),docId[1]);
		}
		docR.close();
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) {
		for(Map.Entry<Integer,String> docNo:block.entrySet()){
			if(docNo.getValue()==docno)
				return docNo.getKey();
		}
		return -1;
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) {
		String docno=block.get(docid);
		return docno;
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		return posting;
	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		indexR=new BufferedReader(new FileReader(colPath));
		Map<Integer,Integer> map=new HashMap<>();//to store all postings for the given token
		String line;
		while((line=indexR.readLine())!=null){
			String[] aPost=line.split("\t");
			if(aPost[0].equals(token)){
				for(int i=1;i<aPost.length;i++){
					String[] aToken=aPost[i].split(",");
					map.put(Integer.parseInt(aToken[0]),Integer.parseInt(aToken[1]));
				}
			}
		}
		int size=map.size(),i=0;

		//to sort the postings by the index of the document
		posting=new int[size][2];
		Map<Integer,Integer> sort=new TreeMap<>(map);

		//generate posting[][]
		if(size!=0){
			for(int key:sort.keySet()){
				posting[i][0]=key;
				posting[i][1]=sort.get(key);
				i++;
			}
		}

		return size;
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		int res=0;
		for(int i=0;i<posting.length;i++)
			res+=posting[i][1];
		return res;
	}
	
	public void Close() throws IOException {
		indexR.close();
	}
	
}