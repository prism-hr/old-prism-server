package com.zuehlke.pgadmissions.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
public class AdvertDAO {

    private final SessionFactory sessionFactory;

    public AdvertDAO() {
        this(null);
    }

    @Autowired
    public AdvertDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Advert advert) {
        sessionFactory.getCurrentSession().saveOrUpdate(advert);
    }

    public Program getProgram(Advert advert) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .add(Restrictions.eq("advert", advert)).uniqueResult();
    }

    public Project getProject(Advert advert) {
        return (Project) sessionFactory.getCurrentSession().createCriteria(Project.class)
                .add(Restrictions.eq("advert", advert)).uniqueResult();
    }

    public Advert getAdvertById(int advertId) {
        return (Advert) sessionFactory.getCurrentSession().get(Advert.class, advertId);
    }

    @SuppressWarnings("unchecked")
    public List<Advert> getActiveAdverts() {
        return sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .add(Restrictions.eq("active", true)).list();
    }

    @SuppressWarnings("unchecked")
    public List<Advert> getRecommendedAdverts(RegisteredUser applicant) {
        return (List<Advert>) sessionFactory.getCurrentSession()
                .createSQLQuery("CALL SELECT_RECOMMENDED_ADVERT(?, ?);")
                .addEntity(Advert.class)
                .setInteger(0, applicant.getId())
                .setBigDecimal(1, new BigDecimal(0.01)).list();
    }

    public void delete(Advert advert) {
        if (advert == null || advert.getId() == null) {
            return;
        }
        sessionFactory.getCurrentSession().delete(advert);
    }
    
}
