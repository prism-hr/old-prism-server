package com.zuehlke.pgadmissions.domain;

public interface InvitationEntity extends UniqueEntity {

    public Integer getId();

    public void setId(Integer id);

    public Invitation getInvitation();

    public void setInvitation(Invitation invitation);

}
