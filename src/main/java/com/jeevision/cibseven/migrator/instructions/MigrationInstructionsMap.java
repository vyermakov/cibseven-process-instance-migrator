package com.jeevision.cibseven.migrator.instructions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * Default implementation for {@link GetMigrationInstructions}.
 */
@Getter
public class MigrationInstructionsMap implements GetMigrationInstructions {

	private Map<String, List<MinorMigrationInstructions>> migrationInstructionMap;

	public MigrationInstructionsMap() {
	    this.migrationInstructionMap = new HashMap<>();
	}

	public void clearInstructions() {
	    this.migrationInstructionMap = new HashMap<>();
	}

	public MigrationInstructionsMap putInstructions(String processDefinitionKey, List<MinorMigrationInstructions> instructions) {
        if (migrationInstructionMap.containsKey(processDefinitionKey)) {
            migrationInstructionMap.get(processDefinitionKey).addAll(instructions);
        } else {
            //generate new ArrayList to guarantee support for structural modification (i.e. add)
            migrationInstructionMap.put(processDefinitionKey, new ArrayList<>(instructions));
        }
        return this;
    }

	@Override
    public List<MinorMigrationInstructions> getApplicableMinorMigrationInstructions(String processDefinitionKey,
			int sourceMinorVersion, int targetMinorVersion, int majorVersion) {
		if (migrationInstructionMap.containsKey(processDefinitionKey))
			return migrationInstructionMap.get(processDefinitionKey).stream()
					.filter(minorMigrationInstructions -> minorMigrationInstructions
							.getTargetMinorVersion() <= targetMinorVersion
							&& minorMigrationInstructions.getSourceMinorVersion() >= sourceMinorVersion
							&& minorMigrationInstructions.getMajorVersion() == majorVersion)
					.collect(Collectors.toList());
		else {
			return Collections.emptyList();
		}
	}
}
