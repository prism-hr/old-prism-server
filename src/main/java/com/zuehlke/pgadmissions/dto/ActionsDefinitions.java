package com.zuehlke.pgadmissions.dto;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class ActionsDefinitions {

    private Map<String, String> actions = new LinkedHashMap<String, String>();
    
    private boolean requiresAttention;

    public ActionsDefinitions() {
    }
    
    public ActionsDefinitions(final Map<String, String> map, final boolean requiresAttention) {
        this.actions = map;
        this.requiresAttention = requiresAttention;
    }

    public boolean isRequiresAttention() {
        return requiresAttention;
    }

    public void setRequiresAttention(boolean requiresAttention) {
        this.requiresAttention = requiresAttention;
    }
    
    public void addAction(String name, String displayValue){
        actions.put(name, displayValue);
    }

    public Map<String, String> getActions() {
        return actions;
    }
    
    public ActionsDefinitions sort() {
        return new ActionsDefinitions(sortByComparator(actions), requiresAttention);
    }
    
    private static Map<String, String> sortByComparator(final Map<String, String> unsortMap) {       
        LinkedList<Map.Entry<String, String>> list = new LinkedList<Entry<String, String>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Iterator<Entry<String, String>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    } 
}
