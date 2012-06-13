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
	                        <span class="icon-role"></span>
	                        <span class="name">${(timelineObject.author.firstName?html)!} ${(timelineObject.author.lastName?html)!}</span>
	                        <span class="datetime">${timelineObject.date?string('dd MMM yy')} at ${timelineObject.date?string('HH:mm')}</span>
	                      </div>
                   
                      	<p><@spring.message '${timelineObject.messageCode}'/>.</p>  
                   	
	                   	<#if timelineObject.reviewRound??>               
	                   		<h3>Invited reviewers:</h3>           			
	
	                   		 <#list  timelineObject.reviewRound.reviewers as reviewer>
	                   		 	<p>${reviewer.user.firstName} ${reviewer.user.lastName}</p>
	                   		 </#list>
	                   	<#elseif timelineObject.interview??>
	                   	 	<h3>Interview</h3>
	                   	 	<p>${timelineObject.interview.interviewDueDate?string('dd MMM yy')} at ${timelineObject.interview.interviewTime}</p>
	                   	 	<p>${timelineObject.interview.furtherDetails?html}</p>                  
	                   		<h3>Invited interviewers:</h3>           			
	
	                   		 <#list  timelineObject.interview.interviewers as interviewer>
	                   		 	<p>${interviewer.user.firstName} ${interviewer.user.lastName}</p>
	                   		 </#list>
	                   	</#if>                   		
                    
                    </div>
                    
                    <ul>
                    <#list timelineObject.comments as comment>     
                      <li>                      	  
                        <div class="box">
                          <div class="title">
                            <span class="icon-role"></span>
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
                  </li>
                  </#list>
                </ul>
              
              </div>           
            
            </div>
          </section>
        

      