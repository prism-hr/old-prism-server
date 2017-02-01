package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.UserDescriptor;
import uk.co.alumeni.prism.rest.UserDescriptorExtended;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class UserRoleDTO extends UserDescriptorExtended<Integer, Integer> {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String firstName2;

    private String firstName3;

    private String fullName;

    private Boolean enabled;

    private String linkedinProfileUrl;

    private String linkedinImageUrl;

    private Integer portraitImage;

    private Integer creatorUser;

    private PrismRole role;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getFirstName2() {
        return firstName2;
    }

    @Override
    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    @Override
    public String getFirstName3() {
        return firstName3;
    }

    @Override
    public void setFirstName3(String firstName3) {
        this.firstName3 = firstName3;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getLinkedinProfileUrl() {
        return linkedinProfileUrl;
    }

    @Override
    public void setLinkedinProfileUrl(String linkedinProfileUrl) {
        this.linkedinProfileUrl = linkedinProfileUrl;
    }

    @Override
    public String getLinkedinImageUrl() {
        return linkedinImageUrl;
    }

    @Override
    public void setLinkedinImageUrl(String linkedinImageUrl) {
        this.linkedinImageUrl = linkedinImageUrl;
    }

    @Override
    public Integer getPortraitImage() {
        return portraitImage;
    }

    @Override
    public void setPortraitImage(Integer portraitImage) {
        this.portraitImage = portraitImage;
    }

    @Override
    public Integer getCreatorUser() {
        return creatorUser;
    }

    @Override
    public void setCreatorUser(Integer creatorUser) {
        this.creatorUser = creatorUser;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }

    public UserRoleDTO withId(Integer id) {
        this.id = id;
        return this;
    }

    public UserRoleDTO withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserRoleDTO withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserRoleDTO withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserRoleDTO withFirstName2(String firstName2) {
        this.firstName2 = firstName2;
        return this;
    }

    public UserRoleDTO withFirstName3(String firstName3) {
        this.firstName3 = firstName3;
        return this;
    }

    public UserRoleDTO withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public UserRoleDTO withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserRoleDTO withLinkedinProfileUrl(String linkedinProfileUrl) {
        this.linkedinProfileUrl = linkedinProfileUrl;
        return this;
    }

    public UserRoleDTO withLinkedinImageUrl(String linkedinImageUrl) {
        this.linkedinImageUrl = linkedinImageUrl;
        return this;
    }

    public UserRoleDTO withPortraitImage(Integer portraitImage) {
        this.portraitImage = portraitImage;
        return this;
    }

    public UserRoleDTO withCreatorUser(Integer creatorUser) {
        this.creatorUser = creatorUser;
        return this;
    }

    public UserRoleDTO withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    @Override
    public int compareTo(UserDescriptor other) {
        if (UserRoleDTO.class.isAssignableFrom(other.getClass())) {
            int compare = compare(role, ((UserRoleDTO) other).getRole());
            return compare == 0 ? super.compareTo(other) : compare;
        }
        return super.compareTo(other);
    }

}
