package com.jeevision.cibseven.migrator.integration;

import java.util.List;

import org.cibseven.bpm.engine.RepositoryService;
import org.cibseven.bpm.engine.RuntimeService;
import org.cibseven.bpm.engine.TaskService;
import org.cibseven.bpm.engine.repository.ProcessDefinition;
import org.cibseven.bpm.engine.runtime.ProcessInstance;
import org.cibseven.bpm.engine.task.Task;

public class TestHelper {

    public static ProcessDefinition getNewestDeployedProcessDefinitionId(String processDefinitionKey,
        RepositoryService repositoryService) {
		return repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey)
				.latestVersion().singleResult();
	}

	public static  List<ProcessInstance> getRunningProcessInstances(String processDefinitionKey, RuntimeService runtimeService) {
		return runtimeService
				.createProcessInstanceQuery()
				.processDefinitionKey(processDefinitionKey)
				.list();
	}

	public static  List<Task> getCurrentTasks(String processDefinitionKey, TaskService taskService) {
		return taskService
				.createTaskQuery()
				.processDefinitionKey(processDefinitionKey)
				.initializeFormKeys()
				.list();
	}

	public static void deployBPMNFromClasspathResource(String path, RepositoryService repositoryService) {
	    repositoryService
			.createDeployment()
			.addClasspathResource(path).deploy();
	}

	public static ProcessInstance startProcessInstance(String processDefinitionKey, RuntimeService runtimeService) {
		return runtimeService.startProcessInstanceByKey(processDefinitionKey);
	}

	public static void suspendProcessInstance(ProcessInstance processInstance, RuntimeService runtimeService) {
	    runtimeService.suspendProcessInstanceById(processInstance.getId());
    }

	public static void suspendProcessDefinition(ProcessDefinition processDefinition, RepositoryService repositoryService) {
	    repositoryService.suspendProcessDefinitionById(processDefinition.getId());
    }
}
