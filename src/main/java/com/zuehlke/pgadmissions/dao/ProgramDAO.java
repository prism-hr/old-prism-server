package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;

@Repository
@SuppressWarnings("unchecked")
public class ProgramDAO {
    // TODO reimplement getProgramsOfWhichPrevious*() methods

    private SessionFactory sessionFactory;

    public ProgramDAO() {
    }

    @Autowired
    public ProgramDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Advert getById(Integer advertId) {
        return (Advert) sessionFactory.getCurrentSession().createCriteria(Advert.class).add(Restrictions.eq("id", advertId)).uniqueResult();
    }

    public Advert getAcceptingApplicationsById(Integer advertId) {
        Advert project = (Advert) sessionFactory.getCurrentSession() //
                .createCriteria(Project.class)//
                .add(Restrictions.eq("id", advertId)) //
                .add(Restrictions.eq("state.id", PrismState.PROJECT_APPROVED))//
                .uniqueResult();
        if (project != null) {
            return project;
        }
        return (Advert) sessionFactory.getCurrentSession() //
                .createCriteria(Program.class)//
                .add(Restrictions.eq("id", advertId)) //
                .add(Restrictions.eq("state.id", PrismState.PROGRAM_APPROVED))//
                .uniqueResult();
    }

    public Program getProgramByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setFetchMode("instances", FetchMode.JOIN).add(Restrictions.eq("code", code)) //
                .uniqueResult();
    }

    public Program getProgamAcceptingApplicationsByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class).add(Restrictions.eq("code", code))
                .add(Restrictions.eq("state.id", PrismState.PROGRAM_APPROVED)).uniqueResult();
    }

    public String getProgramIdByCode(String code) {
        return (String) sessionFactory.getCurrentSession().createCriteria(Program.class).setProjection(Projections.property("id"))
                .add(Restrictions.eq("code", code)).uniqueResult().toString();
    }

    public void save(Advert advert) {
        sessionFactory.getCurrentSession().saveOrUpdate(advert);
    }

    public void merge(Advert advert) {
        sessionFactory.getCurrentSession().merge(advert);
    }

    public void saveStudyOption(StudyOption studyOption) {
        sessionFactory.getCurrentSession().saveOrUpdate(studyOption);
    }

    public List<Program> getAllEnabledPrograms() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq("state.id", PrismState.PROGRAM_APPROVED)).addOrder(Order.asc("title")).list();
    }

    public List<Program> getProgramsForWhichUserCanManageProjects(User user) {
        // TODO implement
        return null;
    }

    public Program getLastCustomProgram(Institution institution) {
        DetachedCriteria maxCustomCode = DetachedCriteria.forClass(Program.class).setProjection(Projections.max("code"))
                .add(Restrictions.eq("institution", institution));
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class).add(Property.forName("code").eq(maxCustomCode)).uniqueResult();
    }

    public AdvertClosingDate getClosingDateById(final Integer id) {
        return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(AdvertClosingDate.class).add(Restrictions.eq("id", id)).uniqueResult();
    }

    public AdvertClosingDate getClosingDateByDate(final Program advert, final LocalDate date) {
        return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(AdvertClosingDate.class).add(Restrictions.eq("advert", advert))
                .add(Restrictions.eq("closingDate", date)).addOrder(Order.desc("id")).setMaxResults(1).uniqueResult();
    }

    public LocalDate getNextClosingDate(Program advert) {
        return (LocalDate) sessionFactory.getCurrentSession().createCriteria(AdvertClosingDate.class).setProjection(Projections.min("closingDate"))
                .add(Restrictions.eq("advert", advert)).add(Restrictions.ge("closingDate", new LocalDate())).uniqueResult();
    }

    public void updateClosingDate(AdvertClosingDate closingDate) {
        if (closingDate != null) {
            Advert advert = closingDate.getAdvert();
            if (advert != null) {
                sessionFactory.getCurrentSession().update(closingDate);
                save(advert);
            }
        }
    }

    public void deleteClosingDate(AdvertClosingDate closingDate) {
        if (closingDate != null) {
            Advert advert = closingDate.getAdvert();
            if (advert != null) {
                sessionFactory.getCurrentSession().delete(closingDate);
                save(advert);
            }
        }
    }

    public List<ProgramType> getProgamTypes() {
        return (List<ProgramType>) sessionFactory.getCurrentSession().createCriteria(ProgramType.class).list();
    }

    public ProgramType getProgramTypeById(ProgramTypeId programTypeId) {
        return (ProgramType) sessionFactory.getCurrentSession().createCriteria(ProgramType.class).add(Restrictions.eq("id", programTypeId)).uniqueResult();
    }

    public Integer getDefaultStudyDurationForProgramType(ProgramTypeId programTypeId) {
        return (Integer) sessionFactory.getCurrentSession().createCriteria(ProgramType.class).setProjection(Projections.property("defaultStudyDuration"))
                .add(Restrictions.eq("id", programTypeId)).uniqueResult();
    }

    public List<Program> getEnabledProgramsForWhichUserHasProgramAuthority(User user) {
        HashSet<Program> programs = new HashSet<Program>();
        for (Authority authority : AuthorityGroup.INTERNAL_PROGRAM_AUTHORITIES.getAuthorities()) {
            programs.addAll((List<Program>) sessionFactory.getCurrentSession().createCriteria(Program.class)
                    .createAlias(authority.toString().toLowerCase() + "s", "registeredUser", JoinType.INNER_JOIN).add(Restrictions.eq("enabled", true))
                    .add(Restrictions.eq("registeredUser.id", user.getId())).list());
        }
        return new ArrayList<Program>(programs);
    }

    public List<Program> getEnabledProgramsForWhichUserHasProjectAuthority(User user) {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(Project.class)
                .setProjection(Projections.groupProperty("program"))
                .createAlias("program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction().add(Restrictions.eq("primarySupervisor", user)).add(Restrictions.eq("administrator", user))
                        .add(Restrictions.eq("secondarySupervisor", user)).add(Restrictions.eq("program.enabled", true))).list();
    }

    public List<Program> getEnabledProgramsForWhichUserHasApplicationAuthority(User user) {
        return sessionFactory.getCurrentSession().createCriteria(UserRole.class).setProjection(Projections.groupProperty("applicationForm.program"))
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN).createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("role", "applicationRole", JoinType.INNER_JOIN).add(Restrictions.eq("user", user))
                .add(Restrictions.in("applicationRole.id", AuthorityGroup.INTERNAL_APPLICATION_AUTHORITIES.getAuthorities()))
                .add(Restrictions.eq("program.enabled", true)).list();
    }

    public List<Project> getProjectsForProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).add(Restrictions.eq("program", program))
                .add(Restrictions.eq("state.id", PrismState.PROJECT_APPROVED)).list();
    }

    public List<Project> getProjectsForProgramOfWhichAuthor(Program program, User author) {
        // TODO check also for primarySupervisor role
        return sessionFactory.getCurrentSession() //
                .createCriteria(Project.class) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("user", author)) //
                .add(Restrictions.eq("state.id", PrismState.PROJECT_APPROVED)).list();
    }

    public void deleteInactiveAdverts() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_INACTIVE_ADVERTS();");
        query.executeUpdate();
    }

    public Date getDefaultStartDate(Program program, StudyOption studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (Date) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).setProjection(Projections.min("applicationDeadline"))
                .add(Restrictions.eq("program", program)).add(Restrictions.eq("enabled", true)).add(Restrictions.eq("studyOption", studyOption))
                .add(Restrictions.ge("applicationDeadline", today)).uniqueResult();
    }

    public List<ProgramInstance> getActiveProgramInstances(Program program) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<ProgramInstance>) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).add(Restrictions.eq("program", program))
                .add(Restrictions.eq("enabled", true)).add(Restrictions.ge("applicationDeadline", today)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    public List<StudyOption> getAvailableStudyOptions(Program program) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<StudyOption>) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class)
                .setProjection(Projections.groupProperty("studyOption")).createAlias("studyOption", "studyOption", JoinType.INNER_JOIN)
                .add(Restrictions.eq("program", program)).add(Restrictions.eq("enabled", true)).add(Restrictions.ge("applicationDeadline", today))
                .addOrder(Order.asc("studyOption.displayName")).list();
    }

    public List<ProgramInstance> getActiveProgramInstancesForStudyOption(Program program, StudyOption studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<ProgramInstance>) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).add(Restrictions.eq("program", program))
                .add(Restrictions.eq("studyOption", studyOption)).add(Restrictions.eq("enabled", true)).add(Restrictions.ge("applicationDeadline", today))
                .addOrder(Order.asc("applicationStartDate")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<StudyOption> getAvailableStudyOptions() {
        // TODO Auto-generated method stub
        return null;
    }

    public ProgramInstance getProgramInstance(Program program, StudyOption studyOption, Date date) {
        // TODO Auto-generated method stub
        return null;
    }

    public DateTimeZone getLatestActiveInstanceDeadline(Program program) {
        // TODO Auto-generated method stub
        return null;
    }

    public void save(ProgramInstance programInstance) {
        // TODO Auto-generated method stub
    }

    public List<Program> getAllPrograms() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class).list();
    }

    public ProgramInstance getByProgramAndAcademicYearAndStudyOption(Program program, String academicYear, StudyOption studyOption) {
        return (ProgramInstance) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("academicYear", academicYear)) //
                .add(Restrictions.eq("studyOption", studyOption)) //
                .uniqueResult();
    }

}
