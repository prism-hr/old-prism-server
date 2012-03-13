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
				                <td>${address.startDate?string('yyyy/MM/dd')}</td>
				                <td>${(address.endDate?string('yyyy/MM/dd'))!}</td>
				                <td><a class="button-delete" type="submit">Delete</a></td>
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
                      		${model.address.addressLocation!}
                    	</div>
                  	</div>

                  	<!-- Postcode -->
                  	<div class="row">
                    	<span class="label">Postal Code</span>
                    	<span class="hint"></span>
                    	<div class="field">
							${model.address.addressPostCode!}                    	
						</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="label">Country</span>
	                    <div class="field">
							${address.country}	                      	
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
								${(model.address.addressStartDate?string('yyyy/MM/dd'))!}                      		
							</label>
                      		
                      		<label>to 
                      			${(model.address.addressEndDate?string('yyyy/MM/dd'))!}
                      		</label>
                    	</div>
                 	</div>

                  	<!-- Purpose -->
                  	<div class="row">
                    	<span class="label">Purpose</span>
                    	<span class="hint"></span>
                    	<div class="field">
                      		${model.address.addressPurpose!}
                    	</div>
                  	</div>
                  	
                </div>

                <#if model.address.addressContactAddress = true>
                <div>
                	<!-- Is contact address? -->
                  	<div class="row">
                    	<span class="label">&nbsp;</span>
                    	<div class="field">
                      		This is my contact address
                    	</div>
                  	</div>
                </div>
                </#if>

                <div class="buttons">
                  	<button class="blue" type="button">Close</button>
                </div>

			</form>
	</div>
	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>