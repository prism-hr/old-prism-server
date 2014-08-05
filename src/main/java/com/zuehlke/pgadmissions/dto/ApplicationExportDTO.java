package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.User;

public class ApplicationExportDTO {

    private Application application;
    
    private String creatorExportId;
    
    private String creatorIpAddress;
    
    private Comment offerRecommendationComment;
    
    private User primarySupervisor;
    
    private ProgramInstance exportProgramInstance;
    
    private List<ApplicationReferee> exportReferees;

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

    public final ProgramInstance getExportProgramInstance() {
        return exportProgramInstance;
    }

    public final List<ApplicationReferee> getExportReferees() {
        return exportReferees;
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
    
    public ApplicationExportDTO withExportProgramInstance(ProgramInstance exportProgramInstance) {
        this.exportProgramInstance = exportProgramInstance;
        return this;
    }
    
    public ApplicationExportDTO withExportReferees(List<ApplicationReferee> exportReferees) {
        this.exportReferees = exportReferees;
        return this;
    }
    
}
