package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramUserRole;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.ProjectUserRole;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.SystemUserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityScope;

@Repository
@SuppressWarnings("unchecked")
public class RoleDAO {

    @Autowired
	private SessionFactory sessionFactory;
	
    public Role getById(final Authority id) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class)
                .add(Restrictions.eq("id", id)).uniqueResult();
    }
    
	public void saveRole(final Role role) {
	    sessionFactory.getCurrentSession().merge(role);
	}
	
	public void saveSystemUserRole(SystemUserRole systemUserRole) {
        sessionFactory.getCurrentSession().merge(systemUserRole);
	}
    
    public void saveInstitutionUserRole(InstitutionUserRole institutionUserRole) {
        sessionFactory.getCurrentSession().merge(institutionUserRole);
    }
    
    public void saveProgramUserRole(ProgramUserRole programUserRole) {
        sessionFactory.getCurrentSession().merge(programUserRole);
    }
    
    public void saveProjectUserRole(ProjectUserRole projectUserRole) {
        sessionFactory.getCurrentSession().merge(projectUserRole);
    }
    
    public void saveApplicationFormUserRole(ApplicationFormUserRole applicationFormUserRole) {
        sessionFactory.getCurrentSession().merge(applicationFormUserRole);
    }

    public List<Role> getSystemUserRoles(RegisteredUser user) {
        return sessionFactory.getCurrentSession().createCriteria(SystemUserRole.class)
                .setProjection(Projections.groupProperty("id.role"))
                .createAlias("id.user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.primaryAccount", user))
                        .add(Restrictions.eq("id.user", user))).list();
    }
    
    public List<Role> getInstitutionUserRoles(Institution institution, RegisteredUser user) {
        List<Role> institutionRoles = sessionFactory.getCurrentSession().createCriteria(InstitutionUserRole.class)
                .setProjection(Projections.groupProperty("id.role"))
                .createAlias("id.user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("id.institution", institution))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.primaryAccount", user))
                        .add(Restrictions.eq("id.user", user))).list();
        institutionRoles.addAll(getSystemUserRoles(user));
        return institutionRoles;
    }
    
    public List<Role> getProgramUserRoles(Program program, RegisteredUser user) {
        List<Role> programRoles = sessionFactory.getCurrentSession().createCriteria(ProgramUserRole.class)
                .setProjection(Projections.groupProperty("id.role"))
                .createAlias("id.user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("id.program", program))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.primaryAccount", user))
                        .add(Restrictions.eq("id.user", user))).list();
        programRoles.addAll(getInstitutionUserRoles(program.getInstitution(), user));
        return programRoles;
    }
    
    public List<Role> getProjectUserRoles(Project project, RegisteredUser user) {
        List<Role> projectRoles = sessionFactory.getCurrentSession().createCriteria(ProjectUserRole.class)
                .setProjection(Projections.groupProperty("role"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("project", project))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.primaryAccount", user))
                        .add(Restrictions.eq("user", user))).list();
        projectRoles.addAll(getProgramUserRoles(project.getProgram(), user));
        return projectRoles;
    }
    
    public List<Role> getApplicationFormUserRoles(ApplicationForm application, RegisteredUser user) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("id.role"))
                .createAlias("id.user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("id.applicationForm", application))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.primaryAccount", user))
                        .add(Restrictions.eq("id.user", user))).list();
    }
    
    public List<Role> getUserRoles(RegisteredUser user) {
        List<Role> roles = getSystemUserRoles(user);
        
        roles.addAll(sessionFactory.getCurrentSession().createCriteria(InstitutionUserRole.class)
                .setProjection(Projections.groupProperty("id.role"))
                .createAlias("id.user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.primaryAccount", user))
                        .add(Restrictions.eq("id.user", user))).list());
        
        roles.addAll(sessionFactory.getCurrentSession().createCriteria(ProgramUserRole.class)
                .setProjection(Projections.groupProperty("id.role"))
                .createAlias("id.user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.primaryAccount", user))
                        .add(Restrictions.eq("id.user", user))).list());
        
        roles.addAll(sessionFactory.getCurrentSession().createCriteria(ProjectUserRole.class)
                .setProjection(Projections.groupProperty("id.role"))
                .createAlias("id.user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.primaryAccount", user))
                        .add(Restrictions.eq("id.user", user))).list());
        
        roles.addAll(sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("id.role"))
                .createAlias("id.user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("id.role", "role", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("registeredUser.primaryAccount", user))
                        .add(Restrictions.eq("id.user", user)))
                .add(Restrictions.eq("role.authorityScope", AuthorityScope.APPLICATION)).list());
        
        return roles;
    }
	
}
