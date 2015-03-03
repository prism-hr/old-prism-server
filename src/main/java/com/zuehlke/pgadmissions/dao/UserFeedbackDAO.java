package com.zuehlke.pgadmissions.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.utils.PrismConstants;

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
		        .setMaxResults(PrismConstants.LIST_PAGE_ROW_COUNT) //
		        .list();
	}

	public DateTime getLatestUserFeedbackTimestamp(User user) {
		return (DateTime) sessionFactory.getCurrentSession().createCriteria(UserFeedback.class) //
		        .setProjection(Projections.max("createdTimestamp")) //
		        .add(Restrictions.eq("user", user)) //
		        .uniqueResult();
	}

}
