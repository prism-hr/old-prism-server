package com.zuehlke.pgadmissions.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.exceptions.WorkflowConfigurationException;
import com.zuehlke.pgadmissions.services.WorkflowConfigurationExportService;
import com.zuehlke.pgadmissions.services.WorkflowConfigurationImportService;
import com.zuehlke.pgadmissions.validators.AbstractValidator;

@Controller
@RequestMapping("/workflowConfiguration")
public class WorkflowConfigurationController {

    private static final String WORKFLOW_CONFIGURATION_FORM = "private/workflow/configuration_form";

    private static final String WORKFLOW_CONFIGURATION_FAILURE = "workflow.configuration.failure";
    
    private static final String WORKFLOW_CONFIGURATION_SUCCESS = "workflow.configuration.success";

    @Autowired
    private WorkflowConfigurationExportService workflowConfigurationExportService;
    
    @Autowired
    private WorkflowConfigurationImportService workflowConfigurationImportService;

    @ResponseBody
    @RequestMapping(value = "/export", method = RequestMethod.GET, produces = "application/xml")
    public String exportWorkflowConfiguration() throws Exception {
        return workflowConfigurationExportService.exportWorkflowConfiguration();
    }

    @RequestMapping(value = "/import", method = RequestMethod.GET)
    public String importWorkflowConfiguration(ModelMap modelMap) {
        modelMap.put("configuration", workflowConfigurationExportService.exportWorkflowConfiguration());
        return WORKFLOW_CONFIGURATION_FORM;
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String importWorkflowConfiguration(BindingResult binding, ModelMap modelMap) {
        String configuration = (String) modelMap.get("configuration");
        
        if (StringUtils.isBlank(configuration)) {
            binding.rejectValue("configuration", AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE);
        }
        
        try {
            workflowConfigurationImportService.importWorkflowConfiguration(configuration);
            modelMap.put("importOutcome", WORKFLOW_CONFIGURATION_SUCCESS);
        } catch (WorkflowConfigurationException e) {
            modelMap.put("importOutcome", WORKFLOW_CONFIGURATION_FAILURE);
            binding.rejectValue("configuration", WORKFLOW_CONFIGURATION_FAILURE, e.getMessage());
        }
        
        modelMap.put("configuration", configuration);
        return WORKFLOW_CONFIGURATION_FORM;
    }

    @InitBinder(value = "workflowConfigurationDTO")
    public void validateWorkflowConfiguration(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

}
