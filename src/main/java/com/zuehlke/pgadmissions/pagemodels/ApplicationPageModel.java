package com.zuehlke.pgadmissions.pagemodels;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.EmploymentPosition;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.QualificationDTO;

public class ApplicationPageModel extends PageModel {

	private Address address;
	private Funding funding;
	private String message;
	private ApplicationForm applicationForm;
	private QualificationDTO qualification;
	private List<Country> countries;
	private List<Language> languages;
	private EmploymentPosition employmentPosition;
	private List<ResidenceStatus> residenceStatuses = new LinkedList<ResidenceStatus>();
	private List<Gender> genders = new LinkedList<Gender>();
	private List<PhoneType> phoneTypes = new LinkedList<PhoneType>();
	private List<LanguageAptitude> languageAptitudes = new LinkedList<LanguageAptitude>();
	private List<DocumentType> documentTypes = new LinkedList<DocumentType>();

	private List<StudyOption> studyOptions = new LinkedList<StudyOption>();
	private List<Referrer> referrers = new LinkedList<Referrer>();
	private Referee referee;
	private String uploadErrorCode;

	public ApplicationForm getApplicationForm() {
		return applicationForm;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Funding getFunding() {
		return funding;
	}

	public void setFunding(Funding funding) {
		this.funding = funding;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;

	}

	public QualificationDTO getQualification() {
		return qualification;
	}

	public void setQualification(QualificationDTO qualification) {
		this.qualification = qualification;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}

	public List<Country> getCountries() {
		return countries;
	}

	public void setEmploymentPosition(EmploymentPosition employmentPosition) {
		this.employmentPosition = employmentPosition;

	}

	public EmploymentPosition getEmploymentPosition() {
		return employmentPosition;
	}

	public List<ResidenceStatus> getResidenceStatuses() {
		return residenceStatuses;
	}

	public void setResidenceStatuses(ResidenceStatus[] values) {
		this.residenceStatuses.addAll(Arrays.asList(values));
	}

	public List<StudyOption> getStudyOptions() {
		return studyOptions;
	}

	public void setStudyOptions(StudyOption[] values) {
		this.studyOptions.addAll(Arrays.asList(values));
	}

	public List<Referrer> getReferrers() {
		return referrers;
	}

	public void setReferrers(Referrer[] values) {
		this.referrers.addAll(Arrays.asList(values));
	}

	public List<Gender> getGenders() {
		return genders;
	}

	public void setGenders(Gender[] genders) {
		this.genders.addAll(Arrays.asList(genders));
	}

	public List<PhoneType> getPhoneTypes() {
		return phoneTypes;
	}

	public void setPhoneTypes(PhoneType[] phoneTypes) {
		this.phoneTypes.addAll(Arrays.asList(phoneTypes));
	}

	public void setReferee(Referee referee) {
		this.referee = referee;
	}

	public Referee getReferee() {
		return referee;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	public List<LanguageAptitude> getLanguageAptitudes() {
		return languageAptitudes;
	}

	public void setLanguageAptitudes(LanguageAptitude[] languageAptitudes) {
		this.languageAptitudes.addAll(Arrays.asList(languageAptitudes));
	}

	public List<DocumentType> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(DocumentType[] documentTypes) {
		this.documentTypes.addAll(Arrays.asList(documentTypes));
	}

	public String getUploadErrorCode() {
		return uploadErrorCode;
	}

	public void setUploadErrorCode(String uploadErrorCode) {
		this.uploadErrorCode = uploadErrorCode;
	}
}
