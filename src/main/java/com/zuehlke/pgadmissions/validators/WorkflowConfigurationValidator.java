package com.zuehlke.pgadmissions.validators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.xml.sax.SAXException;

import com.google.common.io.CharStreams;
import com.zuehlke.pgadmissions.dto.WorkflowConfigurationDTO;

@Component
public class WorkflowConfigurationValidator extends AbstractValidator {

    private final String CONFIGURATION_SCHEMA = "/xml/workflow/workflow_configuration_schema.xsd";
    
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
           
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(CONFIGURATION_SCHEMA));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(workflowConfigurationDTO.getConfiguration())));
        } catch (SAXException e) { 
            errors.rejectValue(rejectValue, "workflow.configuration.invalid");
        } catch (IOException e) {
            throw new Error("Failed to read schema definition", e);
        }
        
    }

}
