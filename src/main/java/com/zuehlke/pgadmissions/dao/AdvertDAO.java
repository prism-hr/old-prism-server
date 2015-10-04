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
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
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
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.dto.AdvertApplicationSummaryDTO;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.dto.AdvertFunctionDTO;
import com.zuehlke.pgadmissions.dto.AdvertIndustryDTO;
import com.zuehlke.pgadmissions.dto.AdvertPartnerActionDTO;
import com.zuehlke.pgadmissions.dto.AdvertStudyOptionDTO;
import com.zuehlke.pgadmissions.dto.AdvertTargetDTO;
import com.zuehlke.pgadmissions.dto.EntityOpportunityFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;

@Repository
@SuppressWarnings("unchecked")
public class AdvertDAO {

    private static final PrismScope[] targetScopes = new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION };

    @Autowired
    private SessionFactory sessionFactory;

    public Advert getAdvert(PrismScope resourceScope, Integer resourceId) {
        return (Advert) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass())
                .setProjection(Projections.property("advert"))
                .add(Restrictions.eq("id", resourceId))
                .uniqueResult();
    }

    public AdvertApplicationSummaryDTO getAdvertApplicationSummary(Advert advert) {
        return (AdvertApplicationSummaryDTO) sessionFactory.getCurrentSession().createCriteria(Application.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.countDistinct("id").as("applicationCount")) //
                        .add(Projections.sum("applicationRatingCount").as("applicationRatingCount")) //
                        .add(Projections.avg("applicationRatingAverage").as("applicationRatingAverage"))) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertApplicationSummaryDTO.class)) //
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
                        .add(Projections.countDistinct("application.id").as("applicationCount")) //
                        .add(Projections.sum("application.applicationRatingCount").as("applicationRatingCount")) //
                        .add(Projections.avg("application.applicationRatingAverage").as("applicationRatingAverage")) //
                        .add(Projections.property("sequenceIdentifier").as("sequenceIdentifier"))) //
                .createAlias("user", "user", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("institution", "institution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applications", "application", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.isNotNull("application.submittedTimestamp")) //
                .createAlias("opportunityType", "opportunityType", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("closingDate", "closingDate", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.in("id", adverts));

        appendOpportunityTypeConstraint(criteria, query);

        String lastSequenceIdentifier = query.getLastSequenceIdentifier();
        if (lastSequenceIdentifier != null) {
            criteria.add(Restrictions.lt("sequenceIdentifier", lastSequenceIdentifier));
        }

        return (List<AdvertDTO>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
                .setMaxResults(ADVERT_LIST_PAGE_ROW_COUNT) //
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class))
                .list();
    }

    public List<EntityOpportunityFilterDTO> getVisibleAdverts(PrismScope scope, Collection<PrismState> activeStates, PrismActionCondition actionCondition,
            OpportunitiesQueryDTO query, User currentUser, Set<Integer> possibleTargets) {
        ProjectionList projections = Projections.projectionList() //
                .add(Projections.groupProperty("advert.id").as("id")) //
                .add(Projections.property("resource.opportunityCategories").as("opportunityCategories"));
      
        if (scope.getScopeCategory().equals(OPPORTUNITY)) {
            projections.add(Projections.property("resource.opportunityType.id").as("opportunityType"));
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projections) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.categories.industries", "industry", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.categories.functions", "function", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.INNER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceCondition.externalMode", true)) //
                                .add(Restrictions.eq("resourceCondition.actionCondition", actionCondition))); //

        ResourceDTO resource = query.getResource();
        boolean narrowedByResource = resource != null;
        boolean narrowedByTarget = currentUser != null;

        if (isNotEmpty(possibleTargets) && (narrowedByResource || narrowedByTarget)) {
            for (PrismScope targetScope : targetScopes) {
                String scopeReference = targetScope.getLowerCamelName();
                String advertReference = scopeReference + "Advert";
                criteria.createAlias("advert." + targetScope.getLowerCamelName(), scopeReference, JoinType.LEFT_OUTER_JOIN)
                        .createAlias(scopeReference + ".advert", advertReference, JoinType.LEFT_OUTER_JOIN)
                        .createAlias(advertReference + ".targets", advertReference + "Target", JoinType.LEFT_OUTER_JOIN);
            }
        }

        Class<? extends Resource> resourceClass = scope.getResourceClass();
        boolean opportunityRequest = ResourceOpportunity.class.isAssignableFrom(resourceClass);
        if (opportunityRequest) {
            criteria.createAlias("resource.opportunityType", "opportunityType", JoinType.INNER_JOIN) //
                    .createAlias("resource.resourceStudyOptions", "resourceStudyOption", JoinType.INNER_JOIN);
        }

        criteria.add(Restrictions.in("state.id", activeStates));

        appendContextConstraint(criteria, query);
        appendVisibilityConstraint(criteria, scope, resource, narrowedByTarget, opportunityRequest, isTrue(query.getRecommended()), possibleTargets);

        appendLocationConstraint(criteria, query);
        appendKeywordConstraint(query, criteria);

        appendIndustryConstraint(criteria, query);
        appendFunctionConstraint(criteria, query);

        if (opportunityRequest) {
            appendStudyOptionConstraint(query, criteria);
        }

        appendPayConstraint(criteria, query);

        if (opportunityRequest) {
            appendDurationConstraint(criteria, query);
        }

        return criteria.setResultTransformer(Transformers.aliasToBean(EntityOpportunityFilterDTO.class))
                .list();
    }

    public List<AdvertPartnerActionDTO> getAdvertActionConditions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        return (List<AdvertPartnerActionDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.groupProperty("resourceCondition.actionCondition").as("actionCondition"))) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceStates", "resourceState", JoinType.INNER_JOIN)
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("stateAction.actionCondition", "resourceCondition.actionCondition")) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.creationScope", "creationScope", JoinType.INNER_JOIN) //
                .add(Restrictions.in("resource.id", resourceIds)) //
                .add(Restrictions.eq("resourceCondition.externalMode", true)) //
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .addOrder(Order.desc("id")) //
                .addOrder(Order.asc("creationScope.ordinal")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertPartnerActionDTO.class)) //
                .list();
    }

    public List<AdvertStudyOptionDTO> getAdvertStudyOptions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        return (List<AdvertStudyOptionDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.groupProperty("resourceStudyOption.studyOption").as("studyOption"))) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceStudyOptions", "resourceStudyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.in("resource.id", resourceIds)) //
                .addOrder(Order.asc("id")) //
                .addOrder(Order.asc("resourceStudyOption.studyOption"))
                .setResultTransformer(Transformers.aliasToBean(AdvertStudyOptionDTO.class)) //
                .list();
    }

    public List<AdvertIndustryDTO> getAdvertIndustries(PrismScope resourceScope, Collection<Integer> resourceIds) {
        return (List<AdvertIndustryDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.groupProperty("industry.industry").as("industry"))) //
                .createAlias("categories.industries", "industry", JoinType.INNER_JOIN) //
                .add(Restrictions.in(resourceScope.getLowerCamelName() + ".id", resourceIds)) //
                .addOrder(Order.asc("id")) //
                .addOrder(Order.asc("industry.industry"))
                .setResultTransformer(Transformers.aliasToBean(AdvertIndustryDTO.class)) //
                .list();
    }

    public List<AdvertFunctionDTO> getAdvertFunctions(PrismScope resourceScope, Collection<Integer> resourceIds) {
        return (List<AdvertFunctionDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.groupProperty("function.function").as("function"))) //
                .createAlias("categories.functions", "function", JoinType.INNER_JOIN) //
                .add(Restrictions.in(resourceScope.getLowerCamelName() + ".id", resourceIds)) //
                .addOrder(Order.asc("id")) //
                .addOrder(Order.asc("function.function"))
                .setResultTransformer(Transformers.aliasToBean(AdvertFunctionDTO.class)) //
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

    public List<AdvertTargetDTO> getAdvertTargets(ResourceParent resource) {
        Department department = resource.getDepartment();
        Institution institution = resource.getInstitution();

        Junction resourceConstraint = null;
        if (department == null) {
            resourceConstraint = Restrictions.conjunction()
                    .add(Restrictions.eq("advert.institution", institution)) //
                    .add(Restrictions.isNull("advert.department")) //
                    .add(Restrictions.isNull("advert.program")) //
                    .add(Restrictions.isNull("advert.project"));
        } else {
            resourceConstraint = Restrictions.conjunction()
                    .add(Restrictions.eq("advert.institution", institution)) //
                    .add(Restrictions.eq("advert.department", department)) //
                    .add(Restrictions.isNull("advert.program")) //
                    .add(Restrictions.isNull("advert.project"));
        }

        return (List<AdvertTargetDTO>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("targetInstitution.id").as("targetInstitutionId")) //
                        .add(Projections.property("targetInstitution.name").as("targetInstitutionName")) //
                        .add(Projections.property("targetInstitution.logoImage.id").as("targetLogoImageId")) //
                        .add(Projections.property("targetDepartment.id").as("targetDepartmentId")) //
                        .add(Projections.property("targetDepartment.name").as("targetDepartmentName"))
                        .add(Projections.property("targetAdvertUser.id").as("userId")) //
                        .add(Projections.property("targetAdvertUser.id").as("userId")) //
                        .add(Projections.property("targetAdvertUser.firstName").as("userFirstName")) //
                        .add(Projections.property("targetAdvertUser.firstName2").as("userFirstName2")) //
                        .add(Projections.property("targetAdvertUser.firstName3").as("userFirstName3")) //
                        .add(Projections.property("targetAdvertUser.lastName").as("userLastName")) //
                        .add(Projections.property("targetAdvertUser.email").as("userEmail")) //
                        .add(Projections.property("targetAdvertUserAccount.linkedinProfileUrl").as("userLinkedinProfileUrl")) //
                        .add(Projections.property("targetAdvertUserAccount.linkedinImageUrl").as("userLinkedinImageUrl")) //
                        .add(Projections.property("targetAdvertUserAccount.portraitImage.id").as("userPortraitImageId"))) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvertUser", "targetAdvertUser", JoinType.INNER_JOIN) //
                .createAlias("targetAdvertUser.userAccount", "targetAdvertUserAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("targetAdvert.institution", "targetInstitution", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert.department", "targetDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("acceptAdvert", "acceptAdvert", JoinType.INNER_JOIN) //
                .createAlias("acceptAdvert.institution", "acceptInstitution", JoinType.INNER_JOIN)
                .createAlias("acceptAdvert.department", "acceptDepartment", JoinType.INNER_JOIN)
                .add(resourceConstraint) //
                .setResultTransformer(Transformers.aliasToBean(AdvertTargetDTO.class))
                .list();
    }

    public List<AdvertTargetDTO> getAdvertTargets(PrismScope resourceScope, String advertReference, User user, boolean pendingResponse) {
        String resourceReference = resourceScope.getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("acceptInstitution.id").as("institutionId")) //
                        .add(Projections.property("acceptInstitution.name").as("institutionName")) //
                        .add(Projections.property("acceptInstitution.logoImage.id").as("logoImageId")) //
                        .add(Projections.property("acceptDepartment.id").as("departmentId")) //
                        .add(Projections.property("acceptDepartment.name").as("departmentName"))
                        .add(Projections.property("id").as("advertTargetId")) //
                        .add(Projections.property("institution.id").as("targetInstitutionId")) //
                        .add(Projections.property("institution.name").as("targetInstitutionName")) //
                        .add(Projections.property("institution.logoImage.id").as("targetLogoImageId")) //
                        .add(Projections.property("department.id").as("targetDepartmentId")) //
                        .add(Projections.property("department.name").as("targetDepartmentName"))
                        .add(Projections.property("advertUser.id").as("userId")) //
                        .add(Projections.property("advertUser.id").as("userId")) //
                        .add(Projections.property("advertUser.firstName").as("userFirstName")) //
                        .add(Projections.property("advertUser.firstName2").as("userFirstName2")) //
                        .add(Projections.property("advertUser.firstName3").as("userFirstName3")) //
                        .add(Projections.property("advertUser.lastName").as("userLastName")) //
                        .add(Projections.property("advertUser.email").as("userEmail")) //
                        .add(Projections.property("advertUserAccount.linkedinProfileUrl").as("userLinkedinProfileUrl")) //
                        .add(Projections.property("advertUserAccount.linkedinImageUrl").as("userLinkedinImageUrl")) //
                        .add(Projections.property("advertUserAccount.portraitImage.id").as("userPortraitImageId"))) //
                .createAlias(advertReference, "advert", JoinType.INNER_JOIN) //
                .createAlias(advertReference + "User", "advertUser", JoinType.INNER_JOIN) //
                .createAlias("advertUser.userAccount", "advertUserAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("advert.department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("acceptAdvert", "acceptAdvert", JoinType.INNER_JOIN) //
                .createAlias("acceptAdvert.institution", "acceptInstitution", JoinType.INNER_JOIN)
                .createAlias("acceptAdvert.department", "acceptDepartment", JoinType.INNER_JOIN)
                .createAlias("acceptAdvert." + resourceScope.getLowerCamelName(), "acceptResource", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("acceptAdvert.id", "acceptResource.advert.id"))
                .createAlias("acceptAdvert.userRoles", "userRole", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.conjunction()
                                .add(Restrictions.eq("userRole.user", user))
                                .add(Restrictions.disjunction()
                                        .add(Restrictions.eq("userRole.role.id", PrismRole.valueOf(resourceReference + "_ADMINISTRATOR")))
                                        .add(Restrictions.eq("userRole.role.id", PrismRole.valueOf(resourceReference) + "_APPROVER"))))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("acceptAdvertUser", user))
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNull("acceptAdvertUser"))
                                .add(Restrictions.isNotNull("userRole.id"))));

        if (pendingResponse) {
            criteria.add(Restrictions.eq("partnershipState", ENDORSEMENT_PENDING));
        }

        return (List<AdvertTargetDTO>) criteria.setResultTransformer(Transformers.aliasToBean(AdvertTargetDTO.class))
                .list();
    }

    public List<Advert> getAdvertsTargetsForWhichUserCanEndorse(Advert advert, User user, PrismScope scope, PrismScope partnerScope) {
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

    public AdvertTarget getAdvertTargetById(Integer advertTargetId) {
        return (AdvertTarget) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .add(Restrictions.eq("id", advertTargetId)) //
                .uniqueResult();
    }

    public List<Integer> getAdvertIds(PrismScope resourceScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("advert.id")) //
                .list();
    }

    public Integer getAdvertId(PrismScope resourceScope, Integer resourceId) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("advert.id")) //
                .add(Restrictions.eq("id", resourceId)) //
                .uniqueResult();
    }

    public List<Integer> getAdvertIds(PrismScope parentScope, Integer parentId, PrismScope resourceScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.property("advert.id")) //
                .add(Restrictions.eq(parentScope.getLowerCamelName() + ".id", parentId)) //
                .list();
    }

    public List<Integer> getUserAdvertIds(PrismScope resourceScope, User user) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("resource.advert.id"))
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role.verified", true)) //
                .list();
    }

    public Integer getUserAdvertId(PrismScope resourceScope, Integer resourceId, User user) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("resource.advert.id"))
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource.id", resourceId)) //
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role.verified", true)) //
                .uniqueResult();
    }

    public List<Integer> getUserAdvertIds(PrismScope parentScope, Integer parentId, PrismScope resourceScope, User user) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("resource.advert.id"))
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource." + parentScope.getLowerCamelName() + ".id", parentId)) //
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role.verified", true)) //
                .list();
    }

    public void processAdvertTarget(Integer advertTargetId, PrismPartnershipState partnershipState) {
        sessionFactory.getCurrentSession().createQuery(
                "update AdvertTarget "
                        + "set partnershipState = :partnershipState "
                        + "where id = :advertTargetId")
                .setParameter("advertTargetId", advertTargetId)
                .setParameter("partnershipState", partnershipState)
                .executeUpdate();
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

    private void appendVisibilityConstraint(Criteria criteria, PrismScope scope, ResourceDTO resource, boolean userLoggedIn, boolean opportunityScope,
            boolean recommended, Set<Integer> possibleTargets) {
        if (userLoggedIn && isNotEmpty(possibleTargets)) {
            boolean hasTargetConstraints = false;
            Junction targetConstraints = Restrictions.disjunction();
            for (PrismScope targetScope : targetScopes) {
                String targetReference = targetScope.getLowerCamelName() + "AdvertTarget";
                targetConstraints.add(Restrictions.conjunction()
                        .add(Restrictions.in(targetReference + ".targetAdvert.id", possibleTargets))
                        .add(Restrictions.ne(targetReference + ".partnershipState", ENDORSEMENT_REVOKED)));
                hasTargetConstraints = true;
            }

            if (opportunityScope) {
                criteria.add(Restrictions.disjunction() //
                        .add(Restrictions.eq("opportunityType.requireEndorsement", false)) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("opportunityType.requireEndorsement", true)) //
                                .add(Restrictions.eq(scope.getLowerCamelName() + "AdvertTarget.partnershipState", ENDORSEMENT_PROVIDED))));
            }

            if (hasTargetConstraints) {
                if (recommended) {
                    criteria.add(targetConstraints);
                } else {
                    criteria.add(Restrictions.disjunction() //
                            .add(Restrictions.eq("advert.globallyVisible", true)) //
                            .add(targetConstraints));
                }
            } else if (!recommended) {
                criteria.add(Restrictions.eq("advert.globallyVisible", true));
            }
        } else {
            criteria.add(Restrictions.eq("advert.globallyVisible", true));
        }

        if (resource != null) {
            Integer resourceId = resource.getId();
            String resourceReference = resource.getScope().getLowerCamelName();

            criteria.add(Restrictions.disjunction()
                    .add(Restrictions.eq("advert." + resourceReference + ".id", resourceId))
                    .add(Restrictions.eq(resourceReference + "AdvertTarget.targetAdvert.id", resourceId)));
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

    private void appendOpportunityTypeConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        List<PrismOpportunityType> opportunityTypes = queryDTO.getOpportunityTypes();
        if (isNotEmpty(opportunityTypes)) {
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

    private void appendDurationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        Junction disjunction = Restrictions.disjunction();
        String lo = "resource.durationMinimum";
        String hi = "resource.durationMaximum";
        appendRangeConstraint(disjunction, lo, hi, queryDTO.getMinDuration(), queryDTO.getMaxDuration(), false);
        criteria.add(disjunction);
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
