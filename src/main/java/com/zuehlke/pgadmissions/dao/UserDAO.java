package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.PrismConstants.PROFILE_LIST_PAGE_ROW_COUNT;
import static com.zuehlke.pgadmissions.PrismConstants.RESOURCE_LIST_PAGE_ROW_COUNT;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getEndorsementActionFilterResolution;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getEndorsementActionJoinResolution;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceStateActionConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getSimilarUserRestriction;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getUserRoleWithPartnerConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOauthProvider.LINKEDIN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_ACTIVITY_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_STUDENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.getUnverifiedRoles;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_INTERVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_POTENTIAL_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.dto.ApplicationAppointmentDTO;
import com.zuehlke.pgadmissions.dto.ProfileListRowDTO;
import com.zuehlke.pgadmissions.dto.UserCompetenceDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.utils.PrismEncryptionUtils;

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
                .add(Restrictions.eq("userAccount.password", PrismEncryptionUtils.getMD5(password))) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

    public List<User> getUsersForResourceAndRoles(Resource resource, PrismRole... roleIds) {
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

    public List<User> getUsersForResourcesAndRoles(Set<Resource> resources, PrismRole... roleIds) {
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
                .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class)).list();
    }

    public List<UserSelectionDTO> getUsersNotInterestedInApplication(Application application) {
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
                .add(Restrictions.eq("applicationInterested", false)) //
                .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class)).list();
    }

    public List<UserSelectionDTO> getUsersPotentiallyInterestedInApplication(Integer program, List<Integer> relatedProjects,
            List<Integer> relatedApplications) {
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
                .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class)).list();
    }

    public void refreshParentUser(User linkIntoUser, User linkFromUser) {
        sessionFactory.getCurrentSession()
                .createQuery("update User " //
                        + "set parentUser = :user " //
                        + "where parentUser = :linkIntoUserParentUser " //
                        + "or parentUser = :linkFromUserParentUser") //
                .setParameter("user", linkIntoUser) //
                .setParameter("linkIntoUserParentUser", linkIntoUser.getParentUser()) //
                .setParameter("linkFromUserParentUser", linkFromUser.getParentUser()) //
                .executeUpdate();
    }

    public void switchParentUser(User oldParentUser, User newParentUser) {
        sessionFactory.getCurrentSession()
                .createQuery("update User " //
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
                        .add(Projections.groupProperty("email"), "email") //
                        .add(Projections.property("userAccount.linkedinImageUrl"), "accountImageUrl") //
                        .add(Projections.property("userAccount.linkedinProfileUrl"), "accountProfileUrl")) //
                .createAlias("userRoles", "userRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
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

    public List<User> getResourceUsers(Resource resource) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .addOrder(Order.desc("user.lastName")) //
                .addOrder(Order.desc("user.firstName")) //
                .list();
    }

    public void selectParentUser(User newParentUser) {
        sessionFactory.getCurrentSession()
                .createQuery("update User " //
                        + "set parentUser = :newParentUser " //
                        + "where parentUser = :oldParentUser") //
                .setParameter("newParentUser", newParentUser) //
                .setParameter("oldParentUser", newParentUser.getParentUser()) //
                .executeUpdate();
    }

    public User getByLinkedinId(String linkedinId) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userAccount.linkedinId", linkedinId)) //
                .uniqueResult();
    }

    public List<User> getBouncedOrUnverifiedUsers(Resource resource, HashMultimap<PrismScope, Integer> administratorResources,
            HashMultimap<PrismScope, PrismScope> expandedScopes, UserListFilterDTO userListFilterDTO) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN); //

        appendAdministratorConditions(criteria, resource, administratorResources, expandedScopes);

        if (userListFilterDTO.isInvalidOnly()) {
            criteria.add(Restrictions.isNotNull("user.emailBouncedMessage"));
        } else {
            criteria.add(getUserAccountUnverifiedDisjunction());
        }

        String searchTerm = userListFilterDTO.getSearchTerm();
        if (searchTerm != null) {
            criteria.add(getSimilarUserRestriction("user", searchTerm)); //
        }

        Integer lastUserId = userListFilterDTO.getLastUserId();
        if (lastUserId != null) {
            criteria.add(Restrictions.lt("user.id", lastUserId));
        }

        return (List<User>) criteria.addOrder(Order.desc("user.id")) //
                .setMaxResults(RESOURCE_LIST_PAGE_ROW_COUNT) //
                .list();
    }

    public User getBouncedOrUnverifiedUser(Integer userId, Resource resource, HashMultimap<PrismScope, Integer> administratorResources,
            HashMultimap<PrismScope, PrismScope> expandedScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN); //

        appendAdministratorConditions(criteria, resource, administratorResources, expandedScopes);

        return (User) criteria.add(getUserAccountUnverifiedDisjunction()) //
                .add(Restrictions.eq("user.id", userId)) //
                .uniqueResult();
    }

    public List<User> getUsersWithAction(Resource resource, PrismAction... actions) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("userRole.user")) //
                .createAlias(resource.getResourceScope().getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("resource.user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userRoles", "ownerRole", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinResolution()) //
                .createAlias("ownerRoles.department", "ownerDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.in("stateAction.action.id", actions)) //
                .add(getUserRoleWithPartnerConstraint(resource)) //
                .add(getEndorsementActionFilterResolution())
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .list();
    }

    public List<UserCompetenceDTO> getUserCompetences(User user) {
        return (List<UserCompetenceDTO>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user"), "user") //
                        .add(Projections.groupProperty("competence.competence"), "competence") //
                        .add(Projections.countDistinct("competence.id"), "ratingCount") //
                        .add(Projections.sum("competence.rating"), "ratingSum")) //
                .createAlias("comments", "comment", JoinType.INNER_JOIN) //
                .createAlias("comment.competences", "competence", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .setResultTransformer(Transformers.aliasToBean(UserCompetenceDTO.class)) //
                .list();
    }

    public List<UserRole> getUsersToVerify(PrismScope resourceScope, Collection<Integer> resources) {
        String resourceReference = resourceScope.getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.property("user")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.in("role.id", getUnverifiedRoles(resourceScope)));

        if (resources == null) {
            criteria.add(Restrictions.isNotNull(resourceReference));
        } else {
            criteria.add(Restrictions.in(resourceReference + ".id", resources)); //
        }

        return criteria.addOrder(Order.asc(resourceReference + ".name")) //
                .addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<Integer> getUsersWithActivityForResourceScope(PrismScope resourceScope, PrismScope roleScope, DateTime updateBaseline, LocalDate lastNotifiedBaseline) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN);

        String roleScopeReference = roleScope.getLowerCamelName();
        if (!resourceScope.equals(roleScope)) {
            criteria.createAlias("resource." + roleScopeReference, roleScopeReference, JoinType.INNER_JOIN) //
                    .createAlias(roleScopeReference + ".userRoles", "userRole", JoinType.INNER_JOIN);
        } else {
            criteria.createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN);
        }

        return (List<Integer>) criteria.createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("userNotification.notificationDefinition.id", SYSTEM_ACTIVITY_NOTIFICATION)) //
                .add(getSystemActivityNotificationLastSentConstraint(lastNotifiedBaseline)) //
                .add(getResourceActiveConstraint(updateBaseline))
                .add(getResourceStateActionConstraint()) //
                .add(Restrictions.eqProperty("stateAction.state", "resourceState.state")) //
                .list();
    }

    public List<Integer> getUsersWithActivityForPartnerResourceScope(PrismScope resourceScope, PrismScope roleScope, DateTime updateBaseline, LocalDate lastNotifiedBaseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("targetAdvert." + roleScope.getLowerCamelName(), "roleResource", JoinType.INNER_JOIN) //
                .createAlias("roleResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", true)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("userNotification.notificationDefinition.id", SYSTEM_ACTIVITY_NOTIFICATION)) //
                .createAlias("resource.user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userRoles", "ownerRole", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinResolution()) //
                .createAlias("ownerRoles.department", "ownerDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(getSystemActivityNotificationLastSentConstraint(lastNotifiedBaseline)) //
                .add(getResourceActiveConstraint(updateBaseline)) //
                .add(getEndorsementActionFilterResolution()) //
                .add(getResourceStateActionConstraint()) //
                .add(Restrictions.eqProperty("stateAction.state", "resourceState.state")) //
                .list();
    }

    public List<Integer> getUsersWithAppointmentsForApplications() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .createAlias("application", "application", JoinType.INNER_JOIN) //
                .createAlias("application.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("application.department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("application.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("application.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("application.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("application.comments", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.in("role.id", APPLICATION_CONFIRMED_INTERVIEW_GROUP.getRoles())) //
                .add(Restrictions.eq("resourceState.state.id", APPLICATION_INTERVIEW_PENDING_INTERVIEW)) //
                .add(Restrictions.isNotNull("comment.interviewAppointment.interviewDateTime")) //
                .addOrder(Order.asc("comment.interviewAppointment.interviewDateTime")) //
                .addOrder(Order.asc("application.id")) //
                .setResultTransformer(Transformers.aliasToBean(ApplicationAppointmentDTO.class)) //
                .list();
    }

    public List<ProfileListRowDTO> getUserProfiles(List<Integer> departments, ProfileListFilterDTO filter) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserAccount.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id").as("userId")) //
                        .add(Projections.property("user.firstName").as("userFirstName")) //
                        .add(Projections.property("user.firstName2").as("userFirstName2")) //
                        .add(Projections.property("user.firstName3").as("userFirstName3")) //
                        .add(Projections.property("user.lastName").as("userLastName")) //
                        .add(Projections.property("userAccount.linkedinImageUrl").as("userAccountImageUrl")) //
                        .add(Projections.property("userDocument.personalSummary").as("personalSummary")) //
                        .add(Projections.property("userDocument.cv.id").as("cvId")) //
                        .add(Projections.property("externalAccount.accountProfileUrl").as("linkedInProfileUrl")) //
                        .add(Projections.countDistinct("application.id").as("applicationCount")) //
                        .add(Projections.sum("application.applicationRatingCount").as("applicationRatingCount")) //
                        .add(Projections.avg("application.applicationRatingAverage").as("applicationRatingAverage")) //
                        .add(Projections.property("updatedTimestamp").as("updatedTimestamp")) //
                        .add(Projections.property("sequenceIdentifier").as("sequenceIdentifier"))) //
                .createAlias("externalAccounts", "externalAccount", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.eq("externalAccount.accountType", LINKEDIN)) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userRoles", "userRole", JoinType.INNER_JOIN,
                        Restrictions.eq("userRole.role.id", DEPARTMENT_STUDENT)) //
                .createAlias("qualifications", "qualification", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("qualification.advert", "qualificationAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("employmentPositions", "employmentPosition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("employmentPosition.advert", "employmentPositionAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userDocument", "userDocument", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.applications", "application", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.in("userRole.department.id", departments)) //
                .add(Restrictions.eq("shared", true));

        String keyword = filter.getKeyword();
        if (keyword != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.like("user.fullName", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("user.email", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("advert.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("advert.summary", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("advert.description", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("qualificationAdvert.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("qualificationAdvert.summary", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("qualificationAdvert.description", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("employmentPositionAdvert.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("employmentPositionAdvert.summary", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("employmentPositionAdvert.description", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("userDocument.personalSummary", keyword, MatchMode.ANYWHERE)));
        }

        String sequenceIdentifier = filter.getSequenceIdentifier();
        if (sequenceIdentifier != null) {
            criteria.add(Restrictions.lt("sequenceIdentifier", sequenceIdentifier));
        }

        return (List<ProfileListRowDTO>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
                .setMaxResults(PROFILE_LIST_PAGE_ROW_COUNT) //
                .setResultTransformer(Transformers.aliasToBean(ProfileListRowDTO.class)) //
                .list();
    }

    private Junction getSystemActivityNotificationLastSentConstraint(LocalDate lastNotifiedBaseline) {
        return Restrictions.disjunction() //
                .add(Restrictions.isNotNull("userNotification.id")) //
                .add(Restrictions.ge("userNotification.lastNotifiedDate", lastNotifiedBaseline));
    }

    private static Junction getResourceActiveConstraint(DateTime updateBaseline) {
        return Restrictions.conjunction() //
                .add(Restrictions.eq("role.verified", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                        .add(Restrictions.ge("updatedtimestamp", updateBaseline)));
    }

    private void appendAdministratorConditions(Criteria criteria, Resource resource, HashMultimap<PrismScope, Integer> resources, HashMultimap<PrismScope, PrismScope> scopes) {
        PrismScope resourceScope = resource.getResourceScope();
        String resourceReference = resourceScope.getLowerCamelName();

        Junction exclusionsDisjunction = Restrictions.disjunction();
        for (PrismScope expandedScope : scopes.keySet()) {
            Junction expandedConjunction = Restrictions.conjunction();

            String expandedReference = expandedScope.getLowerCamelName();
            if (resourceScope.equals(expandedScope)) {
                expandedConjunction.add(Restrictions.eq(resourceReference + ".id", resource.getId()));
            } else {
                expandedConjunction.add(Restrictions.eq(expandedReference + "." + resourceReference + ".id", resource.getId()));
            }

            Junction enclosingDisjunction = Restrictions.disjunction();
            for (PrismScope enclosingScope : scopes.get(expandedScope)) {
                Set<Integer> enclosingResources = resources.get(enclosingScope);
                if (!enclosingResources.isEmpty()) {

                    if (expandedScope.equals(enclosingScope)) {
                        enclosingDisjunction.add(Restrictions.in(expandedReference + ".id", enclosingResources));
                    } else {
                        enclosingDisjunction
                                .add(Restrictions.in(expandedReference + "." + enclosingScope.getLowerCamelName() + ".id", enclosingResources));
                    }

                }
            }

            if (enclosingDisjunction.conditions().iterator().hasNext()) {
                criteria.createAlias(expandedReference, expandedReference, JoinType.LEFT_OUTER_JOIN, //
                        expandedConjunction.add(enclosingDisjunction));
                exclusionsDisjunction.add(Restrictions.isNotNull(expandedReference + ".id"));
            }
        }

        if (exclusionsDisjunction.conditions().iterator().hasNext()) {
            criteria.add(exclusionsDisjunction);
        }
    }

    private Junction getUserAccountUnverifiedDisjunction() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("user.userAccount")) //
                .add(Restrictions.eq("userAccount.enabled", false)) //
                .add(Restrictions.isNotNull("user.emailBouncedMessage"));
    }

}
