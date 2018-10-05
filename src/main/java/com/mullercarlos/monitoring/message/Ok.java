package com.mullercarlos.monitoring.message;


import lombok.*;

import static com.mullercarlos.monitoring.message.Type.OK;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Ok extends Message{
    private String message;

    Ok(String message, String authKey){
        super(OK, authKey);
        this.message = message;
    }
}
