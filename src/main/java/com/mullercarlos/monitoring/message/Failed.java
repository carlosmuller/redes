package com.mullercarlos.monitoring.message;

import lombok.*;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Failed extends Message {
    private final String message;

    public Failed(String message) {
        super(Type.FAILED, null);
        this.message = message;
    }
}
