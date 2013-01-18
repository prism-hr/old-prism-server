package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.ArrayList;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
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

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.QualificationsAdminEditDTO;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditSendToUclDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RefereesAdminEditDTOValidator;

//TODO: Kevin: Finish implementation of this controller by creating a referee comment on behalf of the referee and 
// write the unit tests for this controller.

@Controller
@RequestMapping("/editApplicationFormAsProgrammeAdmin")
public class EditApplicationFormAsProgrammeAdminController {

    private static final String VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_VIEW_NAME = "/private/staff/admin/application/main_application_page_programme_administrator";
    
    private static final String VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME = "/private/staff/admin/application/components/references_details_programme_admin";
    
    private final UserService userService;
    
    private final ApplicationsService applicationsService;
    
    private final DocumentPropertyEditor documentPropertyEditor;
    
    private final QualificationService qualificationService;
    
    private final RefereeService refereeService;
    
    private final RefereesAdminEditDTOValidator refereesAdminEditDTOValidator;
    
    private final EncryptionHelper encryptionHelper;
    
    public EditApplicationFormAsProgrammeAdminController() {
        this(null, null, null, null, null, null, null);
    }
    
    @Autowired
    public EditApplicationFormAsProgrammeAdminController(
            final UserService userService, final ApplicationsService applicationsService, final DocumentPropertyEditor documentPropertyEditor,
            final QualificationService qualificationService, final RefereeService refereeService,
            final RefereesAdminEditDTOValidator refereesAdminEditDTOValidator,
            EncryptionHelper encryptionHelper) {
        this.userService = userService;
        this.applicationsService = applicationsService;
        this.documentPropertyEditor = documentPropertyEditor;
        this.qualificationService = qualificationService;
        this.refereeService = refereeService;
        this.refereesAdminEditDTOValidator = refereesAdminEditDTOValidator;
        this.encryptionHelper = encryptionHelper;
    }
        
    @InitBinder(value = "refereesAdminEditDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(refereesAdminEditDTOValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor());
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String view(@RequestParam String applicationId, Model model) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        if (!isUserAllowedToSeeAndPost(applicationForm)) {
            throw new ResourceNotFoundException();
        }
        return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_VIEW_NAME;
    }
    
    @RequestMapping(value="/postRefereesData", method = RequestMethod.POST)
    public String submitRefereesData(@Valid @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO, BindingResult result, 
            @RequestParam String sendToUclData, @ModelAttribute ApplicationForm applicationForm, Model model) {
        
        model.addAttribute("editedRefereeId", refereesAdminEditDTO.getEditedRefereeId());

        if (result.hasErrors()) {
            return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
        }
        
        if (!isUserAllowedToSeeAndPost(applicationForm)) {
            throw new ResourceNotFoundException();
        }
        
        if (StringUtils.isNotBlank(sendToUclData)) {
            Gson gson = new Gson();
            RefereesAdminEditSendToUclDTO refereesData = gson.fromJson(sendToUclData, RefereesAdminEditSendToUclDTO.class);
            ArrayList<Integer> decryptedIds = new ArrayList<Integer>(2);
            for (String encryptedId : refereesData.getReferees()) {
                decryptedIds.add(encryptionHelper.decryptToInteger(encryptedId));
            }
            refereeService.selectForSendingToPortico(applicationForm.getApplicationNumber(), decryptedIds);
        }
        
        return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
    }
    
    @RequestMapping(value="/postQualificationsData", method = RequestMethod.POST)
    @ResponseBody
    public String submitQualificationsData(@RequestParam final String sendToUclData, @ModelAttribute ApplicationForm applicationForm) {
        if (StringUtils.isBlank(sendToUclData)) {
            throw new ResourceNotFoundException();
        }
        
        Gson gson = new Gson();
        QualificationsAdminEditDTO qualificationsData = gson.fromJson(sendToUclData, QualificationsAdminEditDTO.class);

        if (!isUserAllowedToSeeAndPost(applicationForm)) {
            throw new ResourceNotFoundException();
        }
        
        ArrayList<Integer> decryptedIds = new ArrayList<Integer>(2);
        for (String encryptedId : qualificationsData.getQualifications()) {
            decryptedIds.add(encryptionHelper.decryptToInteger(encryptedId));
        }
        
        qualificationService.selectForSendingToPortico(applicationForm.getApplicationNumber(), decryptedIds);
        return "OK";
    }
    
    @ModelAttribute(value = "refereesAdminEditDTO")
    public RefereesAdminEditDTO getRefereesAdminEditDTO() {
        return new RefereesAdminEditDTO();
    }
    
    private boolean isUserAllowedToSeeAndPost(final ApplicationForm applicationForm) {
        return getCurrentUser().isAdminInProgramme(applicationForm.getProgram()) 
                && applicationForm.isSubmitted()
                && !applicationForm.isInValidationStage()
                && !applicationForm.isDecided() 
                && !applicationForm.isWithdrawn();
    }
    
    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }
    
    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }
    
    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null || !getCurrentUser().canSee(applicationForm)) {
            throw new ResourceNotFoundException();
        }
        return applicationForm;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {       
        return getCurrentUser();
    }
}
