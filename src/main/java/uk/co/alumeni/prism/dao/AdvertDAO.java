package uk.co.alumeni.prism.dao;

import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.PrismConstants.COMMA;
import static uk.co.alumeni.prism.PrismConstants.SPACE;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.HOUR;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.getDurationUnitAsHours;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.STUDY;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.WORK;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.EMPLOYER;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.UNIVERSITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static uk.co.alumeni.prism.utils.PrismEnumUtils.values;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertAttribute;
import uk.co.alumeni.prism.domain.advert.AdvertLocation;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.advert.AdvertTargetPending;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;
import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.AdvertApplicationSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertDTO;
import uk.co.alumeni.prism.dto.AdvertFunctionDTO;
import uk.co.alumeni.prism.dto.AdvertIndustryDTO;
import uk.co.alumeni.prism.dto.AdvertLocationAddressPartSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertLocationDTO;
import uk.co.alumeni.prism.dto.AdvertPartnerActionDTO;
import uk.co.alumeni.prism.dto.AdvertStudyOptionDTO;
import uk.co.alumeni.prism.dto.AdvertTargetDTO;
import uk.co.alumeni.prism.dto.AdvertThemeDTO;
import uk.co.alumeni.prism.dto.AdvertUserDTO;
import uk.co.alumeni.prism.dto.EntityOpportunityCategoryDTO;
import uk.co.alumeni.prism.rest.dto.OpportunitiesQueryDTO;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

@Repository
@SuppressWarnings("unchecked")
public class AdvertDAO {

    @Inject
    private WorkflowDAO workflowDAO;

    @Inject
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
        return (List<AdvertDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("id").as("advertId"))
                        .add(Projections.property("user.firstName").as("userFirstName"))
                        .add(Projections.property("user.lastName").as("userLastName"))
                        .add(Projections.property("userAccount.linkedinProfileUrl").as("userAccountProfileUrl"))
                        .add(Projections.property("userAccount.linkedinImageUrl").as("userAccountImageUrl"))
                        .add(Projections.property("institution.id").as("institutionId"))
                        .add(Projections.property("institution.name").as("institutionName"))
                        .add(Projections.property("institution.logoImage.id").as("logoImageId"))
                        .add(Projections.property("department.id").as("departmentId"))
                        .add(Projections.property("department.name").as("departmentName"))
                        .add(Projections.property("program.id").as("programId"))
                        .add(Projections.property("program.name").as("programName"))
                        .add(Projections.property("program.availableDate").as("programAvailableDate"))
                        .add(Projections.property("program.durationMinimum").as("programDurationMinimum"))
                        .add(Projections.property("program.durationMaximum").as("programDurationMaximum"))
                        .add(Projections.property("project.id").as("projectId"))
                        .add(Projections.property("project.name").as("projectName"))
                        .add(Projections.property("project.availableDate").as("projectAvailableDate"))
                        .add(Projections.property("project.durationMinimum").as("projectDurationMinimum"))
                        .add(Projections.property("project.durationMaximum").as("projectDurationMaximum"))
                        .add(Projections.property("opportunityType.id").as("opportunityType"))
                        .add(Projections.property("targetOpportunityTypes").as("targetOpportunityTypes"))
                        .add(Projections.property("opportunityCategories").as("opportunityCategories"))
                        .add(Projections.property("name").as("name"))
                        .add(Projections.property("summary").as("summary"))
                        .add(Projections.property("description").as("description"))
                        .add(Projections.property("globallyVisible").as("globallyVisible"))
                        .add(Projections.property("homepage").as("homepage"))
                        .add(Projections.property("applyHomepage").as("applyHomepage"))
                        .add(Projections.property("telephone").as("telephone"))
                        .add(Projections.property("address.addressLine1").as("addressLine1"))
                        .add(Projections.property("address.addressLine2").as("addressLine2"))
                        .add(Projections.property("address.addressTown").as("addressTown"))
                        .add(Projections.property("address.addressRegion").as("addressRegion"))
                        .add(Projections.property("address.addressCode").as("addressCode"))
                        .add(Projections.property("address.domicile.id").as("addressDomicileId"))
                        .add(Projections.property("address.googleId").as("addressGoogleId"))
                        .add(Projections.property("address.addressCoordinates.latitude").as("addressCoordinateLatitude"))
                        .add(Projections.property("address.addressCoordinates.longitude").as("addressCoordinateLongitude"))
                        .add(Projections.property("pay.interval").as("payInterval"))
                        .add(Projections.property("pay.hoursWeekMinimum").as("payHoursWeekMinimum"))
                        .add(Projections.property("pay.hoursWeekMaximum").as("payHoursWeekMaximum"))
                        .add(Projections.property("pay.option").as("payOption"))
                        .add(Projections.property("pay.currency").as("payCurrency"))
                        .add(Projections.property("pay.minimum").as("payMaximum"))
                        .add(Projections.property("pay.maximum").as("payMinimum"))
                        .add(Projections.property("pay.benefit").as("payBenefit"))
                        .add(Projections.property("pay.benefitDescription").as("payBenefitDescription"))
                        .add(Projections.property("closingDate").as("closingDate"))
                        .add(Projections.countDistinct("application.id").as("applicationCount"))
                        .add(Projections.sum("application.applicationRatingCount").as("applicationRatingCount"))
                        .add(Projections.avg("application.applicationRatingAverage").as("applicationRatingAverage"))
                        .add(Projections.property("sequenceIdentifier").as("sequenceIdentifier")))
                .createAlias("user", "user", JoinType.LEFT_OUTER_JOIN)
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN)
                .createAlias("institution", "institution", JoinType.LEFT_OUTER_JOIN)
                .createAlias("department", "department", JoinType.LEFT_OUTER_JOIN)
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN)
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN)
                .createAlias("applications", "application", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.isNotNull("application.submittedTimestamp"))
                .createAlias("opportunityType", "opportunityType", JoinType.LEFT_OUTER_JOIN)
                .createAlias("address", "address", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.in("id", adverts))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class))
                .list();
    }

    public List<Integer> getUserAdverts(PrismScope scope, List<PrismState> states, Collection<Integer> userAdverts) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.institution", "institution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.in("institution.advert.id", userAdverts)) //
                        .add(Restrictions.in("department.advert.id", userAdverts)) //
                        .add(Restrictions.in("program.advert.id", userAdverts)) //
                        .add(Restrictions.in("project.advert.id", userAdverts))) //
                .list();
    }

    public List<EntityOpportunityCategoryDTO<?>> getVisibleAdverts(PrismScope scope, Collection<PrismState> states, PrismActionCondition actionCondition,
            Collection<Integer> nodeAdverts, Collection<Integer> userAdverts, OpportunitiesQueryDTO query) {
        ProjectionList projections = Projections.projectionList() //
                .add(Projections.groupProperty("advert.id").as("id")) //
                .add(Projections.property("resource.opportunityCategories").as("opportunityCategories"));

        if (scope.getScopeCategory().equals(OPPORTUNITY)) {
            projections.add(Projections.property("resource.opportunityType.id").as("opportunityType"));
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projections //
                        .add(Projections.property("advert.sequenceIdentifier").as("sequenceIdentifier"))) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.categories.industries", "industry", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.categories.functions", "function", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.categories.locations", "location", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("location.locationAdvert", "locationAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("locationAdvert.address", "locationAddress", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.INNER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceCondition.externalMode", true)) //
                                .add(Restrictions.eq("resourceCondition.actionCondition", actionCondition))); //

        for (PrismScope advertScope : WorkflowDAO.advertScopes) {
            String scopeReference = advertScope.getLowerCamelName();
            String advertReference = scopeReference + "Advert";
            criteria.createAlias("advert." + scopeReference, scopeReference, JoinType.LEFT_OUTER_JOIN)
                    .createAlias(scopeReference + ".advert", advertReference, JoinType.LEFT_OUTER_JOIN);
        }

        boolean opportunityRequest = ResourceOpportunity.class.isAssignableFrom(scope.getResourceClass());
        if (opportunityRequest) {
            criteria.createAlias("resource.opportunityType", "opportunityType", JoinType.INNER_JOIN) //
                    .createAlias("resource.resourceStudyOptions", "resourceStudyOption", JoinType.INNER_JOIN);
        }

        criteria.add(Restrictions.in("state.id", states));

        appendContextConstraint(criteria, query);

        if (isEmpty(nodeAdverts)) {
            appendVisibilityConstraint(criteria, userAdverts, isTrue(query.getRecommendation()));
        } else {
            appendNodeVisibilityConstraint(criteria, nodeAdverts, userAdverts, isTrue(query.getRecommendation()));
        }

        appendKeywordConstraint(query, criteria);
        appendLocationConstraint(criteria, query);
        appendTargetOpportunityTypeConstraint(criteria, query);

        appendIndustryConstraint(criteria, query);
        appendFunctionConstraint(criteria, query);

        if (opportunityRequest) {
            appendStudyOptionConstraint(query, criteria);
        }

        appendPayConstraint(criteria, query);

        if (opportunityRequest) {
            appendDurationConstraint(criteria, query);
        }

        return criteria.setResultTransformer(Transformers.aliasToBean(EntityOpportunityCategoryDTO.class))
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
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("action.actionCondition", "resourceCondition.actionCondition")) //
                .createAlias("action.creationScope", "creationScope", JoinType.INNER_JOIN) //
                .add(Restrictions.in("resource.id", resourceIds)) //
                .add(Restrictions.eq("resourceCondition.externalMode", true)) //
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .addOrder(Order.desc("id")) //
                .addOrder(Order.asc("creationScope.ordinal")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertPartnerActionDTO.class)) //
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

    public List<AdvertThemeDTO> getAdvertThemes(PrismScope resourceScope, Collection<Integer> resourceIds) {
        return (List<AdvertThemeDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.groupProperty("theme.name").as("theme"))) //
                .createAlias("categories.themes", "advertTheme", JoinType.INNER_JOIN) //
                .createAlias("advertTheme.theme", "theme", JoinType.INNER_JOIN)
                .add(Restrictions.in(resourceScope.getLowerCamelName() + ".id", resourceIds)) //
                .addOrder(Order.asc("id")) //
                .addOrder(Order.asc("theme.name"))
                .setResultTransformer(Transformers.aliasToBean(AdvertThemeDTO.class)) //
                .list();
    }

    public List<AdvertLocationDTO> getAdvertLocations(PrismScope resourceScope, Collection<Integer> resourceIds) {
        return (List<AdvertLocationDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("advertId")) //
                        .add(Projections.groupProperty("locationAdvert.id").as("locationAdvertId")) //
                        .add(Projections.max("locationAddressLocationPart.nameIndex").as("location"))) //
                .createAlias("categories.locations", "location", JoinType.INNER_JOIN) //
                .createAlias("location.locationAdvert", "locationAdvert", JoinType.INNER_JOIN) //
                .createAlias("locationAdvert.address", "locationAddress", JoinType.INNER_JOIN) //
                .createAlias("locationAddress.locations", "locationAddressLocation", JoinType.INNER_JOIN) //
                .createAlias("locationAddressLocation.locationPart", "locationAddressLocationPart", JoinType.INNER_JOIN) //
                .add(Restrictions.in(resourceScope.getLowerCamelName() + ".id", resourceIds)) //
                .addOrder(Order.asc("id")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertLocationDTO.class)) //
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

    public List<Integer> getAdvertsWithoutPayConversions(Institution institution, PrismScope scope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.address", "address", JoinType.INNER_JOIN) //
                .createAlias("address.domicile", "domicile", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource.institution", institution)) //
                .add(Restrictions.eqProperty("advert.pay.currency", "institution.currency")) //
                .list();
    }

    public List<Integer> getAdvertsWithElapsedPayConversions(PrismScope scope, LocalDate baseline) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.address", "address", JoinType.INNER_JOIN) //
                .createAlias("address.domicile", "domicile", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN, //
                        Restrictions.eq("action.creationScope.id", APPLICATION)) //
                .add(Restrictions.neProperty("advert.pay.currency", "domicile.currency")) //
                .add(Restrictions.lt("advert.pay.lastConversionDate", baseline)) //
                .add(Restrictions.isNotNull("action.id")) //
                .list();
    }

    public void deleteAdvertAttributes(Advert advert, Class<? extends AdvertAttribute> attributeClass) {
        sessionFactory.getCurrentSession().createQuery(
                "delete " + attributeClass.getSimpleName() + " "
                        + "where advert = :advert")
                .setParameter("advert", advert) //
                .executeUpdate();
    }

    public List<AdvertTargetDTO> getAdvertTargets(PrismScope resourceScope, String thisAdvertReference, String otherAdvertReference, User user,
            Collection<Integer> connectAdverts, Collection<Integer> manageAdverts) {
        Criterion visibilityConstraint = Restrictions.conjunction() //
                .add(Restrictions.eq("target.partnershipState", ENDORSEMENT_PROVIDED))
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("target.advertSevered", false))
                        .add(Restrictions.eq("target.targetAdvertSevered", false)));
        if (isNotEmpty(manageAdverts)) {
            visibilityConstraint = Restrictions.disjunction()
                    .add(visibilityConstraint) //
                    .add(Restrictions.conjunction() //
                            .add(Restrictions.in("thisAdvert.id", manageAdverts)) //
                            .add(Restrictions.eq(thisAdvertReference + "Severed", true)));
        }

        return (List<AdvertTargetDTO>) getAdvertTargetCriteria(resourceScope, thisAdvertReference, otherAdvertReference, user, false)
                .add(Restrictions.in("thisAdvert.id", connectAdverts))
                .add(visibilityConstraint) //
                .addOrder(Order.desc("thisUser.id")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertTargetDTO.class))
                .list();
    }

    public List<AdvertTargetDTO> getAdvertTargetsReceived(PrismScope resourceScope, String thisAdvertReference, String otherAdvertReference, User user,
            Collection<Integer> connectAdverts) {
        Criterion permissionsConstraint;
        if (user != null && isNotEmpty(connectAdverts)) {
            permissionsConstraint = Restrictions.disjunction() //
                    .add(getAdvertTargetAcceptUserConstraint(user))
                    .add(Restrictions.conjunction() //
                            .add(Restrictions.isNull("target.acceptAdvertUser.id"))
                            .add(Restrictions.in("target.acceptAdvert.id", connectAdverts)));
        } else if (user != null) {
            permissionsConstraint = getAdvertTargetAcceptUserConstraint(user);
        } else {
            permissionsConstraint = Restrictions.in("target.acceptAdvert.id", connectAdverts);
        }

        return (List<AdvertTargetDTO>) getAdvertTargetCriteria(resourceScope, thisAdvertReference, otherAdvertReference, user, true)
                .add(permissionsConstraint) //
                .add(Restrictions.eqProperty("thisAdvert.id", "target.acceptAdvert.id")) //
                .add(Restrictions.eq("target.partnershipState", ENDORSEMENT_PENDING)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("target.advertSevered", false))
                        .add(Restrictions.eq("target.targetAdvertSevered", false))) //
                .addOrder(Order.desc("thisUser.id")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertTargetDTO.class))
                .list();
    }

    public List<Advert> getAdvertsTargetsForWhichUserCanEndorse(Advert advert, User user, PrismScope scope, PrismScope targeterScope, PrismScope targetScope,
            List<Integer> targeterEntities) {
        return (List<Advert>) workflowDAO.getWorkflowCriteriaList(scope, targeterScope, targetScope, targeterEntities, Projections.groupProperty("targeterTarget.targetAdvert"))
                .add(Restrictions.eq("targeterTarget.advert", advert)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("targeterTarget.targetAdvertUser", user))
                        .add(Restrictions.eq("userRole.user", user))) //
                .add(Restrictions.in("stateAction.action.id",
                        asList("UNENDORSE", "REENDORSE").stream().map(a -> PrismAction.valueOf(scope.name() + "_" + a)).collect(toList())))
                .add(WorkflowDAO.getTargetActionConstraint())
                .list();
    }

    public <T> List<T> getAdvertsForWhichUserHasRoles(User user, PrismScope scope, Collection<PrismState> states, String[] roleExtensions, Collection<Integer> advertIds,
            boolean strict, Class<T> responseClass) {
        Projection projections;
        boolean integerResponse = responseClass.equals(Integer.class);
        if (integerResponse) {
            projections = Projections.groupProperty("advert.id");
        } else {
            projections = Projections.projectionList() //
                    .add(Projections.groupProperty("advert.id").as("advert")) //
                    .add(Projections.property("advert.opportunityCategories").as("opportunityCategories"));
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class, "advert") //
                .setProjection(projections);

        if (strict) {
            criteria.createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN,
                    Restrictions.eqProperty("advert.id", "resource.advert.id"));
        } else {
            criteria.createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN);
        }

        criteria.createAlias("resource.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resource.userRoles", "resourceUserRole", JoinType.INNER_JOIN)
                .add(Restrictions.in("resourceState.state.id", states))
                .add(Restrictions.eq("resourceUserRole.user", user)) //
                .add(Restrictions.in("resourceUserRole.role.id", values(PrismRole.class, scope, roleExtensions)));

        if (isNotEmpty(advertIds)) {
            criteria.add(Restrictions.in("advert.id", advertIds));
        }

        if (integerResponse) {
            return (List<T>) criteria.list();
        }

        return (List<T>) criteria //
                .setResultTransformer(Transformers.aliasToBean(responseClass)) //
                .list();
    }

    public List<Integer> getUserAdverts(User user, PrismScope scope, Collection<PrismState> states) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("resource.advert.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("role.verified", true)) //
                .list();
    }

    public List<Integer> getUserAdverts(User user, PrismScope scope, PrismScope advertScope, Collection<PrismState> states) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("advertResourceAdvert.id")) //
                .createAlias(advertScope.getLowerCamelName(), "advertResource", JoinType.INNER_JOIN) //
                .createAlias("advertResource.advert", "advertResourceAdvert", JoinType.INNER_JOIN) //
                .createAlias("advertResourceAdvert." + scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("role.verified", true)) //
                .list();
    }

    public List<Integer> getUserTargetAdverts(User user, PrismScope targetScope, PrismScope advertScope, Collection<PrismState> states) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("advertResourceAdvert.id")) //
                .createAlias(advertScope.getLowerCamelName(), "advertResource", JoinType.INNER_JOIN) //
                .createAlias("advertResource.advert", "advertResourceAdvert", JoinType.INNER_JOIN) //
                .createAlias("advertResourceAdvert.targets", "target", JoinType.INNER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource", JoinType.INNER_JOIN, //
                        Restrictions.eqProperty("targetResource.advert.id", "targetAdvert.id"))
                .createAlias("targetResource.userRoles", "targetUserRole", JoinType.INNER_JOIN) //
                .createAlias("targetUserRole.role", "targetRole", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.eq("target.partnershipState", ENDORSEMENT_PROVIDED)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("target.advertSevered", false))
                        .add(Restrictions.eq("target.targetAdvertSevered", false))) //
                .add(Restrictions.eq("targetUserRole.user", user)) //
                .add(Restrictions.eq("targetRole.verified", true)) //
                .list();
    }

    public List<Integer> getUserTargetAdverts(User user, PrismScope targeterScope, PrismScope targetScope, PrismScope advertScope, Collection<PrismState> states) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("advertResourceAdvert.id")) //
                .createAlias(advertScope.getLowerCamelName(), "advertResource", JoinType.INNER_JOIN) //
                .createAlias("advertResource.advert", "advertResourceAdvert", JoinType.INNER_JOIN) //
                .createAlias("advertResourceAdvert." + targeterScope.getLowerCamelName(), "targeterResource", JoinType.INNER_JOIN) //
                .createAlias("targeterResource.advert", "targeterAdvert", JoinType.INNER_JOIN) //
                .createAlias("targeterAdvert.targets", "target", JoinType.INNER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource", JoinType.INNER_JOIN, //
                        Restrictions.eqProperty("targetResource.advert.id", "targetAdvert.id"))
                .createAlias("targetResource.userRoles", "targetUserRole", JoinType.INNER_JOIN) //
                .createAlias("targetUserRole.role", "targetRole", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isEmpty("advertResourceAdvert.targets")) //
                .add(Restrictions.eq("target.partnershipState", ENDORSEMENT_PROVIDED)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("target.advertSevered", false))
                        .add(Restrictions.eq("target.targetAdvertSevered", false))) //
                .add(Restrictions.eq("targetUserRole.user", user)) //
                .add(Restrictions.eq("targetRole.verified", true)) //
                .list();
    }

    public List<Integer> getRevokedAdverts(Collection<Integer> userAdverts) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .add(Restrictions.in("acceptAdvert.id", userAdverts)) //
                .add(Restrictions.eq("partnershipState", ENDORSEMENT_REVOKED)) //
                .list();
    }

    public List<AdvertTarget> getAdvertTargetsForAdverts(Collection<Integer> adverts) {
        return (List<AdvertTarget>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.in("advert.id", adverts)) //
                        .add(Restrictions.in("targetAdvert.id", adverts))) //
                .list();
    }

    public List<Integer> getAdvertTargetsUserCanManage(User user, List<Integer> adverts) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.property("id"));

        if (isNotEmpty(adverts)) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.in("acceptAdvert.id", adverts)) //
                    .add(Restrictions.eq("acceptAdvertUser", user)));
        } else {
            criteria.add(Restrictions.eq("acceptAdvertUser", user));
        }

        return (List<Integer>) criteria.add(Restrictions.eq("acceptAdvertUser", user)) //
                .list();
    }

    public List<Integer> getAdvertTargetPendings() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTargetPending.class) //
                .setProjection(Projections.property("id")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<Integer> getAdvertsForTargets() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("advert.id")) ///
                .list();
    }

    public List<Integer> getAdvertsForTargets(User user, PrismScope targetScope) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .createAlias("targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource") //
                .createAlias("targetResource.userRoles", "targetUserRole", JoinType.INNER_JOIN) //
                .createAlias("targetUserRole.role", "targetRole", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("targetUserRole.user", user)) //
                .add(Restrictions.eq("targetRole.verified", true)) //
                .list();
    }

    public List<Integer> getAdvertsForEnclosedAdverts(PrismScope scope, Collection<PrismState> states, Collection<Integer> enclosedAdverts) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.groupProperty("resource.advert.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", enclosedAdverts)) //
                .add(Restrictions.in("resourceState.state.id", states)) //
                .list();
    }

    public List<Integer> getAdvertsForEnclosingResource(PrismScope scope, Integer resourceId, PrismScope advertScope, Collection<PrismState> states) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("resource.advert.id")) //
                .createAlias(advertScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource." + scope.getLowerCamelName() + ".id", resourceId)) //
                .add(Restrictions.in("state.id", states)) //
                .list();
    }

    public List<Integer> getAdvertsForTargetResource(PrismScope targeterScope, PrismScope resourceScope, Integer resourceId, PrismScope advertScope,
            Collection<PrismState> advertResourceStates) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("advertResource.advert.id")) //
                .createAlias("advert", "targeterAdvert", JoinType.INNER_JOIN) //
                .createAlias("targeterAdvert." + targeterScope.getLowerCamelName(), "targeterResource", JoinType.INNER_JOIN, //
                        Restrictions.eqProperty("targeterResource.advert.id", "targeterAdvert.id"))
                .createAlias("targeterResource." + advertScope.getLowerCamelName() + "s", "advertResource", JoinType.INNER_JOIN)
                .createAlias("advertResource.resourceStates", "advertResourceState", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + resourceScope.getLowerCamelName(), "targetResource", JoinType.INNER_JOIN, //
                        Restrictions.eqProperty("targetResource.advert.id", "targetAdvert.id")) //
                .add(Restrictions.in("advertResourceState.state.id", advertResourceStates)) //
                .add(Restrictions.eq("targetResource.id", resourceId)) //
                .list();
    }

    public List<Integer> getAdvertsForWhichUserIsTarget(User user, String advertProperty) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty(advertProperty + ".id")) //
                .add(Restrictions.eq("acceptAdvertUser", user)) //
                .add(Restrictions.neProperty(advertProperty, "acceptAdvert")) //
                .list();
    }

    public List<AdvertTarget> getAdvertTargetAdmin(AdvertTarget advertTarget) {
        return (List<AdvertTarget>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .add(Restrictions.eq("advert", advertTarget.getAdvert())) //
                .add(Restrictions.eq("targetAdvert", advertTarget.getTargetAdvert())) //
                .add(Restrictions.isNull("acceptAdvertUser")) //
                .list();
    }

    public void updateAdvertTargetGroup(AdvertTarget advertTarget, Collection<String> properties, Boolean severed) {
        Set<String> updates = Sets.newHashSet();
        for (String property : properties) {
            updates.add("set " + property + "Severed = :severed");
        }

        sessionFactory.getCurrentSession().createQuery( //
                "update AdvertTarget " //
                        + Joiner.on(COMMA + SPACE).join(updates) + " " //
                        + "where advert = :advert " //
                        + "and targetAdvert = :targetAdvert") //
                .setParameter("severed", severed) //
                .setParameter("advert", advertTarget.getAdvert()) //
                .setParameter("targetAdvert", advertTarget.getTargetAdvert()) //
                .executeUpdate();
    }

    public AdvertTarget getAdvertTargetAccept(AdvertTarget advertTarget, User acceptUser) {
        return (AdvertTarget) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .add(Restrictions.eq("advert", advertTarget.getAdvert())) //
                .add(Restrictions.eq("targetAdvert", advertTarget.getTargetAdvert())) //
                .add(Restrictions.eq("acceptAdvertUser", acceptUser)) //
                .uniqueResult();
    }

    public List<AdvertUserDTO> getAdvertUsers(PrismScope scope, Collection<Integer> adverts) {
        return (List<AdvertUserDTO>) sessionFactory.getCurrentSession().createCriteria(scope.getResourceClass()) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("advert.id").as("advertId")) //
                        .add(Projections.property("user.id").as("userId")) //
                        .add(Projections.property("user.firstName").as("userFirstName")) //
                        .add(Projections.property("user.lastName").as("userLastName")) //
                        .add(Projections.property("user.email").as("userEmail")) //
                        .add(Projections.property("userAccount.linkedinProfileUrl").as("userLinkedinProfileUrl")) //
                        .add(Projections.property("userAccount.linkedinImageUrl").as("userLinkedinImageUrl")) //
                        .add(Projections.property("userAccount.portraitImage.id").as("userPortraitImageId")))
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.in("advert.id", adverts)) //
                .setResultTransformer(Transformers.aliasToBean(AdvertUserDTO.class)) //
                .list();
    }

    public List<AdvertTarget> getActiveAdvertTargets(List<Integer> advertTargets) {
        return (List<AdvertTarget>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .add(Restrictions.in("id", advertTargets)) //
                .add(Restrictions.eq("partnershipState", PrismPartnershipState.ENDORSEMENT_PROVIDED)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("advertSevered", false))
                        .add(Restrictions.eq("targetAdvertSevered", false))) //
                .list();
    }

    public List<AdvertTarget> getCustomAdvertTargets(Advert advert) {
        return (List<AdvertTarget>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("advert", advert)) //
                        .add(Restrictions.eq("targetAdvert", advert))) //
                .add(Restrictions.eq("partnershipState", PrismPartnershipState.ENDORSEMENT_PROVIDED)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("advertSevered", false))
                        .add(Restrictions.eq("targetAdvertSevered", false))) //
                .list();
    }

    public void deleteCustomAdvertTargets(Advert advert) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete AdvertTarget " //
                        + "where advert = :advert " //
                        + "or targetAdvert = :advert") //
                .setParameter("advert", advert) //
                .executeUpdate();
    }

    public void updateAdvertPayCurrency(List<Integer> adverts, String currency) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Advert " //
                        + "set pay.currency = :currency, " //
                        + "pay.lastConversionDate = null " //
                        + "where id in (:adverts)") //
                .setParameter("currency", currency) //
                .setParameterList("adverts", adverts) //
                .executeUpdate();
    }

    public List<Advert> getPossibleAdvertLocations(Advert advert, Collection<Advert> exclusions) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdvertLocation.class) //
                .setProjection(Projections.groupProperty("locationAdvert")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("locationAdvert", "locationAdvert", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("advert.institution", advert.getInstitution())) //
                        .add(Restrictions.eq("advert.department", advert.getDepartment())) //
                        .add(Restrictions.eq("advert.program", advert.getProgram())) //
                        .add(Restrictions.eq("advert.project", advert.getProject())));

        if (isNotEmpty(exclusions)) {
            criteria.add(Restrictions.not( //
                    Restrictions.in("locationAdvert", exclusions)));
        }

        return (List<Advert>) criteria.addOrder(Order.asc("locationAdvert.name")) //
                .list(); //
    }

    public List<AdvertLocationAddressPartSummaryDTO> getAdvertLocationSummaries(PrismScope scope, Collection<Integer> userAdverts, String searchTerm) {
        Criterion networkConstraint;
        if (isNotEmpty(userAdverts)) {
            networkConstraint = Restrictions.disjunction() //
                    .add(Restrictions.eq("advert.globallyVisible", true)) //
                    .add(Restrictions.in("advert.id", userAdverts));
        } else {
            networkConstraint = Restrictions.eq("advert.globallyVisible", true);
        }

        return (List<AdvertLocationAddressPartSummaryDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id").as("id"))
                        .add(Projections.property("parent.id").as("parentId")) //
                        .add(Projections.property("name").as("name")) //
                        .add(Projections.countDistinct("advert.id").as("advertCount"))) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceConditions", "resourceCondition", JoinType.INNER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("resourceCondition.externalMode", true)) //
                                .add(Restrictions.eq("resourceCondition.actionCondition", ACCEPT_APPLICATION)))
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.categories.locations", "advertLocation", JoinType.INNER_JOIN) //
                .createAlias("advertLocation.locationAdvert", "locationAdvert", JoinType.INNER_JOIN) //
                .createAlias("locationAdvert.address", "locationAddress", JoinType.INNER_JOIN) //
                .createAlias("locationAddress.addressLocationParts", "addressLocationPart", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(networkConstraint) //
                .add(Restrictions.like("addressLocationPart.name", searchTerm)) //
                .add(Restrictions.eq("stateAction.action.id", PrismAction.valueOf(scope.name() + "_CREATE_APPLICATION"))) //
                .addOrder(Order.asc("addressLocationPart.nameIndex")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertLocationAddressPartSummaryDTO.class)) //
                .list();
    }

    private void appendContextConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        PrismResourceContext context = queryDTO.getContext();
        if (context != null) {
            if (context.equals(EMPLOYER)) {
                criteria.add(Restrictions.disjunction() //
                        .add(Restrictions.like("advert.opportunityCategories", EXPERIENCE.name(), MatchMode.ANYWHERE)) //
                        .add(Restrictions.like("advert.opportunityCategories", WORK.name(), MatchMode.ANYWHERE)));
            } else if (context.equals(UNIVERSITY)) {
                criteria.add(Restrictions.disjunction() //
                        .add(Restrictions.like("advert.opportunityCategories", STUDY.name(), MatchMode.ANYWHERE)));
            }
        }
    }

    private void appendVisibilityConstraint(Criteria criteria, Collection<Integer> userAdverts, boolean recommendation) {
        if (recommendation) {
            criteria.add(Restrictions.in("advert.id", userAdverts));
        } else if (isNotEmpty(userAdverts)) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.eq("advert.globallyVisible", true))
                    .add(Restrictions.in("advert.id", userAdverts)));
        } else {
            criteria.add(Restrictions.eq("advert.globallyVisible", true));
        }
    }

    private void appendNodeVisibilityConstraint(Criteria criteria, Collection<Integer> nodeAdverts, Collection<Integer> userAdverts, boolean recommendation) {
        criteria.add(Restrictions.in("advert.id", nodeAdverts));
        appendVisibilityConstraint(criteria, userAdverts, recommendation);
    }

    private void appendLocationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        if (queryDTO.getNeLat() != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.conjunction() //
                            .add(Restrictions.between("address.addressCoordinates.latitude", queryDTO.getSwLat(), queryDTO.getNeLat()))
                            .add(Restrictions.between("address.addressCoordinates.longitude", queryDTO.getSwLon(), queryDTO.getNeLon())))
                    .add(Restrictions.conjunction() //
                            .add(Restrictions.between("locationAddress.addressCoordinates.latitude", queryDTO.getSwLat(), queryDTO.getNeLat()))
                            .add(Restrictions.between("locationAddress.addressCoordinates.longitude", queryDTO.getSwLon(), queryDTO.getNeLon()))));
        }
    }

    private void appendKeywordConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        String keyword = queryDTO.getKeyword();
        if (keyword != null) {
            Junction constraint = Restrictions.disjunction();
            for (PrismScope scope : WorkflowDAO.advertScopes) {
                String scopeReference = scope.getLowerCamelName();
                constraint.add(Restrictions.like(scopeReference + "Advert.name", keyword, MatchMode.ANYWHERE)) //
                        .add(Restrictions.like(scopeReference + "Advert.summary", keyword, MatchMode.ANYWHERE)) //
                        .add(Restrictions.like(scopeReference + "Advert.description", keyword, MatchMode.ANYWHERE));
            }
            criteria.add(constraint);
        }
    }

    private void appendIndustryConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        List<PrismAdvertIndustry> industries = queryDTO.getIndustries();
        if (CollectionUtils.isNotEmpty(industries)) {
            criteria.add(Restrictions.in("industry.industry", industries));
        }
    }

    private void appendFunctionConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        List<PrismAdvertFunction> functions = queryDTO.getFunctions();
        if (CollectionUtils.isNotEmpty(functions)) {
            criteria.add(Restrictions.in("function.function", functions));
        }
    }

    private void appendTargetOpportunityTypeConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        List<PrismOpportunityType> targetOpportunityTypes = queryDTO.getTargetOpportunityTypes();
        if (isNotEmpty(targetOpportunityTypes)) {
            Disjunction constraint = Restrictions.disjunction();
            for (PrismOpportunityType targetOpportunityType : targetOpportunityTypes) {
                String targetOpportunityTypeName = targetOpportunityType.name();
                constraint.add(Restrictions.eq("advert.targetOpportunityTypes", targetOpportunityTypeName))
                        .add(Restrictions.like("advert.targetOpportunityTypes", targetOpportunityTypeName + "|", MatchMode.START))
                        .add(Restrictions.like("advert.targetOpportunityTypes", "|" + targetOpportunityTypeName + "|", MatchMode.ANYWHERE))
                        .add(Restrictions.like("advert.targetOpportunityTypes", "|" + targetOpportunityTypeName, MatchMode.END));

            }
            criteria.add(constraint);
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
        PrismDurationUnit interval = queryDTO.getSalaryInterval();
        if (interval != null) {
            BigDecimal durationAsHours = new BigDecimal(getDurationUnitAsHours(interval));
            BigDecimal minSalary = queryDTO.getMinSalary().divide(durationAsHours, 2, HALF_UP);
            BigDecimal maxSalary = queryDTO.getMaxSalary().divide(durationAsHours, 2, HALF_UP);
            if (!interval.equals(HOUR)) {
                appendRangeConstraint(criteria, "advert.pay.minimumNormalized", "advert.pay.maximumNormalized", minSalary, maxSalary);
            } else {
                appendRangeConstraint(criteria, "advert.pay.minimumNormalizedHour", "advert.pay.maximumNormalizedHour", minSalary, maxSalary);
            }
        }
    }

    private void appendDurationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        appendRangeConstraint(criteria, "resource.durationMinimum", "resource.durationMaximum", queryDTO.getMinDuration(), queryDTO.getMaxDuration());
    }

    private void appendRangeConstraint(Criteria criteria, String loColumn, String hiColumn, Number loValue, Number hiValue) {
        Junction constraint = buildRangeConstraint(loColumn, hiColumn, loValue, hiValue);
        if (constraint != null) {
            criteria.add(constraint);
        }
    }

    private Junction buildRangeConstraint(String loColumn, String hiColumn, Number loValue, Number hiValue) {
        if ((loValue == null || loValue.intValue() == 0) && (hiValue == null || hiValue.intValue() == 0)) {
            return null;
        }

        Junction conjunction = Restrictions.conjunction();
        if (loValue != null) {
            conjunction.add(Restrictions.ge(loColumn, hiValue != null && hiValue.intValue() < loValue.intValue() ? hiValue : loValue));
        }

        if (hiValue != null) {
            conjunction.add(Restrictions.le(hiColumn, loValue != null && loValue.intValue() > hiValue.intValue() ? loValue : hiValue));
        }
        return conjunction;
    }

    private Criteria getAdvertTargetCriteria(PrismScope resourceScope, String thisAdvertReference, String otherAdvertReference, User user, boolean received) {
        ProjectionList projections = Projections.projectionList() //
                .add(Projections.property("target.id").as("id")) //
                .add(Projections.groupProperty("thisAdvert.id").as("thisAdvertId")) //
                .add(Projections.property(thisAdvertReference + "Severed").as("thisAdvertSevered")) //
                .add(Projections.groupProperty("thisInstitution.id").as("thisInstitutionId")) //
                .add(Projections.property("thisInstitution.name").as("thisInstitutionName")) //
                .add(Projections.property("thisInstitution.logoImage.id").as("thisLogoImageId")) //
                .add(Projections.groupProperty("thisDepartment.id").as("thisDepartmentId")) //
                .add(Projections.property("thisDepartment.name").as("thisDepartmentName")) //
                .add(Projections.groupProperty("otherAdvert.id").as("otherAdvertId")) //
                .add(Projections.property(otherAdvertReference + "Severed").as("otherAdvertSevered")) //
                .add(Projections.groupProperty("otherInstitution.id").as("otherInstitutionId")) //
                .add(Projections.property("otherInstitution.name").as("otherInstitutionName")) //
                .add(Projections.property("otherInstitution.logoImage.id").as("otherLogoImageId")) //
                .add(Projections.property("otherInstitutionAdvert.backgroundImage.id").as("otherInstitutionBackgroundImageId")) //
                .add(Projections.groupProperty("otherDepartment.id").as("otherDepartmentId")) //
                .add(Projections.property("otherDepartment.name").as("otherDepartmentName"))
                .add(Projections.property("otherDepartmentAdvert.backgroundImage.id").as("otherDepartmentBackgroundImageId"));

        if (received) {
            projections.add(Projections.groupProperty("otherUser.id").as("otherUserId")) //
                    .add(Projections.property("otherUser.firstName").as("otherUserFirstName")) //
                    .add(Projections.property("otherUser.lastName").as("otherUserLastName")) //
                    .add(Projections.property("otherUser.email").as("otherUserEmail")) //
                    .add(Projections.property("otherUserAccount.linkedinProfileUrl").as("otherUserLinkedinProfileUrl")) //
                    .add(Projections.property("otherUserAccount.linkedinImageUrl").as("otherUserLinkedinImageUrl")) //
                    .add(Projections.property("otherUserAccount.portraitImage.id").as("otherUserPortraitImageId"));
        }

        projections.add(Projections.property("target.partnershipState").as("partnershipState"));

        return sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projections) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.INNER_JOIN) //
                .createAlias(thisAdvertReference, "thisAdvert", JoinType.INNER_JOIN) //
                .createAlias("thisAdvert.institution", "thisInstitution", JoinType.INNER_JOIN) //
                .createAlias("thisAdvert.department", "thisDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(thisAdvertReference + "User", "thisUser", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("thisUser.userRoles", "thisUserRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("thisUserRole.role", "thisRole", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(otherAdvertReference, "otherAdvert", JoinType.INNER_JOIN) //
                .createAlias("otherAdvert.institution", "otherInstitution", JoinType.INNER_JOIN) //
                .createAlias("otherInstitution.advert", "otherInstitutionAdvert", JoinType.LEFT_OUTER_JOIN)
                .createAlias("otherAdvert.department", "otherDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("otherDepartment.advert", "otherDepartmentAdvert", JoinType.LEFT_OUTER_JOIN)
                .createAlias(otherAdvertReference + "User", "otherUser", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("otherUser.userAccount", "otherUserAccount", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.neProperty("thisAdvert.id", "otherAdvert.id")) //
                .add(WorkflowDAO.getResourceParentManageableStateConstraint(resourceScope));
    }

    private Junction getAdvertTargetAcceptUserConstraint(User user) {
        Junction constraint = Restrictions.conjunction()
                .add(Restrictions.eq("thisRole.verified", true)) //
                .add(Restrictions.ne("thisRole.roleCategory", STUDENT));

        if (user != null) {
            constraint.add(Restrictions.eq("target.acceptAdvertUser.id", user.getId()));
        }

        return constraint.add(Restrictions.disjunction() //
                .add(Restrictions.eqProperty("thisDepartment.id", "thisUserRole.department.id"))
                .add(Restrictions.eqProperty("thisInstitution.id", "thisUserRole.institution.id")));
    }

}
