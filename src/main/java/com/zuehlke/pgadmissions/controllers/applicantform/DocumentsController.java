package com.zuehlke.pgadmissions.controllers.applicantform;

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
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
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
    
    public DocumentsController() {
        this(null, null, null, null, null);
    }

    @Autowired
    public DocumentsController(ApplicationsService applicationsService, UserService userService,
            DocumentSectionValidator documentSectionValidator, DocumentPropertyEditor documentPropertyEditor, final ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.applicationsService = applicationsService;
        this.documentSectionValidator = documentSectionValidator;
        this.documentPropertyEditor = documentPropertyEditor;
    }

    @RequestMapping(value = "/editDocuments", method = RequestMethod.POST)
    public String editDocuments(@ModelAttribute ApplicationForm applicationForm, BindingResult result) {
        if (applicationForm.getPersonalStatement() == null) {
            result.rejectValue("personalStatement", "file.upload.empty");
        }
        
        if (result.hasErrors()) {
            return STUDENTS_FORM_DOCUMENTS_VIEW;
        }
        
        
        return "redirect:/update/getDocuments?applicationId=" + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
    public String getDocumentsView(@ModelAttribute ApplicationForm applicationForm) {
        return STUDENTS_FORM_DOCUMENTS_VIEW;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationsService.getSecuredApplicationForm(applicationId, ApplicationFormAction.COMPLETE_APPLICATION,
                ApplicationFormAction.CORRECT_APPLICATION);
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

}
