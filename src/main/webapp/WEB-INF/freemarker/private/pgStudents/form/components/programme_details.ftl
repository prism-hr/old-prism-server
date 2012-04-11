<#-- Assignments -->
<#import "/spring.ftl" as spring />

<#-- Programme Details Rendering -->


<h2 id="programme-H2" class="tick">
	<span class="left"></span><span class="right"></span><span class="status"></span>
	Programme<em>*</em>
</h2>

<div>
	<form>
        
        <input type="hidden" name="programmeDetailsId" id="programmeDetailsId" value="${(programmeDetails.id?string("######"))!}"/>
		<div>
        	
        	<!-- Programme name (disabled) -->
            <div class="row">
            	<label class="plain-label">Programme<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.programme'/>"></span>
                <div class="field">
                	<input class="full" id="programmeName" name="programmeName" type="text" value="${applicationForm.project.program.title?html}" disabled="disabled" />
                	<@spring.bind "programmeDetails.programmeName" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                </div>
			</div>
              
			<!-- Study option -->
            <div class="row">
                <label class="plain-label">Study Option<em>*</em></label>
               <span class="hint" data-desc="<@spring.message 'programmeDetails.studyOption'/>"></span>
                <div class="field">
            		<select class="full" id="studyOption" name="studyOption" 
            		<#if applicationForm.isSubmitted()>
            		disabled="disabled"
            		</#if>>
            		  <option value="">Select...</option>
            		  <#list studyOptions as studyOption>
                          <option value="${studyOption}"
                          <#if programmeDetails.studyOption?? &&  programmeDetails.studyOption == studyOption >
                            selected="selected"
                            </#if>  
                          >${studyOption.freeVal}</option>               
                    </#list>
                  	</select>     
                  	<@spring.bind "programmeDetails.studyOption" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                </div>
			</div>

			<!-- Project -->
			<div class="row">
                <label class="plain-label">Project</label>
               <span class="hint" data-desc="<@spring.message 'programmeDetails.project'/>"></span>
                <div class="field">
            		<input class="full" id="projectName" name="projectName" type="text" value="${applicationForm.project.title?html}" disabled="disabled"/>
                </div>
			</div>
			
			 <div>
        	<!-- Start date -->
            <div class="row">
            	<label class="plain-label">Start Date<em>*</em></label>
               <span class="hint" data-desc="<@spring.message 'programmeDetails.startDate'/>"></span>
                <input class="full date" type="text" id="startDate" name="startDate" value="${(programmeDetails.startDate?string('dd-MMM-yyyy'))!}"
                <#if applicationForm.isSubmitted()>
                    disabled="disabled"
                    </#if>>
                </input> 
                <@spring.bind "programmeDetails.startDate" /> 
                <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
            </div>

            <!-- Referrer -->
            <div class="row">
            	<label class="plain-label">How did you find us?<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.howDidYouFindUs'/>"></span>
                <div class="field">
                	<select class="full" id="referrer" name="referrer"
                	<#if applicationForm.isSubmitted()>
                    disabled="disabled"
                    </#if>>
                	<option value="">Select...</option>
                	 <#list referrers as referrer>
                          <option value="${referrer}"
                           <#if programmeDetails.referrer?? &&  programmeDetails.referrer == referrer >
                            selected="selected"
                            </#if>
                          >${referrer.freeVal}</option>               
                    </#list>
                    </select>    
                   <@spring.bind "programmeDetails.referrer" /> 
                <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>          
                </div>
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
				<@spring.bind "programmeDetails.supervisors" /> 
                <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                <table id="supervisors" class="data-table">
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
        <tbody>
               <#list programmeDetails.supervisors! as supervisor>
               <span name="supervisor_span">
               	<tr>
                     <td> ${(supervisor.firstname?html)!} ${(supervisor.lastname?html)!} </td>
                     <td> ${supervisor.email?html} </td>
                     <td> ${supervisor.awareSupervisor?html} </td>
                     <td> <#if !applicationForm.isSubmitted()><a class="button-delete" name="deleteSupervisor" >delete</a> <a class="button-edit"  id="supervisor_${(supervisor.id?string('#######'))!}" name ="editSupervisorLink">edit</a></#if></td>
                 </tr>
                    <input type="hidden" id="${(supervisor.id?string('#######'))!}_supervisorId" name = "sId" value="${(supervisor.id?string('#######'))!}"/>
                    <input type="hidden" id="${(supervisor.id?string('#######'))!}_firstname" name = "sFN" value="${(supervisor.firstname?html)!}"/>
                    <input type="hidden" id="${(supervisor.id?string('#######'))!}_lastname" name = "sLN" value="${(supervisor.lastname?html)!}"/>
                    <input type="hidden" id="${(supervisor.id?string('#######'))!}_email" name = "sEM"  value="${(supervisor.email?html)!}"/>
                    <input type="hidden" id="${(supervisor.id?string('#######'))!}_aware" name = "sAS" value="${(supervisor.primarySupervisor?html)!}"/>                    
                               
                   <input type="hidden" name="supervisors" value='{"firstname" :"${(supervisor.firstname?html)!}","lastname" :"${(supervisor.lastname?html)!}","email" :"${supervisor.email?html}", "awareSupervisor":"${supervisor.awareSupervisor?html}"}' />                             
              </span>
              </#list>
               </tbody>
              </table>
            </div>
  
            
            <#if !applicationForm.isSubmitted()>
            
            <!-- supervisor rows -->
            <input type="hidden" id="supervisorId" name="supervisorId"/>
            
            <div class="row">
            <label class="plain-label">Supervisor First Name<em>*</em></label>
             <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.firstname'/>"></span>
                <div class="field">
                    <input class="full" type="text" placeholder="First Name" id="supervisorFirstname" name="supervisorFirstname"/>
                </div>
                <span class="invalid" name="superFirstname" style="display:none;"></span>
            </div>
            
            <div class="row">
                <label class="plain-label">Supervisor Last Name<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.lastname'/>"></span>
                <div class="field"> 
                    <input class="full" type="text" placeholder="Last Name" id="supervisorLastname" name="supervisorLastname"/>
                </div>
                <span class="invalid" name="superLastname" style="display:none;"></span>
            </div>
            
            <div class="row">
                <label class="plain-label">Supervisor Email<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.email'/>"></span>
                <div class="field">
                    <input class="full" type="text" placeholder="Email address" id="supervisorEmail" name="supervisorEmail"/>
                </div>
                <span class="invalid" name="superEmail" style="display:none;" ></span>
            </div>
            
            <div class="row">
                <label class="plain-label">Is supervisor aware of your application?</label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.awareOfApplication'/>"></span>
                <input type="checkbox" name="awareSupervisorCB" id="awareSupervisorCB"/>
                <input type="hidden" name="awareSupervisor" id="awareSupervisor"/>
            </div>      
            
            	<span class="supervisorAction"></span>       
            	<a id="updateSupervisorButton" class="button" style="display:none;">Update Supervisor</a>
            	<a id="addSupervisorButton" class="button" style="display:none;">Add Supervisor</a>
                </#if>
		</div>
		
       

        <div class="buttons">
        	<button type="reset" id="programmeCancelButton" name="programmeCancelButton" value="cancel">Cancel</button>
        	<a class="button blue" type="button" id="programmeCloseButton" name="programmeCloseButton">Close</a>
        	<#if !applicationForm.isSubmitted()>
            <a class="button blue" id="programmeSaveButton">Save</a>
            </#if>    
		</div>

	</form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>

<@spring.bind "programmeDetails.*" /> 
 
<#if !message?? || (!spring.status.errorMessages?has_content && (message=='close'))  >
<script type="text/javascript">
	$(document).ready(function(){
		$('#programme-H2').trigger('click');
	});
</script>
</#if>