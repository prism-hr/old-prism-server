package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.UserUnusedEmail;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.mail.MailDescriptor;
import com.zuehlke.pgadmissions.rest.representation.UserAutoSuggestRepresentation;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Repository
@SuppressWarnings("unchecked")
public class UserDAO {
    
    @Value("${db.schema}")
    private String schema;
    
    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private SessionFactory sessionFactory;

    public User getUserByActivationCode(String activationCode) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .add(Restrictions.eq("activationCode", activationCode)) //
                .uniqueResult();
    }

    public Long getNumberOfActiveApplicationsForApplicant(User user) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.rowCount()) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.not(Restrictions.eq("state.stateGroup", PrismStateGroup.APPLICATION_APPROVED))) //
                .add(Restrictions.not(Restrictions.eq("state.stateGroup", PrismStateGroup.APPLICATION_REJECTED))) //
                .add(Restrictions.not(Restrictions.eq("state,stateGroup", PrismStateGroup.APPLICATION_WITHDRAWN))) //
                .uniqueResult();
    }

    public List<User> getUsersForResourceAndRole(Resource resource, PrismRole authority) {
        return sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("application", resource.getApplication())) //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram())) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("system", resource.getSystem()))) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .list();
    }

    public String getUserInstitutionId(User user, Institution institution, PrismUserIdentity identityType) {
        return (String) sessionFactory.getCurrentSession().createCriteria(UserInstitutionIdentity.class) //
                .setProjection(Projections.property("identifier")) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eqOrIsNull("identityType", identityType)) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<User> getSuggestedSupervisors(Application application) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("user.parentUser", "parentUser", JoinType.INNER_JOIN) //
                .createAlias("parentUser.userAccount", "parentUserAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("role.id", PrismRole.APPLICATION_SUGGESTED_SUPERVISOR)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .add(Restrictions.eq("parentUserAccount.enabled", true)) //
                .addOrder(Order.asc("user.firstName")) //
                .addOrder(Order.asc("user.lastName")) //
                .list();
    }

    public List<User> getRecruitersAssignedToApplication(Application application, List<User> usersToExclude) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user.parentUser")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("user.parentUser", "parentUser", JoinType.INNER_JOIN) //
                .createAlias("parentUser.userAccount", "parentUserAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("program", application.getProgram())) //
                        .add(Restrictions.eq("project", application.getProject())) //
                        .add(Restrictions.eq("application", application))) //
                .add(Restrictions.in("role.id", Arrays.asList(PrismRole.PROGRAM_APPROVER, PrismRole.PROGRAM_VIEWER, //
                        PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROJECT_SECONDARY_SUPERVISOR, //
                        PrismRole.APPLICATION_ADMINISTRATOR, PrismRole.APPLICATION_REVIEWER, //
                        PrismRole.APPLICATION_INTERVIEWER, PrismRole.APPLICATION_PRIMARY_SUPERVISOR, //
                        PrismRole.APPLICATION_SECONDARY_SUPERVISOR, PrismRole.APPLICATION_VIEWER_RECRUITER))) //
                .add(Restrictions.eq("userAccount.enabled", true))
                .add(Restrictions.eq("parentUserAccount.enabled", true)); //
        
        for (User excludedUser : usersToExclude) {
            criteria.add(Restrictions.ne("user", excludedUser));
        }
        
        return criteria.addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .list();
    }
    
    public List<User> getRecruitersAssignedToProgramApplications(Program program, List<User> usersToExclude) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.groupProperty("user.parentUser")) //
                .createAlias("applications", "application", JoinType.INNER_JOIN) //
                .createAlias("application.userRoles", "userRole") //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("user.parentUser", "parentUser", JoinType.INNER_JOIN) //
                .createAlias("parentUser.userAccount", "parentUserAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", program.getId())) //
                .add(Restrictions.in("userRole.role.id", Arrays.asList(PrismRole.APPLICATION_ADMINISTRATOR, //
                        PrismRole.APPLICATION_REVIEWER, PrismRole.APPLICATION_INTERVIEWER, //
                        PrismRole.APPLICATION_PRIMARY_SUPERVISOR, PrismRole.APPLICATION_SECONDARY_SUPERVISOR, //
                        PrismRole.APPLICATION_VIEWER_RECRUITER))) //
                .add(Restrictions.eq("userAccount.enabled", true))
                .add(Restrictions.eq("parentUserAccount.enabled", true)); //
        
        for (User excludedUser : usersToExclude) {
            criteria.add(Restrictions.ne("userRole.user", excludedUser));
        }
        
        return criteria.addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .list();
    }
    
    public List<User> getRecruitersAssignedToProgramProjects(Program program, List<User> usersToExclude) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.groupProperty("user.parentUser")) //
                .createAlias("projects", "project", JoinType.INNER_JOIN) //
                .createAlias("project.userRoles", "userRole") //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("user.parentUser", "parentUser", JoinType.INNER_JOIN) //
                .createAlias("parentUser.userAccount", "parentUserAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", program.getId())) //
                .add(Restrictions.in("userRole.role.id", Arrays.asList(PrismRole.PROJECT_PRIMARY_SUPERVISOR, //
                        PrismRole.PROJECT_SECONDARY_SUPERVISOR))) //
                .add(Restrictions.eq("userAccount.enabled", true))
                .add(Restrictions.eq("parentUserAccount.enabled", true)); //
        
        for (User excludedUser : usersToExclude) {
            criteria.add(Restrictions.ne("userRole.user", excludedUser));
        }
        
        return criteria.addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .list();
    }
    
    public User getAuthenticatedUser(String email, String password) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("email", email)) //
                .add(Restrictions.eq("userAccount.password", encryptionUtils.getMD5Hash(password))) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }
    
    public User getUserByUnusedEmailEmail(String email) {
        return (User) sessionFactory.getCurrentSession().createCriteria(UserUnusedEmail.class) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("email", email)) //
                .uniqueResult();
    }

    public void refreshParentUser(User user) {
        sessionFactory.getCurrentSession().createQuery( //
                "update User " //
                        + "set parentUser = :user " //
                        + "where parentUser = :parentUser") //
                .setParameter("user", user)
                .setParameter("parentUser", user.getParentUser()) //
                .executeUpdate();
    }
    
    public List<UserAutoSuggestRepresentation> getSimilarUsers(String searchTerm) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(User.class).get();
        Query query = queryBuilder.keyword().fuzzy().onFields("firstName", "lastName", "email").matching(searchTerm).createQuery();
        
        Criteria filterCriteria = fullTextSession.createCriteria(User.class) //
                .createAlias("childUsers", "childUser", JoinType.INNER_JOIN) //
                .createAlias("childUser.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("childUser.userAccount", "childUserAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.ne("userRole.role.id", PrismRole.APPLICATION_CREATOR)) //
                .add(Restrictions.disjunction()
                        .add(Restrictions.isNull("user.userAccount"))
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.disjunction()
                        .add(Restrictions.isNull("childUser.userAccount"))
                        .add(Restrictions.eq("childUserAccount.enabled", true)));

        return fullTextSession.createFullTextQuery(query, User.class) //
                .setProjection("firstName", "lastName", "email") //
                .setCriteriaQuery(filterCriteria)
                .setMaxResults(10) //
                .setResultTransformer(Transformers.aliasToBean(UserAutoSuggestRepresentation.class)) //
                .list();
    }
    
    public List<MailDescriptor> getUseDueTaskNotification() {
        // TODO Auto-generated method stub
        return null;
    }

}
