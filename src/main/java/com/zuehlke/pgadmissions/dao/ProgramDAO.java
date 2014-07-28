package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class ProgramDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    public Program getProgramByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setFetchMode("programInstances", FetchMode.JOIN) //
                .add(Restrictions.eq("code", code)) //
                .uniqueResult();
    }
    
    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setFetchMode("programInstances", FetchMode.JOIN) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("importedCode", importedCode))
                .uniqueResult();
    }

    public List<Program> getProgramsOpenForApplication() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .add(Restrictions.eq("state.id", PrismState.PROGRAM_APPROVED)) //
                .addOrder(Order.asc("title")) //
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY) //
                .list();
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
            }
        }
    }

    public void deleteClosingDate(AdvertClosingDate closingDate) {
        if (closingDate != null) {
            Advert advert = closingDate.getAdvert();
            if (advert != null) {
                sessionFactory.getCurrentSession().delete(closingDate);
            }
        }
    }

    public void deleteInactiveAdverts() {
        // TODO implement for projects - programs done
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
        return (List<StudyOption>) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class)
                .setProjection(Projections.groupProperty("studyOption")) //
                .createAlias("studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("program", program)).add(Restrictions.eq("enabled", true)) //
                .add(Restrictions.ge("applicationDeadline", new DateTime())) //
                .addOrder(Order.asc("studyOption.name")).list();
    }

    public List<ProgramInstance> getActiveProgramInstancesForStudyOption(Program program, StudyOption studyOption) {
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
        return (List<ProgramInstance>) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class).add(Restrictions.eq("program", program))
                .add(Restrictions.eq("studyOption", studyOption)).add(Restrictions.eq("enabled", true)).add(Restrictions.ge("applicationDeadline", today))
                .addOrder(Order.asc("applicationStartDate")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<StudyOption> getAvailableStudyOptions() {
        return (List<StudyOption>) sessionFactory.getCurrentSession().createCriteria(StudyOption.class) //
                .addOrder(Order.asc("name")).list();
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
    
    public List<Program> getInactiveImportedPrograms() {
        return (List<Program>) sessionFactory.getCurrentSession().createQuery( //
                "select program " //
                        + "from ProgramInstance programInstance join programInstance.program program " //
                        + "where program.importedCode is not null " //
                        + "group by program " //
                        + "having count(enabled) = 0") //
                        .list();
    }

}
