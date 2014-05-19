package com.zuehlke.pgadmissions.workflow;

import org.apache.commons.lang.WordUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
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

    @Autowired
    private MailSenderMock mailSenderMock;

    @Test
    public void runWorkflowTest() throws Exception {
        initializeData();

        Program program = programService.getAllEnabledPrograms().get(0);

        User programAdministrator = manageUsersService.setUserRoles("Jerzy", "Urban", "jerzy@urban.pl", true, program, Authority.PROGRAM_ADMINISTRATOR);

        User applicant = registrationService.submitRegistration(new User().withFirstName("Kuba").withLastName("Fibinger").withEmail("kuba@fibinger.pl")
                .withAccount(new UserAccount().withPassword("password")), program);
        mailSenderMock.assertEmailSent(applicant, NotificationTemplateId.SYSTEM_COMPLETE_REGISTRATION_REQUEST);

        applicant = registrationService.activateAccount(applicant.getActivationCode(), ApplicationFormAction.PROGRAM_CREATE_APPLICATION, program.getId());

        Comment createApplicationComment = null;
        ActionOutcome actionOutcome = actionService.executeAction(program.getId(), applicant, ApplicationFormAction.PROGRAM_CREATE_APPLICATION,
                createApplicationComment);
        System.out.println(actionOutcome.createRedirectionUrl());

        
        Comment completeApplicationComment = null;
        actionOutcome = actionService.executeAction(1, applicant, ApplicationFormAction.APPLICATION_COMPLETE, completeApplicationComment);
        System.out.println(actionOutcome.createRedirectionUrl());

        Comment assignReviewerComment = new Comment();
        actionService.executeAction(1, programAdministrator, ApplicationFormAction.APPLICATION_ASSIGN_REVIEWERS, assignReviewerComment);

        mailSenderMock.verify();
    }

    private void initializeData() {
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
