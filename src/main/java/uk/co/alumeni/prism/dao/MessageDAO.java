package uk.co.alumeni.prism.dao;

import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageDocument;
import uk.co.alumeni.prism.domain.message.MessageRecipient;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.MessageThreadDTO;

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

    public List<MessageThreadDTO> getMessageThreads(Resource resource, User user) {
        return (List<MessageThreadDTO>) sessionFactory.getCurrentSession().createCriteria(MessageRecipient.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("message.thread").as("thread")) //
                        .add(Projections.max("message.createdTimestamp").as("updatedTimestamp"))) //
                .createAlias("message", "message", JoinType.INNER_JOIN) //
                .createAlias("message.thread", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.isNotNull("sendTimestamp")) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("thread")) //
                .setResultTransformer(Transformers.aliasToBean(MessageThreadDTO.class)) //
                .list();
    }

    public List<Message> getMessages(MessageThread thread, User user) {
        return getMessages(newArrayList(thread), user);
    }

    public List<Message> getMessages(Collection<MessageThread> threads, User user) {
        return (List<Message>) sessionFactory.getCurrentSession().createCriteria(MessageRecipient.class) //
                .setProjection(Projections.property("message")) //
                .createAlias("message", "message", JoinType.INNER_JOIN) //
                .createAlias("message.thread", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.in("message.thread", threads)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.isNotNull("sendTimestamp")) //
                .addOrder(Order.desc("message")) //
                .list();
    }

    public List<MessageRecipient> getMessageRecipients(Collection<Message> messages) {
        return (List<MessageRecipient>) sessionFactory.getCurrentSession().createCriteria(MessageRecipient.class) //
                .createAlias("message", "message", JoinType.INNER_JOIN) //
                .createAlias("message.thread", "thread", JoinType.INNER_JOIN) //
                .createAlias("thread.comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.in("message", messages)) //
                .add(Restrictions.neProperty("user", "message.user")) //
                .addOrder(Order.desc("message")) //
                .addOrder(Order.asc("user.fullName")) //
                .list();
    }

    public List<MessageDocument> getMessageDocuments(Collection<Message> messages) {
        return (List<MessageDocument>) sessionFactory.getCurrentSession().createCriteria(MessageDocument.class) //
                .createAlias("document", "document", JoinType.INNER_JOIN) //
                .add(Restrictions.in("message", messages)) //
                .addOrder(Order.desc("message")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

}
