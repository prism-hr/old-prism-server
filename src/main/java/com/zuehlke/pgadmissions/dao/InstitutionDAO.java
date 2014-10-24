package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.rest.dto.InstitutionSuggestionDTO;

@Repository
@SuppressWarnings("unchecked")
public class InstitutionDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<InstitutionDomicile> getDomciles() {
        return sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

    public List<InstitutionDomicileRegion> getRegionsByDomicile(InstitutionDomicile domicile) {
        return sessionFactory.getCurrentSession().createCriteria(InstitutionDomicileRegion.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("nestedPath")) //
                .list();
    }

    public List<Institution> listApprovedInstitutionsByCountry(InstitutionDomicile domicile) {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.eq("state.id", PrismState.INSTITUTION_APPROVED_COMPLETED)) //
                .addOrder(Order.asc("title")) //
                .list();
    }

    public Institution getUclInstitution() {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("uclInstitution", true)) //
                .uniqueResult();
    }

    public List<Institution> getInstitutionsWithoutImportedEntityFeeds() {
        return (List<Institution>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.isEmpty("importedEntityFeeds")) //
                .add(Restrictions.eq("state.id", PrismState.INSTITUTION_APPROVED)) //
                .list();
    }

    public List<String> listAvailableCurrencies() {
        return sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class) //
                .setProjection(Projections.distinct(Projections.property("currency"))) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("currency")) //
                .list();

    }

    public List<InstitutionSuggestionDTO> getSimilarImportedInsitutions(Integer domicileId, String searchTerm) {
        return (List<InstitutionSuggestionDTO>) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class, "institution") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("name"), "title")) //
                .add(Restrictions.eq("domicile.id", "domicileId")) //
                .add(Restrictions.eq("domicile.enabled", true)) //
                .add(Restrictions.eq("enabled", true)) //
                .add(Restrictions.ilike("name", "searchTerm", MatchMode.ANYWHERE)) //
                .addOrder(Order.asc("name")) //
                .setMaxResults(10) //
                .setResultTransformer(Transformers.aliasToBean(InstitutionSuggestionDTO.class)) //
                .list();
    }

    public List<Integer> getInstitutionsToActivate() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("state.id", INSTITUTION_APPROVED)) //
                .add(Restrictions.isNotEmpty("importedEntityFeeds")) //
                .list();
    }

    public List<Institution> list() {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class).list();
    }
    
}
