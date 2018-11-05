package es.gob.minetad.custom.function;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public class ScoreValueSource extends ValueSource {
	
	float constant;

	

	public float getConstant() {
		return constant;
	}

	public void setConstant(float constant) {
		this.constant = constant;
	}

	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
		public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
		  
	    return new FunctionValues() {
	      public float floatVal(int doc) {
	        return constant;
	      }
	      public int intVal(int doc) {
	        return (int)floatVal(doc);
	      }
	      public long longVal(int doc) {
	        return (long)floatVal(doc);
	      }
	      public double doubleVal(int doc) {
	        return (double)floatVal(doc);
	      }
	      public String strVal(int doc) {
	        return Float.toString(floatVal(doc));
	      }
	      public String toString(int doc) {
	        return description();
	      }
	    };
	  }
	// commented out some boilerplate stuff
	}