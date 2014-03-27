package com.zuehlke.pgadmissions.dao;

import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.EnumType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionDefinition;

@Repository
@SuppressWarnings("unchecked")
public class ActionDAO {

    @Autowired
	private SessionFactory sessionFactory;
	
    public Action getById(ApplicationFormAction actionId) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .add(Restrictions.eq("id", actionId)).uniqueResult();
    }
    
    public List<ApplicationFormAction> getActionIdByActionType(ActionType actionType) {
        return (List<ApplicationFormAction>) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("actionType", actionType)).list();
    }
    
    public List<ActionDefinition> selectUserActions(Integer applicationFormId, Integer registeredUserId) {
        return selectUserActionsBase(applicationFormId, registeredUserId, null, null);
    }
    
    public List<ActionDefinition> selectUserActionById(Integer applicationFormId, Integer registeredUserId, ApplicationFormAction action) {
        return selectUserActionsBase(applicationFormId, registeredUserId, action, null);
    }
    
    public List<ActionDefinition> selectUserActionByActionType(Integer applicationFormId, Integer registeredUserId, ActionType actionType) {
        return selectUserActionsBase(applicationFormId, registeredUserId, null, actionType);
    }
    
    public void deleteApplicationActions(ApplicationForm applicationForm) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_APPLICATION_ACTIONS(?);")
                .setInteger(0, applicationForm.getId()).executeUpdate();
    }
    
    public void deleteStateActions(ApplicationForm applicationForm) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_STATE_ACTIONS(?);")
                .setInteger(0, applicationForm.getId()).executeUpdate();
    }

    public void deleteRoleAction(ApplicationForm applicationForm, Authority authority, ApplicationFormAction action) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_ROLE_ACTION(?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setString(1, authority.toString())
                .setString(2, action.toString()).executeUpdate();
    }

    public void deleteUserAction(ApplicationForm applicationForm, RegisteredUser registeredUser, Authority authority, ApplicationFormAction action) {
       sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_USER_ACTION(?, ?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, registeredUser.getId())
                .setString(2, authority.toString())
                .setString(3, action.toString()).executeUpdate();
    }
    
    private List<ActionDefinition> selectUserActionsBase(Integer applicationFormId, Integer registeredUserId, ApplicationFormAction action, ActionType actionType) {
        Properties customDTOProperties = new Properties();
        customDTOProperties.put("enumClass", ApplicationFormAction.class.getCanonicalName());
        customDTOProperties.put("type", "12");
        Type actionEnum = new TypeLocatorImpl(new TypeResolver()).custom(EnumType.class, customDTOProperties);
        
        return (sessionFactory.getCurrentSession().createSQLQuery("CALL SP_SELECT_USER_ACTIONS(?, ?, ?, ?);")
                .addScalar("action_id", actionEnum)
                .addScalar("raises_urgent_flag", BooleanType.INSTANCE)
                .setInteger(0, applicationFormId)
                .setInteger(1, registeredUserId))
                .setString(2, action.toString())
                .setString(3, actionType.toString())
                .setResultTransformer(Transformers.aliasToBean(ActionDefinition.class)).list();
    }
	
}
