package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_POTENTIAL_SUPERVISOR_GROUP;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.PrismConstants;

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

	public String getUserInstitutionId(User user, Institution institution, PrismUserIdentity identityType) {
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
		                .add(Projections.property("parentUser.id"), "id") //
		                .add(Projections.property("parentUser.firstName"), "firstName") //
		                .add(Projections.property("parentUser.lastName"), "lastName") //
		                .add(Projections.property("parentUser.email"), "email")) //
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
		                .add(Projections.groupProperty("parentUser.id"), "id") //
		                .add(Projections.property("parentUser.firstName"), "firstName") //
		                .add(Projections.property("parentUser.lastName"), "lastName") //
		                .add(Projections.property("parentUser.email"), "email") //
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
		                .add(Projections.groupProperty("parentUser.id"), "id") //
		                .add(Projections.property("parentUser.firstName"), "firstName") //
		                .add(Projections.property("parentUser.lastName"), "lastName") //
		                .add(Projections.property("parentUser.email"), "email") //
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
		                .add(Projections.groupProperty("parentUser.id"), "id") //
		                .add(Projections.property("parentUser.firstName"), "firstName") //
		                .add(Projections.property("parentUser.lastName"), "lastName") //
		                .add(Projections.property("parentUser.email"), "email")) //
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

	public List<UserRepresentation> getSimilarUsers(String searchTerm) {
		return (List<UserRepresentation>) sessionFactory.getCurrentSession().createCriteria(User.class) //
		        .setProjection(Projections.projectionList() //
		                .add(Projections.property("firstName"), "firstName") //
		                .add(Projections.property("lastName"), "lastName") //
		                .add(Projections.groupProperty("email"), "email")
		                .add(Projections.property("primaryExternalAccount.accountImageUrl"), "accountImageUrl")
		                .add(Projections.property("externalAccount.accountProfileUrl"), "linkedinProfileUrl")) //
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
		        .add(Restrictions.disjunction() //
		                .add(Restrictions.ilike("firstName", searchTerm, MatchMode.START)) //
		                .add(Restrictions.ilike("lastName", searchTerm, MatchMode.START)) //
		                .add(Restrictions.ilike("fullName", searchTerm, MatchMode.START)) //
		                .add(Restrictions.ilike("email", searchTerm, MatchMode.START))) //
		        .addOrder(Order.desc("lastName")) //
		        .addOrder(Order.desc("firstName")) //
		        .setMaxResults(10) //
		        .setResultTransformer(Transformers.aliasToBean(UserRepresentation.class)) //
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

	public List<Integer> getMatchingUsers(String searchTerm) {
		return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(User.class) //
		        .setProjection(Projections.property("id")) //
		        .add(Restrictions.disjunction() //
		                .add(Restrictions.ilike("fullName", searchTerm, MatchMode.ANYWHERE)) //
		                .add(Restrictions.ilike("email", searchTerm, MatchMode.ANYWHERE))) //
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

	public <T extends Resource> List<User> getBouncedOrUnverifiedUsers(HashMultimap<PrismScope, T> userAdministratorResources,
	        UserListFilterDTO userListFilterDTO) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
		        .setProjection(Projections.groupProperty("user")) //
		        .createAlias("user", "user", JoinType.INNER_JOIN) //
		        .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN); //

		Junction sentDisjunction = Restrictions.disjunction() //
		        .add(Restrictions.isNotNull("lastNotifiedDate"));
		Junction roleDisjunction = Restrictions.disjunction();
		for (PrismScope scope : userAdministratorResources.keySet()) {
			String scopeReference = scope.getLowerCamelName();
			sentDisjunction.add(Restrictions.isNotNull("user.lastNotifiedDate" + WordUtils.capitalize(scopeReference)));
			roleDisjunction.add(Restrictions.in(scopeReference, userAdministratorResources.get(scope)));
		}

		criteria.add(sentDisjunction) //
		        .add(roleDisjunction);

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
			criteria.add(Restrictions.disjunction() //
			        .add(Restrictions.ilike("user.firstName", searchTerm, MatchMode.ANYWHERE)) //
			        .add(Restrictions.ilike("user.lastName", searchTerm, MatchMode.ANYWHERE)) //
			        .add(Restrictions.ilike("user.fullName", searchTerm, MatchMode.ANYWHERE)) //
			        .add(Restrictions.ilike("user.email", searchTerm, MatchMode.ANYWHERE))); //
		}

		Integer lastUserId = userListFilterDTO.getLastUserId();
		if (lastUserId != null) {
			criteria.add(Restrictions.lt("user.id", lastUserId));
		}

		return (List<User>) criteria.addOrder(Order.desc("user.id")) //
		        .setMaxResults(PrismConstants.LIST_PAGE_ROW_COUNT) //
		        .list();
	}

	public <T extends Resource> User getBouncedOrUnverifiedUser(HashMultimap<PrismScope, T> userAdministratorResources, Integer userId) {
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

	public void reassignUsers(User oldUser, User newUser) {
		sessionFactory.getCurrentSession().createQuery(
		        "update User " //
		                + "set parentUser = :newUser " //
		                + "where parentUser = :oldUser") //
		        .setParameter("newUser", newUser) //
		        .setParameter("oldUser", oldUser) //
		        .executeUpdate();
	}

}
