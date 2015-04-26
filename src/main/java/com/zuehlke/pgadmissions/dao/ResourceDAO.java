package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.utils.PrismConstants.SEQUENCE_IDENTIFIER;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterSortOrder;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.program.ResourceStudyLocation;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.dto.UserAdministratorResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;

@Repository
@SuppressWarnings("unchecked")
public class ResourceDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Integer> getResourcesToEscalate(PrismScope resourceScope, PrismAction actionId, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.lt("dueDate", baseline)) //
                .list();
    }

    public List<Integer> getResourcesToPropagate(PrismScope propagatingScope, Integer propagatingId, PrismScope propagatedScope, PrismAction actionId) {
        String propagatedAlias = propagatedScope.getLowerCamelName();
        String propagatedReference = propagatingScope.ordinal() > propagatedScope.ordinal() ? propagatedAlias : propagatedAlias + "s";

        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(propagatingScope.getResourceClass()) //
                .setProjection(Projections.property(propagatedAlias + ".id")) //
                .createAlias(propagatedReference, propagatedAlias, JoinType.INNER_JOIN) //
                .createAlias(propagatedAlias + ".state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", propagatingId)) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .list();
    }

    public List<Integer> getResourcesRequiringIndividualReminders(PrismScope resourceScope, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("lastRemindedRequestIndividual")) //
                        .add(Restrictions.lt("lastRemindedRequestIndividual", baseline))) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .list();
    }

    public List<Integer> getResourcesRequiringSyndicatedReminders(PrismScope resourceScope, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("lastRemindedRequestSyndicated")) //
                        .add(Restrictions.lt("lastRemindedRequestSyndicated", baseline))) //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .list();
    }

    public List<Integer> getResourceRequiringSyndicatedUpdates(PrismScope resourceScope, LocalDate baseline, DateTime rangeStart, DateTime rangeClose) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("lastNotifiedUpdateSyndicated")) //
                        .add(Restrictions.lt("lastNotifiedUpdateSyndicated", baseline))) //
                .add(Restrictions.between("updatedTimestamp", rangeStart, rangeClose)) //
                .list();
    }

    public void deleteResourceState(Resource resource, State state) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourceState " //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and state = :state") //
                .setParameter("resource", resource) //
                .setParameter("state", state) //
                .executeUpdate();
    }

    public void deleteSecondaryResourceState(Resource resource, State state) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourceState " //
                        + "where " + resource.getResourceScope().getLowerCamelName() + " = :resource " //
                        + "and state = :state " + "and primaryState is false") //
                .setParameter("resource", resource) //
                .setParameter("state", state) //
                .executeUpdate();
    }

    public List<ResourceListRowDTO> getResourceConsoleList(User user, PrismScope scopeId, List<PrismScope> parentScopeIds,
            Set<Integer> assignedResources, ResourceListFilterDTO filter, String lastSequenceIdentifier, Integer maxRecords, boolean hasRedactions) {
        if (assignedResources.isEmpty()) {
            return new ArrayList<ResourceListRowDTO>(0);
        }

        Class<? extends Resource> resourceClass = scopeId.getResourceClass();
        String resourceReference = scopeId.getLowerCamelName();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(resourceClass, resourceReference);

        ProjectionList projectionList = Projections.projectionList();

        for (PrismScope parentScopeId : parentScopeIds) {
            String parentScopeName = parentScopeId.getLowerCamelName();
            projectionList.add(Projections.property(parentScopeName + ".id"), parentScopeName + "Id");
        }

        projectionList.add(Projections.property("id"), scopeId.getLowerCamelName() + "Id") //
                .add(Projections.property("user.id"), "creatorId") //
                .add(Projections.property("user.firstName"), "creatorFirstName") //
                .add(Projections.property("user.firstName2"), "creatorFirstName2") //
                .add(Projections.property("user.firstName3"), "creatorFirstName3") //
                .add(Projections.property("user.lastName"), "creatorLastName") //
                .add(Projections.property("user.email"), "creatorEmail") //
                .add(Projections.property("primaryExternalAccount.accountImageUrl"), "creatorAccountImageUrl")
                .add(Projections.property("externalAccount.accountProfileUrl"), "creatorLinkedinProfileUrl")
                .add(Projections.property("code"), "code");

        addResourceListCustomColumns(scopeId, projectionList);

        if (!hasRedactions) {
            projectionList.add(Projections.property("applicationRatingAverage"), "applicationRatingAverage");
        }

        projectionList.add(Projections.property("state.id"), "stateId") //
                .add(Projections.property("state.stateGroup.id"), "stateGroupId") //
                .add(Projections.property("user.email"), "creatorEmail") //
                .add(Projections.property("updatedTimestamp"), "updatedTimestamp") //
                .add(Projections.property("sequenceIdentifier"), "sequenceIdentifier"); //

        criteria.setProjection(projectionList) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN)
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN)
                .createAlias("userAccount.primaryExternalAccount", "primaryExternalAccount", JoinType.LEFT_OUTER_JOIN)
                .createAlias("userAccount.externalAccounts", "externalAccount", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("externalAccount.accountType", OauthProvider.LINKEDIN));

        addResourceListCustomJoins(scopeId, resourceReference, criteria);
        criteria.add(Restrictions.in("id", assignedResources));

        return appendResourceListLimitCriterion(criteria, filter, lastSequenceIdentifier, maxRecords)
                .setResultTransformer(Transformers.aliasToBean(ResourceListRowDTO.class)) //
                .list();
    }

    public List<Integer> getAssignedResources(User user, PrismScope scopeId, ResourceListFilterDTO filter, Junction conditions, String lastIdentifier,
            Integer recordsToRetrieve) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eqProperty("stateAction.state", "resourceState.state")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateAction.actionCondition")) //
                        .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"))) //
                .add(Restrictions.isNull("state.hidden"));

        appendResourceListFilterCriterion(criteria, conditions, filter);
        appendResourceListLimitCriterion(criteria, filter, lastIdentifier, recordsToRetrieve);

        return (List<Integer>) criteria.list();
    }

    public List<Integer> getAssignedResources(User user, PrismScope scopeId, PrismScope parentScopeId, ResourceListFilterDTO filter, Junction conditions,
            String lastIdentifier, Integer recordsToRetrieve) {
        String parentResourceReference = parentScopeId.getLowerCamelName();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(scopeId.getResourceClass()) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(parentResourceReference, parentResourceReference, JoinType.INNER_JOIN) //
                .createAlias(parentResourceReference + ".userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("stateAction.actionCondition")) //
                        .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"))) //
                .add(Restrictions.eqProperty("stateAction.state", "resourceState.state")) //
                .add(Restrictions.isNull("state.hidden"));

        appendResourceListFilterCriterion(criteria, conditions, filter);
        appendResourceListLimitCriterion(criteria, filter, lastIdentifier, recordsToRetrieve);

        return (List<Integer>) criteria.list();
    }

    public List<Integer> getResourcesByMatchingEnclosingResources(PrismScope parentResourceScope, String searchTerm) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(parentResourceScope.getResourceClass()) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("code", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.ilike("title", searchTerm, MatchMode.ANYWHERE))) //
                .list();
    }

    public List<Integer> getResourcesByMatchingUsersAndRole(PrismScope prismScope, String searchTerm, List<PrismRole> prismRoles) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.property(prismScope.getLowerCamelName() + ".id")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.ilike("user.fullName", searchTerm, MatchMode.ANYWHERE)) //
                        .add(Restrictions.ilike("user.email", searchTerm, MatchMode.ANYWHERE))) //
                .add(Restrictions.in("role.id", prismRoles)) //
                .list();
    }

    public List<UserAdministratorResourceDTO> getUserAdministratorResources(User user) {
        return (List<UserAdministratorResourceDTO>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("system"), "system") //
                        .add(Projections.groupProperty("institution"), "institution") //
                        .add(Projections.groupProperty("program"), "program") //
                        .add(Projections.groupProperty("project"), "project")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.roleTransitions", "roleTransition", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.isNull("application")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("roleTransition.roleTransitionType", CREATE)) //
                        .add(Restrictions.eq("roleTransition.restrictToActionOwner", false))) //
                .setResultTransformer(Transformers.aliasToBean(UserAdministratorResourceDTO.class)) //
                .list();
    }

    public void reassignResources(PrismScope prismScope, User oldUser, User newUser) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + prismScope.getUpperCamelName() + " " //
                        + "set user = :newUser " //
                        + "where user = :oldUser") //
                .setParameter("newUser", newUser) //
                .setParameter("oldUser", oldUser) //
                .executeUpdate();
    }

    public ResourceStudyOption getStudyOption(ResourceParent resource, StudyOption studyOption) {
        return (ResourceStudyOption) sessionFactory.getCurrentSession().createCriteria(ResourceStudyOption.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram())) //
                        .add(Restrictions.eq("institution", resource.getInstitution()))) //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .add(Restrictions.eq("studyOption", studyOption)) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public ResourceStudyOption getStudyOptionStrict(ResourceParent resource, StudyOption studyOption) {
        return (ResourceStudyOption) sessionFactory.getCurrentSession().createCriteria(ResourceStudyOption.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("studyOption", studyOption)) //
                .uniqueResult();
    }

    public List<ResourceStudyOption> getStudyOptions(ResourceParent resource) {
        return (List<ResourceStudyOption>) sessionFactory.getCurrentSession().createCriteria(ResourceStudyOption.class) //
                .createAlias("studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram())) //
                        .add(Restrictions.eq("institution", resource.getInstitution()))) //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .addOrder(Order.asc("studyOption.code")) //
                .list();
    }

    public List<ResourceStudyOption> getStudyOptionsStrict(ResourceParent resource) {
        return (List<ResourceStudyOption>) sessionFactory.getCurrentSession().createCriteria(ResourceStudyOption.class) //
                .createAlias("studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .addOrder(Order.asc("studyOption.code")) //
                .list();
    }

    public ResourceStudyOptionInstance getFirstStudyOptionInstanceStrict(ResourceParent resource, StudyOption studyOption) {
        return (ResourceStudyOptionInstance) sessionFactory.getCurrentSession().createCriteria(ResourceStudyOptionInstance.class) //
                .createAlias("studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("studyOption." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("studyOption.studyOption", studyOption)) //
                .addOrder(Order.asc("applicationStartDate")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<ResourceStudyLocation> getStudyLocations(ResourceParent resource) {
        return (List<ResourceStudyLocation>) sessionFactory.getCurrentSession().createCriteria(ResourceStudyLocation.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram())) //
                        .add(Restrictions.eq("institution", resource.getInstitution()))) //
                .addOrder(Order.desc("project")) //
                .addOrder(Order.desc("program")) //
                .addOrder(Order.desc("institution")) //
                .list();
    }

    public void deleteElapsedStudyOptions(LocalDate baseline) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourceStudyOption " //
                        + "where applicationCloseDate < :baseline") //
                .setParameter("baseline", baseline) //
                .executeUpdate();
    }

    public void deleteElapsedStudyOptionInstances(LocalDate baseline) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ResourceStudyOptionInstance " //
                        + "where applicationCloseDate < :baseline") //
                .setParameter("baseline", baseline) //
                .executeUpdate();
    }

    public List<ResourceState> getResourceStatesByStateGroup(Resource resource, PrismStateGroup stateGroup) {
        return (List<ResourceState>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state.stateGroup.id", stateGroup)) //
                .list();
    }

    private void addResourceListCustomColumns(PrismScope scopeId, ProjectionList projectionList) {
        HashMultimap<String, String> customColumns = scopeId.getConsoleListCustomColumns();
        for (String tableName : customColumns.keySet()) {
            boolean prefixColumnName = !tableName.equals(scopeId.getLowerCamelName());
            for (String columnName : customColumns.get(tableName)) {
                projectionList.add(Projections.property(prefixColumnName ? tableName + "." + columnName : columnName),
                        tableName + WordUtils.capitalize(columnName));
            }
        }
    }

    private void addResourceListCustomJoins(PrismScope scopeId, String resourceReference, Criteria criteria) {
        for (String tableName : scopeId.getConsoleListCustomColumns().keySet()) {
            if (!tableName.equals(resourceReference)) {
                criteria.createAlias(tableName, tableName, JoinType.LEFT_OUTER_JOIN); //
            }
        }
    }

    private static void appendResourceListFilterCriterion(Criteria criteria, Junction conditions, ResourceListFilterDTO filter) {
        if (filter.isUrgentOnly()) {
            criteria.add(Restrictions.eq("stateAction.raisesUrgentFlag", true));
        }

        if (conditions != null) {
            criteria.add(conditions);
        }
    }

    private static Criteria appendResourceListLimitCriterion(Criteria criteria, ResourceListFilterDTO filter, String lastSequenceIdentifier,
            Integer recordsToRetrieve) {
        PrismResourceListFilterSortOrder sortOrder = filter.getSortOrder();

        if (lastSequenceIdentifier != null) {
            criteria.add(PrismResourceListFilterSortOrder.getPagingRestriction(SEQUENCE_IDENTIFIER, sortOrder, lastSequenceIdentifier));
        }

        criteria.addOrder(PrismResourceListFilterSortOrder.getOrderExpression(SEQUENCE_IDENTIFIER, sortOrder));

        if (recordsToRetrieve != null) {
            criteria.setMaxResults(recordsToRetrieve);
        }

        return criteria;
    }

}
