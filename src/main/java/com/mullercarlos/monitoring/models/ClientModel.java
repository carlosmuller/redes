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
    private String ip;

    private double cpuUsage;
    private long diskUsage;
    private long ramUsage;
    private long totalRam;

    public void updateHealth(Health healthUpdate) {
        this.cpuUsage = healthUpdate.getCpuUsage();
        this.ramUsage = healthUpdate.getRamUsage();
        this.totalRam = healthUpdate.getTotalRam();
        this.diskUsage = healthUpdate.getDiskUsage();
        this.lastHealthCheck = now();

    }

    public boolean hasHighCpuUsage() {
        return this.cpuUsage >= 75.00;
    }

    public boolean hasEnoughRam() {
        return ramRatio() <= 75.00;
    }

    private double ramRatio() {
        return (this.ramUsage / (double) this.totalRam) * 100;
    }

    public boolean isHealth() {
        long minutesBetweenLastHealthCheckAndNow = ChronoUnit.MINUTES.between(this.lastHealthCheck, now());
        boolean isHealth = hasEnoughRam();
        isHealth = !hasHighCpuUsage() && isHealth;
        isHealth = minutesBetweenLastHealthCheckAndNow <= 5 && isHealth;
        if (!isHealth) {
            String message = String.format("O cliente [%s] não está saudavel cpu[%.2f%%] ramUsage[%.2f%%] tempo entre ultima mensagem do cliente %d!", this.authKey, this.cpuUsage, ramRatio(), minutesBetweenLastHealthCheckAndNow);
            System.err.println(message);
        }
        return isHealth;
    }
}
