package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProgramException;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProjectException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

    private final ProgramDAO programDAO;
    private final ApplicationsService applicationService;
    private final ProgramInstanceDAO programInstanceDAO;
    private final UserService userService;
    private final ProgramsService programsService;

    ApplicationFormController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public ApplicationFormController(ProgramDAO programDAO, ApplicationsService applicationService, ProgramInstanceDAO programInstanceDAO,
            UserService userService, ProgramsService programsService) {
        this.programDAO = programDAO;
        this.applicationService = applicationService;
        this.programInstanceDAO = programInstanceDAO;
        this.userService = userService;
        this.programsService = programsService;
    }

    @RequestMapping(value = "/new", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView createNewApplicationForm(@RequestParam String program, @RequestParam(value = "project", required = false) Integer projectId) {
        return processApplyNew(program, projectId);
    }

    private ModelAndView processApplyNew(String programName, Integer projectId) {
        RegisteredUser user = userService.getCurrentUser();

        Program program = programDAO.getProgramByCode(programName);
        if (program == null || programInstanceDAO.getActiveProgramInstances(program).isEmpty() || !program.isEnabled()) {
            throw new CannotApplyToProgramException(program);
        }
        Project project = null;
        if (projectId != null) {
            project = programsService.getProject(projectId);
            if (project==null || !project.isAcceptingApplications()) {
                throw new CannotApplyToProjectException(project);
            }
        }
        ApplicationForm applicationForm = applicationService.createOrGetUnsubmittedApplicationForm(user, program, project);
        return new ModelAndView("redirect:/application", "applicationId", applicationForm.getApplicationNumber());
    }


}
