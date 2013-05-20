package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testFullTextSearchContext.xml")
public class ApplicationsServiceGetApplicationsWorthConsideringForAttentionFlagTest {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    private RegisteredUser user;

    private RegisteredUser superUser;

    private ApplicationFormListDAO applicationFormListDAO;

    private ApplicationFormDAO applicationFormDAO;

    private Program program;

    private ApplicationsService applicationsService;

    private RoleDAO roleDAO;

    @Before
    public void prepare() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                sessionFactory
                .getCurrentSession()
                .createSQLQuery(""
                        + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (1,'ADMINISTRATOR');"
                        + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (2,'APPLICANT');"
                        + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (4,'APPROVER');"
                        + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (7,'INTERVIEWER');"
                        + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (6,'REFEREE');"
                        + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (3,'REVIEWER');"
                        + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (5,'SUPERADMINISTRATOR');"
                        + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (8,'SUPERVISOR');"
                        + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (9,'VIEWER');")
                 .executeUpdate();
                
                applicationFormListDAO = new ApplicationFormListDAO(sessionFactory);
                applicationFormDAO = new ApplicationFormDAO(sessionFactory);
                applicationsService = new ApplicationsService(applicationFormDAO, applicationFormListDAO, null);
                roleDAO = new RoleDAO(sessionFactory);
                user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                        .username("username").password("password").role(roleDAO.getRoleByAuthority(Authority.APPLICANT))
                        .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
                superUser = new RegisteredUserBuilder().firstName("John").lastName("Doe").email("email@test.com")
                        .username("superUserUsername").password("password")
                        .role(roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).accountNonExpired(false)
                        .accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
                program = new ProgramBuilder().code("doesntexist").administrators(superUser).title("another title").build();
                superUser.getProgramsOfWhichAdministrator().add(program);
                superUser.getProgramsOfWhichApprover().add(program);
                sessionFactory.getCurrentSession().save(user);
                sessionFactory.getCurrentSession().save(superUser);
                sessionFactory.getCurrentSession().save(program);
            }
        });
    }

    @Test
    public void shouldReturnApplicationsAUserIsInterestedIn() throws ParseException {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                try {
                    ApprovalRound approvalRound = new ApprovalRoundBuilder().build();
                    ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL)
                            .applicationNumber("ABC").program(program)
                            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
                            .latestApprovalRound(approvalRound).pendingApprovalRestart(true).applicant(user)
                            .build();
            
                    ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology")
                            .program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user)
                            .build();
            
                    ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL)
                            .applicationNumber("ABCD").program(program).latestApprovalRound(approvalRound)
                            .pendingApprovalRestart(true).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
                            .applicant(user).build();
            
                    ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1")
                            .program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user)
                            .build();
            
                    sessionFactory.getCurrentSession().save(approvalRound);
                    sessionFactory.getCurrentSession().save(applicationFormOne);
                    sessionFactory.getCurrentSession().save(applicationFormTwo);
                    sessionFactory.getCurrentSession().save(applicationFormThree);
                    sessionFactory.getCurrentSession().save(applicationFormFour);
                    
                    List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(
                            superUser, newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1));
            
                    assertEquals(2, applications.size());
                    assertTrue(listContainsId(applicationFormOne, applications));
                    assertTrue(listContainsId(applicationFormThree, applications));
                } catch (ParseException e) {
                    Assert.fail(e.getMessage());
                }
            }
        });
    }
    
    private ApplicationsFiltering newFiltering(SortCategory sortCategory, SortOrder sortOrder, int blockCount, ApplicationsFilter... filters) {
        return new ApplicationsFilteringBuilder().sortCategory(sortCategory).order(sortOrder).blockCount(blockCount)
                .filters(filters).preFilter(ApplicationsPreFilter.URGENT).build();
    }
    
    private boolean listContainsId(ApplicationForm form, List<ApplicationForm> aplicationForms) {
        for (ApplicationForm entry : aplicationForms) {
            if (form.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }
}
