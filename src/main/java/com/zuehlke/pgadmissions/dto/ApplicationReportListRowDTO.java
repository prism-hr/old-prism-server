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

    private String primaryTheme;

    private String secondaryTheme;

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

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = email;
    }

    public final String getNationality() {
        return nationality;
    }

    public final void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public final String getResidence() {
        return residence;
    }

    public final void setResidence(String residence) {
        this.residence = residence;
    }

    public final String getCountryOfBirth() {
        return countryOfBirth;
    }

    public final void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public final LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public final void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public final String getGender() {
        return gender;
    }

    public final void setGender(String gender) {
        this.gender = gender;
    }

    public final String getInstitution() {
        return institution;
    }

    public final void setInstitution(String institution) {
        this.institution = institution;
    }

    public final String getDepartment() {
        return department;
    }

    public final void setDepartment(String department) {
        this.department = department;
    }

    public final String getProgram() {
        return program;
    }

    public final void setProgram(String program) {
        this.program = program;
    }

    public final String getProject() {
        return project;
    }

    public final void setProject(String project) {
        this.project = project;
    }

    public final String getStudyOption() {
        return studyOption;
    }

    public final void setStudyOption(String studyOption) {
        this.studyOption = studyOption;
    }

    public final String getReferralSource() {
        return referralSource;
    }

    public final void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public final String getPrimaryTheme() {
        return primaryTheme;
    }

    public final void setPrimaryTheme(String primaryTheme) {
        this.primaryTheme = primaryTheme;
    }

    public final String getSecondaryTheme() {
        return secondaryTheme;
    }

    public final void setSecondaryTheme(String secondaryTheme) {
        this.secondaryTheme = secondaryTheme;
    }

    public final String getStudyLocation() {
        return studyLocation;
    }

    public final void setStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
    }

    public final String getStudyDivision() {
        return studyDivision;
    }

    public final void setStudyDivision(String studyDivision) {
        this.studyDivision = studyDivision;
    }

    public final String getStudyArea() {
        return studyArea;
    }

    public final void setStudyArea(String studyArea) {
        this.studyArea = studyArea;
    }

    public final String getStudyApplicationId() {
        return studyApplicationId;
    }

    public final void setStudyApplicationId(String studyApplicationId) {
        this.studyApplicationId = studyApplicationId;
    }

    public final LocalDate getStudyStartDate() {
        return studyStartDate;
    }

    public final void setStudyStartDate(LocalDate studyStartDate) {
        this.studyStartDate = studyStartDate;
    }

    public final DateTime getCreatedDate() {
        return createdDate;
    }

    public final void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public final LocalDate getClosingDate() {
        return closingDate;
    }

    public final void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public final DateTime getSubmittedDate() {
        return submittedDate;
    }

    public final void setSubmittedDate(DateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public final DateTime getUpdatedDate() {
        return updatedDate;
    }

    public final void setUpdatedDate(DateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public final Integer getRatingCount() {
        return ratingCount;
    }

    public final void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public final BigDecimal getRatingAverage() {
        return ratingAverage;
    }

    public final void setRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public final PrismStateGroup getState() {
        return state;
    }

    public final void setState(PrismStateGroup state) {
        this.state = state;
    }

    public final Long getReferees() {
        return referees;
    }

    public final void setReferees(Long referees) {
        this.referees = referees;
    }

    public final Long getProvidedReferences() {
        return providedReferences;
    }

    public final void setProvidedReferences(Long providedReferences) {
        this.providedReferences = providedReferences;
    }

    public final Long getDeclinedReferences() {
        return declinedReferences;
    }

    public final void setDeclinedReferences(Long declinedReferences) {
        this.declinedReferences = declinedReferences;
    }


    public final LocalDate getConfirmedStartDate() {
        return confirmedStartDate;
    }

    public final void setConfirmedStartDate(LocalDate confirmedStartDate) {
        this.confirmedStartDate = confirmedStartDate;
    }

    public final PrismOfferType getConfirmedOfferType() {
        return confirmedOfferType;
    }

    public final void setConfirmedOfferType(PrismOfferType confirmedOfferType) {
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

    public String getSecondaryThemeDisplay() {
        return secondaryTheme == null ? "" : secondaryTheme.replace("|", ", ");
    }

    public String getPrimaryThemeDisplay() {
        return primaryTheme == null ? "" : primaryTheme.replace("|", ", ");
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
