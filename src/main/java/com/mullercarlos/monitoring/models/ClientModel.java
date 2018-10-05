package com.mullercarlos.monitoring.models;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ClientModel {
    private List<Service> serviceList;
    private String authKey;
    private LocalDateTime lastHealthCheck;
    private Integer port;
    private String  ip;
    private Boolean isHealth;
}
