<#if applicationForm.program?has_content>
	<#assign hasProgram = true>
<#else>
	<#assign hasProgram = false>
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
                
			<div class="sub_section_amdin">
            	
	            	<!-- Programme name (disabled) -->
	                <div class="admin_row">
	                	<label class="admin_row_label">Programme</label>
	                  
	                    <div class="field">
	                    	${(applicationForm.program.title?html)!}
	                    </div>
					</div>
	                  
					<!-- Study option -->
	                <div class="admin_row">
	                    <label class="admin_row_label">Study Option</label>
	
	                    <div class="field">
	                    <#if applicationForm.programmeDetails?? && applicationForm.programmeDetails.studyOption??>
	                    	${applicationForm.programmeDetails.studyOption.freeVal}
	                    </#if>
	                    </div>
					</div>
	
					<!-- Project -->
					<div class="admin_row">
	                    <label class="admin_row_label">Project</label>
	
	                    <div class="field">
	                		${applicationForm.projectTitle?html}
	                    </div>
					</div>
					
					<!-- Start date -->
	                <div class="admin_row">
	                    <label class="admin_row_label">Start Date</label>
	                   <div class="field">
	                    ${(applicationForm.programmeDetails.startDate?string('dd-MMM-yyyy'))!}</div>
	                </div>
	
	                <!-- Referrer -->
	                <div class="admin_row">
	                    <label class="admin_row_label">How did you find us?</label>
	                   
	                    <div class="field">
	                        <#if applicationForm.programmeDetails?? && applicationForm.programmeDetails.referrer??>                    
	                        ${applicationForm.programmeDetails.referrer.freeVal}
	                        </#if>                    
	                    </div>
	                </div>
                
                </div>
            	<#if applicationForm.programmeDetails.supervisors?? && (applicationForm.programmeDetails.supervisors?size > 0) >
	            	  <#list applicationForm.programmeDetails.supervisors! as supervisor>
	            	  	
	            	  	<div class="sub_section_amdin">
		            	  
			            	 <div class="admin_row">
			            	  	 <label class="admin_header">Supervision (${supervisor_index + 1})</label>
			            	  	 <div class="field">&nbsp</div>
			            	 </div>
			            	 
			            	 <div class="admin_row">
			            	 	<label class="admin_row_label">Name:</label>
			            	   	<div class="field">
			            	    	${(supervisor.firstname?html)!} ${(supervisor.lastname?html)!}
			            	   	</div>
			            	 </div>

			            	 <div class="admin_row">
			            	 	<label class="admin_row_label">Email:</label>
			            	   	<div class="field">
			            	    	${supervisor.email?html}
			            	   	</div>
			            	 </div>

			            	 <div class="admin_row">
			            	 	<label class="admin_row_label">Is this supervisor aware of the application? </label>
			            	   	<div class="field">
			            	    	<#if supervisor.awareSupervisor == "YES"> Yes <#else> No </#if>
			            	   	</div>
			            	 </div>
		            	   
		            	 </div>
	            	  
	            	  </#list>
            	  	
            	  	<#else>
            	  		<div class="sub_section_amdin">
							<div class="row">
				            	<label class="admin_header">Supervision</label>
				            	<div class="field">Not Provided</div>
				           	</div>
				        </div>
            	  	</#if>
            	  	
            <div class="buttons">
				<button class="blue"  id="programmeCloseButton" type="button">Close</button>
			</div>

		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>