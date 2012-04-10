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
                	<label class="plain-label">Programme</label>
                    <span class="hint" data-desc="<@spring.message 'programmeDetails.programme'/>"></span>
                    <div class="field">
                    	<input class="full" id="programmeName" name="programmeName" type="text" value="${model.applicationForm.project.program.title?html}" disabled="disabled" />
                    </div>
				</div>
                  
				<!-- Study option -->
                <div class="row">
                    <label class="plain-label">Study Option<em>*</em></label>
                   <span class="hint" data-desc="<@spring.message 'programmeDetails.studyOption'/>"></span>
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
                    <label class="plain-label">Project</label>
                   <span class="hint" data-desc="<@spring.message 'programmeDetails.project'/>"></span>
                    <div class="field">
                		<input class="full" id="projectName" name="projectName" type="text" value="${model.applicationForm.project.title?html}" disabled="disabled"/>
                    </div>
				</div>
			
			</div>

			<#-- Supervisor Data Table: Include the following section for supervisor table! -->
			<#--
			<div>
				<#include "/private/common/parts/supervisor_data_table.ftl"/>
			</div>
			-->
            <div>
            	
            	<label class="group-heading-label">Supervision</label>
                 <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.supervisor'/>"></span>
                 
                 <div id="supervisor_div">
                 <#if model.hasError('supervisors')>
                          <div class="row">                          
                            <span class="invalid"><@spring.message  model.result.getFieldError('supervisors').code /></span>
                          </div>                             
                     </#if>
                    <table id="supervisors">
                    <thead>
            		<tr>
              			<th >Name</th>
                		<th>Email</th>
                		<th>Primary</th>
               			 <th>Aware</th>
               			 <th>Action</th>
               			 <th>&nbsp;</th>
              		</tr>
            </thead>
                   <#list model.applicationForm.programmeDetails.supervisors! as supervisor>
                   <span name="supervisor_span">
                   	<tr>
                         <td> ${(supervisor.firstname?html)!} ${(supervisor.lastname?html)!} </td>
                         <td> ${supervisor.email?html} </td> 
                         <td> <input type="radio" name="primarySupervisor" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>/> </td>
                         <td> ${supervisor.awareSupervisor?html} </td>
                         <td> <#if !model.applicationForm.isSubmitted()><a class="button-delete">delete</a> <a class="button-edit"  id="supervisor_${(supervisor.id?string('#######'))!}" name ="editSupervisorLink">edit</a></#if></td>
                     </tr>
                        <input type="hidden" id="${supervisor.id?string('#######')}_supervisorId" value="${(supervisor.id?string('#######'))!}"/>
                        <input type="hidden" id="${supervisor.id?string('#######')}_firstname" value="${(supervisor.firstname?html)!}"/>
                        <input type="hidden" id="${supervisor.id?string('#######')}_lastname" value="${(supervisor.lastname?html)!}"/>
                        <input type="hidden" id="${supervisor.id?string('#######')}_email" value="${(supervisor.email?html)!}"/>
                                   
                       <input type="hidden" name="supervisors" value='{"id" :"${(supervisor.id?html)!}","firstname" :"${(supervisor.firstname?html)!}","lastname" :"${(supervisor.lastname?html)!}","email" :"${supervisor.email?html}", "primarySupervisor":"${supervisor.primarySupervisor?html}", "awareSupervisor":"${supervisor.awareSupervisor?html}"}' />                             
                  </span>
                  </#list>
                  </table>
                </div>
                
                <#if !model.applicationForm.isSubmitted()>
                
                <!-- supervisor rows -->
                <input type="hidden" id="supervisorId" name="supervisorId"/>
                
                <div class="row">
                <label class="plain-label">Supervisor First Name<em>*</em></label>
                 <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.firstname'/>"></span>
                    <div class="field">
                        <input class="full" type="text" placeholder="First Name" id="supervisorFirstname" name="supervisorFirstname"/>
                    </div>
                </div>
                
                <div class="row">
                    <label class="plain-label">Supervisor Last Name<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.lastname'/>"></span>
                    <div class="field"> 
                        <input class="full" type="text" placeholder="Last Name" id="supervisorLastname" name="supervisorLastname"/>
                    </div>
                </div>
                
                <div class="row">
                    <label class="plain-label">Supervisor Email<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.email'/>"></span>
                    <div class="field">
                        <input class="full" type="text" placeholder="Email address" id="supervisorEmail" name="supervisorEmail"/>
                    </div>
                </div>
                
                <div class="row">
                       <label class="plain-label">Primary</label>
                       <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.primarySupervisor'/>"></span>
                       <input type="checkbox" name="primarySupervisorCB" id="primarySupervisorCB"/>
                       <input type="hidden" name="primarySupervisor" id="primarySupervisor"/>
                </div>
                
                <div class="row">
                    <label class="plain-label">Is supervisor aware of your application?</label>
                    <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.awareOfApplication'/>"></span>
                    <input type="checkbox" name="awareSupervisorCB" id="awareSupervisorCB"/>
                    <input type="hidden" name="awareSupervisor" id="awareSupervisor"/>
                </div>      
                
                	<span class="supervisorAction"></span>       
                    </#if>
			</div>
			
            <div>
            	<!-- Start date -->
                <div class="row">
                	<label class="plain-label">Start Date<em>*</em></label>
                   <span class="hint" data-desc="<@spring.message 'programmeDetails.startDate'/>"></span>
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
                	<label class="plain-label">Referrer<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'programmeDetails.howDidYouFindUs'/>"></span>
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

<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>

<#if model.result?? && model.result.hasErrors()  >

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#programme-H2').trigger('click');
	});
</script>
</#if>