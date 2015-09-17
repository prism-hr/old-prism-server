package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ApplicationReportListRowDTO {

    private Integer id;

    private String name;

    private String email;

    private String nationality;

    private String residence;

    private String countryOfBirth;

    private LocalDate dateOfBirth;

    private String gender;

    private String institution;

    private String department;

    private String program;

    private String project;

    private String studyOption;

    private String referralSource;

    private String studyLocation;

    private String studyDivision;

    private String studyArea;

    private String studyApplicationId;

    private LocalDate studyStartDate;

    private DateTime createdDate;

    private LocalDate closingDate;

    private DateTime submittedDate;

    private DateTime updatedDate;

    private Integer ratingCount;

    private BigDecimal ratingAverage;

    private PrismStateGroup state;

    private Long referees;

    private Long providedReferences;

    private Long declinedReferences;

    private LocalDate confirmedStartDate;

    private PrismOfferType confirmedOfferType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(String studyOption) {
        this.studyOption = studyOption;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getStudyLocation() {
        return studyLocation;
    }

    public void setStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
    }

    public String getStudyDivision() {
        return studyDivision;
    }

    public void setStudyDivision(String studyDivision) {
        this.studyDivision = studyDivision;
    }

    public String getStudyArea() {
        return studyArea;
    }

    public void setStudyArea(String studyArea) {
        this.studyArea = studyArea;
    }

    public String getStudyApplicationId() {
        return studyApplicationId;
    }

    public void setStudyApplicationId(String studyApplicationId) {
        this.studyApplicationId = studyApplicationId;
    }

    public LocalDate getStudyStartDate() {
        return studyStartDate;
    }

    public void setStudyStartDate(LocalDate studyStartDate) {
        this.studyStartDate = studyStartDate;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public DateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(DateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public DateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(DateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public BigDecimal getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public PrismStateGroup getState() {
        return state;
    }

    public void setState(PrismStateGroup state) {
        this.state = state;
    }

    public Long getReferees() {
        return referees;
    }

    public void setReferees(Long referees) {
        this.referees = referees;
    }

    public Long getProvidedReferences() {
        return providedReferences;
    }

    public void setProvidedReferences(Long providedReferences) {
        this.providedReferences = providedReferences;
    }

    public Long getDeclinedReferences() {
        return declinedReferences;
    }

    public void setDeclinedReferences(Long declinedReferences) {
        this.declinedReferences = declinedReferences;
    }


    public LocalDate getConfirmedStartDate() {
        return confirmedStartDate;
    }

    public void setConfirmedStartDate(LocalDate confirmedStartDate) {
        this.confirmedStartDate = confirmedStartDate;
    }

    public PrismOfferType getConfirmedOfferType() {
        return confirmedOfferType;
    }

    public void setConfirmedOfferType(PrismOfferType confirmedOfferType) {
        this.confirmedOfferType = confirmedOfferType;
    }

    public String getIdDisplay() {
        return id == null ? "" : id.toString();
    }

    public String getNameDisplay() {
        return name == null ? "" : name;
    }

    public String getEmailDisplay() {
        return email == null ? "" : email;
    }

    public String getNationalityDisplay() {
        return nationality == null ? "" : nationality;
    }

    public String getResidenceDisplay() {
        return residence == null ? "" : residence;
    }

    public String getCountryOfBirthDisplay() {
        return countryOfBirth == null ? "" : countryOfBirth;
    }

    public String getDateOfBirthDisplay(String dateFormat) {
        return dateOfBirth == null ? "" : dateOfBirth.toString(dateFormat);
    }

    public String getGenderDisplay() {
        return gender == null ? "" : gender;
    }

    public String getInstitutionDisplay() {
        return institution == null ? "" : institution;
    }

    public String getDepartmentDisplay() {
        return department == null ? "" : department;
    }

    public String getProgramDisplay() {
        return program == null ? "" : program;
    }

    public String getProjectDisplay() {
        return project == null ? "" : project;
    }

    public String getReferralSourceDisplay() {
        return referralSource == null ? "" : referralSource;
    }

    public String getStudyLocationDisplay() {
        return studyLocation == null ? "" : studyLocation;
    }

    public String getStudyDivisionDisplay() {
        return studyDivision == null ? "" : studyDivision;
    }

    public String getStudyAreaDisplay() {
        return studyArea == null ? "" : studyArea;
    }

    public String getStudyApplicationIdDisplay() {
        return studyApplicationId == null ? "" : studyApplicationId;
    }

    public String getStudyStartDateDisplay(String dateFormat) {
        return studyStartDate == null ? "" : studyStartDate.toString(dateFormat);
    }

    public String getCreatedDateDisplay(String dateFormat) {
        return createdDate == null ? "" : createdDate.toString(dateFormat);
    }

    public String getClosingDateDisplay(String dateFormat) {
        return closingDate == null ? "" : closingDate.toString(dateFormat);
    }

    public String getSubmittedDateDisplay(String dateFormat) {
        return submittedDate == null ? "" : submittedDate.toString(dateFormat);
    }

    public String getUpdatedDateDisplay(String dateFormat) {
        return updatedDate == null ? "" : updatedDate.toString(dateFormat);
    }

    public String getAcademicYearDisplay() {
        DateTime baseline = submittedDate == null ? createdDate : submittedDate;
        if (baseline == null) {
            return "";
        }
        Integer baselineMonth = baseline.getMonthOfYear();
        return baselineMonth < 10 ? Integer.toString(baseline.getYear()) : Integer.toString(baseline.plusYears(1).getYear());
    }

    public String getRatingCountDisplay() {
        return ratingCount == null ? "" : ratingCount.toString();
    }

    public String getRatingAverageDisplay() {
        return ratingAverage == null ? "" : ratingAverage.toPlainString();
    }

    public String getProvidedReferencesDisplay() {
        return providedReferences == null ? "" : providedReferences.toString();
    }

    public String getDeclinedReferencesDisplay() {
        return declinedReferences == null ? "" : declinedReferences.toString();
    }

    public PrismStudyOption getStudyOptionDisplay() {
        return studyOption == null ? null : PrismStudyOption.valueOf(studyOption);
    }

    public PrismOfferType getConfirmedOfferTypeDisplay() {
        return confirmedOfferType == null ? null : confirmedOfferType;
    }

    public String getConfirmedStartDateDisplay(String dateFormat) {
        return confirmedStartDate == null ? "" : confirmedStartDate.toString(dateFormat);
    }

    public PrismStateGroup getStateDisplay() {
        return state == null ? null : state;
    }

    public String getRefereesDisplay() {
        return referees == null ? "" : referees.toString();
    }

}
