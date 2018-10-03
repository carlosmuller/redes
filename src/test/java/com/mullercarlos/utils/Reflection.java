package com.mullercarlos.utils;

import net.vidageek.mirror.dsl.Mirror;

public class Reflection {
    private Mirror mirror = new Mirror();

    public  void setField(Object object, String field, Object value) {
        mirror.on(object).set().field(field).withValue(value);
    }

    public Object getField(Object object, String value) {
        return mirror.on(object).get().field(value);
    }
}
