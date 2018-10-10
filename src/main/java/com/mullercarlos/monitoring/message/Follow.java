package com.mullercarlos.monitoring.message;


import lombok.*;

import static com.mullercarlos.monitoring.message.Type.FOLLOW;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Follow extends Message {

    private final String pathOfFile;

    public Follow(String pathOfFile, String auth) {
        super(FOLLOW, auth);
        this.pathOfFile = pathOfFile;
    }
}
