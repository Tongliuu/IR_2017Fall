package PseudoRFSearch;

import java.io.IOException;
import java.util.*;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.*;
//import Search.*;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	int totalLength=0;
	
	public PseudoRFRetrievalModel(MyIndexReader ixreader) throws IOException
	{
		for ( int i = 0; i < ixreader.getTotal(); i++ )// indexReader.getDocNum()= 503473
			totalLength += ixreader.docLength(i);
		this.ixreader=ixreader;
	}
	
	/**
	 * Search for the topic with pseudo relevance feedback in 2017 spring assignment 4. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {	
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		
		
		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);

		String query = aQuery.GetQueryContent();
		String[] token = query.split(" ");
		double[] colFreqs = new double[token.length];
		int[][] postingList;
		Map<Integer, HashMap<String, Integer>> docx = new HashMap<>();

		for (int i=0;i<token.length;i++ ) {
			colFreqs[i] = (double)  ixreader.CollectionFreq(token[i]);
			postingList = ixreader.getPostingList(token[i]);
			if(postingList != null){
				for(int j = 0; j < postingList.length; j++){
					int docId = postingList[j][0];
					if(docx.containsKey(docId)){
						docx.get(docId).put(token[i],postingList[j][1]);
					}else{
						HashMap<String, Integer> termFreqs = new HashMap<>();
						termFreqs.put(token[i],postingList[j][1]);
						docx.put(docId, termFreqs);
					}
				}
			}
		}

		long m=2000;
		List<Document> documents = new ArrayList<>();
		for(int i=0; i < ixreader.getTotal(); i++ ) {
			int docLength = ixreader.docLength(i);
			double score = 1;
			for(int j=0;j<token.length;j++) {
				int docFreq = 0;
				if (colFreqs[j] != 0) {
					if (docx.containsKey(i) && docx.get(i).containsKey(token[j])) {
						docFreq = docx.get(i).get(token[j]);
					}
					score = score * ((alpha * ((docFreq + m * (colFreqs[j] / totalLength)) / (docLength + m))) + ((1 - alpha) * TokenRFScore.get(token[j])));
				}
			}
			Document aDocument = new Document(Integer.toString(i), ixreader.getDocno(i), score);
			documents.add(aDocument);
			//System.out.println(score);
		}
		// override the comparator and sort
		Collections.sort(documents, Collections.reverseOrder(new Comparator<Document>(){
			@Override
			public int compare(Document d1, Document d2) {
				return (new Double(d1.score())).compareTo(new Double(d2.score()));
			}
		}));
		List<Document> results = new ArrayList<>();
		//top N
		for (int t=0;t<TopN;t++){
			results.add(documents.get(t));
		}
		return results;
	}
	
	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception {
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String, Double> TokenRFScore = new HashMap<String, Double>();
		String query = aQuery.GetQueryContent();
		String[] token = query.split(" ");
		double[] colFreqs = new double[token.length];
		int[][] postingList;
		Map<Integer, HashMap<String, Integer>> docx = new HashMap<>();

		for (int i = 0; i < token.length; i++) {
			colFreqs[i] = (double) ixreader.CollectionFreq(token[i]);
			postingList = ixreader.getPostingList(token[i]);
			if (postingList != null) {
				for (int j = 0; j < postingList.length; j++) {
					int docId = postingList[j][0];
					if (docx.containsKey(docId)) {
						docx.get(docId).put(token[i], postingList[j][1]);
					}
					else {
						HashMap<String, Integer> termFreqs = new HashMap<>();
						termFreqs.put(token[i], postingList[j][1]);
						docx.put(docId, termFreqs);
					}
				}
			}
		}

		long m = 2000;
		List<Document> documents = new ArrayList<>();
		for (int i = 0; i < ixreader.getTotal(); i++) {
			int docLength = ixreader.docLength(i);
			double score = 1;
			for (int j = 0; j < token.length; j++) {
				int docFreq = 0;
				if (colFreqs[j] != 0) {
					if (docx.containsKey(i) && docx.get(i).containsKey(token[j])) {
						docFreq = docx.get(i).get(token[j]);
					}
					score = score * ((docFreq + m * (colFreqs[j] / totalLength)) / (docLength + m));
				}
			}
			Document aDocument = new Document(Integer.toString(i), ixreader.getDocno(i), score);
			documents.add(aDocument);
		}

		Collections.sort(documents, Collections.reverseOrder(new Comparator<Document>() {
			@Override
			public int compare(Document d1, Document d2) {
				return new Double(d1.score()).compareTo(new Double(d2.score()));
			}
		}));
		List<Document> results = new ArrayList<>();
		for (int i = 0; i < TopK; i++) {
			results.add(documents.get(i));
		}

		HashSet<String> RF = new HashSet<>();
		for (int i = 0; i < TopK; i++) {
			results.add(documents.get(i));
			String temp = documents.get(i).docid();
			RF.add(temp);
		}
		Double colLengthTopN = 0.0;

		//top n
		for (String s : RF) {
			int docId = Integer.parseInt(s);
			int dLength = ixreader.docLength(docId);
			colLengthTopN = colLengthTopN + dLength;
		}

		for (int i = 0; i < token.length; i++) {
			postingList = ixreader.getPostingList(token[i]);
			Double tokenFreq = 0.0;
			Double tokenTotalFreq = 0.0;
			if (postingList != null) {
				for (int j = 0; j < postingList.length; j++) {
					int docid = postingList[j][0];
					if (RF.contains(String.valueOf(docid))) {
						tokenFreq = tokenFreq + postingList[j][1];
					}
					tokenTotalFreq = tokenTotalFreq + postingList[j][1];
				}
			}
			Double score = (tokenFreq + (m * (tokenTotalFreq / totalLength))) / (colLengthTopN + m);
			TokenRFScore.put(token[i], score);
		}
		return TokenRFScore;
	}
}