package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.rest.representation.AutosuggestedUserRepresentation;

@Repository
@SuppressWarnings("unchecked")
public class FullTextSearchDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private static Comparator<User> LASTNAME_COMPARATOR = new Comparator<User>() {
        @Override
        public int compare(final User o1, final User o2) {
            return o1.getLastName().compareTo(o2.getLastName());
        }
    };

    public List<AutosuggestedUserRepresentation> getMatchingUsersWithFirstnameLike(final String searchTerm) {
        return getMatchingUsers(searchTerm, "firstName", LASTNAME_COMPARATOR);
    }

    public List<AutosuggestedUserRepresentation> getMatchingUsersWithLastnameLike(final String searchTerm) {
        return getMatchingUsers(searchTerm, "lastName", LASTNAME_COMPARATOR);
    }

    public List<AutosuggestedUserRepresentation> getMatchingUsersWithEmailLike(final String searchTerm) {
        return getMatchingUsers(searchTerm, "email", LASTNAME_COMPARATOR);
    }

    private List<AutosuggestedUserRepresentation> getMatchingUsers(final String searchTerm, final String propertyName, final Comparator<User> comparator) {

        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }

        TreeSet<User> uniqueResults = new TreeSet<User>(comparator);

        Criteria wildcardCriteria = sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction()
                        .add(Restrictions.isNull("userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.ilike(propertyName, trimmedSearchTerm, MatchMode.START)) //
                .addOrder(Order.asc("lastName")).setMaxResults(25);

        uniqueResults.addAll(wildcardCriteria.list());

        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(User.class).get();
            Criteria notApplicantCriteria = fullTextSession.createCriteria(User.class);
            // FIXME seems like notApplicantCriteria is completely ignored (have a look at a query)

            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(
                    queryBuilder.keyword().fuzzy().withThreshold(.7f).withPrefixLength(0).onField(propertyName).matching(trimmedSearchTerm).createQuery(),
                    User.class).setCriteriaQuery(notApplicantCriteria);

            uniqueResults.addAll(fuzzyQuery.list());
        }

        ArrayList<User> users = new ArrayList<User>(uniqueResults);
        List<AutosuggestedUserRepresentation> representations = Lists.newArrayListWithCapacity(users.size());
        for (User user : users) {
            representations.add(new AutosuggestedUserRepresentation(user.getFirstName(), user.getLastName(), user.getEmail()));
        }
        return representations;
    }

    public List<String> getMatchingInsitutions(final String searchTerm, final String domicileCode) {

        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }

        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(ImportedInstitution.class).get();

            Criteria filterDomicileCriteria = fullTextSession.createCriteria(ImportedInstitution.class).add(Restrictions.eq("domicileCode", domicileCode));

            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(queryBuilder.phrase().onField("name").sentence(trimmedSearchTerm).createQuery(),
                    ImportedInstitution.class).setCriteriaQuery(filterDomicileCriteria);

            List<ImportedInstitution> fuzzyQueryResults = fuzzyQuery.list();

            List<String> results = Lists.newArrayListWithCapacity(fuzzyQueryResults.size());
            for (int i = 0; i < fuzzyQueryResults.size(); i++) {
                results.add(fuzzyQueryResults.get(i).getName());
            }
            return results;
        }

        return Collections.emptyList();
    }

}