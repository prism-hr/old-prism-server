package com.zuehlke.pgadmissions.pagemodels;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.dto.Address;

import com.zuehlke.pgadmissions.dto.Funding;

import com.zuehlke.pgadmissions.errors.FundingErrors;

public class ApplicationPageModel extends PageModel {

	private Address address = new Address();
	private Funding funding = new Funding();
	private Qualification qualification = new Qualification();
	private EmploymentPosition employmentPosition =new EmploymentPosition();
	private Referee referee= new Referee();
	private String message;
	private ApplicationForm applicationForm;

	private List<Country> countries;
	private List<Language> languages;
	
	private List<Gender> genders = new LinkedList<Gender>();
	private List<PhoneType> phoneTypes = new LinkedList<PhoneType>();
	private List<DocumentType> documentTypes = new LinkedList<DocumentType>();
	private List<QualificationLevel> qualificationLevels = new LinkedList<QualificationLevel>();

	private List<StudyOption> studyOptions = new LinkedList<StudyOption>();
	private List<Referrer> referrers = new LinkedList<Referrer>();
	
	private String uploadErrorCode;
	private String uploadTwoErrorCode;
	private List<FundingType> fundingTypes = new LinkedList<FundingType>();
	private List<AddressPurpose> addressPurposes = new LinkedList<AddressPurpose>();
	
	private FundingErrors fundingErrors;
	private RegisteredUser refereeUser;

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

	public Qualification getQualification() {
		return qualification;
	}

	public void setQualification(Qualification qualification) {
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

	public List<QualificationLevel> getQualificationLevels() {
		return qualificationLevels;
	}
	
	public void setQualificationLevels(QualificationLevel[] qualificationLevels) {
		this.qualificationLevels.addAll(Arrays.asList(qualificationLevels));
	}

	public void setFundingTypes(FundingType[] fundingTypes) {
		this.fundingTypes.addAll(Arrays.asList(fundingTypes));
	}
	public List<FundingType> getFundingTypes() {
		return fundingTypes;
	}

	public void setAddressPurposes(AddressPurpose[] addressPurposes) {
		this.addressPurposes.addAll(Arrays.asList(addressPurposes));
	}
	
	public List<AddressPurpose> getAddressPurposes() {
		return addressPurposes;
	}
	
	public String getUploadTwoErrorCode() {
		return uploadTwoErrorCode;
	}
	
	public void setUploadTwoErrorCode(String uploadTwoErrorCode) {
		this.uploadTwoErrorCode = uploadTwoErrorCode;
	}
	
	public FundingErrors getFundingErrors() {
		return fundingErrors;
	}
	
	public void setFundingErrors(FundingErrors fundingErrors) {
		this.fundingErrors = fundingErrors;
	}

	public void setRefereeUser(RegisteredUser refereeUser) {
		this.refereeUser = refereeUser;
		
	}
	
	public RegisteredUser getRefereeUser() {
		return refereeUser;
	}
}
