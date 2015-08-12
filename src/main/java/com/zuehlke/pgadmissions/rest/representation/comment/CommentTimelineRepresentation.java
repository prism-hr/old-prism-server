package com.zuehlke.pgadmissions.rest.representation.comment;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class CommentTimelineRepresentation {

    private List<CommentGroupRepresentation> commentGroups = Lists.newLinkedList();

    public final List<CommentGroupRepresentation> getCommentGroups() {
        return Lists.reverse(commentGroups);
    }

    public final void setCommentGroups(List<CommentGroupRepresentation> commentGroups) {
        this.commentGroups = commentGroups;
    }

    public void addCommentGroup(CommentGroupRepresentation commentGroup) {
        commentGroups.add(commentGroup);
    }

    public static class CommentGroupRepresentation {

        private PrismStateGroup stateGroup;

        private List<CommentRepresentation> comments = Lists.newLinkedList();

        public final PrismStateGroup getStateGroup() {
            return stateGroup;
        }

        public final void setStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
        }

        public final List<CommentRepresentation> getComments() {
            return Lists.reverse(comments);
        }

        public final void setComments(List<CommentRepresentation> comments) {
            this.comments = comments;
        }

        public CommentGroupRepresentation withStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
            return this;
        }

        public CommentGroupRepresentation addComment(CommentRepresentation comment) {
            comments.add(comment);
            return this;
        }

    }

}
