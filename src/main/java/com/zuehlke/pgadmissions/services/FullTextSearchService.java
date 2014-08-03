package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.FullTextSearchDAO;
import com.zuehlke.pgadmissions.rest.representation.AutosuggestedUserRepresentation;

@Service
public class FullTextSearchService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private FullTextSearchDAO fullTextSearchDAO;

    @Transactional
    public void initialiseSearchIndexes() {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        fullTextSession.createIndexer().start();
    }
    
    @Transactional(readOnly = true)
    public List<AutosuggestedUserRepresentation> getMatchingUsersWithFirstNameLike(final String searchTerm) {
        return fullTextSearchDAO.getMatchingUsersWithFirstnameLike(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<AutosuggestedUserRepresentation> getMatchingUsersWithLastNameLike(final String searchTerm) {
        return fullTextSearchDAO.getMatchingUsersWithLastnameLike(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<AutosuggestedUserRepresentation> getMatchingUsersWithEmailLike(final String searchTerm) {
        return fullTextSearchDAO.getMatchingUsersWithEmailLike(searchTerm);
    }
    
    @Transactional(readOnly = true)
    public List<String> getMatchingInstitutions(final String searchTerm, String domicileCode) {
        return fullTextSearchDAO.getMatchingInsitutions(searchTerm, domicileCode);
    }
}
