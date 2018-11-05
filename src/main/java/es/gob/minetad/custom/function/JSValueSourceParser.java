package es.gob.minetad.custom.function;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.ValueSourceParser;

public class JSValueSourceParser extends ValueSourceParser {
	
	  public void init(NamedList namedList) {
	  }

	  public ValueSource parse(FunctionQParser fqp) {//throws ParseException {
	        //ValueSource source = fp.parseValueSource();
	        //float val = fp.parseFloat();
	        return  null;//new MinFloatFunction(source,val);
	  }
	}
