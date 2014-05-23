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
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;

public class TestObjectProvider {

    private final SessionFactory sessionFactory;

    public TestObjectProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Action getAction(SystemAction action) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class).add(Restrictions.eq("id", action)).uniqueResult();
    }

    public Action getAction(NotificationMethod notificationMethod) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class).add(Restrictions.eq("notificationMethod", notificationMethod))
                .setMaxResults(1).uniqueResult();
    }

    public User getUser() {
        return getUser(Authority.APPLICATION_CREATOR, true, true);
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
        return getProgram(PrismState.PROGRAM_APPROVED, null);
    }

    public Program getDisabledProgram() {
        return getProgram(PrismState.PROGRAM_DISABLED, null);
    }

    public Project getEnabledProject() {
        return getProject(PrismState.PROJECT_APPROVED);
    }

    public Project getDisabledProject() {
        return getProject(PrismState.PROJECT_DISABLED);
    }

    public Program getAlternativeEnabledProgram(Program program) {
        return getProgram(PrismState.PROGRAM_APPROVED, program);
    }

    public Program getAlternativeDisabledProgram(Program program) {
        return getProgram(PrismState.PROGRAM_DISABLED, program);
    }

    public Role getRole(Authority authority) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("id", authority)).uniqueResult();
    }

    public Role getRole(Boolean doSendUpdateNotification) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("doSendUpdateNotification", doSendUpdateNotification))
                .setMaxResults(1).uniqueResult();
    }

    public Application getProgramApplication() {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .add(Restrictions.isNull("project")) //
                .setMaxResults(1).uniqueResult();
    }

    public Application getProjectApplication() {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .createAlias("project", "project", JoinType.INNER_JOIN) //
                .setMaxResults(1).uniqueResult();
    }

    public Application getApplication() {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class).setMaxResults(1).uniqueResult();
    }

    public Application getApplication(PrismState status) {
        return (Application) sessionFactory.getCurrentSession().createCriteria(Application.class).createAlias("program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.eq("state.id", status)).setMaxResults(1).uniqueResult();
    }

    private User getUser(Authority authority, Boolean isInRole, Boolean userEnabled) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class).createAlias("account", "account", JoinType.INNER_JOIN)
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN).createAlias("userRole.role", "role", JoinType.INNER_JOIN);

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

    private Program getProgram(PrismState state, Program alternativeOf) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Program.class).add(Restrictions.eq("state.id", state));

        if (alternativeOf != null) {
            criteria.add(Restrictions.ne("id", alternativeOf.getId()));
        }

        return (Program) criteria.setMaxResults(1).uniqueResult();
    }

    private Project getProject(PrismState state) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Project.class).createAlias("program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.eq("state.id", state));

        return (Project) criteria.setMaxResults(1).uniqueResult();
    }

    public Institution getInstitution() {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class)
                .add(Restrictions.eq("state.id", PrismState.INSTITUTION_APPROVED)).setMaxResults(1).uniqueResult();
    }

    public ImportedInstitution getImportedInstitution() {
        return (ImportedInstitution) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class).add(Restrictions.eq("enabled", true))
                .setMaxResults(1).uniqueResult();
    }

    public Domicile getDomicile() {
        return (Domicile) sessionFactory.getCurrentSession().createCriteria(Domicile.class).setMaxResults(1).uniqueResult();
    }

    public InstitutionDomicile getInstitutionDomicile() {
        return (InstitutionDomicile) sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class).setMaxResults(1).uniqueResult();
    }

    public InstitutionDomicile getAlternativeInstitutionDomicile(InstitutionDomicile domicile) {
        return (InstitutionDomicile) sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class)//
                .add(Restrictions.ne("id", domicile.getId())) //
                .setMaxResults(1).uniqueResult();
    }

    public System getSystem() {
        return (System) sessionFactory.getCurrentSession().createCriteria(System.class).uniqueResult();
    }

    public State getState(PrismState state) {
        return (State) sessionFactory.getCurrentSession().createCriteria(State.class) //
                .add(Restrictions.eq("id", state)) //
                .uniqueResult();
    }

    public ProgramType getProgramType() {
        return (ProgramType) sessionFactory.getCurrentSession().createCriteria(ProgramType.class).setMaxResults(1).uniqueResult();
    }

    public QualificationType getQualificationType() {
        return (QualificationType) sessionFactory.getCurrentSession().createCriteria(QualificationType.class).setMaxResults(1).uniqueResult();
    }

}
