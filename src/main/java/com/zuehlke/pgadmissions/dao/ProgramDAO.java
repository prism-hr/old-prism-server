package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;

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

    public AdvertClosingDate getNextClosingDate(Program program) {
        return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .setProjection(Projections.min("futureClosingDate.closingDate")) //
                .createAlias("advert.closingDate", "currentClosingDate") //
                .createAlias("advert.closingDates", "futureClosingDate") //
                .add(Restrictions.eq("id", program.getId())) //
                .add(Restrictions.gtProperty("futureClosingDate.closingDate", "currentClosingDate.closingDate")) //
                .uniqueResult();
    }

    public List<Program> getPrograms() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .list();
    }
    
    public ProgramInstance getEarliestProgramInstance(Application application) {
        return (ProgramInstance) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class) //
                .add(Restrictions.eq("program", application.getProgram())) //
                .add(Restrictions.eq("studyOption", application.getProgramDetails().getStudyOption())) //
                .add(Restrictions.ge("applicationDeadline", new LocalDate()))
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("applicationStartDate")) //
                .setMaxResults(1) //
                .uniqueResult();  
    }
    
    public ProgramInstance getExportProgramInstance(Application application) {
        LocalDate preferredStartDate = application.getProgramDetails().getStartDate();
        return (ProgramInstance) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class) //
                .add(Restrictions.eq("program", application.getProgram())) //
                .add(Restrictions.eq("studyOption", application.getProgramDetails().getStudyOption())) //
                .add(Restrictions.le("applicationStartDate", preferredStartDate)) //
                .add(Restrictions.ge("applicationDeadline", preferredStartDate)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("applicationStartDate")) //
                .setMaxResults(1) //
                .uniqueResult();
    }
    
    public ProgramInstance getLatestProgramInstance(Application application) {
        return (ProgramInstance) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class) //
                .add(Restrictions.eq("program", application.getProgram())) //
                .add(Restrictions.eq("studyOption", application.getProgramDetails().getStudyOption())) //
                .add(Restrictions.ge("applicationStartDate", new LocalDate()))
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.desc("applicationStartDate")) //
                .setMaxResults(1) //
                .uniqueResult();     
    }
    
    public ProgramInstance getLatestProgramInstance(Program program) {
        return (ProgramInstance) sessionFactory.getCurrentSession().createCriteria(ProgramInstance.class) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.desc("applicationStartDate")) //
                .setMaxResults(1) //
                .uniqueResult();     
    }

    public List<Program> getProgramsWithElapsedClosingDates() {
        return (List<Program>) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.closingDate", "closingDate", JoinType.INNER_JOIN) //
                .add(Restrictions.lt("closingDate.closingDate", new LocalDate())) //
                .list();
    }

}
