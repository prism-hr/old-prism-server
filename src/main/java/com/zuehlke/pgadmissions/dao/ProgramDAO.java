package com.zuehlke.pgadmissions.dao;

import static org.hibernate.criterion.Projections.distinct;
import static org.hibernate.criterion.Projections.property;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Repository
@SuppressWarnings("unchecked")
public class ProgramDAO {

    private final SessionFactory sessionFactory;

    public ProgramDAO() {
        this(null);
    }

    @Autowired
    public ProgramDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Program> getAllPrograms() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.asc("title")).list();
    }

    public Program getProgramById(Integer programId) {
        return (Program) sessionFactory.getCurrentSession().get(Program.class, programId);
    }

    public void save(Program program) {
        sessionFactory.getCurrentSession().saveOrUpdate(program);
    }

    public Program getProgramByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class).add(Restrictions.eq("code", code)).uniqueResult();
    }
    
	public void merge(Program program) {
		sessionFactory.getCurrentSession().merge(program);
	}
	
    public List<Program> getProgramsOfWhichPreviousInterviewer(RegisteredUser user){
        return sessionFactory.getCurrentSession().createCriteria(Interviewer.class, "u")
               .createAlias("u.interview", "i")
               .createAlias("i.application", "a")
               .add(eq("u.user", user))
               .setProjection(distinct(property("a.program")))
               .list();
    }

}
