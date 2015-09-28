package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.PrismConstants.ADVERT_LIST_PAGE_ROW_COUNT;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getEndorsementActionJoinResolution;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getEndorsementActionVisibilityResolution;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceStateActionConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext.EMPLOYER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext.UNIVERSITY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.getOpportunityTypes;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertAttribute;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFunction;
import com.zuehlke.pgadmissions.domain.advert.AdvertIndustry;
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.AdvertActionConditionDTO;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.dto.AdvertStudyOptionDTO;
import com.zuehlke.pgadmissions.dto.AdvertTargetDTO;
import com.zuehlke.pgadmissions.dto.EntityOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;

@Repository
@SuppressWarnings("unchecked")
public class AdvertDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Advert getAdvert(PrismScope resourceScope, Integer resourceId) {
        return (Advert) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass())
                .setProjection(Projections.property("advert"))
                .add(Restrictions.eq("id", resourceId))
                .uniqueResult();
    }

    public List<AdvertDTO> getAdverts(OpportunitiesQueryDTO query, Collection<Integer> adverts) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.property("user.firstName").as("userFirstName")) //
                        .add(Projections.property("user.lastName").as("userLastName")) //
                        .add(Projections.property("userAccount.linkedinProfileUrl").as("userAccountProfileUrl")) //
                        .add(Projections.property("userAccount.linkedinImageUrl").as("userAccountImageUrl")) //
                        .add(Projections.property("institution.id").as("institutionId")) //
                        .add(Projections.property("institution.name").as("institutionName")) //
                        .add(Projections.property("institution.logoImage.id").as("logoImageId")) //
                        .add(Projections.property("department.id").as("departmentId")) //
                        .add(Projections.property("department.name").as("departmentName")) //
                        .add(Projections.property("program.id").as("programId")) //
                        .add(Projections.property("program.name").as("programName")) //
                        .add(Projections.property("program.availableDate").as("programAvailableDate")) //
                        .add(Projections.property("program.durationMinimum").as("programDurationMinimum")) //
                        .add(Projections.property("program.durationMaximum").as("programDurationMaximum")) //
                        .add(Projections.property("project.id").as("projectId")) //
                        .add(Projections.property("project.name").as("projectName")) //
                        .add(Projections.property("project.availableDate").as("projectAvailableDate")) //
                        .add(Projections.property("project.durationMinimum").as("projectDurationMinimum")) //
                        .add(Projections.property("project.durationMaximum").as("projectDurationMaximum")) //
                        .add(Projections.property("opportunityType.id").as("opportunityType")) //
                        .add(Projections.property("name").as("name")) //
                        .add(Projections.property("summary").as("summary")) //
                        .add(Projections.property("description").as("description")) //
                        .add(Projections.property("globallyVisible").as("globallyVisible")) //
                        .add(Projections.property("homepage").as("homepage")) //
                        .add(Projections.property("applyHomepage").as("applyHomepage")) //
                        .add(Projections.property("telephone").as("telephone")) //
                        .add(Projections.property("address.addressLine1").as("addressLine1")) //
                        .add(Projections.property("address.addressLine2").as("addressLine2")) //
                        .add(Projections.property("address.addressTown").as("addressTown")) //
                        .add(Projections.property("address.addressRegion").as("addressRegion")) //
                        .add(Projections.property("address.addressCode").as("addressCode")) //
                        .add(Projections.property("address.domicile.id").as("addressDomicileId")) //
                        .add(Projections.property("address.googleId").as("addressGoogleId")) //
                        .add(Projections.property("address.addressCoordinates.latitude").as("addressCoordinateLatitude")) //
                        .add(Projections.property("address.addressCoordinates.longitude").as("addressCoordinateLongitude")) //
                        .add(Projections.property("pay.currencyAtLocale").as("payCurrency")) //
                        .add(Projections.property("pay.interval").as("payInterval")) //
                        .add(Projections.property("pay.monthMinimumAtLocale").as("payMonthMinimum")) //
                        .add(Projections.property("pay.monthMaximumAtLocale").as("payMonthMaximum")) //
                        .add(Projections.property("pay.yearMinimumAtLocale").as("payYearMinimum")) //
                        .add(Projections.property("pay.yearMaximumAtLocale").as("payYearMaximum")) //
                        .add(Projections.property("closingDate.closingDate").as("closingDate")) //
                        .add(Projections.property("sequenceIdentifier").as("sequenceIdentifier"))) //
                .createAlias("user", "user", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("institution", "institution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("opportunityType", "opportunityType", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("closingDate", "closingDate", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.in("id", adverts));

        String lastSequenceIdentifier = query.getLastSequenceIdentifier();
        if (lastSequenceIdentifier != null) {
            criteria.add(Restrictions.lt("sequenceIdentifier", lastSequenceIdentifier));
        }

        return (List<AdvertDTO>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
                .setMaxResults(ADVERT_LIST_PAGE_ROW_COUNT) //
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class))
                .list();
    }

    public List<EntityOpportunityCategoryDTO> getVisibleAdverts(PrismScope scope, Collection<PrismState> activeStates, PrismActionCondition actionCondition,
            OpportunitiesQueryDTO query, User currentUser, HashMultimap<PrismScope, Integer> userRoleResources) {
        String resourceReference = scope.getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("advert.id").as("id")) //
                        .add(Projections.property(resourceReference + ".opportunityCategories").as("opportunityCategories"))) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.categories.industries", "industry", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.categories.functions", "function", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.INNER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceCondition.externalMode", true)) //
                                .add(Restrictions.eq("resourceCondition.actionCondition", actionCondition))); //

        boolean userLoggedIn = currentUser != null;
        ResourceDTO resource = query.getResource();
        boolean narrowedByResource = resource != null;

        Set<PrismScope> roleScopes = Sets.newHashSet(userRoleResources.keySet());
        roleScopes.add(scope);
        if (userLoggedIn || narrowedByResource) {
            for (PrismScope roleScope : roleScopes) {
                if(roleScope == APPLICATION) {
                    continue;
                }
                String scopeReferenceLower = roleScope.getLowerCamelName();

                String advertScopeReference = "advert" + roleScope.getUpperCamelName();
                String advertScopeAdvertReference = advertScopeReference + "Advert";
                String advertScopeAdvertTargetReference = advertScopeAdvertReference + "Target";

                Set<Integer> resources = userRoleResources.get(scope);
                if (CollectionUtils.isEmpty(resources)) {
                    criteria.createAlias("advert." + scopeReferenceLower, advertScopeReference, JoinType.LEFT_OUTER_JOIN);
                } else {
                    criteria.createAlias("advert." + scopeReferenceLower, advertScopeReference, JoinType.LEFT_OUTER_JOIN,
                            Restrictions.in(advertScopeReference + ".id", userRoleResources.get(roleScope)));
                }

                criteria.createAlias(advertScopeReference + ".advert", advertScopeAdvertReference, JoinType.LEFT_OUTER_JOIN)
                        .createAlias(advertScopeAdvertReference + ".targets", advertScopeAdvertTargetReference, JoinType.LEFT_OUTER_JOIN);
            }
        }

        Class<? extends Resource> resourceClass = scope.getResourceClass();
        boolean opportunityRequest = ResourceOpportunity.class.isAssignableFrom(resourceClass);
        if (opportunityRequest) {
            criteria.createAlias(resourceReference + ".opportunityType", "opportunityType", JoinType.INNER_JOIN) //
                    .createAlias(resourceReference + ".resourceStudyOptions", "resourceStudyOption", JoinType.INNER_JOIN);
        }

        criteria.add(Restrictions.in("state.id", activeStates));

        appendContextConstraint(criteria, query);
        appendVisibilityConstraint(criteria, scope, opportunityRequest, userLoggedIn, roleScopes);

        appendLocationConstraint(criteria, query);
        appendKeywordConstraint(query, criteria);

        appendIndustryConstraint(criteria, query);
        appendFunctionConstraint(criteria, query);

        appendOpportunityTypeConstraint(criteria, scope, query);
        if (opportunityRequest) {
            appendStudyOptionConstraint(query, criteria);
        }

        appendPayConstraint(criteria, query);

        if (opportunityRequest) {
            appendDurationConstraint(criteria, resourceReference, query);
        }

        if (narrowedByResource) {
            appendResourceConstraint(criteria, resource);
        }

        return criteria.setResultTransformer(Transformers.aliasToBean(EntityOpportunityCategoryDTO.class))
                .list();
    }

    public List<AdvertActionConditionDTO> getAdvertActionConditions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        String resourceReference = resourceScope.getLowerCamelName();
        return (List<AdvertActionConditionDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.groupProperty("resourceCondition.actionCondition").as("actionCondition")) //
                        .add(Projections.property("resourceCondition.internalMode").as("internalMode")) //
                        .add(Projections.property("resourceCondition.externalMode").as("externalMode"))) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .add(Restrictions.in(resourceReference + ".id", resourceIds)) //
                .setResultTransformer(Transformers.aliasToBean(AdvertActionConditionDTO.class)) //
                .list();
    }

    public List<AdvertStudyOptionDTO> getAdvertStudyOptions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        String resourceReference = resourceScope.getLowerCamelName();
        return (List<AdvertStudyOptionDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.groupProperty("resourceStudyOption.studyOption").as("studyOption"))) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceStudyOptions", "resourceStudyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.in(resourceReference + ".id", resourceIds)) //
                .setResultTransformer(Transformers.aliasToBean(AdvertStudyOptionDTO.class)) //
                .list();
    }

    public AdvertClosingDate getNextAdvertClosingDate(Advert advert, LocalDate baseline) {
        return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(AdvertClosingDate.class) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.ge("closingDate", baseline)) //
                .addOrder(Order.asc("closingDate")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<Integer> getAdvertsWithElapsedCurrencyConversions(LocalDate baseline, HashMultimap<PrismScope, PrismState> scopes) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.resourceStates", "programState", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.resourceConditions", "programCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.resourceStates", "projectState", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.resourceConditions", "projectCondition", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("program.id")) //
                                .add(Restrictions.isNotNull("programCondition.id")) //
                                .add(Restrictions.in("programState.state.id", scopes.get(PROGRAM)))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("project.id")) //
                                .add(Restrictions.isNotNull("projectCondition.id")) //
                                .add(Restrictions.in("projectState.state.id", scopes.get(PROJECT))))) //
                .add(Restrictions.lt("lastCurrencyConversionDate", baseline)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.isNotNull("pay.currencySpecified")) //
                        .add(Restrictions.isNotNull("pay.currencyAtLocale")) //
                        .add(Restrictions.neProperty("pay.currencySpecified", "pay.currencyAtLocale")))
                .list();
    }

    public List<Integer> getAdvertsWithElapsedClosingDates(LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("closingDate", "closingDate", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("closingDates", "otherClosingDate", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.lt("closingDate.closingDate", baseline)) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNull("closingDate.id")) //
                                .add(Restrictions.ge("otherClosingDate.closingDate", baseline)))) //
                .list();
    }

    public List<Advert> getAdvertsWithFinancialDetails(Institution institution) {
        String currency = institution.getCurrency();
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .createAlias("project", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("institution", "institution", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("project.institution", institution)) //
                        .add(Restrictions.eq("program.institution", institution)) //
                        .add(Restrictions.eq("institution.id", institution.getId()))) //
                .add(Restrictions.eq("pay.currencySpecified", currency)) //
                .list();
    }

    public List<PrismAdvertIndustry> getAdvertIndustries(Advert advert) {
        return (List<PrismAdvertIndustry>) sessionFactory.getCurrentSession().createCriteria(AdvertIndustry.class) //
                .setProjection(Projections.property("industry")) //
                .add(Restrictions.eq("advert", advert)) //
                .addOrder(Order.asc("industry")) //
                .list();
    }

    public List<PrismAdvertFunction> getAdvertFunctions(Advert advert) {
        return (List<PrismAdvertFunction>) sessionFactory.getCurrentSession().createCriteria(AdvertFunction.class) //
                .setProjection(Projections.property("function")) //
                .add(Restrictions.eq("advert", advert)) //
                .addOrder(Order.asc("function")) //
                .list();
    }

    public List<Competence> searchCompetences(String searchTerm) {
        return sessionFactory.getCurrentSession().createCriteria(Competence.class)
                .add(Restrictions.like("name", searchTerm, MatchMode.ANYWHERE))
                .list();
    }

    public void deleteAdvertAttributes(Advert advert, Class<? extends AdvertAttribute> attributeClass) {
        sessionFactory.getCurrentSession().createQuery(
                "delete " + attributeClass.getSimpleName() + " "
                        + "where advert = :advert")
                .setParameter("advert", advert) //
                .executeUpdate();
    }

    public List<AdvertTargetDTO> getAdvertTargets(Advert advert, String connectionContext) {
        return (List<AdvertTargetDTO>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property(connectionContext + "User.id").as("userId")) //
                        .add(Projections.property(connectionContext + "User.firstName").as("userFirstName")) //
                        .add(Projections.property(connectionContext + "User.firstName2").as("userFirstName2")) //
                        .add(Projections.property(connectionContext + "User.firstName3").as("userFirstName3")) //
                        .add(Projections.property(connectionContext + "User.lastName").as("userLastName")) //
                        .add(Projections.property(connectionContext + "User.email").as("userEmail")) //
                        .add(Projections.property(connectionContext + "UserAccount.linkedinProfileUrl").as("userAccountProfileUrl")) //
                        .add(Projections.property(connectionContext + "UserAccount.linkedinImageUrl").as("userAccountImageUrl")) //
                        .add(Projections.property(connectionContext + "UserAccount.portraitImage.id").as("userPortraitImageId")) //
                        .add(Projections.property(connectionContext + "Advert.id").as("advertId")) //
                        .add(Projections.property(connectionContext + "Institution.id").as("institutionId")) //
                        .add(Projections.property(connectionContext + "Institution.name").as("institutionName")) //
                        .add(Projections.property(connectionContext + "Institution.logoImage.id").as("logoImageId")) //
                        .add(Projections.property(connectionContext + "Department.id").as("departmentId")) //
                        .add(Projections.property(connectionContext + "Department.name").as("departmentName")) //
                        .add(Projections.property("accepted").as("accepted"))) //
                .createAlias(connectionContext + "User", connectionContext + "User", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(connectionContext + "User.userAccount", connectionContext + "UserAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(connectionContext + "Advert", connectionContext + "Advert", JoinType.INNER_JOIN) //
                .createAlias(connectionContext + "Advert.institution", connectionContext + "AdvertInstitution", JoinType.INNER_JOIN) //
                .createAlias(connectionContext + "Advert.department", connectionContext + "AdvertDepartment", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq(connectionContext + "Advert.institution", advert.getInstitution()))
                                .add(Restrictions.isNull(connectionContext + "Advert.department"))) //
                        .add(Restrictions.eq(connectionContext + "Advert.department", advert.getDepartment()))) //
                .setResultTransformer(Transformers.aliasToBean(AdvertTargetDTO.class)) //
                .list();
    }

    public List<Advert> getAdvertsForWhichUserCanTarget(PrismScope scope, User user, PrismOpportunityCategory[] opportunityCategories, List<PrismState> activeStates) {
        Junction categoriesConstraint = Restrictions.disjunction();
        for (PrismOpportunityCategory opportunityCategory : opportunityCategories) {
            categoriesConstraint.add(Restrictions.like("opportunityCategories", opportunityCategory.name(), MatchMode.ANYWHERE));
        }

        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("resource.advert")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", activeStates)) //
                .add(categoriesConstraint) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("role.verified", true)) //
                .list();
    }

    public List<Advert> getAdvertsTargetsUserCanEndorseFor(Advert advert, User user, PrismScope scope, PrismScope partnerScope) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class)
                .setProjection(Projections.groupProperty("target.targetAdvert")) //
                .createAlias(advert.getResource().getResourceScope().getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("targetAdvert." + partnerScope.getLowerCamelName(), "partnerResource", JoinType.INNER_JOIN) //
                .createAlias("partnerResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", true)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("target.partnershipState", "action.partnershipState")) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN)
                .createAlias("resource.user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userRoles", "ownerRole", JoinType.LEFT_OUTER_JOIN, //
                        getEndorsementActionJoinResolution()) //
                .createAlias("ownerRole.department", "ownerDepartment", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("resource.advert", advert)) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.in("stateAction.action.id",
                        asList("ENDORSE", "UNENDORSE", "REENDORSE").stream().map(a -> PrismAction.valueOf(scope.name() + "_" + a)).collect(toList()))) //
                .add(getEndorsementActionVisibilityResolution()) //
                .add(getResourceStateActionConstraint()) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .list();
    }

    public AdvertTarget getAdvertTargetForAcceptance(Integer advertTargetId) {
        return (AdvertTarget) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .add(Restrictions.eq("id", advertTargetId)) //
                .add(Restrictions.not(
                        Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("acceptingUser"))
                                .add(Restrictions.eq("partnershipState", ENDORSEMENT_REVOKED)))) //
                .uniqueResult();
    }

    private void appendContextConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        PrismMotivationContext context = queryDTO.getContext();
        if (context.equals(EMPLOYER)) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.like("advert.opportunityCategories", EXPERIENCE.name(), MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("advert.opportunityCategories", WORK.name(), MatchMode.ANYWHERE)));
        } else if (context.equals(UNIVERSITY)) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.like("advert.opportunityCategories", STUDY.name(), MatchMode.ANYWHERE)));
        }
    }

    private void appendVisibilityConstraint(Criteria criteria, PrismScope scope, boolean opportunityScope, boolean userLoggedIn, Set<PrismScope> roleScopes) {
        if (userLoggedIn) {
            Junction targettedConstraint = Restrictions.disjunction();
            for (PrismScope roleScope : roleScopes) {
                targettedConstraint.add(Restrictions.ne("advert" + roleScope.getUpperCamelName() + "AdvertTarget.partnershipState", ENDORSEMENT_REVOKED));
            }

            Junction visibilityConstraint = Restrictions.conjunction() //
                    .add(Restrictions.eq("advert.globallyVisible", false))
                    .add(targettedConstraint);
            if (opportunityScope) {
                visibilityConstraint.add(Restrictions.disjunction() //
                        .add(Restrictions.eq("opportunityType.requireEndorsement", false)) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("opportunityType.requireEndorsement", true)) //
                                .add(Restrictions.eq("advert" + scope.getUpperCamelName() + "AdvertTarget.partnershipState", ENDORSEMENT_PROVIDED))));
            }

            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.eq("advert.globallyVisible", true)) //
                    .add(visibilityConstraint));
        } else {
            criteria.add(Restrictions.eq("advert.globallyVisible", true));
        }
    }

    private void appendLocationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        if (queryDTO.getNeLat() != null) {
            criteria.add(Restrictions.between("address.addressCoordinates.latitude", queryDTO.getSwLat(), queryDTO.getNeLat())) //
                    .add(Restrictions.between("address.addressCoordinates.longitude", queryDTO.getSwLon(), queryDTO.getNeLon()));
        }
    }

    private void appendKeywordConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        String keyword = queryDTO.getKeyword();
        if (keyword != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.like("advert.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("advert.summary", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("advert.description", keyword, MatchMode.ANYWHERE))); //
        }
    }

    private void appendIndustryConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        List<PrismAdvertIndustry> industries = queryDTO.getIndustries();
        if (CollectionUtils.isNotEmpty(industries)) {
            criteria.add(Restrictions.in("industry", industries));
        }
    }

    private void appendFunctionConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        List<PrismAdvertFunction> functions = queryDTO.getFunctions();
        if (CollectionUtils.isNotEmpty(functions)) {
            criteria.add(Restrictions.in("function", functions));
        }
    }

    private void appendOpportunityTypeConstraint(Criteria criteria, PrismScope scope, OpportunitiesQueryDTO queryDTO) {
        if (ResourceOpportunity.class.isAssignableFrom(scope.getResourceClass())) {
            Collection<PrismOpportunityType> opportunityTypes = queryDTO.getOpportunityTypes();
            if (opportunityTypes == null) {
                PrismOpportunityCategory opportunityCategory = queryDTO.getOpportunityCategory();
                if (opportunityCategory != null) {
                    opportunityTypes = getOpportunityTypes(opportunityCategory);
                } else {
                    opportunityTypes = emptyList();
                }
            }

            Disjunction opportunityTypeConstraint = Restrictions.disjunction();
            for (PrismOpportunityType opportunityType : opportunityTypes) {
                opportunityTypeConstraint.add(Restrictions.eq("opportunityType.id", opportunityType));

            }
            criteria.add(opportunityTypeConstraint);
        } else {
            PrismOpportunityCategory opportunityCategory = queryDTO.getOpportunityCategory();
            if (opportunityCategory != null) {
                criteria.add(Restrictions.like("advert.opportunityCategories", opportunityCategory.name(), MatchMode.ANYWHERE));
            }
        }
    }

    private void appendStudyOptionConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        List<PrismStudyOption> studyOptions = queryDTO.getStudyOptions();
        if (CollectionUtils.isNotEmpty(studyOptions)) {
            Disjunction studyOptionConstraint = Restrictions.disjunction();
            for (PrismStudyOption studyOption : studyOptions) {
                studyOptionConstraint.add(Restrictions.eq("resourceStudyOption.studyOption", studyOption));
            }
            criteria.add(studyOptionConstraint);
        }
    }

    private void appendPayConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        appendRangeConstraint(criteria, "advert.pay.monthMinimumAtLocale", "advert.pay.monthMaximumAtLocale", queryDTO.getMinSalary(), queryDTO.getMaxSalary(), true);
    }

    private void appendDurationConstraint(Criteria criteria, String resourceReference, OpportunitiesQueryDTO queryDTO) {
        Junction disjunction = Restrictions.disjunction();
        String lo = resourceReference + ".durationMinimum";
        String hi = resourceReference + ".durationMaximum";
        appendRangeConstraint(disjunction, lo, hi, queryDTO.getMinDuration(), queryDTO.getMaxDuration(), false);
        criteria.add(disjunction);
    }

    private void appendResourceConstraint(Criteria criteria, ResourceDTO resourceDTO) {
        Integer resourceId = resourceDTO.getId();
        PrismScope resourceScope = resourceDTO.getScope();

        String resourceReference = resourceScope.getLowerCamelName();
        String targetResourceReference = "target" + resourceScope.getUpperCamelName();
        Junction resourcesConstraint = Restrictions.disjunction() //
                .add(Restrictions.eq("advert." + resourceReference + ".id", resourceId)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq(targetResourceReference + ".id", resourceId))
                        .add(Restrictions.eqProperty("targetAdvert.id", targetResourceReference + ".advert.id")));

        criteria.add(resourcesConstraint);
    }

    private void appendRangeConstraint(Criteria criteria, String loColumn, String hiColumn, Integer loValue, Integer hiValue, boolean decimal) {
        Junction constraint = buildRangeConstraint(loColumn, hiColumn, loValue, hiValue, decimal);
        if (constraint != null) {
            criteria.add(constraint);
        }
    }

    private void appendRangeConstraint(Junction junction, String loColumn, String hiColumn, Integer loValue, Integer hiValue, boolean decimal) {
        Junction constraint = buildRangeConstraint(loColumn, hiColumn, loValue, hiValue, decimal);
        if (constraint != null) {
            junction.add(constraint);
        }
    }

    private Junction buildRangeConstraint(String loColumn, String hiColumn, Integer loValue, Integer hiValue, boolean decimal) {
        if ((loValue == null || loValue == 0) && (hiValue == null || hiValue == 0)) {
            return null;
        }

        Junction conjunction = Restrictions.conjunction();
        if (loValue != null) {
            conjunction.add(Restrictions.ge(loColumn, hiValue != null && hiValue < loValue ? hiValue : decimal ? new BigDecimal(loValue) : loValue));
        }

        if (hiValue != null) {
            conjunction.add(Restrictions.le(hiColumn, loValue != null && loValue > hiValue ? loValue : decimal ? new BigDecimal(hiValue) : hiValue));
        }
        return conjunction;
    }

}
