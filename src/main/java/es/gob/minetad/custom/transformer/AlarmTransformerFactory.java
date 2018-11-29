package es.gob.minetad.custom.transformer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestInfo;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.response.transform.DocTransformer;
import org.apache.solr.response.transform.TransformerFactory;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.QParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class AlarmTransformerFactory extends TransformerFactory {

	SolrClient solrClient=null;	
	 
	@Override
	public DocTransformer create(String field, SolrParams params, SolrQueryRequest req) {		
       solrClient = new EmbeddedSolrServer(req.getCore());		
		return new AlarmTransformer(field,solrClient);
	}

	
	class AlarmTransformer extends DocTransformer 
	{
	    private final String field;
	    private SolrClient solrClient;
	    public AlarmTransformer(String field,SolrClient solrClient )
	    {
	        this.field = field;
	        this.solrClient=solrClient;
	    }
	    
	    @Override
	    public String getName()
	    {
	        return field;
	    }
	    @Override
		  public boolean needsSolrIndexSearcher() { return true; }
	    
	    @Override
	    public void transform(SolrDocument doc, int id)
	    {
	    	
	    try {
	    	final ModifiableSolrParams newParams=new ModifiableSolrParams();
	    	newParams.add(context.getRequest().getParams());
	    	String qtParam=context.getRequest().getParams().get("qt");
	    	
	    	if (!qtParam.startsWith("/")) {
	    		newParams.remove("qt");
	    		newParams.add("qt", "/"+qtParam);
	    	}
	    	
	    	
	   	      Callable<QueryResponse> subQuery = new Callable<QueryResponse>() {
	   	        @Override
	   	        public QueryResponse call() throws Exception {
	   	          try {
	   	            return new QueryResponse(
	   	            		solrClient.request(
	   	                    new QueryRequest(newParams),context.getRequest().getCore().getName())
	   	                , solrClient);
	   	          } finally {
	   	          }
	   	        }
	   	      };
	   	  
	   	    QueryResponse response =SolrRequestInfoSuspender.doInSuspension(subQuery);
	   	 
	   	    List<TermsResponse.Term> terms=  response.getTermsResponse().getTerms(newParams.get("terms.fl"));
	        JSONObject alarmas = new JSONObject();
	        JSONArray listAlarmas = new JSONArray(); 
	        
	        for (TermsResponse.Term term:terms) {
	        	JSONObject alGroup = new JSONObject();
	        	JSONArray list = new JSONArray();
	        	Query q=QParser.getParser(newParams.get("terms.fl")+":\\"+term.getTerm(), null, this.context.getRequest()).getQuery();
	        	DocSet docset=context.getSearcher().getDocSet(q);
	        	DocIterator di=docset.iterator();	        	
	        	alGroup.put("group", term.getTerm());
	        	while (di.hasNext()) {	        		
	        		Document d=this.context.getSearcher().doc(di.nextDoc());
	        		JSONObject al = new JSONObject();
	        		al.put("id", d.get("id"));
	        		al.put(newParams.get("fieldName"), d.get(newParams.get("fieldName")));
	        		list.add( al);
	        	}
	        	alGroup.put("DocumentsList", list);
	        	listAlarmas.add(alGroup);
	        }
	        alarmas.put("ALARMS", listAlarmas);
	        doc.setField(getName(), alarmas.toJSONString());
	       
	    }catch (Exception e) {
			// TODO: handle exception
	    	System.out.println(e.getLocalizedMessage());
		}
	    
	    	
		}
	    
	  	  
}
final static class SolrRequestInfoSuspender extends SolrRequestInfo {
	    
	    private SolrRequestInfoSuspender(SolrQueryRequest req, SolrQueryResponse rsp) {
	      super(req, rsp);
	    }
	    
	    /** Suspends current SolrRequestInfo invoke the given action, and resumes then */
	    static <T> T doInSuspension(Callable<T> action) throws Exception {
	     
	      final SolrRequestInfo info = threadLocal.get();
	      try {
	        threadLocal.remove();
	        return action.call();
	      } finally {
	        setRequestInfo(info); 
	      }
	    }
	  }

}
