package com.mullercarlos.monitoring.message;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class Failed extends Message {
    private final String message;

    public Failed(String message) {
        this.message = message;
    }
}
