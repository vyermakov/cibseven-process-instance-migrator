package com.jeevision.cibseven.migrator;

import org.cibseven.bpm.engine.ProcessEngine;

import com.jeevision.cibseven.migrator.instances.GetOlderProcessInstances;
import com.jeevision.cibseven.migrator.instances.GetOlderProcessInstancesDefaultImpl;
import com.jeevision.cibseven.migrator.instructions.GetMigrationInstructions;
import com.jeevision.cibseven.migrator.instructions.MigrationInstructionsMap;
import com.jeevision.cibseven.migrator.logging.MigratorLogger;
import com.jeevision.cibseven.migrator.logging.MigratorLoggerDefaultImpl;
import com.jeevision.cibseven.migrator.plan.CreatePatchMigrationplan;
import com.jeevision.cibseven.migrator.plan.CreatePatchMigrationplanDefaultImpl;

import lombok.NoArgsConstructor;

/**
 * Builder for an instance of ProcessInstanceMigrator. Requires at least one call of
 * {@link #ofProcessEngine(ProcessEngine processEngine) ofProcessEngine}.
 * Will create a set of basic configuration object if no further configuration is specified.
 */
@NoArgsConstructor
public class ProcessInstanceMigratorBuilder {

    private ProcessEngine processEngineToSet;
    private GetOlderProcessInstances getOlderProcessInstancesToSet;
    private CreatePatchMigrationplan createPatchMigrationplanToSet;
    private MigratorLogger migratorLoggerToSet;
    private GetMigrationInstructions getMigrationInstructionsToSet;

    public ProcessInstanceMigratorBuilder ofProcessEngine(ProcessEngine processEngine) {
        processEngineToSet = processEngine;
        if (getOlderProcessInstancesToSet == null) {
            this.getOlderProcessInstancesToSet = new GetOlderProcessInstancesDefaultImpl(processEngine);
        }
        if (createPatchMigrationplanToSet == null) {
            this.createPatchMigrationplanToSet = new CreatePatchMigrationplanDefaultImpl(processEngine);
        }
        if (migratorLoggerToSet == null) {
            this.migratorLoggerToSet = new MigratorLoggerDefaultImpl();
        }
        if (getMigrationInstructionsToSet == null) {
            this.getMigrationInstructionsToSet = new MigrationInstructionsMap();
        }
        return this;
    }

    public ProcessInstanceMigratorBuilder withGetOlderProcessInstances(
        GetOlderProcessInstances getOlderProcessInstances) {
        this.getOlderProcessInstancesToSet = getOlderProcessInstances;
        return this;
    }

    public ProcessInstanceMigratorBuilder withCreatePatchMigrationplanToSet(
        CreatePatchMigrationplan createPatchMigrationplan) {
        this.createPatchMigrationplanToSet = createPatchMigrationplan;
        return this;
    }

    public ProcessInstanceMigratorBuilder withMigratorLogger(MigratorLogger migratorLogger) {
        this.migratorLoggerToSet = migratorLogger;
        return this;
    }

    public ProcessInstanceMigratorBuilder withGetMigrationInstructions(
        GetMigrationInstructions getMigrationInstructions) {
        this.getMigrationInstructionsToSet = getMigrationInstructions;
        return this;
    }

    public ProcessInstanceMigrator build() {
        if (processEngineToSet == null) {
            throw new ProcessInstanceMigratorConfigurationException();
        }
        return new ProcessInstanceMigrator(processEngineToSet, getOlderProcessInstancesToSet,
            createPatchMigrationplanToSet,
            migratorLoggerToSet, getMigrationInstructionsToSet);
    }
}
