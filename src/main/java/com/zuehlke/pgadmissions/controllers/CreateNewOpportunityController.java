package com.zuehlke.pgadmissions.controllers;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramTypePropertyEditor;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.OpportunityRequestValidator;

@Controller
@RequestMapping(value = { "/createOpportunity" })
public class CreateNewOpportunityController {

    static final String LOGIN_PAGE = "public/login/login_page";

    static final String OPPORTUNITY_REQUEST_COMPLETE_VIEW_NAME = "public/register/opportunity_request_complete";

    static final String CLICKED_ON_CREATE_OPPORTUNITY = "CLICKED_ON_CREATE_OPPORTUNITY";

    @Autowired
    private UserService userService;

    @Autowired
    private DomicileService domicileService;

    @Autowired
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    @Autowired
    private DomicilePropertyEditor domicilePropertyEditor;

    @Resource(name = "opportunityRequestValidator")
    private OpportunityRequestValidator opportunityRequestValidator;

    @Autowired
    private OpportunitiesService opportunitiesService;

    @Autowired
    private DatePropertyEditor datePropertyEditor;

    @Autowired
    private ProgramService programsService;
    
    @Autowired
    private ProgramInstanceService programInstanceService;
    
    @Autowired
    private ProgramTypePropertyEditor programTypePropertyEditor;

    @InitBinder(value = "opportunityRequest")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(opportunityRequestValidator);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(ProgramType.class, programTypePropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getNewOpportunityPage(final HttpServletRequest request) {
        request.setAttribute(CLICKED_ON_CREATE_OPPORTUNITY, true);
        return LOGIN_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String postOpportunityRequest(@Valid OpportunityRequest opportunityRequest, BindingResult result, Model model, HttpServletRequest request) {

        if (result.hasErrors()) {
            if (opportunityRequest.getInstitutionCountry() != null) {
                model.addAttribute("institutions",
                        qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode(opportunityRequest.getInstitutionCountry().getCode()));
            }
            request.setAttribute(CLICKED_ON_CREATE_OPPORTUNITY, true);
            return LOGIN_PAGE;
        }

        opportunitiesService.createOpportunityRequest(opportunityRequest, true);

        model.addAttribute("pendingUser", opportunityRequest.getAuthor());
        return OPPORTUNITY_REQUEST_COMPLETE_VIEW_NAME;
    }

    @ModelAttribute("countries")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileService.getAllEnabledDomicilesExceptAlternateValues();
    }

    @ModelAttribute("opportunityRequest")
    public OpportunityRequest getOpportunityRequest() {
        OpportunityRequest opportunityRequest = new OpportunityRequest();
        opportunityRequest.setAuthor(new RegisteredUser());
        return opportunityRequest;
    }

    @ModelAttribute("institutions")
    public List<QualificationInstitution> getEmptyQualificationInstitution() {
        return Collections.emptyList();
    }

    @ModelAttribute("studyOptions")
    public List<StudyOption> getDistinctStudyOptions() {
        return programInstanceService.getDistinctStudyOptions();
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
