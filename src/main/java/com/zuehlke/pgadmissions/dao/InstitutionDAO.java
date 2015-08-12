package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Criteria;
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
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.AddressCoordinates;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.InstitutionDTOHibernate;
import com.zuehlke.pgadmissions.dto.InstitutionDTOSql;

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

    public List<InstitutionDTOHibernate> getInstitutions(List<PrismState> activeStates, String searchTerm, String[] googleIds) {
        Disjunction searchCriterion = Restrictions.disjunction();

        if (searchTerm != null) {
            searchCriterion.add(Restrictions.ilike("name", searchTerm, MatchMode.ANYWHERE));
        }
        if (googleIds != null && googleIds.length > 0) {
            searchCriterion.add(Restrictions.in("googleId", googleIds));
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Institution.class)
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.property("name"), "name") //
                        .add(Projections.property("logoImage.id"), "logoImageId") //
                        .add(Projections.property("domicile.name"), "addressDomicileName") //
                        .add(Projections.property("address.addressLine1"), "addressLine1") //
                        .add(Projections.property("address.addressLine2"), "addressLine2") //
                        .add(Projections.property("address.addressTown"), "addressTown") //
                        .add(Projections.property("address.addressRegion"), "addressRegion") //
                        .add(Projections.property("address.addressCode"), "addressCode") //
                        .add(Projections.property("address.googleId"), "addressGoogleId") //
                        .add(Projections.property("address.addressCoordinates.latitude"), "addressCoordinateLatitude") //
                        .add(Projections.property("address.addressCoordinates.longitude"), "addressCoordinateLongitude")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.address", "address", JoinType.INNER_JOIN) //
                .createAlias("address.domicile", "domicile", JoinType.INNER_JOIN);

        if (activeStates != null) {
            criteria.createAlias("resourceStates", "resourceState") //
                    .add(Restrictions.in("resourceState.state.id", activeStates));
        }

        return (List<InstitutionDTOHibernate>) criteria.add(searchCriterion)
                .setResultTransformer(Transformers.aliasToBean(InstitutionDTOHibernate.class))
                .list();
    }

    public List<InstitutionDTOSql> getInstitutionBySubjectAreas(List<PrismState> activeStates, Collection<Integer> subjectAreas,
            AddressCoordinates addressCoordinates) {
        return (List<InstitutionDTOSql>) sessionFactory.getCurrentSession().createSQLQuery(
                "select institution.id as id, institution.name as name, institution.logo_image_id as logoImageId," +
                        " imported_advert_domicile.name as addressDomicileName, advert_address.address_line_1 as addressLine1," +
                        " advert_address.address_line_2 as addressLine2, advert_address.address_town as addressTown," +
                        " advert_address.address_region as addressRegion, advert_address.address_code as addressCode," +
                        " advert_address.google_id as addressGoogleId, advert_address.location_x as addressCoordinateLatitude," +
                        " advert_address.location_y as addressCoordinateLongitude," +
                        " sum(imported_institution_subject_area.relation_strength) as targetingRelevance," +
                        " haversine_distance(:baseLatitude, :baseLongitude, advert_address.location_x, advert_address.location_y) as targetingDistance" +
                        " from institution " +
                        " inner join resource_state " +
                        " on institution.id = resource_state.institution_id " + 
                        " inner join imported_institution" +
                        " on institution.imported_institution_id = imported_institution.id" +
                        " inner join imported_institution_subject_area" +
                        " on imported_institution_subject_area.imported_institution_id = imported_institution.id" +
                        " inner join advert" +
                        " on institution.advert_id = advert.id" +
                        " inner join advert_address" +
                        " on advert.advert_address_id = advert_address.id" +
                        " inner join imported_advert_domicile" +
                        " on advert_address.imported_advert_domicile_id = imported_advert_domicile.id" +
                        " where resource_state.state_id in (:activeStates)" +
                        " and advert_address.location_x is not null" +
                        " and imported_institution_subject_area.imported_subject_area_id in (:subjectAreas)" +
                        " and imported_institution_subject_area.enabled is true " +
                        " group by institution.id" +
                        " order by targetingRelevance desc, targetingDistance asc")
                .addScalar("id", IntegerType.INSTANCE)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("logoImageId", IntegerType.INSTANCE)
                .addScalar("addressLine1", StringType.INSTANCE)
                .addScalar("addressLine2", StringType.INSTANCE)
                .addScalar("addressTown", StringType.INSTANCE)
                .addScalar("addressRegion", StringType.INSTANCE)
                .addScalar("addressCode", StringType.INSTANCE)
                .addScalar("addressDomicileName", StringType.INSTANCE)
                .addScalar("addressCoordinateLatitude", DoubleType.INSTANCE)
                .addScalar("addressCoordinateLongitude", DoubleType.INSTANCE)
                .addScalar("targetingRelevance", DoubleType.INSTANCE)
                .addScalar("targetingDistance", DoubleType.INSTANCE)
                .setParameter("baseLatitude", addressCoordinates.getLatitude())
                .setParameter("baseLongitude", addressCoordinates.getLongitude())
                .setParameterList("activeStates", activeStates.stream().map(activeState -> activeState.name()).collect(Collectors.toList()))
                .setParameterList("subjectAreas", subjectAreas)
                .setResultTransformer(Transformers.aliasToBean(InstitutionDTOSql.class))
                .list();
    }

}
