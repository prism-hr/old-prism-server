<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<!-- Styles for Application List Page -->
<!-- <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/> -->
<!-- <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/> -->
<!-- <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/timeline.css' />"/> -->
<section class="form-rows">
  <div>            
    <div class="row-group">
    
      <ul id="timeline-statuses">
        
        <#assign shownTargetForCompletingStage = false>
        <#list timelineObjects as timelineObject>  
	        <#if (timelineObject.type != 'reference' && timelineObject.type != 'confirmEligibility') || user.isInRole('ADMITTER') || user.hasStaffRightsOnApplicationForm(applicationForm) || user == applicationForm.applicant || (timelineObject.referee?? && timelineObject.referee.user == user)>      
		        <li class="${timelineObject.type}">
                <!-- Box start -->
		          <div class="box">
		            <div class="title">
		              <span class="icon-role 
		              <#if timelineObject.userCapacity == 'admin'>administrator
		              <#else>${timelineObject.userCapacity}</#if>" data-desc="${(timelineObject.getTooltipMessage()?html)!}"></span>
		              <#if timelineObject.type == 'reference' && timelineObject.referee?? && timelineObject.referee.reference?? && timelineObject.referee.reference.providedBy??>
		                  <#assign comment = timelineObject.referee.reference/>
                          <span class="name">${(comment.providedBy.firstName?html)!} ${(comment.providedBy.lastName?html)!} <em>on behalf of</em> ${(timelineObject.referee.user.firstName?html)!} ${(timelineObject.referee.user.lastName?html)!}</span>
	                  <#else>
		                  <span class="name">${(timelineObject.author.firstName?html)!} ${(timelineObject.author.lastName?html)!}</span>
	                  </#if>
		              <span class="datetime">${timelineObject.eventDate?string('dd MMM yy')} at ${timelineObject.eventDate?string('HH:mm')}</span>
		            </div>
                    
		            <p class="highlight"><@spring.message '${timelineObject.messageCode}'/>.</p> <i class="icon-minus-sign"></i> 
				   </div>
		         <!-- Box end -->
                 <div class="excontainer">
							<#if timelineObject.reviewRound?? && (user.hasStaffRightsOnApplicationForm(applicationForm) || user.isInRole('ADMITTER'))>
							<#if timelineObject.reviewRound.reviewers?? && timelineObject.reviewRound.reviewers?size &gt; 0>
							<ul class="status-info">
								<li class="${timelineObject.type}">
									<div class="box">
										<p class="added">
											<#assign size_users = timelineObject.reviewRound.reviewers?size>
											<#list timelineObject.reviewRound.reviewers as reviewer>
											<#assign index_i = reviewer_index>
											${reviewer.user.firstName?html} ${reviewer.user.lastName?html}<#if (index_i &lt; (size_users - 1))>, </#if>
											</#list>
											added as reviewer<#if size_users &gt; 1>s</#if>.
										</p>
									</div>
								</li>
							</ul>
							</#if>
		        
							<#elseif timelineObject.interview??>
							<#if timelineObject.interview.interviewers??>
							<ul class="status-info">
								<li class="${timelineObject.type}">
									<div class="box">
										<p class="added">
											<#assign size_users = timelineObject.interview.interviewers?size>
											<#list timelineObject.interview.interviewers as interviewer>
											<#assign index_i = interviewer_index>
											${interviewer.user.firstName?html} ${interviewer.user.lastName?html}<#if (index_i &lt; (size_users - 1))>, </#if>
											</#list>
											added as interviewer<#if size_users &gt; 1>s</#if>.
										</p>
									</div>
								</li>
								<li class="${timelineObject.type}">
									<div class="box">
										<div class="title">
											<span class="icon-role <#if timelineObject.userCapacity == 'admin'>administrator<#else>${timelineObject.userCapacity}</#if>" data-desc="${(timelineObject.getTooltipMessage()?html)!}"></span>
											<span class="name">${(timelineObject.author.firstName?html)!} ${(timelineObject.author.lastName?html)!}</span> <span class="commented">commented:</span>
											<span class="datetime">${timelineObject.eventDate?string('dd MMM yy')} at ${timelineObject.eventDate?string('HH:mm')}</span>
										</div>
										<p class="datetime">
										  <span></span>
										  <#if timelineObject.interview.stage == 'SCHEDULED'>
                                          	<b>Date and time of interview: </b>
  										  	${timelineObject.interview.interviewDueDate?string('dd MMM yy')}
  										  	at ${timelineObject.interview.interviewTime}
  										  	(${timelineObject.interview.timeZone.getDisplayName(false, 1)}).
										  <#else>
										    Scheduling in progress.
										  </#if>
									  </p>
									</div>
								</li>
							</ul>
							</#if>
		           
							<#elseif timelineObject.approvalRound??>
							<#if timelineObject.approvalRound.supervisors?? && timelineObject.approvalRound.supervisors?size &gt; 0>
                                <ul class="status-info">
                                    <li class="${timelineObject.type}">
                                        <div class="box">
                                            <p class="added">
                                                <#assign size_users = timelineObject.approvalRound.supervisors?size>
                                                <#list timelineObject.approvalRound.supervisors as supervisor>
                                                  <#assign index_i = supervisor_index>
                                                  ${supervisor.user.firstName?html} ${supervisor.user.lastName?html}
                                                  <#if supervisor.isPrimary> (Primary)</#if>
                                                  <#if (index_i &lt; (size_users - 1))>, </#if>
                                                </#list>
                                                added as supervisor<#if size_users &gt; 1>s</#if>.
                                            </p>
                                        </div>
                                    </li>
                                </ul>
							</#if>
							
							<#elseif timelineObject.status?? && timelineObject.status == 'REJECTED'>
							<ul class="status-info">
								<li class="${timelineObject.type}">
									<div class="box">
										<div class="title">
											<span class="icon-role <#if timelineObject.userCapacity == 'admin'>administrator<#else>${timelineObject.userCapacity}</#if>" data-desc="${(timelineObject.getTooltipMessage()?html)!}"></span>
											<span class="name">${(timelineObject.author.firstName?html)!} ${(timelineObject.author.lastName?html)!}</span> <span class="commented">commented:</span>
										</div>
										<p class="rejection">
											<i class="icon-puzzle-piece"></i>
											<em>${applicationForm.rejection.rejectionReason.text?html}</em>
										</p>
									</div>
								</li>
							</ul>
		        
							</#if> 
							
		          <#if !shownTargetForCompletingStage && 
                  		(timelineObject.type != 'reference' && timelineObject.type != 'confirmEligibility')>
		            <#if timelineObject.type == 'validation' || timelineObject.type == 'review'  || timelineObject.type == 'interview' || timelineObject.type == 'approval'> 
                  <ul class="status-info">
                    <li class="${timelineObject.type}">
                      <div class="box">
                        <p class="target">
                        	<span></span><b>Our target date for completing this stage:</b> ${applicationForm.dueDate?string('dd MMM yy')}.
                        </p>
                      </div>
                    </li>
                  </ul>
                </#if>
                <#assign shownTargetForCompletingStage = true>
              </#if>							          
		          <#if timelineObject.comments?? || timelineObject.projectTitle??>
		          <ul>
		          	<#if timelineObject.projectTitle??>
			         <li>                          
  			           <div class="box">
						 <p class="project_title"><span data-desc="Project title and description" aria-describedby="ui-tooltip-2"></span>
                		   <b>${timelineObject.projectTitle}</b><br>
            			   <i>${timelineObject.projectDescription}</i>
            			 </p>
		              </div>
  			         </li>
		          	</#if>
		          
		            <#list timelineObject.comments as comment>
			            <#if comment.type == 'GENERIC' ||  comment.type == 'REVIEW_EVALUATION' ||  comment.type == 'INTERVIEW_EVALUATION' || comment.type == 'VALIDATION' || comment.type == 'REVIEW_EVALUATION' ||  comment.type == 'INTERVIEW_EVALUATION' || comment.type == 'INTERVIEW_VOTE' || comment.type == 'INTERVIEW_SCHEDULE'>                                                    
			           		<#if comment.user.isProgrammeAdministrator(comment.application)>
			           			<#assign role = "administrator"/>     
			           		<#elseif comment.user.isInRole('SUPERADMINISTRATOR')>
                                <#assign role = "administrator"/>
			           		<#elseif comment.user.isInterviewerOfApplicationForm(comment.application)>
			           		    <#assign role = "interviewer"/> 
			           		<#elseif comment.user.id == applicationForm.applicant.id>
			           		    <#assign role = "applicant"/> 
			           		<#else>
			           			<#assign role = "viewer"/>
			           		</#if>
			            <#elseif comment.type == 'REVIEW'>
			            	<#assign role = "reviewer"/>
			            <#elseif comment.type == 'INTERVIEW'>
			            	<#assign role = "interviewer"/>    
			            <#elseif comment.type == 'APPROVAL'>
			            	<#assign role = "administrator"/>                
			            <#elseif comment.type == 'APPROVAL_EVALUATION'  || comment.type == 'REQUEST_RESTART'>
			            	<#assign role = "approver"/>
		            	<#elseif comment.type = 'SUPERVISION_CONFIRMATION'>
                    <#assign role = "supervisor"/>                                
                  <#elseif comment.type == 'STATE_CHANGE_SUGGESTION'>
                    <#assign role = "interviewer"/> 
			            </#if>
			            
			            <#if comment.type == 'SUPERVISION_CONFIRMATION'>
		                <#include "timeline_snippets/supervision_confirmation_comment.ftl"/>
			            <#elseif comment.type == 'APPROVAL'>
		                <#include "timeline_snippets/approval_comment.ftl"/>
                  <#elseif comment.type == 'INTERVIEW_SCHEDULE'>
                    <#include "timeline_snippets/interview_schedule_comment.ftl"/>
	                <#else>
                    
			         <li>                          
  			              <div class="box">
  			                <div class="title">
  			                  <span class="icon-role ${role}" data-desc="${(comment.getTooltipMessage(role)?html)!}"></span>
  			                  <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span> <span class="commented">commented:</span>
  			                  <span class="datetime" data-desc="Date">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
  			                </div>
                            <!-- Request restart -->
  			                <#if comment.type == 'REQUEST_RESTART'>
                                <div class="textContainer"><p><em>${(comment.comment?html?replace("\n", "<br>"))!}</em></p></div>
                                <p class="restart"><span/><b>Restart of approval stage requested.</b></p>
            <#elseif comment.comment?starts_with("Referred to")>
                                <p class="referral"><span data-desc="Referral"></span><em>${(comment.comment?html?replace("\n", "<br>"))!}</em></p>
            <#elseif comment.comment?starts_with("Delegated Application")>
                                <p class="delegate"><span></span><em>${(comment.comment?html?replace("\n", "<br>"))!}</em>.</p>
                            <!-- General comment box-->	
          				    <#elseif comment.comment?trim?length &gt; 0>
                                <div class="textContainer">
                                	<p><em>${(comment.comment?html?replace("\n", "<br>"))!}</em></p>
                                </div>
                            </#if>
  							<!-- General comment box file-->			
                            <#if comment.documents?? && comment.documents?size &gt; 0>
                                <ul class="uploads">                
                                <#list comment.documents as document>
                                    <li><a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a></li>
                                </#list>
                                </ul>
                            </#if> 
                            
                            <!-- To move to validation questions-->	
  			                <#if comment.type == 'VALIDATION' || comment.type == 'ADMITTER_COMMENT'>
  			                	<#include "timeline_snippets/validation_comment.ftl"/>
  			                <#elseif comment.type == 'REVIEW'>
  			                	<#include "timeline_snippets/review_comment.ftl"/>
  			                <#elseif comment.type == 'INTERVIEW'>
  			                	<#include "timeline_snippets/interview_comment.ftl"/>
		                	  <#elseif comment.type == 'INTERVIEW_VOTE'>
			                		<#assign interviewVoteParticipant=comment.interviewParticipant>
			                		<#assign interviewVoteParticipantAsUser=interviewVoteParticipant.user>
			                		<#if interviewVoteParticipant.responded>
                                        <#if interviewVoteParticipant.acceptedTimeslots?has_content>
                                            <h3 class="answer yes"><span aria-describedby="ui-tooltip-150"></span>Confirmed interview preferences.</h3> 
                                        <#else>
                                            <h3 class="answer no"><span aria-describedby="ui-tooltip-150"/></span>Is unable to make interview.</h3>
                                        </#if>
		                		   </#if>
	                		  <#elseif comment.type == 'STATE_CHANGE_SUGGESTION'>
	                		    <h3><span class="icon-hand-right"></span>Recommends that the application be moved to the ${comment.nextStatus.displayValue()} stage.
                		      </h3>
  			                </#if>
  			              </div>
  			            </li>
  			           
    			            
			            </#if>
		            </#list>                       
		          </ul>
		          <#elseif timelineObject.referee?? && (user.hasStaffRightsOnApplicationForm(applicationForm) || timelineObject.referee.user == user)> 
		            	<#include "timeline_snippets/reference_comment.ftl"/>
		          <#elseif timelineObject.type == 'confirmEligibility' && (user.hasStaffRightsOnApplicationForm(applicationForm) ||  user.isInRole('ADMITTER'))>
		            	<#include "timeline_snippets/eligibility_comment.ftl"/>
		          </#if> 
                  </div>
		        </li>
	        </#if>
        </#list>
      </ul>
    
    </div>           
  </div>
</section>
<script type="text/javascript" src="<@spring.url '/design/default/js/expand.js' />"></script>