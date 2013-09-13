package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Repository
@SuppressWarnings("unchecked")
public class FullTextSearchDAO {

    private SessionFactory sessionFactory;
    
    private static Comparator<RegisteredUser> LASTNAME_COMPARATOR = new Comparator<RegisteredUser>() {
        @Override
        public int compare(final RegisteredUser o1, final RegisteredUser o2) {
            return o1.getLastName().compareTo(o2.getLastName());
        }
    };
    
    public FullTextSearchDAO() {
    }
    
    @Autowired
    public FullTextSearchDAO(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public List<RegisteredUser> getMatchingUsersWithFirstnameLike(final String searchTerm) {
        return getMatchingUsers(searchTerm, "firstName", LASTNAME_COMPARATOR);
    }
    
    public List<RegisteredUser> getMatchingUsersWithLastnameLike(final String searchTerm) {
        return getMatchingUsers(searchTerm, "lastName", LASTNAME_COMPARATOR);
    }
    
    public List<RegisteredUser> getMatchingUsersWithEmailLike(final String searchTerm) {
        return getMatchingUsers(searchTerm, "email", LASTNAME_COMPARATOR);
    }
    
    private List<RegisteredUser> getMatchingUsers(final String searchTerm, final String propertyName, final Comparator<RegisteredUser> comparator) {
        Criterion notAnApplicantCriterion = Restrictions.in("r.authorityEnum", new Authority[] {
                Authority.ADMINISTRATOR, Authority.APPROVER, Authority.INTERVIEWER,
                Authority.REFEREE, Authority.REVIEWER, Authority.SUPERADMINISTRATOR, Authority.SUPERVISOR });
        
        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }
        
        TreeSet<RegisteredUser> uniqueResults = new TreeSet<RegisteredUser>(comparator);
        
        Criteria wildcardCriteria = sessionFactory
                .getCurrentSession()
                .createCriteria(RegisteredUser.class)
                .add(Restrictions.ilike(propertyName, trimmedSearchTerm, MatchMode.START))
                .createAlias("roles", "r")
                .add(notAnApplicantCriterion)
                .addOrder(Order.asc("lastName"))
                .setMaxResults(25);

        uniqueResults.addAll(wildcardCriteria.list());
        
        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(RegisteredUser.class)
                    .get();

            Criteria notApplicantCriteria = fullTextSession
                    .createCriteria(RegisteredUser.class)
                    .createAlias("roles", "r")
                    .add(notAnApplicantCriterion);
            
            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(
                    queryBuilder
                    .keyword()
                    .fuzzy()
                    .withThreshold(.7f)
                    .withPrefixLength(0)
                    .onField(propertyName)
                    .matching(trimmedSearchTerm)
                    .createQuery(), RegisteredUser.class)
                    .setCriteriaQuery(notApplicantCriteria);

            uniqueResults.addAll(fuzzyQuery.list());
        }
        
        return new ArrayList<RegisteredUser>(uniqueResults);
    }
    
}
