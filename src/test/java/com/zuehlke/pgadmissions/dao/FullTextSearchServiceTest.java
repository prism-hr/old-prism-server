package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
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

    private RegisteredUser user1;

    private RegisteredUser similiarToUser1;

    @Before
    public void prepare() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                sessionFactory.getCurrentSession()
                                .createSQLQuery("" 
                                                + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (1,'ADMINISTRATOR');"
                                                + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (2,'APPLICANT');"
                                                + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (4,'APPROVER');"
                                                + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (7,'INTERVIEWER');"
                                                + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (6,'REFEREE');"
                                                + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (3,'REVIEWER');"
                                                + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (5,'SUPERADMINISTRATOR');"
                                                + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (8,'SUPERVISOR');"
                                                + "INSERT INTO APPLICATION_ROLE (id,authority) VALUES (9,'VIEWER');").executeUpdate();

                user1 = new RegisteredUserBuilder().firstName("Tyler").lastName("Durden").email("tyler@durden.com").username("tyler@durden.com")
                                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

                similiarToUser1 = new RegisteredUserBuilder().firstName("Taylor").lastName("Dordeen").email("taylor@dordeen.com").username("taylor@durden.com")
                                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

                registeredUserDAO.save(user1);
                registeredUserDAO.save(similiarToUser1);

                user1.getRoles().add(roleDAO.getRoleByAuthority(Authority.REFEREE));
                similiarToUser1.getRoles().add(roleDAO.getRoleByAuthority(Authority.INTERVIEWER));

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
        List<RegisteredUser> matchingUsersWithFirstnameLike = fullTextService.getMatchingUsersWithFirstnameLike("taylar");
        assertEquals(1, matchingUsersWithFirstnameLike.size());
        assertTrue(contains(similiarToUser1, matchingUsersWithFirstnameLike));
    }

    @Test
    public void shouldReturnAFuzzyMatchBasedOnAMisspelledLastname() {
        List<RegisteredUser> matchingUsersWithLastnameLike = fullTextService.getMatchingUsersWithLastnameLike("durdeen");
        assertEquals(2, matchingUsersWithLastnameLike.size());
        assertTrue(contains(user1, matchingUsersWithLastnameLike));
        assertTrue(contains(similiarToUser1, matchingUsersWithLastnameLike));
    }

    @Test
    public void shouldReturnAFuzzyMatchBasedOnAMisspelledEmail() {
        List<RegisteredUser> matchingUsersWithEmailLike = fullTextService.getMatchingUsersWithEmailLike("tulor@durden.com");
        assertEquals(2, matchingUsersWithEmailLike.size());
        assertTrue(contains(user1, matchingUsersWithEmailLike));
        assertTrue(contains(similiarToUser1, matchingUsersWithEmailLike));
    }

    @Test
    public void shouldNotReturnAMatchForApplicants() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                user1.getRoles().clear();
                similiarToUser1.getRoles().clear();

                user1.getRoles().add(roleDAO.getRoleByAuthority(Authority.APPLICANT));
                similiarToUser1.getRoles().add(roleDAO.getRoleByAuthority(Authority.APPLICANT));

                sessionFactory.getCurrentSession().saveOrUpdate(user1);
                sessionFactory.getCurrentSession().saveOrUpdate(similiarToUser1);
            }
        });

        List<RegisteredUser> matchingUsersWithLastnameLike = fullTextService.getMatchingUsersWithLastnameLike("Du");
        assertEquals(0, matchingUsersWithLastnameLike.size());
    }

    @Test
    public void shouldNotReturnAFuzzyMatchBasedOnAMisspelledLastnameForApplicants() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                user1.getRoles().clear();
                similiarToUser1.getRoles().clear();

                user1.getRoles().add(roleDAO.getRoleByAuthority(Authority.APPLICANT));
                similiarToUser1.getRoles().add(roleDAO.getRoleByAuthority(Authority.APPLICANT));

                sessionFactory.getCurrentSession().saveOrUpdate(user1);
                sessionFactory.getCurrentSession().saveOrUpdate(similiarToUser1);
            }
        });

        List<RegisteredUser> matchingUsersWithLastnameLike = fullTextService.getMatchingUsersWithLastnameLike("durden");
        assertEquals(0, matchingUsersWithLastnameLike.size());
    }

    private boolean contains(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
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
