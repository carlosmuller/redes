package com.mullercarlos.monitoring.message;

import com.mullercarlos.monitoring.models.Service;
import lombok.*;

import java.util.List;


@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Signin extends Message {
    private final List<Service> serviceList;
    private final Integer portListener;

    public Signin(String authKey, List serviceList, Integer portListener){
        super(Type.SIGNIN, authKey);
        this.serviceList = serviceList;
        this.portListener = portListener;
    }

}
