package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismDomicile;
import com.zuehlke.pgadmissions.domain.definitions.PrismGender;
import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ApplicationReportListRowDTO {

    private Integer id;

    private String name;

    private String email;

    private PrismGender gender;

    private LocalDate dateOfBirth;

    private PrismDomicile nationality;

    private PrismDomicile domicile;

    private String institution;

    private String department;

    private String program;

    private String project;

    private String applicationYear;

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

    public PrismGender getGender() {
        return gender;
    }

    public void setGender(PrismGender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public PrismDomicile getNationality() {
        return nationality;
    }

    public void setNationality(PrismDomicile nationality) {
        this.nationality = nationality;
    }

    public PrismDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(PrismDomicile domicile) {
        this.domicile = domicile;
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

    public String getApplicationYear() {
        return applicationYear;
    }

    public void setApplicationYear(String applicationYear) {
        this.applicationYear = applicationYear;
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

    public PrismGender getGenderDisplay() {
        return gender;
    }

    public String getDateOfBirthDisplay(String dateFormat) {
        return dateOfBirth == null ? "" : dateOfBirth.toString(dateFormat);
    }

    public PrismDomicile getNationalityDisplay() {
        return nationality;
    }

    public PrismDomicile getDomicileDisplay() {
        return domicile;
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

    public String getApplicationYearDisplay() {
        return applicationYear == null ? "" : applicationYear;
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

    public PrismOfferType getConfirmedOfferTypeDisplay() {
        return confirmedOfferType == null ? null : confirmedOfferType;
    }

    public String getConfirmedStartDateDisplay(String dateFormat) {
        return confirmedStartDate == null ? "" : confirmedStartDate.toString(dateFormat);
    }

    public PrismStateGroup getStateDisplay() {
        return state;
    }

    public String getRefereesDisplay() {
        return referees == null ? "" : referees.toString();
    }

}
