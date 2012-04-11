<#import "/spring.ftl" as spring />

  	
	<h2 id="address-H2" class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
	    Address<em>*</em>	    
	</h2>
	
	<div>
	
        	<form>		
				
            	<div>            	 
            	
                	<h3>Address</h3>
                  
                  	<!-- Address body -->
                  	<div class="row">
                    	<span class="plain-label">Current Address<em>*</em></span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	   <#if !applicationForm.isSubmitted()>
                      		<textarea id="currentAddressLocation" class="max" rows="6" cols="80" >${(addressSectionDTO.currentAddressLocation?html)!}</textarea>
                      			<@spring.bind "addressSectionDTO.currentAddressLocation" /> 
                				<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>  
				             <#else>
                      		    <textarea readonly="readonly" id="currentAddressLocation" class="max" rows="6" cols="80">${(addressSectionDTO.currentAddressLocation?html)!}</textarea>
                            </#if>
                    	</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="plain-label">Country<em>*</em></span>
	                    <div class="field">
	                      	
	                      	<select class="full" name="currentAddressCountry" id="currentAddressCountry"
	                      	<#if applicationForm.isSubmitted()>
                                            disabled="disabled"
                            </#if>>
                            <option value="">Select...</option>
                            	<#list countries as country>
                                	<option value="${country.id?string('#######')}" <#if addressSectionDTO.currentAddressCountry?? && addressSectionDTO.currentAddressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
                            	</#list>
                            </select>
                      	        	<@spring.bind "addressSectionDTO.currentAddressCountry" /> 
                				<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>                	
						</div>
					</div>
					
					<!-- Address body -->
                    <div class="row">
                        <span class="plain-label">Contact Address<em>*</em></span>
                        <span class="hint"></span>
                       
                       <div class="field">
                            <span class="label">Is this the same as your current address?</span>
                            <input type="checkbox" name="sameAddressCB" id="sameAddressCB"
                            <#if addressSectionDTO.sameAddress>
                                            checked="checked"
                            </#if> 
                            <#if applicationForm.isSubmitted()>
                                          disabled="disabled"
                                </#if>
                            />                           
                       </div>
                    </div>
                    
                    <p></p>
                        
                    <div class="row">
                        <div class="field">
                           <#if !applicationForm.isSubmitted()>
                            <textarea id="contactAddressLocation" class="max" rows="6" cols="80" 
                            <#if addressSectionDTO.sameAddress>
                                          disabled="disabled"
                                </#if>
                            >${(addressSectionDTO.contactAddressLocation?html)!}</textarea>
                            	        	<@spring.bind "addressSectionDTO.contactAddressLocation" /> 
                				<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>         
                          
                            <#else>
                                <textarea readonly="readonly" id="contactAddressLocation" class="max" rows="6" cols="80"
                                <#if addressSectionDTO.sameAddress>
                                          disabled="disabled"
                                </#if>
                                >${(addressSectionDTO.contactAddressLocation?html)!}</textarea>
                            </#if>
                        </div>
                    </div>
                                        <!-- Country -->
                    <div class="row">
                        <span class="plain-label">Country<em>*</em></span>
                        <div class="field">
                            
                            <select class="full" name="contactAddressCountry" id="contactAddressCountry"
                            <#if applicationForm.isSubmitted() || (addressSectionDTO.sameAddress)>
                                            disabled="disabled"
                            </#if>>
                            <option value="">Select...</option>
                                <#list countries as country>
                                    <option value="${country.id?string('#######')}" <#if addressSectionDTO.contactAddressCountry?? && addressSectionDTO.contactAddressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
                                </#list>
                            </select>            
                            	        	<@spring.bind "addressSectionDTO.contactAddressCountry" /> 
                				<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>                    
                        </div>
                    </div>
					
				</div>

                <div class="buttons">
                 <#if !applicationForm.isSubmitted()>
               		<a class="button" type="button" id="addressCancelButton" name="addressCancelButton">Cancel</a>
               		<button class="blue" type="button" id="addressCloseButton" name="addressCloseButton">Close</button>
                  	<button class="blue" type="button" id="addressSaveAndAddButton" name="addressSaveAndAddButton">Save</button>
                <#else>
                    <a id="addressCloseButton" class="button blue">Close</a>  	
                </#if>  	
                </div>

			</form>
	</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>

<@spring.bind "addressSectionDTO.*" /> 
 
<#if !message?? || (!spring.status.errorMessages?has_content && (message=='close'))  >
<script type="text/javascript">
	$(document).ready(function(){
		$('#address-H2').trigger('click');
	});
</script>
</#if>
