package com.zuehlke.pgadmissions.validators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.google.common.io.CharStreams;
import com.zuehlke.pgadmissions.dto.WorkflowConfigurationDTO;

@Component
public class WorkflowConfigurationValidator extends AbstractValidator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return WorkflowConfigurationDTO.class.equals(clazz);
    }
    
    @Override
    protected void addExtraValidation(Object target, Errors errors) {
        WorkflowConfigurationDTO workflowConfigurationDTO = (WorkflowConfigurationDTO) target;
        
        if (workflowConfigurationDTO.getLocation() == null && workflowConfigurationDTO.getConfiguration() == null) {
            errors.reject("workflow.configuration.empty");
        }
        
        String rejectValue = "location";
        if (workflowConfigurationDTO.getLocation() == null) {
            rejectValue = "configuration";
        } else {
            try {
                URL locationURL = new URL(workflowConfigurationDTO.getLocation());
                BufferedReader configurationSource = new BufferedReader(new InputStreamReader(locationURL.openStream()));
                workflowConfigurationDTO.setConfiguration(CharStreams.toString(configurationSource));
            } catch (Exception e) {
                errors.rejectValue(rejectValue, "workflow.configuration.location.unreachable");
            }
        }
    }

}
