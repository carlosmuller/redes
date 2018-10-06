package com.mullercarlos.monitoring;

import com.mullercarlos.monitoring.message.*;
import com.mullercarlos.monitoring.models.Service;

import java.util.List;

public class CONSTANTS {
    /**
     * Ida e volta de mensagem tipo signin
     */
    public final static Signin SIGNIN = new Signin("authKey", List.of(Service.builder().name("service").cpuUsage("1").ramUsage("1").build()), 1);
    public final static String SIGNINJSON = "{\"serviceList\":[{\"name\":\"service\",\"ramUsage\":\"1\",\"cpuUsage\":\"1\"}],\"portListener\":1,\"type\":\"SIGNIN\",\"authKey\":\"authKey\"}";

    /**
     * Ida e volta de mensagem tipo HEALTH
     */
    public final static Health HEALTH = new Health("75%", "5G/16G", "50G", "authKey");
    public final static String HEALTHJSON = "{\"cpuUsage\":\"75%\",\"ramUsage\":\"5G/16G\",\"diskUsage\":\"50G\",\"type\":\"HEALTH\",\"authKey\":\"authKey\"}";

}
