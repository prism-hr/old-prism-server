package com.zuehlke.pgadmissions.mail;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.test.utils.MultiPartMimeMessageParser;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class MimeMessagePreparatorTest {

    private MimeMessagePreparatorFactory mimeMessagePreparatorFactory;

    private FreeMarkerConfig freeMarkerConfigMock;

    private Configuration configMock;

    private Map<String, Object> model;

    private String template;

    private String subject;

    private InternetAddress[] tos;

    private InternetAddress replyToAddress;

    @Before
    public void setUp() throws AddressException, UnsupportedEncodingException {
        template = "template";

        tos = new InternetAddress[] { new InternetAddress("email@bla.com"), new InternetAddress("dummy@test.com") };

        replyToAddress = new InternetAddress("replytome@test.com", "Jane Hurrah");

        model = new HashMap<String, Object>();

        subject = "subject";

        freeMarkerConfigMock = EasyMock.createMock(FreeMarkerConfig.class);

        configMock = EasyMock.createMock(Configuration.class);

        EasyMock.expect(freeMarkerConfigMock.getConfiguration()).andReturn(configMock);
    }

    @Test
    public void stripHtmlWithListingAndSendMultiPartMimeMessage() throws IOException, Exception {
        EasyMock.expect(configMock.getTemplate(template)).andReturn(new ApprovalOutcomeHtmlTemplate());
        EasyMock.replay(freeMarkerConfigMock, configMock);

        final ArrayList<MimeMessage> messages = new ArrayList<MimeMessage>();
        final FakeLoggingMailSender mailSender = new FakeLoggingMailSender() {
            @Override
            protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
                for (MimeMessage msg : mimeMessages) {
                    messages.add(msg);
                }
                super.doSend(mimeMessages, originalMessages);
            }
        };

        mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true);
        MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0], subject, template, model, replyToAddress);
        mailSender.send(msgPreparator);

        Assert.assertEquals(1, messages.size());
        
        MimeMessage message = messages.get(0);
        
        List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(message);

        String plainTextMessage = parsedMessage.get(0);
        
        Assert.assertEquals("Dear Alex, \n\nJohn Smith has approved Bill Gates Application RRDSCSSING01-2012-000005 for \nResearch Degree: Security and Crime Science. \n\nThe application has been advanced to UCL Admissions for verification. If this \nis successful, an offer of study will be issued. UCL Admissions aim to be \ncontact with their decision within 5 working days. \n\nShould you have any further questions on the application, you should refer \nthem to one of the following UCL Admissions contacts:\n\n * Green Giant <hohoho@greengiant.com> \n * Anna Hepcat <hepcat@test.com> \n * Polly Peterson <peterson@test.com> \n\nThe candidate number for UCL Admissions is --SitsApplicationId--. Please \nquote this number in all correspondence with them. \n\nYours sincerely,\nUCL Prism \n\n University College London, Gower Street, London, WC1E 6BT\n Tel: +44 (0) 20 7679 2000\n \u00a9 UCL 1999\u20132012 \n\nIf the links do not work in your email client copy and paste them into your browser.", plainTextMessage);
    }

    @Test
    public void stripHtmlWithButtonAndSendMultiPartMimeMessage() throws IOException, Exception {
        EasyMock.expect(configMock.getTemplate(template)).andReturn(new InterviewEvaluationRequestTemplate());
        EasyMock.replay(freeMarkerConfigMock, configMock);

        final ArrayList<MimeMessage> messages = new ArrayList<MimeMessage>();
        final FakeLoggingMailSender mailSender = new FakeLoggingMailSender() {
            @Override
            protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
                for (MimeMessage msg : mimeMessages) {
                    messages.add(msg);
                }
                super.doSend(mimeMessages, originalMessages);
            }
        };

        mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true);
        MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0], subject, template, model, replyToAddress);
        mailSender.send(msgPreparator);

        Assert.assertEquals(1, messages.size());

        MimeMessage message = messages.get(0);

        List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(message);

        String plainTextMessage = parsedMessage.get(0);

        Assert.assertEquals("Dear Charles, \n\nWe recently informed you that the interview of Anthony Jones in connection \nwith Application DDNBENSING09-2012-000011 for UCL EngD Biochemical Engineering \nhas taken place. \n\nYou must evaluate the feedback and select the next action. \n\n *  Evaluate Feedback: http://pgadmissions-sit.zuehlke.com/pgadmissions/progress/getPage?applicationId=DDNBENSING09-2012-000011 \n\nWe will continue to send reminders until you respond to this request. \n\nYours sincerely, \nUCL Prism \n\n University College London, Gower Street, London, WC1E 6BT\n Tel: +44 (0) 20 7679 2000\n \u00a9 UCL 1999\u20132012 \n\nIf the links do not work in your email client copy and paste them into your browser.", plainTextMessage);
    }
    
    @Test
    public void stripHtmlWithTwoButtonAndSendMultiPartMimeMessage() throws IOException, Exception {
        EasyMock.expect(configMock.getTemplate(template)).andReturn(new ReviewRequestReminderTemplate());
        EasyMock.replay(freeMarkerConfigMock, configMock);

        final ArrayList<MimeMessage> messages = new ArrayList<MimeMessage>();
        final FakeLoggingMailSender mailSender = new FakeLoggingMailSender() {
            @Override
            protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
                for (MimeMessage msg : mimeMessages) {
                    messages.add(msg);
                }
                super.doSend(mimeMessages, originalMessages);
            }
        };

        mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true);
        MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0], subject, template, model, replyToAddress);
        mailSender.send(msgPreparator);

        Assert.assertEquals(1, messages.size());

        MimeMessage message = messages.get(0);

        List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(message);

        String plainTextMessage = parsedMessage.get(0);

        Assert.assertEquals("Dear review, \n\nWe recently informed you that apply alastair has submitted an Application \nDDNPRFSING01-2012-000005 for postgraduate research study atUniversity College \nLondon (UCL) <http://www.ucl.ac.uk/> in MRes Management Sciences and Innovation\n<http://www.google.com>. \n\nYou have been selected to review their Application. \n\nYou are asked to complete a short questionnaire confirming their suitability \nfor postgraduate research study. If you feel unable to do this, you may also \ndecline.Be aware that declining to provide a review may reduce the applicant's \nchances of securing a study place. \n\n *  Provide Review: http://pgadmissions-sit.zuehlke.com/pgadmissions/decline/review?applicationId=DDNPRFSING01-2012-000005&activationCode=4533d932-14f7-4bba-a90c-ef54d11edad3 \n *  Decline: http://pgadmissions-sit.zuehlke.com/pgadmissions/decline/review?applicationId=DDNPRFSING01-2012-000005&activationCode=4533d932-14f7-4bba-a90c-ef54d11edad3 \n\nWe will continue to send reminders until you respond to this request. \n\nYours sincerely,\nUCL Prism \n\n University College London, Gower Street, London, WC1E 6BT\n Tel: +44 (0) 20 7679 2000\n \u00a9 UCL 1999\u20132012 \n\nIf the links do not work in your email client copy and paste them into your browser.", plainTextMessage);
    }
    
    @Test
    public void stripHtmlAndNoLeadingWhitespacesAndSendMultiPartMimeMessage() throws IOException, Exception {
        EasyMock.expect(configMock.getTemplate(template)).andReturn(new InterviewConfirmationTemplate());
        EasyMock.replay(freeMarkerConfigMock, configMock);

        final ArrayList<MimeMessage> messages = new ArrayList<MimeMessage>();
        final FakeLoggingMailSender mailSender = new FakeLoggingMailSender() {
            @Override
            protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
                for (MimeMessage msg : mimeMessages) {
                    messages.add(msg);
                }
                super.doSend(mimeMessages, originalMessages);
            }
        };

        mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigMock, true);
        MimeMessagePreparator msgPreparator = mimeMessagePreparatorFactory.getMimeMessagePreparator(tos[0], subject, template, model, replyToAddress);
        mailSender.send(msgPreparator);

        Assert.assertEquals(1, messages.size());

        MimeMessage message = messages.get(0);

        List<String> parsedMessage = MultiPartMimeMessageParser.parseMessage(message);

        String plainTextMessage = parsedMessage.get(0);

        Assert.assertEquals("Dear apply, \n\nWe are pleased to confirm that your Application TMRMBISING01-2012-000005 for \nUCLMRes Medical and Biomedical Imaging has been advanced to interview. \n\nThe interview will take place at 07:00 on 27 Sep 2012. \n\nTesting. \n\n *  Get Directions: http://www.google.co.uk/ \n *  View/Update Application: http://pgadmissions-sit.zuehlke.com/pgadmissions/application?view=view&applicationId=TMRMBISING01-2012-000005 \n\nPlease let us know by e-mail <admin@aktest.com> if you are unable to attend. \n\nYours sincerely,\nUCL Prism \n\n University College London, Gower Street, London, WC1E 6BT\n Tel: +44 (0) 20 7679 2000\n \u00a9 UCL 1999\u20132012 \n\nIf the links do not work in your email client copy and paste them into your browser.", plainTextMessage);
    }

    class ApprovalOutcomeHtmlTemplate extends Template {
        public ApprovalOutcomeHtmlTemplate() throws Exception {
            super(null, new StringReader(""), null);
        }

        @Override
        public void process(Object rootMap, Writer out) {
            try {
                out.write(FileUtils.readFileToString(new File("src/test/resources/mail/approval_outcome.html")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class InterviewEvaluationRequestTemplate extends Template {
        public InterviewEvaluationRequestTemplate() throws Exception {
            super(null, new StringReader(""), null);
        }

        @Override
        public void process(Object rootMap, Writer out) {
            try {
                out.write(FileUtils.readFileToString(new File("src/test/resources/mail/interview_evaluation_request.html")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    class ReviewRequestReminderTemplate extends Template {
        public ReviewRequestReminderTemplate() throws Exception {
            super(null, new StringReader(""), null);
        }

        @Override
        public void process(Object rootMap, Writer out) {
            try {
                out.write(FileUtils.readFileToString(new File("src/test/resources/mail/review_request_reminder.html")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    class InterviewConfirmationTemplate extends Template {
        public InterviewConfirmationTemplate() throws Exception {
            super(null, new StringReader(""), null);
        }

        @Override
        public void process(Object rootMap, Writer out) {
            try {
                out.write(FileUtils.readFileToString(new File("src/test/resources/mail/interview_confirmation.html")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
