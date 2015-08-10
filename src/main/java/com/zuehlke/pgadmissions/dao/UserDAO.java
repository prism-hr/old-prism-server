package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceStateActionConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getSimilarUserRestriction;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getUserRoleConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_POTENTIAL_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.utils.PrismConstants.LIST_PAGE_ROW_COUNT;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserConnection;
import com.zuehlke.pgadmissions.domain.user.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.dto.UserCompetenceDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Repository
@SuppressWarnings("unchecked")
public class UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public User getUserByActivationCode(String activationCode) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .add(Restrictions.eq("activationCode", activationCode)) //
                .uniqueResult();
    }

    public User getAuthenticatedUser(String email, String password) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("email", email)) //
                .add(Restrictions.eq("userAccount.password", EncryptionUtils.getMD5(password))) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

    public List<User> getUsersForResourceAndRoles(Resource<?> resource, PrismRole... roleIds) {
        return sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("application", resource.getApplication())) //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram())) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("system", resource.getSystem()))) //
                .add(Restrictions.in("role.id", roleIds)) //
                .list();
    }

    public List<User> getUsersForResourcesAndRoles(Set<Resource<?>> resources, PrismRole... roleIds) {
        return sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.in(roleIds[0].getScope().getLowerCamelName(), resources)) //
                .add(Restrictions.in("role.id", roleIds)) //
                .list();
    }

    public String getUserInstitutionId(User user, Institution institution, PrismUserInstitutionIdentity identityType) {
        return (String) sessionFactory.getCurrentSession().createCriteria(UserInstitutionIdentity.class) //
                .setProjection(Projections.property("identifier")) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eqOrIsNull("identityType", identityType)) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<UserSelectionDTO> getSuggestedSupervisors(Application application) {
        return (List<UserSelectionDTO>) sessionFactory.getCurrentSession().createCriteria(ApplicationSupervisor.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("user.parentUser"), "user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.parentUser", "parentUser", JoinType.INNER_JOIN) //
                .createAlias("parentUser.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.id")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .addOrder(Order.asc("parentUser.firstName")) //
                .addOrder(Order.asc("parentUser.lastName")) //
                .addOrder(Order.asc("user.id")) //
                .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class)) //
                .list();
    }

    public List<UserSelectionDTO> getUsersInterestedInApplication(Application application) {
        return (List<UserSelectionDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.parentUser"), "user") //
                        .add(Projections.max("createdTimestamp"), "eventTimestamp")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.parentUser", "parentUser", JoinType.INNER_JOIN) //
                .createAlias("parentUser.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.id")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.eq("applicationInterested", true)) //
                .addOrder(Order.asc("parentUser.firstName")) //
                .addOrder(Order.asc("parentUser.lastName")) //
                .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class))
                .list();
    }

    public List<UserSelectionDTO> getUsersNotInterestedInApplication(Application application) {
        return (List<UserSelectionDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.parentUser"), "user")
                        .add(Projections.max("createdTimestamp"), "eventTimestamp")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.parentUser", "parentUser", JoinType.INNER_JOIN) //
                .createAlias("parentUser.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.id")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.eq("applicationInterested", false)) //
                .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class))
                .list();
    }

    public List<UserSelectionDTO> getUsersPotentiallyInterestedInApplication(Integer program, List<Integer> relatedProjects, List<Integer> relatedApplications) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.parentUser"), "user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.parentUser", "parentUser", JoinType.INNER_JOIN) //
                .createAlias("parentUser.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN); //

        Junction condition = Restrictions.disjunction() //
                .add(Restrictions.eq("program.id", program)); //

        if (!relatedProjects.isEmpty()) {
            condition.add(Restrictions.in("project.id", relatedProjects));
        }

        if (!relatedApplications.isEmpty()) {
            condition.add(Restrictions.in("application.id", relatedApplications));
        }

        return (List<UserSelectionDTO>) criteria.add(condition) //
                .add(Restrictions.in("role.id", APPLICATION_POTENTIAL_SUPERVISOR_GROUP.getRoles())) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.id")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .addOrder(Order.asc("parentUser.firstName")) //
                .addOrder(Order.asc("parentUser.lastName")) //
                .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class))
                .list();
    }

    public void refreshParentUser(User linkIntoUser, User linkFromUser) {
        sessionFactory.getCurrentSession().createQuery( //
                "update User " //
                        + "set parentUser = :user " //
                        + "where parentUser = :linkIntoUserParentUser " //
                        + "or parentUser = :linkFromUserParentUser") //
                .setParameter("user", linkIntoUser) //
                .setParameter("linkIntoUserParentUser", linkIntoUser.getParentUser()) //
                .setParameter("linkFromUserParentUser", linkFromUser.getParentUser()) //
                .executeUpdate();
    }

    public void switchParentUser(User oldParentUser, User newParentUser) {
        sessionFactory.getCurrentSession().createQuery( //
                "update User " //
                        + "set parentUser = :newParentUser " //
                        + "where parentUser = :oldParentUser") //
                .setParameter("newParentUser", newParentUser) //
                .setParameter("oldParentUser", oldParentUser) //
                .executeUpdate();
    }

    public List<String> getLinkedUserAccounts(User user) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.property("email")) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("parentUser", user.getParentUser())) //
                .add(Restrictions.ne("id", user.getId())) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .list();
    }

    public List<UserRepresentationSimple> getSimilarUsers(String searchTerm) {
        return (List<UserRepresentationSimple>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("firstName"), "firstName") //
                        .add(Projections.property("lastName"), "lastName") //
                        .add(Projections.groupProperty("email"), "email")
                        .add(Projections.property("primaryExternalAccount.accountImageUrl"), "accountImageUrl")
                        .add(Projections.property("externalAccount.accountProfileUrl"), "accountProfileUrl")) //
                .createAlias("userRoles", "userRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userAccount.primaryExternalAccount", "primaryExternalAccount", JoinType.LEFT_OUTER_JOIN)
                .createAlias("userAccount.externalAccounts", "externalAccount", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("externalAccount.accountType", OauthProvider.LINKEDIN))
                .add(Restrictions.eqProperty("id", "parentUser.id")) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userRole.id")) //
                        .add(Restrictions.ne("userRole.role.id", PrismRole.APPLICATION_CREATOR))) //
                .add(getSimilarUserRestriction(searchTerm)) //
                .addOrder(Order.desc("lastName")) //
                .addOrder(Order.desc("firstName")) //
                .setMaxResults(10) //
                .setResultTransformer(Transformers.aliasToBean(UserRepresentationSimple.class)) //
                .list();
    }

    public List<User> getResourceUsers(Resource<?> resource) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .addOrder(Order.desc("user.lastName")) //
                .addOrder(Order.desc("user.firstName")) //
                .list();
    }

    public void selectParentUser(User newParentUser) {
        sessionFactory.getCurrentSession().createQuery( //
                "update User " //
                        + "set parentUser = :newParentUser " //
                        + "where parentUser = :oldParentUser") //
                .setParameter("newParentUser", newParentUser) //
                .setParameter("oldParentUser", newParentUser.getParentUser()) //
                .executeUpdate();
    }

    public User getByExternalAccountId(OauthProvider oauthProvider, String externalId) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userAccount.externalAccounts", "externalAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("externalAccount.accountType", oauthProvider)) //
                .add(Restrictions.eq("externalAccount.accountIdentifier", externalId)) //
                .uniqueResult();
    }

    public <T extends Resource<?>> List<User> getBouncedOrUnverifiedUsers(HashMultimap<PrismScope, T> userAdministratorResources,
            UserListFilterDTO userListFilterDTO) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.INNER_JOIN); //

        Junction roleDisjunction = Restrictions.disjunction();
        for (PrismScope scope : userAdministratorResources.keySet()) {
            String scopeReference = scope.getLowerCamelName();
            roleDisjunction.add(Restrictions.in(scopeReference, userAdministratorResources.get(scope)));
        }

        criteria.add(roleDisjunction);

        if (userListFilterDTO.isInvalidOnly()) {
            criteria.add(Restrictions.isNotNull("user.emailBouncedMessage"));
        } else {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.isNull("user.userAccount")) //
                    .add(Restrictions.eq("userAccount.enabled", false)) //
                    .add(Restrictions.isNotNull("user.emailBouncedMessage")));
        }

        String searchTerm = userListFilterDTO.getSearchTerm();
        if (searchTerm != null) {
            criteria.add(WorkflowDAOUtils.getSimilarUserRestriction("user", searchTerm)); //
        }

        Integer lastUserId = userListFilterDTO.getLastUserId();
        if (lastUserId != null) {
            criteria.add(Restrictions.lt("user.id", lastUserId));
        }

        return (List<User>) criteria.addOrder(Order.desc("user.id")) //
                .setMaxResults(LIST_PAGE_ROW_COUNT) //
                .list();
    }

    public <T extends Resource<?>> User getBouncedOrUnverifiedUser(HashMultimap<PrismScope, T> userAdministratorResources, Integer userId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN); //

        Disjunction disjunction = Restrictions.disjunction();
        for (PrismScope scope : userAdministratorResources.keySet()) {
            disjunction.add(Restrictions.in(scope.getLowerCamelName(), userAdministratorResources.get(scope)));
        }

        return (User) criteria.add(disjunction) //
                .add(Restrictions.eq("user.id", userId)) //
                .uniqueResult();
    }

    public List<User> getUsersWithAction(Resource<?> resource, PrismAction... actions) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class)
                .setProjection(Projections.groupProperty("userRole.user")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.in("stateAction.action.id", actions)) //
                .add(getUserRoleConstraint(resource, "stateActionAssignment")) //
                .add(getResourceStateActionConstraint()) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .list();
    }

    public UserConnection getUserConnection(User userRequested, User userConnected) {
        return (UserConnection) sessionFactory.getCurrentSession().createCriteria(UserConnection.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("userRequested", userRequested)) //
                                .add(Restrictions.eq("userConnected", userConnected))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("userConnected", userRequested)) //
                                .add(Restrictions.eq("userRequested", userConnected)))) //
                .uniqueResult();
    }

    public UserConnection getUserConnectionStrict(User userRequested, User userConnected) {
        return (UserConnection) sessionFactory.getCurrentSession().createCriteria(UserConnection.class) //
                .add(Restrictions.eq("userRequested", userRequested)) //
                .add(Restrictions.eq("userConnected", userConnected)) //
                .uniqueResult();
    }

    public List<UserCompetenceDTO> getUserCompetences(User user) {
        return (List<UserCompetenceDTO>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id"), "user") //
                        .add(Projections.groupProperty("competence.competence.id"), "competence") //
                        .add(Projections.countDistinct("competence.id"), "ratingCount") //
                        .add(Projections.sum("competence.rating"), "ratingSum")) //
                .createAlias("comments", "comment", JoinType.INNER_JOIN) //
                .createAlias("comment.competences", "competence", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .setResultTransformer(Transformers.aliasToBean(UserCompetenceDTO.class)) //
                .list();
    }

    public Long getUserProgramRelationCount(User user, ImportedProgram program) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.count("qualification.id")) //
                .createAlias("qualification", "qualification", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("program", program)) //
                .uniqueResult();

    }

    public void deleteUserProgram(User user, ImportedProgram program) {
        sessionFactory.getCurrentSession().createSQLQuery( //
                "delete UserProgram " //
                        + "where user = :user " //
                        + "and program = :program") //
                .setParameter("user", user) //
                .setParameter("program", program) //
                .executeUpdate();
    }

}
