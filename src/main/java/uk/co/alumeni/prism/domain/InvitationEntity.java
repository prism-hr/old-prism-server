package uk.co.alumeni.prism.domain;

public interface InvitationEntity extends UniqueEntity {

    public Integer getId();

    public void setId(Integer id);

    public Invitation getInvitation();

    public void setInvitation(Invitation invitation);

}
