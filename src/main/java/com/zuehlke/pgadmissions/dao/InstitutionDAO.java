package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.Coordinates;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.dto.InstitutionTargetingDTO;

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

    public List<InstitutionTargetingDTO> getInstitutionBySubjectAreas(Coordinates coordinates, Collection<Integer> subjectAreas) {
        return (List<InstitutionTargetingDTO>) sessionFactory.getCurrentSession().createSQLQuery(
                "select institution.id as id, sum(imported_institution_subject_area.relation_strength) as relevance," +
                        " haversine_distance(:baseLatitude, :baseLongitude, advert_address.location_x, advert_address.location_y) as distance" +
                        " from institution" +
                        " inner join imported_institution on institution.imported_institution_id = imported_institution.id" +
                        " inner join imported_institution_subject_area on imported_institution_subject_area.imported_institution_id = imported_institution.id" +
                        " inner join advert on institution.advert_id = advert.id" +
                        " inner join advert_address on advert.advert_address_id = advert_address.id" +
                        " where advert_address.location_x is not null" +
                        " and imported_institution_subject_area.imported_subject_area_id in (:subjectAreas)" +
                        " and imported_institution_subject_area.enabled is true " +
                        " group by institution.id" +
                        " order by relevance desc, distance asc")
                .addScalar("id", IntegerType.INSTANCE)
                .addScalar("relevance", DoubleType.INSTANCE)
                .addScalar("distance", DoubleType.INSTANCE)
                .setParameter("baseLatitude", coordinates.getLatitude())
                .setParameter("baseLongitude", coordinates.getLongitude())
                .setParameterList("subjectAreas", subjectAreas)
                .setResultTransformer(Transformers.aliasToBean(InstitutionTargetingDTO.class))
                .list();
    }

}
