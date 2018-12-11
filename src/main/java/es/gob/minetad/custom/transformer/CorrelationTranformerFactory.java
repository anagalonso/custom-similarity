package es.gob.minetad.custom.transformer;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;

import es.gob.minetad.util.Util;

public class CorrelationTranformerFactory extends TransformerFactory {

	@Override
	public DocTransformer create(String field, SolrParams params, SolrQueryRequest req) {
		
		return new Correlation(field);
	}

	
	class Correlation extends DocTransformer 
	{
	    private final String field;
	    
	    public Correlation(String field)
	    {
	        this.field = field;
	    }
	    
	    @Override
	    public String getName()
	    {
	        return field;
	    }
	    
	    @Override
	    public void transform(SolrDocument doc, int id)
	    {
	    	doc.setField( getName(), Util.getTopicNeighbours(this.context.getRequest().getParams().get("model"), id,25));	
	    	
		}
	 
	
}
}