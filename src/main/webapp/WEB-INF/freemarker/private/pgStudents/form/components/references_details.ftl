<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />
<#if applicationForm.referees?has_content>
	<#assign hasReferees = true>
<#else>
	<#assign hasReferees = false>
</#if>

<a name="references-details"></a> 
<h2 id="referee-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
	References<em>*</em>
</h2>

<div class="open">
	
	
	<#if hasReferees>
    	<table class="existing">
        	
        	<colgroup>
              	<col style="width: 30px" />
              	<col/>
              	<col style="width: 30px" />
              	<col style="width: 30px" />
        	</colgroup>
        
        	<thead>
          		<tr>
                    <th id="primary-header" colspan="2">Reference</th>
                    <th>&nbsp;</th>  
                    <th id="last-col">&nbsp;</th>
              	</tr>
        	</thead>
        	
        	<tbody>
        		<#list applicationForm.referees as existingReferee>
                  	<tr>
	                    <td><a class="row-arrow">-</a></td>
	                    <td>${(existingReferee.firstname?html)!} ${(existingReferee.lastname?html)!} 
	                    (${(existingReferee.email?html)!})</td>
	                  	<#if !existingReferee.editable>		                    
		                    <td><a name="editRefereeLink" data-desc="Show" id="referee_${existingReferee.id?string('#######')}" class="button-edit button-hint">show</a></td>
		                    <#if existingReferee.declined || existingReferee.hasProvidedReference()>
		                    	<td>Responded</td>
		                    <#else>
		                   	 	<td></td>
		                    </#if>
		                 <#else>
		                	 <td>
		                    	<a name="editRefereeLink" data-desc="Edit"id="referee_${existingReferee.id?string('#######')}" class="button-edit button-hint">edit</a>
				        	</td>	
				        	 <td>
		                    	<a name="deleteRefereeButton" data-desc="Delete" id="referee_${existingReferee.id?string('#######')}" class="button-delete button-hint">delete</a>
				        	</td>	                   
		                 </#if> 
	                </tr>    
              	</#list>
        	</tbody>
      	
      	</table>
  	</#if>
  	
  	<input type="hidden" id="refereeId" name="refereeId" value="${(referee.id?string('#######'))!}" />

  	<form>
  	
 				<#if errorCode?? && errorCode=="true">
					<div class="section-error-bar">
						<div class="row">
							<span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>             	
							<span class="invalid-info-text">
								<@spring.message 'referencesDetails.sectionInfo'/>
							</span>
				 		</div>
				 	</div>
			 	<#else>
				 	<div id="ref-info-bar-div" class="section-info-bar">
						<div class="row">
							<span id="ref-info-bar-span" class="info-text">
								<@spring.message 'referencesDetails.sectionInfo'/> 
							</span>
						</div>
					</div>	
				</#if>
  	
    	<div class="row-group">
	   		<!-- First name -->
      		<div class="row">
        			<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        				<span class="plain-label">First Name<em>*</em></span>
	                	<span class="hint" data-desc="<@spring.message 'referee.firstname'/>"></span>
	        			<div class="field">
	        				<input class="full" id="ref_firstname" name="ref_firstname" value="${(referee.firstname?html)!}"/>  
	                	</div>
                	<#else>
						<span class="plain-label grey-label">First Name</span>
						<span class="hint"></span>
	                	<div class="field">
	                	   <input readonly="readonly" class="full" id="ref_firstname" 
	                	   			name="ref_firstname" value="${(referee.firstname?html)!}" disabled="disabled"/>
	                	</div>
                	</#if>
      		</div>
      		
         		<@spring.bind "referee.firstname" />    
    			<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
    
      		<!-- Last name -->
      		<div class="row">
	        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
	        		 	<span class="plain-label">Last Name<em>*</em></span>
        				<span class="hint" data-desc="<@spring.message 'referee.lastname'/>"></span>
        				<div class="field">
	        				<input class="full" id="ref_lastname" name="ref_lastname" value="${(referee.lastname?html)!}"/>
	        			</div>	          
	            	<#else>
	        		 	<span class="plain-label grey-label">Last Name</span>
        				<span class="hint"></span>
        				<div class="field">
	            	   		<input readonly="readonly" class="full" id="ref_lastname" 
	            	   			name="ref_lastname" value="${(referee.lastname?html)!}" disabled="disabled"/>
	            	   	</div>
	            	</#if>
      		</div>
	        			
	        <@spring.bind "referee.lastname" /> 
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
    
    	
    	</div>

    	<div class="row-group">
    
      		<!-- Employer / company name -->
      		<div class="row">
	        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
		        		<span class="plain-label">Employer<em>*</em></span>
		        		<span class="hint" data-desc="<@spring.message 'referee.employer'/>"></span>
        				<div class="field">
							<input class="full" id="ref_employer" name="ref_employer" value="${(referee.jobEmployer?html)!}"/>
	        			</div>	          
	            	<#else>
	        		 	<span class="plain-label grey-label">Employer</span>
        				<span class="hint"></span>
        				<div class="field">
	            	   		<input readonly="readonly" class="full" id="ref_employer" 
	            	   				name="ref_employer" value="${(referee.jobEmployer?html)!}" disabled="disabled"/>
	            	   	</div>
	            	</#if>
      		</div>
          	
          	<@spring.bind "referee.jobEmployer" />     
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
    
      		<!-- Position title -->
      		<div class="row">
	        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
		        		<span class="plain-label">Position<em>*</em></span>
		        		<span class="hint" data-desc="<@spring.message 'referee.position'/>"></span>
        				<div class="field">
							<input class="full" id="ref_position" name="ref_position" value="${(referee.jobTitle?html)!}"/>
	        			</div>	          
	            	<#else>
	        		 	<span class="plain-label grey-label">Position</span>
        				<span class="hint"></span>
        				<div class="field">
	            	   		<input readonly="readonly" class="full" id="ref_position" 
	            	   				name="ref_position" value="${(referee.jobTitle?html)!}" disabled="disabled"/>
	            	   	</div>
	            	</#if>
      		</div>
            
            
            <@spring.bind "referee.jobTitle" />
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>

      		
    	</div>

    	<div class="row-group">
    	     
      		<!-- Address body -->
      		
	        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
						<div class="row">
			          		<label class="group-heading-label">Address</label>
			          	</div> 
	        			<div class="row">
			        		<span class="plain-label">Address<em>*</em></span>
			        		<span class="hint" data-desc="<@spring.message 'referee.address'/>"></span>
	        				<div class="field">
		        				<textarea class="max" rows="6" cols="70" maxlength='200' id="ref_address_location" 
	          						name="ref_address_location">${(referee.addressLocation?html)!}</textarea> 
		        			</div>
		        		</div>          
	            	<#else>
	            		<div class="row">
			          		<label class="group-heading-label grey-label">Address</label>
			          	</div> 
	            		<div class="row">
		        		 	<span class="plain-label grey-label">Address</span>
	        				<span class="hint"></span>
	        				<div class="field">
	                	   		<textarea readonly="readonly" class="max" rows="6" cols="70" id="ref_address_location" 
	                        		name="ref_address_location" disabled="disabled">${(referee.addressLocation?html)!}</textarea>
		            	   	</div>
		            	</div>
	            	</#if>
      		
      		
      		
           	<@spring.bind "referee.addressLocation" />     		
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
    
      		<!-- Country -->
      		<div class="row">
        			<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
	        			<span class="plain-label">Country<em>*</em></span>
	        		 	<span class="hint" data-desc="<@spring.message 'referee.country'/>"></span>
	        			<div class="field">
	        				<select class="full" name="ref_address_country" id="ref_address_country">
				                <option value="">Select...</option>
                			    <#list countries as country>
                        			<option value="${encrypter.encrypt(country.id)}" 
                        				<#if referee.addressCountry?? && referee.addressCountry.id == country.id> 
                        										selected="selected"</#if>>${country.name?html}</option>               
                    			</#list>
                			</select>
	                	</div>
                	<#else>
						<span class="plain-label grey-label">Country</span>
						<span class="hint"></span>
	                	<div class="field">
	        				<select class="full" name="ref_address_country" id="ref_address_country" disabled="disabled">
                			</select>
	                	</div>
                	</#if>
          			
      		</div>
      		
                <@spring.bind "referee.addressCountry" /> 
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
  
    	</div>

    	<div class="row-group">
    	
    		
      		<!-- Email address -->
      		
	        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
	        			<div class="row">
          					<label class="group-heading-label">Contact Details</label>
          				</div> 
	        			<div class="row">		
	        				<span class="plain-label">Email<em>*</em></span>
	        		 		<span class="hint" data-desc="<@spring.message 'referee.email'/>"></span>
	        				<div class="field">
								<input class="full" type="email" id="ref_email" name="ref_email" value="${(referee.email?html)!}"/>	        			
							</div>
						</div>	          
	            	<#else>
	        			<div class="row">
          					<label class="group-heading-label grey-label">Contact Details</label>
          				</div> 
	        			<div class="row">	            	
		        		 	<span class="plain-label grey-label">Email</span>
	        				<span class="hint"></span>
	        				<div class="field">
								<input readonly="readonly" class="full" type="email" id="ref_email" 
										name="ref_email" value="${(referee.email?html)!}" disabled="disabled"/>        				
		            	   	</div>
		            	</div>
	            	</#if>
          	
          	<@spring.bind "referee.email" />       		
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>

      		<!-- Telephone -->
      		<div class="row">
	        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        				<span class="plain-label">Telephone<em>*</em></span>
						<span class="hint" data-desc="<@spring.message 'referee.telephone'/>"></span>
        				<div class="field">
							<input class="full" id="refPhoneNumber" name="refPhoneNumber" value="${(referee.phoneNumber?html)!}"/>	        			
						</div>	          
	            	<#else>
	        		 	<span class="plain-label grey-label">Telephone</span>
        				<span class="hint"></span>
        				<div class="field">
							<input readonly="readonly" class="full" id="refPhoneNumber" 
									name="refPhoneNumber" value="${(referee.phoneNumber?html)!}" disabled="disabled"/>        				
	            	   	</div>
	            	</#if>
      		</div> 
			
			<@spring.bind "referee.phoneNumber" />
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>

          	<!-- Skype address -->
      		<div class="row">
	        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
		        		<span class="plain-label">Skype Name</span>
        				<span class="hint" data-desc="<@spring.message 'referee.skype'/>"></span>
        				<div class="field">
							<input class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}"/>	        			
						</div>	          
	            	<#else>
	        		 	<span class="plain-label grey-label">Telephone</span>
        				<span class="hint"></span>
        				<div class="field">
							<input readonly="readonly" class="full" id="ref_messenger" 
									name="ref_messenger" value="${(referee.messenger?html)!}" disabled="disabled"/>        				
	            	   	</div>
	            	</#if>
      		</div>

		<#if referee.editable >
      		<!-- Add another button -->
      		<div class="row">
      			<div class="field">
      				<#if referee.id?? || applicationForm.referees?size &lt; 3>
      					<a id="addReferenceButton" class="button blue"><#if referee.id??>Update<#else>Add</#if></a>
      				<#else>
      					<a class="button" style="cursor:default">Add</a>
      				</#if>
      			</div>
      		</div>
      	</#if>		
    	</div>
	
       <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED') && (referee.id?? || applicationForm.referees?size &lt; 3)>
       <div class="row-group terms-box">
			<div class="row">
				<span class="terms-label">
					I understand that in accepting this declaration I am confirming
					that the information contained in this section is true and accurate. 
					I am aware that any subsequent offer of study may be retracted at any time
					if any of the information contained is found to be misleading or false.
				</span>
				<div class="terms-field">
		        	<input type="checkbox" name="acceptTermsRDCB" id="acceptTermsRDCB"/>
		        </div>
	            <input type="hidden" name="acceptTermsRDValue" id="acceptTermsRDValue"/>
			</div>	        
	    </div>
	    </#if>  
	
	
			<div class="buttons">
				<#if applicationForm.modifiable>
				<a class="button" type="button" id="refereeCancelButton" name="refereeCancelButton">Clear</a>
				<button class="blue" type="button" id="refereeCloseButton" name="refereeCloseButton">Close</button>
				<button class="blue" type="button" value="close" id="refereeSaveAndCloseButton">Save</button>
				<#else>
				<a id="refereeCloseButton" class="button blue">Close</a>   
				</#if> 	
			</div>
			
	</form>
	
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>
            
 <@spring.bind "referee.*" /> 
 
<#if (errorCode?? && errorCode=='false') || (message?? && message='close' && !spring.status.errorMessages?has_content)>	
<script type="text/javascript">
	$(document).ready(function(){
		$('#referee-H2').trigger('click');
	});
</script>
</#if>