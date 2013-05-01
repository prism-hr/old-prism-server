<#import "/spring.ftl" as spring /> <#assign errorCode = RequestParameters.errorCode! /> <#setting locale = "en_US"> <a name="address-details"></a>
<h2 id="address-H2" class="empty open"> <span class="left"></span><span class="right"></span><span class="status"></span> Address<em>*</em> </h2>
<div>
  <form>
    <#if errorCode?? && errorCode=="true">
    <div class="alert alert-error"> 
    <i class="icon-warning-sign" data-desc="Please provide all mandatory fields in this section."></i> 
      <@spring.message 'addressDetails.sectionInfo'/>
    </div>
    <#else>
    <div class="alert alert-info"> <i class="icon-info-sign"></i>
      <@spring.message 'addressDetails.sectionInfo'/>
    </div>
    </#if>
    <div class="row-group">
      <h3>Current Address</h3>
      
      <!-- Address body -->
      <div class="row">
        <label class="plain-label" for="currentAddress1">House name / number & street<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.house'/>"></span>
        <div class="field"> <input id="currentAddress1" class="max" type="text" value="${(addressSectionDTO.currentAddress1?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> > <input id="currentAddress2" class="max" type="text" value="${(addressSectionDTO.currentAddress2?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> >
          <@spring.bind "addressSectionDTO.currentAddress1" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list>
          <@spring.bind "addressSectionDTO.currentAddress2" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label class="plain-label" for="currentAddress3">Town / city / suburb<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.town'/>"></span>
        <div class="field"> <input id="currentAddress3" class="full" type="text" value="${(addressSectionDTO.currentAddress3?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> >
          <@spring.bind "addressSectionDTO.currentAddress3" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label class="plain-label" for="currentAddress4">State / county / region</label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.state'/>"></span>
        <div class="field"> <input id="currentAddress4" class="full" type="text" value="${(addressSectionDTO.currentAddress4?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> >
          <@spring.bind "addressSectionDTO.currentAddress4" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label class="plain-label" for="currentAddress5">Post / zip / area code</label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.zip'/>"></span>
        <div class="field"> <input id="currentAddress5" class="full" type="text" value="${(addressSectionDTO.currentAddress5?html)!}"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> >
          <@spring.bind "addressSectionDTO.currentAddress5" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Country -->
      <div class="row">
        <label class="plain-label" for="currentAddressCountry">Country<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.CurrentAddress.country'/>"></span>
        <div class="field"> <select class="full" name="currentAddressCountry" id="currentAddressCountry"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled" </#if>>
          <option value="">Select...</option>
          <#list countries as country> <option value="${encrypter.encrypt(country.id)}"<#if addressSectionDTO.currentAddressCountry?? && addressSectionDTO.currentAddressCountry.id == country.id> selected="selected"</#if>>
          ${country.name?html}
          </option>
          </#list>
          </select>
          <@spring.bind "addressSectionDTO.currentAddressCountry" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
    </div>
    <div class="row-group">
      <h3>Contact Address</h3>
      <!-- Address body -->
      <div class="row"> 
      <label class="plain-label" for="sameAddressCB">Is this the same as your current address?</label> 
      
      <span class="hint" data-desc="<@spring.message 'addressDetails.ContactAddress.sameAsCurrentAddress'/>"></span>
        <div class="field">
          <input type="checkbox" name="sameAddressCB" type="text" id="sameAddressCB"
          
 
          <#if addressSectionDTO.sameAddress> checked="checked" </#if> <#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled" </#if> /> </div>
      </div>
      <p></p>
      <div class="row"> <span id="add-two-lb-1" class="plain-label">House name / number & street<em id="add-two-em">*</em></span> <span class="hint" data-desc="<@spring.message 'addressDetails.house'/>"></span>
        <div class="field">
        <input id="contactAddress1" type="text" class="max" <#if addressSectionDTO.sameAddress> disabled="disabled" </#if> <#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> value="${(addressSectionDTO.contactAddress1?html)!}" /> </div>
      </div>
      <div class="row">
        <div class="field"> <input id="contactAddress2" type="text" class="max" <#if addressSectionDTO.sameAddress> disabled="disabled" </#if> <#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> value="${(addressSectionDTO.contactAddress2?html)!}" />
          <@spring.bind "addressSectionDTO.contactAddress1" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error" id="contactAddressLocationInvalid"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list>
          <@spring.bind "addressSectionDTO.contactAddress2" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error" id="contactAddressLocationInvalid"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label id="add-two-lb-2" class="plain-label" for="contactAddress3">Town / city / suburb<em id="add-two-em">*</em></label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.town'/>"></span>
        <div class="field"> <input id="contactAddress3" type="text" class="full" <#if addressSectionDTO.sameAddress> disabled="disabled" </#if> <#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> value="${(addressSectionDTO.contactAddress3?html)!}" />
          <@spring.bind "addressSectionDTO.contactAddress3" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error" id="contactAddressLocationInvalid"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label id="add-two-lb-3" class="plain-label" for="contactAddress4">State / county / region</label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.state'/>"></span>
        <div class="field"> <input id="contactAddress4" type="text" class="full" <#if addressSectionDTO.sameAddress> disabled="disabled" </#if> <#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> value="${(addressSectionDTO.contactAddress4?html)!}" />
          <@spring.bind "addressSectionDTO.contactAddress4" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error" id="contactAddressLocationInvalid"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label id="add-two-lb-4" class="plain-label" for="contactAddress5">Post / zip / area code</label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.zip'/>"></span>
        <div class="field"> <input id="contactAddress5" type="text" class="full" <#if addressSectionDTO.sameAddress> disabled="disabled" </#if> <#if applicationForm.isDecided() || applicationForm.isWithdrawn()> readonly="readonly" </#if> value="${(addressSectionDTO.contactAddress5?html)!}" />
          <@spring.bind "addressSectionDTO.contactAddress5" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error" id="contactAddressLocationInvalid"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Country -->
      <div class="row"> <span id="country-two-lb" class="plain-label">Country<em id="country-two-em">*</em></span> <span class="hint" data-desc="<@spring.message 'addressDetails.ContactAddress.country'/>"></span>
        <div class="field"> <select class="full" name="contactAddressCountry" id="contactAddressCountry"<#if (applicationForm.isDecided() || applicationForm.isWithdrawn()) || (addressSectionDTO.sameAddress)> disabled="disabled" </#if>>
          <option value="">Select...</option>
          <#list countries as country> <option value="${encrypter.encrypt(country.id)}"<#if addressSectionDTO.contactAddressCountry?? && addressSectionDTO.contactAddressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>
          </#list>
          </select>
          <@spring.bind "addressSectionDTO.contactAddressCountry" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error" id="contactAddressCountryInvalid"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
    </div>
    <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
    <@spring.bind "addressSectionDTO.acceptedTerms" />
    <#if spring.status.errorMessages?size &gt; 0>
   	  <div class="alert alert-error tac" >
      <#else>
      <div class="alert tac" >
    </#if>
      <div class="row"> 
      <label for="acceptTermsADCB" class="terms-label"> Confirm that the information that you have provided in this section is true and correct. Failure to provide true and correct information may result in a subsequent offer of study being withdrawn. </label>
        <div class="terms-field">
          <input type="checkbox" name="acceptTermsADCB" id="acceptTermsADCB" />
        </div>
        <input type="hidden" name="acceptTermsADValue" id="acceptTermsADValue" />
      </div>
    </div>
    </#if>
    <div class="buttons"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
      <button class="btn" type="button" id="addressClearButton" name="addressClearButton">Clear</button>
      <button class="btn" type="button" id="addressCloseButton" name="addressCloseButton">Close</button>
      <button class="btn btn-primary" type="button" id="addressSaveAndAddButton" name="addressSaveAndAddButton">Save</button>
      <#else>
      <button id="addressCloseButton" type="button" class="btn">Close</button>
      </#if> </div>
  </form>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script> 
