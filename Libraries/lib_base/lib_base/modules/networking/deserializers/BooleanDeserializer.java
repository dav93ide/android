package com.bodhitech.it.lib_base.lib_base.modules.networking.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class BooleanDeserializer implements JsonDeserializer<Boolean> {

    /** Override JsonDeserializer<Boolean> Methods **/
    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
            if (primitive.isNumber()) {
                return primitive.getAsInt() == 1;
            }
            if (primitive.isString()) {
                return "yes".equalsIgnoreCase(primitive.getAsString()) || "true".equalsIgnoreCase(primitive.getAsString()) || "1".equalsIgnoreCase(primitive.getAsString());
            }
        }
        throw new JsonParseException("Wrong boolean");
    }

}