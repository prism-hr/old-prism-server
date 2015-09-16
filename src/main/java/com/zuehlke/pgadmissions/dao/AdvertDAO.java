package com.zuehlke.pgadmissions.dao;

import static com.google.common.collect.Lists.newArrayList;
import static com.zuehlke.pgadmissions.PrismConstants.ADVERT_LIST_PAGE_ROW_COUNT;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceStateActionConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.PrismAdvertContext.EMPLOYERS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismAdvertContext.UNIVERSITIES;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_IDENTIFICATION_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING_IDENTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import com.zuehlke.pgadmissions.domain.advert.AdvertTargetAdvert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAdvert;
import com.zuehlke.pgadmissions.dto.AdvertActionConditionDTO;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.AdvertStudyOptionDTO;
import com.zuehlke.pgadmissions.dto.EntityOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;

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
                        .add(Projections.property("primaryExternalAccount.accountProfileUrl").as("userAccountProfileUrl")) //
                        .add(Projections.property("primaryExternalAccount.accountImageUrl").as("userAccountImageUrl")) //
                        .add(Projections.property("institution.id").as("institutionId")) //
                        .add(Projections.property("institution.name").as("institutionName")) //
                        .add(Projections.property("institution.logoImage.id").as("institutionLogoImageId")) //
                        .add(Projections.property("department.id").as("departmentId")) //
                        .add(Projections.property("department.name").as("departmentName")) //
                        .add(Projections.property("program.id").as("programId")) //
                        .add(Projections.property("program.name").as("programName")) //
                        .add(Projections.property("project.id").as("projectId")) //
                        .add(Projections.property("project.name").as("projectName")) //
                        .add(Projections.property("opportunityType.name").as("opportunityType")) //
                        .add(Projections.property("name").as("name")) //
                        .add(Projections.property("summary").as("summary")) //
                        .add(Projections.property("description").as("description")) //
                        .add(Projections.property("homepage").as("homepage")) //
                        .add(Projections.property("applyHomepage").as("applyHomepage")) //
                        .add(Projections.property("telephone").as("telephone")) //
                        .add(Projections.property("address.addressLine1").as("addressLine1")) //
                        .add(Projections.property("address.addressLine2").as("addressLine2")) //
                        .add(Projections.property("address.addressTown").as("addressTown")) //
                        .add(Projections.property("address.addressRegion").as("addressRegion")) //
                        .add(Projections.property("address.addressCode").as("addressCode")) //
                        .add(Projections.property("domicile.name").as("addressDomicileName")) //
                        .add(Projections.property("address.googleId").as("addressGoogleId")) //
                        .add(Projections.property("address.addressCoordinates.latitude").as("addressCoordinateLatitude")) //
                        .add(Projections.property("address.addressCoordinates.longitude").as("addressCoordinateLongitude")) //
                        .add(Projections.property("pay.currencyAtLocale").as("payCurrency")) //
                        .add(Projections.property("pay.interval").as("payInterval")) //
                        .add(Projections.property("pay.monthMinimumAtLocale").as("payMonthMinimum")) //
                        .add(Projections.property("pay.monthMaximumAtLocale").as("payMonthMaximum")) //
                        .add(Projections.property("pay.yearMinimumAtLocale").as("payYearMinimum")) //
                        .add(Projections.property("pay.yearMaximumAtLocale").as("payYearMaximum")) //
                        .add(Projections.property("closingDate.value").as("closingDate")) //
                        .add(Projections.property("sequenceIdentifier").as("sequenceIdentifier"))) //
                .createAlias("user", "user", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userAccount.primaryExternalAccount", "primaryExternalAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("institution", "institution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("opportunityType", "opportunityType", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("address.domicile", "domicile", JoinType.LEFT_OUTER_JOIN) //
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
            OpportunitiesQueryDTO query) {
        String resourceReference = scope.getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("advert.id").as("id")) //
                        .add(Projections.property(resourceReference + ".opportunityCategories").as("opportunityCategories"))) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.categories.industries", "industry", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.categories.functions", "function", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.categories.themes", "theme", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.INNER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceCondition.externalMode", true)) //
                                .add(Restrictions.eq("resourceCondition.actionCondition", actionCondition))); //

        boolean narrowed = query.isNarrowed();
        if (narrowed) {
            criteria.createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN) //
                    .createAlias("advertTarget.value", "targetAdvert", JoinType.LEFT_OUTER_JOIN);
        } else {
            criteria.createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN);
        }

        Class<? extends Resource> resourceClass = scope.getResourceClass();
        boolean opportunityScope = ResourceOpportunity.class.isAssignableFrom(resourceClass);
        if (opportunityScope) {
            criteria.createAlias(resourceReference + ".opportunityType", "opportunityType", JoinType.LEFT_OUTER_JOIN) //
                    .createAlias(resourceReference + ".instanceGroups", "instanceGroup", JoinType.LEFT_OUTER_JOIN) //
                    .createAlias("instanceGroup.studyOption", "studyOption", JoinType.LEFT_OUTER_JOIN);
        }

        criteria.add(Restrictions.in("state.id", activeStates));

        appendContextConstraint(criteria, query);
        appendLocationConstraint(criteria, query);
        appendKeywordConstraint(query, criteria);

        appendIndustryConstraint(criteria, query);
        appendFunctionConstraint(criteria, query);

        appendOpportunityTypeConstraint(criteria, scope, query);
        if (opportunityScope) {
            appendStudyOptionConstraint(query, criteria);
        }

        appendFeeConstraint(criteria, query);
        appendPayConstraint(criteria, query);

        if (opportunityScope) {
            appendDurationConstraint(criteria, resourceReference, query);
        }

        if (narrowed) {
            appendResourceConstraint(criteria, query.getResourceScope(), query.getResourceId(), actionCondition);
        } else {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.isNull("advertTarget.id")) //
                    .add(Restrictions.eq("advertTarget.selected", false)));
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
                        .add(Projections.groupProperty("studyOption.name").as("studyOption"))) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".instanceGroups", "instanceGroup", JoinType.INNER_JOIN) //
                .createAlias("instanceGroup.studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.in(resourceReference + ".id", resourceIds)) //
                .setResultTransformer(Transformers.aliasToBean(AdvertStudyOptionDTO.class)) //
                .list();
    }

    public List<AdvertRecommendationDTO> getRecommendedAdverts(User user, HashMultimap<PrismScope, PrismState> scopes, List<Integer> advertsRecentlyAppliedFor) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Application.class, "application") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("otherUserApplication.advert"), "advert") //
                        .add(Projections.countDistinct("otherUserApplication.user").as("applicationCount"), "applicationCount")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.applications", "advertApplication", JoinType.INNER_JOIN) //
                .createAlias("advertApplication.user", "otherUser", JoinType.INNER_JOIN) //
                .createAlias("otherUser.applications", "otherUserApplication", JoinType.INNER_JOIN) //
                .createAlias("otherUserApplication.advert", "recommendedAdvert", JoinType.INNER_JOIN) //
                .createAlias("recommendedAdvert.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.resourceStates", "programState", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.resourceConditions", "programCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("recommendedAdvert.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.resourceStates", "projectState", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.resourceConditions", "projectCondition", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.ne("advertApplication.user", user)) //
                .add(Restrictions.neProperty("advert", "otherUserApplication.advert")) //
                .add(Restrictions.isNotNull("otherUserApplication.submittedTimestamp")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("program.id")) //
                                .add(Restrictions.isNotNull("programCondition.id")) //
                                .add(Restrictions.in("programState.state.id", scopes.get(PROGRAM)))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("project.id")) //
                                .add(Restrictions.isNotNull("projectCondition.id")) //
                                .add(Restrictions.in("projectState.state.id", scopes.get(PROJECT)))));

        if (!advertsRecentlyAppliedFor.isEmpty()) {
            criteria.add(Restrictions.not( //
                    Restrictions.in("recommendedAdvert.id", advertsRecentlyAppliedFor)));
        }

        return (List<AdvertRecommendationDTO>) criteria.addOrder(Order.desc("applicationCount")) //
                .addOrder(Order.desc("recommendedAdvert.sequenceIdentifier")) //
                .setMaxResults(10) //
                .setResultTransformer(Transformers.aliasToBean(AdvertRecommendationDTO.class)) //
                .list();
    }

    public List<Integer> getAdvertsRecentlyAppliedFor(User user, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("completionDate")) //
                        .add(Restrictions.ge("completionDate", baseline))) //
                .add(Restrictions.eq("user", user)) //
                .list();
    }

    public AdvertClosingDate getNextAdvertClosingDate(Advert advert, LocalDate baseline) {
        return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(AdvertClosingDate.class) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.ge("value", baseline)) //
                .addOrder(Order.asc("value")) //
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
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("fee.currencySpecified")) //
                                .add(Restrictions.isNotNull("fee.currencyAtLocale")) //
                                .add(Restrictions.neProperty("fee.currencySpecified", "fee.currencyAtLocale")))
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("pay.currencySpecified")) //
                                .add(Restrictions.isNotNull("pay.currencyAtLocale")) //
                                .add(Restrictions.neProperty("pay.currencySpecified", "pay.currencyAtLocale"))))
                .list();
    }

    public List<Integer> getAdvertsWithElapsedClosingDates(LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("closingDate", "closingDate", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("closingDates", "otherClosingDate", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.lt("closingDate.closingDate", baseline)) //
                        .add(Restrictions.conjunction().add(Restrictions.isNull("closingDate.id")) //
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
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("fee.currencySpecified", currency)) //
                        .add(Restrictions.eq("pay.currencySpecified", currency))) //
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

    public List<ImportedDomicile> getAdvertDomiciles() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedDomicile.class) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

    public List<Competence> searchCompetences(String q) {
        return sessionFactory.getCurrentSession().createCriteria(Competence.class)
                .add(Restrictions.like("name", q, MatchMode.ANYWHERE))
                .list();
    }

    public void deleteAdvertAttributes(Advert advert, Class<? extends AdvertAttribute<?>> attributeClass) {
        sessionFactory.getCurrentSession().createQuery(
                "delete " + attributeClass.getSimpleName() + " "
                        + "where advert = :advert")
                .setParameter("advert", advert) //
                .executeUpdate();
    }

    public void deleteAdvertAttributes(Advert advert, Class<? extends AdvertAttribute<?>> attributeClass, List<Integer> newValues) {
        sessionFactory.getCurrentSession().createQuery(
                "delete " + attributeClass.getSimpleName() + " "
                        + "where advert = :advert "
                        + "and value.id not in (:newValues)")
                .setParameter("advert", advert) //
                .setParameterList("newValues", newValues) //
                .executeUpdate();
    }

    public List<Integer> getAdvertTargetAdverts(Advert advert, boolean selected) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTargetAdvert.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.eq("selected", selected)) //
                .list();
    }

    public List<Integer> getAdvertSelectedTargetAdverts(Advert advert) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTargetAdvert.class) //
                .setProjection(Projections.property("value.id")) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.eq("selected", true)) //
                .list();
    }

    public List<Integer> getAdvertTargetResources(Advert advert, PrismScope resourceScope, boolean selected) {
        String resourceReference = resourceScope.getLowerCamelName();
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTargetAdvert.class) //
                .setProjection(Projections.property(resourceReference + ".id")) //
                .createAlias("value", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + resourceReference, resourceReference, JoinType.INNER_JOIN,
                        Restrictions.eqProperty(resourceReference + ".advert.id", "targetAdvert.id")) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.eq("selected", selected)) //
                .list();
    }

    public List<Integer> getAdvertTargetsUserCanEndorseFor(Advert advert, User user, PrismScope scope, PrismScope partnerScope) {
        String partnerScopeReference = partnerScope.getLowerCamelName();
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(scope.getResourceClass())
                .setProjection(Projections.groupProperty("advertTarget.id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.INNER_JOIN,
                        Restrictions.eq("advertTarget.selected", true)) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.INNER_JOIN)
                .createAlias("targetAdvert." + partnerScopeReference, partnerScopeReference, JoinType.INNER_JOIN) //
                .createAlias(partnerScopeReference + ".userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", true)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN,
                        Restrictions.disjunction() //
                                .add(Restrictions.isNull("action.partnershipState")) //
                                .add(Restrictions.eqProperty("advertTarget.partnershipState", "action.partnershipState"))) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.in("stateAction.action.id",
                        asList("ENDORSE", "UNENDORSE", "REENDORSE").stream().map(a -> PrismAction.valueOf(scope.name() + "_" + a)).collect(Collectors.toList()))) //
                .add(getResourceStateActionConstraint()) //
                .add(Restrictions.eqProperty("stateAction.state", "resourceState.state")) //
                .list();
    }

    public List<Integer> getAdvertsUserCanIdentifyFor(Advert advert, User applicant, User user, PrismScope scope, PrismScope partnerScope) {
        String partnerScopeReference = partnerScope.getLowerCamelName();
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(scope.getResourceClass())
                .setProjection(Projections.groupProperty("userAdvert.advert.id")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAdverts", "userAdvert", JoinType.INNER_JOIN) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.INNER_JOIN,
                        Restrictions.eq("advertTarget.selected", true)) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.INNER_JOIN)
                .createAlias("targetAdvert." + partnerScopeReference, partnerScopeReference, JoinType.INNER_JOIN) //
                .createAlias(partnerScopeReference + ".userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", true)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN,
                        Restrictions.disjunction() //
                                .add(Restrictions.isNull("action.partnershipState")) //
                                .add(Restrictions.eqProperty("advertTarget.partnershipState", "action.partnershipState"))) //
                .add(Restrictions.eqProperty("userAdvert.advert", "advertTarget.value")) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.eq("user", applicant)) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("stateAction.action.id", APPLICATION_COMPLETE_IDENTIFICATION_STAGE)) //
                .add(Restrictions.eqProperty("stateAction.state", "resourceState.state")) //
                .list();
    }

    public void endorseForAdvertTargets(Collection<Integer> advertTargets, PrismPartnershipState partnershipState) {
        sessionFactory.getCurrentSession().createQuery( //
                "update AdvertTargetAdvert "
                        + "set partnershipState = :partnershipState "
                        + "where id in (:advertTargets) "
                        + "and selected = true") //
                .setParameterList("advertTargets", advertTargets) //
                .setParameter("partnershipState", partnershipState) //
                .executeUpdate();
    }

    public void identifyForAdverts(User user, Collection<Integer> adverts) {
        sessionFactory.getCurrentSession().createQuery( //
                "update UserAdvert "
                        + "set identified = true "
                        + "where user = :user "
                        + "and advert.id in (:adverts)") //
                .setParameter("user", user) //
                .setParameterList("adverts", adverts) //
                .executeUpdate();
    }

    public List<Integer> getAdvertsUserIdentifiedFor(User user) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserAdvert.class) //
                .setProjection(Projections.property("advert.id")) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("identified", true)) //
                .list();
    }

    public void verifyAdvertTargetUser(Advert value, User valueUser) {
        sessionFactory.getCurrentSession().createQuery( //
                "update AdvertTargetAdvert " //
                        + "set partnershipState = :newPartnershipState "
                        + "where value = :value "
                        + "and valueUser = :valueUser "
                        + "and partnershipState = :oldPartnershipState") //
                .setParameter("newPartnershipState", ENDORSEMENT_PENDING) //
                .setParameter("value", value) //
                .setParameter("valueUser", valueUser) //
                .setParameter("oldPartnershipState", ENDORSEMENT_PENDING_IDENTIFICATION) //
                .executeUpdate();
    }

    public List<Integer> getUserDeparmentInstitutionAdverts(User user, Collection<Integer> departmentAdverts) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserAdvert.class) //
                .setProjection(Projections.groupProperty("institution.advert.id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.department", "department", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("advert.id", "department.advert.id")) //
                .createAlias("department.institution", "institution", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.in("advert.id", departmentAdverts)) //
                .list();
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
                    .add(Restrictions.like("advert.description", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("theme.value", keyword, MatchMode.ANYWHERE))); //
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

    private void appendContextConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        PrismAdvertContext context = queryDTO.getContext();
        if (context.equals(EMPLOYERS)) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.like("advert.opportunityCategories", EXPERIENCE.name(), MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("advert.opportunityCategories", WORK.name(), MatchMode.ANYWHERE)));
        } else if (context.equals(UNIVERSITIES)) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.like("advert.opportunityCategories", STUDY.name(), MatchMode.ANYWHERE)));
        }
    }

    private void appendOpportunityTypeConstraint(Criteria criteria, PrismScope scope, OpportunitiesQueryDTO queryDTO) {
        if (ResourceOpportunity.class.isAssignableFrom(scope.getResourceClass())) {
            Collection<PrismOpportunityType> opportunityTypes = queryDTO.getOpportunityTypes();
            if (opportunityTypes == null) {
                if (queryDTO.getOpportunityCategory() != null) {
                    opportunityTypes = PrismOpportunityType.getOpportunityTypes(queryDTO.getOpportunityCategory());
                } else {
                    opportunityTypes = Collections.emptyList();
                }
            }

            Disjunction opportunityTypeConstraint = Restrictions.disjunction();
            for (PrismOpportunityType opportunityType : opportunityTypes) {
                opportunityTypeConstraint.add(Restrictions.eq("opportunityType.name", opportunityType.name()));

            }
            criteria.add(opportunityTypeConstraint);
        } else {
            if (queryDTO.getOpportunityCategory() != null) {
                criteria.add(Restrictions.like("advert.opportunityCategories", queryDTO.getOpportunityCategory().name(), MatchMode.ANYWHERE));
            }
        }
    }

    private void appendStudyOptionConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        List<PrismStudyOption> studyOptions = queryDTO.getStudyOptions();
        if (CollectionUtils.isNotEmpty(studyOptions)) {
            Disjunction studyOptionConstraint = Restrictions.disjunction();
            for (PrismStudyOption studyOption : studyOptions) {
                studyOptionConstraint.add(Restrictions.eq("studyOption.name", studyOption.name()));
            }
            criteria.add(studyOptionConstraint);
        }
    }

    private void appendFeeConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        appendRangeConstraint(criteria, "advert.fee.monthMinimumAtLocale", "advert.fee.monthMaximumAtLocale", queryDTO.getMinFee(), queryDTO.getMaxFee(), true);
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

    private void appendResourceConstraint(Criteria criteria, PrismScope scope, Integer resourceId, PrismActionCondition actionCondition) {
        if (resourceId != null) {
            Junction resourcesConstraint = Restrictions.disjunction() //
                    .add(Restrictions.eq("advert." + scope.getLowerCamelName() + ".id", resourceId));

            if (actionCondition.equals(ACCEPT_APPLICATION) && newArrayList(DEPARTMENT, INSTITUTION).contains(scope)) {
                resourcesConstraint.add(getPartnerResourceConstraint(scope, resourceId));
            }

            criteria.add(resourcesConstraint);
        }
    }

    private Junction getPartnerResourceConstraint(PrismScope resourceScope, Integer resourceId) {
        return Restrictions.conjunction() //
                .add(Restrictions.eq("advertTarget.partnershipState", ENDORSEMENT_PROVIDED))
                .add(Restrictions.eq("targetAdvert." + resourceScope.getLowerCamelName() + ".id", resourceId));
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
