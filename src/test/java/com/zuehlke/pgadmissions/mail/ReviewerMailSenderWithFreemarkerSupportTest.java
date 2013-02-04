package com.zuehlke.pgadmissions.mail;

import javax.mail.internet.MimeMessage;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testMailContext.xml")
public class ReviewerMailSenderWithFreemarkerSupportTest extends BaseEmailTestWithFreemarkerSupport {

    private ReviewerMailSender reviewerMailSender;
    
    @Test
    public void shouldSendCorrectLinks() {
        RegisteredUser adminOne = new RegisteredUserBuilder().email("bob@test.com").id(8).build();
        RegisteredUser adminTwo = new RegisteredUserBuilder().email("alice@test.com").id(9).build();
        RegisteredUser applicant = new RegisteredUserBuilder().id(10).firstName("Kevin").lastName("Denver").activationCode("123").build();
        RegisteredUser defaultReviewer = new RegisteredUserBuilder().id(11).firstName("Hanna").lastName("Hoopla").email("hanna.hoopla@test.com").activationCode("1234").build();
        
        ApplicationForm form = new ApplicationFormBuilder().id(4).program(new ProgramBuilder().title("prgTitle").administrators(adminOne, adminTwo).code("123").build()).applicant(applicant).applicationNumber("123").build();
        Reviewer reviewer = new ReviewerBuilder().id(4).user(defaultReviewer).reviewRound(new ReviewRoundBuilder().application(form).build()).build();
        
        fakeLoggingMailSender.registerListeners(new FakeLoggingMailSenderListener() {
            
            @Override
            public void onSubject(String subject) {
                Assert.assertEquals("REMINDER: Kevin Denver Application 123 for UCL prgTitle - Review Request", subject);
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
                boolean registrationUrl = body.contains("pgadmissions/reviewFeedback?applicationId=123&activationCode=1234");
                boolean normalUrl = body.contains("pgadmissions/decline/review?applicationId=123&activationCode=1234");
                if (!(registrationUrl && normalUrl)) {
                    Assert.fail("The mail message does not contain the appropriate links");
                }
            }
        });
        
        reviewerMailSender.sendReviewerReminder(reviewer);
    }
    
    @Before
    public void setup() {
        reviewerMailSender = new ReviewerMailSender(mimeMessagePreparatorFactory, fakeLoggingMailSender, messageSource);
    }
}
