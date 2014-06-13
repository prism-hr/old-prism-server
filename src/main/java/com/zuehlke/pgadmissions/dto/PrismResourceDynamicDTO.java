package com.zuehlke.pgadmissions.dto;

import java.util.HashMap;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class PrismResourceDynamicDTO {

    private Integer id;
    
    private String code;
    
    private boolean raisesUrgentFlag;
    
    @Enumerated(EnumType.STRING)
    private PrismState state;
    
    private String creatorFirstName;
    
    private String creatorFirstName2;
    
    private String creatorFirstName3;
    
    private String creatorLastName;
    
    private String programTitle;
    
    private String projectTitle;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime displayTimestamp;
    
    private String actionList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public PrismState getState() {
        return state;
    }

    public void setState(PrismState state) {
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
        String creatorMiddleNames = "";
        if (!(creatorFirstName2 == null && creatorFirstName3 == null)) {
            creatorMiddleNames = creatorMiddleNames + "( ";
            boolean requireSpace = false;
            if (creatorFirstName2 != null) {
                creatorMiddleNames = creatorMiddleNames + creatorFirstName2;
                requireSpace = true;
            }
            if (creatorFirstName3 != null) {
                creatorMiddleNames = (requireSpace ? " " : "") + creatorFirstName3 + ")";
            }
        }
        return creatorFirstName + creatorMiddleNames + " " + creatorLastName;
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

    public DateTime getDisplayTimestamp() {
        return displayTimestamp;
    }

    public void setDisplayTimestamp(DateTime displayTimestamp) {
        this.displayTimestamp = displayTimestamp;
    }

    public HashBiMap<Boolean, PrismAction> getActionList() {
        String[] actionDefinitions = actionList.split(",");
        HashBiMap<PrismAction, Boolean> unpackedActions = HashBiMap.create();
        HashMap<Integer, PrismAction> overriddenActions = Maps.newHashMap();
        for (String actionDefinition : actionDefinitions) {
            String[] actionDefinitionParts = actionDefinition.split("|");
            PrismAction unpackedAction = PrismAction.valueOf(actionDefinitionParts[1]);
            unpackedActions.put(unpackedAction, (actionDefinitionParts[0] == "1" ? true : false));
            if (actionDefinitionParts.length == 3) {
                overriddenActions.put(Integer.parseInt(actionDefinitionParts[2]), unpackedAction);
            }
        }
        for (Integer i = 0; i < overriddenActions.size() - 1; i ++) {
            unpackedActions.remove(overriddenActions.get(i));
        }
        return (HashBiMap<Boolean, PrismAction>) unpackedActions.inverse();
    }

    public void setActionList(String actionList) {
        this.actionList = actionList;
    }
    
}
