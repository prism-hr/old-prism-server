package com.zuehlke.pgadmissions.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertDomicile;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Component
public class TestObjectProvider {

    private SessionFactory sessionFactory;

    @Autowired
    public TestObjectProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Action getAction(Action action) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class).add(Restrictions.eq("id", action)).uniqueResult();
    }

    public Action getAction(PrismNotificationType notificationMethod) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class).add(Restrictions.eq("notificationMethod", notificationMethod))
                .setMaxResults(1).uniqueResult();
    }

    public User getUser() {
        return getUser(PrismRole.APPLICATION_CREATOR, true, true);
    }

    public User getEnabledUserInRole(PrismRole authority) {
        return getUser(authority, true, true);
    }

    public User getDisabledUserInRole(PrismRole authority) {
        return getUser(authority, true, false);
    }

    public User getEnabledUserNotInRole(PrismRole authority) {
        return getUser(authority, false, true);
    }

    public User getDisabledUserNotInRole(PrismRole authority) {
        return getUser(authority, false, false);
    }

    public User getEnabledUserInRoleInEnabledProgram(Program program, PrismRole authority) {
        return getProgramUser(program, true, authority, true, true);
    }

    public User getDisabledUserInRoleInEnabledProgram(Program program, PrismRole authority) {
        return getProgramUser(program, true, authority, true, false);
    }

    public User getEnabledUserNotInRoleInEnabledProgram(Program program, PrismRole authority) {
        return getProgramUser(program, true, authority, false, true);
    }

    public User getDisabledUserNotInRoleInEnabledProgram(Program program, PrismRole authority) {
        return getProgramUser(program, true, authority, false, false);
    }

    public User getEnabledUserInRoleInDisabledProgram(Program program, PrismRole authority) {
        return getProgramUser(program, false, authority, true, true);
    }

    public User getDisabledUserInRoleInDisabledProgram(Program program, PrismRole authority) {
        return getProgramUser(program, false, authority, true, false);
    }

    public User getEnabledUserNotInRoleInDisabledProgram(Program program, PrismRole authority) {
        return getProgramUser(program, false, authority, false, true);
    }

    public User getDisabledUserNotInRoleInDisabledProgram(Program program, PrismRole authority) {
        return getProgramUser(program, false, authority, false, false);
    }

    public Program getEnabledProgram() {
        return getProgram(PrismState.PROGRAM_APPROVED, null);
    }

    public Program getDisabledProgram() {
        return getProgram(PrismState.PROGRAM_DISABLED_COMPLETED, null);
    }

    public Project getEnabledProject() {
        return getProject(PrismState.PROJECT_APPROVED);
    }

    public Project getDisabledProject() {
        return getProject(PrismState.PROJECT_DISABLED_COMPLETED);
    }

    public Program getAlternativeEnabledProgram(Program program) {
        return getProgram(PrismState.PROGRAM_APPROVED, program);
    }

    public Program getAlternativeDisabledProgram(Program program) {
        return getProgram(PrismState.PROGRAM_DISABLED_COMPLETED, program);
    }

    public Role getRole(PrismRole authority) {
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

    private User getUser(PrismRole authority, Boolean isInRole, Boolean userEnabled) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class).createAlias("userAccount", "userAccount", JoinType.INNER_JOIN)
                .createAlias("userRoles", "userRole", JoinType.INNER_JOIN).createAlias("userRole.role", "role", JoinType.INNER_JOIN);

        if (BooleanUtils.isTrue(isInRole)) {
            criteria.add(Restrictions.eq("role.id", authority));
        } else {
            criteria.add(Restrictions.ne("role.id", authority));
        }

        List<Criterion> userCriteria = new ArrayList<Criterion>(4);
        userCriteria.add(Restrictions.eq("userAccount.enabled", userEnabled));

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

    private User getProgramUser(Program program, Boolean programEnabled, PrismRole authority, Boolean isInRole, Boolean userEnabled) {
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

    public ImportedEntitySimple getDomicile() {
        return (ImportedEntitySimple) sessionFactory.getCurrentSession().createCriteria(ImportedEntitySimple.class).setMaxResults(1).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> entityClass) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass).setMaxResults(1).uniqueResult();
    }

    public AdvertDomicile getInstitutionDomicile() {
        return (AdvertDomicile) sessionFactory.getCurrentSession().createCriteria(AdvertDomicile.class).setMaxResults(1).uniqueResult();
    }

    public AdvertDomicile getAlternativeInstitutionDomicile(AdvertDomicile domicile) {
        return (AdvertDomicile) sessionFactory.getCurrentSession().createCriteria(AdvertDomicile.class)//
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

    public ImportedEntitySimple getQualificationType() {
        return (ImportedEntitySimple) sessionFactory.getCurrentSession().createCriteria(ImportedEntitySimple.class).setMaxResults(1).uniqueResult();
    }

}
