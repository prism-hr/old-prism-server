<#-- Assignments -->
<#import "/spring.ftl" as spring />

<#-- Programme Details Rendering -->


	<h2 class="tick">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Programme
	</h2>
	
	<div>
    	<form>
            
            <input type="hidden" name="programmeDetailsId" id="programmeDetailsId" value="${(model.applicationForm.programmeDetails.id?string("######"))!}"/>
            <input type="hidden" id="appId1" name="appId1" value="${model.applicationForm.id?string("######")}"/>    
			<div>
            	
            	<!-- Programme name (disabled) -->
                <div class="row">
                	<label class="label">Programme</label>
                    <span class="hint" title="Tooltip demonstration."></span>
                    <div class="field">
                    	<input class="full" id="programmeName" name="programmeName" type="text" value="${model.applicationForm.project.program.title}" disabled="disabled" />
                    </div>
				</div>
                  
				<!-- Study option -->
                <div class="row">
                    <label class="label">Study Option</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                		<select class="full" id="studyOption" name="studyOption" 
                		<#if model.applicationForm.isSubmitted()>
                		disabled="disabled">
                		</#if>
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
                                <span style="color:red;"><@spring.message  model.result.getFieldError('studyOption').code /></span>                           
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
                   <#list model.applicationForm.programmeDetails.supervisors! as supervisor>
                   <span name="supervisor_span">
                        ${supervisor.email}<#if !model.applicationForm.isSubmitted()><a class="button">delete</a></#if>
                       <input type="hidden" name="supervisors" value='{"email" :"${supervisor.email}", "primarySupervisor":"${supervisor.primarySupervisor}", "awareSupervisor":"${supervisor.awareSupervisor}"}' />                             
                       <p></p>
                  </span>
                  </#list>
                </div>
                
                <#if !model.applicationForm.isSubmitted()>  
                <!-- supervisor rows -->
                <table class="multiples">
                	<colgroup>
                    	<col />
                      	<col style="width: 80px;" />
                      	<col style="width: 80px;" />
                    </colgroup>
                    
                    <thead>
                    	<tr>
	                        <th class="align-left">Supervisor</th>
	                        <th>Primary</th>
	                        <th>Aware</th>
                    	</tr>
                    </thead>
                    
                    <tbody>
						<!-- repeat these rows for every existing supervisor. -->
                      	<tr>
	                        <th class="align-left"><input class="full" type="text" placeholder="Email address" id="supervisorEmail" name="supervisorEmail"/></th>
	                        <th><input type="checkbox" name="primarySupervisorCB" id="primarySupervisorCB"/></th>
	                        <input type="hidden" name="primarySupervisor" id="primarySupervisor"/>
	                        <th><input type="checkbox" name="awareSupervisorCB" id="awareSupervisorCB"/></th>
	                        <input type="hidden" name="awareSupervisor" id="awareSupervisor"/>
                      	</tr>
                      	<!-- end repeat -->
                    </tbody>
                    
				</table>
                    <a id="addSupervisorButton" class="button" style="width: 110px;">Add Supervisor</a>
                    </#if>
			</div>

            <div>
            	<!-- Start date -->
                <div class="row">
                	<label class="label">Start Date</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <input class="full date" type="date" id="startDate" name="startDate" value="${(model.applicationForm.programmeDetails.startDate?string('dd-MMM-yyyy'))!}"
                    <#if model.applicationForm.isSubmitted()>
                        disabled="disabled">
                        </#if>
                    </input>
                    <#if model.hasError('startDate')>                            
                          <span style="color:red;"><@spring.message  model.result.getFieldError('startDate').code /></span>                           
                    </#if>
                </div>

                <!-- Referrer -->
                <div class="row">
                	<label class="label">Referrer</label>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    	<select class="full" id="referrer" name="referrer"
                    	<#if model.applicationForm.isSubmitted()>
                        disabled="disabled">
                        </#if>
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
                            <span style="color:red;"><@spring.message  model.result.getFieldError('referrer').code /></span>                           
                         </#if>
                    </div>
				</div>

			</div>

            <div class="buttons">
            	<a class="button blue" type="button" id="programmeCloseButton" name="programmeCloseButton">Close</a>
            	<#if !model.applicationForm.isSubmitted()>
                <a class="button blue" id="programmeSaveButton">Save</a>
                </#if>    
			</div>

		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>