<#if applicationForm.project?has_content>
	<#assign hasProject = true>
<#else>
	<#assign hasProject = false>
</#if>

<#if programme?has_content>
	<#assign hasProgramme = true>
<#else>
	<#assign hasProgramme = false>
</#if>

<#-- Assignments -->
<#import "/spring.ftl" as spring />

<#-- Programme Details Rendering -->


	<h2 id="programme-H2" class="tick"> 
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Programme
	</h2>
	
	<div>
    	<form>
                
			<div>
            	
            	<!-- Programme name (disabled) -->
                <div class="row">
                	<label class="label">Programme</label>
                  
                    <div class="field">
                    	${applicationForm.project.program.title?html}
                    </div>
				</div>
                  
				<!-- Study option -->
                <div class="row">
                    <label class="label">Study Option</label>

                    <div class="field">
                    <#if applicationForm.programmeDetails?? && applicationForm.programmeDetails.studyOption??>
                    	${applicationForm.programmeDetails.studyOption.freeVal}
                    </#if>
                    </div>
				</div>

				<!-- Project -->
				<div class="row">
                    <label class="label">Project</label>

                    <div class="field">
                		${applicationForm.project.title?html}
                    </div>
				</div>
				
				<!-- Start date -->
                <div class="row">
                    <label class="label">Start Date</label>
                   <div class="field">
                    ${(applicationForm.programmeDetails.startDate?string('dd-MMM-yyyy'))!}</div>
                </div>

                <!-- Referrer -->
                <div class="row">
                    <label class="label">How did you find us?</label>
                   
                    <div class="field">
                        <#if applicationForm.programmeDetails?? && applicationForm.programmeDetails.referrer??>                    
                        ${applicationForm.programmeDetails.referrer.freeVal}
                        </#if>                    
                    </div>
                </div>
			
			</div>

            <div>	
            		<#if applicationForm.programmeDetails.supervisors?? && (applicationForm.programmeDetails.supervisors?size > 0) > 
	            	  <#list applicationForm.programmeDetails.supervisors! as supervisor>
		            	  <div class="row">
		            	  	 <label class="label">Supervision</label>
		            	  	
		            	   <div class="field">
		            	       Name: ${(supervisor.firstname?html)!} ${(supervisor.lastname?html)!}, Email :${supervisor.email?html}, Is this supervisor aware of the application? <#if supervisor.awareSupervisor == "YES"> Yes <#else> No </#if>
		            	       <br/>
		            	   </div>
		            	 </div>
	            	  </#list>
            	  	<#else>
            	  	  	<div class="row">
		            	  	<label class="label">Supervision</label>
		            	  
		            	   <div class="field"> - </div>
		             	</div>
            	  	</#if>
                  
			</div>

            <div class="buttons">
				<button class="blue"  id="programmeCloseButton" type="button">Close</button>
			</div>

		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>