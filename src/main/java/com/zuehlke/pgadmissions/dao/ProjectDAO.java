package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

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

    public List<Project> getProjectsForProgram(Program program) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).
        		add(Restrictions.eq("program", program)).
        		add(Restrictions.eq("enabled", true)).list();
    }

    public List<Project> getProjectsForProgramOfWhichAuthor(Program program, RegisteredUser author) {
        return sessionFactory.getCurrentSession().createCriteria(Project.class).
        		add(Restrictions.eq("program", program)).
        		add(Restrictions.disjunction().
        			add(Restrictions.eq("author", author)).
        			add(Restrictions.eq("administrator", author)).
        			add(Restrictions.eq("primarySupervisor", author))).
        		add(Restrictions.eq("enabled", true)).list();
    }
    
}