<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />

<a name="address-details"></a>
<h2 id="address-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
	Address<em>*</em>	    
</h2>

<div>

	<form>
	
		<#if errorCode?? && errorCode=="true">
		<div class="section-error-bar">
			<span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>             	
			<span class="invalid-info-text">
			<@spring.message 'addressDetails.sectionInfo'/> 
			</span>
		</div>
		<#else>
		<div id="addr-info-bar-div" class="section-info-bar">
			<@spring.message 'addressDetails.sectionInfo'/> 
		</div>	
		</#if>
	
		<div class="row-group">
			<div class="row">
				<label class="group-heading-label">Current Address</label>
			</div>
		
			<!-- Address body -->
			<div class="row">
				<span class="plain-label">Address<em>*</em></span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.currentAddress.address'/>"></span>	
				<div class="field">
					<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
					<textarea id="currentAddressLocation" class="max" rows="5" cols="80" maxlength='2000'>${(addressSectionDTO.currentAddressLocation?html)!}</textarea>
					<#else>
					<textarea readonly="readonly" id="currentAddressLocation" class="max" rows="5" cols="80">${(addressSectionDTO.currentAddressLocation?html)!}</textarea>
					</#if>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.currentAddressLocation" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="currentAddressInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
		
			<!-- Country -->
			<div class="row">
				<span class="plain-label">Country<em>*</em></span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.CurrentAddress.country'/>"></span>	
				<div class="field">
					<select class="full" name="currentAddressCountry" id="currentAddressCountry"
						<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
							disabled="disabled"
						</#if>>
						<option value="">Select...</option>
						<#list countries as country>
						<option value="${encrypter.encrypt(country.id)}" <#if addressSectionDTO.currentAddressCountry?? && addressSectionDTO.currentAddressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
						</#list>
					</select>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.currentAddressCountry" />
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="currentAddressCountryInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
		
		</div> 
	
		<div class="row-group">	
			<div class="row">
				<label class="group-heading-label">Contact Address</label>
			</div>
			
			<!-- Address body -->
			<div class="row">
				<span class="plain-label">Is this the same as your current address?</span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.ContactAddress.sameAsCurrentAddress'/>"></span>	
				<div class="field">
					<input type="checkbox" name="sameAddressCB" id="sameAddressCB"
					<#if addressSectionDTO.sameAddress>
						checked="checked"
					</#if> 
					<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
					disabled="disabled"
					</#if>
					/>                           
				</div>
			</div>
		
			<p></p>
		
			<div class="row">
				<span id="add-two-lb" class="plain-label">Address<em id="add-two-em">*</em></span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.ContactAddress.address'/>"></span>	
				
				<div class="field">
					<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
					<textarea id="contactAddressLocation" class="max" rows="5" cols="80" maxlength='2000'
					<#if addressSectionDTO.sameAddress>
					disabled="disabled"
					</#if>
					>${(addressSectionDTO.contactAddressLocation?html)!}</textarea>
					<#else>
					<textarea readonly="readonly" id="contactAddressLocation" class="max" rows="5" cols="80"
					<#if addressSectionDTO.sameAddress>
					disabled="disabled"
					</#if>
					>${(addressSectionDTO.contactAddressLocation?html)!}</textarea>
					</#if>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.contactAddressLocation" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="contactAddressLocationInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
		
			<!-- Country -->
			<div class="row">
				<span id="country-two-lb" class="plain-label">Country<em id="country-two-em">*</em></span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.ContactAddress.country'/>"></span>	
				<div class="field">
					<select class="full" name="contactAddressCountry" id="contactAddressCountry"
						<#if (applicationForm.isDecided()  || applicationForm.isWithdrawn()) || (addressSectionDTO.sameAddress)>
							disabled="disabled"
						</#if>>
						<option value="">Select...</option>
						<#list countries as country>
						<option value="${encrypter.encrypt(country.id)}" <#if addressSectionDTO.contactAddressCountry?? && addressSectionDTO.contactAddressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
						</#list>
					</select>            
				</div>
			</div>
		
			<@spring.bind "addressSectionDTO.contactAddressCountry" />
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="contactAddressCountryInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
		
		</div>
		
		<#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
			<@spring.bind "addressSectionDTO.acceptedTerms" />
		       	<#if spring.status.errorMessages?size &gt; 0>        
				    <div class="row-group terms-box invalid" >
		
		      	<#else>
		    		<div class="row-group terms-box" >
		     	 </#if>
	
			<div class="row">
				<span class="terms-label">
					Confirm that the information that you have provided in this section is true 
					and correct. Failure to provide true and correct information may result in a 
					subsequent offer of study being withdrawn.				
				</span>
				<div class="terms-field">
					<input type="checkbox" name="acceptTermsADCB" id="acceptTermsADCB"/>
				</div>
				<input type="hidden" name="acceptTermsADValue" id="acceptTermsADValue"/>
			</div>	        
	
		</div>
		</#if>  
		
		<div class="buttons">
			<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
			<button class="clear" type="button" id="addressClearButton" name="addressClearButton">Clear</button>
			<button class="blue" type="button" id="addressCloseButton" name="addressCloseButton">Close</button>
			<button class="blue" type="button" id="addressSaveAndAddButton" name="addressSaveAndAddButton">Save</button>
			<#else>
			<button id="addressCloseButton" type="button" class="blue">Close</button>
			</#if>  	
		</div>
	
	</form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>
