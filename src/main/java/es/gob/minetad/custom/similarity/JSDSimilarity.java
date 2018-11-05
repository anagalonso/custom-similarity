package es.gob.minetad.custom.similarity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;




public class JSDSimilarity extends Similarity  {
	
	

	public JSDSimilarity() {
	}

	@Override
	public long computeNorm(FieldInvertState state) {
		
		return 1;
	}

	@Override
	public SimWeight computeWeight(float boost, CollectionStatistics collectionStats, TermStatistics... termStats) {
		
	    return new BooleanWeight(boost);
	}
	
	private static class BooleanWeight extends SimWeight {
		final float boost;

		BooleanWeight(float boost) {
			this.boost = boost;
		}
	}
	public static List<Double> getVectorFromString(String topic_vector, float multiplication_factor, int size, float epsylon) {
    	//Patstat_750_3|99
        String[] topics = topic_vector.split(" ");
        Double[] vector = new Double[size];
        Arrays.fill(vector,((double)1d/750));
      //  Arrays.fill(vector,(double)epsylon);
      // Arrays.fill(vector,0.0);
        for(int i=0; i<topics.length;i++){
        	String idd=topics[i].substring(topics[i].lastIndexOf("_")+1, topics[i].indexOf("|"));
            int id      = Integer.valueOf(idd);
            int freq    = Integer.valueOf(StringUtils.substringAfter(topics[i],"|"));
            Double score = Double.valueOf(freq) / Double.valueOf(multiplication_factor);
            vector[id] = score;
        }
        return Arrays.asList(vector);
    }
	
	@Override
	public SimScorer simScorer(SimWeight weight, LeafReaderContext context) throws IOException {
		
		 final float boost = ((BooleanWeight) weight).boost;
		 
		
		// final String vector=context.reader().document(context.ord).get("listaBO");
		 
		 
		 return new SimScorer() {

			 
			 
			 @Override
			 public float score(int doc, float freq) throws IOException {	
				 float ret=0;
				 //System.out.println("entra");
				// getVectorFromString(vector,1000f,750,1/750);
				 
				 
				 if(freq > 0){ //Math.abs(boost-freq) != 0
					 ret = boost + freq - Math.abs(boost-freq);
					 
				 }/* else {
					 ret = 0;
				 }*/
				
				 return ret;
			 }

			 @Override
			 public Explanation explain(int doc, Explanation freq) throws IOException {
				 Explanation queryBoostExpl = Explanation.match(boost, "query boost");
				 return Explanation.match(
						 queryBoostExpl.getValue(),
						 "score(" + getClass().getSimpleName() + ", doc=" + doc + "), computed from:",
						 queryBoostExpl);
			 }

			 @Override
			 public float computeSlopFactor(int distance) {
				 return 1f;
			 }

			 @Override
			 public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
				 return 1f;
			 }
		 };
	}
	
	@Override
	public String toString() {
		return "JSDSimilarity";
	}
}

