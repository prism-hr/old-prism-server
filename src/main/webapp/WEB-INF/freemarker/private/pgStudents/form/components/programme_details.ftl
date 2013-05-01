<#-- Assignments -->
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#assign errorCode = RequestParameters.errorCode! />
<#assign studyOptionError = RequestParameters.studyOptionError! />
<#assign programError = RequestParameters.programError! />
<#-- Programme Details Rendering -->

<a name="programme-details"></a>
<h2 id="programme-H2" class="open">
  <span class="left"></span><span class="right"></span><span class="status"></span>
  Programme<em>*</em>
</h2>

<div>
  <form>
    <#if (errorCode?? && errorCode=="true")>
	    <div class="alert alert-error">
            <i class="icon-warning-sign" data-desc="Please provide all mandatory fields in this section."></i>
				<@spring.message 'programmeDetails.project'/>
	    </div>
    <#else>
    	<div class="alert alert-info">
          <i class="icon-info-sign"></i> 
     		  <@spring.message 'programmeDetails.project'/>
    	</div>  
    </#if>
  
    <div class="row-group">
      <#if programError?? && programError=='true'>
      <div class="alert alert-error">
          <i class="icon-warning-sign"></i>  
          <@spring.message 'application.program.invalid'/>
          </div>
      </#if>
      
      <!-- Programme name (disabled) -->
      <div class="row">
        <label class="plain-label grey-label" for="programmeName">Programme<em class="grey-label">*</em></label>
        <span class="hint grey" data-desc="<@spring.message 'programmeDetails.programme'/>"></span>
        <div class="field">
          <input class="full" id="programmeName" name="programmeName" type="text" value="${(applicationForm.program.title?html)!}" disabled="disabled" />
          
          <@spring.bind "programmeDetails.programmeName" />
          <#list spring.status.errorMessages as error>
            <div class="alert alert-error">
                <i class="icon-warning-sign"></i>  ${error}</div>
          </#list>
        </div>
      </div>
  
      <!-- Study option -->
      <div class="row">
        <label class="plain-label" for="studyOption">Study Option<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'programmeDetails.studyOption'/>"></span>
        <div class="field">
          <select class="full" id="studyOption" name="studyOption"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>>
            <option value="">Select...</option>
            <#list studyOptions as studyOption>
            <option value="${studyOption.name}"<#if programmeDetails.studyOption?? &&  programmeDetails.studyOption == studyOption.name> selected="selected"</#if>>${studyOption.name}</option>
            </#list>
          </select>
          <#if studyOptionError?? && studyOptionError=='true'>
          <div class="alert alert-error">
              <i class="icon-warning-sign"></i>  
              <@spring.message  'programmeDetails.studyOption.invalid'/></div>
          </#if>

          <@spring.bind "programmeDetails.studyOption" />
          <#if spring.status.errorMessages?has_content>
          <div class="alert alert-error">
              <i class="icon-warning-sign"></i>  ${spring.status.errorMessages[0]!}</div>
          </#if>
        </div>
      </div>
      
      <!-- Start date -->
      <div class="row">
        <label id="lbl_startDate" class="plain-label" for="startDate">Preferred Start Date<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'programmeDetails.startDate'/>"></span>
        <div class="field">
          <input class="full date" type="text" id="startDate" name="startDate" value="${(programmeDetails.startDate?string('dd MMM yyyy'))!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if> />
          <@spring.bind "programmeDetails.startDate" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error">
              <i class="icon-warning-sign"></i>  ${error}</div>
          </#list>
        </div>
      </div>
  
      <!-- Project -->
      <div class="row">
        <label class="plain-label grey-label" for="projectName">Project</label>
        <span class="hint grey" data-desc="<@spring.message 'programmeDetails.project'/>"></span>
        <div class="field">
        	<#if applicationForm.projectTitle?has_content>
        		<#assign project = true>
        	<#else>
        		<#assign project = false>
        	</#if>
        	
          <input class="full" id="projectName" name="projectName" type="text" value="<#if project>${(applicationForm.projectTitle?html)}<#else>Not Required</#if>" disabled="disabled"/>
        </div>
      </div>
  
      <!-- Referrer -->
      <div class="row">
        <label class="plain-label" for="referrer">How did you find us?<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'programmeDetails.howDidYouFindUs'/>"></span>
        <div class="field">
          <select class="full" id="referrer" name="referrer"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>>
            <option value="">Select...</option>
            <#list sourcesOfInterests as interests>
                <option value="${encrypter.encrypt(interests.id)!}"
                    <#if programmeDetails.sourcesOfInterest?? && programmeDetails.sourcesOfInterest.id?? && programmeDetails.sourcesOfInterest == interests> selected="selected"</#if>
                    >${interests.name}</option>
            </#list>
          </select>    
          <@spring.bind "programmeDetails.sourcesOfInterest" /> 
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error">
            <i class="icon-warning-sign"></i> ${error}</div>
          </#list>
        </div>
      </div>
      
      <!-- Referrer Free Text-->
      <div class="row">
        <label id="referrer-text-lbl" class="plain-label grey-label" for="referrer_text">Please explain<em>*</em></label>
        <span class="hint grey" data-desc="<@spring.message 'programmeDetails.howDidYouFindUsExplain'/>"></span>
        <div class="field">
          <input class="full grey-label" id="referrer_text" name="referrer_text" type="text" value=<#if programmeDetails.sourcesOfInterestText??>"${(programmeDetails.sourcesOfInterestText?html)}"<#else>"Not Required" disabled="disabled"</#if> />
          <@spring.bind "programmeDetails.sourcesOfInterestText" /> 
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error">
            <i class="icon-warning-sign"></i> ${error}</div>
          </#list>
        </div>
      </div>
  
    </div><!-- .row-group -->
  <div id="supervisor_div">
    <div class="row-group">
      <h3>Supervisors</h3>
      <div class="alert alert-info">
          <i class="icon-info-sign"></i> <@spring.message 'programmeDetails.supervisor.supervisor'/>
      </div>
    

        <@spring.bind "programmeDetails.suggestedSupervisors" /> 
        <#list spring.status.errorMessages as error>
        <div class="alert alert-error">
            <i class="icon-warning-sign"></i> ${error}</div>
        </#list>
        <table id="supervisors" class="table table-striped table-condensed table-bordered table-hover">
          <colgroup>
            <col />
            <col style="width: 30px;" />
            <col style="width: 30px;" />
          </colgroup>
          <tbody>
            <#list programmeDetails.suggestedSupervisors! as supervisor>
            <tr class="<#if supervisor.aware>aware<#else>unaware</#if>"<#if supervisor.id?? > rel="${encrypter.encrypt(supervisor.id)!}"</#if>>
              <td data-desc="Supervisor <#if supervisor.aware>aware<#else>unaware</#if> of application">
                ${(supervisor.firstname?html)!} ${(supervisor.lastname?html)!} (${supervisor.email?html})
              </td>
              
                <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>  
                <td>                  
                <a class="button-edit" data-desc="Edit" id="supervisor_<#if supervisor.id??>${encrypter.encrypt(supervisor.id)!}</#if>" name="editSupervisorLink">edit</a>
                </td>
                <td>
                <a class="button-delete" data-desc="Delete" id="supervisorDelete_<#if supervisor.id??>${encrypter.encrypt(supervisor.id)!}</#if>" name="deleteSupervisor">delete</a> </td>
                </#if>
                
                <input type="hidden" id="<#if supervisor.id?? >${encrypter.encrypt(supervisor.id)!}</#if>_supervisorId" name="sId" value="<#if supervisor.id?? >${encrypter.encrypt(supervisor.id)!}</#if>" />
                <input type="hidden" id="<#if supervisor.id?? >${encrypter.encrypt(supervisor.id)!}</#if>_firstname" name="sFN" value="${(supervisor.firstname?html)!}"/>
                <input type="hidden" id="<#if supervisor.id?? >${encrypter.encrypt(supervisor.id)!}</#if>_lastname" name="sLN" value="${(supervisor.lastname?html)!}"/>
                <input type="hidden" id="<#if supervisor.id?? >${encrypter.encrypt(supervisor.id)!}</#if>_email" name="sEM"  value="${(supervisor.email?html)!}"/>
                <input type="hidden" id="<#if supervisor.id?? >${encrypter.encrypt(supervisor.id)!}</#if>_aware" name="sAS" value="<#if supervisor.aware>YES<#else>NO</#if>"/>                    
                <input type="hidden" name="suggestedSupervisors" id="<#if supervisor.id?? >${encrypter.encrypt(supervisor.id)!}</#if>_supervisors" value='{"firstname" :"${(supervisor.firstname?html?replace("'", "\\u0027"))!}","lastname" :"${(supervisor.lastname?html?replace("'", "\\u0027"))!}","email" :"${supervisor.email?html?replace("'", "\\u0027")}", "awareSupervisor":"<#if supervisor.aware>YES<#else>NO</#if>"}' />                             
             
            </tr>
            </#list>
          </tbody>
        </table>
      </div><!-- .row-group -->
      <div class="row-group">
        <h3>Add a Supervisor</h3>
      <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
      <!-- supervisor rows -->
      <input type="hidden" id="supervisorId" name="supervisorId"/>
      
      <div class="row">
        <label class="plain-label" for="supervisorFirstname">Supervisor First Name<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.firstname'/>"></span>
        <div class="field">
          <input class="full" type="text" placeholder="First Name" id="supervisorFirstname" name="supervisorFirstname"/>
        </div>
      </div>
      
      <div class="row">
        <label class="plain-label" for="supervisorLastname">Supervisor Last Name<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.lastname'/>"></span>
        <div class="field"> 
          <input class="full" type="text" placeholder="Last Name" id="supervisorLastname" name="supervisorLastname"/>
        </div>
      </div>
  
      <div class="row">
        <label class="plain-label" for="supervisorEmail">Supervisor Email<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.email'/>"></span>
        <div class="field">
          <input class="full" type="email" placeholder="Email address" id="supervisorEmail" name="supervisorEmail"/>
        </div>
      </div>
  
      <div class="row">
        <label class="plain-label">Is this supervisor aware of your application?<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'programmeDetails.supervisor.awareOfApplication'/>"></span>
        <div class="field">
          <label for="awareYes"><input id="awareYes" type="radio" name="awareSupervisor" value="YES" /> Yes</label>
          <label for="awareNo"><input id="awareNo" type="radio" name="awareSupervisor" value="NO" /> No</label>
        </div>
      </div>
  
      <div class="row">
        <span class="supervisorAction"></span>       
        <div class="field">
          <button id="updateSupervisorButton" class="btn" type="button" style="display:none;">Update</button>
          <button id="addSupervisorButton" class="btn" type="button">Add</button>
        </div>
      </div>
      </#if>
      
    </div><!-- .row-group -->
    </div>
  
    <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
    	<@spring.bind "programmeDetails.acceptedTerms" />
       	<#if spring.status.errorMessages?size &gt; 0>        
		<div class="alert alert-error tac" >
      <#else>
        <div class="alert tac" >
      </#if>
    
      <div class="row">
				<label for="acceptTermsPDCB" class="terms-label">
					Confirm that the information that you have provided in this section is true 
					and correct. Failure to provide true and correct information may result in a 
					subsequent offer of study being withdrawn.				
				</label>

        <div class="terms-field">
          <input type="checkbox" name="acceptTermsPDCB" id="acceptTermsPDCB"/>
        </div>
        <input type="hidden" name="acceptTermsPDValue" id="acceptTermsPDValue"/>
      </div>      
          
    </div><!-- .row-group -->    
    </#if>  


    <div class="buttons">
      <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
      <button class="btn" type="button" id="programmeClearButton" name="programmeClearButton" value="clear">Clear</button>
      </#if>    
      <button class="btn" type="button" id="programmeCloseButton" name="programmeCloseButton">Close</button>
      <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
      <button class="btn btn-primary" type="button" id="programmeSaveButton">Save</button>
      </#if>    
    </div>
  
  </form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/programme.js'/>"></script>
