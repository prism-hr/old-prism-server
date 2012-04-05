<#if model.applicationForm.addresses?has_content>
	<#assign hasAddresses = true>
<#else>
	<#assign hasAddresses = false>
</#if> 
 
<#import "/spring.ftl" as spring />

  	
	<h2 id="address-H2" class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
	    Address<em>*</em>	    
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
			            	<th>Country</th>
			            	<th></th>
			                <th>&nbsp;</th>
						</tr>
			            
					</thead>
			        
			        <tbody>
			        	<#list model.applicationForm.addresses as address>
				        	<tr>
				            	<td><a class="row-arrow"  name="addressEditButton" id="address_${address.id?string('#######')}">-</a></td>
				                <td>${address.location?html}</td>
				                <td>${address.country.name?html}</td>
				                <td></td>
				                <td>
				                 <#if !model.applicationForm.isSubmitted()>
				                  	<form method="Post" action="<@spring.url '/deleteentity/address'/>" style="padding:0">
			                			<input type="hidden" name="id" value="${address.id?string('#######')}"/>		                		
			                			<a name="deleteButton" class="button-delete">delete</a>
			                		</form>
			                		</#if>
				                </td>
				                
								<input type="hidden" id="${address.id?string('#######')}_addressIdDP" value="${address.id?string('#######')}"/>
	                            <input type="hidden" id="${address.id?string('#######')}_locationDP" value="${address.location?html}"/>
	                            <input type="hidden" id="${address.id?string('#######')}_countryDP" value="${address.country.id?string('#######')}"/>
				                
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
            	
                	<h3>Address</h3>
                  
                  	<!-- Address body -->
                  	<div class="row">
                    	<span class="label">Location<em>*</em></span>
                    	<span class="hint"></span>
                    	<div class="field">
                    	   <#if !model.applicationForm.isSubmitted()>
                      		<textarea id="addressLocation" class="max" rows="6" cols="80" >${(model.address.addressLocation?html)!}</textarea>
							
                                <#if model.hasError('addressLocation')>                           
                            	   <span class="invalid"><@spring.message  model.result.getFieldError('addressLocation').code /></span>                           
                                </#if>
                            <#else>
                      		    <textarea readonly="readonly" id="addressLocation" class="max" rows="6" cols="80" 
                                                    value="${(model.address.addressLocation?html)!}"></textarea>
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
                                	<option value="${country.id?string('#######')}" <#if model.address.addressCountry?? && model.address.addressCountry == country.id> selected="selected"</#if>>${country.name?html}</option>               
                            	</#list>
                            </select>
                            
                            <#if model.hasError('addressCountry')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('addressCountry').code /></span>                           
                            </#if>
	                      	
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

<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>

<#if (model.result?? && model.result.hasErrors() ) ||  add?? >

<#else>
<script type="text/javascript">
	$(document).ready(function(){
		$('#address-H2').trigger('click');
	});
</script>

</#if>

