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
	
			<#if model.hasError('numberOfAddresses')>                           
	        	<span class="invalid"><@spring.message  model.result.getFieldError('numberOfAddresses').code /></span><br/>                        
	        </#if>
	        <#if model.hasError('numberOfContactAddresses')>                           
	        	<span class="invalid"><@spring.message  model.result.getFieldError('numberOfContactAddresses').code /></span><br/>                        
			</#if>
	
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
				            	<td><a class="row-arrow" href="#">-</a></td>
				                <td>${address.id}, ${address.location}, ${address.postCode}</td>
				                <td>${address.startDate?string('yyyy/MM/dd')}</td>
				                <td>${(address.endDate?string('yyyy/MM/dd'))!}</td>
				                <td><a class="button-delete" type="submit" name="addressEditButton" id="address_${address.id}">Edit</a></td>
				                
								<input type="hidden" id="${address.id}_addressIdDP" value="${address.id}"/>
	                            <input type="hidden" id="${address.id}_locationDP" value="${address.location}"/>
	                            <input type="hidden" id="${address.id}_postCodeDP" value="${address.postCode}"/>
	                            <input type="hidden" id="${address.id}_countryDP" value="${address.country}"/>
	                            <input type="hidden" id="${address.id}_startDateDP" value="${address.startDate?string('yyyy/MM/dd')}"/>
	                            <input type="hidden" id="${address.id}_endDateDP" value="${(address.endDate?string('yyyy/MM/dd'))!}"/>
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
                      		<textarea id="addressLocation" class="max" rows="6" cols="80" 
                      								value="${model.address.addressLocation!}"></textarea>
							
							<!--input type="text" id="addressLocation" name="addressLocation" value="${model.address.addressLocation!}"/-->
                            
                            <#if model.hasError('addressLocation')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressLocation').code /></span>                           
                            </#if>
                      		
                    	</div>
                  	</div>

                  	<!-- Postcode -->
                  	<div class="row">
                    	<span class="label">Postal Code</span>
                    	<span class="hint"></span>
                    	<div class="field">
                      		<input class="half" type="text" id="addressPostCode" 
                      							name="addressPostCode" value="${model.address.addressPostCode!}" />
                            <#if model.hasError('addressPostCode')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressPostCode').code /></span>                           
                            </#if>
                    	</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="label">Country</span>
	                    <div class="field">
	                      	
	                      	<select class="full" name="addressCountry" id="addressCountry">
                            	<#list model.countries as country>
                                	<option value="${country.name}">${country.name}</option>               
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
                    	<span class="label">Dates</span>
                    	<span class="hint"></span>
                    	<div class="field">
                      		<label>from 
                      				<input class="half date" type="text" id="addressStartDate" name="addressStartDate" 
                      						value="${(model.address.addressStartDate?string('yyyy/MM/dd'))!}"/>
                      		</label>
                      		
							<#if model.hasError('addressStartDate')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressStartDate').code /></span>                           
                            </#if>
                      		 
                      		<label>to 
                      			<input class="half date" type="text" id="addressEndDate" name="addressEndDate" 
                      									value="${(model.address.addressEndDate?string('yyyy/MM/dd'))!}"/>
                      		</label>
                            <#if model.hasError('addressEndDate')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressEndDate').code /></span>                           
                            </#if>
                      		
                    	</div>
                 	</div>

                  	<!-- Purpose -->
                  	<div class="row">
                    	<span class="label">Purpose</span>
                    	<span class="hint"></span>
                    	<div class="field">
                      		
                      		<input class="full" type="text" id="addressPurpose" name="addressPurpose" 
                      					value="${model.address.addressPurpose!}" placeholder="e.g. work, travel" />
                            <#if model.hasError('addressPurpose')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressPurpose').code /></span>                           
                            </#if>
                                                  		
                    	</div>
                  	</div>

                  	<!-- Supporting document -->
                  	<div class="row">
                    	<span class="label">Supporting Document</span>
                    	<span class="hint"></span>
                    	<div class="field">
                      		<input class="full" type="text" value="" />
                      		<a class="button" href="#">Browse</a>
                      		<a class="button" href="#">Upload</a>
                      		<a class="button" href="#">Add a document</a>
                    	</div>  
                  	</div>
                  	
                  	<div class="row">
                    	<div class="field">                      
                    		<a class="button" href="#">Add residency period</a>
						</div>
                  	</div>
                
                </div>

                <div>
                	<!-- Is contact address? -->
                  	<div class="row">
                    	<span class="label">&nbsp;</span>
                    	<div class="field">
                      		<label>
                      			<input type="checkbox" /> This is my contact address
                      		</label>
                    	</div>
                  	</div>
                </div>

                <div class="buttons">
                	<a class="button blue" href="#">Close</a>
                  	<button class="blue" type="submit" id="addressSaveButton">Save</button>
                </div>

			</form>
	</div>
