package com.zuehlke.pgadmissions.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;

@Repository
@SuppressWarnings("unchecked")
public class BadgeDAO {

    private final SessionFactory sessionFactory;
    
    public BadgeDAO(){
        this(null);
    }
    
    @Autowired
    public BadgeDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Badge badge) {
        sessionFactory.getCurrentSession().saveOrUpdate(badge);
    }
    
    public List<Badge> getBadgesByProgram(Program program){
        return (List<Badge>) sessionFactory.getCurrentSession().createCriteria(Badge.class)
                .add(Restrictions.eq("program", program)).list();
    }

    public List<Badge> getAllProjectTitlesByProgramFilteredByNameLikeCaseInsensitive(Program program, String term) {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(Badge.class)
                .add(Restrictions.and(Restrictions.eq("program", program),
                        Restrictions.ilike("projectTitle", term, MatchMode.ANYWHERE)))
                .addOrder(Order.asc("projectTitle")).list();
    }
    
    public List<Badge> getDuplicateBadges(Badge badge) {
        return (List<Badge>) sessionFactory
                .getCurrentSession()
                .createCriteria(Badge.class)
                .add(Restrictions.eq("program", badge.getProgram()))
                .add(Restrictions.or(Restrictions.isNull("closingDate"),
                        Restrictions.eq("closingDate", badge.getClosingDate())))
                .add(Restrictions.or(Restrictions.isNull("projectTitle"),
                        Restrictions.eq("projectTitle", badge.getProjectTitle()))).list();
    }
    
    public Date getNextClosingDateForProgram(Program program, Date today) {
        List<Date> result = (List<Date>) sessionFactory.getCurrentSession().createCriteria(Badge.class).setProjection(Projections.property("closingDate"))
                .add(Restrictions.eq("program", program))
                .add(Restrictions.gt("closingDate", today))
                .addOrder(Order.asc("closingDate"))
                .setMaxResults(1)
                .list();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }
}
