package com.mk.framework.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mk.ots.wallet.model.CashflowTypeEnum;

import java.io.IOException;

/**
 * Deserializer that case-insensitively deserializes
 * "yes", "no", and null JSON field values to
 * true, false, and false respectively
 *
 * @author robin
 */
public class CashflowTypeEnumSerializer extends JsonSerializer<CashflowTypeEnum> {

    @Override
    public void serialize(CashflowTypeEnum arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException,
            JsonProcessingException {
        if (arg0 == null) {
            arg1.writeObject("");
        } else {
            arg1.writeObject(arg0.getId());
        }
    }

}