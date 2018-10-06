package com.mullercarlos.monitoring.models;

import com.mullercarlos.monitoring.message.Health;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

@Data
@Builder
public class ClientModel {
    private List<Service> serviceList;
    private String authKey;
    private LocalDateTime lastHealthCheck;
    private Integer port;
    private String  ip;
    private Boolean isHealth;

    private String cpuUsage;
    private String diskUsage;
    private String ramUsage;

    public void updateHealth(Health healthUpdate) {
        this.setIsHealth(true);
        this.cpuUsage = healthUpdate.getCpuUsage();
        this.ramUsage = healthUpdate.getRamUsage();
        this.diskUsage =  healthUpdate.getDiskUsage();
        this.lastHealthCheck = now();
    }
}
