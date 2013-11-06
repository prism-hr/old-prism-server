package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionDefinition;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;

@Repository
@SuppressWarnings("unchecked")
public class ApplicationFormUserRoleDAO {

    private final SessionFactory sessionFactory;

    public ApplicationFormUserRoleDAO() {
        this(null);
    }

    @Autowired
    public ApplicationFormUserRoleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(ApplicationFormUserRole applicationFormUserRole) {
        sessionFactory.getCurrentSession().save(applicationFormUserRole);
    }

    public List<ApplicationFormUserRole> findByApplicationFormAndUser(ApplicationForm applicationForm, RegisteredUser user) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("user", user)).list();
    }

    public List<ApplicationFormUserRole> findByApplicationFormAndAuthorityUpdateVisility(ApplicationForm applicationForm, int updateVisibility) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.eq("role.updateVisibility", updateVisibility)).list();
    }

    public ApplicationFormUserRole findByApplicationFormAndUserAndAuthority(ApplicationForm applicationForm, RegisteredUser user, Authority authority) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .add(Restrictions.eq("applicationForm", applicationForm)).add(Restrictions.eq("user", user)).add(Restrictions.eq("role.id", authority))
                .uniqueResult();
    }

    public List<ApplicationFormUserRole> findByApplicationFormAndAuthorities(ApplicationForm applicationForm, Authority... authorities) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", applicationForm))
                .add(Restrictions.in("role.id", authorities)).list();
    }

    public List<ApplicationFormUserRole> findByUserAndProgramAndAuthority(RegisteredUser registeredUser, Program program, Authority authority) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).createAlias("applicationForm.program", "program")
                .add(Restrictions.eq("registeredUser", registeredUser)).add(Restrictions.eq("program", program)).add(Restrictions.eq("role.id", authority))
                .list();
    }

    public List<ApplicationFormUserRole> findByUserAndAuthority(RegisteredUser registeredUser, Authority authority) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("registeredUser", registeredUser))
                .add(Restrictions.eq("role.id", authority)).list();
    }

    public List<ApplicationFormUserRole> findByApplicationForm(ApplicationForm applicationForm) {
        return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class).add(Restrictions.eq("applicationForm", applicationForm)).list();
    }

    public ApplicationFormUserRole get(Integer id) {
        return (ApplicationFormUserRole) sessionFactory.getCurrentSession().get(ApplicationFormUserRole.class, id);
    }

    public void delete(ApplicationFormUserRole applicationFormUserRole) {
        sessionFactory.getCurrentSession().delete(applicationFormUserRole);
    }

    public void delete(ApplicationFormActionRequired action) {
        sessionFactory.getCurrentSession().delete(action);
    }

    public void clearActions(ApplicationFormUserRole role) {
        role.getActions().clear();
        sessionFactory.getCurrentSession().flush();
    }

    public List<ActionDefinition> findActionsByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        List<Object[]> actionObjects = (List<Object[]>) sessionFactory.getCurrentSession() //
                .createCriteria(ApplicationFormActionRequired.class) //
                .createAlias("applicationFormUserRole", "role") //
                .add(Restrictions.eq("role.applicationForm", applicationForm)) //
                .add(Restrictions.eq("role.user", user)) //
                .setProjection(Projections.projectionList().add(Projections.groupProperty("action")).add(Projections.max("raisesUrgentFlag"))).list();

        List<ActionDefinition> actionDefinitions = Lists.newArrayListWithCapacity(actionObjects.size());
        for (Object[] actionObject : actionObjects) {
            ActionDefinition actionDefinition = new ActionDefinition((String) actionObject[0], (Boolean) actionObject[1]);
            actionDefinitions.add(actionDefinition);
        }
        return actionDefinitions;
    }

    public Boolean findRaisesUpdateFlagByUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm) {
        return (Boolean) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)//
                .add(Restrictions.eq("applicationForm", applicationForm)) //
                .add(Restrictions.eq("user", user)) //
                .setProjection(Projections.projectionList().add(Projections.max("raisesUpdateFlag"))) //
                .uniqueResult();
    }

    public boolean checkActionAvailableForUserAndApplicationForm(RegisteredUser user, ApplicationForm applicationForm, ApplicationFormAction action) {
        Object result = sessionFactory.getCurrentSession() //
                .createCriteria(ApplicationFormActionRequired.class) //
                .createAlias("applicationFormUserRole", "role") //
                .add(Restrictions.eq("role.applicationForm", applicationForm)) //
                .add(Restrictions.eq("role.user", user)) //
                .add(Restrictions.eq("action", action.name()))
                .setProjection(Projections.projectionList().add(Projections.groupProperty("action"))).uniqueResult();
        return result != null;
    }
}
