package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.zuehlke.pgadmissions.domain.FilterConstraint;

@Component
public class ApplicationsFiltersPropertyEditor extends PropertyEditorSupport {

    public ApplicationsFiltersPropertyEditor() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setAsText(String applicationsFilter) throws IllegalArgumentException {
        if (applicationsFilter == null || StringUtils.isBlank(applicationsFilter)) {
            setValue(null);
            return;
        }

        final GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(List.class, new JsonDeserializer<List<FilterConstraint>>() {

            @Override
            public List<FilterConstraint> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

                List<FilterConstraint> filters = new ArrayList<FilterConstraint>();
                for (JsonElement jsonFilter : json.getAsJsonArray()) {
                    FilterConstraint filter = gson.create().fromJson(jsonFilter, FilterConstraint.class);
                    
                    String searchTerm = filter.getSearchTerm();
                    filter.setSearchTerm(searchTerm.trim());
                    
                    filters.add(filter);
                }
                return filters;
            }
        });
        List<FilterConstraint> filters = gson.create().fromJson(applicationsFilter, List.class);
        setValue(filters);
    }

    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }
        return null;
    }
}
