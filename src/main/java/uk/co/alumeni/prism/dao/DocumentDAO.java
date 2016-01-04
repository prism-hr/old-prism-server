package uk.co.alumeni.prism.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Resource;

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
                        .add(Restrictions.isNotNull("applicationQualification.id")) //
                        .add(Restrictions.isNotNull("applicationCv.id")) //
                        .add(Restrictions.isNotNull("applicationCoveringLetter.id")) //
                        .add(Restrictions.isNotNull("userQualification.id")) //
                        .add(Restrictions.isNotNull("userCv.id")) //
                        .add(Restrictions.isNotNull("portraitImage.id")) //
                        .add(Restrictions.isNotNull("logoImage.id")) //
                        .add(Restrictions.isNotNull("backgroundImage.id"))) //
                .list();
    }

    public List<Integer> getOrphanDocuments(DateTime baselineTime) {
        return (List<Integer>) getDocumentCriteria() //
                .add(Restrictions.isNull("comment.id")) //
                .add(Restrictions.isNull("applicationQualification.id")) //
                .add(Restrictions.isNull("applicationCv.id")) //
                .add(Restrictions.isNull("applicationCoveringLetter.id")) //
                .add(Restrictions.isNull("userQualification.id")) //
                .add(Restrictions.isNull("userCv.id")) //
                .add(Restrictions.isNull("portraitImage.id")) //
                .add(Restrictions.isNull("logoImage.id")) //
                .add(Restrictions.isNull("logoImageEmail.id")) //
                .add(Restrictions.isNull("backgroundImage.id")) //
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

    public List<Document> getResourceOwnerDocuments(Resource resource) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        String resourceReferenceComment = "comment." + resourceReference;
        return (List<Document>) sessionFactory.getCurrentSession().createCriteria(Document.class) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .createAlias(resourceReferenceComment, resourceReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceReferenceComment, resource)) //
                .add(Restrictions.eqProperty("user", resourceReference + ".user")) //
                .list();
    }

    private Criteria getDocumentCriteria() {
        return sessionFactory.getCurrentSession().createCriteria(Document.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("comment", "comment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationQualification", "applicationQualification", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationCv", "applicationCv", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("applicationCoveringLetter", "applicationCoveringLetter", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userQualification", "userQualification", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userCv", "userCv", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("portraitImage", "portraitImage", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("logoImage", "logoImage", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("logoImageEmail", "logoImageEmail", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("backgroundImage", "backgroundImage", JoinType.LEFT_OUTER_JOIN);
    }

}
