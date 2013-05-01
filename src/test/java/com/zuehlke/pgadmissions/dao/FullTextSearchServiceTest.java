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
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.FullTextSearchService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testFullTextSearchContext.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class FullTextSearchServiceTest {

    @Autowired
    private FullTextSearchService fullTextService;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserDAO registeredUserDAO;

    private RegisteredUser user1;

    private RegisteredUser similiarToUser1;
    
    public static boolean dbInit = false;

    @Before
    public void prepare() {
        user1 = new RegisteredUserBuilder().firstName("Tyler").lastName("Durden").email("tyler@durden.com")
                .username("tyler@durden.com").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        similiarToUser1 = new RegisteredUserBuilder().firstName("Taylor").lastName("Dordeen")
                .email("taylor@dordeen.com").username("taylor@durden.com").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        registeredUserDAO.save(user1);
        registeredUserDAO.save(similiarToUser1);

        flushIndexes();
    }

    @After
    public void clean() {
        registeredUserDAO.delete(user1);
        registeredUserDAO.delete(similiarToUser1);
    }

    @Test
    public void shouldReturnAFuzzyMatchBasedOnAMisspelledFirstname() {
        List<RegisteredUser> matchingUsersWithFirstnameLike = fullTextService.getMatchingUsersWithFirstnameLike("taylar");
        assertEquals(1, matchingUsersWithFirstnameLike.size());
        assertTrue(listContainsId(similiarToUser1, matchingUsersWithFirstnameLike));
    }
    
    @Test
    public void shouldReturnAFuzzyMatchBasedOnAMisspelledLastname() {
        List<RegisteredUser> matchingUsersWithLastnameLike = fullTextService.getMatchingUsersWithLastnameLike("durdeen");
        assertEquals(2, matchingUsersWithLastnameLike.size());
        assertTrue(listContainsId(user1, matchingUsersWithLastnameLike));
        assertTrue(listContainsId(similiarToUser1, matchingUsersWithLastnameLike));
    }

    @Test
    public void shouldReturnAFuzzyMatchBasedOnAMisspelledEmail() {
        List<RegisteredUser> matchingUsersWithEmailLike = fullTextService.getMatchingUsersWithEmailLike("tulor@durden.com");
        assertEquals(2, matchingUsersWithEmailLike.size());
        assertTrue(listContainsId(user1, matchingUsersWithEmailLike));
        assertTrue(listContainsId(similiarToUser1, matchingUsersWithEmailLike));
    }
    
    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
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
