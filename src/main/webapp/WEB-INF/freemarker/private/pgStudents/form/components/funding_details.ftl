<#assign errorCode = RequestParameters.errorCode! />
<#if applicationForm.fundings?has_content>
  <#assign hasFundings = true>
<#else>
  <#assign hasFundings = false>
</#if>

<#import "/spring.ftl" as spring />
<#setting locale = "en_US"> <a name="funding-details"></a>
<h2 id="funding-H2" class="empty"> <span class="left"></span><span class="right"></span><span class="status"></span> Funding </h2>
<div style="display:none;"> <#if hasFundings>
  <table class="existing table table-condensed table-bordered">
    <colgroup>
    <col />
    <col style="width: 90px" />
    <col style="width: 36px" />
    <col style="width: 36px" />
    </colgroup>
    <tbody>
     <tr>
        <td colspan="4" class="scrollparent">
    	  <div class="scroll">
            <table class="table-striped table-hover">
                <colgroup>
                <col />
                <col style="width: 150px" />
                <col style="width: 30px" />
                <col style="width: 30px" />
                </colgroup>
            	<tbody>
                    <#list applicationForm.fundings as existingFunding>
                    <tr>
                      <td><#if existingFunding.document??> <a href="<@spring.url '/download'/>?documentId=${encrypter.encrypt(existingFunding.document.id)}" data-desc="Proof of Award" class="button-hint" target="_blank">
                        ${existingFunding.type.displayValue}
                        (&pound;
                        ${(existingFunding.value?html)!}) </a> <#else>
                        ${existingFunding.type.displayValue}
                        (&pound;
                        ${(existingFunding.value?html)!}) - no document!
                        </#if> </td>
                      <td>Awarded: <strong>${existingFunding.awardDate?string('dd MMM yyyy')}</strong></td>
                      <td><a name="editFundingLink" <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>data-desc="Edit" <#else>data-desc="Show"</#if> id="funding_
                        ${encrypter.encrypt(existingFunding.id)}
                        " class="button-edit button-hint">edit</a></td>
                      <td><#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> <a name="deleteFundingButton" data-desc="Delete" id="funding_${encrypter.encrypt(existingFunding.id)}" class="button-delete button-hint">delete</a> </#if> </td>
                   </tr>
                 </#list>
              </tbody>
           </table>
          </div>
        </td>
    </tr>
  </tbody>
    
  </table>
  </#if> 
  <!-- Non-rendering data -->
  
  <form>
    <input type="hidden" id="fundingId" name="fundingId" value="<#if funding?? && funding.id??>${(encrypter.encrypt(funding.id))!}</#if>"/>
    <#if errorCode?? && errorCode=="true">
    <div class="alert alert-error"> <i class="icon-warning-sign" data-desc="Please complete all of the mandatory fields in this section."></i>
      <@spring.message 'fundingDetails.sectionInfo'/>
    </div>
    <#else>
    <div class="alert alert-info"> <i class="icon-info-sign"></i>
      <@spring.message 'fundingDetails.sectionInfo'/>
    </div>
    </#if>
    <div class="row-group"> 
      
      <!-- Award type -->
      <div class="row">
        <label class="plain-label" for="fundingType">Funding Type<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'fundingDetails.award.type'/>"></span>
        <div class="field"> <select id="fundingType" name="fundingType" class="full"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>>
          <option value="">Select...</option>
          <#list fundingTypes as type> <option value="${type}"<#if funding.type?? && funding.type == type> selected="selected"</#if>>
          ${type.displayValue}
          </option>
          </#list>
          </select>
          <@spring.bind "funding.type" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Award description -->
      <div class="row">
        <label class="plain-label" for="fundingDescription">Description<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'fundingDetails.award.description'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <textarea id="fundingDescription" name="fundingDescription" class="max" cols="70" rows="6">${(funding.description?html)!}
</textarea>
          <#else>
          <textarea id="fundingDescription" name="fundingDescription" class="full" readonly>${(funding.description?html)!}
</textarea>
          </#if>
          <@spring.bind "funding.description" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Value of award -->
      <div class="row">
        <label class="plain-label" for="fundingValue">Value of Award (GBP)<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'fundingDetails.award.value'/>"></span>
        <div class="field"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
          <input id="fundingValue" name="fundingValue" class="full" type="text" value="${(funding.value?html)!}" placeholder="Numbers only" />
          <#else>
          <input id="fundingValue" readonly name="fundingValue" class="full" type="text" value="${(funding.value?html)!}" disabled="disabled" />
          </#if>
          <@spring.bind "funding.value" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Award date -->
      <div class="row">
        <label class="plain-label" for="fundingAwardDate">Award Date<em>*</em></label>
        <span class="hint"  data-desc="<@spring.message 'fundingDetails.award.awardDate'/>"></span>
        <div class="field"> <input id="fundingAwardDate" name="fundingAwardDate" class="half date" type="text" value="${(funding.awardDate?string('dd MMM yyyy'))!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if> />
          <@spring.bind "funding.awardDate" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Attachment / supporting document -->
      <div class="row">
        <label class="plain-label" for="fundingDocument">Proof of Award (PDF)<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'fundingDetails.award.proofOfAward'/>"></span>
        <div class="field<#if funding.document??> uploaded</#if>" id="fundingUploadFields"> 
       		 <div class="fileupload fileupload-new" data-provides="fileupload">
                <div class="input-append">
                  <div class="uneditable-input span4" > <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span> </div>
                  <span class="btn btn-file"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
                  <input id="fundingDocument" data-type="SUPPORTING_FUNDING" data-reference="Proof Of Award" class="full" type="file" name="file" value="" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>
 
                </span> </div>
              </div>

		
 		<ul id="fundingUploadedDocument" class="uploaded-files">
          <#if funding.document??>
           <li class="done">
              <span class="uploaded-file" name="supportingDocumentSpan">
                  <input type="hidden" class="file" id="document_SUPPORTING_FUNDING" value="${(encrypter.encrypt(funding.document.id))!}"/>
                   <a href="<@spring.url '/download'/>?documentId=${encrypter.encrypt(funding.document.id)}" data-desc="Proof of Award" class="uploaded-filename" target="_blank">
                  ${funding.document.fileName}
                  </a> 
                  <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> <a class="btn btn-danger delete" data-desc="Edit Proof Of Award"><i class="icon-trash icon-large"></i> Delete</a> 
				  </#if>
              </span>
              </li>
          </#if> 
        </ul> 
          <@spring.bind "funding.document" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> 
      <!-- Add another button -->
      <div class="row">
        <div class="field">
          <button type="button" id="addFundingButton" class="btn"><#if funding?? && funding.id??>Update<#else>Add</#if></button>
        </div>
      </div>
      </#if> </div>
    <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
    <@spring.bind "funding.acceptedTerms" />
    <#if spring.status.errorMessages?size &gt; 0>
    <div class="alert alert-error tac" >
      <#else>
        <div class="alert tac" >
      </#if>
      <div class="row"> 
      <label class="terms-label" for="acceptTermsFDCB"> Confirm that the information that you have provided in this section is true 
        and correct. Failure to provide true and correct information may result in a 
        subsequent offer of study being withdrawn. </label>
        <div class="terms-field">
          <input type="checkbox" name="acceptTermsFDCB" id="acceptTermsFDCB"/>
        </div>
        <input type="hidden" name="acceptTermsFDValue" id="acceptTermsFDValue"/>
      </div>
    </div>
    </#if>
    <div class="buttons"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
      <button class="btn" type="button" id="fundingClearButton" name="fundingClearButton">Clear</button>
      <button class="btn" type="button" id="fundingCloseButton" name="fundingCloseButton">Close</button>
      <button class="btn btn-primary" type="button" id="fundingSaveCloseButton" value="close">Save</button>
      <#else>
      <button type="button" id="fundingCloseButton" class="btn">Close</button>
      </#if> </div>
  </form>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/funding.js'/>"></script> 
