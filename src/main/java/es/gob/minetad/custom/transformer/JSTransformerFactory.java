package es.gob.minetad.custom.transformer;

import java.util.List;

import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;

import es.gob.minetad.util.JensenShannon;
import es.gob.minetad.util.Util;

public class JSTransformerFactory extends TransformerFactory {

	@Override
	public DocTransformer create(String field, SolrParams params, SolrQueryRequest req) {
		// TODO Auto-generated method stub
		return new JSTransformer(field);
	}

	
	class JSTransformer extends DocTransformer 
	{
	    private final String field;
	    
	    public JSTransformer(String field)
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
	    	List <Double> query=Util.getVectorFromString(this.context.getRequest().getParams().get("shape"));
	    	Object v = doc.getFirstValue("listaBO");
	    	double dist=JensenShannon.similarity(query,Util.getVectorFromString(((IndexableField)v).stringValue(),Float.parseFloat(this.context.getRequest().getParams().get("multiplicationFactor")),query.size(),1/query.size()));
	    	doc.setField( getName(), ""+dist);
	    	//transform(doc,id,(float)dist);
		}
	   /* @Override
	    public void transform(SolrDocument doc, int docid, float score) {
	      if( context != null && context.wantsScores() ) {
	          doc.setField( getName(), score );
	         
	      }
	    }*/
	
}
}