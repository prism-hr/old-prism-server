package com.zuehlke.pgadmissions.controllers.applicantform;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SourcesOfInterestPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SuggestedSupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.security.ContentAccessProvider;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgrammeDetailsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DateUtils;
import com.zuehlke.pgadmissions.validators.ProgrammeDetailsValidator;

@RequestMapping("/update")
@Controller
public class ProgrammeDetailsController {
    private static final String STUDENTS_FORM_PROGRAMME_DETAILS_VIEW = "/private/pgStudents/form/components/programme_details";
    private final ApplicationsService applicationsService;
    private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
    private final DatePropertyEditor datePropertyEditor;
    private final ProgrammeDetailsValidator programmeDetailsValidator;
    private final ProgrammeDetailsService programmeDetailsService;
    private final SuggestedSupervisorJSONPropertyEditor supervisorJSONPropertyEditor;
    private final SourcesOfInterestPropertyEditor sourcesOfInterestPropertyEditor;
    private final UserService userService;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;
    private final ContentAccessProvider contentAccessProvider;

    ProgrammeDetailsController() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ProgrammeDetailsController(ApplicationsService applicationsService, ApplicationFormPropertyEditor applicationFormPropertyEditor,
            DatePropertyEditor datePropertyEditor, SuggestedSupervisorJSONPropertyEditor supervisorJSONPropertyEditor,
            ProgrammeDetailsValidator programmeDetailsValidator, ProgrammeDetailsService programmeDetailsService, UserService userService,
            SourcesOfInterestPropertyEditor sourcesOfInterestPropertyEditor, ApplicationFormUserRoleService applicationFormUserRoleService,
            ContentAccessProvider contentAccessProvider) {
        this.applicationsService = applicationsService;
        this.applicationFormPropertyEditor = applicationFormPropertyEditor;
        this.datePropertyEditor = datePropertyEditor;
        this.supervisorJSONPropertyEditor = supervisorJSONPropertyEditor;
        this.programmeDetailsValidator = programmeDetailsValidator;
        this.programmeDetailsService = programmeDetailsService;
        this.userService = userService;
        this.sourcesOfInterestPropertyEditor = sourcesOfInterestPropertyEditor;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.contentAccessProvider = contentAccessProvider;
    }

    @RequestMapping(value = "/editProgrammeDetails", method = RequestMethod.POST)
    public String editProgrammeDetails(@Valid ProgrammeDetails programmeDetails, BindingResult result, Model model, @ModelAttribute ApplicationForm applicationForm) {
        if (result.hasErrors()) {
            return STUDENTS_FORM_PROGRAMME_DETAILS_VIEW;
        }
        
        programmeDetails.setStudyOptionCode(programmeDetailsService.getStudyOptionCodeForProgram(applicationForm.getProgram(), programmeDetails.getStudyOption()));
        programmeDetailsService.save(programmeDetails);
        applicationsService.save(applicationForm);
        applicationFormUserRoleService.applicationEdited(applicationForm, getCurrentUser());
        return "redirect:/update/getProgrammeDetails?applicationId=" + programmeDetails.getApplication().getApplicationNumber();
    }

    @RequestMapping(value = "/getProgrammeStartDate", method = RequestMethod.GET)
    @ResponseBody
    public String getProgrammeDetailsView(@RequestParam String applicationId, @RequestParam String studyOption, @ModelAttribute ApplicationForm applicationForm) {
        if (StringUtils.isBlank(studyOption) || StringUtils.isBlank(applicationId)) {
            return StringUtils.EMPTY;
        }

        List<ProgramInstance> availableProgramInstances = programmeDetailsService.getActiveProgramInstancesOrderedByApplicationStartDate(
                getApplicationForm(applicationId).getProgram(), studyOption);

        String convertedDate = StringUtils.EMPTY;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Date today = new Date();

        for (ProgramInstance instance : availableProgramInstances) {
            try {
                if (DateUtils.isToday(instance.getApplicationStartDate()) || instance.getApplicationStartDate().after(today)) {
                    convertedDate = format.format(instance.getApplicationStartDate());
                    break;
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        return convertedDate;
    }

    @RequestMapping(value = "/getProgrammeDetails", method = RequestMethod.GET)
    public String getProgrammeDetailsView(@ModelAttribute ApplicationForm applicationForm) {
        return STUDENTS_FORM_PROGRAMME_DETAILS_VIEW;
    }

    @ModelAttribute("studyOptions")
    public List<StudyOption> getStudyOptions(@RequestParam String applicationId) {
        return programmeDetailsService.getAvailableStudyOptions(getApplicationForm(applicationId).getProgram());
    }

    @ModelAttribute("sourcesOfInterests")
    public List<SourcesOfInterest> getSourcesOfInterests() {
        return programmeDetailsService.getAllEnabledSourcesOfInterest();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        contentAccessProvider.validateCanEditAsApplicant(application, getCurrentUser());
        return application;
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

    @InitBinder(value = "programmeDetails")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(programmeDetailsValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binder.registerCustomEditor(SuggestedSupervisor.class, supervisorJSONPropertyEditor);
        binder.registerCustomEditor(SourcesOfInterest.class, sourcesOfInterestPropertyEditor);
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute
    public ProgrammeDetails getProgrammeDetails(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        if (applicationForm.getProgrammeDetails() == null) {
            return new ProgrammeDetails();
        }
        return applicationForm.getProgrammeDetails();
    }

    @ModelAttribute("user")
    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }
}