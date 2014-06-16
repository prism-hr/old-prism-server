package com.zuehlke.pgadmissions.rest.domain;

import java.util.HashMap;

import com.google.common.collect.Maps;

public class ResourceConsoleListRowRepresentation {

    private String id;
    
    private String code;
    
    private String raisesUrgentFlag;
    
    private String state;
    
    private String creatorFirstName;
    
    private String creatorFirstName2;
    
    private String creatorFirstName3;
    
    private String creatorLastName;
    
    private String programTitle;
    
    private String projectTitle;
    
    private String displayTimestamp;
    
    private String actionList;
    
    private String averageRating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(String raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreatorFirstName() {
        return creatorFirstName;
    }

    public void setCreatorFirstName(String creatorFirstName) {
        this.creatorFirstName = creatorFirstName;
    }

    public String getCreatorFirstName2() {
        return creatorFirstName2;
    }

    public void setCreatorFirstName2(String creatorFirstName2) {
        this.creatorFirstName2 = creatorFirstName2;
    }

    public String getCreatorFirstName3() {
        return creatorFirstName3;
    }

    public void setCreatorFirstName3(String creatorFirstName3) {
        this.creatorFirstName3 = creatorFirstName3;
    }

    public String getCreatorLastName() {
        return creatorLastName;
    }

    public void setCreatorLastName(String creatorLastName) {
        this.creatorLastName = creatorLastName;
    }
    
    public String getCreatorDisplayName() {
        String creatorMiddleNames = " ";
        if (!(creatorFirstName2 == null && creatorFirstName3 == null)) {
            creatorMiddleNames = creatorMiddleNames + "(";
            boolean requireSpace = false;
            if (creatorFirstName2 != null) {
                creatorMiddleNames = creatorMiddleNames + creatorFirstName2;
                requireSpace = true;
            }
            if (creatorFirstName3 != null) {
                creatorMiddleNames = (requireSpace ? " " : "") + creatorFirstName3;
            }
            creatorMiddleNames = creatorMiddleNames + ") ";
        }
        return creatorFirstName + creatorMiddleNames + creatorLastName;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getDisplayTimestamp() {
        return displayTimestamp;
    }

    public void setDisplayTimestamp(String displayTimestamp) {
        this.displayTimestamp = displayTimestamp;
    }

    public HashMap<String, String> getActionList() {
        String[] actionDefinitions = actionList.split(",");
        HashMap<String, String> unpackedActions = Maps.newHashMap();
        for (String actionDefinition : actionDefinitions) {
            String[] actionDefinitionParts = actionDefinition.split("|");
            unpackedActions.put(actionDefinitionParts[0], actionDefinitionParts[1]);
        }
        return unpackedActions;
    }

    public void setActionList(String actionList) {
        this.actionList = actionList;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }
    
}
