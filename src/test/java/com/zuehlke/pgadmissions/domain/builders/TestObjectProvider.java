package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ProgramState;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ProjectState;

public class TestObjectProvider {

    private final SessionFactory sessionFactory;

    public TestObjectProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Action getAction(ApplicationFormAction action) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class).add(Restrictions.eq("id", action)).uniqueResult();
    }

    public Action getAction(NotificationMethod notificationMethod) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class).add(Restrictions.eq("notificationMethod", notificationMethod))
                .setMaxResults(1).uniqueResult();
    }

    public User getEnabledUserInRole(Authority authority) {
        return getUser(authority, true, true);
    }

    public User getDisabledUserInRole(Authority authority) {
        return getUser(authority, true, false);
    }

    public User getEnabledUserNotInRole(Authority authority) {
        return getUser(authority, false, true);
    }

    public User getDisabledUserNotInRole(Authority authority) {
        return getUser(authority, false, false);
    }

    public User getEnabledUserInRoleInEnabledProgram(Program program, Authority authority) {
        return getProgramUser(program, true, authority, true, true);
    }

    public User getDisabledUserInRoleInEnabledProgram(Program program, Authority authority) {
        return getProgramUser(program, true, authority, true, false);
    }

    public User getEnabledUserNotInRoleInEnabledProgram(Program program, Authority authority) {
        return getProgramUser(program, true, authority, false, true);
    }

    public User getDisabledUserNotInRoleInEnabledProgram(Program program, Authority authority) {
        return getProgramUser(program, true, authority, false, false);
    }

    public User getEnabledUserInRoleInDisabledProgram(Program program, Authority authority) {
        return getProgramUser(program, false, authority, true, true);
    }

    public User getDisabledUserInRoleInDisabledProgram(Program program, Authority authority) {
        return getProgramUser(program, false, authority, true, false);
    }

    public User getEnabledUserNotInRoleInDisabledProgram(Program program, Authority authority) {
        return getProgramUser(program, false, authority, false, true);
    }

    public User getDisabledUserNotInRoleInDisabledProgram(Program program, Authority authority) {
        return getProgramUser(program, false, authority, false, false);
    }

    public Program getEnabledProgram() {
        return getProgram(ProgramState.PROGRAM_APPROVED, null);
    }

    public Program getDisabledProgram() {
        return getProgram(ProgramState.PROGRAM_DISABLED, null);
    }

    public Project getEnabledProject() {
        return getProject(ProjectState.PROJECT_APPROVED);
    }

    public Project getDisabledProject() {
        return getProject(ProjectState.PROJECT_DISABLED);
    }

    public Program getAlternativeEnabledProgram(Program program) {
        return getProgram(ProgramState.PROGRAM_APPROVED, program);
    }

    public Program getAlternativeDisabledProgram(Program program) {
        return getProgram(ProgramState.PROGRAM_DISABLED, program);
    }

    public Role getRole(Authority authority) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("id", authority)).uniqueResult();
    }

    public Role getRole(Boolean doSendUpdateNotification) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("doSendUpdateNotification", doSendUpdateNotification))
                .setMaxResults(1).uniqueResult();
    }

    public Institution getInstitution() {
        return getInstitution(PrismState.INSTITUTION_APPROVED);
    }

    public ApplicationForm getEnabledProgramApplication() {
        return getProgramApplication(true);
    }

    public ApplicationForm getDisabledProgramApplication() {
        return getProgramApplication(false);
    }

    public ApplicationForm getEnabledProjectApplication() {
        return getProjectApplication(true);
    }

    public ApplicationForm getDisabledProjectApplication() {
        return getProjectApplication(false);
    }

    public ApplicationForm getApplication(PrismState status) {
        return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .createAlias("program", "program", JoinType.INNER_JOIN).add(Restrictions.eq("state.id", status)).setMaxResults(1).uniqueResult();
    }

    private User getUser(Authority authority, Boolean isInRole, Boolean userEnabled) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class)
                .createAlias("account", "account", JoinType.INNER_JOIN)
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN)
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN);

        if (BooleanUtils.isTrue(isInRole)) {
            criteria.add(Restrictions.eq("role.id", authority));
        } else {
            criteria.add(Restrictions.ne("role.id", authority));
        }

        List<Criterion> userCriteria = new ArrayList<Criterion>(4);
        userCriteria.add(Restrictions.eq("account.enabled", userEnabled));

        if (BooleanUtils.isTrue(userEnabled)) {
            for (Criterion userCriterion : userCriteria) {
                criteria.add(userCriterion);
            }
        } else {
            Disjunction disjunction = Restrictions.disjunction();
            for (Criterion userCriterion : userCriteria) {
                disjunction.add(userCriterion);
            }
            criteria.add(disjunction);
        }

        return (User) criteria.setMaxResults(1).uniqueResult();
    }

    private User getProgramUser(Program program, Boolean programEnabled, Authority authority, Boolean isInRole, Boolean userEnabled) {
        if (AuthorityGroup.getAllProgramAuthorities().contains(authority)) {
            String authorityString = authority.toString();
            String capitalisedAuthorityString = authorityString.substring(0, 1).toUpperCase() + authorityString.substring(1);

            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class)
                    .createAlias("programsOfWhich" + capitalisedAuthorityString, "program", JoinType.INNER_JOIN);

            if (BooleanUtils.isTrue(isInRole)) {
                criteria.add(Restrictions.eq("role.id", authority));
            } else {
                criteria.add(Restrictions.ne("role.id", authority));
            }

            List<Criterion> userCriteria = new ArrayList<Criterion>(4);
            userCriteria.add(Restrictions.eq("accountNonExpired", userEnabled));
            userCriteria.add(Restrictions.eq("accountNonLocked", userEnabled));
            userCriteria.add(Restrictions.eq("credentialsNonExpired", userEnabled));
            userCriteria.add(Restrictions.eq("enabled", userEnabled));

            if (BooleanUtils.isTrue(userEnabled)) {
                for (Criterion userCriterion : userCriteria) {
                    criteria.add(userCriterion);
                }
            } else {
                Disjunction disjunction = Restrictions.disjunction();
                for (Criterion userCriterion : userCriteria) {
                    disjunction.add(userCriterion);
                }
                criteria.add(disjunction);
            }

            return (User) criteria.setMaxResults(1).uniqueResult();
        }

        return null;
    }

    private Program getProgram(ProgramState state, Program alternativeOf) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Program.class).add(Restrictions.eq("state", state));

        if (alternativeOf != null) {
            criteria.add(Restrictions.ne("id", alternativeOf.getId()));
        }

        return (Program) criteria.setMaxResults(1).uniqueResult();
    }

    private ApplicationForm getProgramApplication(Boolean enabled) {
        return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .createAlias("program", "program", JoinType.INNER_JOIN).add(Restrictions.eq("program.active", enabled)).add(Restrictions.isNull("project"))
                .setMaxResults(1).uniqueResult();
    }

    private ApplicationForm getProjectApplication(Boolean enabled) {
        return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .createAlias("project", "project", JoinType.INNER_JOIN).add(Restrictions.eq("project.active", enabled)).setMaxResults(1).uniqueResult();
    }

    private Project getProject(ProjectState state) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Project.class).createAlias("program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.eq("state", state));

        return (Project) criteria.setMaxResults(1).uniqueResult();
    }

    public Institution getInstitution(PrismState state) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class)
                .add(Restrictions.eq("state.id", state)).setMaxResults(1).uniqueResult();
    }

}
