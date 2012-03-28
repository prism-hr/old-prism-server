<#if model.applicationForm.referees?has_content>
	<#assign hasReferees = true>
<#else>
	<#assign hasReferees = false>
</#if> 
 
<#import "/spring.ftl" as spring />

			<h2 id="referee-H2" class="empty">
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
			                    <th>Reference</th>
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
				                    <td>
					                    <#if referee.hasProvidedReference() ><a href="<@spring.url '/download/reference?referenceId=${referee.reference.id?string("#######")}'/>">${referee.reference.document.fileName}</a><#else> - </#if>
					                 </td>
                                    <input type="hidden" id="${referee.id!}_refereeId" value="${referee.id!}"/>
                                    <input type="hidden" id="${referee.id!}_firstname" value="${referee.firstname!}"/>
                                    <input type="hidden" id="${referee.id!}_lastname" value="${referee.lastname!}"/>
                                    <input type="hidden" id="${referee.id!}_relationship" value="${referee.relationship!}"/>
                                    <input type="hidden" id="${referee.id!}_jobEmployer" value="${referee.jobEmployer!}"/>
                                    <input type="hidden" id="${referee.id!}_jobTitle" value="${referee.jobTitle!}"/>
                                    <input type="hidden" id="${referee.id!}_addressLocation" value="${referee.addressLocation!}"/>
                                    <input type="hidden" id="${referee.id!}_addressPostcode" value="${referee.addressPostcode!}"/>
                                    <input type="hidden" id="${referee.id!}_addressCountry" <#if referee.addressCountry??> value="${referee.addressCountry.id!}" </#if>/>
                                     <input type="hidden" id="${referee.id!}_lastUpdated" value="<#if referee.hasProvidedReference() > 
			                    		Provided ${(referee.reference.lastUpdated?string('dd-MMM-yyyy'))!}
			                    	<#else>
			                    		Not provided
			                    	</#if>"/>
			                    	 
			                    	 <input type="hidden" id="${referee.id!}_reference_document_url" value="<#if referee.hasProvidedReference() && referee.reference.document?? >
			                    	 	<@spring.url '/download/reference?referenceId=${referee.reference.id?string("#######")}'/></#if>"
			                    	 />
			                    	 <input type="hidden" id="${referee.id!}_reference_document_name" value="<#if referee.hasProvidedReference()><#if referee.reference.document??>${referee.reference.document.fileName}<#else>No document uploaded</#if></#if>" />
                                    <input type="hidden" id="${referee.id!}_email" value="${referee.email!}"/>
									
									<#list referee.phoneNumbers! as phoneNumber>
									<span name="${referee.id!}_hiddenPhones" style="display:none" >
                   		 				${phoneNumber.telephoneType.displayValue!}
		                        		${phoneNumber.telephoneNumber!}
											<input class="half" type="hidden" placeholder="Number" name="phoneNumbers" 
		                      			value='{"type" :"${phoneNumber.telephoneType}", "number":"${phoneNumber.telephoneNumber}"}' />
		                      				</span>
									</#list>
									
									<input type="hidden" id="${referee.id!}_messenger" value="${referee.messenger!}"/>   

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
                    		  <input readonly="readonly" class="full" id="ref_firstname" name="ref_firstname" value="${model.referee.firstname!}"/>
                    		</div>
                  		</div>
                
                  		<!-- Last name -->
                  		<div class="row">
                    		<span class="label">Last Name</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<input readonly="readonly" class="full" id="ref_lastname" name="ref_lastname" value="${model.referee.lastname!}"/>
							</div>
                  		</div>
                
                  		<!-- Relationship name -->
                  		<div class="row">
                    		<span class="label">Relationship</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<input readonly="readonly" class="full" id="ref_relationship" name="ref_relationship" value="${model.referee.relationship!}"/>
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
                    		<input readonly="readonly" class="full" id="ref_employer" name="ref_employer" value="${model.referee.jobEmployer!}"/>
                    		</div>
                  		</div>
                
                  		<!-- Position title -->
                  		<div class="row">
                    		<span class="label">Title</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<input readonly="readonly" class="full" id="ref_position" name="ref_position" value="${model.referee.jobTitle!}"/>
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
                    		<textarea readonly="readonly" class="max" rows="6" cols="70" id="ref_address_location" 
                                    name="ref_address_location" value="${model.referee.addressLocation!}"></textarea>
                    		</div>
                  		</div>
                
                  		<!-- Postcode -->
                  		<div class="row">
                    		<span class="label">Postal Code</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<input readonly="readonly" class="half" id="ref_address_postcode" name="ref_address_postcode" value="${model.referee.addressPostcode!}"/>
                    		</div>
                  		</div>
                
                  		<!-- Country -->
                  		<div class="row">
                    		<span class="label">Country</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		  <select class="full" name="ref_address_country" id="ref_address_country"
                                            disabled="disabled">
                            <option value="">Select...</option>
                                <#list model.countries as country>
                                    <option value="${country.id?string('#######')}" <#if model.referee.addressCountry?? && model.referee.addressCountry.id == country.id> selected="selected"</#if>>${country.name}</option>               
                                </#list>
                            </select>
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
                    		<input readonly="readonly" class="full" type="email" id="ref_email" name="ref_email" value="${model.referee.email!}"/>
                    		</div>
                  		</div>

                  		<!-- Telephone -->
                  		<div class="row">
                  		    <span class="label">Telephone</span>
                  		    <span class="hint"></span>
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
                    		<span class="label">Skype</span>
                    		<span class="hint"></span>
                    		<div class="field">
                    		<input readonly="readonly" class="full" type="email" id="ref_messenger" name="ref_messenger" value="${model.referee.messenger!}"/>
                    		</div>
                  		</div>
                  		
                	</div>
						<div>
	                  	            
	                  		<div class="row">
			                  	<label class="label">Reference</label>
			                    <span class="hint"></span>
			                    <div class="field" id="referenceDocument">			                    	
			                    </div>     
		                    </div>		              
		                    <div class="row">			        
		                    	<span class="label"></span>	                   		          
			                   <div class="field" id="referenceUpdated">
			                    	
			                    </div>
		                    </div>
		               
	                	</div>
                	<div class="buttons">
                  		<button class="blue" type="button" value="close" id="refereeCloseButton">Close</button>
                	</div>

				</form>
            
            </div>
            <script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>