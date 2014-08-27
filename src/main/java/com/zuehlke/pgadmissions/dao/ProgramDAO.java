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
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.StudyOption;

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

    public List<Program> getPrograms() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .list();
    }
    
    public ProgramStudyOption getProgramStudyOption(Program program, StudyOption studyOption) {
        return (ProgramStudyOption) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOption.class) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("studyOption", studyOption)) //
                .uniqueResult();
    }
    
    public ProgramStudyOptionInstance getProgramStudyOptionInstance(Program program, StudyOption studyOption, LocalDate startDate) {
        return (ProgramStudyOptionInstance) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOptionInstance.class) //
                .add(Restrictions.eq("programStudyOption.program", program)) //
                .add(Restrictions.eq("programStudyOption.studyOption", studyOption)) //
                .add(Restrictions.le("applicationStartDate", startDate)) //
                .add(Restrictions.ge("applicationCloseDate", startDate)) //
                .add(Restrictions.eq("enabled", true)) //
                .createAlias("programStudyOption", "programStudyOption", JoinType.INNER_JOIN) //
                .addOrder(Order.asc("applicationStartDate")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<Program> getProgramsWithElapsedClosingDates(LocalDate baseline) {
        return (List<Program>) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.closingDate", "closingDate", JoinType.INNER_JOIN) //
                .add(Restrictions.lt("closingDate.closingDate", baseline)) //
                .list();
    }
    
    public AdvertClosingDate getNextClosingDate(Program program, LocalDate baseline) {
        return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.min("closingDate.closingDate")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.closingDates", "closingDate", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", program.getId())) //
                .add(Restrictions.ge("closingDate.closingDate", baseline)) //
                .uniqueResult();
    }
    
    public List<Program> getProgramsWithElapsedDefaultStartDates(LocalDate baseline) {
        return (List<Program>) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOption.class) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("imported", false)) //
                .add(Restrictions.isNotNull("groupStartFrequency")) //
                .add(Restrictions.lt("defaultStartDate", baseline)) //
                .list();
    }
    
    public LocalDate getProgramClosureDate(Program program) {
        return (LocalDate) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOption.class) //
                .setProjection(Projections.max("applicationCloseDate")) //
                .add(Restrictions.eq("program", program)) //
                .uniqueResult();
    }
    
}
