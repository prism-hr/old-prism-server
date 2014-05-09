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

import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EntityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramTypePropertyEditor;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
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
    private ImportedEntityService importedEntityService;

    @Autowired
    private InstitutionDAO qualificationInstitutionDAO;

    @Autowired
    private EntityPropertyEditor<Domicile> domicilePropertyEditor;

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
                        qualificationInstitutionDAO.getByDomicile(opportunityRequest.getInstitutionCountry()));
            }
            request.setAttribute(CLICKED_ON_CREATE_OPPORTUNITY, true);
            return LOGIN_PAGE;
        }

        opportunitiesService.createOpportunityRequest(opportunityRequest, true);

        model.addAttribute("pendingUser", opportunityRequest.getAuthor());
        return OPPORTUNITY_REQUEST_COMPLETE_VIEW_NAME;
    }

    @ModelAttribute("countries")
    public List<Domicile> getAllDomiciles() {
        return importedEntityService.getAllDomiciles();
    }

    @ModelAttribute("opportunityRequest")
    public OpportunityRequest getOpportunityRequest() {
        OpportunityRequest opportunityRequest = new OpportunityRequest();
        opportunityRequest.setAuthor(new User());
        return opportunityRequest;
    }

    @ModelAttribute("institutions")
    public List<Institution> getEmptyQualificationInstitution() {
        return Collections.emptyList();
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
