package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;

public class ApplicationExportDTO {

    private Application application;
    
    private String creatorExportId;
    
    private String creatorIpAddress;
    
    private Comment offerRecommendationComment;
    
    private User primarySupervisor;
    
    private ResourceStudyOptionInstance exportProgramInstance;
    
    private List<ApplicationReferenceDTO> applicationReferences;

    public final Application getApplication() {
        return application;
    }

    public final String getCreatorExportId() {
        return creatorExportId;
    }

    public final String getCreatorIpAddress() {
        return creatorIpAddress;
    }

    public final Comment getOfferRecommendationComment() {
        return offerRecommendationComment;
    }

    public final User getPrimarySupervisor() {
        return primarySupervisor;
    }

    public final ResourceStudyOptionInstance getExportProgramInstance() {
        return exportProgramInstance;
    }

    public final List<ApplicationReferenceDTO> getApplicationReferences() {
        return applicationReferences;
    }

    public final void setApplicationReferences(List<ApplicationReferenceDTO> applicationReferences) {
        this.applicationReferences = applicationReferences;
    }

    public final void setApplication(Application application) {
        this.application = application;
    }

    public final void setCreatorExportId(String creatorExportId) {
        this.creatorExportId = creatorExportId;
    }

    public final void setCreatorIpAddress(String creatorIpAddress) {
        this.creatorIpAddress = creatorIpAddress;
    }

    public final void setOfferRecommendationComment(Comment offerRecommendationComment) {
        this.offerRecommendationComment = offerRecommendationComment;
    }
    
    public final void setPrimarySupervisor(User primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
    }

    public final void setExportProgramInstance(ResourceStudyOptionInstance exportProgramInstance) {
        this.exportProgramInstance = exportProgramInstance;
    }

    public ApplicationExportDTO withApplication(Application application) {
        this.application = application;
        return this;
    }
    
    public ApplicationExportDTO withCreatorExportId(String creatorExportId) {
        this.creatorExportId = creatorExportId;
        return this;
    }
    
    public ApplicationExportDTO withCreatorIpAddress(String creatorIpAddress) {
        this.creatorIpAddress = creatorIpAddress;
        return this;
    }
    
    public ApplicationExportDTO withOfferRecommendationComment(Comment offerRecommendationComment) {
        this.offerRecommendationComment = offerRecommendationComment;
        return this;
    }
    
    public ApplicationExportDTO withPrimarySupervisor(User primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
        return this;
    }
    
    public ApplicationExportDTO withExportProgramInstance(ResourceStudyOptionInstance exportProgramInstance) {
        this.exportProgramInstance = exportProgramInstance;
        return this;
    }
    
    public ApplicationExportDTO withApplicationReferences(List<ApplicationReferenceDTO> applicationReferences) {
        this.applicationReferences = applicationReferences;
        return this;
    }
    
}
