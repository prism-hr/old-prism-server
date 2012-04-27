package com.zuehlke.pgadmissions.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class ApplicationFormDAO {

	private final SessionFactory sessionFactory;

	ApplicationFormDAO() {
		this(null);
	}

	@Autowired
	public ApplicationFormDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(ApplicationForm application) {
		sessionFactory.getCurrentSession().saveOrUpdate(application);
	}

	public ApplicationForm get(Integer id) {
		return (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
	}

	public List<ApplicationForm> getApplicationsByApplicant(RegisteredUser applicant) {
		@SuppressWarnings("unchecked")
		List<ApplicationForm> list = sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("applicant", applicant))
				.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getAllApplications() {
		return (List<ApplicationForm>) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).list();

	}

	@SuppressWarnings("unchecked")
	public List<Qualification> getQualificationsByApplication(ApplicationForm application) {
		return sessionFactory.getCurrentSession().createCriteria(Qualification.class).add(Restrictions.eq("application", application)).list();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getApplicationsDueValidationReminder() {
		Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
		Date oneWeekAgo = DateUtils.addDays(today, -6);
		return (List<ApplicationForm>) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
				.add(Restrictions.eq("status", ApplicationFormStatus.VALIDATION)).add(Restrictions.lt("validationDueDate", today))
				.add(Restrictions.or(Restrictions.isNull("lastEmailReminderDate"), Restrictions.lt("lastEmailReminderDate", oneWeekAgo))).list();
	}

}
