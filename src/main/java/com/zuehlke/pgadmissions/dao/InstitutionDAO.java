package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_DISABLED_COMPLETED;

import java.io.IOException;
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
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.ResourceLocationDTO;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Repository
public class InstitutionDAO {

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private FreeMarkerConfig freemarkerConfig;

    public Institution getInstitutionByImportedCode(String importedCode) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.like("importedCode", importedCode, MatchMode.ANYWHERE)) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<String> getAvailableCurrencies() {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Domicile.class) //
                .setProjection(Projections.distinct(Projections.property("currency"))) //
                .addOrder(Order.asc("currency")) //
                .list();
    }

    public Institution getInstitutionByUcasId(String ucasId) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("ucasId", ucasId)) //
                .uniqueResult();
    }

    public Institution getInstitutionByGoogleId(String googleId) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("googleId", googleId)) //
                .uniqueResult();
    }

    public void changeInstitutionBusinessYear(Integer institutionId, Integer businessYearEndMonth) {
        String templateLocation;

        Map<String, Object> model = Maps.newHashMap();
        model.put("institutionId", institutionId);

        if (businessYearEndMonth == 12) {
            templateLocation = "sql/institution_change_business_year_simple.ftl";
        } else {
            templateLocation = "sql/institution_change_business_year_complex.ftl";
            model.put("businessYearEndMonth", businessYearEndMonth);
        }

        try {
            String statement = Resources.toString(Resources.getResource(templateLocation), Charsets.UTF_8);
            Template template = new Template("statement", statement, freemarkerConfig.getConfiguration());

            sessionFactory.getCurrentSession().createSQLQuery( //
                    FreeMarkerTemplateUtils.processTemplateIntoString(template, model)) //
                    .executeUpdate();
        } catch (IOException | TemplateException e) {
            throw new Error("Could not change institution business year");
        }
    }

    @SuppressWarnings("unchecked")
    public List<ResourceLocationDTO> getInstitutions(String query, String[] googleIds) {
        Disjunction searchConstraint = Restrictions.disjunction();

        if (query != null) {
            searchConstraint.add(Restrictions.like("name", query, MatchMode.ANYWHERE));
        }
        if (googleIds != null && googleIds.length > 0) {
            searchConstraint.add(Restrictions.in("googleId", googleIds));
        }

        List<ResourceLocationDTO> list = (List<ResourceLocationDTO>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id").as("id")) //
                        .add(Projections.property("name").as("name")) //
                        .add(Projections.property("state.id").as("stateId")) //
                        .add(Projections.property("logoImage.id").as("logoImageId"))
                        .add(Projections.property("address.addressLine1").as("addressLine1")) //
                        .add(Projections.property("address.addressLine2").as("addressLine2")) //
                        .add(Projections.property("address.addressTown").as("addressTown")) //
                        .add(Projections.property("address.addressRegion").as("addressRegion")) //
                        .add(Projections.property("address.addressCode").as("addressCode")) //
                        .add(Projections.property("address.domicile.id").as("addressDomicileId")) //
                        .add(Projections.property("address.googleId").as("addressGoogleId")) //
                        .add(Projections.property("address.addressCoordinates.latitude").as("addressCoordinateLatitude")) //
                        .add(Projections.property("address.addressCoordinates.longitude").as("addressCoordinateLongitude"))) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.address", "address", JoinType.LEFT_OUTER_JOIN) //
                .add(searchConstraint) //
                .add(Restrictions.ne("state.id", INSTITUTION_DISABLED_COMPLETED)) //
                .addOrder(Order.asc("name")) //
                .addOrder(Order.asc("id")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceLocationDTO.class)) //
                .list();
        list.forEach(institution -> institution.setScope(PrismScope.INSTITUTION));
        return list;
    }

}
