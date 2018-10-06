package com.mullercarlos.monitoring.message;

public enum Type {
    HEALTH(Health.class),
    SIGNIN(Signin.class),
    FAILED(Failed.class),
    OK(Ok.class),
    START(Message.class),
    STOP(Message.class),
    FOLLOW(Message.class);

    private final Class<? extends Message> clazz;

    Type(Class<? extends Message> clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }
}
