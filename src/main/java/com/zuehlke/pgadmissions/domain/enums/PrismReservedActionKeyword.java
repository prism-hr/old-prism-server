package com.zuehlke.pgadmissions.domain.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;

public enum PrismReservedActionKeyword {

    CREATE(new ActionTypeMatch(PrismActionType.USER_INVOCATION, MatchMode.CONTAINS)), //
    CONCLUDE(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.ENDS_WITH)), //
    ESCALATE(new ActionTypeMatch(PrismActionType.SYSTEM_ESCALATION, MatchMode.ENDS_WITH)), //
    EXPORT(new ActionTypeMatch(PrismActionType.SYSTEM_ESCALATION, MatchMode.ENDS_WITH)), //
    IMPORT(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.CONTAINS)), //
    RESTORE(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.ENDS_WITH)), //
    SUSPEND(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.ENDS_WITH)), //
    TERMINATE(new ActionTypeMatch(PrismActionType.SYSTEM_PROPAGATION, MatchMode.ENDS_WITH));

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
                if ((action.getActionType() != actionTypeMatch.getActionType() && //
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
        
        public ActionTypeMatch(PrismActionType actionType, MatchMode matchMode) {
            this.actionType = actionType;
            this.matchMode = matchMode;
        }

        public PrismActionType getActionType() {
            return actionType;
        }

        public MatchMode getMatchMode() {
            return matchMode;
        }

    }

    private enum MatchMode {
        CONTAINS, ENDS_WITH;
    }

}
