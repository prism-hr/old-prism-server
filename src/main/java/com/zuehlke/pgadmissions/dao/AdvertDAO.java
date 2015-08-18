package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.math.BigDecimal;
import java.util.Arrays;
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertAttribute;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFunction;
import com.zuehlke.pgadmissions.domain.advert.AdvertIndustry;
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.advert.AdvertTargetResource;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;

// FIXME adverts for applying to institution and/or department directly
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

    public List<Integer> getAdverts(HashMultimap<PrismScope, PrismState> scopes, OpportunitiesQueryDTO queryDTO) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("categories.industries", "industry", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("categories.functions", "function", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("categories.themes", "theme", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.resourceStates", "programState", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.resourceConditions", "programCondition", JoinType.LEFT_OUTER_JOIN, //
                        getResourceConditionConstraint("programCondition")) //
                .createAlias("program.institution", "programInstitution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.department", "programDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.opportunityType", "programOpportunityType", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.instanceGroups", "programInstanceGroups", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("programInstanceGroups.studyOption", "programStudyOption", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.resourceStates", "projectState", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.resourceConditions", "projectCondition", JoinType.LEFT_OUTER_JOIN, //
                        getResourceConditionConstraint("projectCondition")) //
                .createAlias("project.program", "projectProgram", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.institution", "projectInstitution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.department", "projectDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.opportunityType", "projectOpportunityType", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.instanceGroups", "projectInstanceGroups", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("projectInstanceGroups.studyOption", "projectStudyOption", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.userRoles", "projectUserRole", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.in("projectUserRole.role.id", Arrays.asList(PROJECT_SUPERVISOR_GROUP))) //
                .createAlias("projectUserRole.user", "projectUser", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("program.id")) //
                                .add(Restrictions.isNotNull("programCondition.id")) //
                                .add(Restrictions.in("programState.state.id", scopes.get(PROGRAM)))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("project.id")) //
                                .add(Restrictions.isNotNull("projectCondition.id")) //
                                .add(Restrictions.in("projectState.state.id", scopes.get(PROJECT)))));

        appendLocationConstraint(criteria, queryDTO);
        appendKeywordConstraint(queryDTO, criteria);

        appendIndustryConstraint(criteria, queryDTO);
        appendFunctionConstraint(criteria, queryDTO);

        appendOpportunityTypeConstraint(criteria, queryDTO);
        appendStudyOptionConstraint(queryDTO, criteria);
        appendActionConditionConstraint(queryDTO, criteria);

        appendFeeConstraint(criteria, queryDTO);
        appendPayConstraint(criteria, queryDTO);
        appendDurationConstraint(criteria, queryDTO);

        appendInstitutionsConstraint(criteria, queryDTO);
        appendDepartmentsConstraint(queryDTO, criteria);
        appendProgramsConstraint(queryDTO, criteria);
        appendProjectsConstraint(queryDTO, criteria);

        String lastSequenceIdentifier = queryDTO.getLastSequenceIdentifier();
        if (lastSequenceIdentifier != null) {
            criteria.add(Restrictions.lt("sequenceIdentifier", lastSequenceIdentifier));
        }

        return (List<Integer>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
                .setMaxResults(25) //
                .list();
    }

    public List<Advert> getActiveAdverts(List<Integer> adverts, boolean prioritizeProgram) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class);

        if (prioritizeProgram) {
            criteria.createAlias("program", "program", JoinType.LEFT_OUTER_JOIN);
        }

        criteria.add(Restrictions.in("id", adverts));

        if (prioritizeProgram) {
            criteria.addOrder(Order.desc("program.id"));
        }

        return (List<Advert>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
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
                .add(getInstitutionConstraint(institution)) //
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

    public List<AdvertTarget<?>> getAdvertTargets(Advert advert, Class<? extends AdvertTarget<?>> targetClass) {
        return (List<AdvertTarget<?>>) sessionFactory.getCurrentSession().createCriteria(targetClass) //
                .add(Restrictions.eq("advert", advert)) //
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
                .add(Restrictions.ilike("name", q, MatchMode.ANYWHERE))
                .list();
    }

    public void deleteAdvertAttributes(Advert advert, Class<? extends AdvertAttribute<?>> attributeClass) {
        sessionFactory.getCurrentSession().createQuery(
                "delete " + attributeClass.getSimpleName() + " "
                        + "where advert = :advert")
                .setParameter("advert", advert) //
                .executeUpdate();
    }

    public List<Integer> getAdvertResources(Advert advert, PrismScope resourceScope, Class<? extends AdvertTargetResource> targetClass) {
        String resourceReference = resourceScope.getLowerCamelName();
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(targetClass) //
                .setProjection(Projections.property(resourceReference + ".id")) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.isNotNull(resourceReference)) //
                .list();
    }

    private Junction getResourceConditionConstraint(String tableReference) {
        return Restrictions.disjunction() //
                .add(Restrictions.eq(tableReference + ".partnerMode", true)) //
                .add(Restrictions.eq(tableReference + ".actionCondition", ACCEPT_APPLICATION));
    }

    private void appendLocationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        if (queryDTO.getNeLat() != null) {
            criteria.add(Restrictions.between("address.location.latitude", queryDTO.getSwLat(), queryDTO.getNeLat())) //
                    .add(Restrictions.between("address.location.longitude", queryDTO.getSwLon(), queryDTO.getNeLon()));
        }
    }

    private void appendKeywordConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        String keyword = queryDTO.getKeyword();
        if (keyword != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.ilike("theme.theme", MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("summary", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("description", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("program.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("project.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("programDepartment.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("programInstitution.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectProgram.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectDepartment.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectInstitution.name", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectUser.firstName", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectUser.lastName", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectUser.email", keyword, MatchMode.ANYWHERE))); //
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
        Collection<PrismOpportunityType> opportunityTypes = queryDTO.getOpportunityTypes();
        if (opportunityTypes == null) {
            opportunityTypes = Lists.newLinkedList();
            if (queryDTO.getProgramCategories() != null) {
                for (PrismOpportunityCategory category : queryDTO.getProgramCategories()) {
                    for (PrismOpportunityType opportunityType : PrismOpportunityType.getOpportunityTypes(category)) {
                        opportunityTypes.add(opportunityType);
                    }
                }
            }
        }

        Disjunction opportunityTypeConstraint = Restrictions.disjunction();
        for (PrismOpportunityType opportunityType : opportunityTypes) {
            opportunityTypeConstraint
                    .add(Restrictions.eq("programOpportunityType.type", opportunityType))
                    .add(Restrictions.eq("projectOpportunityType.type", opportunityType));

        }
        criteria.add(opportunityTypeConstraint);
    }

    private void appendStudyOptionConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        List<PrismStudyOption> studyOptions = queryDTO.getStudyOptions();
        if (CollectionUtils.isNotEmpty(studyOptions)) {
            Disjunction studyOptionConstraint = Restrictions.disjunction();
            for (PrismStudyOption studyOption : studyOptions) {
                studyOptionConstraint
                        .add(Restrictions.eq("programStudyOption.type", studyOption))
                        .add(Restrictions.eq("projectStudyOption.type", studyOption));
            }
            criteria.add(studyOptionConstraint);
        }
    }

    private void appendActionConditionConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        List<PrismActionCondition> actionConditions = queryDTO.getActionConditions();
        if (CollectionUtils.isNotEmpty(actionConditions)) {
            Disjunction actionConditionConstraint = Restrictions.disjunction();
            for (PrismActionCondition actionCondition : actionConditions) {
                actionConditionConstraint //
                        .add(Restrictions.eq("programCondition.actionCondition", actionCondition)) //
                        .add(Restrictions.eq("projectCondition.actionCondition", actionCondition)); //
            }
            criteria.add(actionConditionConstraint);
        }

    }

    private void appendFeeConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        appendRangeConstraint(criteria, "fee.monthMinimumAtLocale", "fee.monthMaximumAtLocale", queryDTO.getMinFee(), queryDTO.getMaxFee(), true);
    }

    private void appendPayConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        appendRangeConstraint(criteria, "pay.monthMinimumAtLocale", "pay.monthMaximumAtLocale", queryDTO.getMinSalary(), queryDTO.getMaxSalary(), true);
    }

    private void appendDurationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        Junction disjunction = Restrictions.disjunction();
        appendRangeConstraint(disjunction, "program.durationMinimum", "program.durationMaximum", queryDTO.getMinDuration(), queryDTO.getMaxDuration(), false);
        appendRangeConstraint(disjunction, "project.durationMinimum", "project.durationMaximum", queryDTO.getMinDuration(), queryDTO.getMaxDuration(), false);
        criteria.add(disjunction);
    }

    private void appendInstitutionsConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        Integer[] institutions = queryDTO.getInstitutions();
        if (institutions != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.in("programInstitution.id", institutions)) //
                    .add(Restrictions.in("projectInstitution.id", institutions))); //
        }
    }

    private void appendDepartmentsConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        Integer[] departments = queryDTO.getDepartments();
        if (departments != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.in("programDepartment.id", departments)) //
                    .add(Restrictions.in("projectDepartment.id", departments)));
        }
    }

    private void appendProgramsConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        Integer[] programs = queryDTO.getPrograms();
        if (programs != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.in("program.id", programs)) //
                    .add(Restrictions.in("projectProgram.id", programs)));
        }
    }

    private void appendProjectsConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        Integer[] projects = queryDTO.getProjects();
        if (projects != null) {
            criteria.add(Restrictions.in("project.id", projects));
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

    private Junction getInstitutionConstraint(Institution institution) {
        return Restrictions.disjunction() //
                .add(Restrictions.eq("project.institution", institution)) //
                .add(Restrictions.eq("program.institution", institution)) //
                .add(Restrictions.eq("institution.id", institution.getId()));
    }

}
