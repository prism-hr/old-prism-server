package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.RESUME_COMPLETE_CONCEALED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.RESUME_COMPLETE_PUBLISHED;

import java.util.Arrays;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.domain.user.User;

@Repository
public class ResumeDAO {

    @Inject
    private SessionFactory sessionFactory;

    public Resume getPreviousCompletedResume(User user, String opportunityCategories) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Resume.class) //
                .add(Restrictions.eq("user", user));

        if (opportunityCategories != null) {
            criteria.add(Restrictions.eq("opportunityCategories", opportunityCategories));
        }

        return (Resume) criteria.add(Restrictions.isNotNull("submittedTimestamp")) //
                .add(Restrictions.in("state.id",
                        Arrays.asList(RESUME_COMPLETE_PUBLISHED, RESUME_COMPLETE_CONCEALED))) //
                .addOrder(Order.desc("submittedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

}
