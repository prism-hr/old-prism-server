package com.zuehlke.pgadmissions.controllers;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.OpportunityRequestValidator;

@Controller
@RequestMapping(value = { "/createOpportunity" })
public class CreateNewOpportunityController {

    private static final String LOGIN_PAGE = "public/login/login_page";

    public static final String CLICKED_ON_CREATE_OPPORTUNITY = "CLICKED_ON_CREATE_OPPORTUNITY";

    @Autowired
    private UserService userService;

    @Autowired
    private DomicileDAO domicileDAO;

    @Autowired
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    @Autowired
    private DomicilePropertyEditor domicilePropertyEditor;

    @Autowired
    private OpportunityRequestValidator opportunityRequestValidator;

    @InitBinder(value = "opportunityRequest")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(opportunityRequestValidator);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getNewOpportunityPage(final HttpServletRequest request, final HttpServletResponse response, final HttpSession session) {
        request.setAttribute(CLICKED_ON_CREATE_OPPORTUNITY, true);
        return LOGIN_PAGE;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String postOpportunityRequest(@Valid OpportunityRequest opportunityRequest, BindingResult result, Model model, HttpServletRequest request) {

        if (result.hasErrors()) {
            if (opportunityRequest.getInstitutionCountry() != null) {
                model.addAttribute("institutions",
                        qualificationInstitutionDAO.getEnabledInstitutionsByCountryCode(opportunityRequest.getInstitutionCountry().getCode()));
            }
            request.setAttribute(CLICKED_ON_CREATE_OPPORTUNITY, true);
            return LOGIN_PAGE;
        }
        
        return LOGIN_PAGE;
    }

    @ModelAttribute("countries")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileDAO.getAllEnabledDomicilesExceptAlternateValues();
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

}