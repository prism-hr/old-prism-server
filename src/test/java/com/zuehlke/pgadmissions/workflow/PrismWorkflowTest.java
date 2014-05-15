package com.zuehlke.pgadmissions.workflow;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.WordUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AssignReviewersComment;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ManageUsersService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;
import com.zuehlke.pgadmissions.timers.XMLDataImportTask;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class PrismWorkflowTest {

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private XMLDataImportTask xmlDataImportTask;

    @Autowired
    private EntityService entityService;

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

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private ActionService actionService;

    @Test
    public void initializeWorkflowTest() throws Exception {
        User superadmin = manageUsersService.setUserRoles("Jozef", "Oleksy", "jozek@oleksy.pl", true, roleService.getPrismSystem(),
                Authority.SYSTEM_ADMINISTRATOR);

        assertTrue(roleService.hasRole(superadmin, Authority.SYSTEM_PROGRAM_CREATOR, roleService.getPrismSystem()));

        for (ImportedEntityFeed feed : entityImportService.getImportedEntityFeeds()) {
            String entityName = WordUtils.uncapitalize(feed.getImportedEntityType().getEntityClass().getSimpleName());
            String url = "reference_data/2014-05-08/" + entityName + ".xml";
            feed.setLocation(url);
            entityService.update(feed);
        }
        xmlDataImportTask.importData();

        InstitutionDomicile polishDomicile = entityService.getBy(InstitutionDomicile.class, "code", "PL");
        ProgramType programType = programService.getProgramTypes().iterator().next();

        User programCreator = new User().withFirstName("Jerzy").withLastName("Urban").withEmail("jerzy@urban.pl")
                .withAccount(new UserAccount().withPassword("password").withConfirmPassword("password"));

        Program program = programService.getAllEnabledPrograms().get(0);

        User applicant = registrationService.submitRegistration(new User().withFirstName("Kuba").withLastName("Fibinger").withEmail("kuba@fibinger.pl").withAccount(new UserAccount().withPassword("password")));

        // TODO assert that (program|project)_create_application action exists

        applicant = registrationService.activateAccount(applicant.getActivationCode());

        actionService.executeAction(applicant, ApplicationFormAction.PROGRAM_CREATE_APPLICATION, program.getId());

        // TODO assert that application_complete action exists

//        applicationFormService.submitApplication(application);
//
//        AssignReviewersComment assignReviewerComment = new AssignReviewersComment();
//        assignReviewerComment.setContent("Assigning reviewers");
//        reviewService.moveApplicationToReview(application.getId(), assignReviewerComment);

    }

}
