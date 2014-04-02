package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;

@Repository
@SuppressWarnings("unchecked")
public class QualificationInstitutionDAO {

    private SessionFactory sessionFactory;

    public QualificationInstitutionDAO() {
    }

    @Autowired
    public QualificationInstitutionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Institution> getEnabledInstitutionsByDomicileCode(String domicileCode) {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class).add(Restrictions.eq("enabled", true))
                .add(Restrictions.eq("domicileCode", domicileCode)).addOrder(Order.asc("name")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<Institution> getEnabledInstitutionsByUserIdAndDomicileCode(Integer userId, String domicileCode) {
        return (List<Institution>) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setProjection(Projections.groupProperty("program.institution")).createAlias("administrators", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("institution", "qualificationInstitution", JoinType.INNER_JOIN).add(Restrictions.eq("registeredUser.id", userId))
                .add(Restrictions.eq("qualificationInstitution.domicileCode", domicileCode)).add(Restrictions.eq("qualificationInstitution.enabled", true))
                .addOrder(Order.asc("institution.name")).list();
    }

    public List<Institution> getEnabledInstitutionsByDomicileCodeExludingUserId(Integer userId, String domicileCode) {
        DetachedCriteria exclusions = DetachedCriteria.forClass(Program.class).setProjection(Projections.groupProperty("program.institution"))
                .createAlias("administrators", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("institution", "qualificationInstitution", JoinType.INNER_JOIN).add(Restrictions.eq("registeredUser.id", userId))
                .add(Restrictions.eq("qualificationInstitution.domicileCode", domicileCode)).add(Restrictions.eq("qualificationInstitution.enabled", true));

        return (List<Institution>) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setProjection(Projections.groupProperty("program.institution")).createAlias("institution", "qualificationInstitution", JoinType.INNER_JOIN)
                .add(Restrictions.eq("qualificationInstitution.domicileCode", domicileCode)).add(Restrictions.eq("qualificationInstitution.enabled", true))
                .add(Property.forName("program.institution").notIn(exclusions)).addOrder(Order.asc("institution.name")).list();
    }

    public Institution getByCode(String institutionCode) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class).add(Restrictions.eq("code", institutionCode)).uniqueResult();
    }

    public Institution getByDomicileAndName(String domicileCode, String institutionName) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class).add(Restrictions.eq("domicileCode", domicileCode))
                .add(Restrictions.eq("name", institutionName)).uniqueResult();
    }

    public Institution getLastCustomInstitution() {
        DetachedCriteria maxCustomCode = DetachedCriteria.forClass(Institution.class).setProjection(Projections.max("code"))
                .add(Restrictions.like("code", "CUST%"));
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class).add(Property.forName("code").eq(maxCustomCode))
                .uniqueResult();
    }

    public void save(Institution institution) {
        sessionFactory.getCurrentSession().saveOrUpdate(institution);
    }

}
