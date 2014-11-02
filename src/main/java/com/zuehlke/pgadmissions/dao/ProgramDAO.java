package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_WITHDRAWN;

import java.util.Arrays;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;

@Repository
@SuppressWarnings("unchecked")
public class ProgramDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Program getProgramByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setFetchMode("studyOptions", FetchMode.JOIN) //
                .add(Restrictions.eq("code", code)) //
                .uniqueResult();
    }

    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setFetchMode("studyOptions", FetchMode.JOIN) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("importedCode", importedCode)) //
                .uniqueResult();
    }

    public List<Program> getPrograms() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .list();
    }

    public ProgramStudyOption getEnabledProgramStudyOption(Program program, StudyOption studyOption) {
        return (ProgramStudyOption) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOption.class) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("studyOption", studyOption)) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

    public List<ProgramStudyOption> getEnabledProgramStudyOptions(Program program) {
        return (List<ProgramStudyOption>) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOption.class) //
                .createAlias("studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("studyOption.code")) //
                .list();
    }

    public ProgramStudyOptionInstance getFirstEnabledProgramStudyOptionInstance(Program program, StudyOption studyOption) {
        return (ProgramStudyOptionInstance) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOptionInstance.class) //
                .createAlias("studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("studyOption.program", program)) //
                .add(Restrictions.eq("studyOption.studyOption", studyOption)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("applicationStartDate")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<ProgramStudyOptionInstance> getProgramStudyOptionInstances(Program program) {
        return (List<ProgramStudyOptionInstance>) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOptionInstance.class) //
                .createAlias("studyOption", "studyOption", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("studyOption.program", "program")) //
                .list();
    }

    public LocalDate getProgramClosureDate(Program program) {
        return (LocalDate) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOption.class) //
                .setProjection(Projections.max("applicationCloseDate")) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

    public List<Program> getProgramsWithElapsedStudyOptions(LocalDate baseline) {
        return (List<Program>) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOption.class) //
                .setProjection(Projections.groupProperty("program")) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("program.imported", false)) //
                .add(Restrictions.lt("applicationCloseDate", baseline)) //
                .add(Restrictions.eq("enabled", true)) //
                .list();
    }

    public List<ProgramStudyOption> getElapsedStudyOptions(Program program, LocalDate baseline) {
        return (List<ProgramStudyOption>) sessionFactory.getCurrentSession().createCriteria(ProgramStudyOption.class) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("program.imported", false)) //
                .add(Restrictions.lt("applicationCloseDate", baseline)) //
                .add(Restrictions.eq("enabled", true)) //
                .list();
    }

    public void deleteProgramStudyOptions(Program program) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ProgramStudyOption " + //
                        "where program = :program") //
                .setEntity("program", program) //
                .executeUpdate();
    }

    public List<ProgramRepresentation> getSimilarPrograms(Integer institutionId, String searchTerm) {
        return (List<ProgramRepresentation>) sessionFactory.getCurrentSession().createCriteria(Program.class, "program") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .add(Restrictions.eq("institution.id", institutionId)) //
                .add(Restrictions.not( //
                        Restrictions.in("state.id", Arrays.asList(PROGRAM_REJECTED, PROGRAM_WITHDRAWN, PROGRAM_DISABLED_COMPLETED)))) //
                .add(Restrictions.ilike("title", searchTerm, MatchMode.ANYWHERE)) //
                .addOrder(Order.desc("title")) //
                .list();
    }

}
