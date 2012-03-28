<#-- Assignments -->
<#import "/spring.ftl" as spring />

<#-- Programme Details Rendering -->


	<h2 id="programme-H2" class="tick">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Programme<em>*</em>
	</h2>
	
	<div>
    	<form>
            
            <input type="hidden" name="programmeDetailsId" id="programmeDetailsId" value="${(model.applicationForm.programmeDetails.id?string("######"))!}"/>
            <input type="hidden" id="appId1" name="appId1" value="${model.applicationForm.id?string("######")}"/>    
			<div>
            	
            	<!-- Programme name (disabled) -->
            	<#if model.hasError('programmeDetails')>
                <div class="row">                           
                    <span class="invalid"><@spring.message  model.result.getFieldError('programmeDetails').code /></span><br/>
                </div>                          
                </#if>
                <div class="row">
                	<label class="label">Programme</label>
                    <span class="hint" title="Tooltip demonstration."></span>
                    <div class="field">
                    	<input class="full" id="programmeName" name="programmeName" type="text" value="${model.applicationForm.project.program.title}" disabled="disabled" />
                    </div>
				</div>
                  
				<!-- Study option -->
                <div class="row">
                    <label class="label">Study Option<em>*</em></label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                		<select class="full" id="studyOption" name="studyOption" 
                		<#if model.applicationForm.isSubmitted()>
                		disabled="disabled"
                		</#if>>
                		  <option value="">Select...</option>
                		  <#list model.studyOptions as studyOption>
                              <option value="${studyOption}"
                              <#if model.applicationForm.programmeDetails.studyOption?? &&  model.applicationForm.programmeDetails.studyOption == studyOption >
                                selected="selected"
                                </#if>  
                              >${studyOption.freeVal}</option>               
                        </#list>
                      	</select>
                      	<#if model.hasError('studyOption')>                            
                                <span class="invalid"><@spring.message  model.result.getFieldError('studyOption').code /></span>                           
                        </#if>
                    </div>
				</div>

				<!-- Project -->
				<div class="row">
                    <label class="label">Project</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                		<input class="full" id="projectName" name="projectName" type="text" value="${model.applicationForm.project.title}" disabled="disabled"/>
                    </div>
				</div>
			
			</div>

            <div>
            	
            	<h3>Supervision</h3>
                 
                 
                 <div id="supervisor_div">
                 <#if model.hasError('supervisors')>
                          <div class="row">                          
                            <span class="invalid"><@spring.message  model.result.getFieldError('supervisors').code /></span>
                          </div>                             
                    </#if>
                   <#list model.applicationForm.programmeDetails.supervisors! as supervisor>
                   <span name="supervisor_span">
                         Name: ${supervisor.firstname!} ${supervisor.lastname!}, Email: ${supervisor.email}, Primary:${supervisor.primarySupervisor}, Is supervisor aware of your application:${supervisor.awareSupervisor} <#if !model.applicationForm.isSubmitted()><a class="button-delete">delete</a></#if>
                       <input type="hidden" name="supervisors" value='{"firstname" :"${supervisor.firstname!}","lastname" :"${supervisor.lastname!}","email" :"${supervisor.email}", "primarySupervisor":"${supervisor.primarySupervisor}", "awareSupervisor":"${supervisor.awareSupervisor}"}' />                             
                       <p></p>
                  </span>
                  </#list>
                </div>
                
                <#if !model.applicationForm.isSubmitted()>  
                <!-- supervisor rows -->
                <div class="row">
                <label class="label">Supervisor First Name<em>*</em></label>
                    <div class="field">
                        <input class="full" type="text" placeholder="First Name" id="supervisorFirstname" name="supervisorFirstname"/>
                    </div>
                </div>
                
                <div class="row">
                    <label class="label">Supervisor Last Name<em>*</em></label>
                    <div class="field"> 
                        <input class="full" type="text" placeholder="Last Name" id="supervisorLastname" name="supervisorLastname"/>
                    </div>
                </div>
                
                <div class="row">
                    <label class="label">Supervisor Email<em>*</em></label>
                    <div class="field">
                        <input class="full" type="text" placeholder="Email address" id="supervisorEmail" name="supervisorEmail"/>
                    </div>
                </div>
                
                <div class="row">
                       <label class="label">Primary</label>
                       <input type="checkbox" name="primarySupervisorCB" id="primarySupervisorCB"/>
                       <input type="hidden" name="primarySupervisor" id="primarySupervisor"/>
                </div>
                
                <div class="row">
                    <label class="label">Is supervisor aware of your application?</label>
                    <input type="checkbox" name="awareSupervisorCB" id="awareSupervisorCB"/>
                    <input type="hidden" name="awareSupervisor" id="awareSupervisor"/>
                </div>      
                
                    <a id="addSupervisorButton" class="button" style="width: 110px;">Add Supervisor</a>
                    </#if>
			</div>
			
            <div>
            	<!-- Start date -->
                <div class="row">
                	<label class="label">Start Date<em>*</em></label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <input class="full date" type="text" id="startDate" name="startDate" value="${(model.applicationForm.programmeDetails.startDate?string('dd-MMM-yyyy'))!}"
                    <#if model.applicationForm.isSubmitted()>
                        disabled="disabled"
                        </#if>>
                    </input>
                    <#if model.hasError('startDate')>                            
                          <span class="invalid"><@spring.message  model.result.getFieldError('startDate').code /></span>                           
                    </#if>
                </div>

                <!-- Referrer -->
                <div class="row">
                	<label class="label">Referrer<em>*</em></label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    	<select class="full" id="referrer" name="referrer"
                    	<#if model.applicationForm.isSubmitted()>
                        disabled="disabled"
                        </#if>>
                    	<option value="">Select...</option>
                    	 <#list model.referrers as referrer>
                              <option value="${referrer}"
                               <#if model.applicationForm.programmeDetails.referrer?? &&  model.applicationForm.programmeDetails.referrer == referrer >
                                selected="selected"
                                </#if>
                              >${referrer.freeVal}</option>               
                        </#list>
                      	</select>
                      	 <#if model.hasError('referrer')>                            
                            <span class="invalid"><@spring.message  model.result.getFieldError('referrer').code /></span>                           
                         </#if>
                    </div>
				</div>

			</div>

            <div class="buttons">
            	<button type="reset" id="programmeCancelButton" name="programmeCancelButton" value="cancel">Cancel</button>
            	<a class="button blue" type="button" id="programmeCloseButton" name="programmeCloseButton">Close</a>
            	<#if !model.applicationForm.isSubmitted()>
                <a class="button blue" id="programmeSaveButton">Save</a>
                </#if>    
			</div>
	
		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>

<#if model.result?? && model.result.hasErrors()  >

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#programme-H2').trigger('click');
	});
</script>
</#if>