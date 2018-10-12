package com.mullercarlos.monitoring.models;

import com.mullercarlos.monitoring.message.Health;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

@Data
@Builder
public class ClientModel {
    @ToString.Exclude
    private List<Service> serviceList;
    private String authKey;
    private LocalDateTime lastHealthCheck;
    @ToString.Exclude
    private Integer port;
    @ToString.Exclude
    private String  ip;
    @ToString.Exclude
    private Boolean isHealth;

    private double cpuUsage;
    private long diskUsage;
    private long ramUsage;
    private long totalRam;

    public void updateHealth(Health healthUpdate) {
        this.setIsHealth(true);
        this.cpuUsage = healthUpdate.getCpuUsage();
        this.ramUsage = healthUpdate.getRamUsage();
        this.totalRam = healthUpdate.getTotalRam();
        this.diskUsage =  healthUpdate.getDiskUsage();
        this.lastHealthCheck = now();
    }
}
