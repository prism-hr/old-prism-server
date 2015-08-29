package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

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

import com.amazonaws.services.dynamodbv2.model.Projection;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertAttribute;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFunction;
import com.zuehlke.pgadmissions.domain.advert.AdvertIndustry;
import com.zuehlke.pgadmissions.domain.advert.AdvertTargetAdvert;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAdvert;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.AdvertStudyOptionDTO;
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

    public List<AdvertDTO> getAdverts(List<Integer> adverts) {
        return (List<AdvertDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
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
                        .add(Projections.property("fee.currencyAtLocale").as("feeCurrency")) //
                        .add(Projections.property("fee.interval").as("feeInterval")) //
                        .add(Projections.property("fee.monthMinimumAtLocale").as("feeMonthMinimum")) //
                        .add(Projections.property("fee.monthMaximumAtLocale").as("feeMonthMaximum")) //
                        .add(Projections.property("fee.yearMinimumAtLocale").as("feeYearMinimum")) //
                        .add(Projections.property("fee.yearMaximumAtLocale").as("feeYearMaximum")) //
                        .add(Projections.property("pay.currencyAtLocale").as("payCurrency")) //
                        .add(Projections.property("pay.interval").as("payInterval")) //
                        .add(Projections.property("pay.monthMinimumAtLocale").as("payMonthMinimum")) //
                        .add(Projections.property("pay.monthMaximumAtLocale").as("payMonthMaximum")) //
                        .add(Projections.property("pay.yearMinimumAtLocale").as("payYearMinimum")) //
                        .add(Projections.property("pay.yearMaximumAtLocale").as("payYearMaximum")) //
                        .add(Projections.property("closingDate.value").as("closingDate")) //
                        .add(Projections.property("sequenceIdentifier").as("sequenceIdentifier"))) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userAccount.primaryExternalAccount", "primaryExternalAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("opportunityType", "opportunityType", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("address", "address", JoinType.INNER_JOIN) //
                .createAlias("address.domicile", "domicile", JoinType.INNER_JOIN) //
                .createAlias("closingDate", "closingDate", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.in("id", adverts)) //
                .addOrder(Order.desc("sequenceIdentifier")) //
                .setMaxResults(25) //
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class))
                .list();
    }

    public List<Integer> getFilteredAdverts(PrismScope scope, Collection<PrismState> activeStates, OpportunitiesQueryDTO queryDTO) {
        String resourceReference = scope.getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.categories.industries", "industry", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.categories.functions", "function", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.categories.themes", "theme", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.INNER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceCondition.partnerMode", true)) //
                                .add(Restrictions.eq("resourceCondition.actionCondition", queryDTO.getActionCondition()))); //

        boolean narrowed = queryDTO.isNarrowed();
        if (narrowed) {
            criteria.createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN, //
                    Restrictions.conjunction() //
                            .add(Restrictions.eq("advertTarget.selected", true))) //
                    .createAlias("advertTarget.advert", "targetAdvert", JoinType.LEFT_OUTER_JOIN);
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

        boolean projectScope = resourceClass.equals(Projection.class);
        if (projectScope) {
            criteria.createAlias(resourceReference + ".userRoles", "userRole", JoinType.LEFT_OUTER_JOIN,
                    Restrictions.in("userRole.role.id", asList(PROJECT_SUPERVISOR_GROUP)))
                    .createAlias("userRole.user", "user", JoinType.LEFT_OUTER_JOIN);
        }

        criteria.add(Restrictions.in("state.id", activeStates));

        appendLocationConstraint(criteria, queryDTO);
        appendKeywordConstraint(queryDTO, criteria);

        if (projectScope) {
            appendSupervisorConstraint(queryDTO, criteria);
        }

        appendIndustryConstraint(criteria, queryDTO);
        appendFunctionConstraint(criteria, queryDTO);

        appendOpportunityTypeConstraint(criteria, scope, queryDTO);
        if (opportunityScope) {
            appendStudyOptionConstraint(queryDTO, criteria);
        }

        appendFeeConstraint(criteria, queryDTO);
        appendPayConstraint(criteria, queryDTO);

        if (opportunityScope) {
            appendDurationConstraint(criteria, resourceReference, queryDTO);
        }

        if (narrowed) {
            appendResourcesConstraint(criteria, INSTITUTION, queryDTO.getInstitutions());
            appendResourcesConstraint(criteria, DEPARTMENT, queryDTO.getDepartments());
            appendResourcesConstraint(criteria, PROGRAM, queryDTO.getPrograms());
            appendResourcesConstraint(criteria, PROJECT, queryDTO.getProjects());
        } else {
            criteria.add(Restrictions.isNull("advertTarget.id"));
        }

        String lastSequenceIdentifier = queryDTO.getLastSequenceIdentifier();
        if (lastSequenceIdentifier != null) {
            criteria.add(Restrictions.lt(resourceReference + ".sequenceIdentifier", lastSequenceIdentifier));
        }

        return (List<Integer>) criteria.addOrder(Order.desc(resourceReference + ".sequenceIdentifier")) //
                .setMaxResults(25) //
                .list();
    }

    public List<AdvertStudyOptionDTO> getAdvertStudyOptions(PrismScope resourceScope, List<Integer> advertIds) {
        String resourceReference = resourceScope.getLowerCamelName();
        return (List<AdvertStudyOptionDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.groupProperty("studyOption.name").as("studyOption"))) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".instanceGroups", "instanceGroup", JoinType.INNER_JOIN) //
                .createAlias("instanceGroup.studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.in(resourceReference + ".id", advertIds)) //
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

    public List<String> getAdvertThemes(Advert advert) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(AdvertTheme.class) //
                .setProjection(Projections.property("value")) //
                .add(Restrictions.eq("advert", advert)) //
                .addOrder(Order.asc("value")) //
                .list();
    }

    public List<ImportedAdvertDomicile> getAdvertDomiciles() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedAdvertDomicile.class) //
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

    public void deleteAdvertTargetAdverts(Advert advert, List<Integer> newValues) {
        sessionFactory.getCurrentSession().createQuery(
                "delete AdvertTargetAdvert "
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

    public List<Integer> getAdvertTargetResources(Advert advert, PrismScope resourceScope, boolean selected) {
        String resourceReference = resourceScope.getLowerCamelName();
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTargetAdvert.class) //
                .setProjection(Projections.property(resourceReference + ".id")) //
                .createAlias("value", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.eq("selected", selected)) //
                .list();
    }

    public void endorseForAdvertTargets(Advert advert, PrismPartnershipState partnershipState) {
        sessionFactory.getCurrentSession().createQuery( //
                "update AdvertTargetAdvert "
                        + "set partnershipState = :partnershipState "
                        + "where selected = true") //
                .setParameter("partnershipState", partnershipState) //
                .executeUpdate();
    }

    public void endorseForAdvertTargets(Advert advert, List<Advert> targetAdverts, PrismPartnershipState partnershipState) {
        sessionFactory.getCurrentSession().createQuery( //
                "update AdvertTargetAdvert "
                        + "set partnershipState = :partnershipState "
                        + "where selected = true "
                        + "and value in (:values)") //
                .setParameter("partnershipState", partnershipState) //
                .setParameterList("values", targetAdverts) //
                .executeUpdate();
    }

    public void identifyForAdverts(User user, List<Integer> adverts) {
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

    public List<Integer> getAdvertSelectedTargetAdverts(Advert advert) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTargetAdvert.class) //
                .setProjection(Projections.property("value.id")) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.eq("selected", true)) //
                .list();
    }

    public List<Integer> getAdvertsToIdentifyUserFor(User user, List<Integer> adverts) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserAdvert.class) //
                .setProjection(Projections.property("advert.id")) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.in("advert.id", adverts)) //
                .add(Restrictions.eq("identified", false)) //
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
                    .add(Restrictions.like("theme.value", MatchMode.ANYWHERE))); //
        }
    }

    private void appendSupervisorConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        String keyword = queryDTO.getKeyword();
        if (keyword != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.like("user.firstName", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("user.lastName", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("user.email", keyword, MatchMode.ANYWHERE))); //
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
                opportunityTypes = Lists.newLinkedList();
                if (queryDTO.getOpportunityCategories() != null) {
                    for (PrismOpportunityCategory category : queryDTO.getOpportunityCategories()) {
                        for (PrismOpportunityType opportunityType : PrismOpportunityType.getOpportunityTypes(category)) {
                            opportunityTypes.add(opportunityType);
                        }
                    }
                }
            }

            Disjunction opportunityTypeConstraint = Restrictions.disjunction();
            for (PrismOpportunityType opportunityType : opportunityTypes) {
                opportunityTypeConstraint.add(Restrictions.eq("opportunityType.name", opportunityType.name()));

            }
            criteria.add(opportunityTypeConstraint);
        } else {
            List<PrismOpportunityCategory> opportunityCategories = queryDTO.getOpportunityCategories();
            if (CollectionUtils.isNotEmpty(opportunityCategories)) {
                Junction categoriesConstraint = Restrictions.disjunction();
                opportunityCategories.forEach(category -> {
                    categoriesConstraint.add(Restrictions.like("advert.opportunityCategories", category.name(), MatchMode.ANYWHERE));
                });
                criteria.add(categoriesConstraint);
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

    private void appendResourcesConstraint(Criteria criteria, PrismScope resourceScope, Integer[] resources) {
        if (resources != null) {
            Junction resourcesConstraint = Restrictions.disjunction();
            criteria.add(resourcesConstraint //
                    .add(Restrictions.in("advert." + resourceScope.getLowerCamelName() + ".id", resources))); //

            if (resourceScope.equals(DEPARTMENT)) {
                resourcesConstraint.add(Restrictions.conjunction() //
                        .add(Restrictions.in("targetAdvert.department.id", resources))
                        .add(Restrictions.isNull("targetAdvert.project"))
                        .add(Restrictions.isNull("targetAdvert.program")));
            } else if (resourceScope.equals(INSTITUTION)) {
                resourcesConstraint.add(Restrictions.conjunction() //
                        .add(Restrictions.in("targetAdvert.institution.id", resources))
                        .add(Restrictions.isNull("targetAdvert.project"))
                        .add(Restrictions.isNull("targetAdvert.program"))
                        .add(Restrictions.isNull("targetAdvert.department")));
            }
        }
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
