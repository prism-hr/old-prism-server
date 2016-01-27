package uk.co.alumeni.prism.rest.representation.comment;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;

import com.google.common.collect.Lists;

public class CommentTimelineRepresentation {

    private List<CommentGroupRepresentation> commentGroups = Lists.newLinkedList();

    public List<CommentGroupRepresentation> getCommentGroups() {
        return Lists.reverse(commentGroups);
    }

    public void setCommentGroups(List<CommentGroupRepresentation> commentGroups) {
        this.commentGroups = commentGroups;
    }

    public void addCommentGroup(CommentGroupRepresentation commentGroup) {
        commentGroups.add(commentGroup);
    }

    public static class CommentGroupRepresentation {

        private PrismStateGroup stateGroup;

        private List<CommentRepresentation> comments = Lists.newLinkedList();

        public PrismStateGroup getStateGroup() {
            return stateGroup;
        }

        public void setStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
        }

        public List<CommentRepresentation> getComments() {
            return Lists.reverse(comments);
        }

        public void setComments(List<CommentRepresentation> comments) {
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
