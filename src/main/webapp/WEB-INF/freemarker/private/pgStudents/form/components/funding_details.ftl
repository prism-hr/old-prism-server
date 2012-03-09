<#if model.applicationForm.fundings?has_content>
	<#assign hasFundings = true>
<#else>
	<#assign hasFundings = false>
</#if>

<#import "/spring.ftl" as spring />

	<h2 class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
			Funding
	</h2>

	<div>
	
		<#if hasFundings>
			<table class="existing">
				
				<colgroup>
	            	<col style="width: 30px" />
	                <col style="width: 120px" />
	                <col />
	                <col style="width: 120px" />
	                <col style="width: 30px" />
	        	</colgroup>
	            
	            <thead>
					<tr>
	                	<th colspan="2">Funding Type</th>
	                    <th>Description</th>
	                    <th>Award Date</th>
	                    <th colspan="1">&nbsp;</th>
					</tr>
				</thead>
	                
				<tbody>
				
					<#list model.applicationForm.fundings as funding>		
						<tr>
		                  	<td><a class="row-arrow" name="fundingEditButton" id="funding_${funding.id}">-</a></td>
		                  	<td>${funding.type}</td>
		                  	<td>${funding.description}</td>
		                  	<td>${funding.awardDate?string('yyyy/MM/dd')}</td>
		                  	<td><a class="button-delete" href="#">delete</a></td>
		                  	
		                  	<!-- Non-rendering data -->
							<input type="hidden" id="${funding.id}_fundingIdDP" value="${funding.id}"/>
	                        <input type="hidden" id="${funding.id}_fundingTypeDP" value="${funding.type}"/>
	                        <input type="hidden" id="${funding.id}_fundingValueDP" value="${funding.value}"/>
	                        <input type="hidden" id="${funding.id}_fundingDescriptionDP" value="${funding.description}"/>
	                        <input type="hidden" id="${funding.id}_fundingAwardDateDP" value="${funding.awardDate?string('yyyy/MM/dd')}"/>
		                  	
		                </tr>
					</#list>				               
				</tbody>
			
			</table>
        </#if>
        <!-- Non-rendering data -->
        <input type="hidden" id="fundingId" name="fundingId"/>
              
		<form>
                
			<div>
				<!-- Award type -->
                <div class="row">
                  	<span class="label">Funding Type</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                	
                	<div class="field">
                	<#if !model.applicationForm.isSubmitted()>
                		<input id="fundingType" name="fundingType" class="full" type="text" value="${model.funding.fundingType!}" placeholder="e.g. scholarship, industry" />
                		<#if model.hasError('fundingType')>
                        	<span class="invalid"><@spring.message  model.result.getFieldError('fundingType').code /></span>                           
                        </#if>
                        <#else>
                            <input id="fundingType" name="fundingType" readonly="readonly" class="full" type="text" value="${model.funding.fundingType!}" placeholder="e.g. scholarship, industry" />
                        </#if>
					</div>
				</div>

                <!-- Award description -->
                <div class="row">
                	<span class="label">Description</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
					<div class="field">
				    <#if !model.applicationForm.isSubmitted()>
                    	<input id="fundingDescription" name="fundingDescription" class="full" type="text" value="${model.funding.fundingDescription!}" />
                    <#if model.hasError('fundingDescription')>                           
                    	<span class="invalid"><@spring.message  model.result.getFieldError('fundingDescription').code /></span>                           
                    </#if>
                    <#else>
                        <input id="fundingDescription" name="fundingDescription" readonly="readonly" class="full" type="text" value="${model.funding.fundingDescription!}" />
                    </#if>
                    </div>
                    
				</div>
                  
                <!-- Value of award -->
                <div class="row">
                  	<span class="label">Value of Award</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    <#if !model.applicationForm.isSubmitted()>
                    	<input id="fundingValue" name="fundingValue" class="full" type="text" value="${model.funding.fundingValue!}" />
	                    <#if model.hasError('fundingValue')>
	                    	<span class="invalid"><@spring.message  model.result.getFieldError('fundingValue').code /></span>
	                    </#if>
                    <#else>
                       <input id="fundingValue" readonly="readonly" name="fundingValue" class="full" type="text" value="${model.funding.fundingValue!}" />
                    </#if>
                    </div>
				</div>
                  
                <!-- Award date -->
                <div class="row">
                  	<span class="label" data-desc="Tooltip demonstration.">Award Date</span>
                    <span class="hint"></span>
                    <div class="field">
                    <#if !model.applicationForm.isSubmitted()>
	                    <input id="fundingAwardDate" name="fundingAwardDate" class="half date" type="text" value="${(model.funding.fundingAwardDate?string('yyyy/MM/dd'))!}" />
                    	<#if model.hasError('fundingAwardDate')>                           
                    		<span class="invalid""><@spring.message  model.result.getFieldError('fundingAwardDate').code /></span>                           
                    	</#if>
                    <#else>
                        <input id="fundingAwardDate" name="fundingAwardDate" readonly="readonly" class="half date" type="text" value="${(model.funding.fundingAwardDate?string('yyyy/MM/dd'))!}" />	
                    </#if>
                    </div>
                    
                </div>
                  
                <div class="row">
                  	<span class="label">Supporting Document</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    	<input class="full" type="text" value="" />
                      	<a class="button" href="#">Browse</a>
                      	<a class="button" href="#">Upload</a>
                      	<a class="button plus" href="#">Add Another</a>
                    </div>	
				</div>

			</div>

        <#if !model.applicationForm.isSubmitted()>
			<div class="buttons">
            	
            	<a class="button" type="button" id="fundingCancelButton" name="fundingCancelButton">Cancel</a>
				<button class="blue" type="button" id="fundingSaveCloseButton" name="id="fundingSaveCloseButton"" value="close">Save and Close</button>
                <button class="blue" type="button" id="fundingSaveAddButton" name="fundingSaveAddButton" value="add">Save and Add</button>
                
			</div>
	   </#if>

		</form>
	</div>

	<script type="text/javascript" src="<@spring.url '/design/default/js/application/funding.js'/>"></script>