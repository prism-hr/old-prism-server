package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.FilterProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.FilterExpression.*;
import static com.zuehlke.pgadmissions.domain.definitions.FilterPropertyType.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum ResourceListFilterProperty implements FilterProperty {

    USER("user.id", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION)), //
    CODE("code", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    INSTITUTION_TITLE("institution.id", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION, PROJECT, PROGRAM)), //
    PROGRAM_TITLE("program.id", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION, PROJECT)), //
    PROJECT_TITLE("project.id", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION)), //
    TITLE("title", STRING, Arrays.asList(CONTAIN), Arrays.asList(PROJECT, PROGRAM, INSTITUTION)), //
    STATE_GROUP_TITLE("state.id", STATE_GROUP, Arrays.asList(EQUAL), Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    CREATED_TIMESTAMP("createdTimestamp", DATE, Arrays.asList(BETWEEN, GREATER, LESSER), Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    SUBMITTED_TIMESTAMP("submittedTimestamp", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), Arrays.asList(APPLICATION)), //
    UPDATED_TIMESTAMP("updatedTimestamp", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    DUE_DATE("dueDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), Arrays.asList(APPLICATION)), //
    CLOSING_DATE("closingDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), Arrays.asList(APPLICATION)), //
    STUDY_LOCATION("studyDetail.studyLocation", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION)), //
    STUDY_DIVISION("studyDetail.studyDivision", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION)), //
    STUDY_AREA("studyDetail.studyArea", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION)), //
    STUDY_APPLICATION("studyDetail.studyApplicationId", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION)), //
    CONFIRMED_START_DATE("confirmedStartDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), Arrays.asList(APPLICATION)), //
    RATING("applicationRatingAverage", NUMBER, Arrays.asList(BETWEEN, GREATER, LESSER), Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    REFERRER("referrer", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    SUPERVISOR("id", STRING, Arrays.asList(CONTAIN), Arrays.asList(APPLICATION)), //
    PROJECT_USER("id", STRING, Arrays.asList(CONTAIN), Arrays.asList(PROJECT)), //
    PROGRAM_USER("id", STRING, Arrays.asList(CONTAIN), Arrays.asList(PROGRAM)), //
    INSTITUTION_USER("id", STRING, Arrays.asList(CONTAIN), Arrays.asList(INSTITUTION)), //
    PRIMARY_THEME("primaryTheme", STRING, Arrays.asList(CONTAIN, EQUAL), Arrays.asList(APPLICATION)), //
    SECONDARY_THEME("secondaryTheme", STRING, Arrays.asList(CONTAIN, EQUAL), Arrays.asList(APPLICATION)), //
    RESERVE_STATUS("applicationReserveStatus", APPLICATION_RESERVE_STATUS, Arrays.asList(EQUAL), Arrays.asList(APPLICATION));

    private String propertyName;

    private FilterPropertyType propertyType;

    private List<FilterExpression> permittedExpressions;

    private List<PrismScope> permittedScopes;

    private static HashMultimap<PrismScope, ResourceListFilterProperty> permittedFilterProperties = HashMultimap.create();

    static {
        for (ResourceListFilterProperty property : ResourceListFilterProperty.values()) {
            for (PrismScope scope : property.getPermittedScopes()) {
                permittedFilterProperties.put(scope, property);
            }
        }
    }

    private ResourceListFilterProperty(String propertyName, FilterPropertyType propertyType, List<FilterExpression> permittedExpressions,
                                       List<PrismScope> permittedScopes) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
        this.propertyType = propertyType;
        this.permittedScopes = permittedScopes;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public FilterPropertyType getPropertyType() {
        return propertyType;
    }

    @Override
    public List<FilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }

    public List<PrismScope> getPermittedScopes() {
        return permittedScopes;
    }

    public static Set<ResourceListFilterProperty> getPermittedFilterProperties(PrismScope scope) {
        return permittedFilterProperties.get(scope);
    }

    public static boolean isPermittedFilterProperty(PrismScope scope, ResourceListFilterProperty property) {
        return ResourceListFilterProperty.getPermittedFilterProperties(scope).contains(property);
    }

}
