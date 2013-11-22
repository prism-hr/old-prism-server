package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.utils.FieldErrorUtils;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

@RequestMapping("/delegate")
@Controller
public class DelegateToApplicationAdministratorController {

    private final ApplicationsService applicationsService;
    private final NewUserByAdminValidator newUserByAdminValidator;
    private final MessageSource messageSource;

    DelegateToApplicationAdministratorController() {
        this(null, null, null, null);
    }

    @Autowired
    public DelegateToApplicationAdministratorController(ApplicationsService applicationsService, 
    		NewUserByAdminValidator newUserByAdminValidator, CommentService commentService, MessageSource messageSource) {
        this.applicationsService = applicationsService;
        this.newUserByAdminValidator = newUserByAdminValidator;
        this.messageSource = messageSource;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        return applicationForm;
    }

    @ModelAttribute("delegatedAdministrator")
    public RegisteredUser getDelegatedInterview() {
        return new RegisteredUser();
    }

    @InitBinder(value = "delegatedAdministrator")
    public void registerPropertyEditors(WebDataBinder dataBinder) {
        dataBinder.setValidator(newUserByAdminValidator);
        dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> delegateToApplicationAdministrator(@ModelAttribute("applicationForm") ApplicationForm applicationForm,
            @Valid @ModelAttribute("delegatedAdministrator") RegisteredUser delegatedAdministrator, BindingResult delegatedAdministratorResult) {
        Map<String, Object> result = Maps.newHashMap();

        if (delegatedAdministratorResult.hasErrors()) {
            result.put("success", "false");
            result.putAll(FieldErrorUtils.populateMapWithErrors(delegatedAdministratorResult, messageSource));
            return result;
        }
        
        result.put("success", "true");
        return result;
    }
}