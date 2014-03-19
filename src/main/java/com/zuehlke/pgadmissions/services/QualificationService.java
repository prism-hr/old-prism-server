package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;

@Service
@Transactional
public class QualificationService {

    private final QualificationDAO qualificationDAO;

    public QualificationService() {
        this(null);
    }

    @Autowired
    public QualificationService(final QualificationDAO qualificationDAO) {
        this.qualificationDAO = qualificationDAO;
    }

    public Qualification getQualificationById(Integer id) {
        return qualificationDAO.getQualificationById(id);
    }

    public void delete(Qualification qualification) {
        qualificationDAO.delete(qualification);
    }

    public void save(ApplicationForm application, Integer qualificationId, Qualification qualification) {
        if (qualificationId != null) {
            Qualification existingQualification = qualificationDAO.getQualificationById(qualificationId);
            existingQualification.setProofOfAward(qualification.getProofOfAward());
            existingQualification.setQualificationAwardDate(qualification.getQualificationAwardDate());
            existingQualification.setQualificationSubject(qualification.getQualificationSubject());
            existingQualification.setQualificationTitle(qualification.getQualificationTitle());
            existingQualification.setInstitutionCountry(qualification.getInstitutionCountry());
            existingQualification.setQualificationInstitution(qualification.getQualificationInstitution());
            existingQualification.setOtherQualificationInstitution(qualification.getOtherQualificationInstitution());
            existingQualification.setQualificationInstitutionCode(qualification.getQualificationInstitutionCode());
            existingQualification.setQualificationLanguage(qualification.getQualificationLanguage());
            existingQualification.setQualificationType(qualification.getQualificationType());
            existingQualification.setQualificationGrade(qualification.getQualificationGrade());
            existingQualification.setQualificationStartDate(qualification.getQualificationStartDate());
            existingQualification.setCompleted(qualification.getCompleted());
        } else {
            qualification.setApplication(application);
            application.getQualifications().add(qualification);
            qualificationDAO.save(qualification);
        }
    }

    public void selectForSendingToPortico(final ApplicationForm applicationForm, final List<Integer> qualificationsSendToPortico) {

        for (Qualification qualification : applicationForm.getQualifications()) {
            qualification = qualificationDAO.getQualificationById(qualification.getId());
            qualification.setSendToUCL(false);
        }

        for (Integer qualificationId : qualificationsSendToPortico) {
            Qualification qualification = qualificationDAO.getQualificationById(qualificationId);
            qualification.setSendToUCL(true);
        }
    }
}
