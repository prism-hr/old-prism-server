package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.user.User;

public class ApplicationExportDTO {

    private Application application;
    
    private String creatorExportId;
    
    private String creatorIpAddress;
    
    private Comment offerRecommendationComment;
    
    private User primarySupervisor;
    
    private ProgramStudyOptionInstance exportProgramInstance;
    
    private List<ApplicationReferee> applicationReferees;

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

    public final ProgramStudyOptionInstance getExportProgramInstance() {
        return exportProgramInstance;
    }
    
    public final List<ApplicationReferee> getApplicationReferees() {
        return applicationReferees;
    }

    public final void setApplicationReferees(List<ApplicationReferee> applicationReferees) {
        this.applicationReferees = applicationReferees;
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
    
    public ApplicationExportDTO withExportProgramInstance(ProgramStudyOptionInstance exportProgramInstance) {
        this.exportProgramInstance = exportProgramInstance;
        return this;
    }
    
    public ApplicationExportDTO withApplicationReferees(List<ApplicationReferee> applicationReferees) {
        this.applicationReferees = applicationReferees;
        return this;
    }
    
}
