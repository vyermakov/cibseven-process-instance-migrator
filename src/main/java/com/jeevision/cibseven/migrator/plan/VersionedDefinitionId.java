package com.jeevision.cibseven.migrator.plan;

import java.util.Optional;

import com.jeevision.cibseven.migrator.ProcessVersion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VersionedDefinitionId {
    private final Optional<ProcessVersion> processVersion;
    private final String processDefinitionId;
}
