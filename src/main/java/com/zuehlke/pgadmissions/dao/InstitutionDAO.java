package com.zuehlke.pgadmissions.dao;

import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    
    public List<InstitutionDomicileRegion> getTopLevelRegions(InstitutionDomicile domicile) {
        return sessionFactory.getCurrentSession().createCriteria(InstitutionDomicileRegion.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.isNull("parentRegion")) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

    public List<Institution> listApprovedInstitutionsByCountry(InstitutionDomicile domicile) {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.eq("state.id", PrismState.INSTITUTION_APPROVED)) //
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

    public List<String> getSimilarInsitutions(String searchTerm, String domicileCode) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());

        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(ImportedInstitution.class).get();
        Query query = queryBuilder.phrase().onField("name").sentence(searchTerm).createQuery();

        Criteria filterCriteria = fullTextSession.createCriteria(ImportedInstitution.class) //
                .add(Restrictions.eq("domicileCode", domicileCode));

        return fullTextSession.createFullTextQuery(query, ImportedInstitution.class) //
                .setProjection("name") //
                .setCriteriaQuery(filterCriteria) //
                .setMaxResults(10) //
                .list();
    }

    public List<String> listAvailableCurrencies() {
        return sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class) //
                .setProjection(Projections.distinct(Projections.property("currency"))) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("currency")) //
                .list();

    }
}
