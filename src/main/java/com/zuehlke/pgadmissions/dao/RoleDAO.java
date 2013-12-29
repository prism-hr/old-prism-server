package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;

@Repository
@SuppressWarnings("unchecked")
public class RoleDAO {

	private final SessionFactory sessionFactory;
	
	public RoleDAO() {
	    this(null);
	}

	@Autowired
	public RoleDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Role getRoleByAuthority(final Authority authority) {
		return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).
				add(Restrictions.eq("id", authority)).uniqueResult();
	}
	
    public List<Authority> getUserRolesForSystem(RegisteredUser user) {
    	return user.getAuthoritiesForSystem();
    }
    
    public List<Authority> getUserRolesForProgram(Program program, RegisteredUser user) {
    	HashSet<Authority> roles = new HashSet<Authority>(getUserRolesForSystem(user));
    	roles.addAll(user.getAuthoritiesForProgram(program));
    	
    	for (Project project : program.getProjects()) {
    		roles.addAll(user.getAuthoritiesForProject(project));
    		if (roles.containsAll(Arrays.asList(AuthorityGroup.PROJECT.authorities()))) {
    			break;
    		}
    	}
    	
    	roles.addAll(
    		sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
        		.setProjection(Projections.groupProperty("role.id"))
        		.createAlias("applicationForm", "applicationForm")
        		.createAlias("applicationForm.program", "program")
        		.add(Restrictions.eq("program", program))
        		.add(Restrictions.not(
        				Restrictions.in("role.id", roles)))
        		.add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("user", user)).list());
    	
    	return new ArrayList<Authority>(roles);
    }
    
    public List<Authority> getUserRolesForProject(Project project, RegisteredUser user) {
    	HashSet<Authority> roles = new HashSet<Authority>(user.getAuthoritiesForSystem());
    	roles.addAll(user.getAuthoritiesForProgram(project.getProgram()));
    	roles.addAll(user.getAuthoritiesForProject(project));
    	
    	roles.addAll(
    		sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
        		.setProjection(Projections.groupProperty("role.id"))
        		.createAlias("applicationForm", "applicationForm")
        		.createAlias("applicationForm.project", "project")
        		.add(Restrictions.eq("project", project))
        		.add(Restrictions.not(
        				Restrictions.in("role.id", roles)))
        		.add(Restrictions.eq("project.disabled", false))
                .add(Restrictions.eq("user", user)).list());
    	
    	return new ArrayList<Authority>(roles);
    }
    
    public List<Authority> getUserRolesForApplication(ApplicationForm applicationForm, RegisteredUser user) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
        		.setProjection(Projections.groupProperty("role.id"))
        		.add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user)).list();
    }
    
}