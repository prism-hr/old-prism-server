package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

@Repository
public class DocumentDAO {

	private final SessionFactory sessionFactory;

	public DocumentDAO() {
		this(null);
	}

	@Autowired
	public DocumentDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(Document document) {
		sessionFactory.getCurrentSession().saveOrUpdate(document);
	}

	public Document getDocumentbyId(Integer id) {
		return (Document) sessionFactory.getCurrentSession().get(Document.class, id);
	}

	public void deleteDocument(Document document) {
		if (DocumentType.PROOF_OF_AWARD == document.getType()) {
			removeFromQualification(document);
		}
		if (DocumentType.SUPPORTING_FUNDING == document.getType()) {
			removeFromFunding(document);
		}
		if (DocumentType.LANGUAGE_QUALIFICATION == document.getType()) {
			removeFromLanguageQualification(document);
		}
		sessionFactory.getCurrentSession().delete(getDocumentbyId(document.getId()));
	}

	private void removeFromLanguageQualification(Document document) {
		LanguageQualification qualification = (LanguageQualification) sessionFactory.getCurrentSession().createCriteria(LanguageQualification.class).add(Restrictions.eq("languageQualificationDocument", document)).uniqueResult();
		if (qualification != null) {
			qualification.setLanguageQualificationDocument(null);
			sessionFactory.getCurrentSession().save(qualification);
		}
	}
	
	private void removeFromFunding(Document document) {
		Funding funding = (Funding) sessionFactory.getCurrentSession().createCriteria(Funding.class).add(Restrictions.eq("document", document))
				.uniqueResult();
		if (funding != null) {
			funding.setDocument(null);
			sessionFactory.getCurrentSession().save(funding);
		}
	}

	private void removeFromQualification(Document document) {
		Qualification qualification = (Qualification) sessionFactory.getCurrentSession().createCriteria(Qualification.class)
				.add(Restrictions.eq("proofOfAward", document)).uniqueResult();
		if (qualification != null) {
			qualification.setProofOfAward(null);
			sessionFactory.getCurrentSession().save(qualification);
		}
	}

}
