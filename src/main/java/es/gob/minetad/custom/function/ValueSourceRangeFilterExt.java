package es.gob.minetad.custom.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.FilteredDocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.Bits;
import org.apache.solr.search.BitsFilteredDocIdSet;
import org.apache.solr.search.SolrFilter;

import es.gob.minetad.util.JensenShannon;
import es.gob.minetad.util.Util;


public class ValueSourceRangeFilterExt  extends SolrFilter {
	  private final ValueSource valueSource;
	  private final String lowerVal;
	  private final String upperVal;
	  private final boolean includeLower;
	  private final boolean includeUpper;
	  private final List<Double> query;
	  private final float multiplication_factor;
	  private final float epsylon;
	  private final List<Document> documents=new ArrayList ();

	  public ValueSourceRangeFilterExt(ValueSource valueSource,
	                                String lowerVal,
	                                String upperVal,
	                                boolean includeLower,
	                                boolean includeUpper,
	                                List<Double> query) {
	    this.valueSource = valueSource;
	    this.lowerVal = lowerVal;
	    this.upperVal = upperVal;
	    this.includeLower = includeLower;
	    this.includeUpper = includeUpper;
	    this.query=query;
	    this.epsylon=1/query.size();
	    this.multiplication_factor= Double.valueOf(1*Math.pow(10,String.valueOf(query.size()).length()+1)).floatValue();;
  }

	  public ValueSource getValueSource() {
	    return valueSource;
	  }

	  public String getLowerVal() {
	    return lowerVal;
	  }

	  public String getUpperVal() {
	    return upperVal;
	  }

	  public boolean isIncludeLower() {
	    return includeLower;
	  }

	  public boolean isIncludeUpper() {
	    return includeUpper;
	  }
	  public List<Double> getQuery() {
		    return query;
		  }

	  
	  public List<Document> getDocuments() {
		return documents;
	}

	@Override
	  public DocIdSet getDocIdSet(final Map context, final LeafReaderContext readerContext, Bits acceptDocs) throws IOException {
		
	
		
		  DocIdSet docSet= BitsFilteredDocIdSet.wrap(new DocIdSet() {
	       @Override
	       public DocIdSetIterator iterator() throws IOException {
	    	   Scorer scorer = valueSource.getValues(context, readerContext).getRangeScorer(readerContext, lowerVal, upperVal, includeLower, includeUpper);
	    	   DocIdSetIterator allDocs = scorer.iterator();
	    	  if (allDocs == null) {
	    	        return null;
	    	   }else{
	    	    	
	    	    return new FilteredDocIdSetIterator(allDocs)  {
	    	    	
	    	        @Override
	    	        protected boolean match(int doc) {
	    	          
	    	        	boolean filtra=false;
	    	        	try{
	    	        	Document document=readerContext.reader().document(doc);
	    	        	
	    	        	double dist=JensenShannon.similarity(query,Util.getVectorFromString(document.get("listaBO"),multiplication_factor,query.size(),1/query.size()));
		    			
		    			  if (dist>0.5d){
		    				  document.add(new StringField("js_s", dist+"",  Store.YES));
		    				  documents.add(document);
		    				  filtra=true;
		    			  }
	    	        	}catch (Exception e) {
							e.printStackTrace();
						}
		    			  return filtra;
	    	        	
	    	        }
	    	        
	    	    };
		  
	    	    }
	    	  }
	       @Override
	       public Bits bits() {
	         return null;  
	       }

	      // @Override
	       public long ramBytesUsed() {
	    	   long ll=0l;
	         return ll;
	       }
	     }, acceptDocs);
		  
		  
		
		return docSet;
	  }

	  
	  
	  
	  @Override
	  public void createWeight(Map context, IndexSearcher searcher) throws IOException {
		 valueSource.createWeight(context, searcher);
	  }

	  @Override
	  public String toString(String field) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("frange(");
	    sb.append(valueSource);
	    sb.append("):");
	    sb.append(includeLower ? '[' : '{');
	    sb.append(lowerVal == null ? "*" : lowerVal);
	    sb.append(" TO ");
	    sb.append(upperVal == null ? "*" : upperVal);
	    sb.append(includeUpper ? ']' : '}');
	    return sb.toString();
	  }

	  @Override
	  public boolean equals(Object o) {
		if (this == o) return true;
	    if (!(o instanceof ValueSourceRangeFilterExt)) return false;
	    ValueSourceRangeFilterExt other = (ValueSourceRangeFilterExt)o;

	    if (!this.valueSource.equals(other.valueSource)
	        || this.includeLower != other.includeLower
	        || this.includeUpper != other.includeUpper
	    ) { return false; }
	    if (this.lowerVal != null ? !this.lowerVal.equals(other.lowerVal) : other.lowerVal != null) return false;
	    if (this.upperVal != null ? !this.upperVal.equals(other.upperVal) : other.upperVal != null) return false;
	    return true;
	  }

	  @Override
	  public int hashCode() {
	    int h = valueSource.hashCode();
	    h += lowerVal != null ? lowerVal.hashCode() : 0x572353db;
	    h = (h << 16) | (h >>> 16);  // rotate to distinguish lower from upper
	    h += (upperVal != null ? (upperVal.hashCode()) : 0xe16fe9e7);
	    h += (includeLower ? 0xdaa47978 : 0)
	    + (includeUpper ? 0x9e634b57 : 0);
	    return h;
	  }
	}
