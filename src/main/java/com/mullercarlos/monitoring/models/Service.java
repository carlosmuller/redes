package com.mullercarlos.monitoring.models;

import lombok.*;

@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Service {

    private final String name;
    private final String status;
}
