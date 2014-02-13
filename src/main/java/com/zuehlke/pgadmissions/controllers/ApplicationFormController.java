package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProgramException;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProjectException;
import com.zuehlke.pgadmissions.services.ApplicationFormCreationService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

    private final ProgramDAO programDAO;
    private final ProjectDAO projectDAO;
    private final ApplicationFormCreationService applicationFormCreationService;
    private final ProgramInstanceDAO programInstanceDAO;
    private final UserService userService;

    ApplicationFormController() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public ApplicationFormController(ProgramDAO programDAO, ProjectDAO projectDAO, ApplicationFormCreationService applicationFormCreationService,
            ProgramInstanceDAO programInstanceDAO, UserService userService, ProgramsService programsService) {
        this.programDAO = programDAO;
        this.projectDAO = projectDAO;
        this.applicationFormCreationService = applicationFormCreationService;
        this.programInstanceDAO = programInstanceDAO;
        this.userService = userService;
    }

    @RequestMapping(value = "/new", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView createNewApplicationForm(@RequestParam(required = false) String program, @RequestParam(required = false) Integer project,
            @RequestParam(required = false) Integer advert) {
        RegisteredUser user = userService.getCurrentUser();
        Program programObject = null;
        Project projectObject = null;
        
        if (program != null) {
            programObject = programDAO.getProgramByCode(program);
        }
        
        if (programObject == null && advert != null) {
            programObject = programDAO.getProgramById(advert);
        }
        
        if (project != null) {
            projectObject = projectDAO.getProjectById(project);
        }
        
        if (projectObject == null && advert != null) {
            projectObject = projectDAO.getProjectById(advert);
        }
        
        if (programObject == null && projectObject != null) {
            programObject = projectObject.getProgram();
        }

        if (programObject == null && projectObject == null) {
            throw new CannotApplyToProjectException(projectObject);
        }
        
        if (programObject != null && (programInstanceDAO.getActiveProgramInstances(programObject).isEmpty() || !programObject.isEnabled())) {
            throw new CannotApplyToProgramException(programObject);
        }

        ApplicationForm applicationForm = applicationFormCreationService.createOrGetUnsubmittedApplicationForm(user, programObject, projectObject);
        return new ModelAndView("redirect:/application", "applicationId", applicationForm.getApplicationNumber());
    }

}
