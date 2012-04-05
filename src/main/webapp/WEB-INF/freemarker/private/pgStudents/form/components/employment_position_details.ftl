<#if applicationForm.employmentPositions?has_content>
	<#assign hasEmploymentPositions = true>
<#else>
	<#assign hasEmploymentPositions = false>
</#if>

<#import "/spring.ftl" as spring />
	
	<h2 id="position-H2" class="empty">
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
	                </tr>
				</thead>
	            
	            <tbody>
	            
	            	<#list applicationForm.employmentPositions as position>
		            	<tr>
		                    <td><a class="row-arrow" name="positionEditButton" id="position_${position.id?string('#######')}">-</a></td>
		                    <td>${(position.position_title?html)!}</td>
		                    <td>${(position.position_startDate?string('dd-MMM-yyyy'))!}</td>
		                    <td>${(position.position_endDate?string('dd-MMM-yyyy'))!}</td>
		                     <td>
		                     	 <#if !applicationForm.isSubmitted()>
				                  	<form method="Post" action="<@spring.url '/deleteentity/employment'/>" style="padding:0">
			                			<input type="hidden" name="id" value="${position.id?string('#######')}"/>		                		
			                			<a name="deleteButton" class="button-delete">delete</a>
			                		</form>
			                		</#if>
				        		</td>
		                    
							<input type="hidden" id="${position.id?string('#######')}_positionId" value="${position.id?string('#######')}"/>
                            <input type="hidden" id="${position.id?string('#######')}_employer" value="${(position.position_employer?html)!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_remit" value="${(position.position_remit?html)!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_language" value="${(position.position_language.id?string('#######'))!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_positionTitle" value="${(position.position_title?html)!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_positionStartDate" value="${(position.position_startDate?string('dd-MMM-yyyy'))!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_positionEndDate" value="${(position.position_endDate?string('dd-MMM-yyyy'))!}"/>
		                    <input type="hidden"  id="${position.id?string('#######')}_positionCompleted" value="${position.completed}"/> 
						
		                </tr>
		            </#list>
				</tbody>
				
			</table>
		</#if>
        
        <input type="hidden" id="positionId" name="positionId" value="${(employmentPosition.positionId?html)!}"/>
        
        <form>
        	
        	<div>
        		<!-- Country -->
				<div class="row">
                	<span class="label">Country<em>*</em></span>
                    <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerCountry'/>"></span>
                   	<div class="field">
                    	<select class="full" id="position_country" name="position_country"
                       		<#if applicationForm.isSubmitted()>disabled="disabled"</#if>>
                        	<option value="">Select...</option>
                        	<#list countries as country>
                         		<option value="${country.id?string('#######')}" <#if employmentPosition.employerCountry?? && employmentPosition.employerCountry.id == country.id> selected="selected"</#if>>${country.name}</option>
                         	</#list>
                     	 </select>						
                    </div>
                </div>
            	<!-- Employer (company name) -->
                <div class="row">
                	<span class="label">Employer Name<em>*</em></span>
                    <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerName'/>"></span>
                    <div class="field">
                    <#if !applicationForm.isSubmitted()>
                    	<input class="full" type="text" id="position_employer_name" name="position_employer_name" 
                    					value="${(employmentPosition.employerName?html)!}" placeholder="Provider of employment" />
                       
                    <#else>
                        <input readonly="readonly" class="full" type="text" id="position_employer_name" name="position_employer_name" 
                                        value="${(employmentPosition.employerName?html)!}" placeholder="Provider of employment" />
                    </#if>    
                   	</div>
                </div>
                
            	<!-- Employer (company name) -->
                <div class="row">
                	<span class="label">Employer Address<em>*</em></span>
                    <span class="hint" data-desc="<@spring.message 'employmentDetails.position.employerAddress'/>"></span>
                    <div class="field">
	                   <#if !applicationForm.isSubmitted()>
                      	<textarea cols="70" rows="3" class="max" id="position_employer_address" 
                      		name="position_employer_address" 
                      		placeholder="Employer's address">${(employmentPosition.employerAddress?html)!}</textarea>
							
						
						 <#else>
						    <textarea readonly="readonly" cols="70" rows="3" class="max" id="position_employer_address" 
                            name="position_employer_address" value="${(employmentPosition.employerAddress?html)!}" 
                            placeholder="Employer's address"></textarea>  
						 </#if>   
                   	</div>
                </div>
                
                <!-- Position -->
                <div class="row">
                	<span class="label">Position<em>*</em></span>
                     <span class="hint" data-desc="<@spring.message 'employmentDetails.position.position'/>"></span>
                    <div class="field">
                        <#if !applicationForm.isSubmitted()>
                    	<input class="full" type="text" id="position_title" name="position_title" 
                    					value="${(employmentPosition.position?html)!}" placeholder="Title of position" />
                        
                        <#else>
                            <input readonly="readonly" class="full" type="text" id="position_title" name="position_title" 
                                        value="${(employmentPosition.position?html)!}" placeholder="Title of position" />
                        </#if> 
                    </div>
                </div>
                
                <!-- Remit (job description) -->
                <div class="row">
                    <span class="label">Description<em>*</em></span>
                   <span class="hint" data-desc="<@spring.message 'employmentDetails.position.remit'/>"></span>
                    <div class="field">
                        <#if !applicationForm.isSubmitted()>
                      	<textarea cols="70" rows="3" class="max" id="position_remit" 
                      		name="position_remit" 
                      		placeholder="Summary of responsibilities">${(employmentPosition.remit?html)!}</textarea>
							
						
						 <#else>
						    <textarea readonly="readonly" cols="70" rows="3" class="max" id="position_remit" 
                            name="position_remit" value="${(employmentPosition.remit?html)!}" 
                            placeholder="Summary of responsibilities"></textarea>  
						 </#if> 
						 
                    </div>
             	</div>
                 <!-- Language -->
                <div class="row">
                    <span class="label">Language of work<em>*</em></span>
                    <span class="hint" data-desc="<@spring.message 'employmentDetails.position.language'/>"></span>
                    <div class="field">
                      	<select class="full" id="position_language" name="position_language"
                       		<#if applicationForm.isSubmitted()>disabled="disabled"</#if>>
                        	<option value="">Select...</option>
                        	<#list languages as language>
                         		<option value="${language.id?string('#######')}" <#if employmentPosition.language?? && employmentPosition.language.id == language.id> selected="selected"</#if>>${language.name}</option>
                         	</#list>
                     	 </select>						
                    </div>
               	</div>
                <!-- Start date -->
                <div class="row">
                    <span class="label">Start Date<em>*</em></span>
                   <span class="hint" data-desc="<@spring.message 'employmentDetails.position.startDate'/>"></span>
                    <div class="field">
                      	<input class="half date" type="text" id="position_startDate" name="position_startDate" 
                      			value="${(employmentPosition.startDate?string('dd-MMM-yyyy'))!}"
                      			<#if applicationForm.isSubmitted()>
                                          disabled="disabled"
                                </#if>>
                        </input>        
                      	
                    </div>
                </div>
                <div class="row">
                       <span class="label">Is this your current position?<em>*</em></span>
                       <span class="hint" data-desc="<@spring.message 'employmentDetails.position.isOngoing'/>"></span>
                       		<input type="checkbox" name="completedPositionCB" id="completedPositionCB"
                       		<#if employmentPosition?? && employmentPosition.completed?? &&  employmentPosition.completed =='NO'>
                                          checked
                              </#if>
                       		<#if applicationForm.isSubmitted()>
                                          disabled="disabled"
                              </#if>/>
                       		<input type="hidden" name="completedPosition" id="completedPosition"/>
               			 </div>
                
                <!-- End date -->
                <div class="row">
                    <span class="label">End Date</span>
                    <span class="hint" data-desc="<@spring.message 'employmentDetails.position.endDate'/>"></span>
                    <div class="field" id="endDateField">
                      	<input class="half date" type="hidden" id="position_endDate" name="position_endDate" 
                      			value="${(employmentPosition.endDate?string('dd-MMM-yyyy'))!}"
                                          disabled="disabled">
                      	</input>		
                    </div>
						
               	</div>
			</div>

			<div class="buttons">
			<#if !applicationForm.isSubmitted()>
            	<a class="button" type="button" id="positionCancelButton" name="positionCancelButton">Cancel</a>
            	<button class="blue" type="button" id="positionCloseButton" name="positionCloseButton">Close</button>
                <button class="blue" type="button" value="close" id="positionSaveAndCloseButton" name="positionSaveButton">Save and Close</button>
                <button class="blue" type="button" value="add" id="positionSaveAndAddButton" name="positionSaveAndAddButton">Save and Add</button>
            <#else>
                <a id="positionCloseButton" class="button blue">Close</a>
            </#if>    
            </div>

		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/employmentPosition.js'/>"></script>
<#if (result?? && result.hasErrors()) || add?? > 

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#position-H2').trigger('click');
	});
</script>
</#if>