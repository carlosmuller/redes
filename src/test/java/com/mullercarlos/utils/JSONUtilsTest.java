package com.mullercarlos.utils;

import com.mullercarlos.message.Signin;
import com.mullercarlos.models.Service;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONUtilsTest {

    @Test
    void serialize__should_serialize_object_correctly() {
        Signin message = new Signin("authKey", List.of(Service.builder().name("service").cpuUsage("1").ramUsage("1").build()));

        String expected = "{\"serviceList\":[{\"name\":\"service\",\"ramUsage\":\"1\",\"cpuUsage\":\"1\"}],\"type\":\"SIGNIN\",\"authKey\":\"authKey\"}";
        assertEquals(expected,JSONUtils.serialize(message));
    }
    @Test
    void deserialize__should_deserialize_object_correctly() {
        Signin expected = new Signin("authKey", List.of(Service.builder().name("service").cpuUsage("1").ramUsage("1").build()));

        String json = "{\"serviceList\":[{\"name\":\"service\",\"ramUsage\":\"1\",\"cpuUsage\":\"1\"}],\"type\":\"SIGNIN\",\"authKey\":\"authKey\"}";
        assertEquals(expected,JSONUtils.deserialize(json, Signin.class));
    }
}