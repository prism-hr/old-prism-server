package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;

public class OpportunityRequestCommentBuilder {

    private Integer id;
    private RegisteredUser author;
    private OpportunityRequestCommentType commentType;
    private String content;
    private Date createdTimestamp;

    public OpportunityRequestCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public OpportunityRequestCommentBuilder author(RegisteredUser author) {
        this.author = author;
        return this;
    }

    public OpportunityRequestCommentBuilder commentType(OpportunityRequestCommentType commentType) {
        this.commentType = commentType;
        return this;
    }

    public OpportunityRequestCommentBuilder content(String content) {
        this.content = content;
        return this;
    }

    public OpportunityRequestCommentBuilder createdTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public OpportunityRequestComment build() {
        OpportunityRequestComment comment = new OpportunityRequestComment();
        comment.setId(id);
        comment.setAuthor(author);
        comment.setCommentType(commentType);
        comment.setContent(content);
        comment.setCreatedTimestamp(createdTimestamp);
        return comment;
    }

    public static OpportunityRequestCommentBuilder aOpportunityRequestComment(RegisteredUser author) {
        return new OpportunityRequestCommentBuilder().author(author).commentType(OpportunityRequestCommentType.APPROVE).content("Approving!");
    }

}
