<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />
<#setting locale = "en_US">
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
				<span class="plain-label">House name / number & street<em>*</em></span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.house'/>"></span>	
				<div class="field">
					<input id="currentAddress1" class="max" value="${(addressSectionDTO.currentAddress1?html)!}"
						<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
						readonly="readonly"
						</#if>
					>
				</div>
			</div>
			
			<div class="row">
				<div class="field">
					<input id="currentAddress2" class="max" value="${(addressSectionDTO.currentAddress2?html)!}"
						<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
						readonly="readonly"
						</#if>
					>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.currentAddress1" /> 
            <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span id="currentAddressInvalid" class="invalid">${error}</span>
                </div>
            </div>
            </#list>
			<@spring.bind "addressSectionDTO.currentAddress2" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="currentAddressInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
			
			<div class="row">
				<span class="plain-label">Town / city / suburb<em>*</em></span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.town'/>"></span>	
				<div class="field">
					<input id="currentAddress3" class="max" value="${(addressSectionDTO.currentAddress3?html)!}"
						<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
						readonly="readonly"
						</#if>
					>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.currentAddress3" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="currentAddressInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
			
			<div class="row">
				<span class="plain-label">State / county / region</span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.state'/>"></span>	
				<div class="field">
					<input id="currentAddress4" class="max" value="${(addressSectionDTO.currentAddress4?html)!}"
						<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
						readonly="readonly"
						</#if>
					>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.currentAddress4" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="currentAddressInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
			
			<div class="row">
				<span class="plain-label">Post / zip / area code</span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.zip'/>"></span>	
				<div class="field">
					<input id="currentAddress5" class="max" value="${(addressSectionDTO.currentAddress5?html)!}"
						<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
						readonly="readonly"
						</#if>
					>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.currentAddress5" /> 
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
				<span id="add-two-lb" class="plain-label">House name / number & street<em id="add-two-em">*</em></span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.house'/>"></span>	
				
				<div class="field">
					<input id="contactAddress1" class="max"
					<#if addressSectionDTO.sameAddress>
					disabled="disabled"
					</#if>
					<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
					readonly="readonly"
					</#if>
					value="${(addressSectionDTO.contactAddress1?html)!}"
					/>
				</div>
			</div>

			<div class="row">
				<div class="field">
					<input id="contactAddress2" class="max"
					<#if addressSectionDTO.sameAddress>
					disabled="disabled"
					</#if>
					<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
					readonly="readonly"
					</#if>
					value="${(addressSectionDTO.contactAddress2?html)!}"
					/>
				</div>
			</div>
            <@spring.bind "addressSectionDTO.contactAddress1" /> 
            <#list spring.status.errorMessages as error>
            <div class="row">
                <div class="field">
                    <span id="contactAddressLocationInvalid" class="invalid">${error}</span>
                </div>
            </div>
            </#list>
			<@spring.bind "addressSectionDTO.contactAddress2" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="contactAddressLocationInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
			
			<div class="row">
				<span id="add-two-lb" class="plain-label">Town / city / suburb<em id="add-two-em">*</em></span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.town'/>"></span>	
				
				<div class="field">
					<input id="contactAddress3" class="max"
					<#if addressSectionDTO.sameAddress>
					disabled="disabled"
					</#if>
					<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
					readonly="readonly"
					</#if>
					value="${(addressSectionDTO.contactAddress3?html)!}"
					/>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.contactAddress3" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="contactAddressLocationInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
			
			<div class="row">
				<span id="add-two-lb" class="plain-label">State / county / region</span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.state'/>"></span>	
				
				<div class="field">
					<input id="contactAddress4" class="max"
					<#if addressSectionDTO.sameAddress>
					disabled="disabled"
					</#if>
					<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
					readonly="readonly"
					</#if>
					value="${(addressSectionDTO.contactAddress4?html)!}"
					/>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.contactAddress4" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span id="contactAddressLocationInvalid" class="invalid">${error}</span>
				</div>
			</div>
			</#list>
			
			<div class="row">
				<span id="add-two-lb" class="plain-label">Post / zip / area code</span>
				<span class="hint" data-desc="<@spring.message 'addressDetails.zip'/>"></span>	
				
				<div class="field">
					<input id="contactAddress5" class="max"
					<#if addressSectionDTO.sameAddress>
					disabled="disabled"
					</#if>
					<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
					readonly="readonly"
					</#if>
					value="${(addressSectionDTO.contactAddress5?html)!}"
					/>
				</div>
			</div>
			<@spring.bind "addressSectionDTO.contactAddress5" /> 
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
