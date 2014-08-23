package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;

@Repository
@SuppressWarnings("unchecked")
public class AdvertDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Advert> getActiveAdverts(List<Integer> activeProgramIds, List<Integer> activeProjectIds) {
        return sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.in("id", activeProgramIds))
                        .add(Restrictions.in("id", activeProjectIds))) //
                .add(Restrictions.ge("publishDate", new LocalDate())) //
                .list();
    }
    
}
