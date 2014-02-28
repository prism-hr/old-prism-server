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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;

public class TestObjectProvider {
    
    private final SessionFactory sessionFactory;
    
    public TestObjectProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public Action getAction(ApplicationFormAction action) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .add(Restrictions.eq("id", action)).uniqueResult();
    }
    
    public Action getAction(NotificationMethod notificationMethod) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .add(Restrictions.eq("notification", notificationMethod))
                .setMaxResults(1).uniqueResult();
    }
    
    public RegisteredUser getEnabledUserInRole(Authority authority) {
        return getUser(authority, true, true);
    }
    
    public RegisteredUser getDisabledUserInRole(Authority authority) {
        return getUser(authority, true, false);
    }
    
    public RegisteredUser getEnabledUserNotInRole(Authority authority) {
        return getUser(authority, false, true);
    }
    
    public RegisteredUser getDisabledUserNotInRole(Authority authority) {
        return getUser(authority, false, false);
    }
    
    public RegisteredUser getEnabledUserInRoleInEnabledProgram(Program program, Authority authority) {
        return getProgramUser(program, true, authority, true, true);
    }
    
    public RegisteredUser getDisabledUserInRoleInEnabledProgram(Program program, Authority authority) {
        return getProgramUser(program, true, authority, true, false);
    }
    
    public RegisteredUser getEnabledUserNotInRoleInEnabledProgram(Program program, Authority authority) {
        return getProgramUser(program, true, authority, false, true);
    }
    
    public RegisteredUser getDisabledUserNotInRoleInEnabledProgram(Program program, Authority authority) {
        return getProgramUser(program, true, authority, false, false);
    }
    
    public RegisteredUser getEnabledUserInRoleInDisabledProgram(Program program, Authority authority) {
        return getProgramUser(program, false, authority, true, true);
    }
    
    public RegisteredUser getDisabledUserInRoleInDisabledProgram(Program program, Authority authority) {
        return getProgramUser(program, false, authority, true, false);
    }
    
    public RegisteredUser getEnabledUserNotInRoleInDisabledProgram(Program program, Authority authority) {
        return getProgramUser(program, false, authority, false, true);
    }
    
    public RegisteredUser getDisabledUserNotInRoleInDisabledProgram(Program program, Authority authority) {
        return getProgramUser(program, false, authority, false, false);
    }
    
    public Program getEnabledProgram() {
        return getProgram(true, null);
    }
    
    public Program getDisabledProgram() {
        return getProgram(true, null);
    }
    
    public Project getEnabledProject() {
        return getProject(true, null);
    }
    
    public Project getDisabledProject() {
        return getProject(true, null);
    }
    
    public Program getAlternativeEnabledProgram(Program program) {
        return getProgram(true, program);
    }
    
    public Program getAlternativeDisabledProgram(Program program) {
        return getProgram(true, program);
    }
    
    public Project getAlternativeEnabledProject(Project project) {
        return getProject(true, project);
    }
    
    public Project getAlternativeDisabledProject(Project project) {
        return getProject(true, project);
    }
    
    public Role getRole(Authority authority) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class)
                .add(Restrictions.eq("id", authority)).uniqueResult();
    }
    
    public Role getRole(Boolean doSendUpdateNotification) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class)
                .add(Restrictions.eq("doSendUpdateNotification", doSendUpdateNotification))
                .setMaxResults(1).uniqueResult();
    }
    
    public QualificationInstitution getEnabledInstitution() {
        return getInstitution(true);
    }
    
    public QualificationInstitution getDisabledInstitution() {
        return getInstitution(false);
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
    
    private RegisteredUser getUser(Authority authority, Boolean isInRole, Boolean userEnabled) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .createAlias("applicationFormUserRoles", "applicationFormUserRole", JoinType.INNER_JOIN)
                .createAlias("applicationFormUserRole.role", "role", JoinType.INNER_JOIN);
       
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
        
        return (RegisteredUser) criteria.setMaxResults(1).uniqueResult();
    }
    
    private RegisteredUser getProgramUser(Program program, Boolean programEnabled, Authority authority, Boolean isInRole, Boolean userEnabled) {
        if (AuthorityGroup.getAllProgramAuthorities().contains(authority)) {
            String authorityString = authority.toString();
            String capitalisedAuthorityString = authorityString.substring(0, 1).toUpperCase() + authorityString.substring(1);
            
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
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
            
            return (RegisteredUser) criteria.setMaxResults(1).uniqueResult();
        }
        
        return null;
    }
    
    private Program getProgram(Boolean enabled, Program alternativeOf) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Program.class)
                .add(Restrictions.eq("enabled", enabled));
        
        if (alternativeOf != null) {
            criteria.add(Restrictions.ne("id", alternativeOf.getId()));
        }
        
        return (Program) criteria.setMaxResults(1).uniqueResult();
    }
    
    private ApplicationForm getProgramApplication(Boolean enabled) {
        return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .createAlias("program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.eq("program.enabled", enabled))
                .add(Restrictions.isNull("project"))
                .setMaxResults(1).uniqueResult();
    }
    
    private ApplicationForm getProjectApplication(Boolean enabled) {
        return (ApplicationForm) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .createAlias("project", "project", JoinType.INNER_JOIN)
                .add(Restrictions.eq("project.enabled", enabled))
                .setMaxResults(1).uniqueResult();
    }
    
    private Project getProject(Boolean enabled, Project alternativeOf) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Project.class)
                .createAlias("program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.eq("enabled", enabled))
                .add(Restrictions.eq("program.enabled", enabled));
       
        if (alternativeOf != null) {
            criteria.add(Restrictions.eq("id", alternativeOf.getId()));
        }
       
        return (Project) criteria.setMaxResults(1).uniqueResult();
    }
    
    public QualificationInstitution getInstitution(Boolean enabled) {
        return (QualificationInstitution) sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class)
                .add(Restrictions.eq("enabled", enabled))
                .setMaxResults(1).uniqueResult();
    }
    
}
