package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.mail.MailDescriptor;
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
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("role.id", PrismRole.APPLICATION_SUGGESTED_SUPERVISOR)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .addOrder(Order.asc("user.firstName")) //
                .addOrder(Order.asc("user.lastName")) //
                .list();
    }

    public List<User> getUsersPotentiallyInterestedInApplication(Application application, List<User> usersInterestedInApplication) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("program", application.getProgram())) //
                        .add(Restrictions.eq("project", application.getProject())) //
                        .add(Restrictions.eq("application", application))) //
                .add(Restrictions.in("role.id", Arrays.asList(PrismRole.PROGRAM_APPROVER, PrismRole.PROGRAM_VIEWER, //
                        PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROJECT_SECONDARY_SUPERVISOR, //
                        PrismRole.APPLICATION_ADMINISTRATOR, PrismRole.APPLICATION_REVIEWER, //
                        PrismRole.APPLICATION_INTERVIEWER, PrismRole.APPLICATION_PRIMARY_SUPERVISOR, //
                        PrismRole.APPLICATION_SECONDARY_SUPERVISOR, PrismRole.APPLICATION_VIEWER_RECRUITER))) //
                .add(Restrictions.eq("userAccount.enabled", true)); //
        
        for (User excludedUser : usersInterestedInApplication) {
            criteria.add(Restrictions.ne("user", excludedUser));
        }
        
        return criteria.addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .list();
    }
    
    public void mergeUsers(User mergeFrom, User mergeInto) {
        sessionFactory.getCurrentSession().createSQLQuery( //
                "CALL SP_MERGE_ENTITIES(:schema, :table, :mergeFromId, :mergeIntoId)") //
                .addSynchronizedEntityClass(User.class) //
                .setParameter("schema", schema.toUpperCase()) //
                .setParameter("table", "USER") //
                .setParameter("mergeFromId", mergeFrom.getId()) //
                .setParameter("mergeIntoId", mergeInto.getId()) //
                .executeUpdate();
    }
    
    public User getAuthenticatedUser(String email, String password) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("email", email)) //
                .add(Restrictions.eq("userAccount.password", encryptionUtils.getMD5Hash(password))) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

    public List<MailDescriptor> getUseDueTaskNotification() {
        // TODO Auto-generated method stub
        return null;
    }

}
