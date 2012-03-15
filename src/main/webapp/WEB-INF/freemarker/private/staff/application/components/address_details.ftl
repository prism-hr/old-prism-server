<#if model.applicationForm.addresses?has_content>
	<#assign hasAddresses = true>
<#else>
	<#assign hasAddresses = false>
</#if> 
 
<#import "/spring.ftl" as spring />

	<h2 class="empty">
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
				            	<td><a class="row-arrow"  name="addressEditButton" id="address_${address.id}">-</a></td>
				                <td>${address.location}, ${address.postCode}</td>
				                <td>${address.startDate?string('dd-MMM-yyyy')}</td>
				                <td>${(address.endDate?string('dd-MMM-yyyy'))!}</td>
				                
				                <input type="hidden" id="${address.id}_addressIdDP" value="${address.id}"/>
                                <input type="hidden" id="${address.id}_locationDP" value="${address.location}"/>
                                <input type="hidden" id="${address.id}_postCodeDP" value="${address.postCode}"/>
                                <input type="hidden" id="${address.id}_countryDP" value="${address.country}"/>
                                <input type="hidden" id="${address.id}_startDateDP" value="${address.startDate?string('dd-MMM-yyyy')}"/>
                                <input type="hidden" id="${address.id}_endDateDP" value="${(address.endDate?string('dd-MMM-yyyy'))!}"/>
                                <input type="hidden" id="${address.id}_purposeDP" value="${address.purpose}"/>
                                <input type="hidden" id="${address.id}_contactAddressDP" value="${address.contactAddress}"/>
                                
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
                                                    value="${model.address.addressLocation!}"></textarea>
                    	</div>
                  	</div>

                  	<!-- Postcode -->
                  	<div class="row">
                    	<span class="label">Postal Code</span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	<input readonly="readonly" class="half" type="text" id="addressPostCode" 
                                                name="addressPostCode" value="${model.address.addressPostCode!}" />
						</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="label">Country</span>
	                    <div class="field">
	                       <select class="full" name="addressCountry" id="addressCountry"
                                            disabled="disabled">
                                <#list model.countries as country>
                                    <option value="${country.name}">${country.name}</option>               
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
                    	 <input readonly="readonly" class="full" type="text" id="addressPurpose" name="addressPurpose" 
                                        value="${model.address.addressPurpose!}" placeholder="e.g. work, travel" />
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
                            <input type="hidden" id="addressContactAddress" value="${model.address.addressContactAddress!}"/>
                        </div>
                    </div>
                </div>


                <div class="buttons">
                  	<button class="blue" type="button">Close</button>
                </div>

			</form>
	</div>
	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>