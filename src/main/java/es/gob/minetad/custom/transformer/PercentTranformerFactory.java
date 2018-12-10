package es.gob.minetad.custom.transformer;

import org.apache.lucene.search.Query;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;
import org.apache.solr.search.QParser;

public class PercentTranformerFactory extends TransformerFactory {

	@Override
	public DocTransformer create(String field, SolrParams params, SolrQueryRequest req) {
		// TODO Auto-generated method stub
		return new PercentTranformer(field);
	}

	
	class PercentTranformer extends DocTransformer 
	{
	    private final String field;
	    
	    public PercentTranformer(String field)
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
	    	double porcen = 0;
	    	String consulta="*:*";
	    	try {
	    	String fq=this.context.getRequest().getParams().get("fq");
	    	if (fq!=null) {
	    		consulta=consulta+" AND "+fq;
	    	}
	    		
	    	Query q=QParser.getParser("*:*", null, this.context.getRequest()).getQuery();
	    	
		    int total=context.getSearcher().count(q);
		    Query qq=QParser.getParser(consulta, null, this.context.getRequest()).getQuery();
		    int results=context.getSearcher().count(qq);
		    System.out.println("TOTAL :"+results+"  "+context.getQuery().toString()+"  "+qq.toString());
		    porcen = results*100 /total;
	    	
	    	}catch (Exception e) {
				// TODO: handle exception
	    		e.getMessage();
			}
	    	doc.setField( getName(), ""+porcen);	
	    	
		}
	 
	
}
}