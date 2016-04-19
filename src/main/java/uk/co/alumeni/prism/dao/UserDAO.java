package uk.co.alumeni.prism.dao;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.ArrayUtils.contains;
import static org.hibernate.sql.JoinType.INNER_JOIN;
import static org.hibernate.transform.Transformers.aliasToBean;
import static uk.co.alumeni.prism.PrismConstants.PROFILE_LIST_PAGE_ROW_COUNT;
import static uk.co.alumeni.prism.PrismConstants.RESOURCE_LIST_PAGE_ROW_COUNT;
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getMatchingUserConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getReadOrUnreadMessageConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getResourceParentManageableStateConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getUnreadMessageConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getVisibleMessageConstraint;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_ACTIVITY_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_REMINDER_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_INTERVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_POTENTIAL_SUPERVISOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static uk.co.alumeni.prism.utils.PrismEnumUtils.values;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.StateActionAssignment;
import uk.co.alumeni.prism.dto.ActivityMessageCountDTO;
import uk.co.alumeni.prism.dto.EntityLocationDTO;
import uk.co.alumeni.prism.dto.ProfileListRowDTO;
import uk.co.alumeni.prism.dto.UnverifiedUserDTO;
import uk.co.alumeni.prism.dto.UserCompetenceDTO;
import uk.co.alumeni.prism.dto.UserOrganizationDTO;
import uk.co.alumeni.prism.dto.UserSelectionDTO;
import uk.co.alumeni.prism.rest.dto.UserListFilterDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

import com.google.common.collect.HashMultimap;

@Repository
@SuppressWarnings("unchecked")
public class UserDAO {

    @Inject
    private WorkflowDAO workflowDAO;

    @Inject
    private SessionFactory sessionFactory;
    
    public User getUserByActivationCode(String activationCode) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .add(Restrictions.eq("activationCode", activationCode)) //
                .uniqueResult();
    }
    
    public List<User> getUsers(List<Integer> userIds) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .add(Restrictions.in("id", userIds)) //
                .list();
    }

    public List<UserSelectionDTO> getUsersInterestedInApplication(Application application) {
        return (List<UserSelectionDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user"), "user") //
                        .add(Projections.max("submittedTimestamp"), "eventTimestamp")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.id")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.eq("interested", true)) //
                .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class)).list();
    }

    public List<UserSelectionDTO> getUsersNotInterestedInApplication(Application application) {
        return (List<UserSelectionDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user"), "user") //
                        .add(Projections.max("submittedTimestamp"), "eventTimestamp")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.id")) //
                        .add(Restrictions.eq("userAccount.enabled", true))) //
                .add(Restrictions.eq("interested", false)) //
                .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class)).list();
    }

    public List<UserSelectionDTO> getUsersPotentiallyInterestedInApplication(List<Integer> programs, List<Integer> projects, List<Integer> applications) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user").as("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN); //

        boolean anyConstraint = false;
        Junction condition = Restrictions.disjunction();
        if (isNotEmpty(programs)) {
            condition.add(Restrictions.in("program.id", programs));
            anyConstraint = true;
        }

        if (isNotEmpty(projects)) {
            condition.add(Restrictions.in("project.id", projects));
            anyConstraint = true;
        }

        if (isNotEmpty(applications)) {
            condition.add(Restrictions.in("application.id", applications));
            anyConstraint = true;
        }

        if (anyConstraint) {
            return (List<UserSelectionDTO>) criteria.add(condition) //
                    .add(Restrictions.in("role.id", APPLICATION_POTENTIAL_SUPERVISOR_GROUP.getRoles())) //
                    .add(Restrictions.disjunction() //
                            .add(Restrictions.isNull("userAccount.id")) //
                            .add(Restrictions.eq("userAccount.enabled", true))) //
                    .addOrder(Order.asc("user.firstName")) //
                    .addOrder(Order.asc("user.lastName")) //
                    .setResultTransformer(Transformers.aliasToBean(UserSelectionDTO.class)).list();
        }

        return emptyList();
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

    public List<User> getLinkedUsers(User user) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("parentUser", user.getParentUser())) //
                .add(Restrictions.ne("id", user.getId())) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .list();
    }

    public List<UserRepresentationSimple> getSimilarUsers(String searchTerm) {
        return (List<UserRepresentationSimple>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("id"))
                        .add(Projections.property("firstName").as("firstName")) //
                        .add(Projections.property("lastName").as("lastName")) //
                        .add(Projections.property("email").as("email")) //
                        .add(Projections.property("userAccount.linkedinImageUrl").as("accountImageUrl")) //
                        .add(Projections.property("userAccount.linkedinProfileUrl").as("accountProfileUrl"))) //
                .createAlias("userRoles", "userRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userRole.id")) //
                        .add(Restrictions.ne("userRole.role.id", PrismRole.APPLICATION_CREATOR))) //
                .add(getMatchingUserConstraint(searchTerm)) //
                .addOrder(Order.desc("lastName")) //
                .addOrder(Order.desc("firstName")) //
                .setMaxResults(10) //
                .setResultTransformer(Transformers.aliasToBean(UserRepresentationSimple.class)) //
                .list();
    }

    public List<User> getResourceUsers(Resource resource, PrismRole role) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .add(Restrictions.eq("role.id", role))
                .list();
    }

    public void setParentUser(User newParentUser) {
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

    public List<User> getBouncedOrUnverifiedUsers(HashMultimap<PrismScope, Integer> enclosedResources, UserListFilterDTO userListFilterDTO) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN); //

        appendAdministratorConditions(criteria, enclosedResources);

        if (userListFilterDTO.isInvalidOnly()) {
            criteria.add(Restrictions.isNotNull("user.emailBouncedMessage"));
        } else {
            criteria.add(getUserAccountUnverifiedConstraint());
        }

        String searchTerm = userListFilterDTO.getSearchTerm();
        if (searchTerm != null) {
            criteria.add(WorkflowDAO.getMatchingUserConstraint("user", searchTerm)); //
        }

        Integer lastUserId = userListFilterDTO.getLastUserId();
        if (lastUserId != null) {
            criteria.add(Restrictions.lt("user.id", lastUserId));
        }

        return (List<User>) criteria.addOrder(Order.desc("user.id")) //
                .setMaxResults(RESOURCE_LIST_PAGE_ROW_COUNT) //
                .list();
    }

    public User getBouncedOrUnverifiedUser(Integer userId, HashMultimap<PrismScope, Integer> enclosedResources) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN); //

        appendAdministratorConditions(criteria, enclosedResources);

        return (User) criteria.add(getUserAccountUnverifiedConstraint()) //
                .add(Restrictions.eq("user.id", userId)) //
                .uniqueResult();
    }

    public List<User> getUsersWithActions(PrismScope scope, Resource resource, PrismAction... actions) {
        return workflowDAO.getWorkflowCriteriaList(scope, Projections.groupProperty("userRole.user")) //
                .add(getUsersWithActionsConstraint(resource, actions)) //
                .list();
    }

    public List<User> getUsersWithActions(PrismScope scope, PrismScope parentScope, Resource resource, PrismAction... actions) {
        return workflowDAO.getWorkflowCriteriaList(scope, parentScope, Projections.groupProperty("userRole.user")) //
                .add(getUsersWithActionsConstraint(resource, actions)) //
                .list();
    }

    public List<User> getUsersWithActions(PrismScope scope, PrismScope targeterScope, PrismScope targetScope, Collection<Integer> targeterEntities,
            Resource resource, PrismAction... actions) {
        return workflowDAO.getWorkflowCriteriaList(scope, targeterScope, targetScope, targeterEntities, Projections.groupProperty("userRole.user"))
                .add(getUsersWithActionsConstraint(resource, actions)) //
                .add(WorkflowDAO.getTargetActionConstraint()) //
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

    public List<UnverifiedUserDTO> getUsersToVerify(PrismScope resourceScope, Collection<Integer> resources) {
        String resourceReference = resourceScope.getLowerCamelName();

        ProjectionList projections = Projections.projectionList() //
                .add(Projections.groupProperty("institution.id").as("institutionId")) //
                .add(Projections.property("institution.name").as("institutionName")) //
                .add(Projections.property("institution.logoImage.id").as("logoImageId"));

        boolean isDepartment = resourceScope.equals(DEPARTMENT);
        if (isDepartment) {
            projections.add(Projections.groupProperty("department.id").as("departmentId")) //
                    .add(Projections.groupProperty("department.name").as("departmentName"));
        }

        projections.add(Projections.groupProperty("user.id").as("userId")) //
                .add(Projections.property("user.firstName").as("userFirstName"))
                .add(Projections.property("user.lastName").as("userLastName")) //
                .add(Projections.property("user.email").as("userEmail")) //
                .add(Projections.property("userAccount.linkedinProfileUrl").as("userLinkedinProfileUrl"))
                .add(Projections.property("userAccount.linkedinImageUrl").as("userLinkedinImageUrl")) //
                .add(Projections.property("userAccount.portraitImage.id").as("userPortraitImageId")) //
                .add(Projections.groupProperty("userRole.role.id").as("roleId"));

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projections) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".userRoles", "userRole", JoinType.INNER_JOIN);

        if (isDepartment) {
            criteria.createAlias(resourceReference + ".institution", "institution", JoinType.INNER_JOIN);
        }

        criteria.createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN);

        criteria.add(getResourceParentManageableStateConstraint(resourceScope));
        if (isNotEmpty(resources)) {
            criteria.add(Restrictions.in(resourceReference + ".id", resources));
        }

        return (List<UnverifiedUserDTO>) criteria //
                .add(Restrictions.in("userRole.role.id", values(PrismRole.class, resourceScope, "STUDENT_UNVERIFIED", "VIEWER_UNVERIFIED"))) //
                .setResultTransformer(aliasToBean(UnverifiedUserDTO.class)) //
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
                .list();
    }

    public List<Integer> getUsersWithUsersToVerify(PrismScope resourceScope, List<Integer> resources) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .add(Restrictions.eq("role.id", PrismRole.valueOf(resourceScope.name() + "_ADMINISTRATOR"))) //
                .add(Restrictions.in(resourceScope.getLowerCamelName() + ".id", resources)) //
                .list();
    }

    public List<Integer> getUsersWithConnectionsToVerify() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("acceptAdvertUser.id")) //
                .add(Restrictions.eq("partnershipState", ENDORSEMENT_PENDING)) //
                .list();
    }

    public List<Integer> getUsersWithConnectionsToVerify(PrismScope resourceScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("userRole.user.id")) //
                .createAlias("acceptAdvert", "acceptAdvert", JoinType.INNER_JOIN) //
                .createAlias("acceptAdvert." + resourceScope.getLowerCamelName(), "acceptResource", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("acceptAdvert.id", "acceptResource.advert.id")) //
                .createAlias("acceptResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("partnershipState", ENDORSEMENT_PENDING)) //
                .add(Restrictions.eq("userRole.role.id", PrismRole.valueOf(resourceScope.name() + "_ADMINISTRATOR")))
                .list();
    }

    public List<Integer> getUsersWithVerifiedRoles(PrismScope resourceScope, Collection<Integer> resources) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.in(resourceScope.getLowerCamelName() + ".id", resources)) //
                .add(Restrictions.eq("role.verified", true)) //
                .list();
    }

    public List<Integer> getUsersWithVerifiedRolesForChildResource(PrismScope resourceScope, PrismScope childScope, Collection<Integer> resources) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("userRole.user.id")) //
                .createAlias(childScope.getLowerCamelName() + "s", "childResource", JoinType.INNER_JOIN) //
                .createAlias("childResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", resources)) //
                .add(Restrictions.eq("role.verified", true)) //
                .list();
    }

    public List<ProfileListRowDTO> getUserProfiles(PrismScope scope, Collection<Integer> resources, User user) {
        return (List<ProfileListRowDTO>) sessionFactory.getCurrentSession().createCriteria(UserAccount.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id").as("userId")) //
                        .add(Projections.property("updatedTimestamp").as("updatedTimestamp"))) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN)
                .add(Restrictions.in("userRole." + scope.getLowerCamelName() + ".id", resources)) //
                .add(Restrictions.eq("role.roleCategory", STUDENT)) //
                .add(Restrictions.eq("role.verified", true)) //
                .add(Restrictions.eq("shared", true)) //
                .add(Restrictions.ne("user.id", user.getId())) //
                .setResultTransformer(Transformers.aliasToBean(ProfileListRowDTO.class))
                .list();
    }

    public List<ProfileListRowDTO> getUserProfiles(PrismScope scope, Collection<Integer> resources, ProfileListFilterDTO filter, User user,
            String lastSequenceIdentifier) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserAccount.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user.id").as("userId")) //
                        .add(Projections.property("user.firstName").as("userFirstName")) //
                        .add(Projections.property("user.firstName2").as("userFirstName2")) //
                        .add(Projections.property("user.firstName3").as("userFirstName3")) //
                        .add(Projections.property("user.lastName").as("userLastName")) //
                        .add(Projections.property("user.email").as("userEmail")) //
                        .add(Projections.property("linkedinImageUrl").as("userAccountImageUrl")) //
                        .add(Projections.property("linkedinProfileUrl").as("linkedInProfileUrl")) //
                        .add(Projections.property("completeScore").as("completeScore")) //
                        .add(Projections.countDistinct("application.id").as("applicationCount")) //
                        .add(Projections.sum("application.applicationRatingCount").as("applicationRatingCount")) //
                        .add(Projections.avg("application.applicationRatingAverage").as("applicationRatingAverage")) //
                        .add(Projections.property("updatedTimestamp").as("updatedTimestamp")) //
                        .add(Projections.property("sequenceIdentifier").as("sequenceIdentifier"))) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("qualifications", "qualification", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("qualification.advert", "qualificationAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("employmentPositions", "employmentPosition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("employmentPosition.advert", "employmentPositionAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("document", "userDocument", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.applications", "application", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.in("userRole." + scope.getLowerCamelName() + ".id", resources)) //
                .add(Restrictions.eq("role.roleCategory", STUDENT)) //
                .add(Restrictions.eq("role.verified", true)) //
                .add(Restrictions.eq("shared", true)) //
                .add(Restrictions.ne("user.id", user.getId()));

        List<Integer> userIds = filter.getUserIds();
        if (isNotEmpty(userIds)) {
            criteria.add(Restrictions.in("user.id", userIds));
        }

        String valueString = filter.getValueString();
        if (valueString != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.like("user.fullName", valueString, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("user.email", valueString, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("qualificationAdvert.name", valueString, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("qualificationAdvert.summary", valueString, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("qualificationAdvert.description", valueString, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("employmentPositionAdvert.name", valueString, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("employmentPositionAdvert.summary", valueString, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("employmentPositionAdvert.description", valueString, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("userDocument.personalSummary", valueString, MatchMode.ANYWHERE)));
        }

        if (lastSequenceIdentifier != null) {
            criteria.add(Restrictions.lt("sequenceIdentifier", lastSequenceIdentifier));
        }

        return (List<ProfileListRowDTO>) criteria.addOrder(Order.desc("sequenceIdentifier"))
                .setMaxResults(PROFILE_LIST_PAGE_ROW_COUNT)
                .setResultTransformer(Transformers.aliasToBean(ProfileListRowDTO.class))
                .list();
    }

    public List<User> getUsersWithRoles(Resource resource, PrismRole... roles) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("role.id", roles)) //
                .list();
    }

    public List<Integer> getUsersWithRoles(PrismScope scope, List<Integer> resources, PrismRole... roles) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .add(Restrictions.in(scope.getLowerCamelName() + ".id", resources)) //
                .add(Restrictions.in("role.id", roles)) //
                .list();
    }

    public List<Integer> getUsersWithRoles(PrismScope scope, PrismScope parentScope, List<Integer> resources, PrismRole... roles) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .add(Restrictions.in("resource." + parentScope.getLowerCamelName() + ".id", resources)) //
                .add(Restrictions.in("role.id", roles)) //
                .list();
    }

    public List<Integer> getUsersForActivityNotification(PrismScope scope, DateTime baseline) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole." + scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.eq("userNotification.notificationDefinition.id", SYSTEM_ACTIVITY_NOTIFICATION)) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("role.verified", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userNotification.id")) //
                        .add(Restrictions.lt("userNotification.notifiedTimestamp", baseline))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNull("lastLoggedInTimestamp")) //
                                .add(Restrictions.lt("userRole.assignedTimestamp", baseline)))
                        .add(Restrictions.lt("lastLoggedInTimestamp", baseline)))
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .add(Restrictions.eq("userAccount.sendActivityNotification", true));

        if (contains(advertScopes, scope)) {
            criteria.add(Restrictions.ne("resourceState.state.id", PrismState.valueOf(scope.name() + "_UNSUBMITTED")));
        }

        return (List<Integer>) criteria.list();
    }

    public List<Integer> getUsersForReminderNotification(PrismScope scope, DateTime baseline) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole." + scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("userNotifications", "userNotification", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("userNotification.notificationDefinition.id", SYSTEM_REMINDER_NOTIFICATION)) //
                .createAlias("userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("role.verified", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userNotification.id")) //
                        .add(Restrictions.lt("userNotification.notifiedTimestamp", baseline))) //
                .add(Restrictions.lt("userRole.assignedTimestamp", baseline)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userAccount.id")) //
                        .add(Restrictions.ne("userAccount.enabled", true)));

        if (contains(advertScopes, scope)) {
            criteria.add(Restrictions.ne("resourceState.state.id", PrismState.valueOf(scope.name() + "_UNSUBMITTED")));
        }

        return (List<Integer>) criteria.list();
    }

    public DateTime getUserCreatedTimestamp(User user) {
        return (DateTime) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.property("acceptedTimestamp")) //
                .add(Restrictions.eq("user", user)) //
                .addOrder(Order.asc("acceptedTimestamp")) //
                .setMaxResults(1)
                .uniqueResult();
    }

    public List<Integer> getUsersWithActivitiesToCache(PrismScope scope, Collection<Integer> resources, PrismScope roleScope) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN);

        if (scope.equals(roleScope)) {
            criteria.createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN);
        } else {
            criteria.createAlias("resource." + roleScope.getLowerCamelName(), "roleResource", JoinType.INNER_JOIN) //
                    .createAlias("roleResource.userRoles", "userRole", JoinType.INNER_JOIN);
        }

        return criteria.createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(getUsersWithActivitiesToCacheConstraint(scope, resources)) //
                .list();
    }

    public List<Integer> getUsersWithActivitiesToCache(PrismScope scope, Collection<Integer> resources, PrismScope targeterScope, PrismScope targetScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("user.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert." + targeterScope.getLowerCamelName(), "targeterResource", JoinType.INNER_JOIN) //
                .createAlias("targeterResource.advert", "targeterAdvert", JoinType.INNER_JOIN) //
                .createAlias("targeterAdvert.targets", "target", JoinType.INNER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource", JoinType.INNER_JOIN) //
                .createAlias("targetResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(getUsersWithActivitiesToCacheConstraint(scope, resources)) //
                .add(Subqueries.propertyIn("userRole.role.id", //
                        DetachedCriteria.forClass(StateActionAssignment.class) //
                                .setProjection(Projections.groupProperty("role.id")) //
                                .createAlias("role", "role", JoinType.INNER_JOIN) //
                                .add(Restrictions.eq("role.scope.id", targetScope)) //
                                .add(Restrictions.eq("externalMode", true))))
                .list();
    }

    public Long getUserUnreadMessageCount(Collection<Integer> userIds, User currentUser) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.countDistinct("message.id")) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userAccount.threads", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.participants", "participant", JoinType.INNER_JOIN) //
                .createAlias("thread.messages", "message", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", userIds))
                .add(Restrictions.eq("participant.user", currentUser)) //
                .add(getVisibleMessageConstraint("message")) //
                .add(getUnreadMessageConstraint()) //
                .uniqueResult();
    }

    public Long getUserReadMessageCounts(User currentUser) {
        return getUserMessageCounts(currentUser, true);
    }

    public Long getUserUnreadMessageCounts(User currentUser) {
        return getUserMessageCounts(currentUser, false);
    }

    public List<ActivityMessageCountDTO> getUserReadMessageCounts(Collection<Integer> userIds, User currentUser) {
        return getUserMessageCounts(userIds, currentUser, true);
    }

    public List<ActivityMessageCountDTO> getUserUnreadMessageCounts(Collection<Integer> userIds, User currentUser) {
        return getUserMessageCounts(userIds, currentUser, false);
    }

    public List<Integer> getUsersWithUnreadMessages(User user) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userAccount.threads", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.participants", "participant", JoinType.INNER_JOIN) //
                .createAlias("thread.messages", "message", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("participant.user", user)) //
                .add(getVisibleMessageConstraint("message")) //
                .add(getUnreadMessageConstraint()) //
                .list();
    }

    public Integer getMaximumUserAccountCompleteScore() {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(UserAccount.class) //
                .setProjection(Projections.property("completeScore")) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.desc("completeScore")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<Integer> getUserAccounts() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserAccount.class) //
                .setProjection(Projections.property("id")) //
                .list();
    }

    public List<EntityLocationDTO> getUserLocations(Collection<Integer> userIds) {
        return (List<EntityLocationDTO>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("id")) //
                        .add(Projections.groupProperty("locationPart.name").as("location"))) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userAccount.address", "address", JoinType.INNER_JOIN) //
                .createAlias("address.currentAddress", "currentAddress", JoinType.INNER_JOIN) //
                .createAlias("currentAddress.locations", "location", JoinType.INNER_JOIN) //
                .createAlias("location.locationPart", "locationPart", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", userIds)) //
                .addOrder(Order.asc("id")) //
                .addOrder(Order.asc("location.id")) //
                .setResultTransformer(Transformers.aliasToBean(EntityLocationDTO.class)) //
                .list();
    }

    public List<UserOrganizationDTO> getUserOrganizations(Collection<Integer> userIds, PrismScope resourceScope, Collection<Integer> resourceIds,
            PrismRoleCategory roleCategory) {
        String resourcePrefix = resourceScope.getLowerCamelName();
        ProjectionList projections = Projections.projectionList() //
                .add(Projections.groupProperty("user.id").as("userId")) //
                .add(Projections.groupProperty(resourcePrefix + ".id").as(resourcePrefix + "Id")) //
                .add(Projections.property(resourcePrefix + ".name").as(resourcePrefix + "Name"));

        boolean departmentScope = resourceScope.equals(DEPARTMENT);
        if (departmentScope) {
            projections.add(Projections.property("departmentInstitution.id").as("institutionId")) //
                    .add(Projections.property("departmentInstitution.name").as("institutionName")) //
                    .add(Projections.property("departmentInstitution.logoImage.id").as("institutionLogoImageId"));
        } else {
            projections.add(Projections.property(resourcePrefix + ".logoImage.id").as(resourcePrefix + "LogoImageId"));
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(projections.add( //
                        Projections.property("acceptedTimestamp").as("acceptedTimestamp"))) //
                .createAlias(resourcePrefix, resourcePrefix, JoinType.INNER_JOIN) //
                .createAlias(resourcePrefix + ".resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias(resourcePrefix + ".advert", "advert", INNER_JOIN) //
                .createAlias("advert.address", "address", JoinType.INNER_JOIN);

        if (departmentScope) {
            criteria.createAlias(resourcePrefix + ".institution", "departmentInstitution", JoinType.INNER_JOIN);
        }

        criteria.createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.in("user.id", userIds));

        if (isNotEmpty(resourceIds)) {
            criteria.add(Restrictions.in(resourcePrefix + ".id", resourceIds));
        }

        return (List<UserOrganizationDTO>) criteria.add(Restrictions.eq("role.roleCategory", roleCategory)) //
                .add(Restrictions.in("resourceState.state.id", values(PrismState.class, resourceScope, "APPROVED", "DISABLED_COMPLETED"))) //
                .addOrder(Order.desc("acceptedTimestamp")) //
                .setResultTransformer(Transformers.aliasToBean(UserOrganizationDTO.class)) //
                .list();
    }

    private void appendAdministratorConditions(Criteria criteria, HashMultimap<PrismScope, Integer> enclosedResources) {
        Junction resourceConstraint = Restrictions.disjunction();
        enclosedResources.keySet().forEach( //
                enclosedScope -> resourceConstraint.add(Restrictions.in(enclosedScope.getLowerCamelName() + ".id", enclosedResources.get(enclosedScope))));
        criteria.add(resourceConstraint);
    }

    private Junction getUserAccountUnverifiedConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("user.userAccount")) //
                .add(Restrictions.eq("userAccount.enabled", false)) //
                .add(Restrictions.isNotNull("user.emailBouncedMessage"));
    }

    private Junction getUsersWithActionsConstraint(Resource resource, PrismAction... actions) {
        return Restrictions.conjunction() //
                .add(Restrictions.eq("resource.id", resource.getId())) //
                .add(Restrictions.in("stateAction.action.id", actions)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("user.userAccount")) //
                        .add(Restrictions.eq("userAccount.enabled", true)));
    }

    private Junction getUsersWithActivitiesToCacheConstraint(PrismScope scope, Collection<Integer> resources) {
        Junction constraint = Restrictions.conjunction() //
                .add(Restrictions.isNotNull("user.userAccount")) //
                .add(Restrictions.in("resource.id", resources));

        if (asList(OPPORTUNITY, ORGANIZATION).contains(scope.getScopeCategory())) {
            constraint.add(Restrictions.ne("state.id", PrismState.valueOf(scope.name() + "_UNSUBMITTED")));
        }

        return constraint.add(Restrictions.disjunction() //
                .add(Restrictions.isNull("resource.activityCachedTimestamp")) //
                .add(Restrictions.isNull("userAccount.activityCachedTimestamp")) //
                .add(Restrictions.ltProperty("userAccount.activityCachedTimestamp", "resource.activityCachedTimestamp")));
    }

    private Long getUserMessageCounts(User currentUser, boolean read) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(MessageThread.class) //
                .setProjection(Projections.countDistinct("message.id").as("id")) //
                .createAlias("messages", "message", JoinType.INNER_JOIN) //
                .createAlias("participants", "participant", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("participant.user", currentUser)) //
                .add(getVisibleMessageConstraint("message"))
                .add(getReadOrUnreadMessageConstraint(read)) //
                .uniqueResult();
    }

    private List<ActivityMessageCountDTO> getUserMessageCounts(Collection<Integer> userIds, User currentUser, boolean read) {
        return (List<ActivityMessageCountDTO>) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("id")) //
                        .add(Projections.countDistinct("message.id").as("messageCount"))) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userAccount.threads", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.participants", "participant", JoinType.INNER_JOIN) //
                .createAlias("thread.messages", "message") //
                .add(Restrictions.in("id", userIds)) //
                .add(Restrictions.eq("participant.user", currentUser)) //
                .add(getVisibleMessageConstraint("message"))
                .add(getReadOrUnreadMessageConstraint(read)) //
                .setResultTransformer(aliasToBean(ActivityMessageCountDTO.class)) //
                .list();
    }

}
