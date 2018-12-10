package es.gob.minetad.custom.function;

import java.util.Arrays;
import java.util.List;

import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.QueryValueSource;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.FunctionQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SyntaxError;

import es.gob.minetad.util.Util;


public class FunctionRangeQParserPluginExt extends QParserPlugin { 
		
	public  final String NAME = "frangeext";
		List<Double> shape=null;
		String consulta=null;
		
		
	  @Override
	  public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
		float epsylon=Float.parseFloat(params.get("epsylon"));
		int  multiplicationFactor=Integer.parseInt(params.get("multiplicationFactor"));
		int cota=0;
		ModifiableSolrParams paramsdos=new ModifiableSolrParams(params);
		if (params.get("url")!=null) {
			shape=Arrays.asList(Util.getVectorFromText(params.get("url"),params.get("qq"), params.get("model"),Boolean.getBoolean(params.get("topics"))));
			String strVector=Util.getVectorString(shape, params.get("prefix"));
			consulta=Util.getQuery(strVector,multiplicationFactor); //precison
			cota=Util.getCota(strVector,epsylon);
		}else {
			int modS=Integer.parseInt(params.get("modelSize"));
			shape=Util.getVectorFromString(params.get("qq"),multiplicationFactor,modS,epsylon);
			consulta=Util.getQuery(params.get("qq"),multiplicationFactor);
			cota=Util.getCota(params.get("qq"),epsylon);
			
		}
		paramsdos.remove("qq"); 
		paramsdos.set("qq", consulta.replaceAll("\\*", " "));
		paramsdos.set("shape", Util.getVectorString(shape)); 
		paramsdos.set("cota", cota);
		req.setParams(paramsdos);
	    QParser pp=   new QParser(qstr, localParams, paramsdos, req) {
		  ValueSource  vs;
	      String funcStr;
	      

	      @Override
	      public Query parse() throws SyntaxError {
	        funcStr = localParams.get(QueryParsing.V, null);
	        QParser subParser = subQuery(funcStr, FunctionQParserPlugin.NAME);
	        subParser.setIsFilter(false);  // the range can be based on the relevancy score of embedded queries.	       
	        Query funcQ = subParser.getQuery();
	        if (funcQ instanceof FunctionQuery) {
	        	vs =  ((FunctionQuery)funcQ).getValueSource();
	        } else {
	        	vs = new QueryValueSource(funcQ, 0.0f);
	        }
	        
	        String l = this.req.getParams().get("cota");
	        System.out.println("l   :"+l);
	        String u = localParams.get("u");
	        boolean includeLower = localParams.getBool("incl",true);
	        boolean includeUpper = localParams.getBool("incu",true);
	        ValueSourceRangeFilterExt rf = new ValueSourceRangeFilterExt(vs, l, u, includeLower, includeUpper, shape,Integer.parseInt(params.get("multiplicationFactor")),Double.parseDouble(params.get("threshold")) );	        
	        FunctionRangeQueryExt frq =new FunctionRangeQueryExt(rf);
	        return frq;
	      }
	    };
	    
	    
	    return pp;
	  }

	 
	  
	 
	  
	}


