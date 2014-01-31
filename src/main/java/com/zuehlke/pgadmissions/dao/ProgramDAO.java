package com.zuehlke.pgadmissions.dao;

import static org.hibernate.criterion.Projections.distinct;
import static org.hibernate.criterion.Projections.property;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;

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
        return sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.asc("title")).list();
    }

    public Program getProgramById(Integer programId) {
        return (Program) sessionFactory.getCurrentSession().get(Program.class, programId);
    }

    public void save(Program program) {
        sessionFactory.getCurrentSession().saveOrUpdate(program);
    }

    public Program getProgramByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .add(Restrictions.eq("code", code)).uniqueResult();
    }
    
	public void merge(Program program) {
		sessionFactory.getCurrentSession().merge(program);
	}
	
    public List<Program> getProgramsOfWhichPreviousReviewer(RegisteredUser user){
        return sessionFactory.getCurrentSession().createCriteria(Reviewer.class, "r")
               .createAlias("r.reviewRound", "rr")
               .createAlias("rr.application", "a")
               .add(eq("r.user", user))
               .setProjection(distinct(property("a.program")))
               .list();
    }
	
	public List<Program> getProgramsOfWhichPreviousInterviewer(RegisteredUser user){
        return sessionFactory.getCurrentSession().createCriteria(Interviewer.class, "u")
               .createAlias("u.interview", "i")
               .createAlias("i.application", "a")
               .add(eq("u.user", user))
               .setProjection(distinct(property("a.program")))
               .list();
	}
    
	public List<Program> getProgramsOfWhichPreviousSupervisor(RegisteredUser user){
	    return sessionFactory.getCurrentSession().createCriteria(Supervisor.class, "s")
	            .createAlias("s.approvalRound", "ar")
	            .createAlias("ar.application", "a")
	            .add(eq("s.user", user))
	            .setProjection(distinct(property("a.program")))
	            .list();
	}
	
    public Program getLastCustomProgram(QualificationInstitution institution) {
        String matcher = String.format("%s_%%", institution.getCode());
        DetachedCriteria maxCustomCode = DetachedCriteria.forClass(Program.class).setProjection(Projections.max("code"))
                .add(Restrictions.like("code", matcher));
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .add(Property.forName("code").eq(maxCustomCode)).uniqueResult();
    }
    
    public ProgramClosingDate getClosingDateById(final Integer id) {
        return (ProgramClosingDate) sessionFactory.getCurrentSession().createCriteria(ProgramClosingDate.class)
            .add(Restrictions.eq("id", id)).uniqueResult();
    }
    
    public ProgramClosingDate getClosingDateByDate(final Program program, final Date date) {
        return (ProgramClosingDate) sessionFactory.getCurrentSession().createCriteria(ProgramClosingDate.class)
                .add(Restrictions.eq("program", program))
                .add(Restrictions.eq("closingDate", date))
                .addOrder(Order.desc("id"))
                .setMaxResults(1).uniqueResult();
    }
    
    public ProgramClosingDate getNextClosingDate(Program program) {
        return (ProgramClosingDate) sessionFactory.getCurrentSession().createCriteria(ProgramClosingDate.class)
                .add(Restrictions.eq("program", program))
                .add(Restrictions.ge("closingDate", new Date()))
                .addOrder(Order.desc("id"))
                .setMaxResults(1).uniqueResult();
    }

    public void updateClosingDate(ProgramClosingDate closingDate) {
        if (closingDate != null) {
            Program program = closingDate.getProgram();
            if (program != null) {
                sessionFactory.getCurrentSession().update(closingDate);
                save(program);
            }
        }
    }
    
    public void deleteClosingDate(ProgramClosingDate closingDate) {
        if (closingDate != null) {
            Program program = closingDate.getProgram();
            if (program != null) {
                sessionFactory.getCurrentSession().delete(closingDate);
                save(program);
            }
        }
    }
    
    public RegisteredUser getFirstAdministratorForProgram(Program program) {
        return program.getAdministrators().get(0);
    }
    
}
