package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.junit.After;
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

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.FullTextSearchService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testFullTextSearchContext.xml")
public class FullTextSearchServiceTest extends AutomaticRollbackTestCase {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private FullTextSearchService fullTextService;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserDAO registeredUserDAO;

    @Autowired
    private RoleDAO roleDAO;

    private User user1;

    private User similiarToUser1;

    @Before
    public void prepare() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                sessionFactory
                        .getCurrentSession()
                        .createSQLQuery(
                                "" + "INSERT INTO APPLICATION_ROLE (id) VALUES ('ADMINISTRATOR');" + "INSERT INTO APPLICATION_ROLE (id) VALUES ('APPLICANT');"
                                        + "INSERT INTO APPLICATION_ROLE (id) VALUES ('APPROVER');"
                                        + "INSERT INTO APPLICATION_ROLE (id) VALUES ('INTERVIEWER');" + "INSERT INTO APPLICATION_ROLE (id) VALUES ('REFEREE');"
                                        + "INSERT INTO APPLICATION_ROLE (id) VALUES ('REVIEWER');"
                                        + "INSERT INTO APPLICATION_ROLE (id) VALUES ('SUPERADMINISTRATOR');"
                                        + "INSERT INTO APPLICATION_ROLE (id) VALUES ('SUPERVISOR');" + "INSERT INTO APPLICATION_ROLE (id) VALUES ('VIEWER');")
                        .executeUpdate();

                user1 = new UserBuilder().firstName("Tyler").lastName("Durden").email("tyler@durden.com")
                        .userAccount(new UserAccount().withPassword("password").withEnabled(true)).build();

                similiarToUser1 = new UserBuilder().firstName("Taylor").lastName("Dordeen").email("taylor@dordeen.com")
                        .userAccount(new UserAccount().withPassword("password").withEnabled(true)).build();

                registeredUserDAO.save(user1);
                registeredUserDAO.save(similiarToUser1);

//                user1.getRoles().add(roleDAO.getById(Authority.REFEREE));
//                similiarToUser1.getRoles().add(roleDAO.getById(Authority.INTERVIEWER));

                flushIndexes();
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
                                "DELETE FROM APPLICATION_FORM;DELETE FROM PROGRAM_APPROVER_LINK;DELETE FROM PROGRAM_ADMINISTRATOR_LINK;DELETE FROM USER_ROLE_LINK;DELETE FROM APPLICATION_ROLE;DELETE FROM REGISTERED_USER")
                        .executeUpdate();
            }
        });
    }

    @Test
    public void shouldReturnAFuzzyMatchBasedOnAMisspelledFirstname() {
        List<User> matchingUsersWithFirstnameLike = fullTextService.getMatchingUsersWithFirstnameLike("taylar");
        assertEquals(1, matchingUsersWithFirstnameLike.size());
        assertTrue(contains(similiarToUser1, matchingUsersWithFirstnameLike));
    }

    @Test
    public void shouldReturnAFuzzyMatchBasedOnAMisspelledLastname() {
        List<User> matchingUsersWithLastnameLike = fullTextService.getMatchingUsersWithLastnameLike("durdeen");
        assertEquals(2, matchingUsersWithLastnameLike.size());
        assertTrue(contains(user1, matchingUsersWithLastnameLike));
        assertTrue(contains(similiarToUser1, matchingUsersWithLastnameLike));
    }

    @Test
    public void shouldReturnAFuzzyMatchBasedOnAMisspelledEmail() {
        List<User> matchingUsersWithEmailLike = fullTextService.getMatchingUsersWithEmailLike("tulor@durden.com");
        assertEquals(2, matchingUsersWithEmailLike.size());
        assertTrue(contains(user1, matchingUsersWithEmailLike));
        assertTrue(contains(similiarToUser1, matchingUsersWithEmailLike));
    }

    @Test
    public void shouldNotReturnAMatchForApplicants() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
//        template.execute(new TransactionCallbackWithoutResult() {
//            @Override
//            protected void doInTransactionWithoutResult(final TransactionStatus status) {
//                user1.getRoles().clear();
//                similiarToUser1.getRoles().clear();
//
//                user1.getRoles().add(roleDAO.getById(Authority.APPLICANT));
//                similiarToUser1.getRoles().add(roleDAO.getById(Authority.APPLICANT));
//
//                sessionFactory.getCurrentSession().saveOrUpdate(user1);
//                sessionFactory.getCurrentSession().saveOrUpdate(similiarToUser1);
//            }
//        });

        List<User> matchingUsersWithLastnameLike = fullTextService.getMatchingUsersWithLastnameLike("Du");
        assertEquals(0, matchingUsersWithLastnameLike.size());
    }

    @Test
    public void shouldNotReturnAFuzzyMatchBasedOnAMisspelledLastnameForApplicants() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
//        template.execute(new TransactionCallbackWithoutResult() {
//            @Override
//            protected void doInTransactionWithoutResult(final TransactionStatus status) {
//                user1.getRoles().clear();
//                similiarToUser1.getRoles().clear();
//
//                user1.getRoles().add(roleDAO.getById(Authority.APPLICANT));
//                similiarToUser1.getRoles().add(roleDAO.getById(Authority.APPLICANT));
//
//                sessionFactory.getCurrentSession().saveOrUpdate(user1);
//                sessionFactory.getCurrentSession().saveOrUpdate(similiarToUser1);
//            }
//        });

        List<User> matchingUsersWithLastnameLike = fullTextService.getMatchingUsersWithLastnameLike("durden");
        assertEquals(0, matchingUsersWithLastnameLike.size());
    }

    @Test
    public void shouldReturnSimilarMatchForInstitutionName() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        final Institution institution = QualificationInstitutionBuilder.aQualificationInstitution().build();

        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(final TransactionStatus status) {
                sessionFactory.getCurrentSession().save(institution);
            }
        });

        List<String> result = fullTextService.getMatchingInstitutions("akademia  gorniczo hutnicza# ", "PL");
        assertThat(result, is(not(empty())));
        assertThat(result, hasItem(institution.getName()));
    }

    private boolean contains(User user, List<User> users) {
        for (User entry : users) {
            if (user.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }

    private void flushIndexes() {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        fullTextSession.flushToIndexes();
    }
}
