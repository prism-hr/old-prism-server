package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Language;

public class LanguageBuilder {
    private String name;
    private Integer id;
    private Boolean enabled;
    private String code;

    public LanguageBuilder code(String code) {
        this.code = code;
        return this;
    }

    public LanguageBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public LanguageBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LanguageBuilder enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Language build() {
        Language language = new Language();
        language.setId(id);
        language.setName(name);
        language.setEnabled(enabled);
        language.setCode(code);
        return language;
    }
}
