<#assign errorCode = RequestParameters.errorCode! />
<#if applicationForm.employmentPositions?has_content>
	<#assign hasEmploymentPositions = true>
<#else>
	<#assign hasEmploymentPositions = false>
</#if>

<#import "/spring.ftl" as spring />
	
<h2 id="position-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
	Employment
</h2>

<div>

	<#if hasEmploymentPositions>
    	<table class="existing">
        	<colgroup>
            	<col style="width: 30px" />
                <col />
                <col style="width: 140px" />
                <col style="width: 140px" />
                <col style="width: 30px" />
			</colgroup>
            
            <thead>
            	<tr>
                	<th colspan="2">Position</th>
                    <th>From</th>
                    <th>To</th>
                    <th>&nbsp;</th>
                    <th id="last-col">&nbsp;</th>
                </tr>
			</thead>
            
            <tbody>
            
            	<#list applicationForm.employmentPositions as position>
	            	<tr>
	                    <td><a class="row-arrow 
	                    	<#if employmentPosition.id?? && position.id==employmentPosition.id>open</#if>" 
	                    	name="positionEditButton" id="position_${position.id?string('#######')}">-</a></td>
	                    <td>${(position.position?html)!}</td>
	                    <td>${(position.startDate?string('dd-MMM-yyyy'))!}</td>
	                    <td>${(position.endDate?string('dd-MMM-yyyy'))!}</td>
	                     
	                    <#if !applicationForm.isSubmitted()>
	                    	<td>		                		
		                		<a name="positionEditButton" data-desc="Edit" 
		                				id="position_${position.id?string('#######')}" class="button-edit button-hint">edit</a>
		                	</td>
	                    	<td>		                		
		                		<a name="deleteEmploymentButton" data-desc="Delete" 
		                				id="position_${position.id?string('#######')}" class="button-delete button-hint">delete</a>
		                	</td>
		            
						<#else>
		                	<td></td><td></td>		                		
		                </#if>
			        	                 
					
	                </tr>
	            </#list>
			</tbody>
			
		</table>
	</#if>
    
    <input type="hidden" id="positionId" name="positionId" value="${(employmentPosition.id?string('#######'))!}"/>
    
    <form>
    	
    	<div id="employmentForm">
    		<!-- Country -->
			<div class="row">
            	<span class="plain-label">Country<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerCountry'/>"></span>
               	<div class="field">
                	<select class="full" id="position_country" name="position_country"
                   		<#if applicationForm.isSubmitted()>disabled="disabled"</#if>>
                    	<option value="">Select...</option>
                    	<#list countries as country>
                     		<option value="${country.id?string('#######')}" <#if employmentPosition.employerCountry?? && employmentPosition.employerCountry.id == country.id> selected="selected"</#if>>${country.name}</option>
                     	</#list>
                 	 </select>
                 	 <@spring.bind "employmentPosition.employerCountry" /> 
            		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>						
                </div>
            </div>
        	<!-- Employer (company name) -->
            <div class="row">
            	<span class="plain-label">Employer Name<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerName'/>"></span>
                <div class="field">
                <#if !applicationForm.isSubmitted()>
                	<input class="full" type="text" id="position_employer_name" name="position_employer_name" 
                					value="${(employmentPosition.employerName?html)!}" placeholder="Provider of employment" />
                	 <@spring.bind "employmentPosition.employerName" /> 
            		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
                   
                <#else>
                    <input readonly="readonly" class="full" type="text" id="position_employer_name" name="position_employer_name" 
                                    value="${(employmentPosition.employerName?html)!}" placeholder="Provider of employment" />
                </#if>    
               	</div>
            </div>
            
        	<!-- Employer (company name) -->
            <div class="row">
            	<span class="plain-label">Employer Address<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerAddress'/>"></span>
                <div class="field">
                   <#if !applicationForm.isSubmitted()>
                  		<textarea cols="70" rows="3" class="max" maxlength='1000' id="position_employer_address" 
                  		name="position_employer_address" 
                  		placeholder="Employer's address">${(employmentPosition.employerAddress?html)!}</textarea>
						
						 <@spring.bind "employmentPosition.employerAddress" /> 
            			<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
					 <#else>
					    <textarea readonly="readonly" cols="70" rows="3" class="max" id="position_employer_address" 
                        name="position_employer_address"
                        placeholder="Employer's address">${(employmentPosition.employerAddress?html)!}</textarea>  
					 </#if>   
               	</div>
            </div>
            
            <!-- Position -->
            <div class="row">
            	<span class="plain-label">Position<em>*</em></span>
                 <span class="hint" data-desc="<@spring.message 'employmentDetails.position.position'/>"></span>
                <div class="field">
                    <#if !applicationForm.isSubmitted()>
                    	<input class="full" type="text" id="position_title" name="position_title" value="${(employmentPosition.position?html)!}" placeholder="Title of position" />
                         <@spring.bind "employmentPosition.position" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
                    <#else>
                        <input readonly="readonly" class="full" type="text" id="position_title" name="position_title" value="${(employmentPosition.position?html)!}" placeholder="Title of position" />
                    </#if> 
                </div>
            </div>
            
            <!-- Remit (job description) -->
            <div class="row">
                <span class="plain-label">Roles and Responsibilities<em>*</em></span>
               <span class="hint" data-desc="<@spring.message 'employmentDetails.position.remit'/>"></span>
                <div class="field">
                    <#if !applicationForm.isSubmitted()>
                  	<textarea cols="70" rows="3" class="max" maxlength='2000' id="position_remit" 
                  		name="position_remit" 
                  		placeholder="Summary of responsibilities">${(employmentPosition.remit?html)!}</textarea>
                  		
						 <@spring.bind "employmentPosition.remit" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
					
					 <#else>
					    <textarea readonly="readonly" cols="70" rows="3" class="max" id="position_remit" 
                        name="position_remit" 
                        placeholder="Summary of responsibilities">${(employmentPosition.remit?html)!}</textarea>  
					 </#if> 
					 
                </div>
         	</div>
             <!-- Language -->
            <div class="row">
                <span class="plain-label">Language of work<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.language'/>"></span>
                <div class="field">
                  	<select class="full" id="position_language" name="position_language"
                   		<#if applicationForm.isSubmitted()>disabled="disabled"</#if>>
                    	<option value="">Select...</option>
                    	<#list languages as language>
                     		<option value="${language.id?string('#######')}" <#if employmentPosition.language?? && employmentPosition.language.id == language.id> selected="selected"</#if>>${language.name}</option>
                     	</#list>
                 	 </select>
                 	  <@spring.bind "employmentPosition.language" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>							
                </div>
           	</div>
            <!-- Start date -->
            <div class="row">
                <span class="plain-label">Start Date<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.startDate'/>"></span>
                <div class="field">
                  	<input class="half date" type="text" id="position_startDate" name="position_startDate" value="${(employmentPosition.startDate?string('dd-MMM-yyyy'))!}"  <#if applicationForm.isSubmitted()>disabled="disabled"</#if>/>
                          
                  	 <@spring.bind "employmentPosition.startDate" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
                </div>
            </div>
            <div class="row">
               <span class="plain-label">Is this your current position?</span>
               <span class="hint" data-desc="<@spring.message 'employmentDetails.position.isOngoing'/>"></span>
               <div class="field">
               		<input type="checkbox" name="current" id="current" 	<#if employmentPosition.current> checked ="checked"</#if><#if applicationForm.isSubmitted()>disabled="disabled"</#if>/>                   		
       			</div>
            </div>
            <!-- End date -->
            <div class="row">
                <span id="posi-end-date-lb" class="plain-label">End Date<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.endDate'/>"></span>
                <div class="field" id="endDateField">
                  	<input class="half date" id="position_endDate" name="position_endDate" value="${(employmentPosition.endDate?string('dd-MMM-yyyy'))!}" <#if employmentPosition.current> disabled ="disabled"</#if>/>                  	
                  	<@spring.bind "employmentPosition.endDate" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>			
                </div>
					
           	</div>
			<#if !applicationForm.isSubmitted()>
	           	<!-- Add another button -->
	            <div class="row">
	            	<div class="field">
	                	<a id="addPosisionButton" class="button blue">Add Employment</a>
	                </div>
	            </div>
           	</#if>
		</div>

		<div class="buttons">
			<#if !applicationForm.isSubmitted()>
            	<a class="button" type="button" id="positionCancelButton" name="positionCancelButton">Cancel</a>
            	<button class="blue" type="button" id="positionCloseButton" name="positionCloseButton">Close</button>
                <button class="blue" type="button" value="add" id="positionSaveAndCloseButton" name="positionSaveAndCloseButton">Save</button>
            <#else>
                <a id="positionCloseButton" class="button blue">Close</a>
            </#if>    
        </div>

	</form>
</div>
	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/employmentPosition.js'/>"></script>
 <@spring.bind "employmentPosition.*" /> 
 
<#if (errorCode?? && errorCode=='false') || (message?? && message='close' && !spring.status.errorMessages?has_content)>	
<script type="text/javascript">
	$(document).ready(function(){
		$('#position-H2').trigger('click');
	});
</script>
</#if>