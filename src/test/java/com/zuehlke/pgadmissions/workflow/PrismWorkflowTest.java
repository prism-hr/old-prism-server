package com.zuehlke.pgadmissions.workflow;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.WordUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
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
    private ApplicationService applicationFormService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private MailSenderMock mailSenderMock;
    
    @Autowired
    private ApplicationTestDataProvider applicationTestDataProvider;

    @Test
    public void runWorkflowTest() throws Exception {
        Program program = programService.getAllEnabledPrograms().get(0);

        User programAdministrator = manageUsersService.setUserRoles("Jerzy", "Urban", "jerzy@urban.pl", true, program, Authority.PROGRAM_ADMINISTRATOR);

        User applicant = registerAndActivateApplicant(program, "Kuba", "Fibinger", "kuba@fibinger.pl");

        Comment createApplicationComment = null;
        ActionOutcome actionOutcome = actionService.executeAction(program.getId(), applicant, ApplicationFormAction.PROGRAM_CREATE_APPLICATION,
                createApplicationComment);
        Application createdApplication = (Application) actionOutcome.getScope();
        assertEquals(ApplicationFormAction.APPLICATION_COMPLETE, actionOutcome.getNextAction());

        applicationTestDataProvider.fillWithData(createdApplication);
        
        Comment completeApplicationComment = null;
        actionOutcome = actionService.executeAction(createdApplication.getId(), applicant, ApplicationFormAction.APPLICATION_COMPLETE,
                completeApplicationComment);
        assertEquals(ApplicationFormAction.SYSTEM_VIEW_APPLICATION_LIST, actionOutcome.getNextAction());
        assertEquals(roleService.getPrismSystem().getId(), actionOutcome.getScope().getId());

        Comment assignReviewerComment = new Comment();
        actionService.executeAction(1, programAdministrator, ApplicationFormAction.APPLICATION_ASSIGN_REVIEWERS, assignReviewerComment);

        mailSenderMock.verify();
    }

    private User registerAndActivateApplicant(Advert advert, String firstName, String lastName, String email) {
        User applicant = registrationService.submitRegistration(
                new User().withFirstName(firstName).withLastName(lastName).withEmail(email).withAccount(new UserAccount().withPassword("password")), advert);
        mailSenderMock.assertEmailSent(applicant, NotificationTemplateId.SYSTEM_COMPLETE_REGISTRATION_REQUEST);

        applicant = registrationService.activateAccount(applicant.getActivationCode(), ApplicationFormAction.PROGRAM_CREATE_APPLICATION, advert.getId());
        return applicant;
    }

    @Before
    public void initializeData() {
        User superadmin = manageUsersService.setUserRoles("Jozef", "Oleksy", "jozek@oleksy.pl", true, roleService.getPrismSystem(),
                Authority.SYSTEM_ADMINISTRATOR);

        for (ImportedEntityFeed feed : entityImportService.getImportedEntityFeeds()) {
            String entityName = WordUtils.uncapitalize(feed.getImportedEntityType().getEntityClass().getSimpleName());
            String url = "reference_data/2014-05-08/" + entityName + ".xml";
            feed.setLocation(url);
            entityService.update(feed);
        }
        xmlDataImportTask.importData();
    }

}
