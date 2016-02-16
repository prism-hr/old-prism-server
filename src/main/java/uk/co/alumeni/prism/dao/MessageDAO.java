package uk.co.alumeni.prism.dao;

import java.util.Collection;
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
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;

@Repository
@SuppressWarnings("unchecked")
public class MessageDAO {

    @Inject
    private SessionFactory sessionFactory;

    public List<MessageRecipient> getMessagesPendingAllocation() {
        return (List<MessageRecipient>) sessionFactory.getCurrentSession().createCriteria(MessageRecipient.class) //
                .add(Restrictions.isNotNull("role")) //
                .add(Restrictions.isNull("user")) //
                .list();
    }
    
    public List<MessageRecipient> getMessagesPendingNotification() {
        return (List<MessageRecipient>) sessionFactory.getCurrentSession().createCriteria(MessageRecipient.class) //
                .add(Restrictions.isNotNull("user")) //
                .add(Restrictions.isNull("sendTimestamp")) //
                .list();
    }

    public List<Message> getMessagesByThreads(Collection<MessageThread> threads) {
        return (List<Message>) sessionFactory.getCurrentSession().createCriteria(MessageRecipient.class) //
                .setProjection(Projections.groupProperty("message")) //
                .createAlias("message", "message", JoinType.INNER_JOIN) //
                .add(Restrictions.in("message.thread", threads)) //
                .add(Restrictions.isNotNull("sendTimestamp")) //
                .addOrder(Order.desc("message.thread.id")) //
                .addOrder(Order.desc("message.id")) //
                .list();
    }

    public List<MessageThread> getMessageThreadsByResourceAndUser(Resource resource, User user) {
        return (List<MessageThread>) sessionFactory.getCurrentSession().createCriteria(Message.class) //
                .setProjection(Projections.groupProperty("thread")) //
                .createAlias("thread", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.comment", "comment", JoinType.INNER_JOIN) //
                .createAlias("recipients", "recipient", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("recipient.user", user)) //
                .add(Restrictions.isNotNull("recipient.sendTimestamp")) //
                .addOrder(Order.desc("thread.id")) //
                .list();
    }

}
