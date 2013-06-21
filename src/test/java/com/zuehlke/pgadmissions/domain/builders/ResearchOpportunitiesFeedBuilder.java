package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;

public class ResearchOpportunitiesFeedBuilder {

    private Integer id;

    private String title;
    
    private RegisteredUser user;
    
    private FeedFormat feedFormat;
    
    private List<Program> programs = new ArrayList<Program>();
    
    public ResearchOpportunitiesFeedBuilder id(final Integer id) {
        this.id = id;
        return this;
    }
    
    public ResearchOpportunitiesFeedBuilder title(final String title) {
        this.title = title;
        return this;
    }
    
    public ResearchOpportunitiesFeedBuilder user(final RegisteredUser user) {
        this.user = user;
        return this;
    }
    
    public ResearchOpportunitiesFeedBuilder feedFormat(final FeedFormat format) {
        this.feedFormat = format;
        return this;
    }
    
    public ResearchOpportunitiesFeedBuilder programs(final Program... programs) {
        this.programs = Arrays.asList(programs);
        return this;
    }
    
    public ResearchOpportunitiesFeed build() {
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeed();
        feed.setFeedFormat(feedFormat);
        feed.setId(id);
        feed.setPrograms(programs);
        feed.setTitle(title);
        feed.setUser(user);
        return feed;
    }
}
