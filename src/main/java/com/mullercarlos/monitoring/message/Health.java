package com.mullercarlos.monitoring.message;

import lombok.*;

import static com.mullercarlos.monitoring.message.Type.HEALTH;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Health extends Message {

    private double cpuUsage;
    private long ramUsage;
    private long totalRam;
    private long diskUsage;

    public Health(double cpuUsage, long ramUsage, long totalRam, long diskUsage, String authKey){
        super(HEALTH, authKey);
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.totalRam = totalRam;
        this.diskUsage = diskUsage;
    }
}
