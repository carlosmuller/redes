package com.mullercarlos.monitoring.message;

public enum Type {
    HEALTH(Health.class), FAILED(Failed.class), OK(Ok.class) , START(Message.class), STOP(Message.class), FOLLOW(Message.class), SIGNIN(Signin.class);

    private final Class<? extends Message> clazz;

    Type(Class<? extends Message> clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }
}
