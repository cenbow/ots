package com.mk.framework.util;
 
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mk.ots.message.model.MessageType;
 
/**
 * Deserializer that case-insensitively deserializes 
 * "yes", "no", and null JSON field values to 
 * true, false, and false respectively
 * 
 * @author robin
 *
 */
public class MessageTypeSerializer extends JsonSerializer<MessageType> {
 
	protected static final String F = "F";
	protected static final String T = "T";
	@Override
	public void serialize(MessageType arg0, JsonGenerator arg1,SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		if(arg0 == null){
			arg1.writeObject("");
		}else{
			arg1.writeObject(arg0.getId());
		}
	}
	
}