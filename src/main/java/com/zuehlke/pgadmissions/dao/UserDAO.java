package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Repository
@SuppressWarnings("unchecked")
public class UserDAO {

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private SessionFactory sessionFactory;

    public User getUserByActivationCode(String activationCode) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .add(Restrictions.eq("activationCode", activationCode)) //
                .uniqueResult();
    }

    public Integer getNumberOfActiveApplicationsForApplicant(User user) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.count("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.not(Restrictions.not(Restrictions.in("state.id", Arrays.asList(PrismState.APPLICATION_APPROVED_PENDING_EXPORT,
                        PrismState.APPLICATION_APPROVED_PENDING_CORRECTION, PrismState.APPLICATION_APPROVED_COMPLETED))))) //
                .add(Restrictions.not(Restrictions.not(Restrictions.in("state.id", Arrays.asList(PrismState.APPLICATION_REJECTED_PENDING_EXPORT,
                        PrismState.APPLICATION_REJECTED_PENDING_CORRECTION, PrismState.APPLICATION_REJECTED_COMPLETED))))) //
                .add(Restrictions.not(Restrictions.eq("state,stateGroup", PrismStateGroup.APPLICATION_WITHDRAWN))) //
                .uniqueResult();
    }

    public List<User> getUsersForResourceAndRole(Resource resource, PrismRole authority) {
        return sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN).add(Restrictions.disjunction() //
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
                .add(Restrictions.eq("userAccount.enabled", true)).add(Restrictions.eq("parentUserAccount.enabled", true)); //

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
                .add(Restrictions.eq("userAccount.enabled", true)).add(Restrictions.eq("parentUserAccount.enabled", true)); //

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
                .add(Restrictions.eq("userAccount.enabled", true)).add(Restrictions.eq("parentUserAccount.enabled", true)); //

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

    public void refreshParentUser(User user) {
        sessionFactory.getCurrentSession().createQuery( //
                "update User " //
                        + "set parentUser = :user " //
                        + "where parentUser = :parentUser") //
                .setParameter("user", user).setParameter("parentUser", user.getParentUser()) //
                .executeUpdate();
    }

    public List<UserRepresentation> getSimilarUsers(String searchTerm) {
        return (List<UserRepresentation>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("firstName"), "firstName") //
                        .add(Projections.property("lastName"), "lastName") //
                        .add(Projections.groupProperty("email"), "email")) //
                .createAlias("userRoles", "userRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eqProperty("id", "parentUser.id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userRole.id")) //
                        .add(Restrictions.ne("userRole.role.id", PrismRole.APPLICATION_CREATOR))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("firstName", searchTerm, MatchMode.START)) //
                        .add(Restrictions.ilike("lastName", searchTerm, MatchMode.START)) //
                        .add(Restrictions.ilike("fullName", searchTerm, MatchMode.START)) //
                        .add(Restrictions.ilike("email", searchTerm, MatchMode.START))) //
                .setMaxResults(10) //
                .setResultTransformer(Transformers.aliasToBean(UserRepresentation.class)) //
                .list();
    }
    
    public List<User> getEnabledResourceUsers(Resource resource) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user"))
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .list();
    }
    
    public List<Integer> getMatchingUsers(String searchTerm) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("fullName", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.ilike("email", searchTerm, MatchMode.ANYWHERE)))
                .list();
    }

}
