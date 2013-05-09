package com.zuehlke.pgadmissions.mail;

import static junit.framework.Assert.assertTrue;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/testMailSendingContext.xml")
public class ScheduledMailSendingServiceIT {

    @Autowired
    private ScheduledMailSendingService service;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Referee referee1;
    private Referee referee2;
    private Referee referee3;
    private Integer referee2Id;
    private Integer referee1Id;
    private Integer referee3Id;
    private DateTime lastNotified;
    private ApplicationForm application2;

    @Before
    public void setup() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                ReminderInterval interval = new ReminderInterval();
                interval.setDuration(1);
                interval.setUnit(DurationUnitEnum.MINUTES);
                sessionFactory.getCurrentSession().save(interval);

                EmailTemplate mailTemplate = new EmailTemplateBuilder().subject("whatever").name(EmailTemplateName.REFEREE_REMINDER)
                        .content("whatever content").active(true).build();

                RegisteredUser refereeUser1 = new RegisteredUserBuilder().email("amanda@mail.com").firstName("Amanda").lastName("Frijelloce").build();
                RegisteredUser refereeUser2 = new RegisteredUserBuilder().email("nina@mail.com").firstName("Nina").lastName("Sinalefe").build();
                RegisteredUser refereeUser3 = new RegisteredUserBuilder().email("gina@mail.com").firstName("Gina").lastName("Round").build();
                RegisteredUser programAdmin = new RegisteredUserBuilder().email("jeppetto@mail.com").build();
                RegisteredUser applicant = new RegisteredUserBuilder().firstName("Luigi").lastName("Babbu").build();

                lastNotified = new DateTime(2013, 5, 7, 0, 0);

                Program program = new ProgramBuilder().administrators(programAdmin).title("ProgramTitle").build();

                ApplicationForm application1 = new ApplicationFormBuilder().applicant(applicant).program(program).applicationNumber("sampelNumber")
                        .status(ApplicationFormStatus.INTERVIEW).build();

                application2 = new ApplicationFormBuilder().applicant(applicant).program(program).applicationNumber("sampelNumber")
                        .status(ApplicationFormStatus.INTERVIEW).build();

                referee1 = new RefereeBuilder().declined(false).application(application1).user(refereeUser1).reference(new ReferenceCommentBuilder().build())
                        .lastNotified(lastNotified.toDate()).build();
                referee2 = new RefereeBuilder().declined(false).application(application2).reference(new ReferenceCommentBuilder().build())
                        .lastNotified(lastNotified.toDate()).user(refereeUser2).build();
                referee3 = new RefereeBuilder().declined(false).application(application1).reference(new ReferenceCommentBuilder().build())
                        .lastNotified(lastNotified.toDate()).user(refereeUser3).build();

                saveObjects(interval, mailTemplate, refereeUser1, refereeUser2, refereeUser3, programAdmin, applicant, program, application1, application2,
                        referee1, referee2, referee3);
                referee1Id = referee1.getId();
                referee2Id = referee2.getId();
                referee3Id = referee3.getId();
            }
        });
    }

    private void saveObjects(Object... objs) {
        Session session = sessionFactory.getCurrentSession();
        for (Object o : objs) {
            session.saveOrUpdate(o);
        }
        session.flush();
    }

    private void deleteObjects(Object... objs) {
        Session session = sessionFactory.getCurrentSession();
        for (Object o : objs) {
            session.delete(o);
        }
        session.flush();
    }

    @After
    public void cleanup() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                deleteObjects(referee1, referee2, referee3);
            }
        });
    }

    @Test
    public void shouldSendEmailToOneRefereesAndUpdateHisStatus() {

        service.setMailSender(new CustomMailSender());

        service.sendReferenceReminder();

        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                referee1 = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee1Id);
                referee2 = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee2Id);
                referee3 = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee3Id);
            }
        });

        assertTrue(lastNotified.toDate().before(referee1.getLastNotified()));
        assertTrue(lastNotified.toDate().equals(referee2.getLastNotified()));
        assertTrue(lastNotified.toDate().before(referee3.getLastNotified()));
    }

    class CustomMailSender extends MailSender {

        CustomMailSender() {
            this(service.getMailSender().javaMailSender, "false", "pgadmissions.jenkins@zuhlke.com", " uclprism@gmail.com", service.getMailSender()
                    .getEmailTemplateService(), service.getMailSender().getFreemarkerConfig());
        }

        public CustomMailSender(JavaMailSender javaMailSender, String production, String emailAddressFrom, String emailAddressTo,
                EmailTemplateService emailTemplateService, FreeMarkerConfig freemarkerConfig) {
            super(javaMailSender, production, emailAddressFrom, emailAddressTo, emailTemplateService, freemarkerConfig);
        }

        @Override
        protected void sendEmailAsDevelopmentMessage(PrismEmailMessage message) {
            if (message.getTo().get(0).getEmail().equals(referee2.getUser().getEmail())) {
                throw new PrismMailMessageException(message);
            } else {
                super.sendEmailAsDevelopmentMessage(message);
            }
        }

    }

}
