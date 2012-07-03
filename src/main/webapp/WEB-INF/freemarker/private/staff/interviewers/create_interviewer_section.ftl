<#import "/spring.ftl" as spring />
<p><strong>Create New Interviewer</strong></p>									

									<div class="row">
										<label class="plain-label normal">Interviewer First Name<em>*</em></label> 
										<span class="hint" data-desc="<@spring.message 'assignInterviewer.firstName'/>"></span>
										<div class="field">
											<input class="full" type="text" name="newInterviewerFirstName" id="newInterviewerFirstName"  value="${(interviewer.firstName?html)!}"/>
											<@spring.bind "interviewer.firstName" /> 
											<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
										</div>
									</div>
									
									<div class="row">
										<label class="plain-label normal">Interviewer Last Name<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'assignInterviewer.lastName'/>"></span>
										<div class="field">
											<input class="full" type="text" name="newInterviewerLastName" id="newInterviewerLastName" value="${(interviewer.lastName?html)!}"/>			                                      
											<@spring.bind "interviewer.lastName" /> 
											<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
									</div>
		
									<div class="row">
										<label class="plain-label normal">Interviewer Email Address<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'assignInterviewer.email'/>"></span>
										<div class="field">
											<input class="full" type="text"  name="newInterviewerEmail" id="newInterviewerEmail" value="${(interviewer.email?html)!}"/>			                                         
											<@spring.bind "interviewer.email" /> 
											<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
									</div>
	
									<div class="row">
										<div class="field">
											<button class="blue" type="button" id="createInterviewer">Create interviewer</button>
										</div>
									</div>