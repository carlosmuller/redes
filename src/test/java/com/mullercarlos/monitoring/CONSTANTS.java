package com.mullercarlos.monitoring;

import com.mullercarlos.monitoring.message.Signin;
import com.mullercarlos.monitoring.models.Service;

import java.util.List;

public class CONSTANTS {
    public final static Signin SIGNIN = new Signin("authKey", List.of(Service.builder().name("service").cpuUsage("1").ramUsage("1").build()), 1);
    public final static String SIGNINJSON = "{\"serviceList\":[{\"name\":\"service\",\"ramUsage\":\"1\",\"cpuUsage\":\"1\"}],\"portListener\":1,\"type\":\"SIGNIN\",\"authKey\":\"authKey\"}";


}
