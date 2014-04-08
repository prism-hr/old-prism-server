package com.zuehlke.pgadmissions.workflow;

import java.security.AuthProvider;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.ManageUsersService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.importers.Importer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class PrismWorkflowTest {

    @Autowired
    private List<Importer> importers;

    @Autowired
    private DomicileService domicileService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ManageUsersService manageUsersService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private OpportunitiesService opportunitiesService;
    
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private ApplicationFormService applicationFormService;
    
    @Test
    public void initializeWorkflowTest() throws Exception {
        RegisteredUser superadmin = manageUsersService.setUserRoles("Jozef", "Oleksy", "jozek@oleksy.pl", true, true,
                manageUsersService.getPrismSystem(), Authority.SUPERADMINISTRATOR);

        for (Importer importer : importers) {
            importer.importData();
        }

        Domicile polishDomicile = domicileService.getEnabledDomicileByCode("PL");
        ProgramType programType = programService.getProgramTypes().iterator().next();

        OpportunityRequest opportunityRequest = opportunitiesService.createOpportunityRequest(
                new OpportunityRequestBuilder()
                        .institutionCountry(polishDomicile)
                        .institutionCode(null)
                        .otherInstitution("Akademia Gorniczo-Hutnicza")
                        .programType(programType)
                        .programTitle("Zywienie zbiorowe")
                        .programDescription("I tak pracy po tym nie znajdziesz.")
                        .studyOptions("F++++,P++++")
                        .studyDuration(18)
                        .advertisingDeadlineYear(new DateTime().getYear() + 3)
                        .author(new RegisteredUserBuilder().firstName("Jerzy").lastName("Urban").email("jerzy@urban.pl").password("password")
                                .confirmPassword("password").build()).build(), false);

        
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("jozek@oleksy.pl", "password"));
        
        Program savedProgram = opportunitiesService.respondToOpportunityRequest(opportunityRequest.getId(), opportunityRequest,
                new OpportunityRequestCommentBuilder().commentType(OpportunityRequestCommentType.APPROVE).content("Ok!").build());
        
        RegisteredUser applicant = registrationService.submitRegistration(new RegisteredUserBuilder().firstName("Kuba").lastName("Fibinger").email("kuba@fibinger.pl").build());
        applicant = registrationService.activateAccount(applicant.getActivationCode());
        
        ApplicationForm application = applicationFormService.getOrCreateApplication(applicant, savedProgram.getId());
        applicationFormService.submitApplication(application);
        
        
        

    }

}
