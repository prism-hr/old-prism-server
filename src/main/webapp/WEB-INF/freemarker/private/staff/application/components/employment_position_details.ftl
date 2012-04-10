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
		                    <td>${position.position_title!}</td>
		                    <td>${(position.position_startDate?string('dd-MMM-yyyy'))!}</td>
		                    <td>${(position.position_endDate?string('dd-MMM-yyyy'))!}</td>
		                    
		                    <input type="hidden" id="${position.id?string('#######')}_positionId" value="${position.id?string('#######')}"/>
                            <input type="hidden" id="${position.id?string('#######')}_employer" value="${(position.position_employer?html)!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_remit" value="${(position.position_remit?html)!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_language" value="${(position.position_language?html)!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_positionTitle" value="${position.position_title}"/>
                            <input type="hidden" id="${position.id?string('#######')}_positionStartDate" value="${(position.position_startDate?string('dd-MMM-yyyy'))!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_positionEndDate" value="${(position.position_endDate?string('dd-MMM-yyyy'))!}"/>
		               		<input type="hidden"  id="${position.id?string('#######')}_positionCompleted" value="${position.completed}"/> 
						
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
                	<span class="label">Employer</span>
                    <span class="hint"></span>
                    <div class="field">
                    <input readonly="readonly" class="full" type="text" id="position_employer" name="position_employer" 
                                        value="${(model.employmentPosition.position_employer?html)!}" placeholder="Provider of employment" />
                   	</div>
                </div>
                
                <!-- Position -->
                <div class="row">
                	<span class="label">Position</span>
                    <span class="hint"></span>
                    <div class="field">
                    <input readonly="readonly" class="full" type="text" id="position_title" name="position_title" 
                                        value="${(model.employmentPosition.position_title?html)!}" placeholder="Title of position" />
                    </div>
                </div>
                
                <!-- Remit (job description) -->
                <div class="row">
                    <span class="label">Remit</span>
                    <span class="hint"></span>
                    <div class="field">
                      <textarea readonly="readonly" cols="70" rows="3" class="max" id="position_remit" 
                            name="position_remit" value="${(model.employmentPosition.position_remit?html)!}" 
                            placeholder="Summary of responsibilities"></textarea>  
                    </div>
             	</div>
                
                <!-- Start date -->
                <div class="row">
                    <span class="label">Start Date</span>
                    <span class="hint"></span>
                    <div class="field">
                    <input class="half date" type="text" id="position_startDate" name="position_startDate" 
                                value="${(model.employmentPosition.position_startDate?string('dd-MMM-yyyy'))!}"
                                          disabled="disabled">
                        </input>  
                    </div>
                </div>
                <div class="row">
                       <label class="label">Is Completed</label>
                <span class="hint" data-desc="Tooltip demonstration."></span>
                       		<input type="checkbox" name="completedPositionCB" id="completedPositionCB"/
                       		<#if model.employmentPosition.isEmploymentPositionCompleted()>
                                          checked
                                </#if>
                                          disabled="disabled">
                    <input type="hidden" name="completedPosition" id="completedPosition"/>
               		
               			 </div>
                
                <!-- End date -->
                <div class="row">
                    <span class="label">End date</span>
                    <span class="hint"></span>
                    <div class="field">
                    <input class="half date" type="text" id="position_endDate" name="position_endDate" 
                                value="${(model.employmentPosition.position_endDate?string('dd-MMM-yyyy'))!}"
                                          disabled="disabled">
                        </input>    
                    </div>
               	</div>
                
                <!-- Language -->
                <div class="row">
                    <span class="label">Language of work</span>
                    <span class="hint"></span>
                    <div class="field">
                        <select class="full" id="position_language" name="position_language" value="${(model.employmentPosition.position_language?html)!}"
                                                disabled="disabled">
                        <option value="">Select...</option>
                         <#list model.languages as language>
                            <option value="${language.id?string('#######')}" <#if model.employmentPosition.position_language?? && model.employmentPosition.position_language == language.id> selected="selected"</#if>>${language.name?html}</option>
                         </#list>
                      </select>
                    </div>
               	</div>
                
                <!-- Document -->
                <div class="row">
                	<!-- Add freemarker expression to get the content -->
              	</div>

			</div>

			<div class="buttons">
                <button class="blue" id="positionCloseButton" type="button">Close</button>
            </div>

		</form>
	</div>
	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/employmentPosition.js'/>"></script>