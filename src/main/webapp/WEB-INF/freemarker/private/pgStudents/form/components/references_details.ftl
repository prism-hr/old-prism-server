<#import "/spring.ftl" as spring />
<#if applicationForm.referees?has_content>
	<#assign hasReferees = true>
<#else>
	<#assign hasReferees = false>
</#if> 
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
    
	   		<!-- First name -->
      		<div class="row">
        		<span class="plain-label">First Name<em>*</em></span>
        		<span class="hint"></span>
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
        		<span class="hint"></span>
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
        		<span class="hint"></span>
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
        		<span class="hint"></span>
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
    	     
      		<!-- Address body -->
      		<div class="row">
        		<span class="plain-label">Address<em>*</em></span>
        		<span class="hint"></span>
        		<div class="field">
        		<#if !applicationForm.isSubmitted()>
          			<textarea class="max" rows="6" cols="70" id="ref_address_location" 
          				name="ref_address_location">${(referee.addressLocation?html)!}</textarea> 
          				 <@spring.bind "referee.addressLocation" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>           		
                	<#else>
                	   <textarea readonly="readonly" class="max" rows="6" cols="70" id="ref_address_location" 
                        name="ref_address_location" value="${(referee.addressLocation?html)!}"></textarea>
                	</#if> 
        		</div>
      		</div>
      		
    
      		<!-- Country -->
      		<div class="row">
        		<span class="plain-label">Country<em>*</em></span>
        		<span class="hint"></span>
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
      		<!-- Email address -->
      		<div class="row">
        		<span class="plain-label">Email<em>*</em></span>
        		<span class="hint"></span>
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
        		<span class="hint"></span>
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
        		<span class="hint"></span>
        		<div class="field">
        			<#if !applicationForm.isSubmitted()>
        			<input class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}"/> 
                     
                	<#else>
                	   <input readonly="readonly" class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}"/>
                	</#if>
          			
        		</div>
      		</div>
      		
      		<!-- Add another button -->
      		<div class="row">
      			<div class="field">
      				<a id="addReferenceButton" class="button blue">Add Reference</a>
      			</div>
      		</div>
      		
    	</div>
		<#if applicationForm.isSubmitted()>
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
    	 <#if !applicationForm.isSubmitted()>
      		 <a class="button" type="button" id="refereeCancelButton" name="refereeCancelButton">Cancel</a>
      		 <button class="blue" type="button" id="refereeCloseButton" name="refereeCloseButton">Close</button>
      		 <button class="blue" type="button" value="close" id="refereeSaveButton">Save</button>
      	 <#else>
            <a id="refereeCloseButton" class="button blue">Close</a>   
        </#if> 	
    	</div>

	</form>

</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>
            
 <@spring.bind "referee.*" /> 
 
<#if !message?? || (!spring.status.errorMessages?has_content && (message=='close'))  >
<script type="text/javascript">
	$(document).ready(function(){
		$('#referee-H2').trigger('click');
	});
</script>
</#if>