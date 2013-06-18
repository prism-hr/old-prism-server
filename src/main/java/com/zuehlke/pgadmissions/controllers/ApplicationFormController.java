package com.zuehlke.pgadmissions.controllers;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProgramException;
import com.zuehlke.pgadmissions.exceptions.InvalidParameterFormatException;
import com.zuehlke.pgadmissions.propertyeditors.PlainTextUserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

    private final ProgramDAO programDAO;
    private final ApplicationsService applicationService;
    private final PlainTextUserPropertyEditor userPropertyEditor;
    public static final String PROGRAM_DOES_NOT_EXIST = "private/pgStudents/programs/program_does_not_exist";
    private final ProgramInstanceDAO programInstanceDAO;
    private final UserService userService;

    ApplicationFormController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public ApplicationFormController(ProgramDAO programDAO, ApplicationsService applicationService, PlainTextUserPropertyEditor userPropertyEditor,
            ProgramInstanceDAO programInstanceDAO, UserService userService) {
        this.programDAO = programDAO;
        this.applicationService = applicationService;
        this.userPropertyEditor = userPropertyEditor;
        this.programInstanceDAO = programInstanceDAO;
        this.userService = userService;
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ModelAndView createNewApplicationForm(@RequestParam String program, @RequestParam(required = false) String programDeadline, @RequestParam(required = false) String projectTitle,
            @RequestParam(required = false) String programhome) {
        return processApplyNew(program, programDeadline, projectTitle, programhome);
    }

    @RequestMapping(value = "/new", method = { RequestMethod.GET })
    public ModelAndView createNewApplicationFormGet(@RequestParam String program, @RequestParam(required = false) String sequence,
            @RequestParam(required = false) String programDeadline, @RequestParam(required = false) String projectTitle,
            @RequestParam(required = false) String programhome) {
        return processApplyNew(program, programDeadline, projectTitle, programhome);
    }

    private ModelAndView processApplyNew(String programName, String programDeadline, String projectTitle, String programhome) {
        Date batchDeadline = parseBatchDeadline(programDeadline);
        RegisteredUser user = userService.getCurrentUser();

        Program program = programDAO.getProgramByCode(programName);
        if (program == null || programInstanceDAO.getActiveProgramInstances(program).isEmpty() || !program.isEnabled()) {
            throw new CannotApplyToProgramException(program);
        }
        ApplicationForm applicationForm = applicationService.createOrGetUnsubmittedApplicationForm(user, program, batchDeadline, projectTitle, programhome);
        return new ModelAndView("redirect:/application", "applicationId", applicationForm.getApplicationNumber());
    }

    private Date parseBatchDeadline(String programDeadline) {
        Date batchDeadline = null;
        if (StringUtils.isBlank(programDeadline)) {
            return null;
        }

        try {
            batchDeadline = DateUtils.parseDate(programDeadline, new String[] { "dd MMM yyyy", "dd-MMM-yyyy" });
        } catch (ParseException e) {
            throw new InvalidParameterFormatException(e);
        }

        return batchDeadline;
    }

    ApplicationForm newApplicationForm() {
        return new ApplicationForm();
    }

    @InitBinder
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

    }
}
