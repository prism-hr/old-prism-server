package uk.co.alumeni.prism.dao;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.ArrayUtils.contains;
import static uk.co.alumeni.prism.PrismConstants.PROFILE_LIST_PAGE_ROW_COUNT;
import static uk.co.alumeni.prism.PrismConstants.RESOURCE_LIST_PAGE_ROW_COUNT;
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_ACTIVITY_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_REMINDER_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_INTERVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_POTENTIAL_SUPERVISOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationAdvertRelationSection;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismOauthProvider;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserAdvertRelationSection;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.dto.ProfileListRowDTO;
import uk.co.alumeni.prism.dto.UnverifiedUserDTO;
import uk.co.alumeni.prism.dto.UserCompetenceDTO;
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

    public List<UserSelectionDTO> getUsersInterestedInApplication(Application application) {
        return (List<UserSelectionDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("user"), "user") //
                        .add(Projections.max("createdTimestamp"), "eventTimestamp")) //
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
                        .add(Projections.max("createdTimestamp"), "eventTimestamp")) //
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
                        .add(Projections.property("firstName"), "firstName") //
                        .add(Projections.property("lastName"), "lastName") //
                        .add(Projections.groupProperty("email"), "email") //
                        .add(Projections.property("userAccount.linkedinImageUrl"), "accountImageUrl") //
                        .add(Projections.property("userAccount.linkedinProfileUrl"), "accountProfileUrl")) //
                .createAlias("userRoles", "userRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("userRole.id")) //
                        .add(Restrictions.ne("userRole.role.id", PrismRole.APPLICATION_CREATOR))) //
                .add(WorkflowDAO.getSimilarUserConstraint(searchTerm)) //
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
            criteria.add(WorkflowDAO.getSimilarUserConstraint("user", searchTerm)); //
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
            Resource resource,
            PrismAction... actions) {
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
                .add(Projections.groupProperty("role.id").as("roleId"));

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projections) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".userRoles", "userRole", JoinType.INNER_JOIN);

        if (isDepartment) {
            criteria.createAlias(resourceReference + ".institution", "institution", JoinType.INNER_JOIN);
        }

        criteria.createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN);

        criteria.add(WorkflowDAO.getResourceParentManageableStateConstraint(resourceScope));
        if (isNotEmpty(resources)) {
            criteria.add(Restrictions.in(resourceReference + ".id", resources));
        }

        return (List<UnverifiedUserDTO>) criteria //
                .add(Restrictions.eq("role.verified", false)) //
                .setResultTransformer(Transformers.aliasToBean(UnverifiedUserDTO.class)) //
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

    public List<ProfileListRowDTO> getUserProfiles(PrismScope scope, Collection<Integer> resources, ProfileListFilterDTO filter) {
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
                        Restrictions.eq("externalAccount.accountType", PrismOauthProvider.LINKEDIN)) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userRoles", "userRole", JoinType.INNER_JOIN,
                        Restrictions.eq("userRole.role.id", PrismRole.DEPARTMENT_STUDENT)) //
                .createAlias("qualifications", "qualification", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("qualification.advert", "qualificationAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("employmentPositions", "employmentPosition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("employmentPosition.advert", "employmentPositionAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userDocument", "userDocument", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.applications", "application", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.in("userRole." + scope.getLowerCamelName() + ".id", resources)) //
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

    public <T extends UserAdvertRelationSection, U extends ApplicationAdvertRelationSection> void deleteUserProfileSection(
            Class<T> userProfileSectionClass, Class<U> applicationSectionClass, Integer propertyId) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete " + userProfileSectionClass.getSimpleName() + " " //
                        + "where " + applicationSectionClass.getSimpleName().toLowerCase() + " = :propertyId") //
                .setParameter("propertyId", propertyId) //
                .executeUpdate();
    }

        HashMultimap<PrismScope, PrismRole> prismRolesByScope = HashMultimap.create();
        stream(prismRoles).forEach(prismRole -> prismRolesByScope.put(prismRole.getScope(), prismRole));

        Junction userRoleConstraint = Restrictions.disjunction();
        prismRolesByScope.keySet().forEach(prismScope -> {
            Resource enclosingResource = resource.getEnclosingResource(prismScope);
            if (enclosingResource != null) {
                userRoleConstraint.add(Restrictions.conjunction() //
                        .add(Restrictions.eq(prismScope.getLowerCamelName(), enclosingResource)) //
                        .add(Restrictions.in("role.id", prismRolesByScope.get(prismScope))));
            }
        });

                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(userRoleConstraint) //
                .addOrder(Order.asc("user.fullName")) //

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

}
