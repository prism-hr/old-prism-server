package com.zuehlke.pgadmissions.dao;

import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
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
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionDefinition;

@Repository
@SuppressWarnings("unchecked")
public class ActionDAO {

	private SessionFactory sessionFactory;
	
    public ActionDAO() {
    }

    @Autowired
    public ActionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public Action getById(SystemAction actionId) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(Action.class)
                .add(Restrictions.eq("id", actionId)).uniqueResult();
    }
    
    public List<ActionDefinition> getUserActions(Integer applicationFormId, Integer userId) {
        return getUserActionsAbstract(applicationFormId, userId, null);
    }
    
    public List<ActionDefinition> getUserActionById(Integer applicationFormId, Integer userId, SystemAction action) {
        return getUserActionsAbstract(applicationFormId, userId, action);
    }
    
    public void deleteApplicationActions(Application applicationForm) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_APPLICATION_ACTIONS(?);")
                .setInteger(0, applicationForm.getId()).executeUpdate();
    }
    
    public void deleteStateActions(Application applicationForm) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_STATE_ACTIONS(?);")
                .setInteger(0, applicationForm.getId()).executeUpdate();
    }

    public void deleteRoleAction(Application applicationForm, Authority authority, SystemAction action) {
        sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_ROLE_ACTION(?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setString(1, authority.toString())
                .setString(2, action.toString()).executeUpdate();
    }

    public void deleteUserAction(Application applicationForm, User user, Authority authority, SystemAction action) {
       sessionFactory.getCurrentSession().createSQLQuery("CALL SP_DELETE_USER_ACTION(?, ?, ?, ?);")
                .setInteger(0, applicationForm.getId())
                .setInteger(1, user.getId())
                .setString(2, authority.toString())
                .setString(3, action.toString()).executeUpdate();
    }
    
    private List<ActionDefinition> getUserActionsAbstract(Integer applicationFormId, Integer userId, SystemAction action) {
        Properties customDTOProperties = new Properties();
        customDTOProperties.put("enumClass", SystemAction.class.getCanonicalName());
        customDTOProperties.put("type", "12");
        Type actionEnum = new TypeLocatorImpl(new TypeResolver()).custom(EnumType.class, customDTOProperties);
        
        return (sessionFactory.getCurrentSession().createSQLQuery("CALL SP_SELECT_USER_ACTIONS(?, ?, ?, ?);")
                .addScalar("action_id", actionEnum)
                .addScalar("raises_urgent_flag", BooleanType.INSTANCE)
                .setInteger(0, applicationFormId)
                .setInteger(1, userId))
                .setString(2, action.toString())
                .setResultTransformer(Transformers.aliasToBean(ActionDefinition.class)).list();
    }
	
}
