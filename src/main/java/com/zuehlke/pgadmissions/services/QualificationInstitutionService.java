package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;

@Service
@Transactional
public class QualificationInstitutionService {

	@Autowired
	private QualificationInstitutionDAO qualificationInstitutionDAO;

	public QualificationInstitution getInstitutionByCode(String institutionCode) {
		return qualificationInstitutionDAO.getInstitutionByCode(institutionCode);
	}

	public void createNewCustomInstitution(QualificationInstitution institution) {
		QualificationInstitution lastCustomInstitution = qualificationInstitutionDAO.getLastCustomInstitution();
		Integer codeNumber;
		if (lastCustomInstitution != null) {
			codeNumber = Integer.valueOf(lastCustomInstitution.getCode().substring(4));
			codeNumber++;
		} else {
			codeNumber = 0;
		}
		institution.setCode(String.format("CUST%05d", codeNumber));
		qualificationInstitutionDAO.save(institution);
	}

}