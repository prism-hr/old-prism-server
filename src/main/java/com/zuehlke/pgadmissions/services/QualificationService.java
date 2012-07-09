package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.Qualification;

@Service
public class QualificationService {

	private final QualificationDAO qualificationDAO;

	QualificationService() {
		this(null);
	}

	@Autowired
	public QualificationService(QualificationDAO qualificationDAO) {
		this.qualificationDAO = qualificationDAO;
	}

	public Qualification getQualificationById(Integer id) {
		return qualificationDAO.getQualificationById(id);
	}

	@Transactional
	public void delete(Qualification qualification) {
		qualificationDAO.delete(qualification);

	}

	@Transactional
	public void save(Qualification qualification) {
		qualificationDAO.save(qualification);
		
	}

}
