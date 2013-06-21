package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ResearchOpportunitiesFeedDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;

@Service
public class ResearchOpportunitiesFeedService {

    private final UserService userService;
    
    private final ResearchOpportunitiesFeedDAO dao;
    
    private final ProgramDAO programDAO;
    
    private static final String LARGE_IFRAME = ""
            + "<html> "
            + "<body> "
            + "<iframe src=\"${host}/pgadmissions/adverts/standaloneAdverts?feed=${id}\" " 
            + "width=\"430\" " 
            + "height=\"514\" " 
            + "style=\"border:none;\"> "
            + "</body> "
            + "</html> ";
    
    private static final String SMALL_IFRAME = ""
            + "<html> "
            + "<body> "
            + "<iframe src=\"${host}/pgadmissions/adverts/standaloneAdverts?feed=${id}\" " 
            + "width=\"210\" " 
            + "height=\"514\" " 
            + "style=\"border:none;\"> "
            + "</body> "
            + "</html> ";
    
    private final String host;
    
    public ResearchOpportunitiesFeedService() {
        this(null, null, null, null);
    }
    
    @Autowired
    public ResearchOpportunitiesFeedService(final ResearchOpportunitiesFeedDAO dao, final ProgramDAO programDAO, final UserService userService, @Value("${application.host}") final String host) {
        this.dao = dao;
        this.programDAO = programDAO;
        this.host = host;
        this.userService = userService;
    }
    
    public String getIframeHtmlCode(final ResearchOpportunitiesFeed feed) {
        switch (feed.getFeedFormat()) {
        case SMALL:
            return StringUtils.replace(StringUtils.replace(SMALL_IFRAME, "${host}", host), "${id}", String.valueOf(feed.getId()));
        default:
        case LARGE:
            return StringUtils.replace(StringUtils.replace(LARGE_IFRAME, "${host}", host), "${id}", String.valueOf(feed.getId()));
        }
    }

    @Transactional
    public ResearchOpportunitiesFeed saveNewFeed(final List<Integer> selectedProgramIds, final RegisteredUser user,
            final FeedFormat format, final String title) {
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeed();
        for (Integer programId : selectedProgramIds) {
            feed.getPrograms().add(programDAO.getProgramById(programId));
        }
        feed.setFeedFormat(format);
        feed.setTitle(title);
        feed.setUser(user);
        dao.save(feed);
        return feed;
    }
    
    @Transactional(readOnly = true)
    public List<ResearchOpportunitiesFeed> getAllFeedsForUser(final RegisteredUser user) {
        return dao.getAllFeedsForUser(user);
    }
    
    @Transactional(readOnly = true)
    public boolean isUniqueFeedTitleForUser(final String title, final RegisteredUser user) {
        return dao.isUniqueFeedTitleForUser(title, user);
    }

    @Transactional
    public void deleteById(final Integer feedId) {
        Assert.isTrue(isOwner(feedId));
        dao.deleteById(feedId);
    }
    
    private boolean isOwner(final Integer feedId) {
        RegisteredUser currentUser = userService.getCurrentUser();
        return dao.getById(feedId).getUser().getId().equals(currentUser.getId());
    }

    @Transactional(readOnly = true)
    public ResearchOpportunitiesFeed getById(final Integer feedId) {
        Assert.isTrue(isOwner(feedId));
        return dao.getById(feedId);
    }

    @Transactional
    public ResearchOpportunitiesFeed updateFeed(final Integer feedId, final List<Integer> selectedProgramIds,
            final RegisteredUser currentUser, final FeedFormat format, final String title) {
        Assert.isTrue(isOwner(feedId));
        ResearchOpportunitiesFeed feed = dao.getById(feedId);
        feed.setFeedFormat(format);
        feed.getPrograms().clear();
        for (Integer programId : selectedProgramIds) {
            feed.getPrograms().add(programDAO.getProgramById(programId));
        }
        feed.setTitle(title);
        return feed;
    }
}