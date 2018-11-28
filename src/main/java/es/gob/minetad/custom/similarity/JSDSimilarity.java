package es.gob.minetad.custom.similarity;

import java.io.IOException;

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
		
		return 0;
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
	
	
	@Override
	public SimScorer simScorer(SimWeight weight, LeafReaderContext context) throws IOException {

		
		 final float boost = ((BooleanWeight) weight).boost;
		 
		 
		 return new SimScorer() {

			 
			 
			 @Override
			 public float score(int doc, float freq) throws IOException {	
				 float ret=0;
				 
				
				 if(freq > 0){ //Math.abs(boost-freq) != 0
					 ret = boost + freq - Math.abs(boost-freq);
				 }
				
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

