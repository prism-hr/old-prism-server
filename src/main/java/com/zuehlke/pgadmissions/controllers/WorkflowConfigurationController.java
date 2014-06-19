package com.zuehlke.pgadmissions.controllers;

import javax.validation.Valid;

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

import com.zuehlke.pgadmissions.dto.WorkflowConfigurationDTO;
import com.zuehlke.pgadmissions.services.WorkflowConfigurationExportService;
import com.zuehlke.pgadmissions.services.WorkflowConfigurationImportService;
import com.zuehlke.pgadmissions.validators.WorkflowConfigurationValidator;

@Controller
@RequestMapping("/workflowConfiguration")
public class WorkflowConfigurationController {

    private static final String CONFIGURATION_FORM = "private/workflow/configuration_form";

    private static final String IMPORT_SUCCESS_MESSAGE = "Your workflow configuration was successfully uploaded.";

    @Autowired
    private WorkflowConfigurationExportService workflowConfigurationExportService;
    
    @Autowired
    private WorkflowConfigurationImportService workflowConfigurationImportService;

    @Autowired
    private WorkflowConfigurationValidator workflowConfigurationValidator;

    @ResponseBody
    @RequestMapping(value = "/export", method = RequestMethod.GET, produces = "application/xml")
    public String exportWorkflowConfiguration() throws Exception {
        return workflowConfigurationExportService.exportWorkflowConfiguration();
    }

    @RequestMapping(value = "/import", method = RequestMethod.GET)
    public String importWorkflowConfiguration(ModelMap modelMap) {
        modelMap.put("workflowConfigurationDTO", new WorkflowConfigurationDTO());
        return CONFIGURATION_FORM;
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String importWorkflowConfiguration(@Valid WorkflowConfigurationDTO workflowConfigurationDTO, BindingResult binding, ModelMap modelMap)
            throws Exception {
        if (!binding.hasErrors()) {
            workflowConfigurationImportService.importWorkflowConfiguration(workflowConfigurationDTO.getConfiguration());
            modelMap.put("importSuccess", IMPORT_SUCCESS_MESSAGE);
        } else {
            modelMap.put("workflowConfigurationDTO", workflowConfigurationDTO);
        }
        return CONFIGURATION_FORM;
    }

    @InitBinder(value = "workflowConfigurationDTO")
    public void validateWorkflowConfiguration(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.setValidator(workflowConfigurationValidator);
    }

}
