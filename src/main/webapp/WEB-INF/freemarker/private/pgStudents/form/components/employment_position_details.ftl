<#assign errorCode = RequestParameters.errorCode! /> <#if applicationForm.employmentPositions?has_content> <#assign hasEmploymentPositions = true> <#else> <#assign hasEmploymentPositions = false> </#if> <#setting locale = "en_US"> <#import "/spring.ftl" as spring /> <a name="position-details"></a>
<h2 id="position-H2" class="empty"> <span class="left"></span><span class="right"></span><span class="status"></span> Employment </h2>
<div style="display:none;"> <#if hasEmploymentPositions>
  <table class="existing table table-condensed table-bordered">
    <colgroup>
    <col />
    <col style="width: 220px">
    <col style="width: 36px">
    <col style="width: 36px">
    </colgroup>
    <thead>
      <tr>
        <th>Position</th>
        <th>Dates</th>
        <th>&nbsp;</th>
        <th>&nbsp;</th>
      </tr>
    </thead>
    <tbody>
     <tr>
        <td colspan="4" class="scrollparent">
    	  <div class="scroll">
            <table class="table-striped table-hover">
                <colgroup>
                <col />
                <col style="width: 220px" />
                <col style="width: 30px" />
                <col style="width: 30px" />
                </colgroup>
            	<tbody>
                <#list applicationForm.employmentPositions as position>
                <tr>
                  <td>${(position.position?html)!}
                    ${(position.employerName?html)!}
                    ${(position.employerCountry.name)!}</td>
                  <td>${(position.startDate?string('dd MMM yyyy'))!}
                    -
                    ${(position.endDate?string('dd MMM yyyy'))!"Ongoing"}</td>
                  <td><a name="positionEditButton"<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>data-desc="Edit" <#else>data-desc="Show"</#if> id="position_
                    ${encrypter.encrypt(position.id)}
                    " class="button-edit button-hint">edit</a></td>
                  <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
                  <td><a name="deleteEmploymentButton" data-desc="Delete" id="position_${encrypter.encrypt(position.id)}" class="button-delete button-hint">delete</a></td>
                  <#else>
                  <td></td>
                  <td></td>
                  </#if> </tr>
                </#list>
      		    </tbody>
               </table>
              </div>
            </td>
          </tr>
        </tbody>
     </table>
  </#if>
  <input type="hidden" id="positionId" name="positionId" value="<#if employmentPosition?? && employmentPosition.id??>${(encrypter.encrypt(employmentPosition.id))!}</#if>" />
  <form>
    <#if errorCode?? && errorCode=="true">
    <div class="alert alert-error"> <i class="icon-warning-sign" data-desc="Please complete all of the mandatory fields in this section."></i>
      <@spring.message 'employmentDetails.sectionInfo'/>
    </div>
    <#else>
    <div class="alert alert-info"> <i class="icon-info-sign"></i>
      <@spring.message 'employmentDetails.sectionInfo'/>
    </div>
    </#if>
    <div id="employmentForm">
    <div class="row-group"> 
      
      <!-- Employer (company name) -->
      <div class="row">
        <label class="plain-label" for="position_employer_name">Employer Name<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerName'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <input class="full" type="text" id="position_employer_name" name="position_employer_name" value="${(employmentPosition.employerName?html)!}" placeholder="Provider of employment" />
          <#else>
          <input readonly class="full" type="text" id="position_employer_name" name="position_employer_name" value="${(employmentPosition.employerName?html)!}" placeholder="Provider of employment" />
          </#if>
          <@spring.bind "employmentPosition.employerName" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
    </div>
    <div class="row-group">
      <div class="row">
        <label class="group-heading-label">Address</label>
      </div>
      
      <!-- Address -->
      <div class="row">
        <label class="plain-label" for="position_employer_address1">House name / number & street<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.house'/>"></span>
        <div class="field"> <input id="position_employer_address1" class="max"  type="text" name="position_employer_address1" value="${(employmentPosition.employerAddress.address1?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> /> <input id="position_employer_address2" class="max"  type="text" name="position_employer_address2" value="${(employmentPosition.employerAddress.address2?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> />
          <@spring.bind "employmentPosition.employerAddress.address1" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list>
          <@spring.bind "employmentPosition.employerAddress.address2" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label class="plain-label" for="position_employer_address3">Town / city / suburb<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.town'/>"></span>
        <div class="field"> <input id="position_employer_address3" class="full" type="text" name="position_employer_address3" value="${(employmentPosition.employerAddress.address3?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> />
          <@spring.bind "employmentPosition.employerAddress.address3" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label class="plain-label" for="position_employer_address4"> State / county / region</label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.state'/>"></span>
        <div class="field"> <input id="position_employer_address4"  class="full" type="text" name="position_employer_address4" value="${(employmentPosition.employerAddress.address4?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> />
          <@spring.bind "employmentPosition.employerAddress.address4" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label class="plain-label" for="position_employer_address5">Post / zip / area code</label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.zip'/>"></span>
        <div class="field"> <input id="position_employer_address5" class="full" type="text" name="position_employer_address5" value="${(employmentPosition.employerAddress.address5?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> />
          <@spring.bind "employmentPosition.employerAddress.address5" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label class="plain-label" for="position_country">Country<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerCountry'/>"></span>
        <div class="field"> <select class="full" id="position_country" name="position_country"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>>
          <option value="">Select...</option>
          <#list domiciles as domicile> <option value="${encrypter.encrypt(domicile.id)}"<#if employmentPosition.employerAddress.domicile?? && employmentPosition.employerAddress.domicile.id == domicile.id> selected="selected"</#if>>
          ${domicile.name?html}
          </option>
          </#list>
          </select>
          <@spring.bind "employmentPosition.employerAddress.domicile" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
    </div>
    <div class="row-group"> 
      
      <!-- Position -->
      <div class="row">
        <label class="plain-label" for="position_title">Position<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'employmentDetails.position.position'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <input class="full" type="text" id="position_title" name="position_title" value="${(employmentPosition.position?html)!}" placeholder="Title of position" />
          <#else>
          <input readonly class="full" type="text" id="position_title" name="position_title" value="${(employmentPosition.position?html)!}" placeholder="Title of position" />
          </#if>
          <@spring.bind "employmentPosition.position" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Remit (job description) -->
      <div class="row">
        <label class="plain-label" for="position_remit">Roles and Responsibilities<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'employmentDetails.position.remit'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <textarea cols="80" rows="5" class="max counter" id="position_remit" name="position_remit" placeholder="Summary of responsibilities">${(employmentPosition.remit?html)!}
</textarea>
          <#else>
          <textarea cols="80" rows="5" class="max" id="position_remit" name="position_remit" placeholder="Summary of responsibilities">${(employmentPosition.remit?html)!}</textarea>
          </#if>
          <@spring.bind "employmentPosition.remit" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Start date -->
      <div class="row">
        <label class="plain-label" for="position_startDate">Start Date<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'employmentDetails.position.startDate'/>"></span>
        <div class="field"> <input class="half date" type="text" id="position_startDate" name="position_startDate" value="${(employmentPosition.startDate?string('dd MMM yyyy'))!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>
          <@spring.bind "employmentPosition.startDate" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
    </div>
    <div class="row-group">
      <div class="row">
        <label class="plain-label" for="current">Is this your current position?</label>
        <span class="hint" data-desc="<@spring.message 'employmentDetails.position.isOngoing'/>"></span>
        <div class="field"> <input type="checkbox" name="current" id="current"<#if employmentPosition.current> checked ="checked"</#if><#if (applicationForm.isDecided() || applicationForm.isWithdrawn())>disabled="disabled"</#if>/> </div>
      </div>
      <!-- End date -->
      <div class="row">
        <label for="position_endDate" id="posi-end-date-lb" class="plain-label<#if employmentPosition.current> grey-label</#if>">End Date<#if !employmentPosition.current><em>*</em></#if> </label>
        <span class="hint" data-desc="<@spring.message 'employmentDetails.position.endDate'/>"></span>
        <div class="field" id="endDateField"> <input class="half date" type="text" id="position_endDate" name="position_endDate" value="${(employmentPosition.endDate?string('dd MMM yyyy'))!}"<#if employmentPosition.current || applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>/>
          <@spring.bind "employmentPosition.endDate" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error" id="position-enddate-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> 
      <!-- Add another button -->
      <div class="row">
        <div class="field">
          <button type="button" id="addPosisionButton" class="btn"><#if employmentPosition?? && employmentPosition.id??>Update<#else>Add</#if></button>
        </div>
      </div>
      </#if> </div>
    <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
    <@spring.bind "employmentPosition.acceptedTerms" />
    <#if spring.status.errorMessages?size &gt; 0>
         <div class="alert alert-error tac" >
    <#else>
        <div class="alert tac" >
    </#if>
        <div class="row"> 
        <label for="acceptTermsEPCB" class="terms-label"> Confirm that the information that you have provided in this section is true and correct. Failure to provide true and correct information may result in a subsequent offer of study being withdrawn. </label>
          <div class="terms-field">
            <input type="checkbox" name="acceptTermsEPCB" id="acceptTermsEPCB" />
          </div>
          <input type="hidden" name="acceptTermsEPValue" id="acceptTermsEPValue" />
        </div>
      </div>
      </#if>
      <div class="buttons"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
        <button class="btn" type="button" id="positionclearButton" name="positionclearButton">Clear</button>
        <button class="btn" type="button" id="positionCloseButton" name="positionCloseButton">Close</button>
        <button class="btn btn-primary" type="button" value="add" id="positionSaveAndCloseButton" name="positionSaveAndCloseButton">Save</button>
        <#else>
        <button type="button" id="positionCloseButton" class="btn">Close</button>
        </#if> </div>
    </div>
  </form>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/employmentPosition.js'/>"></script> 