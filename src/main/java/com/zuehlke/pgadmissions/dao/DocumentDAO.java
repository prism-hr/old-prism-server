package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.user.User;

@Repository
@SuppressWarnings("unchecked")
public class DocumentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Integer> getExportDocuments() {
        return (List<Integer>) getDocumentCriteria() //
                .add(Restrictions.eq("exported", false)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNotNull("comment.id")) //
                        .add(Restrictions.isNotNull("applicationLanguageQualification.id")) //
                        .add(Restrictions.isNotNull("applicationQualification.id")) //
                        .add(Restrictions.isNotNull("applicationFunding.id")) //
                        .add(Restrictions.isNotNull("applicationPersonalStatement.id")) //
                        .add(Restrictions.isNotNull("applicationResearchStatement.id")) //
                        .add(Restrictions.isNotNull("applicationCv.id")) //
                        .add(Restrictions.isNotNull("applicationCoveringLetter.id")) //
                        .add(Restrictions.isNotNull("portraitImage.id")) //
                        .add(Restrictions.isNotNull("logoImage.id")) //
                        .add(Restrictions.isNotNull("institutionBackgroundImage.id")) //
                        .add(Restrictions.isNotNull("programBackgroundImage.id")) //
                        .add(Restrictions.isNotNull("projectBackgroundImage.id"))) //
                .list();
    }

    public List<Integer> getOrphanDocuments(DateTime baselineTime) {
        return (List<Integer>) getDocumentCriteria() //
                .add(Restrictions.isNull("comment.id")) //
                .add(Restrictions.isNull("applicationLanguageQualification.id")) //
                .add(Restrictions.isNull("applicationQualification.id")) //
                .add(Restrictions.isNull("applicationFunding.id")) //
                .add(Restrictions.isNull("applicationPersonalStatement.id")) //
                .add(Restrictions.isNull("applicationResearchStatement.id")) //
                .add(Restrictions.isNull("applicationCv.id")) //
                .add(Restrictions.isNull("applicationCoveringLetter.id")) //
                .add(Restrictions.isNull("portraitImage.id")) //
                .add(Restrictions.isNull("logoImage.id")) //
                .add(Restrictions.isNotNull("institutionBackgroundImage.id")) //
                .add(Restrictions.isNotNull("programBackgroundImage.id")) //
                .add(Restrictions.isNotNull("projectBackgroundImage.id")) //
                .add(Restrictions.le("createdTimestamp", baselineTime.minusDays(1))) //
                .list();
    }

    public void deleteOrphanDocuments(List<Integer> documentIds) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete Document " //
                        + "where id in (:documentIds)") //
                .setParameterList("documentIds", documentIds) //
                .executeUpdate();
    }

    public void reassignDocuments(User oldUser, User newUser) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Document " //
                        + "set user = :newUser " //
                        + "where user = :oldUser") //
                .setParameter("newUser", newUser) //
                .setParameter("oldUser", oldUser) //
                .executeUpdate();
    }

    private Criteria getDocumentCriteria() {
        return sessionFactory.getCurrentSession().createCriteria(Document.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("comment", "comment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationLanguageQualification", "applicationLanguageQualification", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationQualification", "applicationQualification", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationFunding", "applicationFunding", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationPersonalStatement", "applicationPersonalStatement", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationResearchStatement", "applicationResearchStatement", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationCv", "applicationCv", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationCoveringLetter", "applicationCoveringLetter", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("portraitImage", "portraitImage", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("logoImage", "logoImage", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("institutionBackgroundImage", "institutionBackgroundImage", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("programBackgroundImage", "programBackgroundImage", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("projectBackgroundImage", "projectBackgroundImage", JoinType.LEFT_OUTER_JOIN);
    }

}
