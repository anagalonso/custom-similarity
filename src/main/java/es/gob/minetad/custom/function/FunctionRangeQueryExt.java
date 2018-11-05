package es.gob.minetad.custom.function;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.solr.search.DelegatingCollector;
import org.apache.solr.search.PostFilter;
import org.apache.solr.search.SolrConstantScoreQuery;


public class FunctionRangeQueryExt extends SolrConstantScoreQuery implements PostFilter {
	
	 
	final ValueSourceRangeFilterExt rangeFilt;

	  public FunctionRangeQueryExt(ValueSourceRangeFilterExt filter) {
		 
	    super(filter);
	    this.rangeFilt = filter;
	    this.setCost(100); // default behavior should be PostFiltering
	    System.out.println("lista::"+rangeFilt.getDocuments().size());
	    
	    
	   
	  }

	 // @Override
	  public DelegatingCollector getFilterCollector(IndexSearcher searcher) {
		Map fcontext = ValueSource.newContext(searcher);
	    return new FunctionRangeCollectorExt(fcontext);
	  }

	  class FunctionRangeCollectorExt extends DelegatingCollector {
	    final Map fcontext;
	    ValueSourceScorer scorer;
	    int maxdoc;
	    
	    public FunctionRangeCollectorExt(Map fcontext) {
	    	System.out.println("FunctionRangeCollectorExt");
	      this.fcontext = fcontext;
	    }

	    
	    
	    
	    @Override
	    public void collect(int doc) throws IOException {
	    	System.out.println("collect");
	      assert doc < maxdoc;
	      if (scorer.matches(doc)) {
	        leafDelegate.collect(doc);
	      }
	    }

	    @Override
	    protected void doSetNextReader(LeafReaderContext context) throws IOException {	    	
	      super.doSetNextReader(context);
	      FunctionValues dv = rangeFilt.getValueSource().getValues(fcontext, context);
	      scorer = dv.getRangeScorer(context, rangeFilt.getLowerVal(), rangeFilt.getUpperVal(), rangeFilt.isIncludeLower(), rangeFilt.isIncludeUpper());
	    
	    }
	  }
	}
