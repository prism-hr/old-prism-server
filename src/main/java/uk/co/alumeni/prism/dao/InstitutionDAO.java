package uk.co.alumeni.prism.dao;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_DISABLED_COMPLETED;

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

import com.google.common.collect.Maps;
import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.dto.ResourceLocationDTO;
import uk.co.alumeni.prism.utils.PrismTemplateUtils;

@Repository
public class InstitutionDAO {

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private PrismTemplateUtils prismTemplateUtils;

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

        sessionFactory.getCurrentSession().createSQLQuery( //
                prismTemplateUtils.getContentFromLocation("statement", templateLocation, model)) //
                .executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<ResourceLocationDTO> getInstitutions(String query, String[] googleIds) {
        Disjunction searchConstraint = Restrictions.disjunction();

        if (query != null) {
            searchConstraint.add(WorkflowDAO.getLikeConstraint("name", query));
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
                        .add(Projections.property("address.addressCoordinates.longitude").as("addressCoordinateLongitude"))
                        .add(Projections.property("user.id").as("userId"))
                        .add(Projections.property("user.firstName").as("userFirstName"))
                        .add(Projections.property("user.lastName").as("userLastName"))
                        .add(Projections.property("user.email").as("userEmail")))
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.address", "address", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.user", "user", JoinType.LEFT_OUTER_JOIN) //
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
