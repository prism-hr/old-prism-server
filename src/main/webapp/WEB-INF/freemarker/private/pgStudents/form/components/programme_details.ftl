<#-- Assignments -->
<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />
<#assign studyOptionError = RequestParameters.studyOptionError! />
<#assign programError = RequestParameters.programError! />
<#-- Programme Details Rendering -->

<a name="programme-details"></a>
<h2 id="programme-H2" class="open ">
	<span class="left"></span><span class="right"></span><span class="status"></span>
	Programme<em>*</em>
</h2>

<div>
	
	<form>
				<#if errorCode?? && errorCode=="true">
					<div class="section-error-bar">
						<div class="row">
							<span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>             	
							<span class="invalid-info-text">
								<@spring.message 'programmeDetails.project'/>
							</span>
				 		</div>
				 	</div>
			 	<#else>
				 	<div id="prog-info-bar-div" class="section-info-bar">
						<div class="row">
							<span id="prog-info-bar-span" class="info-text">
								<@spring.message 'programmeDetails.project'/> 
							</span>
						</div>
					</div>	
				</#if>
		
        

		<div class="row-group">
			<#if programError?? && programError=='true'>
                	<span class="invalid"><@spring.message 'application.program.invalid'/></span>
             </#if>
        	<!-- Programme name (disabled) -->
            <div class="row">
            	<label class="plain-label grey-label">Programme<em class="grey-label">*</em></label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.programme'/>"></span>
                <div class="field">
                	<input class="full" id="programmeName" name="programmeName" type="text" value="${(applicationForm.program.title?html)!}" disabled="disabled" />
                	 
                </div>
			</div>
			<@spring.bind "programmeDetails.programmeName" />
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
			
              
			<!-- Study option -->
            <div class="row">
                <label class="plain-label">Study Option<em>*</em></label>
               <span class="hint" data-desc="<@spring.message 'programmeDetails.studyOption'/>"></span>
                <div class="field">
            		<select class="full" id="studyOption" name="studyOption" 
            		<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
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
                  	
                	<#if studyOptionError?? && studyOptionError=='true'>
                		 <span class="invalid"><@spring.message  'programmeDetails.studyOption.invalid'/></span>
                	</#if>
                </div>
			</div>
				<@spring.bind "programmeDetails.studyOption" />
				
				<#if spring.status.errorMessages?has_content>
					<div class="row">
						<div class="field">
							<span class="invalid">${spring.status.errorMessages[0]!}</span>
						</div>
					</div>
				</#if>
				
			<!-- Project -->
			<div class="row">
                <label class="plain-label grey-label">Project</label>
               <span class="hint" data-desc="<@spring.message 'programmeDetails.project'/>"></span>
                <div class="field">
            		<input class="full" id="projectName" name="projectName" type="text" value="${(applicationForm.projectTitle?html)!'Not Specified'}" disabled="disabled"/>
                </div>
			</div>
			
			 <div>
        	<!-- Start date -->
            <div class="row">
            	<label class="plain-label">Start Date<em>*</em></label>
               <span class="hint" data-desc="<@spring.message 'programmeDetails.startDate'/>"></span>
               <div class="field">
                	<input class="full date" type="text" id="startDate" name="startDate" value="${(programmeDetails.startDate?string('dd-MMM-yyyy'))!}"
                	<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                    	disabled="disabled"
                    </#if>>
                	</input> 
                	 
                </div>
            </div>
            <@spring.bind "programmeDetails.startDate" />
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>

            <!-- Referrer -->
            <div class="row">
            	<label class="plain-label">How did you find us?<em>*</em></label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.howDidYouFindUs'/>"></span>
                <div class="field">
                	<select class="full" id="referrer" name="referrer"
                	<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
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
                   
                </div>
			</div>
			<@spring.bind "programmeDetails.referrer" /> 
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
			

		</div>
		
		</div>


        <div class="row-group">
        	
        	<label class="group-heading-label">Supervision</label>
             <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.supervisor'/>"></span>
             
             <div id="supervisor_div">
				<@spring.bind "programmeDetails.suggestedSupervisors" /> 
                <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                <table id="supervisors" class="data-table">
                <thead>
                <#if (!applicationForm.isSubmitted() || programmeDetails.hasSupervisors())>
        		<tr>
          			<th >Name</th>
            		<th>Email</th>
           			 <th>Aware</th>
           			 <th>Action</th>
          		</tr>
          		</#if>
        </thead>
        <tbody>
               <#list programmeDetails.suggestedSupervisors! as supervisor>
               <span name="supervisor_span">
               	<tr>
                     <td> ${(supervisor.firstname?html)!} ${(supervisor.lastname?html)!} </td>
                     <td> ${supervisor.email?html} </td>
                     <td><#if supervisor.aware > Yes <#else> No </#if></td>
                     <td> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()><a class="button-delete" name="deleteSupervisor" id="supervisorDelete_${encrypter.encrypt(supervisor.id)}">delete</a> <a class="button-edit"  id="supervisor_${encrypter.encrypt(supervisor.id)}" name ="editSupervisorLink">edit</a></#if></td>
                 </tr>
                    <input type="hidden" id="${encrypter.encrypt(supervisor.id)}_supervisorId" name = "sId" value="${encrypter.encrypt(supervisor.id)}"/>
                    <input type="hidden" id="${encrypter.encrypt(supervisor.id)}_firstname" name = "sFN" value="${(supervisor.firstname?html)!}"/>
                    <input type="hidden" id="${encrypter.encrypt(supervisor.id)}_lastname" name = "sLN" value="${(supervisor.lastname?html)!}"/>
                    <input type="hidden" id="${encrypter.encrypt(supervisor.id)}_email" name = "sEM"  value="${(supervisor.email?html)!}"/>
                    <input type="hidden" id="${encrypter.encrypt(supervisor.id)}_aware" name = "sAS" value="${(supervisor.primarySupervisor?html)!}"/>                    
                               
                   <input type="hidden" name="suggestedSupervisors" id="${encrypter.encrypt(supervisor.id)}_supervisors" value='{"firstname" :"${(supervisor.firstname?html)!}","lastname" :"${(supervisor.lastname?html)!}","email" :"${supervisor.email?html}", "awareSupervisor":"<#if supervisor.aware>YES<#else>NO</#if>"}' />                             
              </span>
              </#list>
               </tbody>
              </table>
            </div>
  
            
            <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
            
            <!-- supervisor rows -->
            <input type="hidden" id="supervisorId" name="supervisorId"/>
            
            <div class="row">
            <label class="plain-label">Supervisor First Name</label>
             <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.firstname'/>"></span>
                <div class="field">
                    <input class="full" type="text" placeholder="First Name" id="supervisorFirstname" name="supervisorFirstname"/>
                <span class="invalid" name="superFirstname" style="display:none;"></span>
                </div>
            </div>
            
            <div class="row">
                <label class="plain-label">Supervisor Last Name</label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.lastname'/>"></span>
                <div class="field"> 
                    <input class="full" type="text" placeholder="Last Name" id="supervisorLastname" name="supervisorLastname"/>
                <span class="invalid" name="superLastname" style="display:none;"></span>
                </div>
            </div>
            
            <div class="row">
                <label class="plain-label">Supervisor Email</label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.email'/>"></span>
                <div class="field">
                    <input class="full" type="text" placeholder="Email address" id="supervisorEmail" name="supervisorEmail"/>
                	<span class="invalid" name="superEmail" style="display:none;" ></span>
                </div>
            </div>
            
            <div class="row">
                <label class="plain-label">Is this supervisor aware of your application?</label>
                <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.awareOfApplication'/>"></span>
                <div class="field">
                	<input type="checkbox" name="awareSupervisorCB" id="awareSupervisorCB"/>
                	<input type="hidden" name="awareSupervisor" id="awareSupervisor"/>
                </div>
            </div>      
            <div class="row">
            	<span class="supervisorAction"></span>       
            	<a id="updateSupervisorButton" class="button" style="display:none;">Update Supervisor</a>
            	<div class="field">
            		<a id="addSupervisorButton" class="button blue" style="display:none;">Submit</a>
            	</div>
            </div>
                </#if>
		</div>
		
       <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
       <div class="row-group terms-box">
			<div class="row">
				<span class="terms-label">
					I understand that in accepting this declaration I am confirming
					that the information contained in this section is true and accurate. 
					I am aware that any subsequent offer of study may be retracted at any time
					if any of the information contained is found to be misleading or false.
				</span>
				<div class="terms-field">
		        	<input type="checkbox" name="acceptTermsPDCB" id="acceptTermsPDCB"/>
		        </div>
	            <input type="hidden" name="acceptTermsPDValue" id="acceptTermsPDValue"/>
			</div>	        
	    </div>
	    </#if>  

        <div class="buttons">
        
        	<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
        		<button type="reset" id="programmeCancelButton" name="programmeCancelButton" value="cancel">Clear</button>
            </#if>    
        	<a class="button blue" type="button" id="programmeCloseButton" name="programmeCloseButton">Close</a>
        	<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
            	<a class="button blue" type="button" id="programmeSaveButton">Save</a>
            </#if>  	
		</div>

	</form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>
<@spring.bind "programmeDetails.*" />

<#if ((errorCode?? && errorCode=='false' && studyOptionError?? && studyOptionError =='false' && programError?? && programError =='false' && !applicationForm.shouldOpenFirstSection()) || applicationForm.isSubmitted() || (message?? && message='close' && !spring.status.errorMessages?has_content))>

<script type="text/javascript">
	$(document).ready(function()
	{
		if (!$('#programmeDetailsSection').hasClass('loaded'))
		{
			$('#programmeDetailsSection').addClass('loaded');
		}
		else
		{
			$('#programme-H2').trigger('click');
		}
	});
</script> 
</#if>
