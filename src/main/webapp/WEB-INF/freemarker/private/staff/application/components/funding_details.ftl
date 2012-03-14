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
		                  	<td>${funding.awardDate?string('dd-MMM-yyyy')}</td>
		                </tr>
					</#list>				               
				</tbody>
			
			</table>
        </#if>
              
		<form>
                
			<div>
				<!-- Award type -->
                <div class="row">
                  	<span class="label">Funding Type</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                	
                	<div class="field">
						${model.funding.fundingType!}
					</div>
				</div>

                <!-- Award description -->
                <div class="row">
                	<span class="label">Description</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
					<div class="field">
						${model.funding.fundingDescription!}                    
					</div>
				</div>
                  
                <!-- Value of award -->
                <div class="row">
                  	<span class="label">Value of Award</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
						${model.funding.fundingValue!}                    
					</div>
				</div>
                  
                <!-- Award date -->
                <div class="row">
                  	<span class="label" data-desc="Tooltip demonstration.">Award Date</span>
                    <span class="hint"></span>
                    <div class="field">
						${(model.funding.fundingAwardDate?string('dd-MMM-yyyy'))!}                    
					</div>
                </div>
                
                <!--  
                <div class="row">
                  	<span class="label">Supporting Document</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    </div>	
				</div> -->

			</div>

			<div class="buttons">
				<button class="blue" type="button">Close</button>
			</div>

		</form>
	</div>

	<script type="text/javascript" src="<@spring.url '/design/default/js/application/funding.js'/>"></script>