package uk.co.alumeni.prism.dto;

import static org.jboss.util.Strings.EMPTY;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.definitions.PrismAgeRange;
import uk.co.alumeni.prism.domain.definitions.PrismDomicile;
import uk.co.alumeni.prism.domain.definitions.PrismEthnicity;
import uk.co.alumeni.prism.domain.definitions.PrismGender;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;

public class ApplicationReportListRowDTO {

    private Integer id;

    private String name;

    private String email;

    private PrismGender gender;

    private PrismAgeRange ageRange;

    private PrismEthnicity ethnicity;

    private LocalDate dateOfBirth;

    private PrismDomicile nationality;

    private PrismDomicile domicile;

    private String institution;

    private String department;

    private String program;

    private String project;

    private String primaryTheme;

    private String secondaryTheme;

    private String primaryLocation;
    
    private String primaryLocationDescription;

    private String secondaryLocation;
    
    private String secondaryLocationDescription;

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

    public PrismAgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(PrismAgeRange ageRange) {
        this.ageRange = ageRange;
    }

    public PrismEthnicity getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(PrismEthnicity ethnicity) {
        this.ethnicity = ethnicity;
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

    public String getPrimaryTheme() {
        return primaryTheme;
    }

    public void setPrimaryTheme(String primaryTheme) {
        this.primaryTheme = primaryTheme;
    }

    public String getSecondaryTheme() {
        return secondaryTheme;
    }

    public void setSecondaryTheme(String secondaryTheme) {
        this.secondaryTheme = secondaryTheme;
    }

    public String getPrimaryLocation() {
        return primaryLocation;
    }

    public void setPrimaryLocation(String primaryLocation) {
        this.primaryLocation = primaryLocation;
    }

    public String getPrimaryLocationDescription() {
        return primaryLocationDescription;
    }

    public void setPrimaryLocationDescription(String primaryLocationDescription) {
        this.primaryLocationDescription = primaryLocationDescription;
    }

    public String getSecondaryLocation() {
        return secondaryLocation;
    }

    public void setSecondaryLocation(String secondaryLocation) {
        this.secondaryLocation = secondaryLocation;
    }

    public String getSecondaryLocationDescription() {
        return secondaryLocationDescription;
    }

    public void setSecondaryLocationDescription(String secondaryLocationDescription) {
        this.secondaryLocationDescription = secondaryLocationDescription;
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

    public String getIdDisplay() {
        return id == null ? EMPTY : id.toString();
    }

    public String getNameDisplay() {
        return name == null ? EMPTY : name;
    }

    public String getEmailDisplay() {
        return email == null ? EMPTY : email;
    }

    public PrismGender getGenderDisplay() {
        return gender;
    }

    public PrismAgeRange getAgeRangeDisplay() {
        return ageRange;
    }

    public PrismEthnicity getEthnicityDisplay() {
        return ethnicity;
    }

    public String getDateOfBirthDisplay(String dateFormat) {
        return dateOfBirth == null ? EMPTY : dateOfBirth.toString(dateFormat);
    }

    public PrismDomicile getNationalityDisplay() {
        return nationality;
    }

    public PrismDomicile getDomicileDisplay() {
        return domicile;
    }

    public String getInstitutionDisplay() {
        return institution == null ? EMPTY : institution;
    }

    public String getDepartmentDisplay() {
        return department == null ? EMPTY : department;
    }

    public String getProgramDisplay() {
        return program == null ? EMPTY : program;
    }

    public String getProjectDisplay() {
        return project == null ? EMPTY : project;
    }

    public String getPrimaryThemeDisplay() {
        return primaryTheme == null ? EMPTY : primaryTheme;
    }

    public String getSecondaryThemeDisplay() {
        return secondaryTheme == null ? EMPTY : secondaryTheme;
    }
    
    public String getPrimaryLocationDisplay() {
        return primaryLocation == null ? EMPTY : primaryLocation;
    }
    
    public String getPrimaryLocationDescriptionDisplay() {
        return primaryLocationDescription == null ? EMPTY : primaryLocationDescription;
    }
    
    public String getSecondaryLocationDisplay() {
        return secondaryLocation == null ? EMPTY : secondaryLocation;
    }
    
    public String getSecondaryLocationDescriptionDisplay() {
        return secondaryLocationDescription == null ? EMPTY : secondaryLocationDescription;
    }

    public String getApplicationYearDisplay() {
        return applicationYear == null ? EMPTY : applicationYear;
    }

    public String getCreatedDateDisplay(String dateFormat) {
        return createdDate == null ? EMPTY : createdDate.toString(dateFormat);
    }

    public String getClosingDateDisplay(String dateFormat) {
        return closingDate == null ? EMPTY : closingDate.toString(dateFormat);
    }

    public String getSubmittedDateDisplay(String dateFormat) {
        return submittedDate == null ? EMPTY : submittedDate.toString(dateFormat);
    }

    public String getUpdatedDateDisplay(String dateFormat) {
        return updatedDate == null ? EMPTY : updatedDate.toString(dateFormat);
    }

    public String getAcademicYearDisplay() {
        DateTime baseline = submittedDate == null ? createdDate : submittedDate;
        if (baseline == null) {
            return EMPTY;
        }
        Integer baselineMonth = baseline.getMonthOfYear();
        return baselineMonth < 10 ? Integer.toString(baseline.getYear()) : Integer.toString(baseline.plusYears(1).getYear());
    }

    public String getRatingCountDisplay() {
        return ratingCount == null ? EMPTY : ratingCount.toString();
    }

    public String getRatingAverageDisplay() {
        return ratingAverage == null ? EMPTY : ratingAverage.toPlainString();
    }

    public String getProvidedReferencesDisplay() {
        return providedReferences == null ? EMPTY : providedReferences.toString();
    }

    public String getDeclinedReferencesDisplay() {
        return declinedReferences == null ? EMPTY : declinedReferences.toString();
    }

    public String getConfirmedStartDateDisplay(String dateFormat) {
        return confirmedStartDate == null ? EMPTY : confirmedStartDate.toString(dateFormat);
    }

    public PrismStateGroup getStateDisplay() {
        return state;
    }

    public String getRefereesDisplay() {
        return referees == null ? EMPTY : referees.toString();
    }

}
