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
	                        <span class="icon-role ${timelineObject.userCapacity}"></span>
	                        <span class="name">${(timelineObject.author.firstName?html)!} ${(timelineObject.author.lastName?html)!}</span>
	                        <span class="datetime">${timelineObject.eventDate?string('dd MMM yy')} at ${timelineObject.eventDate?string('HH:mm')}</span>
	                      </div>
                   
                      	<p><@spring.message '${timelineObject.messageCode}'/>.</p>  
                   	
	                   	<#if timelineObject.reviewRound??>               
	                   		<h3>Invited reviewers:</h3>           			
	
	                   		 <#list  timelineObject.reviewRound.reviewers as reviewer>
	                   		 	<p>${reviewer.user.firstName?html} ${reviewer.user.lastName?html}</p>
	                   		 </#list>
	                   		 
	                   	<#elseif timelineObject.interview??>
	                   	 	<h3>Interview</h3>
	                   	 	<p>${timelineObject.interview.interviewDueDate?string('dd MMM yy')} at ${timelineObject.interview.interviewTime}</p>
	                   	 	<p>${timelineObject.interview.furtherDetails?html}</p>                  
	                   		<h3>Invited interviewers:</h3>           			
	
	                   		 <#list  timelineObject.interview.interviewers as interviewer>
	                   		 	<p>${interviewer.user.firstName?html} ${interviewer.user.lastName?html}</p>
	                   		 </#list>
	                   	 <#elseif timelineObject.approvalRound??>
	                   		 <h3>Selected supervisors:</h3>           			
	
	                   		 <#list  timelineObject.approvalRound.supervisors as supervisor>
	                   		 	<p>${supervisor.user.firstName?html} ${supervisor.user.lastName?html}</p>
	                   		 </#list>
	                   	<#elseif timelineObject.status?? &&   timelineObject.status == 'REJECTED'>
	                   		<h3>Reason:</h3>
	                   		<p>${applicationForm.rejection.rejectionReason.text?html}</p>           			
	
	                   	</#if>                   		
                    
                    </div>
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
	                            <span class="icon-role ${role}"></span>
	                              <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span>
	                        		<span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
	                          </div>
	                          <p>${(comment.comment?html)!}</p>
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
	                  <#elseif timelineObject.referee??>
	                  	<#if timelineObject.referee.declined>
	                  		<p>${timelineObject.referee.user.firstName?html} ${timelineObject.referee.user.lastName?html} declined to act as referee</p>
	                  	<#else>  
	                  		<p>${timelineObject.referee.user.firstName?html} ${timelineObject.referee.user.lastName?html} provided a reference: <a href="<@spring.url '/download/reference?referenceId=${encrypter.encrypt(timelineObject.referee.reference.id)}'/>">${timelineObject.referee.reference.document.fileName?html}</a></p>
	                  	</#if>     
	                  </#if> 
	                              	
	                </li>
	               </#list>
	             </ul>
              
              </div>           
            
            </div>
          </section>
        

      