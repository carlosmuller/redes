package com.mullercarlos.monitoring.message;

import lombok.*;

import static com.mullercarlos.monitoring.message.Type.HEALTH;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Health extends Message {

    private String cpuUsage;
    private String ramUsage;
    private String diskUsage;

    public Health(String cpuUsage, String ramUsage, String diskUsage, String authKey){
        super(HEALTH, authKey);
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.diskUsage = diskUsage;
    }
}
