<#import "/spring.ftl" as spring />
<!-- Styles for Application List Page -->
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/timeline.css' />"/>


<section class="form-rows">
  <div>            
    <div class="row-group">
    
      <ul id="timeline-statuses">
      
        <#list timelineObjects as timelineObject>        
        <li class="${timelineObject.type}">
          <div class="box">
            <div class="title">
              <span class="icon-role ${timelineObject.userCapacity}" data-desc="${timelineObject.userCapacity?cap_first}"></span>
              <span class="name">${(timelineObject.author.firstName?html)!} ${(timelineObject.author.lastName?html)!}</span>
              <span class="datetime">${timelineObject.eventDate?string('dd MMM yy')} at ${timelineObject.eventDate?string('HH:mm')}</span>
            </div>
      
            <p class="highlight"><@spring.message '${timelineObject.messageCode}'/>.</p>  
					</div>
        
					<#if timelineObject.reviewRound?? && user != applicationForm.applicant>
					<#if timelineObject.reviewRound.reviewers?? && timelineObject.reviewRound.reviewers?size &gt; 0>
					<ul class="status-info">
						<li class="${timelineObject.type}">
							<div class="box">
								<h3>Invited reviewers:</h3>
								<ul>
									<#list timelineObject.reviewRound.reviewers as reviewer>
									<li>${reviewer.user.firstName?html} ${reviewer.user.lastName?html}</li>
									</#list>
								</ul>
							</div>
						</li>
					</ul>
					</#if>
        
					<#elseif timelineObject.interview??>
					<#if timelineObject.interview.interviewers??>
					<ul class="status-info">
						<li class="${timelineObject.type}">
							<div class="box">
								<h3>Interview</h3>
								<p>${timelineObject.interview.interviewDueDate?string('dd MMM yy')} at ${timelineObject.interview.interviewTime}</p>
								<p><em>${timelineObject.interview.furtherDetails?html}</em></p>                  
								<h3>Invited interviewers:</h3>
								<ul>
									<#list timelineObject.interview.interviewers as interviewer>
									<li>${interviewer.user.firstName?html} ${interviewer.user.lastName?html}</li>
									</#list>
								</ul>
							</div>
						</li>
					</ul>
					</#if>
           
					<#elseif timelineObject.approvalRound??>
					<#if timelineObject.approvalRound.supervisors?? && timelineObject.approvalRound.supervisors?size &gt; 0>
					<ul class="status-info">
						<li class="${timelineObject.type}">
							<div class="box">
								<h3>Selected supervisors:</h3>       
								<ul>
									<#list timelineObject.approvalRound.supervisors as supervisor>
									<li>${supervisor.user.firstName?html} ${supervisor.user.lastName?html}</li>
									</#list>
								</ul>
							</div>
						</li>
					</ul>
					</#if>
					
					<#elseif timelineObject.status?? &&   timelineObject.status == 'REJECTED'>
					<ul class="status-info">
						<li class="${timelineObject.type}">
							<div class="box">
								<h3>Reason:</h3>
								<p>${applicationForm.rejection.rejectionReason.text?html}</p>                 
							</div>
						</li>
					</ul>
        
					</#if>                       
          
          <#if timelineObject.comments??>
          <ul>
            <#list timelineObject.comments as comment>
            <#if comment.type == 'GENERIC' || comment.type == 'VALIDATION' ||  comment.type == 'REVIEW_EVALUATION' ||  comment.type == 'INTERVIEW_EVALUATION'>                                                    
            <#assign role = "admin"/>     
            <#elseif comment.type == 'REVIEW'>
            <#assign role = "reviewer"/>
            <#elseif comment.type == 'INTERVIEW'>
            <#assign role = "interviewer"/>    
            <#elseif comment.type == 'APPROVAL'>
            <#assign role = "approver"/>                
            </#if>
            <li>                          
              <div class="box">
                <div class="title">
                  <span class="icon-role ${role}" data-desc="${role?cap_first}"></span>
                  <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span>
                  <span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
                </div>
                <p><em>${(comment.comment?html)!}</em></p>
				<#if comment.documents?? && comment.documents?size &gt; 0>
	                <ul>                
	                <#list comment.documents as document>
	                	<li><a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a></li>
	                </#list>
	                </ul>
				</#if>
                <#if comment.type == 'VALIDATION'>                                                    
                <#include "timeline_snippets/validation_comment.ftl"/>
                <#elseif comment.type == 'REVIEW'>
                <#include "timeline_snippets/review_comment.ftl"/>
                <#elseif comment.type == 'INTERVIEW'>
                <#include "timeline_snippets/interview_comment.ftl"/>                  
                </#if>
              </div>
            </li>
            </#list>                       
          </ul>
          <#elseif timelineObject.referee?? && user != applicationForm.applicant>
            	<#include "timeline_snippets/reference_comment.ftl"/>	         
          </#if> 
        </li>
        </#list>
      </ul>
    
    </div>           
  </div>
</section>