package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.springframework.beans.BeanUtils.copyProperties;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.dto.DocumentsSectionDTO;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.DocumentSectionValidator;

@Controller
@RequestMapping("/update")
public class DocumentsController {

    private static final String STUDENTS_FORM_DOCUMENTS_VIEW = "/private/pgStudents/form/components/documents";

    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private DocumentSectionValidator documentSectionValidator;

    @Autowired
    private DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @Autowired
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @RequestMapping(value = "/editDocuments", method = RequestMethod.POST)
    public String editDocuments(@Valid DocumentsSectionDTO documentsSectionDTO,
            BindingResult result, ModelMap modelMap) {
        
        if (result.hasErrors()) {
            modelMap.put("documentsSectionDTO", documentsSectionDTO);
            return STUDENTS_FORM_DOCUMENTS_VIEW;
        }
        ApplicationForm applicationForm = (ApplicationForm) modelMap.get("applicationForm");
        
        return "redirect:/update/getDocuments?applicationId=" + applicationForm.getApplicationNumber();
    }

    @RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
    public String getDocumentsView(ModelMap modelMap) {
        ApplicationForm application = (ApplicationForm) modelMap.get("applicationForm");
        DocumentsSectionDTO documentsSectionDTO = new DocumentsSectionDTO();
        copyProperties(application, documentsSectionDTO, DocumentsSectionDTO.class);
        modelMap.put("documentsSectionDTO", documentsSectionDTO);
        return STUDENTS_FORM_DOCUMENTS_VIEW;
    }

    @ModelAttribute("applicationForm")
    public ApplicationForm getApplicationForm(String applicationId) {
        return applicationsService.getSecuredApplicationForm(applicationId, ApplicationFormAction.COMPLETE_APPLICATION,
                ApplicationFormAction.CORRECT_APPLICATION);
    }
    
    @InitBinder("documentsSectionDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(documentSectionValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
    }

    @ModelAttribute("message")
    public String getMessage(@RequestParam(required = false) String message) {
        return message;
    }

}
