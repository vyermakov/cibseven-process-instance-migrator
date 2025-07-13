package com.jeevision.cibseven.migrator.instances;

import com.jeevision.cibseven.migrator.ProcessVersion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VersionedProcessInstance {
    private final String processInstanceId;
    private final String businessKey;
    private final ProcessVersion processVersion;
    private final String processDefinitionId;
}
