package com.mullercarlos.monitoring.utils;

import com.mullercarlos.monitoring.message.*;
import com.mullercarlos.monitoring.models.Service;

import java.util.List;

public class CONSTANTS {
    /**
     * Ida e volta de mensagem tipo signin
     */
    public final static Signin SIGNIN = new Signin("authKey", List.of(Service.builder().name("service").status("running").build()), 1);
    public final static String SIGNINJSON = "{\"serviceList\":[{\"name\":\"service\",\"status\":\"running\"}],\"portListener\":1,\"type\":\"SIGNIN\",\"authKey\":\"authKey\"}";

    /**
     * Ida e volta de mensagem tipo HEALTH
     */
    public final static Health HEALTH = new Health(45.00, 500000, 5000,123546, "authKey");
    public final static String HEALTHJSON = "{\"cpuUsage\":45.0,\"ramUsage\":500000,\"totalRam\":5000,\"diskUsage\":123546,\"type\":\"HEALTH\",\"authKey\":\"authKey\"}";

}
