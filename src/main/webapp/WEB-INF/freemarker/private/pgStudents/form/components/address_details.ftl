<#import "/spring.ftl" as spring />

  	
	<h2 id="address-H2" class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
	    Address<em>*</em>	    
	</h2>
	
	<div>
	
        	<form>
				
				<input type="hidden" id="addressId" name="addressId"/>
            	<div>
            	           <#if model.hasError('numberOfAddresses')>            
            	           <div class="row">
                                <span class="invalid"><@spring.message  model.result.getFieldError('numberOfAddresses').code /></span><br/>
                           </div>                             
                           </#if>
            	
                	<h3>Address</h3>
                  
                  	<!-- Address body -->
                  	<div class="row">
                    	<span class="label">Current Address<em>*</em></span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	   <#if !model.applicationForm.isSubmitted()>
                      		<textarea id="currentAddressLocation" class="max" rows="6" cols="80" >${(model.address.addressLocation?html)!}</textarea>
							
                                <#if model.hasError('addressLocation')>                           
                            	   <span class="invalid"><@spring.message  model.result.getFieldError('addressLocation').code /></span>                           
                                </#if>
                            <#else>
                      		    <textarea readonly="readonly" id="currentAddressLocation" class="max" rows="6" cols="80" 
                                                    value="${(model.address.addressLocation?html)!}"></textarea>
                            </#if>
                    	</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="label">Country<em>*</em></span>
	                    <div class="field">
	                      	
	                      	<select class="full" name="currentAddressCountry" id="currentAddressCountry"
	                      	<#if model.applicationForm.isSubmitted()>
                                            disabled="disabled"
                            </#if>>
                            <option value="">Select...</option>
                            	<#list model.countries as country>
                                	<option value="${country.id?string('#######')}" <#if model.address.addressCountry?? && model.address.addressCountry == country.id> selected="selected"</#if>>${country.name?html}</option>               
                            	</#list>
                            </select>
                            
                            <#if model.hasError('addressCountry')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressCountry').code /></span>                           
                            </#if>
	                      	
						</div>
					</div>
					
					<!-- Address body -->
                    <div class="row">
                        <span class="label">Contact Address<em>*</em></span>
                        <span class="hint"></span>
                       
                       <div class="field">
                            <input type="checkbox" name="sameAddress" id="sameAddress"/>
                            <span class="label">Is this the same as your current address?</span>
                       </div>
                    </div>
                    
                    <p></p>
                        
                        <div class="field">
                           <#if !model.applicationForm.isSubmitted()>
                            <textarea id="contactAddressLocation" class="max" rows="6" cols="80" ></textarea>
                            <#else>
                                <textarea readonly="readonly" id="contactAddressLocation" class="max" rows="6" cols="80" 
                                                    value=""></textarea>
                            </#if>
                        </div>
                    
                                        <!-- Country -->
                    <div class="row">
                        <span class="label">Country<em>*</em></span>
                        <div class="field">
                            
                            <select class="full" name="contactAddressCountry" id="contactAddressCountry"
                            <#if model.applicationForm.isSubmitted()>
                                            disabled="disabled"
                            </#if>>
                            <option value="">Select...</option>
                                <#list model.countries as country>
                                    <option value="${country.id?string('#######')}" <#if model.address.addressCountry?? && model.address.addressCountry == country.id> selected="selected"</#if>>${country.name?html}</option>               
                                </#list>
                            </select>
                            
                        </div>
                    </div>
					
				</div>

                <div class="buttons">
                 <#if !model.applicationForm.isSubmitted()>
               		<a class="button" type="button" id="addressCancelButton" name="addressCancelButton">Cancel</a>
               		<button class="blue" type="button" id="addressCloseButton" name="addressCloseButton">Close</button>
                  	<button class="blue" type="button" id="addressSaveAndCloseButton" name="addressSaveAndCloseButton">Save and Close</button>
                  	<button class="blue" type="button" id="addressSaveAndAddButton" name="addressSaveAndAddButton">Save and Add</button>
                <#else>
                    <a id="addressCloseButton" class="button blue">Close</a>  	
                </#if>  	
                </div>

			</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>

<#if (model.result?? && model.result.hasErrors() ) ||  add?? >

<#else>
<script type="text/javascript">
	$(document).ready(function(){
		$('#address-H2').trigger('click');
	});
</script>

</#if>

