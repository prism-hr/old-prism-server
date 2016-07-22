package uk.co.alumeni.prism.dao;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static uk.co.alumeni.prism.PrismConstants.COMMA;
import static uk.co.alumeni.prism.PrismConstants.SPACE;
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getMatchingFlattenedPropertyConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getResolvedAliasReference;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getResourceParentManageableStateConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getTargetActionConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.organizationScopes;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.HOUR;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.getDurationUnitAsHours;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.STUDY;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.WORK;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.EMPLOYER;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.UNIVERSITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.utils.PrismEnumUtils.getSimilar;
import static uk.co.alumeni.prism.utils.PrismEnumUtils.values;
import static uk.co.alumeni.prism.utils.PrismIterableUtils.noneNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
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
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertAttribute;
import uk.co.alumeni.prism.domain.advert.AdvertLocation;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.advert.AdvertTargetPending;
import uk.co.alumeni.prism.domain.advert.AdvertTheme;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;
import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.AdvertApplicationDTO;
import uk.co.alumeni.prism.dto.AdvertApplicationSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertCategoryDTO;
import uk.co.alumeni.prism.dto.AdvertDTO;
import uk.co.alumeni.prism.dto.AdvertFunctionDTO;
import uk.co.alumeni.prism.dto.AdvertFunctionSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertIndustryDTO;
import uk.co.alumeni.prism.dto.AdvertIndustrySummaryDTO;
import uk.co.alumeni.prism.dto.AdvertInstitutionSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertLocationDTO;
import uk.co.alumeni.prism.dto.AdvertLocationSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertOpportunityCategoryDTO;
import uk.co.alumeni.prism.dto.AdvertPartnerActionDTO;
import uk.co.alumeni.prism.dto.AdvertStudyOptionDTO;
import uk.co.alumeni.prism.dto.AdvertTargetAdvertDTO;
import uk.co.alumeni.prism.dto.AdvertTargetDTO;
import uk.co.alumeni.prism.dto.AdvertThemeDTO;
import uk.co.alumeni.prism.dto.AdvertThemeSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertUserDTO;
import uk.co.alumeni.prism.dto.UserAdvertDTO;
import uk.co.alumeni.prism.rest.dto.OpportunityQueryDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertThemeRepresentation;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
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

    public List<Integer> getAdverts(PrismScope resourceScope, Collection<Integer> resourceIds) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .add(Restrictions.in("id", resourceIds)) //
                .list();
    }

    public List<Integer> getEnclosingAdverts(PrismScope enclosingScope, PrismScope resourceScope, Collection<Integer> advertIds) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(resourceScope.getResourceClass()) //
                .setProjection(Projections.groupProperty("enclosingResource.advert.id")) //
                .createAlias(enclosingScope.getLowerCamelName(), "enclosingResource", JoinType.INNER_JOIN) //
                .add(Restrictions.in("advert.id", advertIds)) //
                .list();
    }

    public List<AdvertDTO> getAdverts(OpportunityQueryDTO query, Collection<Integer> adverts) {
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
                        .add(Projections.property("project.id").as("projectId"))
                        .add(Projections.property("project.name").as("projectName"))
                        .add(Projections.property("opportunityType.id").as("opportunityType"))
                        .add(Projections.property("opportunityCategories").as("opportunityCategories"))
                        .add(Projections.property("studyOptions").as("studyOptions"))
                        .add(Projections.property("name").as("name"))
                        .add(Projections.property("summary").as("summary"))
                        .add(Projections.property("description").as("description"))
                        .add(Projections.property("globallyVisible").as("globallyVisible"))
                        .add(Projections.property("published").as("published"))
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
                        .add(Projections.property("durationMinimum").as("durationMinimum"))
                        .add(Projections.property("durationMaximum").as("durationMaximum"))
                        .add(Projections.property("pay.interval").as("payInterval"))
                        .add(Projections.property("pay.hoursWeekMinimum").as("payHoursWeekMinimum"))
                        .add(Projections.property("pay.hoursWeekMaximum").as("payHoursWeekMaximum"))
                        .add(Projections.property("pay.option").as("payOption"))
                        .add(Projections.property("pay.currency").as("payCurrency"))
                        .add(Projections.property("pay.minimum").as("payMinimum"))
                        .add(Projections.property("pay.maximum").as("payMaximum"))
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

    public List<AdvertOpportunityCategoryDTO> getVisibleAdverts(Collection<PrismScope> scopes, Collection<Integer> nodeAdverts, UserAdvertDTO userAdvertDTO,
            OpportunityQueryDTO query) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("id").as("id")) //
                        .add(Projections.property("globallyVisible").as("globallyVisible")) //
                        .add(Projections.property("institution.id").as("institutionId")) //
                        .add(Projections.property("institution.name").as("institutionName")) //
                        .add(Projections.property("institution.logoImage.id").as("institutionLogoImageId")) //
                        .add(Projections.property("opportunityCategories").as("opportunityCategories")) //
                        .add(Projections.property("opportunityType.id").as("opportunityType")) //
                        .add(Projections.property("sequenceIdentifier").as("sequenceIdentifier"))) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN);

        stream(advertScopes).forEach(advertScope -> {
            String advertReference = advertScope.getLowerCamelName() + "Advert";
            criteria.createAlias(advertReference, advertReference, JoinType.LEFT_OUTER_JOIN);
        });

        criteria.createAlias("categories.industries", "industry", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("categories.functions", "function", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("categories.themes", "theme", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("categories.locations", "location", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("location.locationAdvert", "locationAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("locationAdvert.address", "locationAddress", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("locationAddress.locations", "locationAddressLocation", JoinType.LEFT_OUTER_JOIN); //

        appendScopeConstraint(criteria, scopes);
        appendContextConstraint(criteria, query);
        appendKeywordConstraint(query, criteria);
        appendIndustryConstraint(criteria, query);
        appendFunctionConstraint(criteria, query);
        appendThemeConstraint(criteria, query);
        appendLocationConstraint(criteria, query);
        appendStudyOptionConstraint(query, criteria);
        appendPayConstraint(criteria, query);
        appendDurationConstraint(criteria, query);
        appendInstitutionConstraint(criteria, query);

        appendVisibilityConstraint(criteria, query, userAdvertDTO, nodeAdverts);
        return (List<AdvertOpportunityCategoryDTO>) criteria //
                .setResultTransformer(Transformers.aliasToBean(AdvertOpportunityCategoryDTO.class)) //
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
                .addOrder(Order.asc("resourceStudyOption.studyOption")) //
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
                .add(Restrictions.eq("target.advertSevered", false))
                .add(Restrictions.eq("target.targetAdvertSevered", false));
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
        return (List<Advert>) workflowDAO
                .getWorkflowCriteriaList(scope, targeterScope, targetScope, targeterEntities, Projections.groupProperty("targeterTarget.targetAdvert"))
                .add(Restrictions.eq("targeterTarget.advert", advert)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("targeterTarget.targetAdvertUser", user))
                        .add(Restrictions.eq("userRole.user", user))) //
                .add(Restrictions.in("stateAction.action.id",
                        asList("UNENDORSE", "REENDORSE").stream().map(a -> PrismAction.valueOf(scope.name() + "_" + a)).collect(toList())))
                .add(getTargetActionConstraint())
                .list();
    }

    public List<AdvertCategoryDTO> getAdvertsForWhichUserHasRoles(User user, PrismScope scope, String[] roleExtensions, Collection<Integer> advertIds) {
        ProjectionList projections;
        boolean hasRoleExtensions = isNotEmpty(roleExtensions);
        if (hasRoleExtensions) {
            projections = Projections.projectionList() //
                    .add(Projections.groupProperty("id").as("advert")) //
                    .add(Projections.property("opportunityCategories").as("opportunityCategories"));
        } else {
            projections = Projections.projectionList() //
                    .add(Projections.property("id").as("advert")) //
                    .add(Projections.property("opportunityCategories").as("opportunityCategories")) //
                    .add(Projections.property("userRole.role.id").as("role"));
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class, "advert") //
                .setProjection(projections)
                .createAlias("advert." + scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("advert.id", "resource.advert.id"));

        criteria.createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN)
                .add(Restrictions.eq("advert.published", true)) //
                .add(Restrictions.eq("userRole.user", user));

        if (hasRoleExtensions) {
            criteria.add(Restrictions.in("userRole.role.id", values(PrismRole.class, scope, roleExtensions)));
        }

        if (isNotEmpty(advertIds)) {
            criteria.add(Restrictions.in("advert.id", advertIds));
        }

        return (List<AdvertCategoryDTO>) criteria //
                .setResultTransformer(Transformers.aliasToBean(AdvertCategoryDTO.class)) //
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

    public List<Integer> getResourceAdverts(PrismScope scope, Integer resourceId, PrismScope[] displayScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.groupProperty("id")) //
                .add(Restrictions.eq(scope.getLowerCamelName() + ".id", resourceId));
        appendVisibleAdvertConstraint(criteria, EMPTY, displayScopes);

        return criteria.list();
    }

    public List<Integer> getResourceAdvertsTargeted(PrismScope resourceScope, Integer resourceId, PrismScope[] displayScopes) {
        Set<Integer> adverts = newHashSet();
        Criteria advertCriteria = sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.property("advert.id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert", "targetAdvert", JoinType.INNER_JOIN);
        appendVisibleAdvertConstraint(advertCriteria, "advert", displayScopes);

        adverts.addAll((List<Integer>) advertCriteria
                .add(Restrictions.eq("partnershipState", ENDORSEMENT_PROVIDED)) //
                .add(Restrictions.eq("targetAdvert." + resourceScope.getLowerCamelName() + ".id", resourceId)) //
                .list());

        stream(WorkflowDAO.organizationScopes).forEach(organizationScope -> {
            Criteria advertCriteriaTarget = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                    .setProjection(Projections.property("id")) //
                    .createAlias(organizationScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                    .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                    .createAlias("advert.targets", "target", JoinType.INNER_JOIN) //
                    .createAlias("target.targetAdvert", "targetAdvert", JoinType.INNER_JOIN);
            appendVisibleAdvertConstraint(advertCriteriaTarget, EMPTY, displayScopes);

            adverts.addAll((List<Integer>) advertCriteriaTarget
                    .add(Restrictions.eq("target.partnershipState", ENDORSEMENT_PROVIDED)) //
                    .add(Restrictions.eq("target.advertSevered", false)) //
                    .add(Restrictions.eq("target.targetAdvertSevered", false)) //
                    .add(Restrictions.eq("targetAdvert." + resourceScope.getLowerCamelName() + ".id", resourceId)) //
                    .list());
        });

        return newArrayList(adverts);
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

    public void deleteAdvertTargets(Advert advert) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete AdvertTarget " //
                        + "where advert = :advert " //
                        + "or targetAdvert = :advert") //
                .setParameter("advert", advert) //
                .executeUpdate();
    }

    public void deleteAdvertTargets(Advert advert, Collection<Advert> targetAdverts, PrismPartnershipState partnershipState) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete AdvertTarget " //
                        + "where advert = :advert " //
                        + "and targetAdvert in (:targetAdverts) "
                        + "and partnershipState = :partnershipState") //
                .setParameter("advert", advert) //
                .setParameterList("targetAdverts", targetAdverts) //
                .setParameter("partnershipState", partnershipState) //
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

    public List<Advert> getPossibleAdvertLocations(Advert advert, PrismScope locationScope, Collection<Advert> exclusions) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdvertLocation.class) //
                .setProjection(Projections.groupProperty("locationAdvert")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("locationAdvert", "locationAdvert", JoinType.INNER_JOIN) //
                .createAlias("locationAdvert." + locationScope.getLowerCamelName(), "locationResource", JoinType.INNER_JOIN, //
                        Restrictions.eqProperty("locationResource.advert.id", "locationAdvert.id")) //
                .createAlias("locationResource.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("advert.institution", advert.getInstitution())) //
                        .add(Restrictions.eq("advert.department", advert.getDepartment())) //
                        .add(Restrictions.eq("advert.program", advert.getProgram())) //
                        .add(Restrictions.eq("advert.project", advert.getProject()))) //
                .add(getResourceParentManageableStateConstraint("state"));

        if (isNotEmpty(exclusions)) {
            criteria.add(Restrictions.not( //
                    Restrictions.in("locationAdvert", exclusions)));
        }

        return (List<Advert>) criteria.addOrder(Order.asc("locationAdvert.name")) //
                .list(); //
    }

    public List<AdvertIndustrySummaryDTO> getAdvertIndustrySummaries(List<Integer> visibleAdverts, String searchTerm) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("industry.industry").as("industry"))
                        .add(Projections.countDistinct("id").as("advertCount"))) //
                .createAlias("categories.industries", "industry", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", visibleAdverts));

        if (isNotEmpty(searchTerm)) {
            List<PrismAdvertIndustry> matchingValues = getSimilar(PrismAdvertIndustry.class, searchTerm);
            if (matchingValues.size() > 0) {
                criteria.add(Restrictions.in("industry.industry", matchingValues));
            } else {
                criteria.add(Restrictions.eq("id", 0));
            }
        }

        return (List<AdvertIndustrySummaryDTO>) criteria.addOrder(Order.asc("industry.industry")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertIndustrySummaryDTO.class)) //
                .list();
    }

    public List<AdvertFunctionSummaryDTO> getAdvertFunctionSummaries(List<Integer> visibleAdverts, String searchTerm) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("function.function").as("function"))
                        .add(Projections.countDistinct("id").as("advertCount"))) //
                .createAlias("categories.functions", "function", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", visibleAdverts));

        if (isNotEmpty(searchTerm)) {
            List<PrismAdvertIndustry> matchingValues = getSimilar(PrismAdvertIndustry.class, searchTerm);
            if (matchingValues.size() > 0) {
                criteria.add(Restrictions.in("function.function", matchingValues));
            } else {
                criteria.add(Restrictions.eq("id", 0));
            }
        }

        return (List<AdvertFunctionSummaryDTO>) criteria.addOrder(Order.asc("function.function")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertFunctionSummaryDTO.class)) //
                .list();
    }

    public List<AdvertThemeSummaryDTO> getAdvertThemeSummaries(List<Integer> visibleAdverts, String searchTerm) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("theme.id").as("id")) //
                        .add(Projections.property("theme.name").as("name"))
                        .add(Projections.countDistinct("id").as("advertCount"))) //
                .createAlias("categories.themes", "advertTheme", JoinType.INNER_JOIN) //
                .createAlias("advertTheme.theme", "theme", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", visibleAdverts));

        if (isNotEmpty(searchTerm)) {
            criteria.add(Restrictions.like("theme.name", searchTerm, MatchMode.ANYWHERE));
        }

        return (List<AdvertThemeSummaryDTO>) criteria.addOrder(Order.asc("theme.name")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertThemeSummaryDTO.class)) //
                .list();
    }

    public List<AdvertLocationSummaryDTO> getAdvertLocationSummaries(List<Integer> visibleAdverts, String searchTerm) {
        Criteria criteria = getAdvertLocationSummaryCriteria(visibleAdverts);

        if (isNotEmpty(searchTerm)) {
            criteria.add(Restrictions.like("locationPart.name", searchTerm, MatchMode.ANYWHERE));
        }

        Set<AdvertLocationSummaryDTO> rows = newTreeSet(criteria.list());

        Set<String> parentTokens = newHashSet();
        rows.stream().forEach(row -> {
            Set<String> nameIndexSplit = newHashSet(row.getNameIndex().split("\\|"));
            nameIndexSplit.remove(row.getName());
            parentTokens.addAll(nameIndexSplit);

        });
        if (parentTokens.size() > 0) {
            rows.addAll(getAdvertLocationSummaryCriteria(visibleAdverts) //
                    .add(Restrictions.in("locationPart.name", parentTokens)) //
                    .list());
        }

        return newLinkedList(rows);
    }

    public List<AdvertLocationSummaryDTO> getAdvertLocationSummaries(List<Integer> locationPartIds) {
        return sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.property("locationPart.id").as("id"))
                        .add(Projections.property("locationPart.name").as("name"))
                        .add(Projections.countDistinct("id").as("advertCount")))
                .createAlias("categories.locations", "advertLocation", JoinType.INNER_JOIN) //
                .createAlias("advertLocation.locationAdvert", "locationAdvert", JoinType.INNER_JOIN) //
                .createAlias("locationAdvert.address", "locationAddress", JoinType.INNER_JOIN) //
                .createAlias("locationAddress.locations", "location", JoinType.INNER_JOIN) //
                .createAlias("location.locationPart", "locationPart", JoinType.INNER_JOIN) //
                .add(Restrictions.in("locationPart.id", locationPartIds))
                .setResultTransformer(Transformers.aliasToBean(AdvertLocationSummaryDTO.class))
                .list();
    }

    public List<AdvertInstitutionSummaryDTO> getAdvertInstitutionSummaries(List<Integer> visibleAdverts, String searchTerm) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution.id").as("id")) //
                        .add(Projections.property("institution.name").as("name")) //
                        .add(Projections.property("institution.logoImage.id").as("logoImageId")) //
                        .add(Projections.countDistinct("id").as("advertCount"))) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", visibleAdverts));

        if (isNotEmpty(searchTerm)) {
            criteria.add(Restrictions.like("institution.name", searchTerm, MatchMode.ANYWHERE));
        }

        return (List<AdvertInstitutionSummaryDTO>) criteria.addOrder(Order.asc("institution.name")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertInstitutionSummaryDTO.class)) //
                .list();
    }

    public List<Advert> getTargetedAdverts(Collection<Advert> adverts) {
        return sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("targetAdvert")) //
                .add(Restrictions.in("advert", adverts)) //
                .add(Restrictions.eq("partnershipState", ENDORSEMENT_PROVIDED)) //
                .list();
    }

    public List<Advert> getTargeterAdverts(Collection<Advert> adverts) {
        return sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("advert")) //
                .add(Restrictions.in("targetAdvert", adverts))
                .add(Restrictions.eq("partnershipState", ENDORSEMENT_PROVIDED)) //
                .list();
    }

    public List<Integer> getUserAdverts(HashMultimap<PrismScope, Integer> userResources, PrismScope... displayScopes) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.property("id")) //
                .add(getVisibleAdvertConstraint(EMPTY, EMPTY, userResources, true, displayScopes)) //
                .list();
    }

    public List<AdvertTargetAdvertDTO> getUserAdvertsTargeted(HashMultimap<PrismScope, Integer> userResources, PrismScope[] displayScopes) {
        Set<AdvertTargetAdvertDTO> adverts = newHashSet();
        adverts.addAll((List<AdvertTargetAdvertDTO>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("advert.id").as("advertId")) //
                        .add(Projections.property("targetAdvert.id").as("targetAdvertId"))) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("partnershipState", ENDORSEMENT_PROVIDED)) //
                .add(getVisibleAdvertConstraint("advert", "targetAdvert", userResources, true, displayScopes)) //
                .setResultTransformer(Transformers.aliasToBean(AdvertTargetAdvertDTO.class)) //
                .list());

        stream(organizationScopes).forEach(organizationScope -> {
            adverts.addAll((List<AdvertTargetAdvertDTO>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                    .setProjection(Projections.projectionList() //
                            .add(Projections.property("id").as("advertId")) //
                            .add(Projections.property("resourceTargetAdvert.id").as("targetAdvertId"))) //
                    .createAlias(organizationScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                    .createAlias("resource.advert", "resourceAdvert", JoinType.INNER_JOIN) //
                    .createAlias("resourceAdvert.targets", "resourceTarget", JoinType.INNER_JOIN) //
                    .createAlias("resourceTarget.targetAdvert", "resourceTargetAdvert", JoinType.INNER_JOIN) //
                    .createAlias("targets", "target", JoinType.LEFT_OUTER_JOIN, //
                            Restrictions.eq("target.partnershipState", ENDORSEMENT_PROVIDED)) //
                    .add(Restrictions.eq("resourceTarget.partnershipState", ENDORSEMENT_PROVIDED)) //
                    .add(Restrictions.eq("resourceTarget.advertSevered", false)) //
                    .add(Restrictions.eq("resourceTarget.targetAdvertSevered", false)) //
                    .add(getVisibleAdvertConstraint(EMPTY, "resourceTargetAdvert", userResources, true, displayScopes)) //
                    .add(Restrictions.isNull("target.id")) //
                    .setResultTransformer(Transformers.aliasToBean(AdvertTargetAdvertDTO.class)) //
                    .list());
        });

        return newArrayList(adverts);
    }

    public List<Integer> getUserAdvertsTargeted(HashMultimap<PrismScope, Integer> userResources, PrismScope displayScope, PrismScope... sourceScopes) {
        Set<Integer> adverts = newHashSet();
        adverts.addAll((List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.property("advert.id").as("advertId")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .add(getVisibleAdvertConstraint("advert", "targetAdvert", userResources, false, sourceScopes)) //
                .list());

        Junction notSeveredConstraint = null;
        if (!displayScope.equals(APPLICATION)) {
            notSeveredConstraint = Restrictions.conjunction() //
                    .add(Restrictions.eq("resourceTarget.advertSevered", false)) //
                    .add(Restrictions.eq("resourceTarget.targetAdvertSevered", false));
        }

        for (PrismScope organizationScope : organizationScopes) {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                    .setProjection(Projections.property("id").as("advertId")) //
                    .createAlias(organizationScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                    .createAlias("resource.advert", "resourceAdvert", JoinType.INNER_JOIN) //
                    .createAlias("resourceAdvert.targets", "resourceTarget", JoinType.INNER_JOIN) //
                    .createAlias("resourceTarget.targetAdvert", "resourceTargetAdvert", JoinType.INNER_JOIN) //
                    .createAlias("targets", "target", JoinType.LEFT_OUTER_JOIN, //
                            Restrictions.eq("target.partnershipState", ENDORSEMENT_PROVIDED));

            if (notSeveredConstraint != null) {
                criteria.add(notSeveredConstraint);
            }

            criteria.add(getVisibleAdvertConstraint(EMPTY, "resourceTargetAdvert", userResources, false, sourceScopes)) //
                    .add(Restrictions.isNull("target.id"));

            adverts.addAll((List<Integer>) criteria.list());
        }

        return newArrayList(adverts);
    }

    public List<Integer> getAdvertsRevoked() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("partnershipState", ENDORSEMENT_REVOKED)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNotNull("advert.project.id")) //
                        .add(Restrictions.isNotNull("advert.program.id"))) //
                .list();
    }

    public List<Integer> getUserAdvertsRevoked(Collection<Integer> userAdverts, HashMultimap<PrismScope, Integer> userResources, PrismScope[] displayScopes) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(AdvertTarget.class) //
                .setProjection(Projections.groupProperty("advert.id")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("partnershipState", ENDORSEMENT_REVOKED)) //
                .add(Restrictions.in("advert.id", userAdverts)) //
                .add(getVisibleAdvertConstraint("advert", "targetAdvert", userResources, true, displayScopes)) //
                .list();
    }

    public List<AdvertThemeRepresentation> getSuggestedAdvertThemes(Advert advert) {
        return (List<AdvertThemeRepresentation>) sessionFactory.getCurrentSession().createCriteria(AdvertTheme.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("theme.id"), "id")
                        .add(Projections.property("theme.name"), "name"))
                .createAlias("advert", "advert")
                .createAlias("theme", "theme", JoinType.INNER_JOIN)
                .add(Restrictions.eq("advert.institution", advert.getInstitution()))
                .addOrder(Order.asc("theme.name"))
                .setResultTransformer(Transformers.aliasToBean(AdvertThemeRepresentation.class))
                .list();
    }

    public List<AdvertApplicationDTO> getAdvertsUserApplyingFor(User user, Collection<Integer> adverts) {
        return (List<AdvertApplicationDTO>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("advert.id"), "advertId")
                        .add(Projections.property("id"), "applicationId")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.in("advert.id", adverts)) //
                .add(Restrictions.isNull("completionDate")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertApplicationDTO.class))
                .list();
    }

    public List<Advert> getBadgeAdverts(ResourceParent parentResource, int count) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .add(Restrictions.eq(parentResource.getResourceScope().getLowerCamelName(), parentResource))
                .add(Restrictions.disjunction()
                        .add(Restrictions.isNotNull("project"))
                        .add(Restrictions.isNotNull("program")))
                .setMaxResults(count)
                .list();
    }

    private void appendScopeConstraint(Criteria criteria, Collection<PrismScope> scopes) {
        if (isNotEmpty(scopes)) {
            criteria.add(Restrictions.in("scope.id", scopes)); //
        }
    }

    private void appendContextConstraint(Criteria criteria, OpportunityQueryDTO queryDTO) {
        PrismResourceContext context = queryDTO.getContext();
        if (context != null) {
            if (context.equals(EMPLOYER)) {
                criteria.add(Restrictions.disjunction() //
                        .add(getMatchingFlattenedPropertyConstraint("opportunityCategories", EXPERIENCE.name())) //
                        .add(getMatchingFlattenedPropertyConstraint("opportunityCategories", WORK.name())));
            } else if (context.equals(UNIVERSITY)) {
                criteria.add(getMatchingFlattenedPropertyConstraint("opportunityCategories", STUDY.name()));
            }
        }
    }

    private void appendKeywordConstraint(OpportunityQueryDTO queryDTO, Criteria criteria) {
        String keyword = queryDTO.getKeyword();
        if (isNotEmpty(keyword)) {
            Junction constraint = Restrictions.disjunction();
            for (PrismScope advertScope : advertScopes) {
                String enclosingAdvertReference = advertScope.getLowerCamelName() + "Advert.";
                constraint.add(Restrictions.like(enclosingAdvertReference + "name", keyword, MatchMode.ANYWHERE)) //
                        .add(Restrictions.like(enclosingAdvertReference + "summary", keyword, MatchMode.ANYWHERE)) //
                        .add(Restrictions.like(enclosingAdvertReference + "description", keyword, MatchMode.ANYWHERE));
            }
            criteria.add(constraint);
        }
    }

    private void appendLocationConstraint(Criteria criteria, OpportunityQueryDTO queryDTO) {
        BigDecimal swLat = queryDTO.getSwLat();
        BigDecimal swLon = queryDTO.getSwLon();
        BigDecimal neLat = queryDTO.getNeLat();
        BigDecimal neLon = queryDTO.getNeLon();
        if (noneNull(swLat, swLon, neLat, neLon)) {
            criteria.add(Restrictions.conjunction() //
                    .add(Restrictions.between("locationAddress.addressCoordinates.latitude", swLat, neLat)) //
                    .add(Restrictions.between("locationAddress.addressCoordinates.longitude", swLon, neLon)));
        }

        List<Integer> locations = queryDTO.getLocations();
        if (isNotTrue(queryDTO.getIgnoreLocations()) && isNotEmpty(locations)) {
            criteria.add(Restrictions.in("locationAddressLocation.locationPart.id", locations));
        }
    }

    private void appendIndustryConstraint(Criteria criteria, OpportunityQueryDTO queryDTO) {
        List<PrismAdvertIndustry> industries = queryDTO.getIndustries();
        if (isNotTrue(queryDTO.getIgnoreIndustries()) && isNotEmpty(industries)) {
            criteria.add(Restrictions.in("industry.industry", industries));
        }
    }

    private void appendFunctionConstraint(Criteria criteria, OpportunityQueryDTO queryDTO) {
        List<PrismAdvertFunction> functions = queryDTO.getFunctions();
        if (isNotTrue(queryDTO.getIgnoreFunctions()) && isNotEmpty(functions)) {
            criteria.add(Restrictions.in("function.function", functions));
        }
    }

    private void appendThemeConstraint(Criteria criteria, OpportunityQueryDTO queryDTO) {
        List<Integer> themes = queryDTO.getThemes();
        if (isNotTrue(queryDTO.getIgnoreThemes()) && isNotEmpty(themes)) {
            criteria.add(Restrictions.in("theme.theme.id", themes));
        }
    }

    private void appendInstitutionConstraint(Criteria criteria, OpportunityQueryDTO queryDTO) {
        List<Integer> institutions = queryDTO.getInstitutions();
        if (isNotTrue(queryDTO.getIgnoreInstitutions()) && isNotEmpty(institutions)) {
            criteria.add(Restrictions.in("institution.id", institutions));
        }
    }

    private void appendStudyOptionConstraint(OpportunityQueryDTO queryDTO, Criteria criteria) {
        List<PrismStudyOption> studyOptions = queryDTO.getStudyOptions();
        if (isNotEmpty(studyOptions)) {
            Disjunction studyOptionConstraint = Restrictions.disjunction();
            for (PrismStudyOption studyOption : studyOptions) {
                studyOptionConstraint.add(getMatchingFlattenedPropertyConstraint("studyOptions", studyOption.name()));
            }
            criteria.add(studyOptionConstraint);
        }
    }

    private void appendPayConstraint(Criteria criteria, OpportunityQueryDTO queryDTO) {
        BigDecimal minSalary = queryDTO.getMinSalary();
        BigDecimal maxSalary = queryDTO.getMaxSalary();
        PrismDurationUnit interval = queryDTO.getSalaryInterval();
        if (noneNull(minSalary, maxSalary, interval)) {
            BigDecimal durationAsHours = new BigDecimal(getDurationUnitAsHours(interval));
            minSalary = queryDTO.getMinSalary().divide(durationAsHours, 2, HALF_UP);
            maxSalary = queryDTO.getMaxSalary().divide(durationAsHours, 2, HALF_UP);
            if (!interval.equals(HOUR)) {
                appendRangeConstraint(criteria, "pay.minimumNormalized", "pay.maximumNormalized", minSalary, maxSalary);
            } else {
                appendRangeConstraint(criteria, "pay.minimumNormalizedHour", "pay.maximumNormalizedHour", minSalary, maxSalary);
            }
        }
    }

    private void appendDurationConstraint(Criteria criteria, OpportunityQueryDTO queryDTO) {
        appendRangeConstraint(criteria, "durationMinimum", "durationMaximum", queryDTO.getMinDuration(), queryDTO.getMaxDuration());
    }

    private void appendVisibilityConstraint(Criteria criteria, OpportunityQueryDTO query, UserAdvertDTO userAdvertDTO, Collection<Integer> nodeAdverts) {
        Junction constraint = Restrictions.conjunction();
        if (isTrue(query.getRecommendation())) {
            constraint.add(Restrictions.in("id", userAdvertDTO.getVisibleDirect()));
        }

        List<Integer> invisible = userAdvertDTO.getInvisible();
        if (isNotEmpty(invisible)) {
            constraint.add(Restrictions.not( //
                    Restrictions.in("id", invisible)));
        }

        constraint.add(Restrictions.eq("published", true));

        criteria.add(constraint);
        if (isNotEmpty(nodeAdverts)) {
            criteria.add(Restrictions.in("id", nodeAdverts));
        }
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
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.neProperty("thisAdvert.id", "otherAdvert.id")) //
                .add(getResourceParentManageableStateConstraint("state"));
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

    private void appendVisibleAdvertConstraint(Criteria criteria, String selectAdvertAlias, PrismScope... displayScopes) {
        selectAdvertAlias = getResolvedAliasReference(selectAdvertAlias);
        if (isNotEmpty(displayScopes)) {
            criteria.add(Restrictions.in(selectAdvertAlias + "scope.id", displayScopes));
        }

        criteria.add(Restrictions.eq(selectAdvertAlias + "published", true));
    }

    private Junction getVisibleAdvertConstraint(String selectAdvertAlias, String restrictAdvertAlias, HashMultimap<PrismScope, Integer> userResources,
            boolean publishedOnly, PrismScope... displayScopes) {
        String selectAdvertAliasResolved = getResolvedAliasReference(selectAdvertAlias);

        Junction visibilityConstraint = Restrictions.conjunction();
        if (userResources == null || userResources.isEmpty()) {
            visibilityConstraint.add(Restrictions.eq(selectAdvertAliasResolved + "id", 0));
        } else {
            String restrictAdvertAliasResolved = getResolvedAliasReference(restrictAdvertAlias);

            Junction permissionConstraint = Restrictions.disjunction();
            userResources.keySet().stream().forEach(userResourceScope -> {
                Set<Integer> userResourcesScope = userResources.get(userResourceScope);
                if (isNotEmpty(userResourcesScope)) {
                    permissionConstraint.add(Restrictions.in(restrictAdvertAliasResolved + userResourceScope.getLowerCamelName() + ".id", userResourcesScope));
                }
            });

            visibilityConstraint.add(permissionConstraint);

            if (isNotEmpty(displayScopes)) {
                visibilityConstraint.add(Restrictions.in(selectAdvertAliasResolved + "scope.id", displayScopes));
            }

            visibilityConstraint.add(Restrictions.eq(selectAdvertAliasResolved + "submitted", true));
            if (publishedOnly) {
                visibilityConstraint.add(Restrictions.eq(selectAdvertAliasResolved + "published", true));
            }
        }

        return visibilityConstraint;
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

    private Criteria getAdvertLocationSummaryCriteria(Collection<Integer> adverts) {
        return sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("locationPart.id").as("id")) //
                        .add(Projections.property("locationPart.parent.id").as("parentId")) //
                        .add(Projections.property("locationPart.name").as("name")) //
                        .add(Projections.property("locationPart.nameIndex").as("nameIndex")) //
                        .add(Projections.countDistinct("id").as("advertCount"))) //
                .createAlias("categories.locations", "advertLocation", JoinType.INNER_JOIN) //
                .createAlias("advertLocation.locationAdvert", "locationAdvert", JoinType.INNER_JOIN) //
                .createAlias("locationAdvert.address", "locationAddress", JoinType.INNER_JOIN) //
                .createAlias("locationAddress.locations", "location", JoinType.INNER_JOIN) //
                .createAlias("location.locationPart", "locationPart", JoinType.INNER_JOIN) //
                .add(Restrictions.in("id", adverts)) //
                .addOrder(Order.asc("locationPart.nameIndex")) //
                .setResultTransformer(Transformers.aliasToBean(AdvertLocationSummaryDTO.class));
    }

}
