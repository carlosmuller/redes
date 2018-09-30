package com.mullercarlos.models;

import lombok.*;

@RequiredArgsConstructor
@Builder
public class Service {

    private final String name;
    private final Boolean running;
    private final String ramUsage;
    private final String cpuUsage;
}
