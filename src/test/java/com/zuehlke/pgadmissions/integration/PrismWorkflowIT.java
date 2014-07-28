package com.zuehlke.pgadmissions.integration;

import static org.junit.Assert.assertEquals;

import java.beans.Introspector;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.integration.providers.ApplicationTestDataProvider;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.rest.dto.RegistrationDetails;
import com.zuehlke.pgadmissions.rest.representation.ResourceRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.ReviewService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;
import com.zuehlke.pgadmissions.timers.MaintenanceTask;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class PrismWorkflowIT {

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private MaintenanceTask maintenanceTask;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RegistrationService registrationService;

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

    @Autowired
    private SystemService systemService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Test
    public void runWorkflowTest() throws Exception {
        Program program = programService.getAllEnabledPrograms().get(0);

        User programAdministrator = userService.getOrCreateUserWithRoles("Jerzy", "Urban", "jerzy@urban.pl", program, Lists.newArrayList(new ResourceRepresentation.RoleRepresentation(PrismRole.PROGRAM_ADMINISTRATOR, true)));

        User applicant = registerAndActivateUser(PrismAction.PROGRAM_CREATE_APPLICATION, program.getId(), "Kuba", "Fibinger", "kuba@fibinger.pl");

        Comment createApplicationComment = new Comment().withCreatedTimestamp(new DateTime()).withUser(applicant);
        Application application = new Application().withInitialData(applicant, program, null);
        Action action = entityService.getByProperty(Action.class, "id", PrismAction.PROGRAM_CREATE_APPLICATION);
        ActionOutcome actionOutcome = actionService.executeUserAction(application, action, createApplicationComment);
        Application createdApplication = (Application) actionOutcome.getTransitionResource();
        assertEquals(PrismAction.APPLICATION_COMPLETE, actionOutcome.getNextAction());

        entityService.update(createdApplication);
        applicationTestDataProvider.fillWithData(createdApplication);

        Comment completeApplicationComment = null;
        action = entityService.getByProperty(Action.class, "id", PrismAction.APPLICATION_COMPLETE);
        actionOutcome = actionService.executeUserAction(createdApplication, action, completeApplicationComment);
        assertEquals(PrismAction.SYSTEM_VIEW_APPLICATION_LIST, actionOutcome.getNextAction());
        assertEquals(systemService.getSystem().getId(), actionOutcome.getTransitionResource().getId());

        Comment assignReviewerComment = new Comment().withUser(programAdministrator);
        action = entityService.getByProperty(Action.class, "id", PrismAction.APPLICATION_ASSIGN_REVIEWERS);
        actionService.executeUserAction(createdApplication, action, assignReviewerComment);

        mailSenderMock.verify();

    }

    private User registerAndActivateUser(PrismAction createAction, int resourceId, String firstName, String lastName, String email) {
        User applicant = registrationService.submitRegistration(
                new RegistrationDetails().withFirstName(firstName).withLastName(lastName).withEmail(email).withPassword("password").withAction(createAction).withResourceId(resourceId));
        mailSenderMock.assertEmailSent(applicant, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        applicant = registrationService.activateAccount(applicant.getActivationCode());
        return applicant;
    }

    @Before
    public void initializeData() {
        userService.getOrCreateUserWithRoles("Jozef", "Oleksy", "jozek@oleksy.pl", systemService.getSystem(), Lists.newArrayList(new ResourceRepresentation.RoleRepresentation(PrismRole.PROGRAM_ADMINISTRATOR, true)));

        for (ImportedEntityFeed feed : entityImportService.getImportedEntityFeeds()) {
            String entityName = Introspector.decapitalize(feed.getImportedEntityType().getEntityClass().getSimpleName());
            String url = "reference_data/2014-05-08/" + entityName + ".xml";
            feed.setLocation(url);
            entityService.update(feed);
        }
        maintenanceTask.importReferenceData();
    }

}
