<#if model.applicationForm.employmentPositions?has_content>
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
	            
	            	<#list model.applicationForm.employmentPositions as position>
		            	<tr>
		                    <td><a class="row-arrow" name="positionEditButton" id="position_${position.id?string('#######')}">-</a></td>
		                    <td>${(position.position_title?html)!}</td>
		                    <td>${(position.position_startDate?string('dd-MMM-yyyy'))!}</td>
		                    <td>${(position.position_endDate?string('dd-MMM-yyyy'))!}</td>
		                     <td>
		                     	 <#if !model.applicationForm.isSubmitted()>
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
		                    
		                </tr>
		            </#list>
				</tbody>
				
			</table>
		</#if>
        
        <input type="hidden" id="positionId" name="positionId"/>
        
        <form>
        	
        	<div>
                
            	<!-- Employer (company name) -->
                <div class="row">
                	<span class="label">Employer<em>*</em></span>
                    <span class="hint"></span>
                    <div class="field">
                    <#if !model.applicationForm.isSubmitted()>
                    	<input class="full" type="text" id="position_employer" name="position_employer" 
                    					value="${(model.employmentPosition.position_employer?html)!}" placeholder="Provider of employment" />
                        <#if model.hasError('position_employer')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_employer').code /></span>                           
                        </#if>
                    <#else>
                        <input readonly="readonly" class="full" type="text" id="position_employer" name="position_employer" 
                                        value="${(model.employmentPosition.position_employer?html)!}" placeholder="Provider of employment" />
                    </#if>    
                   	</div>
                </div>
                
                <!-- Position -->
                <div class="row">
                	<span class="label">Position<em>*</em></span>
                    <span class="hint"></span>
                    <div class="field">
                        <#if !model.applicationForm.isSubmitted()>
                    	<input class="full" type="text" id="position_title" name="position_title" 
                    					value="${(model.employmentPosition.position_title?html)!}" placeholder="Title of position" />
                        <#if model.hasError('position_title')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_title').code /></span>                           
                        </#if>
                        <#else>
                            <input readonly="readonly" class="full" type="text" id="position_title" name="position_title" 
                                        value="${(model.employmentPosition.position_title?html)!}" placeholder="Title of position" />
                        </#if> 
                    </div>
                </div>
                
                <!-- Remit (job description) -->
                <div class="row">
                    <span class="label">Remit<em>*</em></span>
                    <span class="hint"></span>
                    <div class="field">
                        <#if !model.applicationForm.isSubmitted()>
                      	<textarea cols="70" rows="3" class="max" id="position_remit" 
                      		name="position_remit" 
                      		placeholder="Summary of responsibilities">${(model.employmentPosition.position_remit?html)!}</textarea>
							
						<#if model.hasError('position_remit')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_remit').code /></span>                           
						</#if>
						 <#else>
						    <textarea readonly="readonly" cols="70" rows="3" class="max" id="position_remit" 
                            name="position_remit" value="${(model.employmentPosition.position_remit?html)!}" 
                            placeholder="Summary of responsibilities"></textarea>  
						 </#if> 
						 
                    </div>
             	</div>
                
                <!-- Start date -->
                <div class="row">
                    <span class="label">Start Date<em>*</em></span>
                    <span class="hint"></span>
                    <div class="field">
                      	<input class="half date" type="text" id="position_startDate" name="position_startDate" 
                      			value="${(model.employmentPosition.position_startDate?string('dd-MMM-yyyy'))!}"
                      			<#if model.applicationForm.isSubmitted()>
                                          disabled="disabled"
                                </#if>>
                        </input>        
                      	<#if model.hasError('position_startDate')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_startDate').code /></span>                           
                        </#if>
                    </div>
                </div>
                
                <!-- End date -->
                <div class="row">
                    <span class="label">End Date</span>
                    <span class="hint"></span>
                    <div class="field">
                      	<input class="half date" type="text" id="position_endDate" name="position_endDate" 
                      			value="${(model.employmentPosition.position_endDate?string('dd-MMM-yyyy'))!}"
                      			<#if model.applicationForm.isSubmitted()>
                                          disabled="disabled"
                                </#if>>
                      	</input>		
						<#if model.hasError('position_endDate')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_endDate').code /></span>                           
                        </#if>
                    </div>
               	</div>
                
                <!-- Language -->
                <div class="row">
                    <span class="label">Language of work<em>*</em></span>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full" id="position_language" name="position_language"
                       <#if model.applicationForm.isSubmitted()>
                                                disabled="disabled"
                                            </#if>>
                        <option value="">Select...</option>
                         <#list model.languages as language>
                         	<option value="${language.id?string('#######')}" <#if model.employmentPosition.position_language?? && model.employmentPosition.position_language == language.id> selected="selected"</#if>>${language.name}</option>
                         </#list>
                      </select>
						<#if model.hasError('position_language')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_language').code /></span>                           
                        </#if>
                    </div>
               	</div>
                
                <!-- Document
                <div class="row">
                    <span class="label">Supporting Document</span>
                    <span class="hint"></span>
                    <div class="field">
                      	<input class="full" type="text" value="" />
                      	<a class="button" href="#">Browse</a>
                      	<a class="button" href="#">Upload</a>
                      	<a class="button" href="#">Add Document</a>
                    </div>  
              	</div>  -->

			</div>

			<div class="buttons">
			<#if !model.applicationForm.isSubmitted()>
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
<#if (model.result?? && model.result.hasErrors()) || add?? > 

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#position-H2').trigger('click');
	});
</script>
</#if>