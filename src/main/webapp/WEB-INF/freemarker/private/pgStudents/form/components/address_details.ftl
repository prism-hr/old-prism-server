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
				                <td>${address.location}, ${address.postCode}</td>
				                <td>${address.startDate?string('dd-MMM-yyyy')}</td>
				                <td>${(address.endDate?string('dd-MMM-yyyy'))!}</td>
				                <td>
				                 <#if !model.applicationForm.isSubmitted()>
				                  	<form method="Post" action="<@spring.url '/deleteentity/address'/>" style="padding:0">
			                			<input type="hidden" name="id" value="${address.id?string('#######')}"/>		                		
			                			<a name="deleteButton" class="button-delete">delete</a>
			                		</form>
			                		</#if>
				        </td>
				                
								<input type="hidden" id="${address.id?string('#######')}_addressIdDP" value="${address.id?string('#######')}"/>
	                            <input type="hidden" id="${address.id?string('#######')}_locationDP" value="${address.location}"/>
	                            <input type="hidden" id="${address.id?string('#######')}_postCodeDP" value="${address.postCode}"/>
	                            <input type="hidden" id="${address.id?string('#######')}_countryDP" value="${address.country.id?string('#######')}"/>
	                            <input type="hidden" id="${address.id?string('#######')}_startDateDP" value="${address.startDate?string('dd-MMM-yyyy')}"/>
	                            <input type="hidden" id="${address.id?string('#######')}_endDateDP" value="${(address.endDate?string('dd-MMM-yyyy'))!}"/>
	                            <input type="hidden" id="${address.id?string('#######')}_purposeDP" value="${address.purpose}"/>
	                            <input type="hidden" id="${address.id?string('#######')}_contactAddressDP" value="${address.contactAddress}"/>
				                
							</tr>
						</#list>
					</tbody>
				</table>
        	</#if>

        	<form>
				
				<input type="hidden" id="addressId" name="addressId"/>
            	<div>
            	           <#if model.hasError('numberOfAddresses')>
            	           <div class="row">
                                <span class="invalid"><@spring.message  model.result.getFieldError('numberOfAddresses').code /></span><br/>
                           </div>                             
                           </#if>
                           <#if model.hasError('numberOfContactAddresses')>
                           <div class="row">                           
                                <span class="invalid"><@spring.message  model.result.getFieldError('numberOfContactAddresses').code /></span><br/>
                           </div>                              
                           </#if>
            	
                	<h3>Address</h3>
                  
                  	<!-- Address body -->
                  	<div class="row">
                    	<span class="label">Location<em>*</em></span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	   <#if !model.applicationForm.isSubmitted()>
                      		<textarea id="addressLocation" class="max" rows="6" cols="80" >${model.address.addressLocation!}</textarea>
							
                                <#if model.hasError('addressLocation')>                           
                            	   <span class="invalid"><@spring.message  model.result.getFieldError('addressLocation').code /></span>                           
                                </#if>
                            <#else>
                      		    <textarea readonly="readonly" id="addressLocation" class="max" rows="6" cols="80" 
                                                    value="${model.address.addressLocation!}"></textarea>
                            </#if>
                    	</div>
                  	</div>

                  	<!-- Postcode -->
                  	<div class="row">
                    	<span class="label">Postal Code<em>*</em></span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	<#if !model.applicationForm.isSubmitted()>
                      		<input class="half" type="text" id="addressPostCode" 
                      							name="addressPostCode" value="${model.address.addressPostCode!}" />
                            <#if model.hasError('addressPostCode')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressPostCode').code /></span>                           
                            </#if>
                        <#else>
                            <input readonly="readonly" class="half" type="text" id="addressPostCode" 
                                                name="addressPostCode" value="${model.address.addressPostCode!}" />
                        </#if>                            
                    	</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="label">Country<em>*</em></span>
	                    <div class="field">
	                      	
	                      	<select class="full" name="addressCountry" id="addressCountry"
	                      	<#if model.applicationForm.isSubmitted()>
                                            disabled="disabled"
                            </#if>>
                            <option value="">Select...</option>
                            	<#list model.countries as country>
                                	<option value="${country.id?string('#######')}" <#if model.address.addressCountry?? && model.address.addressCountry == country.id> selected="selected"</#if>>${country.name}</option>               
                            	</#list>
                            </select>
                            
                            <#if model.hasError('addressCountry')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressCountry').code /></span>                           
                            </#if>
	                      	
						</div>
					</div>
					
				</div>

                <div>
                	
                	<h3>Residency Period</h3>
                  
                  	<!-- Residency period -->
                  	<div class="row">
                    	<span class="label">Dates<em>*</em></span>
                    	<span class="hint"></span>
                    	<div class="field">
                      		<label>from 
                      				<input class="half date" type="text" id="addressStartDate" name="addressStartDate" 
                      						value="${(model.address.addressStartDate?string('dd-MMM-yyyy'))!}"
                      						<#if model.applicationForm.isSubmitted()>
                                                disabled="disabled"
                                            </#if>>
                      				</input>		
                      		</label>
                      		 
                      		<label>to 
                      			<input class="half date" type="text" id="addressEndDate" name="addressEndDate" 
                      									value="${(model.address.addressEndDate?string('dd-MMM-yyyy'))!}"
                      									<#if model.applicationForm.isSubmitted()>
                                                            disabled="disabled"
                                                        </#if>>
                      			</input>						
                      		</label>
                      		
                      		<#if model.hasError('addressStartDate')>
                      			<p>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('addressStartDate').code /></span>
                            	</p>                           
                            </#if>
                      		
                      		
                            <#if model.hasError('addressEndDate')>
                            	<p>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('addressEndDate').code /></span>
                            	</p>                           
                            </#if>
                      		
                    	</div>
                 	</div>

                  	<!-- Purpose -->
                  	<div class="row">
                    	<span class="label">Purpose<em>*</em></span>
                    	<span class="hint"></span>
                    	<div class="field">
                      	  	<select id="addressPurpose" name="addressPurpose" class="full" value="${model.address.addressPurpose!}"
                      	  	 <#if model.applicationForm.isSubmitted()>
                                                disabled="disabled"
                                            </#if>>
                        	<option value="">Select...</option>
                        	<#list model.addressPurposes as purpose>
                             	<option value="${purpose}"
                             	<#if model.address.addressPurpose?? &&  model.address.addressPurpose == purpose >
                                selected="selected"
                                </#if> 
                             	>${purpose.displayValue}</option>               
                        	</#list>
                      		</select>
                            <#if model.hasError('addressPurpose')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressPurpose').code /></span>                           
                            </#if>
                    	</div>
                  	</div>

                  	<!-- Supporting document 
                  	<div class="row">
                    	<span class="label">Supporting Document</span>
                    	<span class="hint"></span>
                    	<div class="field">
                      		<input class="full" type="text" value="" />
                      		<a class="button" href="#">Browse</a>
                      		<a class="button" href="#">Upload</a>
                      		<a class="button" href="#">Add a document</a>
                    	</div>  
                  	</div> -->
                  	
                </div>

                <div>
                	<!-- Is contact address? -->
                  	<div class="row">
                    	<span class="label">&nbsp;</span>
                    	<div class="field">
                      		<label>
                      			<input type="checkbox" name="isCA" id="isCA"
                      			<#if model.applicationForm.isSubmitted()>
                                      disabled="disabled"
                                </#if>
                                <#if model.address.addressContactAddress?? && model.address.addressContactAddress == 'YES'>
                                	checked="checked"                                
                                </#if>
                                >
                      			</input> This is my contact address
                      		</label>
                      		<input type="hidden" id="addressContactAddress" value="${model.address.addressContactAddress!}"/>
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

<#if (model.result?? && model.result.hasErrors() ) || add??>

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#address-H2').trigger('click');
	});
</script>
</#if>