package com.zuehlke.pgadmissions.mail;

import java.io.IOException;

import javax.mail.internet.MimeMessage;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.ConfigurationService;

import freemarker.template.TemplateException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testMailContext.xml")
public class AdminMailSenderWithFreemarkerSupportTest extends BaseEmailTestWithFreemarkerSupport {

    private ConfigurationService personServiceMock;
    
    private AdminMailSender adminMailSender;

    @Test
    public void shouldSendApproverEmailWithRegistrationLink() throws IOException {
        RegisteredUser admin = new RegisteredUserBuilder().id(1).email("ked@zuhlke.com").firstName("Kevin").lastName("Denver").enabled(false).activationCode("1").build();
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Smith").email("jane.smith@test.com").enabled(true).activationCode("2").id(10).build();
        RegisteredUser approver = new RegisteredUserBuilder().firstName("George").lastName("Smith").email("george.smith@test.com").enabled(true).activationCode("3").id(90).build();

        RegisteredUser supervisorUser1 = new RegisteredUserBuilder().id(1).firstName("benny").lastName("brack").email("bb@test.com").enabled(true).activationCode("4").build();
        Supervisor supervisor1 = new SupervisorBuilder().id(1).user(supervisorUser1).build();
        ApprovalRound previousApprovalRound = new ApprovalRoundBuilder().id(1).supervisors(supervisor1).build();

        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(3).firstName("Fred").lastName("Forse").email("Forse@test.com").enabled(true).activationCode("5").build();
        Supervisor supervisor3 = new SupervisorBuilder().id(3).user(interviewerUser2).build();

        ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().id(1).supervisors(supervisor3).build();

        Program program = new ProgramBuilder().title("prg").administrators(admin).approver(approver, admin).build();
        ApplicationForm application = new ApplicationFormBuilder().id(4).approvalRounds(previousApprovalRound, latestApprovalRound)
                .latestApprovalRound(latestApprovalRound).applicationNumber("007").applicant(applicant)
                .program(program).build();

        String expTemplate = "private/approvers/mail/application_approval_reminder.ftl";
        
        fakeLoggingMailSender.registerListeners(new FakeLoggingMailSenderListener() {
            
            @Override
            public void onSubject(String subject) {
                Assert.assertEquals("REMINDER: Jane Smith Application 007 for UCL prg - Approval Request", subject);
            }
            
            @Override
            public void onSender(String sender) {
            }
            
            @Override
            public void onRecipient(String recipient) {
            }
            
            @Override
            public void onDoSend(MimeMessage message) {
            }
            
            @Override
            public void onDoSend(String message) {
            }
            
            @Override
            public void onBody(String body) {
                boolean registrationUrl = body.contains("pgadmissions/register?activationCode=1&directToUrl=/approved/moveToApproved?applicationId=007&activationCode=1");
                boolean normalUrl = body.contains("pgadmissions/approved/moveToApproved?applicationId=007&activationCode=3");
                if (!(registrationUrl ^ normalUrl)) {
                    Assert.fail("The mail message does not contain the appropriate links");
                }
            }
        });
        
        adminMailSender.sendMailsForApplication(application, "approval.request.reminder", expTemplate, NotificationType.APPROVAL_REMINDER);
    }
    
    @Before
    public void setup() throws IOException, TemplateException {
        fakeLoggingMailSender = new FakeLoggingMailSender();
        personServiceMock = EasyMock.createMock(ConfigurationService.class);
        adminMailSender = new AdminMailSender(mimeMessagePreparatorFactory, fakeLoggingMailSender, messageSource, personServiceMock);
    }
}
