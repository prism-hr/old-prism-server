package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

@RequestMapping("/update")
@Controller
public class RefereeController {

    private static final String STUDENTS_FORM_REFEREES_VIEW = "/private/pgStudents/form/components/references_details";
    private final RefereeService refereeService;
    private final DomicileService domicileService;
    private final ApplicationsService applicationsService;
    private final DomicilePropertyEditor domicilePropertyEditor;
    private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
    private final RefereeValidator refereeValidator;
    private final EncryptionHelper encryptionHelper;
    private final UserService userService;
    private final ApplicationFormAccessService accessService;

    public RefereeController() {
        this(null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public RefereeController(RefereeService refereeService, UserService userService, ApplicationsService applicationsService,
                    DomicilePropertyEditor domicilePropertyEditor, ApplicationFormPropertyEditor applicationFormPropertyEditor,
                    RefereeValidator refereeValidator, EncryptionHelper encryptionHelper, final ApplicationFormAccessService accessService,
                    DomicileService domicileService) {
        this.refereeService = refereeService;
        this.userService = userService;
        this.applicationsService = applicationsService;
        this.domicilePropertyEditor = domicilePropertyEditor;
        this.applicationFormPropertyEditor = applicationFormPropertyEditor;
        this.refereeValidator = refereeValidator;
        this.encryptionHelper = encryptionHelper;
        this.accessService = accessService;
        this.domicileService = domicileService;
    }

    @RequestMapping(value = "/editReferee", method = RequestMethod.POST)
    public String editReferee(String refereeId, @Valid Referee newReferee, BindingResult result, ModelMap modelMap) {

        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new InsufficientApplicationFormPrivilegesException(application.getApplicationNumber());
        }

        if (application.isDecided()) {
            throw new CannotUpdateApplicationException(application.getApplicationNumber());
        }

        Referee referee = getReferee(refereeId);
        if (result.hasErrors()) {
            newReferee.setId(referee.getId());
            modelMap.addAttribute("referee", newReferee);
            return STUDENTS_FORM_REFEREES_VIEW;
        }
        
        if(referee == null){
            referee = newReferee;
        } else {
            referee.setFirstname(newReferee.getFirstname());
            referee.setLastname(newReferee.getLastname());
            referee.setEmail(newReferee.getEmail());
            referee.setJobEmployer(newReferee.getJobEmployer());
            referee.setJobTitle(newReferee.getJobTitle());
            referee.setAddressLocation(newReferee.getAddressLocation());
            referee.setPhoneNumber(newReferee.getPhoneNumber());
            referee.setMessenger(newReferee.getMessenger());
        }
        
        if (!application.isSubmitted()) {
            refereeService.save(referee);
        } else if (application.isModifiable()) {
            refereeService.processRefereesRoles(Arrays.asList(referee));

        }

        application.addApplicationUpdate(new ApplicationFormUpdate(application, ApplicationUpdateScope.ALL_USERS, new Date()));
        application.setLastUpdated(new Date());
        accessService.updateAccessTimestamp(application, userService.getCurrentUser(), new Date());
        applicationsService.save(application);
        return "redirect:/update/getReferee?applicationId=" + application.getApplicationNumber();
    }

    @ModelAttribute("domiciles")
    public List<Domicile> getAllDomiciles() {
        return domicileService.getAllEnabledDomiciles();
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return application;
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

    @InitBinder(value = "referee")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(refereeValidator);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    public Referee getReferee(String refereeId) {
        if (StringUtils.isBlank(refereeId)) {
            return null;
        }
        Integer id = encryptionHelper.decryptToInteger(refereeId);
        Referee referee = refereeService.getRefereeById(id);
        if (referee == null) {
            throw new ResourceNotFoundException();
        }
        return referee;
    }

    @RequestMapping(value = "/getReferee", method = RequestMethod.GET)
    public String getRefereeView(@RequestParam(required = false) String refereeId, ModelMap modelMap) {
        
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new InsufficientApplicationFormPrivilegesException(application.getApplicationNumber());
        }
        Referee referee = getReferee(refereeId);
        if(referee == null){
            referee = new Referee();
        }
        modelMap.put("referee", referee);
        return STUDENTS_FORM_REFEREES_VIEW;
    }

    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

}
