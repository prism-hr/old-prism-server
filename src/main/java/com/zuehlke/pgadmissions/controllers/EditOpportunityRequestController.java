package com.zuehlke.pgadmissions.controllers;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
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

    @RequestMapping(value = "/{requestId}", method = RequestMethod.GET)
    public String getEditOpportunityRequestPage(@PathVariable("requestId") Integer requestId, ModelMap modelMap) {
        OpportunityRequest opportunityRequest = opportunitiesService.getOpportunityRequest(requestId);
        opportunityRequest.computeStudyDurationNumberAndUnit();

        modelMap.addAttribute("opportunityRequest", opportunityRequest);

        if (opportunityRequest.getInstitutionCountry() != null) {
            modelMap.addAttribute("institutions",
                    qualificationInstitutionDAO.getEnabledInstitutionsByCountryCode(opportunityRequest.getInstitutionCountry().getCode()));
        }

        return EDIT_REQUEST_PAGE_VIEW_NAME;
    }

    @RequestMapping(value = "/{requestId}", method = RequestMethod.POST)
    public String approveOrRejectOpportunityRequest(@PathVariable("requestId") Integer requestId, @Valid OpportunityRequest opportunityRequest,
            BindingResult bindingResult, @RequestParam String editAction, ModelMap modelMap) {
        if ("approve".equals(editAction)) {
            if (bindingResult.hasErrors()) {
                RegisteredUser author = opportunitiesService.getOpportunityRequest(requestId).getAuthor();
                opportunityRequest.setAuthor(author);
                modelMap.addAttribute("opportunityRequest", opportunityRequest);

                if (opportunityRequest.getInstitutionCountry() != null) {
                    modelMap.addAttribute("institutions",
                            qualificationInstitutionDAO.getEnabledInstitutionsByCountryCode(opportunityRequest.getInstitutionCountry().getCode()));
                }

                return EDIT_REQUEST_PAGE_VIEW_NAME;
            }
            opportunityRequest.computeStudyDuration();
            opportunitiesService.approveOpportunityRequest(requestId, opportunityRequest);
        } else if ("reject".equals(editAction)) {
            opportunitiesService.rejectOpportunityRequest(requestId);
        }
        return "redirect:/requests";
    }

    @InitBinder(value = "opportunityRequest")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(opportunityRequestValidator);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
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
