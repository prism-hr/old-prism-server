package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.FullTextSearchDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service
public class FullTextSearchService {

    private final SessionFactory sessionFactory;
    
    private final FullTextSearchDAO fullTextSearchDAO;
    
    public FullTextSearchService() {
        this(null, null);
    }
    
    @Autowired
    public FullTextSearchService(final SessionFactory sessionFactory, final FullTextSearchDAO userIndexDAO) {
        this.sessionFactory = sessionFactory;
        this.fullTextSearchDAO = userIndexDAO;
    }

    @Transactional
    public void initialiseSearchIndexes() {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        fullTextSession.createIndexer().start();
    }
    
    @Transactional(readOnly = true)
    public List<RegisteredUser> getMatchingUsersWithFirstnameLike(final String searchTerm) {
        return fullTextSearchDAO.getMatchingUsersWithFirstnameLike(searchTerm);
    }

    public List<RegisteredUser> getMatchingUsersWithLastnameLike(String string) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<RegisteredUser> getMatchingUsersWithEmailLike(String string) {
        // TODO Auto-generated method stub
        return null;
    }
}
