package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ResearchOpportunitiesFeedDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class ResearchOpportunitiesFeedService {

    private static final int DEFAULT_LARGE_FEED_ID = -2;

    private static final int DEFAULT_SMALL_FEED_ID = -1;

    public static final String LARGE_IFRAME = "/private/prospectus/large_feed_iframe.ftl";

    public static final String SMALL_IFRAME = "/private/prospectus/small_feed_iframe.ftl";

    private final UserService userService;

    private final ResearchOpportunitiesFeedDAO dao;

    private final ProgramService programService;

    private final FreeMarkerConfigurer freeMarkerConfigurer;

    private final String host;

    public ResearchOpportunitiesFeedService() {
        this(null, null, null, null, null);
    }

    @Autowired
    public ResearchOpportunitiesFeedService(final ResearchOpportunitiesFeedDAO dao, final ProgramService programService, final UserService userService,
            final FreeMarkerConfigurer freeMarkerConfigurer, @Value("${application.host}") final String host) {
        this.dao = dao;
        this.programService = programService;
        this.host = host;
        this.userService = userService;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }

    public String getIframeHtmlCode(final ResearchOpportunitiesFeed feed) {
        try {
            String templateName = feed.getFeedFormat() == FeedFormat.SMALL ? SMALL_IFRAME : LARGE_IFRAME;
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("host", host);
            if (feed.getId() == DEFAULT_SMALL_FEED_ID || feed.getId() == DEFAULT_LARGE_FEED_ID) {
                dataMap.put("feedKey", "OPPORTUNITIESBYUSERUSERNAME");
                dataMap.put("feedKeyValue", userService.getCurrentUser().getUsername());
            } else {
                dataMap.put("feedKey", "OPPORTUNITIESBYFEEDID");
                dataMap.put("feedKeyValue", feed.getId());
            }
            StringWriter writer = new StringWriter();
            template.process(dataMap, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public ResearchOpportunitiesFeed saveNewFeed(final List<Integer> selectedProgramIds, final User user, final FeedFormat format, final String title) {
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeed();
        for (Integer programId : selectedProgramIds) {
            feed.getPrograms().add((Program) programService.getById(programId));
        }
        feed.setFeedFormat(format);
        feed.setTitle(title);
        feed.setUser(user);
        dao.save(feed);
        return feed;
    }

    @Transactional(readOnly = true)
    public List<ResearchOpportunitiesFeed> getAllFeedsForUser(final User user) {
        ResearchOpportunitiesFeed defaultFeedSmall = getDefaultOpportunitiesFeed(user, FeedFormat.SMALL);
        ResearchOpportunitiesFeed defaultFeedLarge = getDefaultOpportunitiesFeed(user, FeedFormat.LARGE);
        List<ResearchOpportunitiesFeed> savedFeeds = dao.getAllFeedsForUser(user);

        List<ResearchOpportunitiesFeed> allFeeds = Lists.newArrayListWithCapacity(savedFeeds.size() + 2);
        allFeeds.add(defaultFeedSmall);
        allFeeds.add(defaultFeedLarge);
        allFeeds.addAll(savedFeeds);

        return allFeeds;
    }

    private ResearchOpportunitiesFeed getDefaultOpportunitiesFeed(final User user, FeedFormat format) {
        List<Program> defaultPrograms = programService.getProgramsForWhichCanManageProjects(user);

        ResearchOpportunitiesFeed defaultFeedSmall = new ResearchOpportunitiesFeed();
        defaultFeedSmall.setId(format == FeedFormat.SMALL ? DEFAULT_SMALL_FEED_ID : DEFAULT_LARGE_FEED_ID);
        String title = format == FeedFormat.SMALL ? "My Opportunities Feed - Small" : "My Opportunities Feed - Large";
        defaultFeedSmall.setTitle(title);
        defaultFeedSmall.setPrograms(defaultPrograms);
        defaultFeedSmall.setUser(user);
        defaultFeedSmall.setFeedFormat(format);
        return defaultFeedSmall;
    }

    private List<ResearchOpportunitiesFeed> getDefaultOpportunitiesFeeds(List<User> users, FeedFormat format) {
        LinkedList<ResearchOpportunitiesFeed> feeds = Lists.newLinkedList();
        for (User linkedUser : users) {
            feeds.add(getDefaultOpportunitiesFeed(linkedUser, format));
        }
        return feeds;
    }

    public List<ResearchOpportunitiesFeed> getDefaultOpportunitiesFeedsByUsername(String username, FeedFormat format) {
        User user = userService.getUserByUsername(username);
        Assert.notNull(user);
        return getDefaultOpportunitiesFeeds(user.getLinkedAccounts(), format);
    }

    public List<ResearchOpportunitiesFeed> getDefaultOpportunitiesFeedsByUpi(String upi, FeedFormat format) {
        return getDefaultOpportunitiesFeeds(userService.getUsersWithUpi(upi), format);
    }

    @Transactional(readOnly = true)
    public boolean isUniqueFeedTitleForUser(final String title, final User user) {
        return dao.isUniqueFeedTitleForUser(title, user);
    }

    @Transactional
    public void deleteById(final Integer feedId) {
        Assert.isTrue(isOwner(dao.getById(feedId)));
        dao.deleteById(feedId);
    }

    private boolean isOwner(ResearchOpportunitiesFeed feed) {
        User currentUser = userService.getCurrentUser();
        return feed.getUser().getId().equals(currentUser.getId());
    }

    @Transactional(readOnly = true)
    public ResearchOpportunitiesFeed getById(final Integer feedId) {
        if (feedId == DEFAULT_SMALL_FEED_ID) {
            return getDefaultOpportunitiesFeed(userService.getCurrentUser(), FeedFormat.SMALL);
        } else if (feedId == DEFAULT_LARGE_FEED_ID) {
            return getDefaultOpportunitiesFeed(userService.getCurrentUser(), FeedFormat.LARGE);
        }
        return dao.getById(feedId);
    }

    @Transactional
    public ResearchOpportunitiesFeed updateFeed(final Integer feedId, final List<Integer> selectedProgramIds, final User currentUser,
            final FeedFormat format, final String title) {
        ResearchOpportunitiesFeed feed = dao.getById(feedId);
        Assert.isTrue(isOwner(feed));
        feed.setFeedFormat(format);
        feed.getPrograms().clear();
        for (Integer programId : selectedProgramIds) {
            feed.getPrograms().add((Program) programService.getById(programId));
        }
        feed.setTitle(title);
        return feed;
    }

}