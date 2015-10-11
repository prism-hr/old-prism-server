package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;

public class AdvertTargetDTO {

    private Integer id;

    private Integer thisInstitutionId;

    private String thisInstitutionName;

    private Integer thisLogoImageId;

    private Integer thisDepartmentId;

    private String thisDepartmentName;

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

    private Boolean canManage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getThisInstitutionId() {
        return thisInstitutionId;
    }

    public void setThisInstitutionId(Integer thisInstitutionId) {
        this.thisInstitutionId = thisInstitutionId;
    }

    public String getThisInstitutionName() {
        return thisInstitutionName;
    }

    public void setThisInstitutionName(String thisInstitutionName) {
        this.thisInstitutionName = thisInstitutionName;
    }

    public Integer getThisLogoImageId() {
        return thisLogoImageId;
    }

    public void setThisLogoImageId(Integer thisLogoImageId) {
        this.thisLogoImageId = thisLogoImageId;
    }

    public Integer getThisDepartmentId() {
        return thisDepartmentId;
    }

    public void setThisDepartmentId(Integer thisDepartmentId) {
        this.thisDepartmentId = thisDepartmentId;
    }

    public String getThisDepartmentName() {
        return thisDepartmentName;
    }

    public void setThisDepartmentName(String thisDepartmentName) {
        this.thisDepartmentName = thisDepartmentName;
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

    public Boolean getCanManage() {
        return canManage;
    }

    public void setCanManage(Boolean canManage) {
        this.canManage = canManage;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
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
        return Objects.equal(id, other.getId());
    }

}
