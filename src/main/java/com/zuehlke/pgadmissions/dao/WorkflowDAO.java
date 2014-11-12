package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_ROLE_ASSIGNMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.PROJECT_ROLE_ASSIGNMENT;

import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyDefinition;

@Repository
@SuppressWarnings("unchecked")
public class WorkflowDAO {

    @Autowired
    private SessionFactory sessionFactory;
   
    public List<WorkflowPropertyDefinition> getActiveWorkflowPropertyDefinitions() {
        return (List<WorkflowPropertyDefinition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.groupProperty("workflowPropertyDefinition")) //
                .add(Restrictions.isNotNull("workflowPropertyDefinition")) //
                .list();
    }
    
    public void deleteObseleteWorkflowPropertyConfigurations(List<WorkflowPropertyDefinition> activeWorkflowPropertyDefinitions) {
        sessionFactory.getCurrentSession().createQuery(
                "delete WorkflowPropertyConfiguration " //
                    + "where workflowPropertyDefinition not in (:activeWorkflowPropertyDefinitions) "
                        + "and workflowPropertyDefinition in ("
                            + "from workflowPropertyDefinition "
                                + "where workflowPropertyCategory in (:workflowPropertyCategories))") //
        .setParameterList("activeWorkflowPropertyDefinitions", activeWorkflowPropertyDefinitions) //
        .setParameterList("workflowPropertyCategories", Arrays.asList(APPLICATION_ROLE_ASSIGNMENT, PROJECT_ROLE_ASSIGNMENT)) //
        .executeUpdate();
    }
}
