package com.jeevision.cibseven.migrator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cibseven.bpm.engine.ProcessEngine;
import org.cibseven.bpm.engine.migration.MigrationInstruction;
import org.cibseven.bpm.engine.migration.MigrationPlan;
import org.cibseven.bpm.engine.repository.ProcessDefinition;
import org.cibseven.bpm.engine.runtime.ProcessInstance;

import com.jeevision.cibseven.migrator.instances.GetOlderProcessInstances;
import com.jeevision.cibseven.migrator.instances.VersionedProcessInstance;
import com.jeevision.cibseven.migrator.instructions.GetMigrationInstructions;
import com.jeevision.cibseven.migrator.instructions.MigrationInstructionCombiner;
import com.jeevision.cibseven.migrator.instructions.MigrationInstructionsAdder;
import com.jeevision.cibseven.migrator.instructions.MigrationInstructionsMap;
import com.jeevision.cibseven.migrator.instructions.MinorMigrationInstructions;
import com.jeevision.cibseven.migrator.logging.MigratorLogger;
import com.jeevision.cibseven.migrator.plan.CreatePatchMigrationplan;
import com.jeevision.cibseven.migrator.plan.VersionedDefinitionId;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;

/**
 * This migrator will, when called, attempt to migrate all existing process instances that come from a process
 * definition with an older version tag. To enable this, all process models need to be properly versioned:
 * <ul>
 * <li> Increase patch version for simple changes which can be migrated by mapping equal task IDs. Migration of those changes should work out of the box.
 * <li> Increase minor version for changes that need a mapping of some kind for migration to work. Provide these mappings via a {@link MigrationInstructionsMap}-Bean.
 * <li> Increase major version for changes where no migration is possible or wanted.
 * </ul>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessInstanceMigrator {

    private final ProcessEngine processEngine;
    private final GetOlderProcessInstances getOlderProcessInstances;
    private final CreatePatchMigrationplan createPatchMigrationplan;
    private final MigratorLogger migratorLogger;
    private final GetMigrationInstructions getMigrationInstructions;

    public static ProcessInstanceMigratorBuilder builder() {
        return new ProcessInstanceMigratorBuilder();
    }

    public void migrateInstancesOfAllProcesses() {
        processEngine.getRepositoryService().createProcessDefinitionQuery()
            .active()
            .latestVersion()
            .list()
            .forEach(processDefinition -> migrateProcessInstances(processDefinition.getKey()));
    }

    //TODO: make private
    public void migrateProcessInstances(String processDefinitionKey) {
    	migratorLogger.logMigrationStart(processDefinitionKey);
    	migratorLogger.logMessageForInstancesBeforeMigration(processDefinitionKey);
        logExistingProcessInstanceInfos(processDefinitionKey);

        Optional<VersionedDefinitionId> newestProcessDefinition = getNewestDeployedVersion(processDefinitionKey);
        if (!newestProcessDefinition.isPresent()) {
        	migratorLogger.logNoProcessInstancesDeployedWithKey(processDefinitionKey);
        } else if (!newestProcessDefinition.get().getProcessVersion().isPresent()) {
        	migratorLogger.logNewestDefinitionDoesNotHaveVersionTag(processDefinitionKey);
    	} else {
            ProcessVersion newestProcessVersion = newestProcessDefinition.get().getProcessVersion().get();
            migratorLogger.logNewestVersionInfo(processDefinitionKey, newestProcessVersion.toVersionTag());

			List<VersionedProcessInstance> olderProcessInstances = getOlderProcessInstances
					.getOlderProcessInstances(processDefinitionKey, newestProcessVersion);

            for (VersionedProcessInstance processInstance : olderProcessInstances) {
                MigrationPlan migrationPlan = null;
                if (processInstance.getProcessVersion().isOlderPatchThan(newestProcessVersion)) {
                    migrationPlan = createPatchMigrationplan.migrationPlanByMappingEqualActivityIDs(newestProcessDefinition.get(), processInstance);
                } else if (processInstance.getProcessVersion().isOlderMinorThan(newestProcessVersion)) {
                	migrationPlan = createPatchMigrationplan.migrationPlanByMappingEqualActivityIDs(newestProcessDefinition.get(), processInstance);

					List<MinorMigrationInstructions> applicableMinorMigrationInstructions = getMigrationInstructions
							.getApplicableMinorMigrationInstructions(processDefinitionKey,
									processInstance.getProcessVersion().getMinorVersion(),
									newestProcessVersion.getMinorVersion(), newestProcessVersion.getMajorVersion());

					List<MigrationInstruction> executableMigrationInstructions = MigrationInstructionCombiner.combineMigrationInstructions(
							applicableMinorMigrationInstructions);

					MigrationInstructionsAdder.addInstructions(migrationPlan, executableMigrationInstructions);
                }
                if (migrationPlan != null) {
                    try {
                        processEngine.getRuntimeService()
                            .newMigration(migrationPlan)
                            .processInstanceIds(processInstance.getProcessInstanceId())
                            .execute();
                        migratorLogger.logMigrationSuccessful(
                                processInstance.getProcessInstanceId(), processInstance.getBusinessKey(),
                                processInstance.getProcessVersion().toVersionTag(), newestProcessVersion.toVersionTag());

                    } catch(Exception  e) {
                    	migratorLogger.logMigrationError(
                    			processInstance.getProcessInstanceId(), processInstance.getBusinessKey(),
                                processInstance.getProcessVersion().toVersionTag(), newestProcessVersion.toVersionTag(),
                                processInstance.getProcessDefinitionId(), newestProcessDefinition.get().getProcessDefinitionId(), e);
                    }
                } else {
                	migratorLogger.logMigrationPlanGenerationError(
                			processInstance.getProcessInstanceId(), processInstance.getBusinessKey(),
                            processInstance.getProcessVersion().toVersionTag(), newestProcessVersion.toVersionTag());
                }
            }

        }
        migratorLogger.logMessageForInstancesAfterMigration(processDefinitionKey);
        logExistingProcessInstanceInfos(processDefinitionKey);
    }

    private void logExistingProcessInstanceInfos(String processDefinitionKey) {
        processEngine.getRuntimeService().createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByBusinessKey()
                .asc()
                .list()
                .stream()
                .collect(Collectors.groupingBy(ProcessInstance::getProcessDefinitionId))
                .forEach((processDefinitionId, instances) -> {
                    ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
                    String businessKeys = instances.stream().map(instance -> instance.getBusinessKey()).collect(Collectors.joining(","));
                    migratorLogger.logProcessInstancesInfo(processDefinitionId, processDefinition.getVersionTag(), instances.size(), businessKeys);
        });
    }

    private Optional<VersionedDefinitionId> getNewestDeployedVersion(String processDefinitionKey) {
        ProcessDefinition latestProcessDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .active()
                .singleResult();

        return Optional.ofNullable(latestProcessDefinition).map(processDefinition ->
                    new VersionedDefinitionId(ProcessVersion.fromString(processDefinition.getVersionTag()), processDefinition.getId()));
    }

}
