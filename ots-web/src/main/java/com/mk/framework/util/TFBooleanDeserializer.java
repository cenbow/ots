package com.mk.framework.util;
 
import java.io.IOException;
 
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
 
/**
 * Deserializer that case-insensitively deserializes 
 * "yes", "no", and null JSON field values to 
 * true, false, and false respectively
 * 
 * @author robin
 *
 */
public class TFBooleanDeserializer extends JsonDeserializer<Boolean> {
 
	protected static final String F = "F";
	protected static final String T = "T";
 
	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public Boolean deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonToken currentToken = jp.getCurrentToken();
		
		if (currentToken.equals(JsonToken.VALUE_STRING)) {
			String text = jp.getText().trim();
			
			if (T.equalsIgnoreCase(text)) {
				return Boolean.TRUE;
			} else if (F.equalsIgnoreCase(text)) {
				return Boolean.FALSE;
			}
			
			throw ctxt.weirdStringException(text, Boolean.class, 
					"Only \"" + T + "\" or \"" + F + "\" values supported");
		} else if (currentToken.equals(JsonToken.VALUE_NULL)) {
			return getNullValue();
		}
		
		throw ctxt.mappingException(Boolean.class);
	}
	
	@Override
	public Boolean getNullValue() {
		return Boolean.FALSE;
	}
 
}