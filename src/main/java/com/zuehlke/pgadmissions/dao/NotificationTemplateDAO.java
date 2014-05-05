package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;

@Repository
public class NotificationTemplateDAO {

    private final SessionFactory sessionFactory;

    public NotificationTemplateDAO() {
        this(null);
    }

    @Autowired
    public NotificationTemplateDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public NotificationTemplate getById(NotificationTemplateId templateId) {
        return (NotificationTemplate) sessionFactory.getCurrentSession().createCriteria(NotificationTemplate.class).add(Restrictions.eq("id", templateId))
                .uniqueResult();
    }

    public NotificationTemplateVersion getVersionById(Integer id) {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer saveVersion(NotificationTemplateVersion version) {
        return (Integer) sessionFactory.getCurrentSession().save(version);
    }
}
