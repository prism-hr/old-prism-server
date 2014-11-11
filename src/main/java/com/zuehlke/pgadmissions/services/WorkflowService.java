package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.WorkflowDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    private WorkflowDAO workflowDAO;
    
    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private EntityService entityService;

    public WorkflowPropertyDefinition getWorkflowPropertyDefinitionById(PrismWorkflowPropertyDefinition id) {
        return entityService.getById(WorkflowPropertyDefinition.class, id);
    }
    
    public WorkflowPropertyConfiguration getWorkflowPropertyConfiguration(Resource resource, User user, WorkflowPropertyDefinition workflowPropertyDefinition) {
        return customizationService.getConfiguration(WorkflowPropertyConfiguration.class, resource, user, "workflowPropertyDefinition",
                workflowPropertyDefinition);
    }

    public WorkflowPropertyConfiguration getWorkflowPropertyConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType,
            WorkflowPropertyDefinition workflowPropertyDefinition) {
        return customizationService.getConfiguration(WorkflowPropertyConfiguration.class, resource, locale, programType, "workflowPropertyDefinition",
                workflowPropertyDefinition);
    }
    
    public List<WorkflowPropertyDefinition> getActiveWorkflowPropertyDefinitions() {
        return workflowDAO.getActiveWorkflowPropertyDefinitions();
    }
    
    public void deleteObseleteWorkflowPropertyConfigurations() {
        workflowDAO.deleteObseleteWorkflowPropertyConfigurations(getActiveWorkflowPropertyDefinitions());
    }

}
