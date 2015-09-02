package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.resource.ResourceTargetDTO;

import freemarker.template.Template;

@Repository
@SuppressWarnings("unchecked")
public class InstitutionDAO {

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private FreeMarkerConfig freemarkerConfig;

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

    public Institution getActivatedInstitutionByGoogleId(String googleId) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("googleId", googleId)) //
                .add(Restrictions.eq("state.id", INSTITUTION_APPROVED)) //
                .uniqueResult();
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

    public List<ResourceTargetDTO> getInstitutions(List<PrismState> activeStates, String searchTerm, String[] googleIds) {
        Disjunction searchCriterion = Restrictions.disjunction();

        if (searchTerm != null) {
            searchCriterion.add(Restrictions.like("name", searchTerm, MatchMode.ANYWHERE));
        }
        if (googleIds != null && googleIds.length > 0) {
            searchCriterion.add(Restrictions.in("googleId", googleIds));
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Institution.class)
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "institutionId") //
                        .add(Projections.property("name"), "institutionName") //
                        .add(Projections.property("logoImage.id"), "institutionLogoImageId") //
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

        return (List<ResourceTargetDTO>) criteria.add(searchCriterion)
                .setResultTransformer(Transformers.aliasToBean(ResourceTargetDTO.class))
                .list();
    }

    public List<Integer> getInstitutionsByDepartments(List<Integer> departments, List<PrismState> activeStates) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("resourceStates", "resourceState") //
                .createAlias("departments", "department") //
                .add(Restrictions.in("resourceState.state.id", activeStates)) //
                .add(Restrictions.in("department.id", departments)) //
                .list();
    }

    public Institution getInstitutionByImportedProgram(ImportedProgram importedProgram) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .createAlias("importedInstitution", "importedInstitution", JoinType.INNER_JOIN) //
                .createAlias("importedInstitution.programs", "importedProgram", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("importedProgram.id", importedProgram.getId())) //
                .uniqueResult();
    }

}
