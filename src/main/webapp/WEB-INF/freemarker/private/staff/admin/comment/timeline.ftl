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
	        <#if timelineObject.type != 'reference' ||  user.hasStaffRightsOnApplicationForm(applicationForm) || user == applicationForm.applicant || (timelineObject.referee?? && timelineObject.referee.user == user)>      
		        <li class="${timelineObject.type}">
		          <div class="box">
		            <div class="title">
		              <span class="icon-role <#if timelineObject.userCapacity == 'admin'>administrator<#else>${timelineObject.userCapacity}</#if>" data-desc="<#if timelineObject.userCapacity == 'admin'>Administrator<#else>${timelineObject.userCapacity?cap_first}</#if>"></span>
		              <span class="name">${(timelineObject.author.firstName?html)!} ${(timelineObject.author.lastName?html)!}</span>
		              <span class="datetime">${timelineObject.eventDate?string('dd MMM yy')} at ${timelineObject.eventDate?string('HH:mm')}</span>
		            </div>
		      
		            <p class="highlight"><@spring.message '${timelineObject.messageCode}'/>.</p>  
							</div>
		        
							<#if timelineObject.reviewRound?? && user != applicationForm.applicant  && user.hasStaffRightsOnApplicationForm(applicationForm)>
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
											<span class="icon-role <#if timelineObject.userCapacity == 'admin'>administrator<#else>${timelineObject.userCapacity}</#if>" data-desc="<#if timelineObject.userCapacity == 'admin'>Administrator<#else>${timelineObject.userCapacity?cap_first}</#if>"></span>
											<span class="name">${(timelineObject.author.firstName?html)!} ${(timelineObject.author.lastName?html)!}</span>
											<span class="datetime">${timelineObject.eventDate?string('dd MMM yy')} at ${timelineObject.eventDate?string('HH:mm')}</span>
										</div>
										<p><em>${timelineObject.interview.furtherDetails?html}</em></p>                  
										<p class="datetime"><span data-desc="Date and Time"></span> ${timelineObject.interview.interviewDueDate?string('dd MMM yy')} at ${timelineObject.interview.interviewTime}</p>
										<#if timelineObject.interview.locationURL?length &gt; 0><p class="location"><span data-desc="Location"></span> ${timelineObject.interview.locationURL?html}</p></#if>
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
											${supervisor.user.firstName?html} ${supervisor.user.lastName?html}<#if (index_i &lt; (size_users - 1))>, </#if>
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
											<span class="icon-role <#if timelineObject.userCapacity == 'admin'>administrator<#else>${timelineObject.userCapacity}</#if>" data-desc="<#if timelineObject.userCapacity == 'admin'>Administrator<#else>${timelineObject.userCapacity?cap_first}</#if>"></span>
											<span class="name">${(timelineObject.author.firstName?html)!} ${(timelineObject.author.lastName?html)!}</span>
										</div>
										<p>
											<strong>Reason for rejection</strong><br />
											<em>${applicationForm.rejection.rejectionReason.text?html}</em>
										</p>
									</div>
								</li>
							</ul>
		        
							</#if>                       
		          
		          <#if timelineObject.comments??>
		          <ul>
		            <#list timelineObject.comments as comment>
			            <#if comment.type == 'GENERIC' || comment.type == 'VALIDATION' ||  comment.type == 'REVIEW_EVALUATION' ||  comment.type == 'INTERVIEW_EVALUATION'>                                                    
			           		<#assign role = "administrator"/>     
			            <#elseif comment.type == 'REVIEW'>
			            	<#assign role = "reviewer"/>
			            <#elseif comment.type == 'INTERVIEW'>
			            	<#assign role = "interviewer"/>    
			            <#elseif comment.type == 'APPROVAL'>
			            	<#assign role = "approver"/>                
			            <#elseif comment.type == 'APPROVAL_EVALUATION'  || comment.type == 'REQUEST_RESTART'>
			            	<#assign role = "approver"/>                
			            </#if>
			            <li>                          
			              <div class="box">
			                <div class="title">
			                  <span class="icon-role ${role}" data-desc="${role?cap_first}"></span>
			                  <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span>
			                  <span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
			                </div>
			                <#if comment.type == 'REQUEST_RESTART'>
												<p><em>${(comment.comment?html)!}</em></p>
												<p class="restart"><span></span><em>Requested restart of approval phase.</em></p>
			                <#elseif comment.comment?starts_with("Referred to admissions")>
												<p class="referral"><span></span><em>${(comment.comment?html)!}</em></p>
											<#elseif comment.comment?length &gt; 0>
												<p><em>${(comment.comment?html)!}</em></p>
											</#if>
							<#if comment.documents?? && comment.documents?size &gt; 0>
				                <ul class="uploads">                
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
		          <#elseif timelineObject.referee?? && user != applicationForm.applicant && ( user.hasStaffRightsOnApplicationForm(applicationForm) || timelineObject.referee.user == user)> 
		            	<#include "timeline_snippets/reference_comment.ftl"/>	         
		            	
		          </#if> 
		        </li>
	        </#if>
        </#list>
      </ul>
    
    </div>           
  </div>
</section>