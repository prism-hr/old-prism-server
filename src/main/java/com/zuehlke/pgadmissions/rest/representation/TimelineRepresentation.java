package com.zuehlke.pgadmissions.rest.representation;

import java.util.Set;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;

public class TimelineRepresentation {

    private Set<TimelineCommentGroupRepresentation> commentGroups = Sets.newLinkedHashSet();

    public final Set<TimelineCommentGroupRepresentation> getCommentGroups() {
        return commentGroups;
    }

    public final void setCommentGroups(Set<TimelineCommentGroupRepresentation> commentGroups) {
        this.commentGroups = commentGroups;
    }

    public void addCommentGroup(TimelineCommentGroupRepresentation commentGroup) {
        commentGroups.add(commentGroup);
    }

    public static class TimelineCommentGroupRepresentation {

        private PrismStateGroup stateGroup;

        private Set<CommentRepresentation> comments = Sets.newLinkedHashSet();

        public final PrismStateGroup getStateGroup() {
            return stateGroup;
        }

        public final void setStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
        }

        public final Set<CommentRepresentation> getComments() {
            return comments;
        }

        public final void setComments(Set<CommentRepresentation> comments) {
            this.comments = comments;
        }

        public TimelineCommentGroupRepresentation withStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
            return this;
        }

        public TimelineCommentGroupRepresentation addComment(CommentRepresentation comment) {
            comments.add(comment);
            return this;
        }

    }

}
