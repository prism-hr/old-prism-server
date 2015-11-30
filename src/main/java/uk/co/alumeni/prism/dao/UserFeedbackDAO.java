package uk.co.alumeni.prism.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import uk.co.alumeni.prism.PrismConstants;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserFeedback;

@Repository
@SuppressWarnings("unchecked")
public class UserFeedbackDAO {

	@Inject
	private SessionFactory sessionFactory;

	public List<UserFeedback> getUserFeedback(Integer ratingThreshold, String lastSequenceIdentifier) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserFeedback.class) //
		        .add(Restrictions.eq("declinedResponse", true)) //
		        .add(Restrictions.ge("rating", ratingThreshold));

		if (lastSequenceIdentifier != null) {
			criteria.add(Restrictions.le("sequenceIdentifier", lastSequenceIdentifier));
		}

		return (List<UserFeedback>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
		        .setMaxResults(PrismConstants.RESOURCE_LIST_PAGE_ROW_COUNT) //
		        .list();
	}

	public DateTime getLatestUserFeedbackTimestamp(User user) {
		return (DateTime) sessionFactory.getCurrentSession().createCriteria(UserFeedback.class) //
		        .setProjection(Projections.max("createdTimestamp")) //
		        .add(Restrictions.eq("user", user)) //
		        .uniqueResult();
	}

}
