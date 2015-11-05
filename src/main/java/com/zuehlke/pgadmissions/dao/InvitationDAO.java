package com.zuehlke.pgadmissions.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.InvitationEntity;

@Repository
public class InvitationDAO {

    @Inject
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public <T extends InvitationEntity> List<Integer> getInvitationEntities(Class<T> invitationClass) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(invitationClass) //
                .setProjection(Projections.groupProperty("id")) //
                .add(Restrictions.isNotNull("invitation")) //
                .addOrder(Order.asc("invitation.id")) //
                .list();
    }

}
