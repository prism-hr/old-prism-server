<#if model.applicationForm.referees?has_content>
	<#assign hasReferees = true>
<#else>
	<#assign hasReferees = false>
</#if> 
 
<#import "/spring.ftl" as spring />

			<h2 class="empty">
				<span class="left"></span><span class="right"></span><span class="status"></span>
        		References
       		</h2>
			<div class="open">
				
				<#if model.hasError('numberOfReferees')>                           
	        		<span class="invalid"><@spring.message  model.result.getFieldError('numberOfReferees').code /></span><br/>                        
	       		</#if>
            	
            	<#if hasReferees>
	            	<table class="existing">
	                	
	                	<colgroup>
		                  	<col style="width: 30px" />
		                  	<col style="width: 160px" />
		                  	<col style="width: 160px" />
		                  	<col style="width: 260px" />
		                  	<col />
		                  	<col style="width: 30px" />
	                	</colgroup>
	                
	                	<thead>
	                  		<tr>
			                    <th colspan="2">First name</th>
			                    <th>Surname</th>
			                    <th>Job Title</th>
			                    <th>Email</th>
			                    <th>&nbsp;</th>
		                  	</tr>
	                	</thead>
	                	
	                	<tbody>
	                		<#list model.applicationForm.referees as referee>
			                  	<tr>
				                    <td><a class="row-arrow" name="refereeEditButton" id="referee_${referee.id!}">-</a></td>
				                    <td>${referee.firstname!}</td>
				                    <td>${referee.lastname!}</td>
				                    <td>${referee.jobTitle!}</td>
				                    <td>${referee.email!}</td>
                                    <input type="hidden" id="${referee.id!}_refereeId" value="${referee.id!}"/>
                                    <input type="hidden" id="${referee.id!}_firstname" value="${referee.firstname!}"/>
                                    <input type="hidden" id="${referee.id!}_lastname" value="${referee.lastname!}"/>
                                    <input type="hidden" id="${referee.id!}_relationship" value="${referee.relationship!}"/>
                                    <input type="hidden" id="${referee.id!}_jobEmployer" value="${referee.jobEmployer!}"/>
                                    <input type="hidden" id="${referee.id!}_jobTitle" value="${referee.jobTitle!}"/>
                                    <input type="hidden" id="${referee.id!}_addressLocation" value="${referee.addressLocation!}"/>
                                    <input type="hidden" id="${referee.id!}_addressPostcode" value="${referee.addressPostcode!}"/>
                                    <input type="hidden" id="${referee.id!}_addressCountry" value="${referee.addressCountry!}"/>
                                    <input type="hidden" id="${referee.id!}_email" value="${referee.email!}"/>
									
									<#list referee.phoneNumbersRef! as phoneNumber>
									<span name="${referee.id!}_hiddenPhones" style="display:none" >
                   		 				<select class="half" value="${phoneNumber.telephoneType.displayValue!}"> 
                   		 				<option>${phoneNumber.telephoneType.displayValue}</option>
		                      			</select>
		                        		${phoneNumber.telephoneNumber!}
		                      			<a class="button">delete</a>
											<input class="half" type="hidden" placeholder="Number" name="phoneNumbersRef" 
		                      			value='{"type" :"${phoneNumber.telephoneType}", "number":"${phoneNumber.telephoneNumber}"}' />
		                      				</span>
									</#list>
									
									<#list referee.messengersRef! as messenger>
									<span name="${referee.id!}_hiddenMessengers" style="display:none" >
                   		 				${messenger.messengerAddress!} <a class="button">delete</a>
										<input type="hidden" name="messengersRef" value='{"address":"${messenger.messengerAddress!}"}' />								
									</span>
                   				 	</#list>

			                  	</tr>
		                  	</#list>
	                	</tbody>
	              	
	              	</table>
              	</#if>
              	
              	<input type="hidden" id="refereeId" name="refereeId"/>
              	
              	<form>
                
                	<div>
                
                  		<!-- First name -->
                  		<div class="row">
                    		<span class="label">First Name</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    			
                    			<input class="full" id="ref_firstname" name="ref_firstname" value="${model.referee.firstname!}"/> 
                                 
                           		<#if model.hasError('firstname')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('firstname').code /></span>                           
                            	</#if>
                      			
                    		</div>
                  		</div>
                
                  		<!-- Last name -->
                  		<div class="row">
                    		<span class="label">Last Name</span>
                    		<span class="hint"></span>
                    		<div class="field">
                      			<input class="full" id="ref_lastname" name="ref_lastname" value="${model.referee.lastname!}"/>
                                <#if model.hasError('lastname')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('lastname').code /></span>                           
                            	</#if>
                    		</div>
                  		</div>
                
                  		<!-- Relationship name -->
                  		<div class="row">
                    		<span class="label">Relationship</span>
                    		<span class="hint"></span>
                    		<div class="field">
                      			<input class="full" id="ref_relationship" name="ref_relationship" value="${model.referee.relationship!}"/>
                                <#if model.hasError('relationship')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('relationship').code /></span>                           
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
                      			<input class="full" id="ref_employer" name="ref_employer" value="${model.referee.jobEmployer!}"/>
                                <#if model.hasError('jobEmployer')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('jobEmployer').code /></span>                           
                            	</#if>
                    		</div>
                  		</div>
                
                  		<!-- Position title -->
                  		<div class="row">
                    		<span class="label">Title</span>
                    		<span class="hint"></span>
                    		<div class="field">
                      			<input class="full" id="ref_position" name="ref_position" value="${model.referee.jobTitle!}"/>
                                <#if model.hasError('jobTitle')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('jobTitle').code /></span>                           
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
                      			<textarea class="max" rows="6" cols="70" id="ref_address_location" 
                      				name="ref_address_location" value="${model.referee.addressLocation!}"></textarea>
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
                      			<input class="half" id="ref_address_postcode" name="ref_address_postcode" value="${model.referee.addressPostcode!}"/>
                                <#if model.hasError('addressPostcode')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('addressPostcode').code /></span>                           
                            	</#if>
                    		</div>
                  		</div>
                
                  		<!-- Country -->
                  		<div class="row">
                    		<span class="label">Country</span>
                    		<span class="hint"></span>
                    		<div class="field">
                      			<input class="half" id="ref_address_country" name="ref_address_country" value="${model.referee.addressCountry!}"/>
                                <#if model.hasError('addressCountry')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('addressCountry').code /></span>                           
                            	</#if>
                    		</div>
                  		</div>
                	
                	</div>

                	<div>
                  	
                  		<p><strong>Contact Details</strong></p>
                
                  		<!-- Email address -->
                  		<div class="row">
                    		<span class="label">Email</span>
                    		<span class="hint"></span>
                    		<div class="field">
                      			<input class="full" type="email" id="ref_email" name="ref_email" value="${model.referee.email!}"/>
                                <#if model.hasError('email')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('email').code /></span>                           
                            	</#if>
                    		</div>
                  		</div>

                  		<!-- Telephone -->
                  		<div class="row">
                    		<span class="label">Telephone</span>
                    		<span class="hint"></span>
                    		<div class="field" id="phonenumbersref">
                    			<#list model.referee.phoneNumbers! as phoneNumber>
                    			<span name="phone_number_ref">
		                      		<select class="half" value="${phoneNumber.telephoneType.displayValue}">
		                        		<option>${phoneNumber.telephoneType.displayValue}</option>
		                      		</select>
		                        		${phoneNumber.telephoneNumber}
		                      		
		                      		<a class="button">delete</a>
		                      		
		                      		<input class="half" type="hidden" placeholder="Number" name="phoneNumbersRef" 
		                      			value='{"type" :"${phoneNumber.telephoneType}", "number":"${phoneNumber.telephoneNumber}"}' />
		                      			</span>	
		                      	</#list>
                    		</div>
                    		<select class="full" id="phoneTypeRef">
                    			 <#list model.phoneTypes as phoneType >
                      				<option value="${phoneType}">${phoneType.displayValue}</option>
                      			</#list>
                      			
                      			</select>
                      			<input type="text" placeholder="Number" id="phoneNumberRef"/>
                     			 	<a class="button" id="addPhoneRefButton" style="width: 110px;">Add Phone</a>
                     			 	 <#if model.hasError('phoneNumbersRef')>                           
                            			<span class="invalid"><@spring.message  model.result.getFieldError('phoneNumbersRef').code /></span>                           
                            		</#if>
                      			
                    		
                    	
                  		</div>

	                  	<!-- Skype address -->
	                  	<div class="row">
	                  		<span class="label">Skype</span>
	                    	<span class="hint"></span>
	                    	<div class="field" id="messengersref">
	                    		<#list model.referee.messengers! as messenger>
	                    			<span name="messenger_ref">
                   		 				${messenger.messengerAddress} <a class="button">delete</a>
										<input type="hidden" name="messengersRef" value='{"address":"${messenger.messengerAddress}"}' />								
									</span>
	                      		</#list>
	                      			<input class="full" type="text" placeholder="Skype address" id="messengerAddressRef" />
	                      			<a id="addMessengerRefButton" class="button" style="width: 110px;">Add Messenger</a>
	                      			
	                    	</div>
	                  	</div>
                  	
                	</div>

                	<div class="buttons">
                  		 <a class="button" type="button" id="refereeCancelButton" name="refereeCancelButton">Cancel</a>
                  		<button class="blue" type="button" value="close" id="refereeSaveButton">Save and Close</button>
                  		<button class="blue" type="button" id="refereeSaveAndAddButton" value="add">Save and Add</button>
                	</div>

				</form>
            
            </div>

            <script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>