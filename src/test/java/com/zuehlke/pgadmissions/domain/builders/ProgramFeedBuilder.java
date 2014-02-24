package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;

public class ProgramFeedBuilder {

    private Integer id;
    private String feedUrl;
    private QualificationInstitution institution;

    public ProgramFeedBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ProgramFeedBuilder feedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
        return this;
    }

    public ProgramFeedBuilder institution(QualificationInstitution institution) {
        this.institution = institution;
        return this;
    }

    public ProgramFeed build() {
        ProgramFeed programFeed = new ProgramFeed();
        programFeed.setId(id);
        programFeed.setFeedUrl(feedUrl);
        programFeed.setInstitution(institution);
        return programFeed;
    }
}
