package uk.co.alumeni.prism.domain.definitions;

import static uk.co.alumeni.prism.domain.definitions.PrismApplicationReportColumnAccessorType.DATE;
import static uk.co.alumeni.prism.domain.definitions.PrismApplicationReportColumnAccessorType.DISPLAY_PROPERTY;
import static uk.co.alumeni.prism.domain.definitions.PrismApplicationReportColumnAccessorType.STRING;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_AGE_RANGE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_CONFIRMED_START_DATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_DECLINED_REFERENCES;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_PRIMARY_LOCATION;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_PRIMARY_LOCATION_DESCRIPTION;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_PRIMARY_THEME;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_PROVIDED_REFERENCES;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_REFEREES;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_SECONDARY_LOCATION;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_SECONDARY_LOCATION_DESCRIPTION;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_SECONDARY_THEME;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_SUBMISSION_DATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_DOMICILE_LABEL;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_ETHNICITY_HINT;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_GENDER_LABEL;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_NATIONALITY_LABEL;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ACADEMIC_YEAR;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_AVERAGE_RATING;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_CLOSING_DATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_CREATED_DATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EMAIL;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ID;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NAME;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROJECT;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_STATE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_TOTAL_RATING;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_UPDATED_DATE;

import java.util.Collections;
import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowConstraint;

public enum PrismApplicationReportColumn {

    ID(SYSTEM_ID, "application.id", null, false, "id", STRING), //
    USER_NAME(SYSTEM_NAME, "user.fullName", null, false, "name", STRING), //
    USER_EMAIL(SYSTEM_EMAIL, "user.email", null, false, "email", STRING), //
    USER_DATE_OF_BIRTH(PROFILE_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL, "userPersonalDetail.dateOfBirth", null, false, "dateOfBirth", DATE), //
    USER_GENDER(PROFILE_PERSONAL_DETAIL_GENDER_LABEL, "applicationPersonalDetail.gender", null, false, "gender", DISPLAY_PROPERTY), //
    USER_AGE_RANGE(APPLICATION_AGE_RANGE, "applicationPersonalDetail.ageRange.id", null, false, "ageRange", DISPLAY_PROPERTY), //
    USER_ETHNICITY(PROFILE_PERSONAL_DETAIL_ETHNICITY_HINT, "applicationPersonalDetail.ethnicity", null, false, "ethnicity", DISPLAY_PROPERTY), //
    USER_NATIONALITY(PROFILE_PERSONAL_DETAIL_NATIONALITY_LABEL, "applicationPersonalDetail.nationality.id", null, false, "nationality", DISPLAY_PROPERTY), //
    USER_DOMICILE(PROFILE_PERSONAL_DETAIL_DOMICILE_LABEL, "applicationPersonalDetail.domicile.id", null, false, "domicile", DISPLAY_PROPERTY), //
    INSTITUTION_NAME(SYSTEM_INSTITUTION, "institution.name", null, false, "institution", STRING), //
    DEPARTMENT_NAME(SYSTEM_DEPARTMENT, "department.name", null, false, "department", STRING), //
    PROGRAM_NAME(SYSTEM_PROGRAM, "program.name", null, false, "program", STRING), //
    PROJECT_NAME(SYSTEM_PROJECT, "project.name", null, false, "project", STRING), //
    PRIMARY_THEME(APPLICATION_PRIMARY_THEME, "primaryThemeTag.name", null, false, "primaryTheme", STRING), //
    SECONDARY_THEME(APPLICATION_SECONDARY_THEME, "secondaryThemeTag.name", null, false, "secondaryTheme", STRING), //
    PRIMARY_LOCATION(APPLICATION_PRIMARY_LOCATION, "concat(primaryLocationInstitution.name, ' ', primaryLocationDepartment.name)", null, false, "primaryLocation", STRING), //
    PRIMARY_LOCATION_DESCRIPTION(APPLICATION_PRIMARY_LOCATION_DESCRIPTION, "primaryLocation.description", null, false, "primaryLocationDescription", STRING), //
    SECONDARY_LOCATION(APPLICATION_SECONDARY_LOCATION, "concat(secondaryLocationInstitution.name, ' ', secondaryLocationDepartment.name)", null, false, "secondaryLocation", STRING), //
    SECONDARY_LOCATION_DESCRIPTION(APPLICATION_SECONDARY_LOCATION_DESCRIPTION, "secondaryLocation.description", null, false, "secondaryLocationDescription", STRING), //
    APPLICATION_YEAR(SYSTEM_ACADEMIC_YEAR, "application.applicationYear", null, false, "applicationYear", STRING), //
    CREATED_DATE(SYSTEM_CREATED_DATE, "application.createdTimestamp", null, false, "createdDate", DATE), //
    CLOSING_DATE(SYSTEM_CLOSING_DATE, "application.closingDate", null, false, "closingDate", DATE), //
    SUBMITTED_DATE(APPLICATION_SUBMISSION_DATE, "application.submittedTimestamp", null, false, "submittedDate", DATE), //
    UPDATED_DATE(SYSTEM_UPDATED_DATE, "application.updatedTimestamp", null, false, "updatedDate", DATE), //
    RATING_COUNT(SYSTEM_TOTAL_RATING, "application.applicationRatingCount", null, true, "ratingCount", STRING), //
    RATING_AVERAGE(SYSTEM_AVERAGE_RATING, "application.applicationRatingAverage", null, true, "ratingAverage", STRING), //
    STATE(SYSTEM_STATE, "state.stateGroup.id", null, false, "state", DISPLAY_PROPERTY), //
    REFEREE_COUNT(APPLICATION_REFEREES, "count(distinct referee.id)", null, false, "referees", STRING), //
    PROVIDED_REFERENCE_COUNT(APPLICATION_PROVIDED_REFERENCES, "count(distinct provideReferenceComment.id)", null, false, "providedReferences", STRING), //
    DECLINED_REFERENCE_COUNT(APPLICATION_DECLINED_REFERENCES, "count(distinct declineReferenceComment.id)", null, false, "declinedReferences", STRING), //
    START_DATE(APPLICATION_CONFIRMED_START_DATE, "application.confirmedStartDate", null, false, "confirmedStartDate", DATE);

    private PrismDisplayPropertyDefinition title;

    private String column;

    private List<PrismWorkflowConstraint> definitions;

    private boolean hasRedactions;

    private String accessor;

    private PrismApplicationReportColumnAccessorType accessorType;

    private PrismApplicationReportColumn(PrismDisplayPropertyDefinition title, String column, List<PrismWorkflowConstraint> definitions,
            boolean hasRedactions, String accessor, PrismApplicationReportColumnAccessorType accessorType) {
        this.title = title;
        this.column = column;
        this.definitions = definitions;
        this.hasRedactions = hasRedactions;
        this.accessor = accessor;
        this.accessorType = accessorType;
    }

    public final PrismDisplayPropertyDefinition getTitle() {
        return title;
    }

    public final String getColumn() {
        return column;
    }

    public final List<PrismWorkflowConstraint> getDefinitions() {
        return definitions == null ? Collections.<PrismWorkflowConstraint> emptyList() : definitions;
    }

    public boolean isHasRedactions() {
        return hasRedactions;
    }

    public final String getAccessor() {
        return accessor;
    }

    public final PrismApplicationReportColumnAccessorType getAccessorType() {
        return accessorType;
    }

    public final String getColumnAccessor() {
        return column + " as " + accessor;
    }

}
