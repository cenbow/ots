package com.mk.framework.util;
 
import java.io.IOException;
 
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
 
/**
 * Deserializer that case-insensitively deserializes 
 * "yes", "no", and null JSON field values to 
 * true, false, and false respectively
 * 
 * @author robin
 *
 */
public class TFBooleanSerializer extends JsonSerializer<Boolean> {
 
	protected static final String F = "F";
	protected static final String T = "T";
	@Override
	public void serialize(Boolean arg0, JsonGenerator arg1,SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		if(arg0 == null || !arg0){
			arg1.writeObject(F);
		}else{
			arg1.writeObject(T);
		}
	}
	
}