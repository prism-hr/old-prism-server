package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;

@Repository
@SuppressWarnings("unchecked")
public class ProjectDAO {

    private final SessionFactory sessionFactory;

    public ProjectDAO() {
        this(null);
    }

    @Autowired
    public ProjectDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Project getProjectById(Integer projectId) {
        return (Project) sessionFactory.getCurrentSession().get(Project.class, projectId);
    }

    public void save(Project project) {
        sessionFactory.getCurrentSession().saveOrUpdate(project);
    }
    
    public List<Project> getAllProjects() {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).list();
    }
    
    public List<Project> getAllProjectsForProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).
        		add(Restrictions.eq("program", program)).list();
    }
    
	public List<Project> getProgramProjectsOfWhichProjectEditor(Program program, RegisteredUser user) {
		return getProgramProjectsForWhichUserHasRoles(program, user, AuthorityGroup.PROJECTEDITOR.authorities());
	}
    
    public List<Project> getProgramProjectsForWhichUserHasRoles(Program program, RegisteredUser user, Authority... authorities) {
		List<Authority> authorRoles = Arrays.asList(authorities);
		for (Authority systemRole : AuthorityGroup.SYSTEM.authorities()) {
			if (authorRoles.contains(systemRole) && user.isInRole(systemRole)) {
				return getAllActiveProjectsForProgram(program);
			}
		}

		for (Authority programRole : AuthorityGroup.PROGRAM.authorities()) {
			if (authorRoles.contains(programRole)) {
				return getAllActiveProjectsForProgram(program);
			}
		}
		
		/* This is horrible. If we generalised it in the DB we could clean it up. */
		HashSet<Project> projects = new HashSet<Project>();
		for (Authority projectRole : AuthorityGroup.PROJECT.authorities()) {
			if (authorRoles.contains(projectRole)) {
				switch (projectRole) {
					case PROJECTADMINISTRATOR:
						projects.addAll(getProgramProjectsOfWhichAdministrator(program, user));
						authorRoles.remove(Authority.PROJECTADMINISTRATOR);
						break;
					case PROJECTAUTHOR:
						projects.addAll(getProgramProjectsOfWhichAuthor(program, user));
						authorRoles.remove(Authority.PROJECTAUTHOR);
						break;
					default: break;
				}
			}
		}
		
		List <Project> projectsFinal = new ArrayList<Project>(projects);
		projects.addAll(
				sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
					.setProjection(Projections.groupProperty("applicationForm.project"))
					.createAlias("applicationForm", "applicationForm")
					.createAlias("applicationForm.project", "project")
					.add(Restrictions.eq("user", user))
					.add(Restrictions.eq("project.disabled", false))
					.add(Restrictions.in("role.id", authorRoles))
					.add(Restrictions.not(
							Restrictions.in("project", projectsFinal)))
					.addOrder(Order.asc("project.title")).list());
		
		return projectsFinal;
	}
    
    private List<Project> getAllActiveProjectsForProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class)
        		.add(Restrictions.eq("program", program))
        		.add(Restrictions.eq("disabled", false))
        		.addOrder(Order.asc("title")).list();
    }
    
    private List<Project> getProgramProjectsOfWhichAdministrator(Program program, RegisteredUser registeredUser) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class)
        		.add(Restrictions.eq("program", program))
        		.add(Restrictions.disjunction()
        			.add(Restrictions.eq("administrator", registeredUser))
        			.add(Restrictions.eq("primarySupervisor", registeredUser)))
        		.add(Restrictions.eq("disabled", false))
        		.addOrder(Order.asc("title")).list();
    }

    private List<Project> getProgramProjectsOfWhichAuthor(Program program, RegisteredUser author) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class)
        		.add(Restrictions.eq("program", program))
        		.add(Restrictions.eq("author", author))
        		.add(Restrictions.eq("disabled", false))
        		.addOrder(Order.asc("title")).list();
    }

}