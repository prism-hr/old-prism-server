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
              	<col style="width: 160px" />
              	<col style="width: 160px" />
              	<col style="width: 240px" />
              	<col />          
              	<col />
              	<col style="width: 30px" />
        	</colgroup>
        
        	<thead>
          		<tr>
                    <th id="primary-header" colspan="2">First name</th>
                    <th>Surname</th>
                    <th>Job Title</th>
                    <th>Email</th>              
                    <th>&nbsp;</th>  
                    <th id="last-col">&nbsp;</th>
              	</tr>
        	</thead>
        	
        	<tbody>
        		<#list applicationForm.referees as existingReferee>
                  	<tr>
	                    <td><a class="row-arrow">-</a></td>
	                    <td>${(existingReferee.firstname?html)!}</td>
	                    <td>${(existingReferee.lastname?html)!}</td>
	                    <td>${(existingReferee.jobTitle?html)!}</td>
	                    <td>${(existingReferee.email?html)!}</td>
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
  	
    	<div>
	   		<!-- First name -->
      		<div class="row">
        		<span class="plain-label">First Name<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.firstname'/>"></span>
        		<div class="field">
        			<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        				<input class="full" id="ref_firstname" name="ref_firstname" value="${(referee.firstname?html)!}"/>  

                		
                	<#else>
                	   <input readonly="readonly" class="full" id="ref_firstname" name="ref_firstname" value="${(referee.firstname?html)!}"/>
                	</#if>
          			
        		</div>
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
        		<span class="plain-label">Last Name<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.lastname'/>"></span>
        		<div class="field">
	        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
	        			<input class="full" id="ref_lastname" name="ref_lastname" value="${(referee.lastname?html)!}"/>	          


	            	<#else>
	            	   <input readonly="readonly" class="full" id="ref_lastname" name="ref_lastname" value="${(referee.lastname?html)!}"/>
	            	</#if>
        		</div>
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

    	<div>
    
      		<!-- Employer / company name -->
      		<div class="row">
        		<span class="plain-label">Employer<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.employer'/>"></span>
        		<div class="field">
        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
          				<input class="full" id="ref_employer" name="ref_employer" value="${(referee.jobEmployer?html)!}"/> 

                		             
                	<#else>
                	   <input readonly="readonly" class="full" id="ref_employer" name="ref_employer" value="${(referee.jobEmployer?html)!}"/>
                	</#if>
        		</div>
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
        		<span class="plain-label">Position<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.position'/>"></span>
        		<div class="field">
        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
          			<input class="full" id="ref_position" name="ref_position" value="${(referee.jobTitle?html)!}"/>
 
  
                	<#else>
                	   <input readonly="readonly" class="full" id="ref_position" name="ref_position" value="${(referee.jobTitle?html)!}"/>
                	</#if>   
        		</div>
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

    	<div>
    	     
			<div class="row">
          		<label class="group-heading-label">Address</label>
          	</div> 
      		<!-- Address body -->
      		<div class="row">
        		<span class="plain-label">Address<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.address'/>"></span>
        		<div class="field">
        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
          			<textarea class="max" rows="6" cols="70" maxlength='200' id="ref_address_location" 
          				name="ref_address_location">${(referee.addressLocation?html)!}</textarea> 
 
                		           		
                	<#else>
                	   <textarea readonly="readonly" class="max" rows="6" cols="70" id="ref_address_location" 
                        name="ref_address_location" >${(referee.addressLocation?html)!}</textarea>
                	</#if> 
        		</div>
      		</div>
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
        		<span class="plain-label">Country<em>*</em></span>
        		 <span class="hint" data-desc="<@spring.message 'referee.country'/>"></span>
        		<div class="field">
        		<select class="full" name="ref_address_country" id="ref_address_country"
                <#if !referee.editable || (!referee.id?? &&  applicationForm.referees?size &gt;= 3)>
                                disabled="disabled"
                </#if>>
                <option value="">Select...</option>
                    <#list countries as country>
                        <option value="${country.id?string('#######')}" <#if referee.addressCountry?? && referee.addressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
                    </#list>
                </select>

                    
        		</div>
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

    	<div>
    	
    		<div class="row">
          		<label class="group-heading-label">Contact Details</label>
          	</div> 
    		
      		<!-- Email address -->
      		<div class="row">
        		<span class="plain-label">Email<em>*</em></span>
        		 <span class="hint" data-desc="<@spring.message 'referee.email'/>"></span>
        		<div class="field">
        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
          			<input class="full" type="email" id="ref_email" name="ref_email" value="${(referee.email?html)!}"/> 

                	                
                	<#else>
                	   <input readonly="readonly" class="full" type="email" id="ref_email" name="ref_email" value="${(referee.email?html)!}"/>
                	</#if>
        		</div>
      		</div>
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
        		<span class="plain-label">Telephone<em>*</em></span>
				<span class="hint" data-desc="<@spring.message 'referee.telephone'/>"></span>
        		<div class="field">
        		<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        			<input class="full" id="refPhoneNumber" name="refPhoneNumber" value="${(referee.phoneNumber?html)!}"/> 
 
                	<#else>
                	   <input readonly="readonly" class="full" id="refPhoneNumber" name="refPhoneNumber" value="${(referee.phoneNumber?html)!}"/>
                	</#if>
          			
        		</div>
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
        		<span class="plain-label">Skype Name</span>
        		<span class="hint" data-desc="<@spring.message 'referee.skype'/>"></span>
        		<div class="field">
        			<#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        			<input class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}"/> 
                     
                	<#else>
                	   <input readonly="readonly" class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}"/>
                	</#if>
          			
        		</div>
      		</div>
		<#if referee.id??>
		</div>
		<div>
			<div>
          	            
          		<div class="row">
                  	<span class="label">Responded</span>                    
                    <div class="field" id="referenceUpdated">
                    	<#if referee.declined || referee.hasProvidedReference()>
                   		 	Yes
                   		 <#else>
                   		 	No
                   		 </#if>
                    </div>
                </div>
        	</div>
		</#if>
		<#if referee.editable >
      		<!-- Add another button -->
      		<div class="row">
      			<div class="field">
      				<#if !referee.id?? && applicationForm.referees?size &lt; 3>
      					<a id="addReferenceButton" class="button blue">Submit</a>
      				<#elseif referee.id??>
      					<a id="addReferenceButton" class="button blue">Update Reference</a>
      				</#if>
      			</div>
      		</div>
      	</#if>		
    	</div>
	
       <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED') && (referee.id?? || applicationForm.referees?size &lt; 3)>
       <div class="terms-box">
			<div class="row">
				<span class="terms-label">
					I understand that in accepting this declaration I am confirming
					that the information contained in this application is true and accurate. 
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