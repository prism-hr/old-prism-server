package com.zuehlke.pgadmissions.domain;

import java.beans.Introspector;
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

    public <T extends WorkflowResourceConfiguration, U extends WorkflowResource> void deleteObseleteWorkflowResourceConfiguration(
            Class<T> workflowResourceConfigurationClass, List<U> configurableResources) {
        if (!configurableResources.isEmpty()) {
            String configurableResourceClass = Introspector.decapitalize(configurableResources.get(0).getClass().getSimpleName());
            sessionFactory.getCurrentSession().createQuery( //
                    "delete " + workflowResourceConfigurationClass.getSimpleName() + " " //
                            + "where state not in (:configurableStates)") //
                    .setParameterList(configurableResourceClass, configurableResources) //
                    .executeUpdate();
        }
    }

}
