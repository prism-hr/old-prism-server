package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;

public class AdvertTargetDTO {

    private Integer advertTargetId;

    private Integer otherInstitutionId;

    private String otherInstitutionName;

    private Integer otherInstitutionLogoImageId;

    private Integer otherDepartmentId;

    private String otherDepartmentName;

    private Integer otherUserId;

    private String otherUserFirstName;

    private String otherUserLastName;

    private String otherUserEmail;

    private String otherUserLinkedinProfileUrl;

    private String otherUserLinkedinImageUrl;

    private Integer otherUserPortraitImageId;

    private PrismPartnershipState partnershipState;
    
    private Boolean canAccept;

    public Integer getAdvertTargetId() {
        return advertTargetId;
    }

    public void setAdvertTargetId(Integer advertTargetId) {
        this.advertTargetId = advertTargetId;
    }

    public Integer getOtherInstitutionId() {
        return otherInstitutionId;
    }

    public void setOtherInstitutionId(Integer otherInstitutionId) {
        this.otherInstitutionId = otherInstitutionId;
    }

    public String getOtherInstitutionName() {
        return otherInstitutionName;
    }

    public void setOtherInstitutionName(String otherInstitutionName) {
        this.otherInstitutionName = otherInstitutionName;
    }

    public Integer getOtherInstitutionLogoImageId() {
        return otherInstitutionLogoImageId;
    }

    public void setOtherInstitutionLogoImageId(Integer otherInstitutionLogoImageId) {
        this.otherInstitutionLogoImageId = otherInstitutionLogoImageId;
    }

    public Integer getOtherDepartmentId() {
        return otherDepartmentId;
    }

    public void setOtherDepartmentId(Integer otherDepartmentId) {
        this.otherDepartmentId = otherDepartmentId;
    }

    public String getOtherDepartmentName() {
        return otherDepartmentName;
    }

    public void setOtherDepartmentName(String otherDepartmentName) {
        this.otherDepartmentName = otherDepartmentName;
    }

    public Integer getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Integer otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherUserFirstName() {
        return otherUserFirstName;
    }

    public void setOtherUserFirstName(String otherUserFirstName) {
        this.otherUserFirstName = otherUserFirstName;
    }

    public String getOtherUserLastName() {
        return otherUserLastName;
    }

    public void setOtherUserLastName(String otherUserLastName) {
        this.otherUserLastName = otherUserLastName;
    }

    public String getOtherUserEmail() {
        return otherUserEmail;
    }

    public void setOtherUserEmail(String otherUserEmail) {
        this.otherUserEmail = otherUserEmail;
    }

    public String getOtherUserLinkedinProfileUrl() {
        return otherUserLinkedinProfileUrl;
    }

    public void setOtherUserLinkedinProfileUrl(String otherUserLinkedinProfileUrl) {
        this.otherUserLinkedinProfileUrl = otherUserLinkedinProfileUrl;
    }

    public String getOtherUserLinkedinImageUrl() {
        return otherUserLinkedinImageUrl;
    }

    public void setOtherUserLinkedinImageUrl(String otherUserLinkedinImageUrl) {
        this.otherUserLinkedinImageUrl = otherUserLinkedinImageUrl;
    }

    public Integer getOtherUserPortraitImageId() {
        return otherUserPortraitImageId;
    }

    public void setOtherUserPortraitImageId(Integer otherUserPortraitImageId) {
        this.otherUserPortraitImageId = otherUserPortraitImageId;
    }

    public PrismPartnershipState getPartnershipState() {
        return partnershipState;
    }

    public void setPartnershipState(PrismPartnershipState partnershipState) {
        this.partnershipState = partnershipState;
    }

    public Boolean getCanAccept() {
        return canAccept;
    }

    public void setCanAccept(Boolean canAccept) {
        this.canAccept = canAccept;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(advertTargetId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        AdvertTargetDTO other = (AdvertTargetDTO) object;
        return Objects.equal(advertTargetId, other.getAdvertTargetId());
    }

}
