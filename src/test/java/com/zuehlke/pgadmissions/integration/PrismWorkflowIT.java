package com.zuehlke.pgadmissions.integration;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.integration.providers.ApplicationTestDataProvider;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.rest.domain.ResourceRepresentation;
import com.zuehlke.pgadmissions.rest.dto.RegistrationDetails;
import com.zuehlke.pgadmissions.services.*;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;
import com.zuehlke.pgadmissions.timers.XMLDataImportTask;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.beans.Introspector;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class PrismWorkflowIT {

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private XMLDataImportTask xmlDataImportTask;

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

        User applicant = registerAndActivateApplicant(PrismScope.PROGRAM, program.getId(), "Kuba", "Fibinger", "kuba@fibinger.pl");

        Comment createApplicationComment = new Comment().withCreatedTimestamp(new DateTime()).withUser(applicant);
        Application application = new Application().withInitialData(applicant, program, null);
        ActionOutcome actionOutcome = actionService.executeAction(application, PrismAction.PROGRAM_CREATE_APPLICATION, createApplicationComment);
        Application createdApplication = (Application) actionOutcome.getResource();
        assertEquals(PrismAction.APPLICATION_COMPLETE, actionOutcome.getNextAction());

        entityService.update(createdApplication);
        applicationTestDataProvider.fillWithData(createdApplication);

        Comment completeApplicationComment = null;
        actionOutcome = actionService.executeAction(createdApplication.getId(), PrismAction.APPLICATION_COMPLETE, completeApplicationComment);
        assertEquals(PrismAction.SYSTEM_VIEW_APPLICATION_LIST, actionOutcome.getNextAction());
        assertEquals(systemService.getSystem().getId(), actionOutcome.getResource().getId());

        Comment assignReviewerComment = new Comment().withUser(programAdministrator);
        actionService.executeAction(1, PrismAction.APPLICATION_ASSIGN_REVIEWERS, assignReviewerComment);

        mailSenderMock.verify();

    }

    private User registerAndActivateApplicant(PrismScope resourceType, int resourceId, String firstName, String lastName, String email) {
        User applicant = registrationService.submitRegistration(
                new RegistrationDetails().withFirstName(firstName).withLastName(lastName).withEmail(email).withPassword("password").withResourceType(resourceType).withResourceId(resourceId));
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
        xmlDataImportTask.importData();
    }

}
