package com.zuehlke.pgadmissions.domain.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;

public enum PrismReservedActionKeyword {

    CREATE(new ActionTypeMatch(PrismActionType.USER_INVOCATION, MatchMode.CONTAINS, MatchResponse.RESTRICT)), //
    CONCLUDE(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.ENDS_WITH, MatchResponse.RESTRICT)), //
    ESCALATE(new ActionTypeMatch(PrismActionType.SYSTEM_ESCALATION, MatchMode.ENDS_WITH, MatchResponse.RESTRICT)), //
    EXPORT(new ActionTypeMatch(PrismActionType.SYSTEM_ESCALATION, MatchMode.ENDS_WITH, MatchResponse.RESTRICT)), //
    IMPORT(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.CONTAINS, MatchResponse.RESTRICT)), //
    RESTORE(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.ENDS_WITH, MatchResponse.RESTRICT)), //
    SUSPEND(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.ENDS_WITH, MatchResponse.RESTRICT)), //
    TERMINATE(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.ENDS_WITH, MatchResponse.RESTRICT));

    private ActionTypeMatch actionTypeMatch;

    private static HashMap<String, PrismReservedActionKeyword> keywordIndex = Maps.newHashMap();
    
    static {
        for (PrismReservedActionKeyword keyword: PrismReservedActionKeyword.values()) {
            keywordIndex.put(keyword.toString(), keyword);
        }
    }
    
    private PrismReservedActionKeyword(ActionTypeMatch actionTypeMatch) {
        this.actionTypeMatch = actionTypeMatch;
    }

    public ActionTypeMatch getActionTypeMatch() {
        return actionTypeMatch;
    }

    public static boolean validateActionDefinition(Action action) {
        List<String> actionStringParts = Arrays.asList(action.getId().toString().split("_"));
        for (String actionStringPart : actionStringParts) {
            PrismReservedActionKeyword keywordMatch = keywordIndex.get(actionStringPart);
            if (keywordMatch != null) {
                ActionTypeMatch actionTypeMatch = keywordMatch.getActionTypeMatch();
                if (actionTypeMatch.getMatchResponse() == MatchResponse.PREVENT || (action.getActionType() != actionTypeMatch.getActionType() && //
                        (actionTypeMatch.getMatchMode() == MatchMode.CONTAINS || actionStringPart == actionStringParts.get(actionStringParts.size() - 1)))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static class ActionTypeMatch {

        private PrismActionType actionType;

        private MatchMode matchMode;

        private MatchResponse matchResponse;

        public ActionTypeMatch(PrismActionType actionType, MatchMode matchMode, MatchResponse matchResponse) {
            this.actionType = actionType;
            this.matchMode = matchMode;
            this.matchResponse = matchResponse;
        }

        public PrismActionType getActionType() {
            return actionType;
        }

        public MatchMode getMatchMode() {
            return matchMode;
        }

        public MatchResponse getMatchResponse() {
            return matchResponse;
        }

    }

    private enum MatchMode {
        CONTAINS, ENDS_WITH;
    }

    private enum MatchResponse {
        PREVENT, RESTRICT;
    }

}
