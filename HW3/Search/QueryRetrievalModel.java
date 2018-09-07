package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	public int collectionLength = 0;

	public QueryRetrievalModel(MyIndexReader ixreader) throws IOException {
		indexReader = ixreader;
		for(int i=0;i<indexReader.getTotal();i++){
			collectionLength+=indexReader.docLength(i);
		}
	}

	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		String query = aQuery.GetQueryContent();
		String[] token = query.split(" ");
		double [] collectionFreqs = new double[token.length];
		int [][] postingList;

		Map<Integer, HashMap<String, Integer>> docx = new HashMap<Integer, HashMap<String, Integer>>();

		for (int i=0;i<token.length;i++ ) {
			collectionFreqs[i] = (double)  indexReader.CollectionFreq(token[i]);
			postingList = indexReader.getPostingList(token[i]); // postinglist

			if(postingList != null){
				for(int j = 0; j < postingList.length; j++){
					int docId = postingList[j][0];
					if(docx.containsKey(docId)){
						docx.get(docId).put(token[i], postingList[j][1]);
					}else{
						HashMap<String, Integer> termFreqs = new HashMap<String, Integer>();
						termFreqs.put(token[i], postingList[j][1]); // inside hashmap
						docx.put(docId, termFreqs); // double hashmap
					}
				}
			}
		}

		long miu = 2000;
		List<Document> documents = new ArrayList<>();
		for ( int i = 0; i < indexReader.getTotal(); i++ ) {
			int docLength = indexReader.docLength(i);
			double score = 1;
			for(int k=0;k<token.length;k++){
				int docFreq = 0;
				double collectionFreq = collectionFreqs[k];
				if ( collectionFreq == 0 ){
					continue;
				}
				if(docx.containsKey(i) && docx.get(i).containsKey(token[k])){
					docFreq = docx.get(i).get(token[k]);
				}
				score = score * ( docFreq + miu*( collectionFreq/collectionLength ) ) / ( docLength + miu );
			}
			Document aDocument = new Document(Integer.toString(i), indexReader.getDocno(i), score);
			documents.add(aDocument);
		}
		// override the comparator
			Collections.sort(documents, Collections.reverseOrder(new Comparator<Document>(){
			@Override
			public int compare(Document d1, Document d2) {
				return new Double(d1.score()).compareTo(new Double(d2.score()));
			}
		}));

		List<Document> doc = new ArrayList<>();
		for (int t=0;t<TopN;t++){
			doc.add(documents.get(t));
		}
		return doc;
	}
	
}