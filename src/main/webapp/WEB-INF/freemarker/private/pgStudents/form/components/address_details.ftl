<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />
  	
  	<a name="address-details"></a>
	<h2 id="address-H2" class="empty open">
		<span class="left"></span><span class="right"></span><span class="status"></span>
	    Address<em>*</em>	    
	</h2>
	
	<div>
	
        	<form>
				 	<div id="addr-info-bar-div" class="section-info-bar">
						<div class="row">
							<span id="addr-info-bar-span" class="info-text">
								<@spring.message 'addressDetails.sectionInfo'/> 
							</span>
						</div>
					</div>	
        	
        		
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
                      		<textarea id="currentAddressLocation" class="max" rows="6" cols="80" maxlength='2000'>${(addressSectionDTO.currentAddressLocation?html)!}</textarea>
                      			
                				  
				             <#else>
                      		    <textarea readonly="readonly" id="currentAddressLocation" class="max" rows="6" cols="80">${(addressSectionDTO.currentAddressLocation?html)!}</textarea>
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
                            <textarea id="contactAddressLocation" class="max" rows="6" cols="80" maxlength='2000'
                            <#if addressSectionDTO.sameAddress>
                                          disabled="disabled"
                                </#if>
                            >${(addressSectionDTO.contactAddressLocation?html)!}</textarea>
                            	        	 
                          
                            <#else>
                                <textarea readonly="readonly" id="contactAddressLocation" class="max" rows="6" cols="80"
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
		       <div class="row-group terms-box">
					<div class="row">
						<span class="terms-label">
							I understand that in accepting this declaration I am confirming
							that the information contained in this section is true and accurate. 
							I am aware that any subsequent offer of study may be retracted at any time
							if any of the information contained is found to be misleading or false.
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
               		<a class="button" type="button" id="addressCancelButton" name="addressCancelButton">Clear</a>
               		<button class="blue" type="button" id="addressCloseButton" name="addressCloseButton">Close</button>
                  	<button class="blue" type="button" id="addressSaveAndAddButton" name="addressSaveAndAddButton">Save</button>
                <#else>
                    <a id="addressCloseButton" class="button blue">Close</a>  	
                </#if>  	
                </div>

			</form>
	</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>

<#--
<@spring.bind "addressSectionDTO.*" /> 
 
<#if (errorCode?? && errorCode=='false') || (message?? && message='close' && !spring.status.errorMessages?has_content)>
<script type="text/javascript">
	$(document).ready(function(){
		$('#address-H2').trigger('click');
	});
</script>
</#if>
-->