package com.zuehlke.pgadmissions.controllers;

import java.util.Date;
import java.util.List;

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

import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
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
    private DomicileService domicileService;

    @Autowired
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    @Autowired
    private DomicilePropertyEditor domicilePropertyEditor;

    @Autowired
    private OpportunityRequestValidator opportunityRequestValidator;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private DatePropertyEditor datePropertyEditor;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/{requestId}", method = RequestMethod.GET)
    public String getEditOpportunityRequestPage(@PathVariable("requestId") Integer requestId, ModelMap modelMap) {
        OpportunityRequest opportunityRequest = opportunitiesService.getOpportunityRequest(requestId);

        // force to recompute study duration number and unit
        opportunityRequest.setStudyDuration(opportunityRequest.getStudyDuration());

        List<OpportunityRequest> opportunityRequests = opportunitiesService.getAllRelatedOpportunityRequests(opportunityRequest);
        OpportunityRequestComment comment = new OpportunityRequestComment();

        modelMap.addAttribute("opportunityRequest", opportunityRequest);
        modelMap.addAttribute("opportunityRequests", opportunityRequests);
        modelMap.addAttribute("comment", comment);

        if (opportunityRequest.getInstitutionCountry() != null) {
            modelMap.addAttribute("institutions",
                    qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode(opportunityRequest.getInstitutionCountry().getCode()));
        }

        return EDIT_REQUEST_PAGE_VIEW_NAME;
    }

    @RequestMapping(value = "/{requestId}", method = RequestMethod.POST)
    public Object respondToOpportunityRequest(@PathVariable("requestId") Integer requestId, @Valid OpportunityRequest opportunityRequest,
            BindingResult requestBindingResult, @ModelAttribute("comment") @Valid OpportunityRequestComment comment, BindingResult commentBindingResult,
            ModelMap modelMap) {
        if (requestBindingResult.hasErrors() || commentBindingResult.hasErrors()) {
            OpportunityRequest existingRequest = opportunitiesService.getOpportunityRequest(requestId);

            opportunityRequest.setAuthor(existingRequest.getAuthor());
            opportunityRequest.setCreatedDate(existingRequest.getCreatedDate());
            opportunityRequest.setStatus(existingRequest.getStatus());
            opportunityRequest.setType(existingRequest.getType());

            List<OpportunityRequest> opportunityRequests = opportunitiesService.getAllRelatedOpportunityRequests(opportunityRequest);

            modelMap.put("opportunityRequest", opportunityRequest);
            modelMap.addAttribute("opportunityRequests", opportunityRequests);
            modelMap.put("comment", comment);

            if (opportunityRequest.getInstitutionCountry() != null) {
                modelMap.addAttribute("institutions",
                        qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode(opportunityRequest.getInstitutionCountry().getCode()));
            }

            return EDIT_REQUEST_PAGE_VIEW_NAME;
        }
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
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("countries")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileService.getAllEnabledDomicilesExceptAlternateValues();
    }

    @ModelAttribute("studyOptions")
    public List<StudyOption> getDistinctStudyOptions() {
        return programInstanceService.getDistinctStudyOptions();
    }

    @ModelAttribute("advertisingDeadlines")
    public List<Integer> getAdvertisingDeadlines() {
        return programInstanceService.getPossibleAdvertisingDeadlineYears();
    }

}
