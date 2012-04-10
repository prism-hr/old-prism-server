<#import "/spring.ftl" as spring />

  	
	<h2 id="address-H2" class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
	    Address<em>*</em>	    
	</h2>
	
	<div>
	
        	<form>
				
				<input type="hidden" id="currentAddressId" name="currentAddressId" value="${model.address.currentAddressId!}"/>
				<input type="hidden" id="contactAddressId" name="contactAddressId" value="${model.address.contactAddressId!}"/>
            	<div>
            	    <#if model.hasError('numberOfAddresses')>            
            	       <div class="row">
                         <span class="invalid"><@spring.message  model.result.getFieldError('numberOfAddresses').code /></span><br/>
                       </div>                             
                    </#if>
            	
                	<h3>Address</h3>
                  
                  	<!-- Address body -->
                  	<div class="row">
                    	<span class="plain-label">Current Address<em>*</em></span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	   <#if !model.applicationForm.isSubmitted()>
                      		<textarea id="currentAddressLocation" class="max" rows="6" cols="80" >${(model.address.currentAddressLocation?html)!}</textarea>
							
                                <#if model.hasError('currentAddressLocation')>                           
                            	   <span class="invalid"><@spring.message  model.result.getFieldError('currentAddressLocation').code /></span>                           
                                </#if>
                            <#else>
                      		    <textarea readonly="readonly" id="currentAddressLocation" class="max" rows="6" cols="80">${(model.address.currentAddressLocation?html)!}</textarea>
                            </#if>
                    	</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="plain-label">Country<em>*</em></span>
	                    <div class="field">
	                      	
	                      	<select class="full" name="currentAddressCountry" id="currentAddressCountry"
	                      	<#if model.applicationForm.isSubmitted()>
                                            disabled="disabled"
                            </#if>>
                            <option value="">Select...</option>
                            	<#list model.countries as country>
                                	<option value="${country.id?string('#######')}" <#if model.address.currentAddressCountry?? && model.address.currentAddressCountry == country.id> selected="selected"</#if>>${country.name?html}</option>               
                            	</#list>
                            </select>
                            
                            <#if model.hasError('currentAddressCountry')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('currentAddressCountry').code /></span>                           
                            </#if>
	                      	
						</div>
					</div>
					
					<!-- Address body -->
                    <div class="row">
                        <span class="plain-label">Contact Address<em>*</em></span>
                        <span class="hint"></span>
                       
                       <div class="field">
                            <span class="label">Is this the same as your current address?</span>
                            <input type="checkbox" name="sameAddressCB" id="sameAddressCB"
                            <#if model.address.sameAddress??>
                                            checked="checked"
                                        </#if> 
                            <#if model.applicationForm.isSubmitted()>
                                          disabled="disabled"
                                </#if>
                            />
                            <input type="hidden" name="sameAddress" id="sameAddress" value="${(model.address.sameAddress?html)!}"/>
                       </div>
                    </div>
                    
                    <p></p>
                        
                    <div class="row">
                        <div class="field">
                           <#if !model.applicationForm.isSubmitted()>
                            <textarea id="contactAddressLocation" class="max" rows="6" cols="80" 
                            <#if model.address.sameAddress??>
                                          disabled="disabled"
                                </#if>
                            >${(model.address.contactAddressLocation?html)!}</textarea>
                           
                           <#if model.hasError('contactAddressLocation')>                           
                                   <span class="invalid"><@spring.message  model.result.getFieldError('contactAddressLocation').code /></span>                           
                                </#if>
                            <#else>
                                <textarea readonly="readonly" id="contactAddressLocation" class="max" rows="6" cols="80"
                                <#if model.address.sameAddress??>
                                          disabled="disabled"
                                </#if>
                                >${(model.address.contactAddressLocation?html)!}</textarea>
                            </#if>
                        </div>
                    </div>
                                        <!-- Country -->
                    <div class="row">
                        <span class="plain-label">Country<em>*</em></span>
                        <div class="field">
                            
                            <select class="full" name="contactAddressCountry" id="contactAddressCountry"
                            <#if model.applicationForm.isSubmitted() || model.address.sameAddress??>
                                            disabled="disabled"
                            </#if>>
                            <option value="">Select...</option>
                                <#list model.countries as country>
                                    <option value="${country.id?string('#######')}" <#if model.address.contactAddressCountry?? && model.address.contactAddressCountry == country.id> selected="selected"</#if>>${country.name?html}</option>               
                                </#list>
                            </select>
                            <#if model.hasError('contactAddressCountry')>                           
                                <span class="invalid"><@spring.message  model.result.getFieldError('contactAddressCountry').code /></span>                           
                            </#if>
                        </div>
                    </div>
					
				</div>

                <div class="buttons">
                 <#if !model.applicationForm.isSubmitted()>
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

<#if (model.result?? && model.result.hasErrors() ) ||  add?? >

<#else>
<script type="text/javascript">
	$(document).ready(function(){
		$('#address-H2').trigger('click');
	});
</script>

</#if>

