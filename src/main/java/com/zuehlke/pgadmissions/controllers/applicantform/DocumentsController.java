package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.DocumentSectionValidator;

@Controller
@RequestMapping("/update")
public class DocumentsController {

    private static final String STUDENTS_FORM_DOCUMENTS_VIEW = "/private/pgStudents/form/components/documents";
    
    private final ApplicationsService applicationsService;
    
    private final DocumentSectionValidator documentSectionValidator;
    
    private final DocumentPropertyEditor documentPropertyEditor;
    
    private final UserService userService;
    
    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    public DocumentsController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public DocumentsController(ApplicationsService applicationsService, UserService userService,
            DocumentSectionValidator documentSectionValidator, DocumentPropertyEditor documentPropertyEditor, final ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.documentSectionValidator = documentSectionValidator;
        this.documentPropertyEditor = documentPropertyEditor;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    @RequestMapping(value = "/editDocuments", method = RequestMethod.POST)
    public String editDocuments(ApplicationForm applicationForm, BindingResult result) {
        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }
        
        if (applicationForm.isDecided()) {
            throw new CannotUpdateApplicationException(applicationForm.getApplicationNumber());
        }
        
        if (applicationForm.getPersonalStatement() == null) {
            result.rejectValue("personalStatement", "file.upload.empty");
        }
        
        if (result.hasErrors()) {
            return STUDENTS_FORM_DOCUMENTS_VIEW;
        }
        
        applicationsService.save(applicationForm);
        applicationFormUserRoleService.registerApplicationUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS);
        return "redirect:/update/getDocuments?applicationId=" + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
    public String getDocumentsView() {

        if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
            throw new ResourceNotFoundException();
        }
        return STUDENTS_FORM_DOCUMENTS_VIEW;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (application == null || !getCurrentUser().canSee(application)) {
            throw new ResourceNotFoundException();
        }
        return application;
    }

    @InitBinder(value = "applicationForm")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(documentSectionValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);

    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }
}
