package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.AddressDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.EmploymentPositionDAO;
import com.zuehlke.pgadmissions.dao.FundingDAO;
import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service("applicationsService")
public class ApplicationsService {

	private final ApplicationFormDAO applicationFormDAO;
	private final AddressDAO addressDAO;
	private final QualificationDAO qualificationDAO;
	private final FundingDAO fundingDAO;
	private final EmploymentPositionDAO employmentPositionDAO;
	private final RefereeDAO refereeDAO;

	ApplicationsService() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public ApplicationsService(ApplicationFormDAO applicationFormDAO, AddressDAO addressDAO, QualificationDAO qualificationDAO, FundingDAO fundingDAO,
			EmploymentPositionDAO employmentPositionDAO, RefereeDAO refereeDAO) {
		this.applicationFormDAO = applicationFormDAO;
		this.addressDAO = addressDAO;
		this.qualificationDAO = qualificationDAO;
		this.fundingDAO = fundingDAO;
		this.employmentPositionDAO = employmentPositionDAO;
		this.refereeDAO = refereeDAO;
	}

	public List<ApplicationForm> getVisibleApplications(RegisteredUser user) {
		List<ApplicationForm> visibleApplications = new ArrayList<ApplicationForm>();

		if (user.isInRole(Authority.APPLICANT)) {
			List<ApplicationForm> applications = new ArrayList<ApplicationForm>();
			applications = applicationFormDAO.getApplicationsByApplicant(user);
			if (applications != null) {
				visibleApplications.addAll(applications);
			}
		} else {
			List<ApplicationForm> applications = applicationFormDAO.getAllApplications();
			if (applications != null) {
				for (ApplicationForm application : applications) {
					if (user.canSee(application)) {
						visibleApplications.add(application);
					}
				}
			}
		}

		Collections.sort(visibleApplications);
		return visibleApplications;
	}

	public ApplicationForm getApplicationById(Integer id) {
		return applicationFormDAO.get(id);
	}

	@Transactional
	public void save(ApplicationForm application) {
		applicationFormDAO.save(application);

	}

	public Qualification getQualificationById(Integer id) {
		return applicationFormDAO.getQualification(id);
	}

	@Transactional
	public Referee getRefereeById(Integer id) {
		return applicationFormDAO.getRefereeById(id);
	}

	public List<Qualification> getQualificationsByApplication(ApplicationForm applicationForm) {
		return applicationFormDAO.getQualificationsByApplication(applicationForm);
	}

	public com.zuehlke.pgadmissions.domain.Funding getFundingById(Integer fundingId) {
		return applicationFormDAO.getFundingById(fundingId);
	}

	public EmploymentPosition getEmploymentPositionById(Integer positionId) {
		return applicationFormDAO.getEmploymentById(positionId);
	}

	public com.zuehlke.pgadmissions.domain.Address getAddressById(Integer addressId) {
		return applicationFormDAO.getAdddressById(addressId);
	}


	public Telephone getTelephoneById(Integer id) {
		return applicationFormDAO.getTelephoneById(id);
	}

	@Transactional
	public void saveTelephone(Telephone telephone) {
		applicationFormDAO.saveTelephone(telephone);

	}

	@Transactional
	public void saveReferee(com.zuehlke.pgadmissions.domain.Referee referee) {
		applicationFormDAO.saveReferee(referee);

	}

	@Transactional
	public void deleteAddress(Address address) {
		addressDAO.delete(address);

	}

	@Transactional
	public void deleteQualification(Qualification qual) {
		qualificationDAO.delete(qual);

	}

	@Transactional
	public void deleteFunding(Funding funding) {
		fundingDAO.delete(funding);

	}

	@Transactional
	public void deleteEmployment(EmploymentPosition position) {
		employmentPositionDAO.delete(position);
	}
	
	@Transactional
	public void deleteReferee(Referee referee) {
		refereeDAO.delete(referee);		
	}

	@Transactional
	public void saveDocument(Document document) {
		applicationFormDAO.saveDocument(document);
		
	}
	
	@Transactional
	public ApplicationForm createAndSaveNewApplicationForm(RegisteredUser user, Project project) {

		ApplicationForm applicationForm = newApplicationForm();
		applicationForm.setApplicant(user);
		applicationForm.setProject(project);
		applicationFormDAO.save(applicationForm);
		return applicationForm;
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

}
