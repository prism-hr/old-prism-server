package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Collections;
import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;

public enum PrismReportColumn {

    ID(PrismDisplayPropertyDefinition.SYSTEM_ID, "application.id as id", null, null), //
    USER_FULL_NAME(PrismDisplayPropertyDefinition.SYSTEM_FULL_NAME, "user.fullName as name", null, null), //
    ;

    private PrismDisplayPropertyDefinition title;

    private String selectExpression;

    private List<PrismWorkflowPropertyDefinition> inclusionConditions;

    private List<PrismActionRedactionType> redactionConditions;

    private PrismReportColumn(PrismDisplayPropertyDefinition title, String selectExpression, List<PrismWorkflowPropertyDefinition> inclusionConditions,
            List<PrismActionRedactionType> redactionConditions) {
        this.title = title;
        this.selectExpression = selectExpression;
        this.inclusionConditions = inclusionConditions;
        this.redactionConditions = redactionConditions;
    }

    public final PrismDisplayPropertyDefinition getTitle() {
        return title;
    }

    public final String getSelectExpression() {
        return selectExpression;
    }

    public final List<PrismWorkflowPropertyDefinition> getInclusionConditions() {
        return inclusionConditions == null ? Collections.<PrismWorkflowPropertyDefinition> emptyList() : inclusionConditions;
    }

    public final List<PrismActionRedactionType> getRedactionConditions() {
        return redactionConditions == null ? Collections.<PrismActionRedactionType> emptyList() : redactionConditions;
    }

}
