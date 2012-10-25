<#assign errorCode = RequestParameters.errorCode! />
<#if applicationForm.employmentPositions?has_content>
	<#assign hasEmploymentPositions = true>
<#else>
	<#assign hasEmploymentPositions = false>
</#if>
<#setting locale = "en_US">
<#import "/spring.ftl" as spring />

<a name="position-details"></a>	
<h2 id="position-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
	Employment
</h2>

<div>

	<#if hasEmploymentPositions>
    	<table class="existing">
        	<colgroup>
				<col style="width: 30px">
				<col>
				<col style="width: 220px">
				<col style="width: 30px">
				<col style="width: 30px">
			</colgroup>
            
            <thead>
            	<tr>
                	<th id="primary-header" colspan="2">Position</th>
                    <th>Dates</th>
                    <th>&nbsp;</th>
                    <th id="last-col">&nbsp;</th>
                </tr>
			</thead>
            
            <tbody>
            
            	<#list applicationForm.employmentPositions as position>
	            	<tr>
	                    <td><a class="row-arrow">-</a></td>
	                    <td>${(position.position?html)!} ${(position.employerName?html)!} ${(position.employerCountry.name)!}</td>
	                    <td>${(position.startDate?string('dd MMM yyyy'))!} - ${(position.endDate?string('dd MMM yyyy'))!"Ongoing"}</td>
	                     
	                    	<td>		                		
		                		<a name="positionEditButton" <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>data-desc="Edit" <#else>data-desc="Show"</#if>
		                				id="position_${encrypter.encrypt(position.id)}" class="button-edit button-hint">edit</a>
		                	</td>
	                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
	                    	<td>		                		
		                		<a name="deleteEmploymentButton" data-desc="Delete" 
		                				id="position_${encrypter.encrypt(position.id)}" class="button-delete button-hint">delete</a>
		                	</td>
		            
						<#else>
		                	<td></td><td></td>		                		
		                </#if>
			        	                 
					
	                </tr>
	            </#list>
			</tbody>
			
		</table>
	</#if>
    
    <input type="hidden" id="positionId" name="positionId" value="<#if employmentPosition?? && employmentPosition.id??>${(encrypter.encrypt(employmentPosition.id))!}</#if>"/>
    
    <form>
    
				<#if errorCode?? && errorCode=="true">
					<div class="section-error-bar">
						<span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>             	
						<@spring.message 'employmentDetails.sectionInfo'/>
				 	</div>
			 	<#else>
				 	<div id="emp-info-bar-div" class="section-info-bar">
						<@spring.message 'employmentDetails.sectionInfo'/> 
					</div>	
				</#if>
    
    	
    	<div class="row-group" id="employmentForm">
    		<!-- Country -->
			<div class="row">
            	<span class="plain-label">Country<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerCountry'/>"></span>
               	<div class="field">
                	<select class="full" id="position_country" name="position_country"
                   		<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>>
                    	<option value="">Select...</option>
                    	<#list countries as country>
                     		<option value="${encrypter.encrypt(country.id)}" <#if employmentPosition.employerCountry?? && employmentPosition.employerCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>
                     	</#list>
                 	 </select>
                 	 
                </div>
            </div>
            <@spring.bind "employmentPosition.employerCountry" /> 
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
            
        	<!-- Employer (company name) -->
            <div class="row">
            	<span class="plain-label">Employer Name<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerName'/>"></span>
                <div class="field">
                <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
                	<input class="full" type="text" id="position_employer_name" name="position_employer_name" 
                					value="${(employmentPosition.employerName?html)!}" placeholder="Provider of employment" />
                	 
                   
                <#else>
                    <input readonly="readonly" class="full" type="text" id="position_employer_name" name="position_employer_name" 
                                    value="${(employmentPosition.employerName?html)!}" placeholder="Provider of employment" />
                </#if>    
               	</div>
            </div>
            <@spring.bind "employmentPosition.employerName" /> 
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
            
            
        	<!-- Employer (company name) -->
            <div class="row">
            	<span class="plain-label">Employer Address<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerAddress'/>"></span>
                <div class="field">
                   <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
                  		<textarea cols="80" rows="5" class="max"  ='1000' id="position_employer_address" 
                  		name="position_employer_address" 
                  		placeholder="Employer's address">${(employmentPosition.employerAddress?html)!}</textarea>
						
						  
            				
					 <#else>
					    <textarea readonly="readonly" cols="80" rows="5" class="max" id="position_employer_address" 
                        name="position_employer_address"
                        placeholder="Employer's address">${(employmentPosition.employerAddress?html)!}</textarea>  
					 </#if>   
               	</div>
            </div>
            <@spring.bind "employmentPosition.employerAddress" />
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
            
            <!-- Position -->
            <div class="row">
            	<span class="plain-label">Position<em>*</em></span>
                 <span class="hint" data-desc="<@spring.message 'employmentDetails.position.position'/>"></span>
                <div class="field">
                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
                    	<input class="full" type="text" id="position_title" name="position_title" value="${(employmentPosition.position?html)!}" placeholder="Title of position" />
                         
                			
                    <#else>
                        <input readonly="readonly" class="full" type="text" id="position_title" name="position_title" value="${(employmentPosition.position?html)!}" placeholder="Title of position" />
                    </#if> 
                </div>
            </div>
            	<@spring.bind "employmentPosition.position" />	
            	<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
            
            <!-- Remit (job description) -->
            <div class="row">
                <span class="plain-label">Roles and Responsibilities<em>*</em></span>
               <span class="hint" data-desc="<@spring.message 'employmentDetails.position.remit'/>"></span>
                <div class="field">
                    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
                  	<textarea cols="80" rows="5" class="max" id="position_remit" 
                  		name="position_remit" 
                  		placeholder="Summary of responsibilities">${(employmentPosition.remit?html)!}</textarea>
					 <#else>
					    <textarea readonly="readonly" cols="80" rows="5" class="max" id="position_remit" 
                        name="position_remit" 
                        placeholder="Summary of responsibilities">${(employmentPosition.remit?html)!}</textarea>  
					 </#if> 
					 
                </div>
         	</div>
         		 <@spring.bind "employmentPosition.remit" /> 
         		<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
           	
            <!-- Start date -->
            <div class="row">
                <span class="plain-label">Start Date<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.startDate'/>"></span>
                <div class="field">
                  	<input class="half date" type="text" id="position_startDate" name="position_startDate" value="${(employmentPosition.startDate?string('dd MMM yyyy'))!}"  <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>
                          
                  
                			
                </div>
            </div>
            	 <@spring.bind "employmentPosition.startDate" /> 
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
            
        </div>
        
        <div class="row-group">
            <div class="row">
               <span class="plain-label">Is this your current position?</span>
               <span class="hint" data-desc="<@spring.message 'employmentDetails.position.isOngoing'/>"></span>
               <div class="field">
               		<input type="checkbox" name="current" id="current" 	<#if employmentPosition.current> checked ="checked"</#if><#if (applicationForm.isDecided() || applicationForm.isWithdrawn())>disabled="disabled"</#if>/>                   		
       			</div>
            </div>
            <!-- End date -->
            <div class="row">
                <span id="posi-end-date-lb" class="plain-label<#if employmentPosition.current> grey-label</#if>">End Date<#if !employmentPosition.current><em>*</em></#if></span>
                <span class="hint" data-desc="<@spring.message 'employmentDetails.position.endDate'/>"></span>
                <div class="field" id="endDateField">
                  	<input class="half date" id="position_endDate" name="position_endDate" value="${(employmentPosition.endDate?string('dd MMM yyyy'))!}" <#if employmentPosition.current ||  applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if>/>                  	
                 
                					
                </div>
					
           	</div>
           	 	<@spring.bind "employmentPosition.endDate" /> 
           		<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span id="position-enddate-error" class="invalid">${error}</span>
						</div>
					</div>
				</#list>
           	
				<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
	           	<!-- Add another button -->
	            <div class="row">
	            	<div class="field">
	                	<button type="button" id="addPosisionButton" class="blue"><#if employmentPosition?? && employmentPosition.id??>Update<#else>Add</#if></button>
	                </div>
	            </div>
           		</#if>
			</div>

	       <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
	    	  <@spring.bind "employmentPosition.acceptedTerms" />
		       	<#if spring.status.errorMessages?size &gt; 0>        
				    <div class="row-group terms-box invalid" >
		
		      	<#else>
		    		<div class="row-group terms-box" >
		     	 </#if>
				<div class="row">
					<span class="terms-label">
						Confirm that the information that you have provided in this section is true 
						and correct. Failure to provide true and correct information may result in a 
						subsequent offer of study being withdrawn.				
					</span>
					<div class="terms-field">
			        	<input type="checkbox" name="acceptTermsEPCB" id="acceptTermsEPCB"/>
			        </div>
		            <input type="hidden" name="acceptTermsEPValue" id="acceptTermsEPValue"/>
				</div>	        
		    </div>
		    </#if>  

			<div class="buttons">
				<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
				<button class="clear" type="button" id="positionclearButton" name="positionclearButton">Clear</button>
				<button class="blue" type="button" id="positionCloseButton" name="positionCloseButton">Close</button>
				<button class="blue" type="button" value="add" id="positionSaveAndCloseButton" name="positionSaveAndCloseButton">Save</button>
				<#else>
				<button type="button" id="positionCloseButton" class="blue">Close</button>
				</#if>    
			</div>

		</div>
	</form>
</div>
	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/employmentPosition.js'/>"></script>
