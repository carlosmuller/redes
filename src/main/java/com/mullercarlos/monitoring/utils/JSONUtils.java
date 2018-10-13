package com.mullercarlos.monitoring.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mullercarlos.monitoring.models.ClientModel;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

public class JSONUtils {

    public static String serialize(Object object) {
        return new GsonBuilder().setDateFormat("dd/MM/yyyy").create().toJson(object).toString();
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        return new GsonBuilder().setDateFormat("dd/MM/yyyy").create().fromJson(json, clazz);
    }

    public static ConcurrentHashMap deserializeFromType(String clients) {
        Type type = new TypeToken<ConcurrentHashMap<String, ClientModel>>() {
        }.getType();
        return new GsonBuilder().setDateFormat("dd/MM/yyyy").create().fromJson(clients, type);
    }
}
