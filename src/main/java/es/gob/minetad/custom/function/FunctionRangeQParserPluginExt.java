package es.gob.minetad.custom.function;

import java.util.Arrays;
import java.util.Iterator;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.gob.minetad.util.Util;


public class FunctionRangeQParserPluginExt extends QParserPlugin { 
	 private static final Logger LOG = LoggerFactory.getLogger(FunctionRangeQParserPluginExt.class);
	public static final String NAME = "frangeext";
	  List<Double> shape=null;
	  String consulta;
	  @Override
	  public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
		 
		 ModifiableSolrParams paramsdos=new ModifiableSolrParams(params);
		// LOG.info("PRUEBAS : "+params.get("url")+"--"+params.get("qq")+"--"+params.get("model")+"--"+Boolean.getBoolean(params.get("topics")));
		shape=Arrays.asList(Util.getVectorFromText(params.get("url"),params.get("qq"), params.get("model"),Boolean.getBoolean(params.get("topics"))));
		// shape=Arrays.asList(Util.getVectorFromText());
		consulta=Util.getQuery(Util.getVectorString(shape, params.get("prefix")), 1000); //precison
		try {
			Query queryCount=QParser.getParser("*:*", null, req).getQuery();
	        int i= req.getSearcher().count(queryCount);
	        paramsdos.set("total", i+"");	        
	        
		}catch (Exception e) {
		// TODO: handle exception
		 
		}
		paramsdos.remove("qq");
		
		if (params.getBool("pruebas", true)){//provisional para pruebas de patentes
		consulta="sumTotalTermFreq(listaBO:t2|35) sumTotalTermFreq(listaBO:t7|26) sumTotalTermFreq(listaBO:t13|20) sumTotalTermFreq(listaBO:t15|66) sumTotalTermFreq(listaBO:t20|94) sumTotalTermFreq(listaBO:t21|34) sumTotalTermFreq(listaBO:t26|18) sumTotalTermFreq(listaBO:t28|64) sumTotalTermFreq(listaBO:t32|21) sumTotalTermFreq(listaBO:t37|22) sumTotalTermFreq(listaBO:t41|48) sumTotalTermFreq(listaBO:t44|18) sumTotalTermFreq(listaBO:t45|17) sumTotalTermFreq(listaBO:t47|25) sumTotalTermFreq(listaBO:t57|20) sumTotalTermFreq(listaBO:t61|57) sumTotalTermFreq(listaBO:t65|22) sumTotalTermFreq(listaBO:t69|17)*listaBO:t2^965 listaBO:t7^974 listaBO:t13^980 listaBO:t15^934 listaBO:t20^906 listaBO:t21^966 listaBO:t26^982 listaBO:t28^936 listaBO:t32^979 listaBO:t37^978 listaBO:t41^952 listaBO:t44^982 listaBO:t45^983 listaBO:t47^975 listaBO:t57^980 listaBO:t61^943 listaBO:t65^978 listaBO:t69^983";
		shape= Arrays.asList(0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.020186335403726708,0.009316770186335404,0.009316770186335404,0.020186335403726708,0.020186335403726708,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.009316770186335404,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.020186335403726708,0.036490683229813664,0.0038819875776397515,0.014751552795031056,0.0038819875776397515,0.0038819875776397515,0.009316770186335404,0.06909937888198757,0.014751552795031056,0.0038819875776397515,0.009316770186335404,0.009316770186335404,0.04192546583850932,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.020186335403726708,0.0038819875776397515,0.009316770186335404,0.0038819875776397515,0.020186335403726708,0.014751552795031056,0.0038819875776397515,0.020186335403726708,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.0038819875776397515,0.014751552795031056,0.0038819875776397515,0.014751552795031056,0.014751552795031056,0.020186335403726708,0.009316770186335404,0.3299689440993789,0.0038819875776397515,0.0038819875776397515,0.009316770186335404,0.009316770186335404,0.020186335403726708);
		}
		paramsdos.set("qq", consulta.replaceAll("\\*", " "));
		paramsdos.set("shape", Util.getVectorString(shape)); 
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

	        String l = Util.getCota(consulta.split("\\*")[1],Float.parseFloat(params.get("epsylon")))+"";
	        String u = localParams.get("u");
	        boolean includeLower = localParams.getBool("incl",true);
	        boolean includeUpper = localParams.getBool("incu",true);
	        ValueSourceRangeFilterExt rf = new ValueSourceRangeFilterExt(vs, l, u, includeLower, includeUpper, shape);	        
	        FunctionRangeQueryExt frq =new FunctionRangeQueryExt(rf);
	        return frq;
	      }
	    };
	    
	    
	    return pp;
	  }

	 
	  
	 
	  
	}


