package es.gob.minetad.custom.transformer;

import java.io.IOException;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.response.transform.DocTransformer;

public class CustomTransformer extends DocTransformer {


	final NamedList<String> rename;

	 public CustomTransformer( NamedList<String> rename ) {
	  this.rename = rename;
	 }

	 @Override
	 public String getName() {
	  StringBuilder str = new StringBuilder();
	  str.append( "Rename[" );
	  for( int i=0; i< rename.size(); i++ ) {
	   if( i > 0 ) {
	    str.append( "," );
	   }
	   str.append( rename.getName(i) ).append( ">>" ).append( rename.getVal( i ) );
	  }
	  str.append( "]" );
	  return str.toString();
	 }

	@Override
	public void transform(SolrDocument doc, int docid) throws IOException {
		System.out.println("qq : "+ this.context.getRequest().getOriginalParams().get("qq"));
		System.out.println("shape : "+ this.context.getRequest().getParams().get("shape"));
		for( int i=0; i<rename.size(); i++ ) {
			   Object v = doc.remove( rename.getName(i) );
			   
			   if( v != null ) {
			    doc.setField(rename.getVal(i), "12");
			   }
			  }
	}
	
	

}
