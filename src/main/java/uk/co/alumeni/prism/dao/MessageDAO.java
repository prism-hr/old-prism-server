package uk.co.alumeni.prism.dao;

import static java.util.stream.Collectors.toList;
import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hibernate.sql.JoinType.INNER_JOIN;
import static org.hibernate.transform.Transformers.aliasToBean;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getSimilarUserConstraint;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.domain.message.Message;
import uk.co.alumeni.prism.domain.message.MessageDocument;
import uk.co.alumeni.prism.domain.message.MessageNotification;
import uk.co.alumeni.prism.domain.message.MessageThread;
import uk.co.alumeni.prism.domain.message.MessageThreadParticipant;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.MessageThreadDTO;

@Repository
@SuppressWarnings("unchecked")
public class MessageDAO {

    @Inject
    private SessionFactory sessionFactory;

    public List<Integer> getMessageNotificationsPending() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(MessageNotification.class) //
                .setProjection(Projections.property("id")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<MessageThreadDTO> getMessageThreads(Resource resource, User user) {
        return (List<MessageThreadDTO>) sessionFactory.getCurrentSession().createCriteria(Message.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("thread").as("thread")) //
                        .add(Projections.max("createdTimestamp").as("updatedTimestamp"))) //
                .createAlias("thread", "thread", INNER_JOIN) //
                .createAlias("thread.comment", "comment", INNER_JOIN) //
                .createAlias("thread.participants", "participant", INNER_JOIN) //
                .add(Restrictions.eq("comment." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("participant.user", user)) //
                .addOrder(Order.desc("updatedTimestamp")) //
                .addOrder(Order.desc("thread")) //
                .setResultTransformer(aliasToBean(MessageThreadDTO.class)) //
                .list();
    }

    public List<MessageThreadDTO> getMatchingMessageThreads(Collection<MessageThread> threads, String searchTerm) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Message.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("thread").as("thread")) //
                        .add(Projections.max("createdTimestamp").as("updatedTimestamp"))) //
                .createAlias("thread", "thread", INNER_JOIN) //
                .add(Restrictions.in("thread", threads));

        if (isNotBlank(searchTerm)) {
            criteria.add(getMatchingMessageConstraint(searchTerm)
                    .add(Restrictions.like("thread.subject", searchTerm)));
        }

        return (List<MessageThreadDTO>) criteria //
                .addOrder(Order.desc("updatedTimestamp")) //
                .addOrder(Order.desc("thread")) //
                .setResultTransformer(aliasToBean(MessageThreadDTO.class)) //
                .list();
    }

    public List<Message> getMessages(MessageThread thread, User user) {
        return getMessages(newArrayList(thread), user);
    }

    public List<Message> getMessages(Collection<MessageThread> threads, User user) {
        return (List<Message>) sessionFactory.getCurrentSession().createCriteria(Message.class) //
                .createAlias("thread", "thread", INNER_JOIN) //
                .createAlias("thread.participants", "participant", INNER_JOIN) //
                .add(Restrictions.in("thread", threads)) //
                .add(Restrictions.eq("participant.user", user)) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.geProperty("id", "participant.startMessage.id")) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("participant.closeMessage")) //
                                .add(Restrictions.ltProperty("id", "participant.closeMessage.id")))) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    public List<Message> getMatchingMessages(Collection<Message> messages, String searchTerm) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Message.class) //
                .add(Restrictions.in("id", messages.stream().map(m -> m.getId()).collect(toList())));

        if (isNotBlank(searchTerm)) {
            criteria.add(getMatchingMessageConstraint(searchTerm));
        }

        return (List<Message>) criteria //
                .addOrder(Order.desc("message")) //
                .list();
    }

    public List<MessageThreadParticipant> getMessageThreadParticipants(Collection<MessageThread> threads) {
        return (List<MessageThreadParticipant>) sessionFactory.getCurrentSession().createCriteria(MessageThreadParticipant.class) //
                .createAlias("user", "user", INNER_JOIN) //
                .add(Restrictions.in("thread", threads)) //
                .add(Restrictions.isNull("closeMessage")) //
                .addOrder(Order.desc("thread")) //
                .addOrder(Order.asc("user.fullName")) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    public List<MessageDocument> getMessageDocuments(Collection<Message> messages) {
        return (List<MessageDocument>) sessionFactory.getCurrentSession().createCriteria(MessageDocument.class) //
                .createAlias("document", "document", INNER_JOIN) //
                .add(Restrictions.in("message", messages)) //
                .addOrder(Order.desc("message")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public MessageThreadParticipant getMessageThreadParticipant(User user, Integer message) {
        return (MessageThreadParticipant) sessionFactory.getCurrentSession().createCriteria(MessageThreadParticipant.class) //
                .createAlias("thread", "thread", INNER_JOIN) //
                .createAlias("thread.messages", "message", INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.isNull("closeMessage")) //
                .add(Restrictions.eq("message.id", message)) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public void closeMessageThreadParticipants(MessageThread thread, Message message, List<Integer> userIds) {
        sessionFactory.getCurrentSession().createQuery(
                "update MessageThreadParticipant "
                        + "set closeMessage = :message "
                        + "where thread = :thread "
                        + "and user.id not in (:userIds)")
                .setParameter("message", message)
                .setParameter("thread", thread)
                .setParameterList("userIds", userIds)
                .executeUpdate();
    }

    private Junction getMatchingMessageConstraint(String searchTerm) {
        return Restrictions.disjunction() //
                .add(getSimilarUserConstraint("user", searchTerm)) //
                .add(Restrictions.like("content", searchTerm));
    }

}
