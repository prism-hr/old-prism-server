package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;

@Repository
@SuppressWarnings("unchecked")
public class ProgramDAO {

    private final SessionFactory sessionFactory;

    public ProgramDAO() {
        this(null);
    }

    @Autowired
    public ProgramDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Program> getAllPrograms() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class)
        		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .addOrder(Order.asc("title")).list();
    }

    public Program getProgramById(Integer programId) {
        return (Program) sessionFactory.getCurrentSession().get(Program.class, programId);
    }

    public void save(Program program) {
        sessionFactory.getCurrentSession().saveOrUpdate(program);
    }

    public Program getProgramByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class)
        		.add(Restrictions.eq("code", code)).uniqueResult();
    }
    
	public void merge(Program program) {
		sessionFactory.getCurrentSession().merge(program);
	}
	
    public Date getNextClosingDateForProgram(Program program, Date today) {
        List<Date> result = (List<Date>) sessionFactory.getCurrentSession().createCriteria(ProgramClosingDate.class)
        		.setProjection(Projections.property("closingDate"))
                .add(Restrictions.eq("program", program))
                .add(Restrictions.gt("closingDate", today))
                .addOrder(Order.asc("closingDate"))
                .setMaxResults(1)
                .list();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }
	
	public List<Program> getProgramsOfWhichAdministrator(RegisteredUser user) {
		return getProgramsForWhichUserHasRoles(user, AuthorityGroup.PROGRAMADMIN.authorities());
	}
	
	public List<Program> getProgramsOfWhichAuthor(RegisteredUser user) {
		return getProgramsForWhichUserHasRoles(user, AuthorityGroup.PROGRAMAUTHOR.authorities());
	}
	
	public List<Program> getProgramsOfWhichProjectAuthor(RegisteredUser user) {
		return getProgramsForWhichUserHasRoles(user, AuthorityGroup.PROJECTAUTHOR.authorities());
	}
	
	public List<Program> getProgramsOfWhichProjectEditor(RegisteredUser user) {
		return getProgramsForWhichUserHasRoles(user, AuthorityGroup.PROJECTEDITOR.authorities());
	}
	
	public List<Program> getProgramsForWhichUserHasRoles(RegisteredUser user, Authority... authorities) {
		List<Authority> authorRoles = Arrays.asList(authorities);
		for (Authority systemRole : AuthorityGroup.SYSTEM.authorities()) {
			if (authorRoles.contains(systemRole) && user.isInRole(systemRole)) {
				return getAllActivePrograms();
			}
		}
		
		/* This is horrible. If we generalised it in the DB we could clean it up. */
		HashSet<Program> programs = new HashSet<Program>();
		for (Authority programRole : AuthorityGroup.PROGRAM.authorities()) {
			if (authorRoles.contains(programRole)) {
				switch (programRole) {
					case ADMINISTRATOR:
						programs.addAll(getEnabledPrograms(user.getProgramsOfWhichAdministrator()));
						authorRoles.remove(Authority.ADMINISTRATOR);
						break;
					case APPROVER:
						programs.addAll(getEnabledPrograms(user.getProgramsOfWhichApprover()));
						authorRoles.remove(Authority.APPROVER);
						break;
					case VIEWER:
						programs.addAll(getEnabledPrograms(user.getProgramsOfWhichViewer()));
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
						programs.addAll(getProgramsOfWhichAssignedProjectAdministrator(user));
						authorRoles.remove(Authority.PROJECTADMINISTRATOR);
						break;
					case PROJECTAUTHOR:
						programs.addAll(getProgramsOfWhichAssignedProjectAuthor(user));
						authorRoles.remove(Authority.PROJECTAUTHOR);
						break;
					default: break;
				}
			}
		}
		
		List <Program> programsFinal = new ArrayList<Program>(programs);
		programs.addAll(
				sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
					.setProjection(Projections.groupProperty("applicationForm.program"))
					.createAlias("applicationForm", "applicationForm")
					.add(Restrictions.eq("user", user))
					.add(Restrictions.eq("enabled", true))
					.add(Restrictions.in("role.id", authorRoles))
					.add(Restrictions.not(
							Restrictions.in("applicationForm.program", programsFinal))).list());
		
		return programsFinal;
	}
	
	private List<Program> getAllActivePrograms() {
        return sessionFactory.getCurrentSession().createCriteria(Program.class)
        		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
        		.add(Restrictions.eq("enabled", true))
                .addOrder(Order.asc("title")).list();
    }
	
	private List<Program> getProgramsOfWhichAssignedProjectAdministrator(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(Program.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.createAlias("projects", "project")
				.add(Restrictions.eq("enabled", true))
				.add(Restrictions.eq("project.disabled", false))
				.add(Restrictions.disjunction()
	        			.add(Restrictions.eq("project.administrator", user))
	        			.add(Restrictions.eq("project.primarySupervisor", user))).list();
	}
	
	private List<Program> getProgramsOfWhichAssignedProjectAuthor(RegisteredUser user) {
		return sessionFactory.getCurrentSession().createCriteria(Program.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.createAlias("projects", "project")
				.add(Restrictions.eq("enabled", true))
				.add(Restrictions.eq("project.disabled", false))
				.add(Restrictions.eq("project.author", user)).list();
	}
	
	private List<Program> getEnabledPrograms(List<Program> programs) {
		List<Program> enabledPrograms = new ArrayList<Program>();
		for (Program program : programs) {
			if (program.isEnabled()) {
				enabledPrograms.add(program);
			}
		}
		return enabledPrograms;
	}

}