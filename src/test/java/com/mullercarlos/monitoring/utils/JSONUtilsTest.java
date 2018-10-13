package com.mullercarlos.monitoring.utils;

import com.mullercarlos.monitoring.message.Signin;
import com.mullercarlos.monitoring.models.Service;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONUtilsTest {

    @Test
    void serialize__should_serialize_object_correctly() {
        Signin message = new Signin("authKey", List.of(Service.builder().name("service").status("running").build()), 1);

        String expected = "{\"serviceList\":[{\"name\":\"service\",\"status\":\"running\"}],\"portListener\":1,\"type\":\"SIGNIN\",\"authKey\":\"authKey\"}";
        assertEquals(expected,JSONUtils.serialize(message));
    }
    @Test
    void deserialize__should_deserialize_object_correctly() {
        Signin expected = new Signin("authKey", List.of(Service.builder().name("service").status("running").build()), 1);

        String json = "{\"serviceList\":[{\"name\":\"service\",\"status\":\"running\"}],\"portListener\":1,\"type\":\"SIGNIN\",\"authKey\":\"authKey\"}";
        assertEquals(expected,JSONUtils.deserialize(json, Signin.class));
    }
}