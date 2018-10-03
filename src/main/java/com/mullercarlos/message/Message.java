package com.mullercarlos.message;

import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class Message {

    private final Type type;
    private final String authKey;


    public Message(){
        this(null, null);
    }

}
