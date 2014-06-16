package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.FullTextSearchDAO;
import com.zuehlke.pgadmissions.domain.User;

@Service
public class FullTextSearchService {

    private final SessionFactory sessionFactory;
    
    private final FullTextSearchDAO fullTextSearchDAO;
    
    public FullTextSearchService() {
        this(null, null);
    }
    
    @Autowired
    public FullTextSearchService(final SessionFactory sessionFactory, final FullTextSearchDAO fullTextSearchDAO) {
        this.sessionFactory = sessionFactory;
        this.fullTextSearchDAO = fullTextSearchDAO;
    }

    @Transactional
    public void initialiseSearchIndexes() {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        fullTextSession.createIndexer().start();
    }
    
    @Transactional(readOnly = true)
    public List<User> getMatchingUsersWithFirstnameLike(final String searchTerm) {
        return fullTextSearchDAO.getMatchingUsersWithFirstnameLike(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<User> getMatchingUsersWithLastnameLike(final String searchTerm) {
        return fullTextSearchDAO.getMatchingUsersWithLastnameLike(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<User> getMatchingUsersWithEmailLike(final String searchTerm) {
        return fullTextSearchDAO.getMatchingUsersWithEmailLike(searchTerm);
    }
    
    @Transactional(readOnly = true)
    public List<String> getMatchingInstitutions(final String searchTerm, String domicileCode) {
        return fullTextSearchDAO.getMatchingInsitutions(searchTerm, domicileCode);
    }
}
