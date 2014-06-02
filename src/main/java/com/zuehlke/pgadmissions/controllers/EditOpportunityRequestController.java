package com.zuehlke.pgadmissions.controllers;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.EntityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.PermissionsService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.OpportunityRequestValidator;

@Controller
@RequestMapping("/requests/edit")
public class EditOpportunityRequestController {

    protected static final String EDIT_REQUEST_PAGE_VIEW_NAME = "/private/staff/superAdmin/edit_opportunity_request";

    @Autowired
    private OpportunitiesService opportunitiesService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private InstitutionDAO institutionDAO;

    @Autowired
    private EntityPropertyEditor<Domicile> domicilePropertyEditor;

    @Resource(name = "opportunityRequestValidator")
    private OpportunityRequestValidator opportunityRequestValidator;

    @Autowired
    private ProgramService programsService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private LocalDatePropertyEditor datePropertyEditor;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PermissionsService permissionsService;

    @RequestMapping(value = "/{requestId}", method = RequestMethod.GET)
    public String getEditOpportunityRequestPage(@PathVariable("requestId") Integer requestId, ModelMap modelMap) {
        OpportunityRequest opportunityRequest = opportunitiesService.getOpportunityRequest(requestId);
        if (!permissionsService.canSeeOpportunityRequest(opportunityRequest)) {
            throw new ResourceNotFoundException();
        }

        // force to recompute study duration number and unit
        opportunityRequest.setStudyDuration(opportunityRequest.getStudyDuration());

        List<OpportunityRequest> opportunityRequests = opportunitiesService.getAllRelatedOpportunityRequests(opportunityRequest);
        OpportunityRequestComment comment = new OpportunityRequestComment();

        modelMap.addAttribute("opportunityRequest", opportunityRequest);
        modelMap.addAttribute("opportunityRequests", opportunityRequests);
        modelMap.addAttribute("comment", comment);

        if (opportunityRequest.getInstitutionCountry() != null) {
            modelMap.addAttribute("institutions", institutionDAO.getEnabledByDomicile(opportunityRequest.getInstitutionCountry()));
        }

        return EDIT_REQUEST_PAGE_VIEW_NAME;
    }

    @RequestMapping(value = "/{requestId}", method = RequestMethod.POST)
    public Object respondToOpportunityRequest(@PathVariable("requestId") Integer requestId, @Valid OpportunityRequest opportunityRequest,
            BindingResult requestBindingResult, @ModelAttribute("comment") @Valid OpportunityRequestComment comment, BindingResult commentBindingResult,
            ModelMap modelMap) {
        OpportunityRequest existingRequest = opportunitiesService.getOpportunityRequest(requestId);
        if (!permissionsService.canPostOpportunityRequestComment(existingRequest, comment)) {
            throw new ResourceNotFoundException();
        }

        if (requestBindingResult.hasErrors() || commentBindingResult.hasErrors()) {

            opportunityRequest.setAuthor(existingRequest.getAuthor());
            opportunityRequest.setCreatedDate(existingRequest.getCreatedDate());
            opportunityRequest.setStatus(existingRequest.getStatus());
            opportunityRequest.setType(existingRequest.getType());

            List<OpportunityRequest> opportunityRequests = opportunitiesService.getAllRelatedOpportunityRequests(opportunityRequest);

            modelMap.put("opportunityRequest", opportunityRequest);
            modelMap.addAttribute("opportunityRequests", opportunityRequests);
            modelMap.put("comment", comment);

            if (opportunityRequest.getInstitutionCountry() != null) {
                modelMap.addAttribute("institutions", institutionDAO.getEnabledByDomicile(opportunityRequest.getInstitutionCountry()));
            }

            return EDIT_REQUEST_PAGE_VIEW_NAME;
        }
        opportunityRequest.setAcceptingApplications(existingRequest.getAcceptingApplications());
        opportunitiesService.respondToOpportunityRequest(requestId, opportunityRequest, comment);
        return new RedirectView("/requests", true, true, false);
    }

    @InitBinder(value = "opportunityRequest")
    public void registerOpportunityRequestPropertyEditors(WebDataBinder binder) {
        binder.setValidator(opportunityRequestValidator);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @InitBinder(value = "comment")
    public void registerCommentPropertyEditors(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("countries")
    public List<Domicile> getAllDomiciles() {
        return importedEntityService.getAllDomiciles();
    }

    @ModelAttribute("studyOptions")
    public List<StudyOption> getDistinctStudyOptions() {
        return programInstanceService.getAvailableStudyOptions();
    }

    @ModelAttribute("advertisingDeadlines")
    public List<Integer> getAdvertisingDeadlines() {
        return programInstanceService.getPossibleAdvertisingDeadlineYears();
    }

    @ModelAttribute("programTypes")
    public List<ProgramType> getProgramTypes() {
        return programsService.getProgramTypes();
    }

}
