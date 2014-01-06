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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Referee;
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
    
    public List<String> getMatchingQualificationsWithTitlesLike(final String searchTerm) {
    	return getMatchingQualificationTitles(searchTerm, "qualificationTitle");
    }
    
    public List<String> getMatchingQualificationsWithSubjectsLike(final String searchTerm) {
    	return getMatchingQualificationSubjects(searchTerm, "qualificationSubject");
    }
    
    public List<String> getMatchingQualificationsWithGradesLike(final String searchTerm) {
    	return getMatchingQualificationGrades(searchTerm, "qualificationGrade");
    }
    
    public List<String> getMatchingEmploymentPositionsWithEmployerNamesLike(final String searchTerm) {
    	return getMatchingEmploymentPositionEmployerNames(searchTerm, "employerName");
    }
    
    public List<String> getMatchingEmploymentPositionsWithPositionsLike(final String searchTerm) {
    	return getMatchingEmploymentPositionPositions(searchTerm, "position");
    }
      
    public List<String> getMatchingRefereesWithJobEmployersLike(final String searchTerm) {
    	return getMatchingRefereeJobEmployers(searchTerm, "employerName");
    }
    
    public List<String> getMatchingRefereesWithJobTitlesLike(final String searchTerm) {
    	return getMatchingRefereeJobTitles(searchTerm, "position");
    }
    
    private List<RegisteredUser> getMatchingUsers(final String searchTerm, final String propertyName, final Comparator<RegisteredUser> comparator) {
        
    	Criterion notAnApplicantCriterion = Restrictions.in("r.id", new Authority[] {
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
                .add(Restrictions.eq("enabled", true))
                .add(Restrictions.isNull("primaryAccount"))
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
                    .add(Restrictions.eq("enabled", true))
                    .add(Restrictions.isNull("primaryAccount"))
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
    
    private List<String> getMatchingQualificationTitles(final String searchTerm, final String propertyName) {
        
        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }
        
        TreeSet<String> uniqueResults = new TreeSet<String>();
        
        Criteria wildcardCriteria = sessionFactory
                .getCurrentSession()
                .createCriteria(Qualification.class)
                .setProjection(Projections.distinct(Projections.property(propertyName)))
                .add(Restrictions.ilike(propertyName, trimmedSearchTerm, MatchMode.START))
                .addOrder(Order.asc(propertyName))
                .setMaxResults(25);

        uniqueResults.addAll(wildcardCriteria.list());
        
        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(Qualification.class)
                    .get();
            
            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(
                    queryBuilder
                    .keyword()
                    .fuzzy()
                    .withThreshold(.7f)
                    .withPrefixLength(0)
                    .onField(propertyName)
                    .matching(trimmedSearchTerm)
                    .createQuery(), Qualification.class);
           
           List<Qualification> fuzzyQueryResults = fuzzyQuery.list();
            
           for (int i = 0; i < fuzzyQueryResults.size(); i++) {
        	   uniqueResults.add(fuzzyQueryResults.get(i).getQualificationTitle());
           }
           
        }
        
        return new ArrayList<String>(uniqueResults);
    }
    
    private List<String> getMatchingQualificationSubjects(final String searchTerm, final String propertyName) {
        
        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }
        
        TreeSet<String> uniqueResults = new TreeSet<String>();
        
        Criteria wildcardCriteria = sessionFactory
                .getCurrentSession()
                .createCriteria(Qualification.class)
                .setProjection(Projections.distinct(Projections.property(propertyName)))
                .add(Restrictions.ilike(propertyName, trimmedSearchTerm, MatchMode.START))
                .addOrder(Order.asc(propertyName))
                .setMaxResults(25);

        uniqueResults.addAll(wildcardCriteria.list());
        
        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(Qualification.class)
                    .get();
            
            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(
                    queryBuilder
                    .keyword()
                    .fuzzy()
                    .withThreshold(.7f)
                    .withPrefixLength(0)
                    .onField(propertyName)
                    .matching(trimmedSearchTerm)
                    .createQuery(), Qualification.class);
           
           List<Qualification> fuzzyQueryResults = fuzzyQuery.list();
            
           for (int i = 0; i < fuzzyQueryResults.size(); i++) {
        	   uniqueResults.add(fuzzyQueryResults.get(i).getQualificationSubject());
           }
           
        }
        
        return new ArrayList<String>(uniqueResults);
    }
    
    private List<String> getMatchingQualificationGrades(final String searchTerm, final String propertyName) {
        
        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }
        
        TreeSet<String> uniqueResults = new TreeSet<String>();
        
        Criteria wildcardCriteria = sessionFactory
                .getCurrentSession()
                .createCriteria(Qualification.class)
                .setProjection(Projections.distinct(Projections.property(propertyName)))
                .add(Restrictions.ilike(propertyName, trimmedSearchTerm, MatchMode.START))
                .addOrder(Order.asc(propertyName))
                .setMaxResults(25);

        uniqueResults.addAll(wildcardCriteria.list());
        
        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(Qualification.class)
                    .get();
            
            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(
                    queryBuilder
                    .keyword()
                    .fuzzy()
                    .withThreshold(.7f)
                    .withPrefixLength(0)
                    .onField(propertyName)
                    .matching(trimmedSearchTerm)
                    .createQuery(), Qualification.class);
           
           List<Qualification> fuzzyQueryResults = fuzzyQuery.list();
            
           for (int i = 0; i < fuzzyQueryResults.size(); i++) {
        	   uniqueResults.add(fuzzyQueryResults.get(i).getQualificationGrade());
           }
           
        }
        
        return new ArrayList<String>(uniqueResults);
    }
    
    private List<String> getMatchingEmploymentPositionEmployerNames(final String searchTerm, final String propertyName) {
        
        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }
        
        TreeSet<String> uniqueResults = new TreeSet<String>();
        
        Criteria wildcardCriteria = sessionFactory
                .getCurrentSession()
                .createCriteria(EmploymentPosition.class)
                .setProjection(Projections.distinct(Projections.property(propertyName)))
                .add(Restrictions.ilike(propertyName, trimmedSearchTerm, MatchMode.START))
                .addOrder(Order.asc(propertyName))
                .setMaxResults(25);

        uniqueResults.addAll(wildcardCriteria.list());
        
        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(EmploymentPosition.class)
                    .get();
            
            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(
                    queryBuilder
                    .keyword()
                    .fuzzy()
                    .withThreshold(.7f)
                    .withPrefixLength(0)
                    .onField(propertyName)
                    .matching(trimmedSearchTerm)
                    .createQuery(), EmploymentPosition.class);
           
           List<EmploymentPosition> fuzzyQueryResults = fuzzyQuery.list();
            
           for (int i = 0; i < fuzzyQueryResults.size(); i++) {
        	   uniqueResults.add(fuzzyQueryResults.get(i).getEmployerName());
           }
           
           uniqueResults.addAll(this.getMatchingRefereesWithJobEmployersLike(trimmedSearchTerm));
        }
        
        return new ArrayList<String>(uniqueResults);
    }
    
    private List<String> getMatchingEmploymentPositionPositions(final String searchTerm, final String propertyName) {
        
        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }
        
        TreeSet<String> uniqueResults = new TreeSet<String>();
        
        Criteria wildcardCriteria = sessionFactory
                .getCurrentSession()
                .createCriteria(EmploymentPosition.class)
                .setProjection(Projections.distinct(Projections.property(propertyName)))
                .add(Restrictions.ilike(propertyName, trimmedSearchTerm, MatchMode.START))
                .addOrder(Order.asc(propertyName))
                .setMaxResults(25);

        uniqueResults.addAll(wildcardCriteria.list());
        
        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(EmploymentPosition.class)
                    .get();
            
            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(
                    queryBuilder
                    .keyword()
                    .fuzzy()
                    .withThreshold(.7f)
                    .withPrefixLength(0)
                    .onField(propertyName)
                    .matching(trimmedSearchTerm)
                    .createQuery(), EmploymentPosition.class);
           
           List<EmploymentPosition> fuzzyQueryResults = fuzzyQuery.list();
            
           for (int i = 0; i < fuzzyQueryResults.size(); i++) {
        	   uniqueResults.add(fuzzyQueryResults.get(i).getPosition());
           }
           
           uniqueResults.addAll(this.getMatchingRefereesWithJobTitlesLike(trimmedSearchTerm));
        }
        
        return new ArrayList<String>(uniqueResults);
    }
    
    private List<String> getMatchingRefereeJobEmployers(final String searchTerm, final String propertyName) {
        
        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }
        
        TreeSet<String> uniqueResults = new TreeSet<String>();
        
        Criteria wildcardCriteria = sessionFactory
                .getCurrentSession()
                .createCriteria(Referee.class)
                .setProjection(Projections.distinct(Projections.property(propertyName)))
                .add(Restrictions.ilike(propertyName, trimmedSearchTerm, MatchMode.START))
                .addOrder(Order.asc(propertyName))
                .setMaxResults(25);

        uniqueResults.addAll(wildcardCriteria.list());
        
        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(Referee.class)
                    .get();
            
            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(
                    queryBuilder
                    .keyword()
                    .fuzzy()
                    .withThreshold(.7f)
                    .withPrefixLength(0)
                    .onField(propertyName)
                    .matching(trimmedSearchTerm)
                    .createQuery(), Referee.class);
           
           List<Referee> fuzzyQueryResults = fuzzyQuery.list();
            
           for (int i = 0; i < fuzzyQueryResults.size(); i++) {
        	   uniqueResults.add(fuzzyQueryResults.get(i).getJobEmployer());
           }
           
           uniqueResults.addAll(this.getMatchingEmploymentPositionsWithEmployerNamesLike(trimmedSearchTerm));
        }
        
        return new ArrayList<String>(uniqueResults);
    }
    
    private List<String> getMatchingRefereeJobTitles(final String searchTerm, final String propertyName) {
        
        String trimmedSearchTerm = StringUtils.trimToEmpty(searchTerm);

        if (StringUtils.isEmpty(trimmedSearchTerm)) {
            return Collections.emptyList();
        }
        
        TreeSet<String> uniqueResults = new TreeSet<String>();
        
        Criteria wildcardCriteria = sessionFactory
                .getCurrentSession()
                .createCriteria(Referee.class)
                .setProjection(Projections.distinct(Projections.property(propertyName)))
                .add(Restrictions.ilike(propertyName, trimmedSearchTerm, MatchMode.START))
                .addOrder(Order.asc(propertyName))
                .setMaxResults(25);

        uniqueResults.addAll(wildcardCriteria.list());
        
        if (StringUtils.length(trimmedSearchTerm) >= 3) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

            QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(Referee.class)
                    .get();
            
            FullTextQuery fuzzyQuery = fullTextSession.createFullTextQuery(
                    queryBuilder
                    .keyword()
                    .fuzzy()
                    .withThreshold(.7f)
                    .withPrefixLength(0)
                    .onField(propertyName)
                    .matching(trimmedSearchTerm)
                    .createQuery(), Referee.class);
           
           List<Referee> fuzzyQueryResults = fuzzyQuery.list();
            
           for (int i = 0; i < fuzzyQueryResults.size(); i++) {
        	   uniqueResults.add(fuzzyQueryResults.get(i).getJobTitle());
           }
           
           uniqueResults.addAll(this.getMatchingEmploymentPositionsWithPositionsLike(trimmedSearchTerm));
        }
        
        return new ArrayList<String>(uniqueResults);
    }

}