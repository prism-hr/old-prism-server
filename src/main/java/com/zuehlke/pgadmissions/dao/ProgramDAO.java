package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DEACTIVATED;

import java.util.Arrays;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.imported.StudyOption;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.program.ProgramLocation;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.workflow.State;
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

    public void deleteProgramStudyOptionInstances(Program program) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ProgramStudyOptionInstance " + //
                        "where studyOption in ( " + //
                        "from ProgramStudyOption " + //
                        "where program = :program)") //
                .setEntity("program", program) //
                .executeUpdate();
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

    public State getPreviousState(Program program) {
        return (State) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("state")) //
                .add(Restrictions.eq("program", program)) // /
                .add(Restrictions.isNotNull("state")) //
                .add(Restrictions.ne("state", program.getState())) //
                .add(Restrictions.in("state.id", Arrays.asList(PROGRAM_APPROVED, PROJECT_DEACTIVATED))) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<String> getPossibleLocations(Program program) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(ProgramLocation.class) //
                .setProjection(Projections.property("location")) //
                .add(Restrictions.eq("program", program)) //
                .addOrder(Order.asc("location")) //
                .list();
    }

    public List<String> listSuggestedDivisions(Program program, String location) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("studyDetail.studyDivision")) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("studyDetail.studyLocation", location)) //
                .add(Subqueries.in(location, DetachedCriteria.forClass(ProgramLocation.class) //
                        .setProjection(Projections.property("location")) //
                        .add(Restrictions.eq("program", program)))) //
                .list();
    }

    public List<String> listSuggestedStudyAreas(Program program, String location, String division) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("studyDetail.studyArea")) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("studyDetail.studyLocation", location)) //
                .add(Restrictions.eq("studyDetail.studyDivision", division)) //
                .add(Subqueries.in(location, DetachedCriteria.forClass(ProgramLocation.class) //
                        .setProjection(Projections.property("location")) //
                        .add(Restrictions.eq("program", program)))) //
                .list();
    }

    public Long getActiveProgramCount(Institution institution) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.countDistinct("id")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("stateAction.action.id", PrismAction.PROGRAM_CREATE_APPLICATION)) //
                .uniqueResult();
    }
    
}
