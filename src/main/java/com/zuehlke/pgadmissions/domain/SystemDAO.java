package com.zuehlke.pgadmissions.domain;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SystemDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends IUniqueResource> void deleteWorkflowResource(Class<T> workflowResourceClass) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete " + workflowResourceClass.getSimpleName()) //
                .executeUpdate();
    }

    public <T extends WorkflowResourceConfiguration> void deleteObseleteWorkflowResourceConfiguration(Class<T> workflowResourceConfigurationClass,
            List<State> configurableStates) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete " + workflowResourceConfigurationClass.getSimpleName() + " " //
                        + "where state not in (:configurableStates)") //
                .setParameterList("configurableStates", configurableStates) //
                .executeUpdate();
    }

}
