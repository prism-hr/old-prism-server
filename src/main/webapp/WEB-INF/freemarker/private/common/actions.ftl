<#import "/spring.ftl" as spring />
<#if application??>
    <#assign application = application>
<#elseif applicationForm??>
    <#assign application = applicationForm>
</#if>
    <select id="actionTypeSelect" class="actionType" name="app_[${application.applicationNumber}]">
        <option>Actions</option> 
        <option value="view">View<#if application.isUserAllowedToSeeAndEditAsAdministrator(user) || (user == application.applicant && application.isModifiable())> / Edit</#if></option>

        <#if user.hasAdminRightsOnApplication(application) && application.isInState('VALIDATION')> 
            <option value="validate">Validate</option>
        </#if>
        <#if user.hasAdminRightsOnApplication(application) && application.isInState('REVIEW')> 
            <option value="validate">Evaluate reviews</option>
        </#if>
        <#if user.hasAdminRightsOnApplication(application) && application.isInState('INTERVIEW')> 
            <option value="validate">Evaluate interview feedback</option>
        </#if>
        <#if user.hasAdminRightsOnApplication(application) || user.isViewerOfProgramme(application)>
            <option value="comment">Comment</option>                                                    
        </#if>
        <#if (user.isReviewerInLatestReviewRoundOfApplicationForm(application) && application.isInState('REVIEW') && !user.hasRespondedToProvideReviewForApplicationLatestRound(application))> 
            <option value="review">Add Review</option>                                                    
        </#if>
        <#if user.isInterviewerOfApplicationForm(application) && application.isInState('INTERVIEW') && !user.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application)> 
            <option value="interviewFeedback">Add Interview Feedback</option>                                                    
        </#if>
        <#if (user.isRefereeOfApplicationForm(application) && application.isSubmitted() && application.isModifiable() && !user.getRefereeForApplicationForm(application).hasResponded()  )>
            <option value="reference">Add Reference</option>
        </#if>
        <#if (user == application.applicant && !application.isDecided() && !application.isWithdrawn())>
            <option value="withdraw">Withdraw</option>
        </#if>
        <#if (user.hasAdminRightsOnApplication(application) && application.isPendingApprovalRestart())>
            <option value="restartApproval">Approve</option>
        </#if>
        <#if user.isInRoleInProgram('APPROVER', application.program) && application.isInState('APPROVAL') && !application.isPendingApprovalRestart() && application.getLatestApprovalRound().getPrimarySupervisor()??>
            <#assign primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor()>
            <#if primarySupervisor?? && primarySupervisor.confirmedSupervision?? && primarySupervisor.confirmedSupervision>
                <option value="validate">Approve</option>
            </#if>
        </#if>
        <#if application.isInState('APPROVAL') && application.getLatestApprovalRound().getPrimarySupervisor()??>
            <#assign primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor()>
            <#if primarySupervisor?? && user == primarySupervisor.getUser() && !primarySupervisor.confirmedSupervision??> 
                <option value="confirmSupervision">Confirm Supervision</option>
            </#if>
        </#if>
</select>
