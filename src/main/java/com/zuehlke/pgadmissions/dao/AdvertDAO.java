package com.zuehlke.pgadmissions.dao;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFilterCategory;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;

@Repository
@SuppressWarnings("unchecked")
public class AdvertDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Integer> getAdverts(List<PrismState> programStates, List<PrismState> projectStates, OpportunitiesQueryDTO queryDTO) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("address", "address", JoinType.INNER_JOIN) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.institution", "institution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.department", "department", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.programType", "programType", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("program.studyOptions", "programStudyOption", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("programStudyOption.studyOption", "studyOption", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.program", "projectProgram", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("projectProgram.institution", "projectInstitution", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("projectProgram.department", "projectDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("projectProgram.programType", "projectProgramType", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("projectProgram.studyOptions", "projectProgramStudyOption", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("projectProgramStudyOption.studyOption", "projectStudyOption", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.userRoles", "userRole", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.in("userRole.role.id", Arrays.asList(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROJECT_SECONDARY_SUPERVISOR))) //
                .createAlias("userRole.user", "user", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("program")) //
                                .add(Restrictions.in("program.state.id", programStates))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("project")) //
                                .add(Restrictions.in("project.state.id", projectStates))));

        appendLocationConstraint(criteria, queryDTO);
        appendKeywordConstraint(queryDTO, criteria);

        appendProgramTypeConstraint(criteria, queryDTO);
        appendStudyOptionConstraint(queryDTO, criteria);
        appendFeeConstraint(criteria, queryDTO);
        appendPayConstraint(criteria, queryDTO);
        appendDurationConstraint(criteria, queryDTO);
        
        appendInstitutionsConstraint(criteria, queryDTO);
        appendDepartmentsConstraint(queryDTO, criteria);
        appendProgramsConstraint(queryDTO, criteria);
        appendProjectsConstraint(queryDTO, criteria);

        String lastSequenceIdentifier = queryDTO.getLastSequenceIdentifier();
        if (lastSequenceIdentifier != null) {
            criteria.add(Restrictions.lt("sequenceIdentifier", lastSequenceIdentifier));
        }

        return (List<Integer>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
                .setMaxResults(25) //
                .list();
    }

    public List<Advert> getActiveAdverts(List<Integer> adverts, boolean prioritizeProgram) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class);

        if (prioritizeProgram) {
            criteria.createAlias("program", "program", JoinType.LEFT_OUTER_JOIN);
        }

        criteria.add(Restrictions.in("id", adverts));

        if (prioritizeProgram) {
            criteria.addOrder(Order.desc("program.id"));
        }

        return (List<Advert>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
                .list();
    }

    public List<Advert> getRecommendedAdverts(User user, List<PrismState> activeProgramStates, List<PrismState> activeProjectStates) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("application.advert")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.applications", "application", JoinType.INNER_JOIN) //
                .createAlias("application.advert", "recommendedAdvert", JoinType.INNER_JOIN) //
                .createAlias("recommendedAdvert.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("recommendedAdvert.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.ne("application.user", user)) //
                .add(Restrictions.isNotNull("application.submittedTimestamp")) //
                .add(Restrictions.neProperty("advert", "application.advert")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("recommendedAdvert.program")) //
                                .add(Restrictions.in("program.state.id", activeProgramStates))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("recommendedAdvert.project")) //
                                .add(Restrictions.in("project.state.id", activeProjectStates)))) //
                .addOrder(Order.desc("application.submittedTimestamp")) //
                .addOrder(Order.desc("recommendedAdvert.sequenceIdentifier")) //
                .setMaxResults(25) //
                .list();
    }

    public List<Advert> getAdvertsWithElapsedClosingDates(LocalDate baseline) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .createAlias("closingDate", "closingDate", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("closingDates", "otherClosingDate", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions //
                        .disjunction() //
                        .add(Restrictions.lt("closingDate.closingDate", baseline)) //
                        .add(Restrictions.conjunction().add(Restrictions.isNull("closingDate.id")) //
                                .add(Restrictions.ge("otherClosingDate.closingDate", baseline)))) //
                .list();
    }

    public AdvertClosingDate getNextAdvertClosingDate(Advert advert, LocalDate baseline) {
        return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(AdvertClosingDate.class) //
                .add(Restrictions.eq("advert", advert)) //
                .add(Restrictions.ge("closingDate", baseline)) //
                .addOrder(Order.asc("closingDate")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<Advert> getAdvertsWithElapsedCurrencyConversions(LocalDate baseline, List<PrismState> activeProgramStates, List<PrismState> activeProjectStates) {
        return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
                .createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("program")) //
                                .add(Restrictions.in("program.state.id", activeProgramStates))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("project")) //
                                .add(Restrictions.in("project.state.id", activeProjectStates)))) //
                .add(Restrictions.lt("lastCurrencyConversionDate", baseline)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("fee.currencySpecified")) //
                                .add(Restrictions.isNotNull("fee.currencyAtLocale")) //
                                .add(Restrictions.neProperty("fee.currencySpecified", "fee.currencyAtLocale"))).add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("pay.currencySpecified")) //
                                .add(Restrictions.isNotNull("pay.currencyAtLocale")) //
                                .add(Restrictions.neProperty("pay.currencySpecified", "pay.currencyAtLocale")))).list();
    }

    public List<String> getLocalizedTags(Institution institution, Class<? extends AdvertFilterCategory> clazz) {
        String propertyName = clazz.getSimpleName().replace("Advert", "").toLowerCase();
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(clazz) //
                .setProjection(Projections.groupProperty(propertyName)) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.program", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.project", "project", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("project.program", "projectProgram", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNull("project.institution")) //
                                .add(Restrictions.eq("program.institution", institution))) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNull("program.institution")) //
                                .add(Restrictions.eq("projectProgram.institution", institution)))) //
                .list();
    }

    public List<String> getLocalizedProgramThemes(Program program) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(AdvertTheme.class) //
                .setProjection(Projections.groupProperty("theme")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.program", "program", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("program.id", program.getId())) //
                .list();
    }

    public List<String> getLocalizedProjectThemes(Project project) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(AdvertTheme.class) //
                .setProjection(Projections.groupProperty("theme")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.project", "project", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("project.id", project.getId())) //
                .list();
    }

    private void appendLocationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        if (queryDTO.getNeLat() != null) {
            criteria.add(Restrictions.between("address.location.locationX", queryDTO.getNeLon(), queryDTO.getNeLat()));
            criteria.add(Restrictions.between("address.location.locationY", queryDTO.getSwLat(), queryDTO.getSwLon()));
        }
    }

    private void appendKeywordConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        String keyword = queryDTO.getKeyword();
        if (keyword != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.ilike("title", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("summary", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("description", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("project.title", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("program.title", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("department.title", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("institution.title", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("institution.summary", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectProgram.title", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectDepartment.title", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectInstitution.title", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("projectInstitution.summary", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("user.firstName", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("user.lastName", keyword, MatchMode.ANYWHERE)) //
                    .add(Restrictions.ilike("user.email", keyword, MatchMode.ANYWHERE))); //
        }
    }

    private void appendProgramTypeConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        Collection<PrismProgramType> programTypes = queryDTO.getProgramTypes();
        programTypes = programTypes == null ? PrismProgramType.getProgramTypes(queryDTO.getProgramCategory()) : programTypes;

        Disjunction programTypeConstraint = Restrictions.disjunction();
        for (PrismProgramType programType : programTypes) {
            String programTypeReference = programType.name();
            programTypeConstraint //
                    .add(Restrictions.eq("programType.code", programTypeReference)) //
                    .add(Restrictions.eq("projectProgramType.code", programTypeReference));

        }
        criteria.add(programTypeConstraint);
    }

    private void appendStudyOptionConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        List<PrismStudyOption> studyOptions = queryDTO.getStudyOptions();
        Disjunction studyOptionConstraint = Restrictions.disjunction();
        if (studyOptions != null) {
            for (PrismStudyOption studyOption : studyOptions) {
                studyOptionConstraint //
                        .add(Restrictions.eq("studyOption.code", studyOption.name())) //
                        .add(Restrictions.eq("projectStudyOption.code", studyOption.name())); //
            }
        }
        criteria.add(studyOptionConstraint);
    }

    private void appendFeeConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        appendRangeConstraint(criteria, "fee.monthMinimumAtLocale", "fee.monthMaximumAtLocale", queryDTO.getMinFee(), queryDTO.getMaxFee(), true);
    }

    private void appendPayConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        appendRangeConstraint(criteria, "pay.monthMinimumAtLocale", "pay.monthMaximumAtLocale", queryDTO.getMinSalary(), queryDTO.getMaxSalary(), true);
    }

    private void appendDurationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        appendRangeConstraint(criteria, "studyDurationMinimum", "studyDurationMaximum", queryDTO.getMinDuration(), queryDTO.getMaxDuration(), false);
    }

    private void appendInstitutionsConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
        Integer[] institutions = queryDTO.getInstitutions();
        if (institutions != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.in("institution.id", institutions)) //
                    .add(Restrictions.in("projectInstitution.id", institutions))); //
        }
    }

    private void appendDepartmentsConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        Integer[] departments = queryDTO.getDepartments();
        if (departments != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.in("department.id", departments)) //
                    .add(Restrictions.in("projectDepartment.id", departments)));
        }
    }
    
    private void appendProgramsConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        Integer[] programs = queryDTO.getPrograms();
        if (programs != null) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.in("program.id", programs)) //
                    .add(Restrictions.in("projectProgram.id", programs)));
        }
    }

    private void appendProjectsConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
        Integer[] projects = queryDTO.getProjects();
        if (projects != null) {
            criteria.add(Restrictions.in("project.id", projects));
        }
    }

    private void appendRangeConstraint(Criteria criteria, String loColumn, String hiColumn, Integer loValue, Integer hiValue, boolean decimal) {
        if (loValue == null && hiValue == null) {
            return;
        } else if (loValue == 0 && hiValue == 0) {
            return;
        }

        Junction conjunction = Restrictions.conjunction();
        if (loValue != null) {
            conjunction.add(Restrictions.ge(loColumn, hiValue != null && hiValue < loValue ? hiValue : decimal ? new BigDecimal(loValue) : loValue));
        }

        if (hiValue != null) {
            conjunction.add(Restrictions.le(hiColumn, loValue != null && loValue > hiValue ? loValue : decimal ? new BigDecimal(hiValue) : hiValue));
        }
        criteria.add(conjunction);
    }

}
