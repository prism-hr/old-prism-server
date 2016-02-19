package uk.co.alumeni.prism.dao;

import static org.joda.time.DateTime.now;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageRecipient;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;

@Repository
@SuppressWarnings("unchecked")
public class MessageDAO {

    @Inject
    private SessionFactory sessionFactory;

    public List<Integer> getMessageRecipientsPendingAllocation() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(MessageRecipient.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.isNotNull("role")) //
                .add(Restrictions.isNull("user")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<Integer> getMessageRecipientsPendingNotification() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(MessageRecipient.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.isNotNull("user")) //
                .add(Restrictions.isNull("sendTimestamp")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<Message> getMessagesByResourceAndUser(Resource resource, User user) {
        return (List<Message>) sessionFactory.getCurrentSession().createCriteria(Message.class) //
                .createAlias("thread", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.comment", "comment", JoinType.INNER_JOIN) //
                .createAlias("recipients", "recipient", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("recipient.user", user)) //
                .add(Restrictions.isNotNull("recipient.sendTimestamp")) //
                .addOrder(Order.desc("thread.id")) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    public void setMessageThreadViewed(Integer thread, Integer message) {
        sessionFactory.getCurrentSession().createQuery( //
                "update MessageRecipient " //
                        + "set viewTimestamp = :baseline "
                        + "where message in ("
                            + "from Message " //
                            + "where thread.id = :thread "
                            + "and id <= :message) " //
                        + "and viewTimestamp is null") //
                .setParameter("baseline", now()) //
                .setParameter("thread", thread) //
                .setParameter("message", message) //
                .executeUpdate();
    }

}
