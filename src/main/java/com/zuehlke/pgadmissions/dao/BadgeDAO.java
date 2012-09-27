package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;

@Repository
public class BadgeDAO {

    private final SessionFactory sessionFactory;
    
    BadgeDAO(){
        this(null);
    }
    
    @Autowired
    public BadgeDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Badge badge) {
        sessionFactory.getCurrentSession().saveOrUpdate(badge);
    }
    
    @SuppressWarnings("unchecked")
    public List<Badge> getBadgesByProgram(Program program){
        return (List<Badge>) sessionFactory.getCurrentSession().createCriteria(Badge.class).add(Restrictions.eq("program", program)).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Badge> getDuplicateBadges(Badge badge) {
        return (List<Badge>) sessionFactory.getCurrentSession()
                .createCriteria(Badge.class)
                .add(Restrictions.eq("program", badge.getProgram()))
                .add(Restrictions.or(Restrictions.isNull("closingDate"), Restrictions.eq("closingDate", badge.getClosingDate())))
                .add(Restrictions.or(Restrictions.isNull("projectTitle"), Restrictions.eq("projectTitle", badge.getProjectTitle())))
                .list();
    }
}
