package com.mullercarlos.message;

import com.mullercarlos.models.Service;
import lombok.*;

import java.util.List;


@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Signin extends Message {
    private final List<Service> serviceList;

    public Signin(String authKey, List serviceList){
        super(Type.SIGNIN, authKey);
        this.serviceList = serviceList;
    }
}
