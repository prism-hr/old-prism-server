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
    
    public List<Project> getAllActiveProjects() {
        return sessionFactory.getCurrentSession().createCriteria(Project.class)
        		.add(Restrictions.eq("disabled", false)).list();
    }
    
    public List<Project> getAllProjectsForProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).
        		add(Restrictions.eq("program", program)).list();
    }

    public List<Project> getAllActiveProjectsForProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).
        		add(Restrictions.eq("program", program)).
        		add(Restrictions.eq("disabled", false)).list();
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
		
		/* This is horrible. If we generalised it in the DB we could clean it up. */
		HashSet<Project> projects = new HashSet<Project>();
		for (Authority programRole : AuthorityGroup.PROGRAM.authorities()) {
			if (authorRoles.contains(programRole)) {
				switch (programRole) {
					case ADMINISTRATOR:
						projects.addAll(getEnabledProjects(program.getProjects()));
						authorRoles.remove(Authority.ADMINISTRATOR);
						break;
					case APPROVER:
						projects.addAll(getEnabledProjects(program.getProjects()));
						authorRoles.remove(Authority.APPROVER);
						break;
					case VIEWER:
						projects.addAll(getEnabledProjects(program.getProjects()));
						authorRoles.remove(Authority.VIEWER);
						break;
					default: break;
				}
			}
		}
		
		/* As per previous code block. */
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
					.add(Restrictions.eq("user", user))
					.add(Restrictions.eq("enabled", true))
					.add(Restrictions.in("role.id", authorRoles))
					.add(Restrictions.not(
							Restrictions.in("applicationForm.project", projectsFinal))).list());
		
		return projectsFinal;
	}
    
    private List<Project> getProgramProjectsOfWhichAdministrator(Program program, RegisteredUser registeredUser) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).
        		add(Restrictions.eq("program", program)).
        		add(Restrictions.disjunction().
        			add(Restrictions.eq("administrator", registeredUser)).
        			add(Restrictions.eq("primarySupervisor", registeredUser))).
        		add(Restrictions.eq("disabled", false)).list();
    }

    private List<Project> getProgramProjectsOfWhichAuthor(Program program, RegisteredUser author) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).
        		add(Restrictions.eq("program", program)).
        		add(Restrictions.eq("author", author)).
        		add(Restrictions.eq("disabled", false)).list();
    }
    
	private List<Project> getEnabledProjects(List<Project> projects) {
		List<Project> enabledProjects = new ArrayList<Project>();
		for (Project project : projects) {
			if (!project.isDisabled()) {
				enabledProjects.add(project);
			}
		}
		return enabledProjects;
	}

}