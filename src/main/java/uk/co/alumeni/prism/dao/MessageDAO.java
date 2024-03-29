package uk.co.alumeni.prism.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import uk.co.alumeni.prism.domain.activity.ActivityEditable;
import uk.co.alumeni.prism.domain.message.*;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.dto.MessageThreadDTO;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hibernate.sql.JoinType.INNER_JOIN;
import static org.hibernate.transform.Transformers.aliasToBean;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getMatchingUserConstraint;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getVisibleMessageConstraint;

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

    public List<MessageThreadDTO> getMessageThreads(ActivityEditable activity, User user) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Message.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("thread").as("thread")) //
                        .add(Projections.max("createdTimestamp").as("updatedTimestamp"))) //
                .createAlias("thread", "thread", INNER_JOIN) //
                .createAlias("thread.participants", "participant", INNER_JOIN);

        if (Resource.class.isAssignableFrom(activity.getClass())) {
            Resource resource = (Resource) activity;
            criteria.createAlias("thread.comment", "comment", INNER_JOIN, //
                    Restrictions.eq("comment." + resource.getResourceScope().getLowerCamelName(), resource));
        } else if (!((UserAccount) activity).getUser().equals(user)) {
            criteria.add(Restrictions.eq("thread.userAccount", activity));
        }

        return (List<MessageThreadDTO>) criteria //
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
                .createAlias("thread.searchUser", "searchUser", JoinType.INNER_JOIN) //
                .createAlias("thread.searchAdvert", "searchAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("searchAdvert.user", "searchAdvertUser", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.in("thread", threads));

        if (isNotBlank(searchTerm)) {
            criteria.add(getMatchingMessageConstraint(searchTerm)
                    .add(Restrictions.like("thread.subject", searchTerm, MatchMode.ANYWHERE)) //
                    .add(getMatchingUserConstraint("searchUser", searchTerm)) //
                    .add(Restrictions.like("searchAdvert.name", searchTerm, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("searchAdvert.summary", searchTerm, MatchMode.ANYWHERE)) //
                    .add(Restrictions.like("searchAdvert.description", searchTerm, MatchMode.ANYWHERE)) //
                    .add(getMatchingUserConstraint("searchAdvertUser", searchTerm)) //
                    .add(Restrictions.like("thread.searchResourceCode", searchTerm)));
        }

        return (List<MessageThreadDTO>) criteria //
                .addOrder(Order.desc("updatedTimestamp")) //
                .addOrder(Order.desc("thread")) //
                .setResultTransformer(aliasToBean(MessageThreadDTO.class)) //
                .list();
    }

    public List<Message> getMessages(Collection<MessageThread> threads, User user) {
        return (List<Message>) sessionFactory.getCurrentSession().createCriteria(Message.class) //
                .createAlias("thread", "thread", INNER_JOIN) //
                .createAlias("thread.participants", "participant", INNER_JOIN) //
                .add(Restrictions.in("thread", threads)) //
                .add(Restrictions.eq("participant.user", user)) //
                .add(getVisibleMessageConstraint()) //
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

    public MessageThreadParticipant getMessageThreadParticipant(MessageThread thread, User user) {
        return (MessageThreadParticipant) sessionFactory.getCurrentSession().createCriteria(MessageThreadParticipant.class) //
                .add(Restrictions.eq("thread", thread)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.isNull("closeMessage")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public void closeMessageThreadParticipants(MessageThread thread, Message message, Collection<Integer> userIds) {
        sessionFactory.getCurrentSession().createQuery(
                "update MessageThreadParticipant "
                        + "set closeMessage = :message "
                        + "where thread = :thread "
                        + "and user.id not in (:userIds) "
                        + "and closeMessage is null")
                .setParameter("message", message)
                .setParameter("thread", thread)
                .setParameterList("userIds", userIds)
                .executeUpdate();
    }

    public Message getLastViewedMessage(MessageThread thread, User user) {
        return (Message) sessionFactory.getCurrentSession().createCriteria(MessageThreadParticipant.class) //
                .setProjection(Projections.property("lastViewedMessage")) //
                .add(Restrictions.eq("thread", thread)) //
                .add(Restrictions.eq("user", user)) //
                .addOrder(Order.desc("lastViewedMessage")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<Integer> getMessageThreadsForResource(Resource resource) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(MessageThread.class) //
                .setProjection(Projections.groupProperty("id")) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .list();
    }

    public void setMessageThreadSearchUser(List<Integer> messageThreads, User user) {
        sessionFactory.getCurrentSession().createQuery(
                "update MessageThread "
                        + "set searchUser = :user "
                        + "where id in (:messageThreads)")
                .setParameter("user", user)
                .setParameterList("messageThreads", messageThreads) //
                .executeUpdate();
    }

    private Junction getMatchingMessageConstraint(String searchTerm) {
        return Restrictions.disjunction() //
                .add(getMatchingUserConstraint("user", searchTerm)) //
                .add(Restrictions.like("content", searchTerm, MatchMode.ANYWHERE));
    }

}
