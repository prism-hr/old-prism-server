package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;

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
		List<ApplicationForm> list = sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
				.add(Restrictions.eq("applicant", applicant)).list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationForm> getAllApplications() {
		return (List<ApplicationForm>) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).list();

	}

	public Qualification getQualification(Integer qualificationId) {
		return (Qualification) sessionFactory.getCurrentSession().get(Qualification.class, qualificationId);
	}

	@SuppressWarnings("unchecked")
	public List<Qualification> getQualificationsByApplication(ApplicationForm application) {
		return sessionFactory.getCurrentSession().createCriteria(Qualification.class)
				.add(Restrictions.eq("application", application)).list();
	}

	public Funding getFundingById(Integer fundingId) {
		return (Funding) sessionFactory.getCurrentSession().get(Funding.class, fundingId);
	}

	public EmploymentPosition getEmploymentById(Integer positionId) {
		return (EmploymentPosition) sessionFactory.getCurrentSession().get(EmploymentPosition.class, positionId);
	}

	public Address getAdddressById(Integer addressId) {
		return (Address) sessionFactory.getCurrentSession().get(Address.class, addressId);
	}

	public Referee getRefereeById(Integer id) {
		return (Referee) sessionFactory.getCurrentSession().get(Referee.class, id);
	}

	public Messenger getMessengerById(Integer id) {
		return (Messenger) sessionFactory.getCurrentSession().get(Messenger.class, id);
	}

	public Telephone getTelephoneById(Integer id) {
		return (Telephone) sessionFactory.getCurrentSession().get(Telephone.class, id);
	}

	public void saveTelephone(Telephone telephone) {
		sessionFactory.getCurrentSession().saveOrUpdate(telephone);

	}

	public void saveMessenger(Messenger messenger) {
		sessionFactory.getCurrentSession().saveOrUpdate(messenger);

	}

	public void saveReferee(Referee referee) {
		sessionFactory.getCurrentSession().saveOrUpdate(referee);
	}

	public void saveDocument(Document document) {
		sessionFactory.getCurrentSession().saveOrUpdate(document);
	}

}
