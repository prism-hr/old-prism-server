<#import "/spring.ftl" as spring />
	<h2 class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
			Funding
	</h2>

	<div>
	
		<#list model.applicationForm.fundings as funding>
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
	                    <th>Awarding Body</th>
	                    <th>Issue Date</th>
	                    <th colspan="1">&nbsp;</th>
					</tr>
				</thead>
	                
				<tbody>
						
						<tr>
		                  	<td><a class="row-arrow" href="#">-</a></td>
		                  	<td>${funding.type}</td>
		                  	<td>${funding.value}</td>
		                  	<td>${funding.awardDate?string('yyyy/MM/dd')}</td>
		                  	<td><a class="button-delete" href="#">delete</a></td>
		                  	
		                  	<!-- Non-rendering data -->
							<input type="hidden" id="${funding.id}_fundingIdDP" value="${funding.id}"/>
	                        <input type="hidden" id="${funding.id}_fundingTypeDP" value="${funding.type}"/>
	                        <input type="hidden" id="${funding.id}_fundingValueDP" value="${funding.value}"/>
	                        <input type="hidden" id="${funding.id}_fundingDescriptionDP" value="${funding.description}"/>
	                        <input type="hidden" id="${funding.id}_fundingAwardDateDP" value="${funding.awardDate?string('yyyy/MM/dd')}"/>
		                  	
		                </tr>
						               
				</tbody>
			
			</table>
        </#list>
        <!-- Non-rendering data -->
        <input type="hidden" id="fundingId" name="fundingId"/>
              
		<form>
                
			<div>
				<!-- Award type -->
                <div class="row">
                  	<span class="label">Funding Source</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                	
                	<div class="field">
                		<input id="fundingType" class="full" type="text" value="${model.funding.fundingType!}" placeholder="e.g. scholarship, industry" />
                		<#if model.hasError('fundingType')>
                        	<span class="invalid"><@spring.message  model.result.getFieldError('fundingType').code /></span>                           
                        </#if>
					</div>
				</div>

                <!-- Award description -->
                <div class="row">
                	<span class="label">Description</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
				
					<div class="field">
                    	<input id="fundingDescription" class="full" type="text" value="${model.funding.fundingDescription!}" />
                    </div>
                    <#if model.hasError('fundingDescription')>                           
                    	<span style="color:red;"><@spring.message  model.result.getFieldError('fundingDescription').code /></span>                           
                    </#if>
                    
				</div>
                  
                <!-- Value of award -->
                <div class="row">
                  	<span class="label">Value of Award</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    	<input id="fundingValue" class="full" type="text" value="${model.funding.fundingValue!}" />
                    </div>
                    <#if model.hasError('fundingValue')>
                    	<span class="invalid"><@spring.message  model.result.getFieldError('fundingValue').code /></span>
                    </#if>
				</div>
                  
                <!-- Award date -->
                <div class="row">
                  	<span class="label" data-desc="Tooltip demonstration.">Award Date</span>
                    <span class="hint"></span>
                    <div class="field">
	                    <input id="fundingAwardDate" class="half" type="date" value="${(model.funding.fundingAwardDate?string('yyyy/MM/dd'))!}" />
                    </div>
                    <#if model.hasError('fundingAwardDate')>                           
                    	<span style="color:red;"><@spring.message  model.result.getFieldError('fundingAwardDate').code /></span>                           
                    </#if>
                    
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

			<div class="buttons">
            	
            	<a class="button" href="#">Cancel</a>
				<button class="blue" type="submit" value="close">Save and Close</button>
                <button class="blue" type="submit" value="add">Save and Add</button>
                
			</div>

		</form>
	</div>

	<script type="text/javascript" src="<@spring.url '/design/default/js/application/funding.js'/>"></script>