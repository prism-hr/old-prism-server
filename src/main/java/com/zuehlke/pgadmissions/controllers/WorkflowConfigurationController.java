package com.zuehlke.pgadmissions.controllers;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.services.WorkflowConfigurationService;

@Controller
@RequestMapping("/workflowConfiguration")
public class WorkflowConfigurationController {

    @Autowired
    private WorkflowConfigurationService workflowConfigurationService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String getWorkflowConfiguration() throws ParserConfigurationException {
        return workflowConfigurationService.getWorkflowConfiguration();
    }
    
}
