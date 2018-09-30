package com.mullercarlos.message;

import com.mullercarlos.models.Service;
import lombok.*;

import java.util.List;

@RequiredArgsConstructor
@Builder
public class Message {

    private final Type type;
    private final List<Service> serviceList;
    private final String authKey;
}
