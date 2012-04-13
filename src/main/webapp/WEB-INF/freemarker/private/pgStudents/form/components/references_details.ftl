<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />
<#if applicationForm.referees?has_content>
	<#assign hasReferees = true>
<#else>
	<#assign hasReferees = false>
</#if> 
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
              	<col style="width: 260px" />
              	<col />
              	<#if applicationForm.isSubmitted()>
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
                    <#if applicationForm.isSubmitted()>
                    	<th>Responded</th>
                    <#else>
                    	<th>&nbsp;</th>
                    	<th id="last-col">&nbsp;</th>
                    </#if>
                    
              	</tr>
        	</thead>
        	
        	<tbody>
        		<#list applicationForm.referees as existingReferee>
                  	<tr>
	                    <td><a class="row-arrow <#if referee.id?? && existingReferee.id==referee.id>open</#if>" id="referee_${existingReferee.id?string('#######')}" name ="editRefereeLink">-</a></td>
	                    <td>${(existingReferee.firstname?html)!}</td>
	                    <td>${(existingReferee.lastname?html)!}</td>
	                    <td>${(existingReferee.jobTitle?html)!}</td>
	                    <td>${(existingReferee.email?html)!}</td>
	                     <#if applicationForm.isSubmitted()>
		                    <td>
		                    	<#if existingReferee.hasProvidedReference() >Yes<#else>No</#if>
		                    </td>
	                    <#else>
		                    <td>
		                    	<a name="editRefereeLink" id="referee_${existingReferee.id?string('#######')}" class="button-edit">edit</a>
				        	</td>
		                    <td>
		                    	<a name="deleteRefereeButton" id="referee_${existingReferee.id?string('#######')}" class="button-delete">delete</a>
				        	</td>
	                     </#if>    
              	</#list>
        	</tbody>
      	
      	</table>
  	</#if>
  	
  	<input type="hidden" id="refereeId" name="refereeId" value="${(referee.id?string('#######'))!}" />
  	
  	<form>
    
    	<div>
        	<#if errorCode?? && errorCode =="true">
				<div class="row">              	
					<span class="invalid">Please provide details of at least three referees.<p></p></span>
			     </div>            	
			</#if>
	   		<!-- First name -->
      		<div class="row">
        		<span class="plain-label">First Name<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.firstname'/>"></span>
        		<div class="field">
        			<#if !applicationForm.isSubmitted()>
        				<input class="full" id="ref_firstname" name="ref_firstname" value="${(referee.firstname?html)!}"/>  
        				<@spring.bind "referee.firstname" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                	<#else>
                	   <input readonly="readonly" class="full" id="ref_firstname" name="ref_firstname" value="${(referee.firstname?html)!}"/>
                	</#if>
          			
        		</div>
      		</div>
    
      		<!-- Last name -->
      		<div class="row">
        		<span class="plain-label">Last Name<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.lastname'/>"></span>
        		<div class="field">
	        		<#if !applicationForm.isSubmitted()>
	        			<input class="full" id="ref_lastname" name="ref_lastname" value="${(referee.lastname?html)!}"/>	          
	        			<@spring.bind "referee.lastname" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
	            	<#else>
	            	   <input readonly="readonly" class="full" id="ref_lastname" name="ref_lastname" value="${(referee.lastname?html)!}"/>
	            	</#if>
        		</div>
      		</div>
    
    	
    	</div>

    	<div>
    
      		<!-- Employer / company name -->
      		<div class="row">
        		<span class="plain-label">Employer<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.employer'/>"></span>
        		<div class="field">
        			<#if !applicationForm.isSubmitted()>
          				<input class="full" id="ref_employer" name="ref_employer" value="${(referee.jobEmployer?html)!}"/> 
          				<@spring.bind "referee.jobEmployer" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>             
                	<#else>
                	   <input readonly="readonly" class="full" id="ref_employer" name="ref_employer" value="${(referee.jobEmployer?html)!}"/>
                	</#if>
        		</div>
      		</div>
    
      		<!-- Position title -->
      		<div class="row">
        		<span class="plain-label">Position<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.position'/>"></span>
        		<div class="field">
        		<#if !applicationForm.isSubmitted()>
          			<input class="full" id="ref_position" name="ref_position" value="${(referee.jobTitle?html)!}"/>
                   		 <@spring.bind "referee.jobTitle" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>  
                	<#else>
                	   <input readonly="readonly" class="full" id="ref_position" name="ref_position" value="${(referee.jobTitle?html)!}"/>
                	</#if>   
        		</div>
      		</div>
      		
    	</div>

    	<div>
    	     
			<div class="row">
          		<label class="label">Address</label>
          	</div> 
      		<!-- Address body -->
      		<div class="row">
        		<span class="plain-label">Address<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'referee.address'/>"></span>
        		<div class="field">
        		<#if !applicationForm.isSubmitted()>
          			<textarea class="max" rows="6" cols="70" maxlength='200' id="ref_address_location" 
          				name="ref_address_location">${(referee.addressLocation?html)!}</textarea> 
          				 <@spring.bind "referee.addressLocation" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>           		
                	<#else>
                	   <textarea readonly="readonly" class="max" rows="6" cols="70" id="ref_address_location" 
                        name="ref_address_location" >${(referee.addressLocation?html)!}</textarea>
                	</#if> 
        		</div>
      		</div>
      		
    
      		<!-- Country -->
      		<div class="row">
        		<span class="plain-label">Country<em>*</em></span>
        		 <span class="hint" data-desc="<@spring.message 'referee.country'/>"></span>
        		<div class="field">
        		<select class="full" name="ref_address_country" id="ref_address_country"
                <#if applicationForm.isSubmitted()>
                                disabled="disabled"
                </#if>>
                <option value="">Select...</option>
                    <#list countries as country>
                        <option value="${country.id?string('#######')}" <#if referee.addressCountry?? && referee.addressCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>               
                    </#list>
                </select>
                 <@spring.bind "referee.addressCountry" /> 
                <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>    
        		</div>
      		</div>
    	
    	</div>

    	<div>
    	
    		<div class="row">
          		<label class="label">Contact Details</label>
          	</div> 
    		
      		<!-- Email address -->
      		<div class="row">
        		<span class="plain-label">Email<em>*</em></span>
        		 <span class="hint" data-desc="<@spring.message 'referee.email'/>"></span>
        		<div class="field">
        		<#if !applicationForm.isSubmitted()>
          			<input class="full" type="email" id="ref_email" name="ref_email" value="${(referee.email?html)!}"/> 
          			 <@spring.bind "referee.email" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>                
                	<#else>
                	   <input readonly="readonly" class="full" type="email" id="ref_email" name="ref_email" value="${(referee.email?html)!}"/>
                	</#if>
        		</div>
      		</div>

      		<!-- Telephone -->
      		
     		
            <div class="row">
        		<span class="plain-label">Telephone<em>*</em></span>
				<span class="hint" data-desc="<@spring.message 'referee.telephone'/>"></span>
        		<div class="field">
        			<#if !applicationForm.isSubmitted()>
        			<input class="full" id="refPhoneNumber" name="refPhoneNumber" value="${(referee.phoneNumber?html)!}"/> 
                      <@spring.bind "referee.phoneNumber" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>             
                	<#else>
                	   <input readonly="readonly" class="full" id="refPhoneNumber" name="refPhoneNumber" value="${(referee.phoneNumber?html)!}"/>
                	</#if>
          			
        		</div>
      		</div>

          	<!-- Skype address -->
          
            <div class="row">
        		<span class="plain-label">Skype Name</span>
        		<span class="hint" data-desc="<@spring.message 'referee.skype'/>"></span>
        		<div class="field">
        			<#if !applicationForm.isSubmitted()>
        			<input class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}"/> 
                     
                	<#else>
                	   <input readonly="readonly" class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}"/>
                	</#if>
          			
        		</div>
      		</div>
		<#if applicationForm.isSubmitted()>
		</div>
		<div>
			<div>
          	            
          		<div class="row">
                  	<span class="label">Reference</span>                    
                    <div class="field" id="referenceUpdated">
                    	<#if referee.hasProvidedReference()>
                    		Provided ${(referee.reference.lastUpdated?string('dd-MMM-yyyy'))!}
                    	<#elseif referee.id??>
                    		Not provided
                    	</#if>
                    </div>
                </div>
        	</div>
		</#if>
		<#if !applicationForm.isSubmitted()>
      		<!-- Add another button -->
      		<div class="row">
      			<div class="field">
      				<a id="addReferenceButton" class="button blue">Add Reference</a>
      			</div>
      		</div>
      	</#if>		
    	</div>
		
    	<div class="buttons">
    	 <#if !applicationForm.isSubmitted()>
      		 <a class="button" type="button" id="refereeCancelButton" name="refereeCancelButton">Cancel</a>
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