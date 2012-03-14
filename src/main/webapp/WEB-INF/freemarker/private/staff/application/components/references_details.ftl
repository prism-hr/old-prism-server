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
                   		 				${phoneNumber.telephoneType.displayValue!}
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
								${model.referee.firstname!}                      			
                    		</div>
                  		</div>
                
                  		<!-- Last name -->
                  		<div class="row">
                    		<span class="label">Last Name</span>
                    		<span class="hint"></span>
                    		<div class="field">
								${model.referee.lastname!}                    		
							</div>
                  		</div>
                
                  		<!-- Relationship name -->
                  		<div class="row">
                    		<span class="label">Relationship</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    			${model.referee.relationship!}
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
                    			${model.referee.jobEmployer!}
                    		</div>
                  		</div>
                
                  		<!-- Position title -->
                  		<div class="row">
                    		<span class="label">Title</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    			${model.referee.jobTitle!}
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
                    			${model.referee.addressLocation!}
                    		</div>
                  		</div>
                
                  		<!-- Postcode -->
                  		<div class="row">
                    		<span class="label">Postal Code</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    			${model.referee.addressPostcode!}
                    		</div>
                  		</div>
                
                  		<!-- Country -->
                  		<div class="row">
                    		<span class="label">Country</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    			${model.referee.addressCountry!}
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
                    			${model.referee.email!}
                    		</div>
                  		</div>

                  		<!-- Telephone -->
                  		<div class="row">
                    		<div class="field" id="phonenumbersref">
                    			<#list model.referee.phoneNumbers! as phoneNumber>
                    				<p>
                   		 				${phoneNumber.telephoneType.displayValue} 
                   		 				${phoneNumber.telephoneNumber}								
									</p>	
		                      	</#list>
                    		</div>
                  		</div>

	                  	<!-- Skype address -->
	                  	<div class="row">
	                    	<div class="field" id="messengersref">
	                    		<#list model.referee.messengers! as messenger>
	                    			<span name="messenger_ref">
                   		 				${messenger.messengerAddress}
									</span>
	                      		</#list>
	                    	</div>
	                    	<span class="label">Skype</span>
	                    	<span class="hint"></span>
	                  	</div>
                  	
                	</div>

                	<div class="buttons">
                  		<button class="blue" type="button" value="close" id="refereeSaveButton">Close</button>
                	</div>

				</form>
            
            </div>

            <script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>