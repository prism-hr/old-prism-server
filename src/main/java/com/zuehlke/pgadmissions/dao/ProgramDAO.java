package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;

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
    
    public Advert getById(Integer advertId) {
        return (Advert) sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .add(Restrictions.eq("id", advertId)).uniqueResult();
    }
    
    public Advert getAcceptingApplicationsById(Integer advertId) {
        return (Advert) sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .add(Restrictions.eq("id", advertId))
                .add(Restrictions.eq("active", true)).uniqueResult();
    }
    
    public Program getProgramByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .add(Restrictions.eq("code", code)).uniqueResult();
    }
    
    public Program getProgamAcceptingApplicationsByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .add(Restrictions.eq("code", code))
                .add(Restrictions.eq("active", true)).uniqueResult();
    }
    
    public String getProgramIdByCode(String code) {
        return (String) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("code", code)).uniqueResult().toString();
    }

    public void save(Advert advert) {
        sessionFactory.getCurrentSession().saveOrUpdate(advert);
    }
    
    public Advert merge(Advert advert) {
        return (Advert) sessionFactory.getCurrentSession().merge(advert);
    }

    public List<Program> getAllEnabledPrograms() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq("enabled", true))
                .addOrder(Order.asc("title")).list();
    }
    
    public List<Program> getProgramsForWhichUserCanManageProjects(RegisteredUser user) {
        if (user.isInRole(Authority.SUPERADMINISTRATOR)) {
            return getAllEnabledPrograms();
        } else {
            Set<Program> programs = new TreeSet<Program>(new Comparator<Program>() {
                @Override
                public int compare(Program p1, Program p2) {
                    int compareInstitutions = p1.getInstitution().getName().compareTo(p2.getInstitution().getName());
                    if (compareInstitutions != 0) {
                        return compareInstitutions;
                    }
                    return p1.getTitle().compareTo(p2.getTitle());
                }
            });
    
            programs.addAll(getEnabledProgramsForWhichUserHasProgramAuthority(user));
            programs.addAll(getEnabledProgramsForWhichUserHasProjectAuthority(user));
            programs.addAll(getEnabledProgramsForWhichUserHasApplicationAuthority(user));
            
            return Lists.newArrayList(programs);
        }
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
    
    public Date getNextClosingDate(Program program) {
        return (Date) sessionFactory.getCurrentSession().createCriteria(ProgramClosingDate.class)
                .setProjection(Projections.min("closingDate"))
                .add(Restrictions.eq("program", program))
                .add(Restrictions.ge("closingDate", new Date())).uniqueResult();
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
    
    public List<ProgramType> getProgamTypes() {
        return (List<ProgramType>) sessionFactory.getCurrentSession().createCriteria(ProgramType.class).list();
    }
    
    public ProgramType getProgramTypeById(ProgramTypeId programTypeId) {
        return (ProgramType) sessionFactory.getCurrentSession().createCriteria(ProgramType.class)
                .add(Restrictions.eq("id", programTypeId)).uniqueResult();
    }
    
    public Integer getDefaultStudyDurationForProgramType(ProgramTypeId programTypeId) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(ProgramType.class)
                .setProjection(Projections.property("defaultStudyDuration"))
                .add(Restrictions.eq("id", programTypeId)).uniqueResult();
    }
    
    public List<Program> getEnabledProgramsForWhichUserHasProgramAuthority(RegisteredUser user) {
        HashSet<Program> programs = new HashSet<Program>();
        for (Authority authority : AuthorityGroup.INTERNAL_PROGRAM_AUTHORITIES.getAuthorities()) {
            programs.addAll((List<Program>) sessionFactory.getCurrentSession().createCriteria(Program.class)
                    .createAlias(authority.toString().toLowerCase() + "s", "registeredUser", JoinType.INNER_JOIN)
                    .add(Restrictions.eq("enabled", true))
                    .add(Restrictions.eq("registeredUser.id", user.getId())).list());
        }
        return new ArrayList<Program>(programs);
    }
    
    public List<Program> getEnabledProgramsForWhichUserHasProjectAuthority(RegisteredUser user) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class)
                .setProjection(Projections.groupProperty("program"))
                .createAlias("program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("primarySupervisor", user))
                        .add(Restrictions.eq("administrator", user))
                        .add(Restrictions.eq("secondarySupervisor", user))
                .add(Restrictions.eq("program.enabled", true))).list();
    }
    
    public List<Program> getEnabledProgramsForWhichUserHasApplicationAuthority(RegisteredUser user) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("applicationForm.program"))
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("role", "applicationRole", JoinType.INNER_JOIN)
                .add(Restrictions.eq("user", user))
                .add(Restrictions.in("applicationRole.id", AuthorityGroup.INTERNAL_APPLICATION_AUTHORITIES.getAuthorities()))
                .add(Restrictions.eq("program.enabled", true)).list();
    }
    
    public List<Project> getProjectsForProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).
                add(Restrictions.eq("program", program)).
                add(Restrictions.eq("enabled", true)).list();
    }

    public List<Project> getProjectsForProgramOfWhichAuthor(Program program, RegisteredUser author) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).
                add(Restrictions.eq("program", program)).
                add(Restrictions.disjunction().
                    add(Restrictions.eq("contactUser", author)).
                    add(Restrictions.eq("administrator", author)).
                    add(Restrictions.eq("primarySupervisor", author))).
                add(Restrictions.eq("enabled", true)).list();
    }
    
    public void deleteInactiveAdverts() {
        Query query = sessionFactory.getCurrentSession()
                .createSQLQuery("CALL SP_DELETE_INACTIVE_ADVERTS();");
        query.executeUpdate();
    }
    
    public Date getDefaultStartDate(Program program, String studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (Date) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class)
                .setProjection(Projections.min("applicationDeadline"))
                .add(Restrictions.eq("program", program))
                .add(Restrictions.eq("enabled", true))
                .add(Restrictions.eq("studyOption", studyOption))
                .add(Restrictions.ge("applicationDeadline", today)).uniqueResult();
    }
    
}
