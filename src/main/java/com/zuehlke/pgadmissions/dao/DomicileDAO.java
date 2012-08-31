package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Domicile;

@Repository
public class DomicileDAO {

    private final SessionFactory sessionFactory;

    DomicileDAO() {
        this(null);
    }

    @Autowired
    public DomicileDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public List<Domicile> getAllDomiciles() {
        return sessionFactory.getCurrentSession().createCriteria(Domicile.class).addOrder(Order.asc("name")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public Domicile getDomicileById(Integer id) {
        return (Domicile) sessionFactory.getCurrentSession().get(Domicile.class, id);
    }
}
