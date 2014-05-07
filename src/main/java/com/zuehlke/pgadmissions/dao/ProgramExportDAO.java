package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ProgramExport;

@Repository
public class ProgramExportDAO {

    private final SessionFactory sessionFactory;

    public ProgramExportDAO() {
        this(null);
    }

    @Autowired
    public ProgramExportDAO(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public ProgramExport getById(Integer id) {
        return (ProgramExport) sessionFactory.getCurrentSession().get(ProgramExport.class, id);
    }
    
    public void save(final ProgramExport feed) {
        sessionFactory.getCurrentSession().saveOrUpdate(feed);
    }

    @SuppressWarnings("unchecked")
    public List<ProgramExport> getAllFeedsForUser(final User user) {
        return sessionFactory.getCurrentSession()
                .createCriteria(ProgramExport.class).add(Restrictions.eq("user", user))
                .addOrder(Order.asc("title")).list();
    }

    @SuppressWarnings("unchecked")
    public boolean isUniqueFeedTitleForUser(final String title, final User user) {
        List<Integer> list = sessionFactory.getCurrentSession().createCriteria(ProgramExport.class)
                .setProjection(Projections.property("id")).add(Restrictions.ilike("title", title, MatchMode.EXACT))
                .add(Restrictions.eq("user", user)).list();
        return list.isEmpty();
    }

    public void deleteById(final Integer feedId) {
        sessionFactory.getCurrentSession().delete(getById(feedId));
    }
}
