package com.zuehlke.pgadmissions.controllers;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.services.WorkflowConfigurationService;

@Controller
@RequestMapping("/workflowConfiguration")
public class WorkflowConfigurationController {

    @Autowired
    private WorkflowConfigurationService workflowConfigurationService;
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "application/xml")
    public String getWorkflowConfiguration() throws ParserConfigurationException, TransformerException {
        return workflowConfigurationService.getWorkflowConfiguration();
    }
    
}
