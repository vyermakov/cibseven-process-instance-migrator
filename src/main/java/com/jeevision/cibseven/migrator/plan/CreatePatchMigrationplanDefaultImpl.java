package com.jeevision.cibseven.migrator.plan;

import org.cibseven.bpm.engine.ProcessEngine;
import org.cibseven.bpm.engine.migration.MigrationPlan;

import com.jeevision.cibseven.migrator.instances.VersionedProcessInstance;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreatePatchMigrationplanDefaultImpl implements CreatePatchMigrationplan {

	private final ProcessEngine processEngine;

	@Override
    public MigrationPlan migrationPlanByMappingEqualActivityIDs(VersionedDefinitionId newestProcessDefinition, VersionedProcessInstance processInstance) {
        return processEngine.getRuntimeService()
                .createMigrationPlan(processInstance.getProcessDefinitionId(), newestProcessDefinition.getProcessDefinitionId())
                .mapEqualActivities()
                .updateEventTriggers()
                .build();
    }
}
