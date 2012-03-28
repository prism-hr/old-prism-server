<#if model.applicationForm.project?has_content>
	<#assign hasProject = true>
<#else>
	<#assign hasProject = false>
</#if>

<#if model.programme?has_content>
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
                    <span class="hint" title="Tooltip demonstration."></span>
                    <div class="field">
                    	${model.applicationForm.project.program.title}
                    </div>
				</div>
                  
				<!-- Study option -->
                <div class="row">
                    <label class="label">Study Option</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    <#if model.applicationForm.programmeDetails?? && model.applicationForm.programmeDetails.studyOption??>
                    	${model.applicationForm.programmeDetails.studyOption.freeVal}
                    </#if>
                    </div>
				</div>

				<!-- Project -->
				<div class="row">
                    <label class="label">Project</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                		${model.applicationForm.project.title}
                    </div>
				</div>
			
			</div>

            <div>	
            		<#if model.applicationForm.programmeDetails.supervisors?? && (model.applicationForm.programmeDetails.supervisors?size > 0) > 
	            	  <#list model.applicationForm.programmeDetails.supervisors! as supervisor>
		            	  <div class="row">
		            	  	 <label class="label">Supervision</label>
		            	  	 <span class="hint" data-desc="Tooltip demonstration."></span>
		            	   <div class="field">
		            	       Name: ${supervisor.firstname!} ${supervisor.lastname!}, Email :${supervisor.email}, Primary:${supervisor.primarySupervisor}, Supervisor is aware of the application:${supervisor.awareSupervisor}
		            	       <br/>
		            	   </div>
		            	 </div>
	            	  </#list>
            	  	<#else>
            	  	  	<div class="row">
		            	  	<label class="label">Supervision</label>
		            	  	<span class="hint" data-desc="Tooltip demonstration."></span>
		            	   <div class="field"> - </div>
		             	</div>
            	  	</#if>
                  
			</div>

            <div>
            	<!-- Start date -->
                <div class="row">
                	<label class="label">Start Date</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    ${(model.applicationForm.programmeDetails.startDate?string('dd-MMM-yyyy'))!}
                </div>

                <!-- Referrer -->
                <div class="row">
                	<label class="label">Referrer</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                        <#if model.applicationForm.programmeDetails?? && model.applicationForm.programmeDetails.referrer??>                    
						${model.applicationForm.programmeDetails.referrer.freeVal}
						</#if>                    
					</div>
				</div>

			</div>

            <div class="buttons">
				<button class="blue"  id="programmeCloseButton" type="button">Close</button>
			</div>

		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>