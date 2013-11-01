package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.SessionFactory;
import org.junit.After;
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
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
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
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testApplicationsServiceContext.xml")
public class ApplicationsServiceGetApplicationsWorthConsideringForAttentionFlagTest extends AutomaticRollbackTestCase {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private ApplicationsService applicationsService;

    private RegisteredUser user;

    private RegisteredUser superUser;

    @Autowired
    private ApplicationFormListDAO applicationFormListDAO;

    @Autowired
    private ApplicationFormDAO applicationFormDAO;
    
    private StateTransitionViewResolver stateTransitionViewResolverMock;

    private Program program;

    
    private RoleDAO roleDAO;

    @Before
    public void prepare() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                sessionFactory.getCurrentSession()
                                .createSQLQuery("" 
                                                + "INSERT INTO APPLICATION_ROLE (id) VALUES ('ADMINISTRATOR');"
                                                + "INSERT INTO APPLICATION_ROLE (id) VALUES ('APPLICANT');"
                                                + "INSERT INTO APPLICATION_ROLE (id) VALUES ('APPROVER');"
                                                + "INSERT INTO APPLICATION_ROLE (id) VALUES ('INTERVIEWER');"
                                                + "INSERT INTO APPLICATION_ROLE (id) VALUES ('REFEREE');"
                                                + "INSERT INTO APPLICATION_ROLE (id) VALUES ('REVIEWER');"
                                                + "INSERT INTO APPLICATION_ROLE (id) VALUES ('SUPERADMINISTRATOR');"
                                                + "INSERT INTO APPLICATION_ROLE (id) VALUES ('SUPERVISOR');"
                                                + "INSERT INTO APPLICATION_ROLE (id) VALUES ('VIEWER');").executeUpdate();

                applicationsService = new ApplicationsService(applicationFormDAO, applicationFormListDAO, null, null, null);
                stateTransitionViewResolverMock = EasyMock.createMock(StateTransitionViewResolver.class);
                roleDAO = new RoleDAO(sessionFactory);
                user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).accountNonExpired(false).accountNonLocked(false)
                                .credentialsNonExpired(false).enabled(true).build();
                superUser = new RegisteredUserBuilder().firstName("John").lastName("Doe").email("email@test.com").username("superUserUsername")
                                .password("password").role(roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).accountNonExpired(false)
                                .accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
                program = new ProgramBuilder().code("doesntexist").administrators(superUser).title("another title").build();
                superUser.getProgramsOfWhichAdministrator().add(program);
                superUser.getProgramsOfWhichApprover().add(program);
                sessionFactory.getCurrentSession().save(user);
                sessionFactory.getCurrentSession().save(superUser);
                sessionFactory.getCurrentSession().save(program);
                
                EasyMock.replay(stateTransitionViewResolverMock);
            }
        });
    }

    @After
    public void clean() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                sessionFactory
                        .getCurrentSession()
                        .createSQLQuery(
                                "DELETE FROM SUPERVISOR;DELETE FROM APPLICATION_FORM;DELETE FROM PROGRAM_APPROVER_LINK;DELETE FROM PROGRAM_ADMINISTRATOR_LINK;DELETE FROM USER_ROLE_LINK;DELETE FROM APPLICATION_ROLE;DELETE FROM REGISTERED_USER")
                                .executeUpdate();
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
                    ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(new SupervisorBuilder().user(superUser).isPrimary(true).build()).build();
                    ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).applicationNumber("ABC")
                                    .program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).latestApprovalRound(approvalRound)
                                    .applicant(user).build();

                    ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program)
                                    .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).build();

                    ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).applicationNumber("ABCD")
                                    .program(program).latestApprovalRound(approvalRound)
                                    .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).build();

                    ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program)
                                    .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).build();

                    sessionFactory.getCurrentSession().save(approvalRound);
                    sessionFactory.getCurrentSession().save(applicationFormOne);
                    sessionFactory.getCurrentSession().save(applicationFormTwo);
                    sessionFactory.getCurrentSession().save(applicationFormThree);
                    sessionFactory.getCurrentSession().save(applicationFormFour);

                    List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(superUser,
                                    newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1));

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
        return new ApplicationsFilteringBuilder().sortCategory(sortCategory).order(sortOrder).blockCount(blockCount).filters(filters)
                        .preFilter(ApplicationsPreFilter.URGENT).build();
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
