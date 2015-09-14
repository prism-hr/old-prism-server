package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;

public class ApplicationReferenceDTO {

    private Integer id;

    private User user;

    private ResourceCreationDTO resource;

    private String phone;

    private String skype;

    private Comment comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public final User getUser() {
        return user;
    }

    public final void setUser(User user) {
        this.user = user;
    }

    public ResourceCreationDTO getResource() {
        return resource;
    }

    public void setResource(ResourceCreationDTO resource) {
        this.resource = resource;
    }

    public final String getPhone() {
        return phone;
    }

    public final void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public final Comment getComment() {
        return comment;
    }

    public final void setComment(Comment comment) {
        this.comment = comment;
    }

}
