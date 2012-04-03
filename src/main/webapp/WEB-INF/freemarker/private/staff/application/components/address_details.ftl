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
	
			<#if hasAddresses>
				<table class="existing">
			    	<colgroup>
			        	<col style="width: 30px" />
			            <col />
			            <col style="width: 100px" />
			            <col style="width: 100px" />
			            <col style="width: 30px" />
					</colgroup>
					
			        <thead>
			        	<tr>
			            	<th colspan="2">Address</th>
			                <th>From</th>
			                <th>To</th>
			                <th>&nbsp;</th>
						</tr>
			            
					</thead>
			        
			        <tbody>
			        	<#list model.applicationForm.addresses as address>
				        	<tr>
				            	<td><a class="row-arrow"  name="addressEditButton" id="address_${address.id?string('#######')}">-</a></td>
				                <td>${address.location?html}, ${address.postCode?html}</td>
				                <td>${address.startDate?string('dd-MMM-yyyy')}</td>
				                <td>${(address.endDate?string('dd-MMM-yyyy'))!}</td>
				                
				                <input type="hidden" id="${address.id?string('#######')}_addressIdDP" value="${address.id?string('#######')}"/>
                                <input type="hidden" id="${address.id?string('#######')}_locationDP" value="${address.location?html}"/>
                                <input type="hidden" id="${address.id?string('#######')}_postCodeDP" value="${address.postCode?html}"/>
                                <input type="hidden" id="${address.id?string('#######')}_countryDP" value="${address.country.id?string('#######')}"/>
                                <input type="hidden" id="${address.id?string('#######')}_startDateDP" value="${address.startDate?string('dd-MMM-yyyy')}"/>
                                <input type="hidden" id="${address.id?string('#######')}_endDateDP" value="${(address.endDate?string('dd-MMM-yyyy'))!}"/>
                                <input type="hidden" id="${address.id?string('#######')}_purposeDP" value="${address.purpose?html}"/>
                                <input type="hidden" id="${address.id?string('#######')}_contactAddressDP" value="${address.contactAddress?html}"/>
                                
							</tr>
						</#list>
					</tbody>
				</table>
        	</#if>

        	<form>
				
				<input type="hidden" id="addressId" name="addressId"/>
            	<div>
                	<h3>Address</h3>
                  
                  	<!-- Address body -->
                  	<div class="row">
                    	<span class="label">Location</span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	 <textarea readonly="readonly" id="addressLocation" class="max" rows="6" cols="80" 
                                                    value="${(model.address.addressLocation?html)!}"></textarea>
                    	</div>
                  	</div>

                  	<!-- Postcode -->
                  	<div class="row">
                    	<span class="label">Postal Code</span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	<input readonly="readonly" class="half" type="text" id="addressPostCode" 
                                                name="addressPostCode" value="${(model.address.addressPostCode?html)!}" />
						</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="label">Country</span>
	                    <div class="field">
	                       <select class="full" name="addressCountry" id="addressCountry"
                                            disabled="disabled">
                            <option value="">Select...</option>
                                <#list model.countries as country>
                                    <option value="${country.id?string('#######')}" <#if model.address.addressCountry?? && model.address.addressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
                                </#list>
                            </select>
						</div>
					</div>
					
				</div>

                <div>
                	
                	<h3>Residency Period</h3>
                  
                  	<!-- Residency period -->
                  	<div class="row">
                    	<span class="label">Dates</span>
                    	<span class="hint"></span>
                    	<div class="field">
                      		<label>from 
                      		<input class="half date" type="text" id="addressStartDate" name="addressStartDate" 
                                            value="${(model.address.addressStartDate?string('dd-MMM-yyyy'))!}"
                                                disabled="disabled"/>
							</label>
                      		
                      		<label>to
                      		<input class="half date" type="text" id="addressEndDate" name="addressEndDate" 
                                                        value="${(model.address.addressEndDate?string('dd-MMM-yyyy'))!}"
                                                            disabled="disabled"/>
                      		</label>
                    	</div>
                 	</div>

                  	<!-- Purpose -->
                  	<div class="row">
                    	<span class="label">Purpose</span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	   <select id="addressPurpose" name="addressPurpose" class="full" value="${(model.address.addressPurpose?html)!}"
                                                disabled="disabled">
                            <option value="">Select...</option>
                            <#list model.addressPurposes as purpose>
                                <option value="${purpose}"
                                <#if model.address.addressPurpose?? &&  model.address.addressPurpose == purpose >
                                selected="selected"
                                </#if> 
                                >${purpose.displayValue}</option>               
                            </#list>
                            </select>
                    	</div>
                  	</div>
                  	
                </div>
                
                 <div>
                    <!-- Is contact address? -->
                    <div class="row">
                        <span class="label">&nbsp;</span>
                        <div class="field">
                            <label>
                                <input type="checkbox" name="isCA" id="isCA"
                                      disabled="disabled">
                                </input> This is my contact address
                            </label>
                            <input type="hidden" id="addressContactAddress" value="${(model.address.addressContactAddress?html)!}"/>
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