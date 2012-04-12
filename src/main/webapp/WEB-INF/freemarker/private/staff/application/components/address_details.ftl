<#if model.applicationForm.addresses?has_content>
	<#assign hasAddresses = true>
<#else>
	<#assign hasAddresses = false>
</#if> 
 
<#import "/spring.ftl" as spring />

	<h2 id="address-H2" class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
	    Address
	</h2>
	    
	<div>
	
        	<form>
				
				<input type="hidden" id="addressId" name="addressId"/>
            	<div>
                	<h3>Address</h3>
                  
                  	<!-- Address body -->
                  	<div class="row">
                    	<span class="label">Current Address</span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	 <textarea readonly="readonly" id="currentAddressLocation" class="max" rows="6" cols="80">${(model.address.currentAddressLocation?html)!}</textarea>
                    	</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="label">Country</span>
	                    <div class="field">
	                       <select class="full" name="currentAddressCountry" id="currentAddressCountry"
                                            disabled="disabled">
                            <option value="">Select...</option>
                                <#list model.countries as country>
                                    <option value="${country.id?string('#######')}" <#if model.address.currentAddressCountry?? && model.address.currentAddressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
                                </#list>
                            </select>
						</div>
					</div>
					
					                   <!-- Address body -->
                    <div class="row">
                        <span class="label">Contact Address</span>
                        <span class="hint"></span>
                        <div class="field">
                         <textarea readonly="readonly" id="contactAddressLocation" class="max" rows="6" cols="80">${(model.address.contactAddressLocation?html)!}</textarea>
                        </div>
                    </div>

                    <!-- Country -->
                    <div class="row">
                        <span class="label">Country</span>
                        <div class="field">
                           <select class="full" name="contactAddressCountry" id="contactAddressCountry"
                                            disabled="disabled">
                            <option value="">Select...</option>
                                <#list model.countries as country>
                                    <option value="${country.id?string('#######')}" <#if model.address.contactAddressCountry?? && model.address.contactAddressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
                                </#list>
                            </select>
                        </div>
                    </div>
					
				</div>

                <div class="buttons">
                  	<button id="addressCloseButton" class="blue" type="button">Close</button>
                </div>

			</form>
	</div>
	<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>