package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ProgramImport;
import com.zuehlke.pgadmissions.domain.Institution;

public class ProgramFeedBuilder {

    private Integer id;
    private String feedUrl;
    private Institution institution;

    public ProgramFeedBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ProgramFeedBuilder feedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
        return this;
    }

    public ProgramFeedBuilder institution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public ProgramImport build() {
        ProgramImport programFeed = new ProgramImport();
        programFeed.setId(id);
        programFeed.setFeedUrl(feedUrl);
        programFeed.setInstitution(institution);
        return programFeed;
    }
}
