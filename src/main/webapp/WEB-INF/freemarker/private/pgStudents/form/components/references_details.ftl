<#if model.applicationForm.referees?has_content>
	<#assign hasReferees = true>
<#else>
	<#assign hasReferees = false>
</#if> 
  
<#import "/spring.ftl" as spring />

			<h2 id="referee-H2" class="empty">
				<span class="left"></span><span class="right"></span><span class="status"></span>
        		References<em>*</em>
       		</h2>
       		
			<div class="open">
				
            	
            	<#if hasReferees>
	            	<table class="existing">
	                	
	                	<colgroup>
		                  	<col style="width: 30px" />
		                  	<col style="width: 160px" />
		                  	<col style="width: 160px" />
		                  	<col style="width: 260px" />
		                  	<col />
		                  	<#if model.applicationForm.isSubmitted()>
		                  		<col />
		                  	<#else>
		                  		<col style="width: 30px" />
		                  	</#if>
		                  	
	                	</colgroup>
	                
	                	<thead>
	                  		<tr>
			                    <th colspan="2">First name</th>
			                    <th>Surname</th>
			                    <th>Job Title</th>
			                    <th>Email</th>
			                    <#if model.applicationForm.isSubmitted()>
			                    	<th>Responded</th>
			                    <#else>
			                    	<th>&nbsp;</th>
			                    </#if>
			                    
		                  	</tr>
	                	</thead>
	                	
	                	<tbody>
	                		<#list model.applicationForm.referees as referee>
			                  	<tr>
				                    <td><a class="row-arrow" name="refereeEditButton" id="referee_${(referee.id?string('#######'))!}">-</a></td>
				                    <td>${(referee.firstname?html)!}</td>
				                    <td>${(referee.lastname?html)!}</td>
				                    <td>${(referee.jobTitle?html)!}</td>
				                    <td>${(referee.email?html)!}</td>
				                     <#if model.applicationForm.isSubmitted()>
					                    <td>
					                    	<#if referee.hasProvidedReference() >Yes<#else>No</#if>
					                    </td>
				                    <#else>
					                    <td>
					                    
						                  	<form method="Post" action="<@spring.url '/deleteentity/referee'/>" style="padding:0">
					                			<input type="hidden" name="id" value="${referee.id?string('#######')}"/>		                		
					                			<a name="deleteButton" class="button-delete">delete</a>
					                		</form>
						                
							        	</td>
				                     </#if>
                                    <input type="hidden" id="${referee.id?string('#######')}_refereeId" value="${referee.id?string('#######')}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_firstname" value="${(referee.firstname?html)!}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_lastname" value="${(referee.lastname?html)!}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_relationship" value="${(referee.relationship?html)!}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_jobEmployer" value="${(referee.jobEmployer?html)!}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_jobTitle" value="${(referee.jobTitle?html)!}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_addressLocation" value="${(referee.addressLocation?html)!}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_addressPostcode" value="${(referee.addressPostcode?html)!}"/>                                    
                                    <input type="hidden" id="${referee.id?string('#######')}_addressCountry" <#if referee.addressCountry??> value="${referee.addressCountry.id?string('#######')}" </#if>/>
                                    <input type="hidden" id="${referee.id?string('#######')}_lastUpdated" value="<#if referee.hasProvidedReference() > 
			                    		Provided ${(referee.reference.lastUpdated?string('dd-MMM-yyyy'))!}
			                    	<#else>
			                    		Not provided
			                    	</#if>"/>
			                    	
                                    <input type="hidden" id="${referee.id?string('#######')}_email" value="${(referee.email?html)!}"/>
									<#list referee.phoneNumbers! as phoneNumber>
										<span name="${referee.id?string('#######')}_hiddenPhones" style="display:none" >
			                  	  		<div class="row">
			                  	  	 		<span class="label">Telephone</span>    
			                  				<div class="field">
			                  					<label class="half"> ${phoneNumber.telephoneType.displayValue?html}</label>
			                  					<label id="multi-phone" class="half"> ${phoneNumber.telephoneNumber?html}</label> 
			                  	  				<#if !model.applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>           
			                  	  			</div>
			                  	  			
			                  	  		</div>   
			                            <input type="hidden" name="phoneNumbers" value='${phoneNumber.asJson?html}'/>   
			                            </span>
		                            </#list>
									
								
									
									 <input type="hidden" id="${referee.id?string('#######')}_messenger" value="${(referee.messenger?html)!}"/>   

			                  	</tr>
		                  	</#list>
	                	</tbody>
	              	
	              	</table>
              	</#if>
              	
              	<input type="hidden" id="refereeId" name="refereeId"/>
              	
              	<form>
                
                	<div>
                
				<#if model.hasError('numberOfReferees')>
				<div class="row">                           
	        		<span class="invalid"><@spring.message  model.result.getFieldError('numberOfReferees').code /></span><br/>
	        	</div>	                        
	       		</#if>
                  		<!-- First name -->
                  		<div class="row">
                    		<span class="label">First Name<em>*</em></span>
                    		<span class="hint"></span>
                    		<div class="field">
                    			<#if !model.applicationForm.isSubmitted()>
                    			<input class="full" id="ref_firstname" name="ref_firstname" value="${(model.referee.firstname?html)!}"/> 
                                 
                           		<#if model.hasError('firstname')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('firstname').code /></span>                           
                            	</#if>
                            	<#else>
                            	   <input readonly="readonly" class="full" id="ref_firstname" name="ref_firstname" value="${(model.referee.firstname?html)!}"/>
                            	</#if>
                      			
                    		</div>
                  		</div>
                
                  		<!-- Last name -->
                  		<div class="row">
                    		<span class="label">Last Name<em>*</em></span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<input class="full" id="ref_lastname" name="ref_lastname" value="${(model.referee.lastname?html)!}"/>
                                <#if model.hasError('lastname')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('lastname').code /></span>                           
                            	</#if>
                            	<#else>
                            	   <input readonly="readonly" class="full" id="ref_lastname" name="ref_lastname" value="${(model.referee.lastname)?html!}"/>
                            	</#if>
                    		</div>
                  		</div>
                
                  		<!-- Relationship name -->
                  		<div class="row">
                    		<span class="label">Relationship<em>*</em></span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<input class="full" id="ref_relationship" name="ref_relationship" value="${(model.referee.relationship?html)!}"/>
                                <#if model.hasError('relationship')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('relationship').code /></span>                           
                            	</#if>
                            	<#else>
                            	   <input readonly="readonly" class="full" id="ref_relationship" name="ref_relationship" value="${(model.referee.relationship?html)!}"/>
                            	</#if>
                    		</div>
                  		</div>
                	
                	</div>

                	<div>
                  
                  		<p><strong>Position</strong></p>
                
                  		<!-- Employer / company name -->
                  		<div class="row">
                    		<span class="label">Employer</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<input class="full" id="ref_employer" name="ref_employer" value="${(model.referee.jobEmployer?html)!}"/>
                                <#if model.hasError('jobEmployer')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('jobEmployer').code /></span>                           
                            	</#if>
                            	<#else>
                            	   <input readonly="readonly" class="full" id="ref_employer" name="ref_employer" value="${(model.referee.jobEmployer?html)!}"/>
                            	</#if>
                    		</div>
                  		</div>
                
                  		<!-- Position title -->
                  		<div class="row">
                    		<span class="label">Title</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<input class="full" id="ref_position" name="ref_position" value="${(model.referee.jobTitle?html)!}"/>
                                <#if model.hasError('jobTitle')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('jobTitle').code /></span>                           
                            	</#if>
                            	<#else>
                            	   <input readonly="readonly" class="full" id="ref_position" name="ref_position" value="${(model.referee.jobTitle?html)!}"/>
                            	</#if>   
                    		</div>
                  		</div>
                  		
                	</div>

                	<div>
                	
                  		<p><strong>Address</strong></p>
                  
                  		<!-- Address body -->
                  		<div class="row">
                    		<span class="label">Location</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<textarea class="max" rows="6" cols="70" id="ref_address_location" 
                      				name="ref_address_location">${(model.referee.addressLocation?html)!}</textarea>
                      			<#if model.hasError('addressLocation')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('addressLocation').code /></span>                           
                            	</#if>
                            	<#else>
                            	   <textarea readonly="readonly" class="max" rows="6" cols="70" id="ref_address_location" 
                                    name="ref_address_location" value="${(model.referee.addressLocation?html)!}"></textarea>
                            	</#if> 
                    		</div>
                  		</div>
                
                  		<!-- Postcode -->
                  		<div class="row">
                    		<span class="label">Postal Code</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<input class="half" id="ref_address_postcode" name="ref_address_postcode" value="${(model.referee.addressPostcode?html)!}"/>
                                <#if model.hasError('addressPostcode')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('addressPostcode').code /></span>                           
                            	</#if>
                            	<#else>
                            	   <input readonly="readonly" class="half" id="ref_address_postcode" name="ref_address_postcode" value="${(model.referee.addressPostcode?html)!}"/>
                            	</#if>
                    		</div>
                  		</div>
                
                  		<!-- Country -->
                  		<div class="row">
                    		<span class="label">Country</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<select class="full" name="ref_address_country" id="ref_address_country"
                            <#if model.applicationForm.isSubmitted()>
                                            disabled="disabled"
                            </#if>>
                            <option value="">Select...</option>
                                <#list model.countries as country>
                                    <option value="${country.id?string('#######')}" <#if model.referee.addressCountry?? && model.referee.addressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
                                </#list>
                            </select>
                    		</div>
                  		</div>
                	
                	</div>

                	<div>
                  	
                  		<p><strong>Contact Details</strong></p>
                
                  		<!-- Email address -->
                  		<div class="row">
                    		<span class="label">Email<em>*</em></span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<input class="full" type="email" id="ref_email" name="ref_email" value="${(model.referee.email?html)!}"/>
                                <#if model.hasError('email')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('email').code /></span>                           
                            	</#if>
                            	<#else>
                            	   <input readonly="readonly" class="full" type="email" id="ref_email" name="ref_email" value="${(model.referee.email?html)!}"/>
                            	</#if>
                    		</div>
                  		</div>

                  		<!-- Telephone -->
                  		
                  		<div class="row" id="phonenumbersref" >           
                    			<#list model.referee.phoneNumbers! as phoneNumber>
                    			<span  name="phone_number_ref">
		                  	  		<div class="row">
		                  	  	 		<span class="label">Telephone</span>    
		                  				<div class="field">
		                  					<label class="half"> ${phoneNumber.telephoneType.displayValue?html}</label>
		                  					<label class="half"> ${phoneNumber.telephoneNumber?html}</label> 
		                  	  				<#if !model.applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>           
		                  	  			</div>
		                  	  			
		                  	  		</div>   
		                            <input type="hidden" name="phoneNumbers" value='${phoneNumber.asJson?html}'/>   
                  	  			</span>              
		                      	</#list>
                    		</div>
                  		
                  		<div class="row">
                  		<span class="label">Telephone<em id="telephone-em">*</em></span>
                        <span class="hint"></span>
                    		
                    		<#if !model.applicationForm.isSubmitted()>
                    		<div class="field">
                    		<select class="half" id="phoneTypeRef">
                    			 <#list model.phoneTypes as phoneType >
                      				<option value="${phoneType}">${phoneType.displayValue}</option>
                      			</#list>
                      			</select>
                      				<input type="text" placeholder="Number" id="phoneNumberRef"/>
                     			 	<a class="button blue" id="addPhoneRefButton" style="width: 110px;">Add Phone</a>
                     		</div>
									<#if model.hasError('phoneNumbersRef')>                           
                            			<span class="invalid"><@spring.message  model.result.getFieldError('phoneNumbersRef').code /></span>                           
                            		</#if>
                     		</#if>	 	
                     		<#if model.hasError('phoneNumbers')>                           
                            	<span class="invalid"><@spring.message  model.result.getFieldError('phoneNumbers').code /></span>                           
                            </#if>
                  		</div>

	                  	<!-- Skype address -->
	                  
	                    <div class="row">
                    		<span class="label">Skype</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    			<#if !model.applicationForm.isSubmitted()>
                    			<input class="full" id="ref_messenger" name="ref_messenger" value="${(model.referee.messenger?html)!}"/> 
                                 
                            	<#else>
                            	   <input readonly="readonly" class="full" id="ref_messenger" name="ref_messenger" value="${(model.referee.messenger?html)!}"/>
                            	</#if>
                      			
                    		</div>
                  		</div>
                	</div>
					<#if model.applicationForm.isSubmitted()>
						<div>
	                  	            
	                  		<div class="row">
			                  	<label class="label">Reference</label>
			                    <span class="hint"></span>
			                    <div class="field" id="referenceUpdated">
			                    	
			                    </div>
		                    </div>
	                	</div>
					</#if>

                	<div class="buttons">
                	 <#if !model.applicationForm.isSubmitted()>
                  		 <a class="button" type="button" id="refereeCancelButton" name="refereeCancelButton">Cancel</a>
                  		 <button class="blue" type="button" id="refereeCloseButton" name="refereeCloseButton">Close</button>
                  		<button class="blue" type="button" value="close" id="refereeSaveButton">Save and Close</button>
                  		<button class="blue" type="button" id="refereeSaveAndAddButton" value="add">Save and Add</button>
                  	 <#else>
                        <a id="refereeCloseButton" class="button blue">Close</a>   
                    </#if> 	
                	</div>

				</form>
            
            </div>

            <script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>
            
 <#if (model.result?? && model.result.hasErrors() )  || add??>

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#referee-H2').trigger('click');
	});
</script>
</#if>