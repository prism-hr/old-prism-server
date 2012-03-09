<#if model.applicationForm.employmentPositions?has_content>
	<#assign hasEmploymentPositions = true>
<#else>
	<#assign hasEmploymentPositions = false>
</#if>

<#import "/spring.ftl" as spring />
	
	<h2 class="empty">
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
	                	<th colspan="2">Title</th>
	                    <th>From</th>
	                    <th>To</th>
	                    <th>&nbsp;</th>
	                </tr>
				</thead>
	            
	            <tbody>
	            
	            	<#list model.applicationForm.employmentPositions as position>
		            	<tr>
		                    <td><a class="row-arrow" href="#">-</a></td>
		                    <td>${position.position_title}</td>
		                    <td>${position.position_startDate?string('yyyy/MM/dd')}</td>
		                    <td>${position.position_endDate?string('yyyy/MM/dd')}</td>
		                    <td>
		                    	<a class="button-delete" 
		                    			type="submit" name="positionEditButton" id="position_${position.id}">Edit</a>
		                    </td>
		                    
							<input type="hidden" id="${position.id}_positionId" value="${position.id}"/>
                            <input type="hidden" id="${position.id}_employer" value="${position.position_employer}"/>
                            <input type="hidden" id="${position.id}_remit" value="${position.position_remit}"/>
                            <input type="hidden" id="${position.id}_language" value="${position.position_language}"/>
                            <input type="hidden" id="${position.id}_positionTitle" value="${position.position_title}"/>
                            <input type="hidden" id="${position.id}_positionStartDate" value="${position.position_startDate?string('yyyy/MM/dd')}"/>
                            <input type="hidden" id="${position.id}_positionEndDate" value="${position.position_endDate?string('yyyy/MM/dd')}"/>
		                    
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
                    	<input class="full" type="text" id="position_employer" name="position_employer" 
                    					value="${model.employmentPosition.position_employer!}" placeholder="Provider of employment" />
                        <#if model.hasError('position_employer')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_employer').code /></span>                           
                        </#if>
                   	</div>
                </div>
                
                <!-- Position -->
                <div class="row">
                	<span class="label">Position</span>
                    <span class="hint"></span>
                    <div class="field">
                    	<input class="full" type="text" id="position_title" name="position_title" 
                    					value="${model.employmentPosition.position_title!}" placeholder="Title of position" />
                        <#if model.hasError('position_title')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_title').code /></span>                           
                        </#if>
                    </div>
                </div>
                
                <!-- Remit (job description) -->
                <div class="row">
                    <span class="label">Remit</span>
                    <span class="hint"></span>
                    <div class="field">
                      	<textarea cols="70" rows="3" class="max" id="position_remit" 
                      		name="position_remit" value="${model.employmentPosition.position_remit!}" 
                      		placeholder="Summary of responsibilities"></textarea>
							
						<#if model.hasError('position_remit')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_remit').code /></span>                           
						</#if>
                    </div>
             	</div>
                
                <!-- Start date -->
                <div class="row">
                    <span class="label">Start Date</span>
                    <span class="hint"></span>
                    <div class="field">
                      	<input class="half date" type="text" id="position_startDate" name="position_startDate" 
                      			value="${(model.employmentPosition.position_startDate?string('yyyy/MM/dd'))!}"/>
                      	<#if model.hasError('position_startDate')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_startDate').code /></span>                           
                        </#if>
                    </div>
                </div>
                
                <!-- End date -->
                <div class="row">
                    <span class="label">End date</span>
                    <span class="hint"></span>
                    <div class="field">
                      	<input class="half date" type="text" id="position_endDate" name="position_endDate" 
                      			value="${(model.employmentPosition.position_endDate?string('yyyy/MM/dd'))!}"/>
						<#if model.hasError('position_endDate')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_endDate').code /></span>                           
                        </#if>
                    </div>
               	</div>
                
                <!-- Language -->
                <div class="row">
                    <span class="label">Language of work</span>
                    <span class="hint"></span>
                    <div class="field">
						<input class="full" type="text" id="position_language" 
									name="position_language" value="${model.employmentPosition.position_language!}"/>
						<#if model.hasError('position_language')>                           
                        	<span class="invalid"><@spring.message  model.result.getFieldError('position_language').code /></span>                           
                        </#if>
                    </div>
               	</div>
                
                <!-- Document -->
                <div class="row">
                    <span class="label">Supporting Document</span>
                    <span class="hint"></span>
                    <div class="field">
                      	<input class="full" type="text" value="" />
                      	<a class="button" href="#">Browse</a>
                      	<a class="button" href="#">Upload</a>
                      	<a class="button" href="#">Add Document</a>
                    </div>  
              	</div>

			</div>

			<div class="buttons">
            	<a class="button" href="#">Cancel</a>
                <button class="blue" type="submit" value="close" id="positionSaveButton">Save and Close</button>
                <button class="blue" type="submit" value="add">Save and Add</button>
            </div>

		</form>
	</div>
	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/employmentPosition.js'/>"></script>