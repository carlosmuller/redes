package com.mullercarlos.monitoring.models;

import com.mullercarlos.monitoring.message.Health;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    private double cpuUsage;
    private long diskUsage;
    private long ramUsage;
    private long totalRam;

    public void updateHealth(Health healthUpdate) {
        this.cpuUsage = healthUpdate.getCpuUsage();
        this.ramUsage = healthUpdate.getRamUsage();
        this.totalRam = healthUpdate.getTotalRam();
        this.diskUsage =  healthUpdate.getDiskUsage();
        this.lastHealthCheck = now();

    }

    public boolean hasHighCpuUsage(){
        return this.cpuUsage >= 75.00;
    }

    public boolean hasEnoughRam(){
        return ramRatio() >= 75.00;
    }

    private double ramRatio() {
        return (this.ramUsage/(double)this.totalRam)*100;
    }

    public boolean isHealth(){
        boolean isHealth = !hasEnoughRam() && hasHighCpuUsage() && ChronoUnit.MINUTES.between(lastHealthCheck, now())>=5;
        if(!isHealth){
            System.err.println("O client ["+ this.authKey+"] não está saudavel cpu["+this.cpuUsage+"%] ramUsage["+ramRatio()+"%]!");
        }
        return isHealth;
    }
}
