package com.mullercarlos.monitoring.utils;

import com.google.gson.*;

public class JSONUtils {

    public static String serialize(Object object) {
        return new GsonBuilder().setDateFormat("dd/MM/yyyy").create().toJson(object).toString();
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }
}
