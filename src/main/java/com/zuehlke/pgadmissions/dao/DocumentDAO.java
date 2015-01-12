package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.document.Document;

@Repository
@SuppressWarnings("unchecked")
public class DocumentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Integer> getDocumentsForExport() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Document.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("exported", false)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNotNull("applicationLanguageQualification")) //
                        .add(Restrictions.isNotNull("applicationQualification")) //
                        .add(Restrictions.isNotNull("applicationFunding")) //
                        .add(Restrictions.isNotNull("applicationPersonalStatement")) //
                        .add(Restrictions.isNotNull("applicationResearchStatement")) //
                        .add(Restrictions.isNotNull("applicationCv")) //
                        .add(Restrictions.isNotNull("applicationCoveringLetter")) //
                        .add(Restrictions.isNotNull("userPortrait")) //
                        .add(Restrictions.isNotNull("institutionLogo"))) //
                .list();
    }

    public void deleteOrphanDocuments(DateTime baseline) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete Document " //
                        + "where comment is null " //
                        + "and applicationLanguageQualification is null " //
                        + "and applicationQualification is null " //
                        + "and applicationFunding is null " //
                        + "and applicationPersonalStatement is null " //
                        + "and applicationResearchStatement is null " //
                        + "and applicationCv is null " //
                        + "and applicationCoveringLetter is null " //
                        + "and userPortrait is null " //
                        + "and institutionLogo is null " //
                        + "and createdTimestamp <= :createdTimestamp") //
                .setParameter("createdTimestamp", baseline.minusDays(1)) //
                .executeUpdate();
    }

}
