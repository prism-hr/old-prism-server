package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceConditionConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.Coordinates;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;

import freemarker.template.Template;

@Repository
@SuppressWarnings("unchecked")
public class InstitutionDAO {

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private FreeMarkerConfig freemarkerConfig;

    public List<Institution> getApprovedInstitutionsByDomicile(ImportedAdvertDomicile domicile) {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("advert.domicile", domicile)) //
                .add(Restrictions.eq("state.id", INSTITUTION_APPROVED)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

    public Institution getUclInstitution() {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("uclInstitution", true)) //
                .uniqueResult();
    }

    public List<String> listAvailableCurrencies() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedAdvertDomicile.class) //
                .setProjection(Projections.distinct(Projections.property("currency"))) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("currency")) //
                .list();
    }

    public List<Institution> list() {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class).list();
    }

    public List<Integer> getApprovedInstitutions() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resourceState.state.id", INSTITUTION_APPROVED)) //
                .list();
    }

    public Institution getActivatedInstitutionByGoogleId(String googleId) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("googleId", googleId)) //
                .add(Restrictions.eq("state.id", INSTITUTION_APPROVED)) //
                .uniqueResult();
    }

    public void disableInstitutionDomiciles(List<String> updates) {
        sessionFactory.getCurrentSession().createQuery( //
                "update InstitutionDomicile " //
                        + "set enabled = false " //
                        + "where id not in (:updates)") //
                .setParameterList("updates", updates) //
                .executeUpdate();
    }

    public void changeInstitutionBusinessYear(Integer institutionId, Integer businessYearEndMonth) throws Exception {
        String templateLocation;

        Map<String, Object> model = Maps.newHashMap();
        model.put("institutionId", institutionId);

        if (businessYearEndMonth == 12) {
            templateLocation = "sql/institution_change_business_year_simple.ftl";
        } else {
            templateLocation = "sql/institution_change_business_year_complex.ftl";
            model.put("businessYearEndMonth", businessYearEndMonth);
        }

        String statement = Resources.toString(Resources.getResource(templateLocation), Charsets.UTF_8);
        Template template = new Template("statement", statement, freemarkerConfig.getConfiguration());

        sessionFactory.getCurrentSession().createSQLQuery( //
                FreeMarkerTemplateUtils.processTemplateIntoString(template, model)) //
                .executeUpdate();
    }

    public List<Institution> getInstitutions(String searchTerm, String[] googleIds) {
        Disjunction searchCriterion = Restrictions.disjunction();

        if (searchTerm != null) {
            searchCriterion.add(Restrictions.ilike("name", searchTerm, MatchMode.ANYWHERE));
        }
        if (googleIds != null && googleIds.length > 0) {
            searchCriterion.add(Restrictions.in("googleId", googleIds));
        }

        return sessionFactory.getCurrentSession().createCriteria(Institution.class)
                .add(searchCriterion)
                .add(Restrictions.eq("state.id", INSTITUTION_APPROVED))
                .list();
    }

    public List<ResourceChildCreationDTO> getInstitutionsForWhichUserCanCreateProgram(List<PrismState> states, boolean userLoggedIn) {
        return (List<ResourceChildCreationDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(getResourceConditionConstraint(ACCEPT_PROGRAM, userLoggedIn)) //
                .add(Restrictions.eq("action.creationScope.id", PROGRAM)) //
                .addOrder(Order.asc("institution.name")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceChildCreationDTO.class)) //
                .list();
    }

    public List<ResourceChildCreationDTO> getInstitutionsForWhichUserCanCreateProject(List<PrismState> states, boolean userLoggedIn) {
        return (List<ResourceChildCreationDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(getResourceConditionConstraint(ACCEPT_PROJECT, userLoggedIn)) //
                .add(Restrictions.eq("action.creationScope.id", PROJECT)) //
                .addOrder(Order.asc("institution.name")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceChildCreationDTO.class)) //
                .list();
    }

    public List<ResourceChildCreationDTO> getInstitutionsWhichHaveProgramsForWhichUserCanCreateProject(List<PrismState> states,
                                                                                                       boolean userLoggedIn) {
        return (List<ResourceChildCreationDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("program.institution"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .createAlias("program.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("program.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(getResourceConditionConstraint(ACCEPT_PROJECT, userLoggedIn)) //
                .add(Restrictions.eq("action.creationScope.id", PROJECT)) //
                .addOrder(Order.asc("institution.name")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceChildCreationDTO.class)) //
                .list();
    }

    public List<Institution> getInstitutionBySubjectAreas(Coordinates coordinates, List<Integer> subjectAreas) {
        List<Object[]> results = sessionFactory.getCurrentSession().createSQLQuery(
                "select institution.id, institution.name, sum(imported_institution_subject_area.relation_strength) as relevance," + 
                        " ((acos(sin(:baseLatitude * pi() / 180) * sin(advert_address.location_x * pi() / 180)" +
                        " + cos(:baseLatitude * pi() / 180) * cos(advert_address.location_x * pi() / 180) * cos((:baseLongitude - advert_address.location_y)" +
                        " * pi() / 180)) * 180 / pi()) * 60 * 1.1515) as distance" +
                        " from institution" +
                        " inner join imported_institution on institution.imported_institution_id = imported_institution.id" +
                        " inner join imported_institution_subject_area on imported_institution_subject_area.imported_institution_id = imported_institution.id" +
                        " inner join advert on institution.advert_id = advert.id" +
                        " inner join advert_address on advert.advert_address_id = advert_address.id" +
                        " where advert_address.location_x is not null" +
                        " and imported_institution_subject_area.imported_subject_area_id in (:subjectAreas)" +
                        " group by institution.id" +
                        " order by relevance desc, distance asc" +
                        " limit 20")
                .setParameter("baseLatitude", coordinates.getLatitude())
                .setParameter("baseLongitude", coordinates.getLongitude())
                .setParameterList("subjectAreas", subjectAreas)
                .list();
        List<Integer> institutionIds = results.stream().map(result -> (Integer) result[0]).collect(Collectors.toList());
        return institutionIds.isEmpty() ? Collections.emptyList() : sessionFactory.getCurrentSession().createCriteria(Institution.class)
                .add(Restrictions.in("id", institutionIds))
                .list();
    }

}
